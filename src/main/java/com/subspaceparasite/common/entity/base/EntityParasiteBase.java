package com.subspaceparasite.common.entity.base;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import com.subspaceparasite.api.ICanAbility;
import com.subspaceparasite.api.IEvolvable;
import com.subspaceparasite.api.IHitboxedEntity;
import com.subspaceparasite.api.IInfectable;
import com.subspaceparasite.api.IParasite;
import com.subspaceparasite.api.parasite.DislodgmentCode;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.ai.ParasiteMeleeAttackGoal;
import com.subspaceparasite.common.world.ModWorldData;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.entity.PartEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

/**
 * Base class for ALL parasite entities in the SubspaceParasite mod.
 * <p>
 * Extends {@link Monster} and implements {@link IParasite}, {@link IEvolvable},
 * {@link IInfectable}, {@link IHitboxedEntity}, {@link ICanAbility}, and
 * {@link RangedAttackMob}.
 * <p>
 * Uses a component-based architecture to manage combat, evolution,
 * infection, and colony behaviors independently. Each component handles
 * its own tick logic and can be overridden by subclasses.
 * <p>
 * Architecture inspired by the original SRP EntityParasiteBase but
 * refactored to use the component pattern for maintainability.
 */
public abstract class EntityParasiteBase extends Monster
        implements IParasite, IEvolvable, IInfectable, IHitboxedEntity,
                   ICanAbility, RangedAttackMob {

    // ========== Inner Enum: AnimState ==========

    /**
     * Animation states used by the client renderer to determine which
     * animation set to play. Mapped from {@link #parasiteStatus} and
     * other runtime state.
     */
    public enum AnimState {
        IDLE, WALKING, ATTACKING, SPECIAL_SKILL, SWELLING, CLOAKING, MELTING, LEAPING, DYING
    }

    // ========== SynchedEntityData Accessors ==========

    private static final EntityDataAccessor<Byte> DATA_SKIN =
            SynchedEntityData.defineId(EntityParasiteBase.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> DATA_COLD =
            SynchedEntityData.defineId(EntityParasiteBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> DATA_STATUS =
            SynchedEntityData.defineId(EntityParasiteBase.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> DATA_SELFDESTRUCT =
            SynchedEntityData.defineId(EntityParasiteBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_DISLODGE =
            SynchedEntityData.defineId(EntityParasiteBase.class, EntityDataSerializers.INT);

    // ========== Modifier UUIDs ==========

    private static final UUID PHASE_HEALTH_MODIFIER_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-000000000001");
    private static final UUID PHASE_DAMAGE_MODIFIER_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-000000000002");
    private static final UUID PHASE_SPEED_MODIFIER_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-000000000003");
    private static final UUID GENE_HEALTH_MODIFIER_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-000000000004");
    private static final UUID GENE_DAMAGE_MODIFIER_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-000000000005");

    // ========== Core Fields ==========

    protected ParasiteType parasiteType = null;
    protected EvolutionPath evolutionPath = EvolutionPath.INFECTED;
    protected EvoPhase phaseCreated = EvoPhase.ZERO;
    protected int levelCreated = 0;
    protected double killCount = 0.0;
    protected boolean colonySpawned = false;
    protected boolean canDespawn = true;
    protected boolean canWorkTask = true;

    // Ownership / hierarchy
    protected EntityParasiteBase owner;
    protected EntityParasiteBase followTarget;
    protected EntityParasiteBase pivotEntity;

    // Variant
    protected boolean canChangeVariant = true;
    protected byte skinVariant = 0;
    protected boolean coldBiome = false;

    /** Status byte: 0=idle, 1-2=attacking, 6=dying, 10=leaping */
    protected byte parasiteStatus = 0;

    // Self-destruct
    protected int selfDestructState = 0;

    // Dislodgment bitfield
    protected int dislodgmentBitfield = 0;

    // Gene system: 6 booleans, 8 floats (matching GeneType indices)
    protected boolean[] geneBooleans = new boolean[GeneType.booleanGeneCount()];
    protected float[] geneFloats = new float[GeneType.floatGeneCount()];

    // Infection state (for IInfectable)
    protected int infectionLevel = 0;
    protected float infectionResistance = 0.0F;

    // Evolution points (for IEvolvable)
    protected int evolutionPoints = 0;

    // Combat
    protected int damageCap = 10;
    protected float miniDamage = 1.0F;
    protected float scentHPMultiplier = 1.0F;
    protected float foodSteal = 0.0F;
    protected int attackCooldown = 0;

    // Timers
    protected int waitTimer = 0;
    protected int srpTicks = 0;

    // Block inventory (stored blocks from breaking)
    protected List<String> blockInventoryNames = new ArrayList<>();
    protected List<Integer> blockInventoryCounts = new ArrayList<>();

    // Leap skill
    protected int leapCooldown = 0;
    protected int leapChargeTicks = 0;
    protected boolean isLeaping = false;

    // Block break skill
    protected int breakCooldown = 0;
    protected int breakProgress = 0;
    protected BlockPos breakTargetPos = null;

    // Ability tracking (for ICanAbility)
    protected boolean[] abilities = new boolean[ICanAbility.AbilityType.values().length];

    // Components
    protected CombatComponent combatComponent;
    protected EvolutionComponent evolutionComponent;
    protected InfectionComponent infectionComponent;
    protected ColonyComponent colonyComponent;

    // Hitbox parts
    protected EntityHitbox[] hitboxParts = new EntityHitbox[0];

    // Animation and rendering state
    protected AnimState currentAnimState = AnimState.IDLE;
    protected float animProgress = 0.0F;
    protected float scaleModifier = 1.0F;
    protected float cloakingLevel = 0.0F;
    protected boolean hasGlowFlag = false;
    protected float infectionOverlayLevel = 0.0F;
    protected float swellAmount = 0.0F;
    protected float meltingProgress = 0.0F;
    protected int hurtFlashTimer = 0;

    // Leadership hierarchy
    protected EntityParasiteBase leader;
    protected boolean isLeaderFlag = false;

    // Skill system
    protected int skillCooldown = 0;

    // Owner UUID for deferred resolution
    protected UUID ownerUUID;

    // ========== Multi-Target Tracking (EntityCanPullMobs) ==========

    /** Target entity slots for pull ability (original SRP uses 3 slots) */
    protected int[] targetedEntityIds = new int[]{-1, -1, -1};
    protected LivingEntity[] targetedEntities = new LivingEntity[3];
    protected int pullTimer = 0;
    protected boolean skillPulling = false;
    protected int pullBorderTimer = 0;

    // ========== Constructor ==========

    protected EntityParasiteBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.phaseCreated = getCurrentPhase(level);
        this.levelCreated = level instanceof ServerLevel sl
                ? (int) ModWorldData.get(sl).getKillStat("total")
                : 0;
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.xpReward = 5;

        // Initialize components
        this.combatComponent = createCombatComponent();
        this.evolutionComponent = createEvolutionComponent();
        this.infectionComponent = createInfectionComponent();
        this.colonyComponent = createColonyComponent();

        // Apply initial bonuses
        applyBonuses();
    }

    // ========== Component Factory Methods ==========

    /** Creates the combat component. Override in subclasses for custom combat behavior. */
    protected CombatComponent createCombatComponent() {
        return new CombatComponent(this);
    }

    /** Creates the evolution component. Override in subclasses for custom evolution behavior. */
    protected EvolutionComponent createEvolutionComponent() {
        return new EvolutionComponent(this);
    }

    /** Creates the infection component. Override in subclasses for custom infection behavior. */
    protected InfectionComponent createInfectionComponent() {
        return new InfectionComponent(this);
    }

    /** Creates the colony component. Override in subclasses for custom colony behavior. */
    protected ColonyComponent createColonyComponent() {
        return new ColonyComponent(this);
    }

    // ========== Data Registration ==========

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SKIN, (byte) 0);
        this.entityData.define(DATA_COLD, false);
        this.entityData.define(DATA_STATUS, (byte) 0);
        this.entityData.define(DATA_SELFDESTRUCT, 0);
        this.entityData.define(DATA_DISLODGE, 0);
    }

    // ========== AI Goals ==========

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new ParasiteWaitGoal(this));
        this.goalSelector.addGoal(3, new ParasiteMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(4, new ParasiteJumpGoal(this));
        this.goalSelector.addGoal(5, new ParasiteFollowOwnerGoal(this));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    // ========== Main Tick ==========

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            clientTick();
            return;
        }

        srpTicks++;

        // Handle wait timer
        if (waitTimer > 0) {
            waitTimer--;
            if (waitTimer <= 0) {
                canWorkTask = true;
            }
        }

        // Handle cooldowns
        if (attackCooldown > 0) attackCooldown--;
        if (leapCooldown > 0) leapCooldown--;
        if (breakCooldown > 0) breakCooldown--;
        if (skillCooldown > 0) skillCooldown--;
        if (hurtFlashTimer > 0) hurtFlashTimer--;

        // Delegate to components
        if (combatComponent != null) combatComponent.tick();
        if (evolutionComponent != null) evolutionComponent.tick();
        if (infectionComponent != null) infectionComponent.tick();
        if (colonyComponent != null) colonyComponent.tick();

        // Handle status effects
        handleStatus();

        // Self-destruct state handling
        if (selfDestructState != 0) {
            handleSelfDestruct();
        }

        // Cold biome check
        boolean wasCold = this.entityData.get(DATA_COLD);
        boolean isCold = isParasiteBiome() && isInColdBiome();
        if (wasCold != isCold) {
            this.entityData.set(DATA_COLD, isCold);
            this.coldBiome = isCold;
        }

        // Passive regeneration from REGEN_RATE gene
        float regenRate = geneFloats[GeneType.REGEN_RATE.getIndex()];
        if (regenRate > 0 && srpTicks % 20 == 0 && getHealth() < getMaxHealth()) {
            heal(regenRate);
        }

        // Update hitbox parts
        updateHitboxParts();

        // COTH spread on contact
        spreadCOTHContact();
    }

    /** Client-side tick for visual effects. */
    protected void clientTick() {
        if (this.getParasiteStatus() == 6) {
            this.level().addParticle(ParticleTypes.PORTAL,
                    this.getX() + (this.random.nextDouble() - 0.5) * this.getBbWidth(),
                    this.getY() + this.random.nextDouble() * this.getBbHeight(),
                    this.getZ() + (this.random.nextDouble() - 0.5) * this.getBbWidth(),
                    0.0, 0.05, 0.0);
        }
    }

    // ========== AI Step ==========

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (scentHPMultiplier != 1.0F && this.getHealth() > 0) {
            float maxModified = this.getMaxHealth() * scentHPMultiplier;
            if (this.getHealth() > maxModified) {
                this.setHealth(maxModified);
            }
        }
    }

    // ========== Damage Handling ==========

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide) return false;
        if (isInvulnerableTo(source)) return false;

        // Damage cap
        if (combatComponent != null) {
            amount = combatComponent.applyDamageCap(source, amount);
        }

        // Minimum damage enforcement
        if (amount < miniDamage && amount > 0) {
            amount = miniDamage;
        }

        // Poison healing: certain parasites heal from witch-resistant damage
        if (this.isPoisonImmune()) {
            if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
                this.heal(amount * 0.5F);
                return false;
            }
        }

        // Dislodgment effects on hurt
        if (source.getEntity() instanceof LivingEntity attacker) {
            applyDislodgmentOnHurt(attacker, amount);
        }

        boolean result = super.hurt(source, amount);

        if (result) {
            hurtFlashTimer = 10;

            if (combatComponent != null) combatComponent.onHurt(source, amount);
            if (infectionComponent != null) infectionComponent.onHurt(source, amount);
        }

        return result;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (attackCooldown > 0) return false;

        boolean result = super.doHurtTarget(target);

        if (result && target instanceof LivingEntity livingTarget) {
            float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if (damage < miniDamage) {
                float supplement = miniDamage - damage;
                livingTarget.hurt(this.damageSources().mobAttack(this), supplement);
            }

            // COTH spread on attack
            if (infectionComponent != null) {
                infectionComponent.spreadCOTH(livingTarget);
            }

            // Food stealing
            if (foodSteal > 0 && target instanceof Player player) {
                stealFood(player);
            }

            // Melee attack callback
            onMeleeAttack(livingTarget, damage);

            killCount += 0.1;
            attackCooldown = 10;
        }

        return result;
    }

    // ========== Death Handling ==========

    @Override
    public void die(DamageSource source) {
        setParasiteStatus((byte) 6);

        applyDislodgmentOnDeath(source);

        if (!blockInventoryNames.isEmpty()) {
            spawnCyst();
        }

        checkBeckonOnDeath(source);

        // Update world kill stats
        if (this.level() instanceof ServerLevel serverLevel) {
            if (source.getEntity() instanceof LivingEntity killer) {
                ModWorldData worldData = ModWorldData.get(serverLevel);
                if (parasiteType != null) {
                    worldData.addKillStat(parasiteType.getSerializedName(), 1);
                }
                worldData.addKillStat("total", 1);

                if (killer instanceof EntityParasiteBase parasiteKiller) {
                    parasiteKiller.addKillCount(1.0);
                }
            }
        }

        if (infectionComponent != null) infectionComponent.onDeath(source);
        if (colonyComponent != null) colonyComponent.onDeath();

        removeHitboxParts();

        super.die(source);
    }

    // ========== Effect Immunity ==========

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        MobEffect effect = effectInstance.getEffect();

        if (isCO_THEffect(effect)) return false;
        if (isCorrosionEffect(effect)) return false;
        if (isVirusEffect(effect)) return false;
        if (effect == MobEffects.POISON && isPoisonImmune()) return false;
        if (effect == MobEffects.REGENERATION && isRegenImmune()) return false;

        // Dislodgment code 11: immune to negative effects
        if (hasDislodgmentCode(DislodgmentCode.ELEVEN) && !isBeneficial(effect)) {
            return false;
        }

        return super.canBeAffected(effectInstance);
    }

    /**
     * Returns whether the given effect is beneficial.
     * Override in subclasses to add type-specific beneficial effects.
     *
     * @param effect the mob effect to check
     * @return true if the effect is considered beneficial
     */
    protected boolean isBeneficial(MobEffect effect) {
        return effect == MobEffects.REGENERATION
                || effect == MobEffects.HEAL
                || effect == MobEffects.DAMAGE_BOOST
                || effect == MobEffects.MOVEMENT_SPEED
                || effect == MobEffects.JUMP
                || effect == MobEffects.DAMAGE_RESISTANCE
                || effect == MobEffects.FIRE_RESISTANCE
                || effect == MobEffects.WATER_BREATHING
                || effect == MobEffects.INVISIBILITY
                || effect == MobEffects.NIGHT_VISION
                || effect == MobEffects.SLOW_FALLING
                || effect == MobEffects.CONDUIT_POWER
                || effect == MobEffects.DOLPHINS_GRACE;
    }

    /** Returns false by default. Override in subclasses for COTH-specific effect immunity. */
    protected boolean isCO_THEffect(MobEffect effect) { return false; }

    /** Returns false by default. Override in subclasses for corrosion effect immunity. */
    protected boolean isCorrosionEffect(MobEffect effect) { return false; }

    /** Returns false by default. Override in subclasses for virus effect immunity. */
    protected boolean isVirusEffect(MobEffect effect) { return false; }

    /**
     * Returns true if this parasite is immune to poison damage.
     * Based on the POISON_HEALING gene value being greater than 0.
     *
     * @return true if poison immune
     */
    protected boolean isPoisonImmune() { return geneFloats[GeneType.POISON_HEALING.getIndex()] > 0; }

    /** Parasites are always immune to their own regeneration suppression. */
    protected boolean isRegenImmune() { return true; }

    // ========== Spawn Rules ==========

    /**
     * Checks whether this parasite type can spawn under the given conditions.
     *
     * @param type      the entity type being spawned
     * @param level     the server level accessor
     * @param spawnType the type of spawn (natural, spawner, etc.)
     * @param pos       the spawn position
     * @param random    the random source
     * @return true if spawning is allowed
     */
    public static boolean checkSpawnRules(EntityType<? extends EntityParasiteBase> type,
                                          ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, RandomSource random) {
        EvoPhase currentPhase = getCurrentPhase(level.getLevel());
        if (!currentPhase.isAtLeast(EvoPhase.ONE)) return false;
        if (level.getDifficulty() == Difficulty.PEACEFUL) return false;

        int lightLevel = level.getMaxLocalRawBrightness(pos);
        int maxLight = ModConfigSystems.getMaxSpawnLightLevel();
        if (lightLevel > maxLight) return false;

        if (isMobCapReached(level, type, pos)) return false;
        if (ModConfigSystems.isDimensionBlacklisted(level.getLevel())) return false;

        return true;
    }

    /**
     * Checks whether the mob cap for parasite entities has been reached.
     *
     * @param level the server level accessor
     * @param type  the entity type
     * @return true if the mob cap is reached
     */
    protected static boolean isMobCapReached(ServerLevelAccessor level, EntityType<?> type, BlockPos pos) {
        int cap = ModConfigSystems.getMobCap();
        if (cap <= 0) return false;
        long count = level.getLevel().getEntitiesOfClass(EntityParasiteBase.class,
                new AABB(pos.getX() - 128, pos.getY() - 64, pos.getZ() - 128,
                         pos.getX() + 128, pos.getY() + 256, pos.getZ() + 128)).size();
        return count >= cap;
    }

    // ========== NBT Save/Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (parasiteType != null) {
            tag.putByte("ParasiteTypeID", parasiteType.getId());
            tag.putString("ParasiteType", parasiteType.getSerializedName());
        }
        tag.putString("EvolutionPath", evolutionPath.name());
        tag.putInt("PhaseCreated", phaseCreated.getPhaseNumber());
        tag.putInt("LevelCreated", levelCreated);
        tag.putDouble("KillCount", killCount);
        tag.putBoolean("ColonySpawned", colonySpawned);
        tag.putBoolean("CanDespawn", canDespawn);
        tag.putBoolean("CanChangeVariant", canChangeVariant);
        tag.putByte("SkinVariant", skinVariant);
        tag.putByte("ParasiteStatus", parasiteStatus);
        tag.putInt("SelfDestructState", selfDestructState);
        tag.putInt("DislodgmentBitfield", dislodgmentBitfield);
        tag.putInt("DamageCap", damageCap);
        tag.putFloat("MiniDamage", miniDamage);
        tag.putFloat("ScentHPMultiplier", scentHPMultiplier);
        tag.putFloat("FoodSteal", foodSteal);
        tag.putInt("WaitTimer", waitTimer);
        tag.putInt("SrpTicks", srpTicks);

        // Gene system
        for (int i = 0; i < geneBooleans.length; i++) {
            tag.putBoolean("GeneBool" + i, geneBooleans[i]);
        }
        for (int i = 0; i < geneFloats.length; i++) {
            tag.putFloat("GeneFloat" + i, geneFloats[i]);
        }

        // Block inventory
        ListTag nameList = new ListTag();
        for (String name : blockInventoryNames) {
            nameList.add(StringTag.valueOf(name));
        }
        tag.put("BlockNames", nameList);

        ListTag countList = new ListTag();
        for (Integer count : blockInventoryCounts) {
            CompoundTag countTag = new CompoundTag();
            countTag.putInt("Count", count);
            countList.add(countTag);
        }
        tag.put("BlockCounts", countList);

        // Owner UUID
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner.getUUID());
        }

        // Infection state
        tag.putInt("InfectionLevel", infectionLevel);
        tag.putFloat("InfectionResistance", infectionResistance);

        // Evolution points
        tag.putInt("EvolutionPoints", evolutionPoints);

        // Abilities
        for (int i = 0; i < abilities.length; i++) {
            tag.putBoolean("Ability" + i, abilities[i]);
        }

        // Component data
        if (combatComponent != null) tag.put("CombatData", combatComponent.save());
        if (evolutionComponent != null) tag.put("EvolutionData", evolutionComponent.save());
        if (infectionComponent != null) tag.put("InfectionData", infectionComponent.save());
        if (colonyComponent != null) tag.put("ColonyData", colonyComponent.save());

        // Rendering state
        tag.putFloat("ScaleModifier", scaleModifier);
        tag.putFloat("CloakingLevel", cloakingLevel);
        tag.putBoolean("HasGlow", hasGlowFlag);
        tag.putFloat("InfectionOverlayLevel", infectionOverlayLevel);
        tag.putFloat("SwellAmount", swellAmount);
        tag.putFloat("MeltingProgress", meltingProgress);
        tag.putBoolean("IsLeader", isLeaderFlag);

        // Leader UUID
        if (leader != null) {
            tag.putUUID("LeaderUUID", leader.getUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("ParasiteTypeID")) {
            parasiteType = ParasiteType.getByTypeId(tag.getByte("ParasiteTypeID"));
        } else if (tag.contains("ParasiteType")) {
            try {
                parasiteType = ParasiteType.valueOf(tag.getString("ParasiteType"));
            } catch (IllegalArgumentException e) {
                parasiteType = null;
            }
        }
        if (tag.contains("EvolutionPath")) {
            try { evolutionPath = EvolutionPath.valueOf(tag.getString("EvolutionPath")); }
            catch (IllegalArgumentException e) { evolutionPath = EvolutionPath.INFECTED; }
        }
        phaseCreated = EvoPhase.getByPhaseNumber(tag.getInt("PhaseCreated"));
        levelCreated = tag.getInt("LevelCreated");
        killCount = tag.getDouble("KillCount");
        colonySpawned = tag.getBoolean("ColonySpawned");
        canDespawn = tag.getBoolean("CanDespawn");
        canChangeVariant = tag.getBoolean("CanChangeVariant");
        skinVariant = tag.getByte("SkinVariant");
        parasiteStatus = tag.getByte("ParasiteStatus");
        selfDestructState = tag.getInt("SelfDestructState");
        dislodgmentBitfield = tag.getInt("DislodgmentBitfield");
        damageCap = tag.getInt("DamageCap");
        miniDamage = tag.getFloat("MiniDamage");
        scentHPMultiplier = tag.getFloat("ScentHPMultiplier");
        foodSteal = tag.getFloat("FoodSteal");
        waitTimer = tag.getInt("WaitTimer");
        srpTicks = tag.getInt("SrpTicks");

        // Gene system
        for (int i = 0; i < geneBooleans.length; i++) {
            geneBooleans[i] = tag.getBoolean("GeneBool" + i);
        }
        for (int i = 0; i < geneFloats.length; i++) {
            geneFloats[i] = tag.getFloat("GeneFloat" + i);
        }

        // Block inventory
        blockInventoryNames.clear();
        ListTag nameList = tag.getList("BlockNames", 8);
        for (int i = 0; i < nameList.size(); i++) {
            blockInventoryNames.add(nameList.getString(i));
        }
        blockInventoryCounts.clear();
        ListTag countList = tag.getList("BlockCounts", 10);
        for (int i = 0; i < countList.size(); i++) {
            blockInventoryCounts.add(countList.getCompound(i).getInt("Count"));
        }

        // Owner resolution
        if (tag.contains("OwnerUUID")) {
            resolveOwner(tag.getUUID("OwnerUUID"));
        }

        // Infection state
        infectionLevel = tag.getInt("InfectionLevel");
        infectionResistance = tag.getFloat("InfectionResistance");

        // Evolution points
        evolutionPoints = tag.getInt("EvolutionPoints");

        // Abilities
        for (int i = 0; i < abilities.length; i++) {
            abilities[i] = tag.getBoolean("Ability" + i);
        }

        // Component data
        if (combatComponent != null && tag.contains("CombatData"))
            combatComponent.load(tag.getCompound("CombatData"));
        if (evolutionComponent != null && tag.contains("EvolutionData"))
            evolutionComponent.load(tag.getCompound("EvolutionData"));
        if (infectionComponent != null && tag.contains("InfectionData"))
            infectionComponent.load(tag.getCompound("InfectionData"));
        if (colonyComponent != null && tag.contains("ColonyData"))
            colonyComponent.load(tag.getCompound("ColonyData"));

        // Rendering state
        if (tag.contains("ScaleModifier")) scaleModifier = tag.getFloat("ScaleModifier");
        if (tag.contains("CloakingLevel")) cloakingLevel = tag.getFloat("CloakingLevel");
        if (tag.contains("HasGlow")) hasGlowFlag = tag.getBoolean("HasGlow");
        if (tag.contains("InfectionOverlayLevel")) infectionOverlayLevel = tag.getFloat("InfectionOverlayLevel");
        if (tag.contains("SwellAmount")) swellAmount = tag.getFloat("SwellAmount");
        if (tag.contains("MeltingProgress")) meltingProgress = tag.getFloat("MeltingProgress");
        if (tag.contains("IsLeader")) isLeaderFlag = tag.getBoolean("IsLeader");

        // Leader resolution
        if (tag.contains("LeaderUUID")) {
            resolveLeader(tag.getUUID("LeaderUUID"));
        }

        applyBonuses();
    }

    // ========== RangedAttackMob Implementation ==========

    /** Default no-op. Subclasses override for ranged attacks. */
    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        // Default no-op - subclasses override for ranged attacks
    }

    // ========== IParasite Implementation ==========

    @Override
    public ParasiteType getParasiteType() { return parasiteType; }

    /** Sets the parasite type. Not part of IParasite interface; used internally. */
    public void setParasiteType(ParasiteType type) { this.parasiteType = type; }

    @Override
    public EvoPhase getPhaseCreated() { return phaseCreated; }

    @Override
    public void setPhaseCreated(EvoPhase phase) { this.phaseCreated = phase; }

    @Override
    public int getLevelCreated() { return levelCreated; }

    @Override
    public void setLevelCreated(int level) { this.levelCreated = level; }

    @Override
    public double getKillCount() { return killCount; }

    @Override
    public void setKillCount(double kills) { this.killCount = kills; }

    @Override
    public boolean isColonySpawned() { return colonySpawned; }

    @Override
    public void setColonySpawned(boolean colony) { this.colonySpawned = colony; }

    @Override
    public GeneType[] getActiveGenes() {
        List<GeneType> active = new ArrayList<>();
        for (GeneType gene : GeneType.values()) {
            if (hasGene(gene)) active.add(gene);
        }
        return active.toArray(new GeneType[0]);
    }

    @Override
    public boolean hasGene(GeneType gene) {
        if (gene.isBoolean()) {
            return geneBooleans[gene.getIndex()];
        } else {
            return geneFloats[gene.getIndex()] > gene.getDefaultValue();
        }
    }

    @Override
    public void activateGene(GeneType gene) {
        if (gene.isBoolean()) {
            geneBooleans[gene.getIndex()] = true;
        } else {
            geneFloats[gene.getIndex()] = gene.getDefaultValue() + 0.1F;
        }
    }

    // ========== IEvolvable Implementation ==========

    @Override
    public int getEvolutionPoints() {
        if (evolutionComponent != null) {
            return (int) evolutionComponent.getEvolutionPointsInternal();
        }
        return evolutionPoints;
    }

    @Override
    public void addEvolutionPoints(int points) { this.evolutionPoints += points; }

    @Override
    public boolean canEvolveTo(ParasiteType type) {
        if (type == null) return false;
        return getEvolutionThreshold(type) <= this.evolutionPoints;
    }

    @Override
    public float getEvolutionThreshold(ParasiteType type) {
        // Base threshold scales with the target type's evolution weight
        // Directly computes threshold without calling canEvolveTo (avoids circular recursion)
        return type.getEvolutionWeight() * 100.0F;
    }

    @Override
    public EvolutionPath getEvolutionPath() { return evolutionPath; }

    @Override
    public void setEvolutionPath(EvolutionPath path) { this.evolutionPath = path; }

    // ========== IInfectable Implementation ==========

    @Override
    public void addInfection(int amplifier) {
        if (isImmuneToInfection()) return;
        int newLevel = infectionLevel + amplifier;
        infectionLevel = Math.max(0, Math.min(newLevel, 100));
    }

    @Override
    public int getInfectionLevel() { return infectionLevel; }

    @Override
    public void setInfectionLevel(int level) { this.infectionLevel = Math.max(0, Math.min(level, 100)); }

    /** Parasites are always immune to their own infection. */
    @Override
    public boolean isImmuneToInfection() { return true; }

    @Override
    public float getInfectionResistance() { return infectionResistance; }

    // ========== IHitboxedEntity Implementation ==========

    /** Returns null - this IS the parent entity, not a hitbox part. */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> T getParent() { return null; }

    // ========== ICanAbility Implementation ==========

    @Override
    public boolean hasAbility(ICanAbility.AbilityType type) {
        int idx = type.ordinal();
        return idx >= 0 && idx < abilities.length && abilities[idx];
    }

    /** Sets whether this entity has the given ability. */
    public void setAbility(ICanAbility.AbilityType type, boolean value) {
        int idx = type.ordinal();
        if (idx >= 0 && idx < abilities.length) {
            abilities[idx] = value;
        }
    }

    // ========== DataParameter Accessors ==========

    /** Returns the parasite status byte from synced data. */
    public byte getParasiteStatus() { return this.entityData.get(DATA_STATUS); }

    /** Sets the parasite status byte in both local field and synced data. */
    public void setParasiteStatus(byte status) {
        this.parasiteStatus = status;
        this.entityData.set(DATA_STATUS, status);
    }

    /** Returns the skin variant byte from synced data. */
    public byte getSkinVariant() { return this.entityData.get(DATA_SKIN); }

    /** Sets the skin variant byte. */
    public void setSkinVariant(byte variant) {
        this.skinVariant = variant;
        this.entityData.set(DATA_SKIN, variant);
    }

    /** Returns whether this entity is in a cold biome (from synced data). */
    public boolean isColdBiome() { return this.entityData.get(DATA_COLD); }

    /** Returns the self-destruct countdown state from synced data. */
    public int getSelfDestructState() { return this.entityData.get(DATA_SELFDESTRUCT); }

    /** Sets the self-destruct state. */
    public void setSelfDestructState(int state) {
        this.selfDestructState = state;
        this.entityData.set(DATA_SELFDESTRUCT, state);
    }

    // ========== Dislodgment System ==========

    /** Adds a dislodgment code to this entity's bitfield. */
    public void addDislodgmentCode(DislodgmentCode code) {
        dislodgmentBitfield |= (1 << code.getCode());
        this.entityData.set(DATA_DISLODGE, dislodgmentBitfield);
    }

    /** Removes a dislodgment code from this entity's bitfield. */
    public void removeDislodgmentCode(DislodgmentCode code) {
        dislodgmentBitfield &= ~(1 << code.getCode());
        this.entityData.set(DATA_DISLODGE, dislodgmentBitfield);
    }

    /** Checks whether this entity has the specified dislodgment code. */
    public boolean hasDislodgmentCode(DislodgmentCode code) {
        return (dislodgmentBitfield & (1 << code.getCode())) != 0;
    }

    /** Returns true if this entity has any dislodgment codes active. */
    public boolean hasAnyDislodgment() { return dislodgmentBitfield != 0; }

    /** Applies dislodgment effects when this entity is hurt by a living attacker. */
    protected void applyDislodgmentOnHurt(LivingEntity attacker, float damage) {
        if (!hasAnyDislodgment()) return;

        // Code 6: Break held items
        if (hasDislodgmentCode(DislodgmentCode.SIX)) {
            for (ItemStack held : attacker.getHandSlots()) {
                if (held.isDamageableItem()) {
                    held.hurtAndBreak(2, attacker, e -> {});
                }
            }
        }
        // Code 4: Toxin cloud on hit
        if (hasDislodgmentCode(DislodgmentCode.FOUR)) {
            attacker.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
        }
        // Code 5: Acid splash on hit
        if (hasDislodgmentCode(DislodgmentCode.FIVE)) {
            attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
        }
        // Code 10: Slow cloud on hit
        if (hasDislodgmentCode(DislodgmentCode.TEN)) {
            attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
        }
    }

    /** Applies dislodgment effects when this entity dies. */
    protected void applyDislodgmentOnDeath(DamageSource source) {
        if (!hasAnyDislodgment()) return;
        if (!(source.getEntity() instanceof LivingEntity killer)) return;

        // Code 2: Jugg spawn on death
        if (hasDislodgmentCode(DislodgmentCode.TWO)) {
            // TODO: Spawn Jugg parasite entity
        }

        // Code 3: Minor explosion - knockback nearby entities
        if (hasDislodgmentCode(DislodgmentCode.THREE)) {
            AABB area = this.getBoundingBox().inflate(3.0);
            List<LivingEntity> nearby = this.level().getEntitiesOfClass(LivingEntity.class, area,
                    e -> e != this && !(e instanceof EntityParasiteBase));
            for (LivingEntity entity : nearby) {
                double dx = entity.getX() - this.getX();
                double dz = entity.getZ() - this.getZ();
                entity.knockback(1.5F, dx, dz);
            }
        }

        // Code 7: Heal nearby parasites
        if (hasDislodgmentCode(DislodgmentCode.SEVEN)) {
            AABB area = this.getBoundingBox().inflate(8.0);
            List<EntityParasiteBase> nearby = this.level().getEntitiesOfClass(EntityParasiteBase.class, area);
            for (EntityParasiteBase parasite : nearby) {
                parasite.heal(4.0F);
            }
        }

        // Code 8: Damage nearby non-parasites
        if (hasDislodgmentCode(DislodgmentCode.EIGHT)) {
            AABB area = this.getBoundingBox().inflate(5.0);
            List<LivingEntity> nearby = this.level().getEntitiesOfClass(LivingEntity.class, area,
                    e -> !(e instanceof EntityParasiteBase));
            for (LivingEntity entity : nearby) {
                entity.hurt(this.damageSources().generic(), 6.0F);
            }
        }

        // Code 9: Drain hunger
        if (hasDislodgmentCode(DislodgmentCode.NINE)) {
            AABB area = this.getBoundingBox().inflate(5.0);
            List<Player> nearby = this.level().getEntitiesOfClass(Player.class, area);
            for (Player player : nearby) {
                player.getFoodData().setFoodLevel(
                        Math.max(0, player.getFoodData().getFoodLevel() - 6));
            }
        }

        // Code 14: Scream alerts nearby parasites
        if (hasDislodgmentCode(DislodgmentCode.FOURTEEN)) {
            checkBeckonOnDeath(source);
        }

        // Code 19: Fire burst
        if (hasDislodgmentCode(DislodgmentCode.NINETEEN)) {
            AABB area = this.getBoundingBox().inflate(4.0);
            List<LivingEntity> nearby = this.level().getEntitiesOfClass(LivingEntity.class, area,
                    e -> !(e instanceof EntityParasiteBase));
            for (LivingEntity entity : nearby) {
                entity.setSecondsOnFire(5);
            }
        }
    }

    // ========== Kill Count ==========

    /** Adds to the kill count and notifies the evolution component. */
    public void addKillCount(double kills) {
        this.killCount += kills;
        if (evolutionComponent != null) {
            evolutionComponent.onKillCountChanged(killCount);
        }
    }

    // ========== Gene System ==========

    /** Gets a boolean gene value by index. */
    public boolean getGeneBoolean(int index) {
        return index >= 0 && index < geneBooleans.length && geneBooleans[index];
    }

    /** Sets a boolean gene value by index. */
    public void setGeneBoolean(int index, boolean value) {
        if (index >= 0 && index < geneBooleans.length) {
            geneBooleans[index] = value;
        }
    }

    /** Gets a float gene value by index. */
    public float getGeneFloat(int index) {
        return index >= 0 && index < geneFloats.length ? geneFloats[index] : 0.0F;
    }

    /** Sets a float gene value by index. */
    public void setGeneFloat(int index, float value) {
        if (index >= 0 && index < geneFloats.length) {
            geneFloats[index] = value;
        }
    }

    /**
     * Gets a gene value as an int.
     * For boolean genes: returns 0 or 1.
     * For float genes: returns (int)(floatValue * 10).
     *
     * @param index the gene index (matches GeneType indices)
     * @return the gene value as int
     */
    public int getGene(int index) {
        if (index < 0) return 0;
        if (index < geneBooleans.length) {
            return geneBooleans[index] ? 1 : 0;
        }
        int floatIdx = index - geneBooleans.length;
        if (floatIdx >= 0 && floatIdx < geneFloats.length) {
            return (int)(geneFloats[floatIdx] * 10);
        }
        return 0;
    }

    /**
     * Returns the total count of active genes (boolean true + float above default).
     * Used by AI goals to gate behavior behind gene requirements.
     *
     * @return the number of active genes
     */
    public int getGene() {
        int count = 0;
        for (boolean b : geneBooleans) { if (b) count++; }
        for (GeneType gene : GeneType.values()) {
            if (!gene.isBoolean() && geneFloats[gene.getIndex()] > gene.getDefaultValue()) count++;
        }
        return count;
    }

    // ========== Bonuses and Modifications ==========

    /** Applies phase-based bonuses and gene modifications to attributes. */
    public void applyBonuses() {
        float phaseBonus = phaseCreated.getEntityStatBonus();

        // Phase-based health multiplier (using AttributeModifier to prevent compounding on NBT load)
        float healthMultiplier = phaseBonus; // additive portion for MULTIPLY_TOTAL
        if (this.getAttribute(Attributes.MAX_HEALTH) != null) {
            var attr = this.getAttribute(Attributes.MAX_HEALTH);
            attr.removeModifier(PHASE_HEALTH_MODIFIER_UUID);
            if (healthMultiplier != 0.0F) {
                attr.addTransientModifier(new AttributeModifier(
                        PHASE_HEALTH_MODIFIER_UUID, "Phase health bonus",
                        healthMultiplier, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }

        // Phase-based damage multiplier (using AttributeModifier to prevent compounding on NBT load)
        float damageMultiplier = phaseBonus;
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            var attr = this.getAttribute(Attributes.ATTACK_DAMAGE);
            attr.removeModifier(PHASE_DAMAGE_MODIFIER_UUID);
            if (damageMultiplier != 0.0F) {
                attr.addTransientModifier(new AttributeModifier(
                        PHASE_DAMAGE_MODIFIER_UUID, "Phase damage bonus",
                        damageMultiplier, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }

        applyGeneModifications();
    }

    /** Applies gene-based modifications to stats and behavior flags. */
    public void applyGeneModifications() {
        // Gene MIN_DAMAGE: enables minimum damage
        if (geneBooleans[GeneType.MIN_DAMAGE.getIndex()]) {
            miniDamage = Math.max(miniDamage, geneFloats[GeneType.POISON_HEALING.getIndex()]);
        }

        // Gene DAMAGE_CAP: enables damage cap
        if (geneBooleans[GeneType.DAMAGE_CAP.getIndex()]) {
            damageCap = Math.max(1, (int)(10.0 / (1.0 + geneFloats[GeneType.ATTACK_SPEED.getIndex()])));
        }

        // Gene SPRINTING: speed boost (using AttributeModifier to prevent compounding)
        if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            var speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
            speedAttr.removeModifier(PHASE_SPEED_MODIFIER_UUID);
            if (geneBooleans[GeneType.SPRINTING.getIndex()]) {
                float speedBoost = geneFloats[GeneType.ATTACK_SPEED.getIndex()] * 0.5F;
                speedAttr.addTransientModifier(new AttributeModifier(
                        PHASE_SPEED_MODIFIER_UUID, "Gene speed bonus",
                        speedBoost, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }

        // Float gene: ANTI_KNOCKBACK (using AttributeModifier to prevent compounding)
        float antiKB = geneFloats[GeneType.ANTI_KNOCKBACK.getIndex()];
        if (this.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
            var kbAttr = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            kbAttr.removeModifier(GENE_HEALTH_MODIFIER_UUID);
            if (antiKB > 0) {
                kbAttr.addTransientModifier(new AttributeModifier(
                        GENE_HEALTH_MODIFIER_UUID, "Gene knockback resistance",
                        Math.min(1.0, antiKB), AttributeModifier.Operation.ADDITION));
            }
        }

        // Float gene: INFECTIOUSNESS
        foodSteal = geneFloats[GeneType.INFECTIOUSNESS.getIndex()];
    }

    // ========== Special Skills ==========

    /** Dispatches a special skill by ID. Override for custom skills. */
    public void doSpecialSkill(byte skillId) {
        switch (skillId) {
            case 13 -> skillBreakBlocks();
            case 14 -> skillLeap();
            default -> { /* Subclass override for custom skills */ }
        }
    }

    /** Breaks blocks in front of the parasite. */
    protected void skillBreakBlocks() {
        if (breakCooldown > 0) return;
        if (!ModConfigSystems.canParasitesBreakBlocks()) return;

        BlockPos frontPos = this.blockPosition().relative(this.getDirection());
        Level level = this.level();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 0; dy <= 2; dy++) {
                BlockPos targetPos = frontPos.offset(dx, dy, 0);
                BlockState state = level.getBlockState(targetPos);

                if (!state.isAir() && state.getDestroySpeed(level, targetPos) >= 0
                        && state.getDestroySpeed(level, targetPos) <= ModConfigSystems.getMaxBlockHardness()) {
                    String blockName = state.getBlock().getDescriptionId();
                    int idx = blockInventoryNames.indexOf(blockName);
                    if (idx >= 0) {
                        blockInventoryCounts.set(idx, blockInventoryCounts.get(idx) + 1);
                    } else {
                        blockInventoryNames.add(blockName);
                        blockInventoryCounts.add(1);
                    }
                    level.destroyBlock(targetPos, true, this);
                    breakProgress++;
                }
            }
        }
        breakCooldown = ModConfigSystems.getBlockBreakCooldown();
    }

    /** Performs a leap attack toward the current target. */
    protected void skillLeap() {
        if (leapCooldown > 0) return;
        if (!canLeap()) return;

        LivingEntity target = getTarget();
        if (target == null) return;

        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 2.0) {
            double dy = target.getY() - this.getY();
            float leapStrength = getLeapStrength();

            double motionX = (dx / distance) * leapStrength;
            double motionY = Math.max(dy * 0.3, 0.4) * leapStrength;
            double motionZ = (dz / distance) * leapStrength;

            this.setDeltaMovement(new Vec3(motionX, motionY, motionZ));
            this.isLeaping = true;
            this.setParasiteStatus((byte) 10);
        }

        leapCooldown = ModConfigSystems.getLeapCooldown();
    }

    /** Checks whether this parasite can currently leap. */
    protected boolean canLeap() {
        boolean waterLeap = geneBooleans[GeneType.WATER_LEAP.getIndex()];
        if (this.isInWater() && !waterLeap) return false;
        return this.onGround() && !this.isBaby();
    }

    /** Gets the leap strength based on phase and genes. */
    protected float getLeapStrength() {
        float base = 0.6F;
        base += phaseCreated.getPhaseNumber() * 0.05F;
        base += geneFloats[GeneType.LEAP_POWER.getIndex()] * 0.3F;
        return base;
    }

    // ========== Skill System ==========

    /**
     * Checks whether the special skill with the given ID is available.
     *
     * @param skillId the skill identifier
     * @return true if the skill can be used
     */
    public boolean canUseSkill(byte skillId) {
        if (skillCooldown > 0) return false;
        if (!geneBooleans[GeneType.SPECIAL_MOVE.getIndex()]) return false;
        return true;
    }

    /**
     * Executes a special skill targeting the given entity.
     *
     * @param skillId the skill identifier
     * @param target  the target entity
     */
    public void executeSkill(byte skillId, LivingEntity target) {
        doSpecialSkill(skillId);
        skillCooldown = 40;
    }

    /** Executes the default special skill. */
    public void executeSkill() {
        doSpecialSkill((byte) 0);
    }

    /** Callback when a skill is charging. Override for charging behavior. */
    public void onSkillCharging(byte skillId, int ticksCharging, int maxCharge) { }

    /** Callback when a skill executes. Override for execution effects. */
    public void onSkillExecute(byte skillId) { }

    /** Callback when a minion is summoned at a position. */
    public void onSummonMinion(BlockPos pos, int count) { }

    /** Callback when a minion entity is summoned. */
    public void onSummonMinion(EntityParasiteBase minion) { }

    /** Returns true if this parasite is currently self-destructing. */
    public boolean isSelfDestructing() { return selfDestructState > 0; }

    // ========== Cyst Spawning ==========

    /** Spawns a cyst at the entity's position containing its block inventory. */
    protected void spawnCyst() {
        if (this.level().isClientSide) return;
        if (blockInventoryNames.isEmpty()) return;

        ServerLevel serverLevel = (ServerLevel) this.level();
        // TODO: Replace with actual EntityGoreCyst spawn when entity is implemented
        serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                this.getX(), this.getY() + 1.0, this.getZ(),
                1, 0.0, 0.0, 0.0, 0.0);
    }

    // ========== Self-Destruct ==========

    /** Handles the self-destruct countdown and explosion. */
    protected void handleSelfDestruct() {
        if (selfDestructState == -1) {
            // Defusing: decrease swell and reset
            swellAmount = Math.max(0.0F, swellAmount - 0.05F);
            if (swellAmount <= 0.0F) {
                swellAmount = 0.0F;
                selfDestructState = 0;
                this.entityData.set(DATA_SELFDESTRUCT, 0);
            }
            return;
        }

        selfDestructState--;
        swellAmount = Math.min(1.0F, swellAmount + 0.05F);

        if (selfDestructState <= 0) {
            float radius = 3.0F + phaseCreated.getPhaseNumber() * 0.3F;
            AABB area = this.getBoundingBox().inflate(radius);
            List<LivingEntity> nearby = this.level().getEntitiesOfClass(LivingEntity.class, area,
                    e -> e != this && !(e instanceof EntityParasiteBase));
            for (LivingEntity entity : nearby) {
                float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F;
                entity.hurt(this.damageSources().explosion(this, null), damage);
                if (infectionComponent != null) {
                    infectionComponent.spreadCOTH(entity);
                }
            }

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        2, 0.5, 0.5, 0.5, 0.0);
            }

            this.kill();
        }
    }

    // ========== COTH Contact Spread ==========

    /** Spreads COTH to nearby non-parasite entities on contact. */
    protected void spreadCOTHContact() {
        if (this.srpTicks % 20 != 0) return;
        if (infectionComponent == null) return;
        if (!infectionComponent.canSpread()) return;

        float range = getCothContactRange();
        AABB area = this.getBoundingBox().inflate(range);
        List<LivingEntity> nearby = this.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != this && !(e instanceof EntityParasiteBase));

        for (LivingEntity entity : nearby) {
            infectionComponent.spreadCOTH(entity);
        }
    }

    /** Returns the COTH contact spread range. Override for type-specific ranges. */
    protected float getCothContactRange() { return 1.5F; }

    // ========== Beckon System ==========

    /** Checks and triggers beckon behavior on death - alerts nearby parasites. */
    protected void checkBeckonOnDeath(DamageSource source) {
        if (random.nextFloat() > ModConfigSystems.getBeckonChance()) return;

        AABB area = this.getBoundingBox().inflate(phaseCreated.getAwarenessRange());
        List<EntityParasiteBase> nearby = this.level().getEntitiesOfClass(EntityParasiteBase.class, area,
                p -> p != this && p.isAlive());

        for (EntityParasiteBase parasite : nearby) {
            if (source.getEntity() instanceof LivingEntity killer) {
                parasite.setTarget(killer);
                parasite.setParasiteStatus((byte) 1);
            }
        }
    }

    // ========== Food Stealing ==========

    /** Steals food from a player and heals this entity. */
    protected void stealFood(Player player) {
        if (foodSteal <= 0) return;

        int foodLevel = player.getFoodData().getFoodLevel();
        int stealAmount = Math.min(foodLevel, (int)(foodSteal * 4));
        if (stealAmount > 0) {
            player.getFoodData().setFoodLevel(foodLevel - stealAmount);
            this.heal(stealAmount * 0.5F);
        }
    }

    // ========== Status Handling ==========

    /** Handles status-based logic each tick. */
    protected void handleStatus() {
        byte status = getParasiteStatus();
        switch (status) {
            case 0: break; // Idle
            case 1, 2: break; // Attacking
            case 6: break; // Dying
            case 10: // Leaping
                if (this.onGround() && isLeaping) {
                    isLeaping = false;
                    setParasiteStatus((byte) 0);
                }
                break;
            default: break;
        }
    }

    // ========== Hitbox Parts ==========

    /** Updates all hitbox part positions. */
    protected void updateHitboxParts() {
        for (EntityHitbox part : hitboxParts) {
            if (part != null && part.isAlive()) {
                part.tick();
            }
        }
    }

    /** Removes all hitbox parts from the world. */
    protected void removeHitboxParts() {
        for (EntityHitbox part : hitboxParts) {
            if (part != null) part.discard();
        }
    }

    /** Returns true if this entity has multiple hitbox parts. */
    @Override
    public boolean isMultipartEntity() {
        return hitboxParts.length > 0;
    }

    /** Returns the hitbox part entities. */
    @Override
    public PartEntity<?>[] getParts() {
        return hitboxParts;
    }

    // ========== Look At ==========

    /** Makes this entity face the given target entity. */
    public void lookAt(Entity target) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double dy = target.getEyeY() - this.getEyeY();
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float)(Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        float pitch = (float)(-(Mth.atan2(dy, dist) * (180.0 / Math.PI)));
        this.setYRot(yaw);
        this.yRotO = yaw;
        this.setXRot(pitch);
        this.xRotO = pitch;
    }

    // ========== Biome Checks ==========

    /** Checks if the entity is in a parasite-tagged biome. */
    public boolean isParasiteBiome() {
        return this.level().getBiome(this.blockPosition()).is(
                TagKey.create(Registries.BIOME,
                        new ResourceLocation("subspaceparasite", "is_parasite")));
    }

    /** Checks if the entity is in a cold/icy biome. */
    protected boolean isInColdBiome() {
        return this.level().getBiome(this.blockPosition()).value().getBaseTemperature() < 0.15F;
    }

    // ========== UUID Resolution ==========

    /** Resolves the owner entity from a stored UUID after NBT load. */
    protected void resolveOwner(UUID ownerUUID) {
        if (this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(ownerUUID);
            if (entity instanceof EntityParasiteBase parasiteOwner) {
                this.owner = parasiteOwner;
            }
        }
    }

    /** Resolves the leader entity from a stored UUID after NBT load. */
    protected void resolveLeader(UUID leaderUUID) {
        if (this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(leaderUUID);
            if (entity instanceof EntityParasiteBase parasiteLeader) {
                this.leader = parasiteLeader;
            }
        }
    }

    // ========== Static Helpers ==========

    /** Gets the current evolution phase for the given level. */
    public static EvoPhase getCurrentPhase(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return ModWorldData.get(serverLevel).getCurrentPhase();
        }
        return EvoPhase.ZERO;
    }

    // ========== Despawn Control ==========

    public boolean canDespawn() {
        return canDespawn;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return !canDespawn || owner != null;
    }

    /** Sets whether this entity can despawn naturally. */
    public void setCanDespawn(boolean value) { this.canDespawn = value; }

    // ========== Attribute Supplier ==========

    /** Creates the default attribute supplier for parasite entities. */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }

    // ========== Rendering Methods (for client) ==========

    /**
     * Returns the tier level based on the parasite's evolution path.
     * Used by the renderer for texture selection.
     *
     * @return the tier level (0 = lowest, 14 = highest)
     */
    public int getParasiteTier() { return evolutionPath.getTierLevel(); }

    /**
     * Returns the parasite type ID for network and rendering lookups.
     *
     * @return parasiteType.getId(), or -1 if parasiteType is null
     */
    public int getParasiteTypeId() {
        return parasiteType != null ? parasiteType.getId() : -1;
    }

    /**
     * Returns the parasite type ordinal for texture mapping.
     * Uses the evolution path ordinal as a fallback.
     *
     * @return the type ordinal for rendering selection
     */
    public int getParasiteTypeOrdinal() {
        if (parasiteType != null) {
            return parasiteType.getEvolutionTier().ordinal();
        }
        return evolutionPath.ordinal();
    }

    /**
     * Returns the current animation state for the client renderer.
     * Mapped from parasiteStatus and runtime state.
     *
     * @return the current AnimState
     */
    public AnimState getCurrentAnimState() {
        if (selfDestructState > 0) return AnimState.SWELLING;
        if (meltingProgress > 0) return AnimState.MELTING;
        if (cloakingLevel > 0.5F) return AnimState.CLOAKING;
        switch (parasiteStatus) {
            case 0: return getDeltaMovement().horizontalDistance() > 0.01 ? AnimState.WALKING : AnimState.IDLE;
            case 1:
            case 2: return AnimState.ATTACKING;
            case 6: return AnimState.DYING;
            case 10: return AnimState.LEAPING;
            default: return AnimState.IDLE;
        }
    }

    /** Gets the animation progress (0.0-1.0) for blending. */
    public float getAnimProgress() { return Mth.clamp(animProgress, 0.0F, 1.0F); }

    /** Gets the animation progress with partial tick interpolation (alias for getAnimProgress). */
    public float getAnimProgress(float partialTick) { return getAnimProgress(); }

    /** Sets the animation progress. */
    public void setAnimProgress(float progress) { this.animProgress = Mth.clamp(progress, 0.0F, 1.0F); }

    /** Gets the scale modifier for the renderer. */
    public float getScaleModifier() { return scaleModifier; }

    /** Sets the scale modifier for the renderer. */
    public void setScaleModifier(float modifier) { this.scaleModifier = modifier; }

    /** Gets the animated scale offset. Default returns 0.0F (no animation). */
    public float getScaleAnimation(float partialTick) { return 0.0F; }

    /** Gets the cloaking/invisibility level (0.0 = visible, 1.0 = fully cloaked). */
    public float getCloakingLevel() { return Mth.clamp(cloakingLevel, 0.0F, 1.0F); }

    /** Sets the cloaking level. */
    public void setCloakingLevel(float level) { this.cloakingLevel = Mth.clamp(level, 0.0F, 1.0F); }

    /** Returns true if this entity should render its glow layer. */
    public boolean hasGlow() {
        return hasGlowFlag || geneBooleans[GeneType.SPECIAL_MOVE.getIndex()];
    }

    /** Sets whether the glow layer should render. */
    public void setHasGlow(boolean glow) { this.hasGlowFlag = glow; }

    /** Gets the infection overlay intensity (0.0 = none, 1.0 = full). */
    public float getInfectionOverlayLevel() { return Mth.clamp(infectionOverlayLevel, 0.0F, 1.0F); }

    /** Sets the infection overlay level. */
    public void setInfectionOverlayLevel(float level) { this.infectionOverlayLevel = Mth.clamp(level, 0.0F, 1.0F); }

    /** Gets the swell amount for self-destruct rendering (0.0-1.0). */
    public float getSwellAmount() {
        if (selfDestructState <= 0) return 0.0F;
        return Mth.clamp(swellAmount, 0.0F, 1.0F);
    }

    /** Sets the swell amount. */
    public void setSwellAmount(float amount) { this.swellAmount = Mth.clamp(amount, 0.0F, 1.0F); }

    /** Gets the melting animation progress (0.0-1.0). */
    public float getMeltingProgress() { return Mth.clamp(meltingProgress, 0.0F, 1.0F); }

    /** Sets the melting progress. */
    public void setMeltingProgress(float progress) { this.meltingProgress = Mth.clamp(progress, 0.0F, 1.0F); }

    /**
     * Gets the hurt flash timer for white overlay rendering, interpolated
     * with partial tick for smooth rendering.
     *
     * @param partialTick the partial tick for client-side interpolation
     * @return flash intensity, decreasing as hurtTime recovers
     */
    public float getHurtFlashTimer(float partialTick) {
        if (hurtFlashTimer <= 0) return 0.0F;
        float maxFlash = 10.0F;
        return Mth.clamp(1.0F - (hurtFlashTimer - partialTick) / maxFlash, 0.0F, 1.0F);
    }

    // ========== Block Interaction ==========

    /** Returns whether this parasite can infest the given block. Default: false. */
    public boolean canInfestBlock(BlockState state) { return false; }

    /** Returns the infested block state for the given original block. Default: AIR. */
    public BlockState getInfestedBlock(BlockState original, int stage) {
        return Blocks.AIR.defaultBlockState();
    }

    /** Returns the residue block to place. Default: AIR. */
    public BlockState getResidueBlock() { return Blocks.AIR.defaultBlockState(); }

    // ========== AI/Status Methods ==========

    /** Returns true when the parasite is in passive/waiting mode. */
    public boolean isPassive() { return parasiteStatus == 0 && getTarget() == null; }

    /** Callback when a melee attack lands. Override for tier-specific behavior. */
    public void onMeleeAttack(LivingEntity target, float damage) { }

    /** Convenience overload for melee attack callback without explicit damage. */
    public void onMeleeAttack(LivingEntity target) {
        onMeleeAttack(target, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
    }

    /** Returns bonus attack reach. Override for extended-reach variants. */
    public float getAttackReachBonus() { return 0.0F; }

    /** Synonym for setCanWorkTask(). Used by AI goals. */
    public void setWorking(boolean working) { setCanWorkTask(working); }

    /** Returns whether this parasite can currently work (execute AI tasks). */
    public boolean canWorkTask() { return canWorkTask; }

    /** Sets whether this parasite can currently work. */
    public void setCanWorkTask(boolean value) { this.canWorkTask = value; }

    // ========== Leader System ==========

    /** Returns the leader entity this parasite follows. */
    public EntityParasiteBase getLeader() { return leader; }

    /** Sets the leader entity. */
    public void setLeader(EntityParasiteBase leader) { this.leader = leader; }

    /** Returns true if this entity is a leader. */
    public boolean isLeader() { return isLeaderFlag; }

    /** Sets whether this entity is a leader. */
    public void setLeader(boolean leader) { this.isLeaderFlag = leader; }

    // ========== Component Accessors ==========

    public CombatComponent getCombatComponent() { return combatComponent; }
    public EvolutionComponent getEvolutionComponent() { return evolutionComponent; }
    public InfectionComponent getInfectionComponent() { return infectionComponent; }
    public ColonyComponent getColonyComponent() { return colonyComponent; }

    // ========== Owner/Follow ==========

    /** Gets the owner of this parasite. */
    public EntityParasiteBase getOwner() { return owner; }

    /** Sets the owner of this parasite. */
    public void setOwner(EntityParasiteBase owner) { this.owner = owner; }

    /** Gets the follow target. */
    public EntityParasiteBase getFollowTarget() { return followTarget; }

    /** Sets the follow target. */
    public void setFollowTarget(EntityParasiteBase target) { this.followTarget = target; }

    /** Gets the pivot entity. */
    public EntityParasiteBase getPivotEntity() { return pivotEntity; }

    /** Sets the pivot entity. */
    public void setPivotEntity(EntityParasiteBase pivot) { this.pivotEntity = pivot; }

    // ========== Misc Accessors ==========

    public int getWaitTimer() { return waitTimer; }

    public void setWaitTimer(int ticks) {
        this.waitTimer = ticks;
        if (ticks > 0) canWorkTask = false;
    }

    public int getDamageCapValue() { return damageCap; }
    public void setDamageCapValue(int value) { this.damageCap = value; }
    public float getMiniDamage() { return miniDamage; }
    public void setMiniDamage(float value) { this.miniDamage = value; }

    // ========== Inner AI Goal Classes ==========

    /**
     * AI goal that blocks all other goals while the parasite is waiting.
     * Used for post-attack delays, transformation pauses, etc.
     */
    public static class ParasiteWaitGoal extends Goal {
        private final EntityParasiteBase parasite;

        public ParasiteWaitGoal(EntityParasiteBase parasite) {
            this.parasite = parasite;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return !parasite.canWorkTask();
        }

        @Override
        public boolean canContinueToUse() {
            return !parasite.canWorkTask();
        }

        @Override
        public void start() {
            parasite.getNavigation().stop();
        }

        @Override
        public void tick() {
            parasite.getNavigation().stop();
        }
    }

    /**
     * AI goal that makes the parasite jump toward elevated targets
     * when it cannot reach them via normal pathfinding.
     */
    public static class ParasiteJumpGoal extends Goal {
        private final EntityParasiteBase parasite;
        private int jumpCooldown;

        public ParasiteJumpGoal(EntityParasiteBase parasite) {
            this.parasite = parasite;
            this.setFlags(EnumSet.of(Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (jumpCooldown > 0) { jumpCooldown--; return false; }
            LivingEntity target = parasite.getTarget();
            if (target == null || !target.isAlive()) return false;
            if (!parasite.onGround()) return false;
            if (!parasite.canLeap()) return false;
            double distY = target.getY() - parasite.getY();
            return distY > 1.0 && parasite.distanceTo(target) < 8.0;
        }

        @Override
        public boolean canContinueToUse() { return false; }

        @Override
        public void start() {
            LivingEntity target = parasite.getTarget();
            if (target != null) {
                double dx = target.getX() - parasite.getX();
                double dz = target.getZ() - parasite.getZ();
                double dist = Math.sqrt(dx * dx + dz * dz);
                float strength = parasite.getLeapStrength() * 0.7F;
                if (dist > 0.1) {
                    parasite.setDeltaMovement(new Vec3(
                            (dx / dist) * strength,
                            0.5,
                            (dz / dist) * strength));
                }
            }
            jumpCooldown = 40;
        }
    }

    /**
     * AI goal that makes the parasite follow its owner entity.
     * Only activates when the parasite has an owner and is too far away.
     */
    public static class ParasiteFollowOwnerGoal extends Goal {
        private final EntityParasiteBase parasite;
        private int pathCooldown;
        private static final double FOLLOW_RANGE = 12.0;
        private static final double TELEPORT_RANGE = 40.0;

        public ParasiteFollowOwnerGoal(EntityParasiteBase parasite) {
            this.parasite = parasite;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            EntityParasiteBase owner = parasite.getOwner();
            if (owner == null || !owner.isAlive()) return false;
            if (parasite.getTarget() != null) return false;
            return parasite.distanceTo(owner) > FOLLOW_RANGE;
        }

        @Override
        public boolean canContinueToUse() {
            EntityParasiteBase owner = parasite.getOwner();
            if (owner == null || !owner.isAlive()) return false;
            return parasite.distanceTo(owner) > 3.0;
        }

        @Override
        public void start() {
            pathCooldown = 0;
        }

        @Override
        public void tick() {
            EntityParasiteBase owner = parasite.getOwner();
            if (owner == null) return;

            if (parasite.distanceTo(owner) > TELEPORT_RANGE) {
                parasite.moveTo(owner.getX(), owner.getY(), owner.getZ(),
                        parasite.getYRot(), parasite.getXRot());
                return;
            }

            pathCooldown--;
            if (pathCooldown <= 0) {
                parasite.getNavigation().moveTo(owner, 1.0);
                pathCooldown = 10;
            }
        }

        @Override
        public void stop() {
            parasite.getNavigation().stop();
        }
    }
}
