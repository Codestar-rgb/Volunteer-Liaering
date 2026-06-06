package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ColonyDefenseGoal;
import com.subspaceparasite.common.entity.ai.ParasiteBlockInfestGoal;
import com.subspaceparasite.common.entity.ai.ParasiteGiveEffectsGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSpecialSkillGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSummonGoal;
import com.subspaceparasite.common.world.ColonyStructureGenerator;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Abstract base class for Rooter-tier parasites.
 * <p>
 * Rooters are colony nexus structures that buff nearby parasites and
 * distribute incoming damage among defender entities (rooterballs).
 * They generate NexusProtection3 structures.
 * <p>
 * Extends {@link EntityStationaryArchitectBase} and adds:
 * <ul>
 *   <li>Pivot buff — applies damage resistance to nearby parasites</li>
 *   <li>Parate buff — applies speed boost to nearby parasites</li>
 *   <li>Damage distribution — splits damage among rooterball defenders</li>
 *   <li>NexusProtection3 structure generation</li>
 * </ul>
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 100.0</li>
 *   <li>Attack Damage: 6.0</li>
 *   <li>Movement Speed: 0.00</li>
 *   <li>Armor: 10.0</li>
 *   <li>Follow Range: 32.0</li>
 *   <li>Knockback Resistance: 0.9</li>
 * </ul>
 */
public abstract class EntityRooterBase extends EntityStationaryArchitectBase {

    // ========== Buff System ==========

    /** Range of the buff aura in blocks. */
    protected float buffRange = 16.0F;

    /** Ticks between buff applications. */
    protected int buffInterval = 80;

    /** Internal tick counter for buff applications. */
    protected int buffTickCounter = 0;

    // ========== Damage Distribution ==========

    /** Whether damage is distributed among rooterball defenders. */
    protected boolean distributeDamage = true;

    /** Maximum fraction of damage redirected to each defender. */
    protected float damageRedirectFraction = 0.25F;

    // ========== Constructor ==========

    protected EntityRooterBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);

        // Rooter-tier gene activations
        geneBooleans[GeneType.DAMAGE_CAP.getIndex()] = true;
        geneBooleans[GeneType.MIN_DAMAGE.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.7F);
        geneFloats[GeneType.ANTI_KNOCKBACK.getIndex()] = Math.max(
                geneFloats[GeneType.ANTI_KNOCKBACK.getIndex()], 0.8F);
        geneFloats[GeneType.MOB_HEALING.getIndex()] = Math.max(
                geneFloats[GeneType.MOB_HEALING.getIndex()], 0.6F);

        // Rooter-tier abilities — colony building and support
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.SUMMON, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.BLOCK_PLACE, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.BLOCK_BREAK, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.HEAL_AURA, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.INFECT_AURA, true);
    }

    // ========== AI Goals ==========

    /**
     * Registers goals for rooter-tier parasites.
     * Adds block infestation, buff aura, and summon goals on top of
     * stationary architect base goals. Rooters buff nearby parasites
     * and distribute damage among defenders.
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteSummonGoal(this, 220, 16, 8));
        this.goalSelector.addGoal(2, new ParasiteBlockInfestGoal(this, 4));
        this.goalSelector.addGoal(3, new ParasiteSpecialSkillGoal(this));
        this.goalSelector.addGoal(5, new ColonyDefenseGoal(this));
        this.goalSelector.addGoal(6, new ParasiteGiveEffectsGoal(this, 60, 16, new String[]{"COTH", "VIRULENCE"}));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    // ========== Buff Application ==========

    /**
     * Applies Pivot and Parate buffs to nearby parasite entities.
     * Pivot = damage resistance, Parate = movement speed.
     */
    protected void applyBuffs() {
        if (this.level().isClientSide) return;

        AABB area = this.getBoundingBox().inflate(buffRange);
        List<EntityParasiteBase> nearby = this.level().getEntitiesOfClass(
                EntityParasiteBase.class, area, p -> p.isAlive() && p != this);

        for (EntityParasiteBase parasite : nearby) {
            // Pivot buff: damage resistance II for 4 seconds
            parasite.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 1));
            // Parate buff: speed I for 4 seconds
            parasite.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 0));
        }
    }

    // ========== Damage Distribution ==========

    /**
     * Distributes incoming damage among nearby rooterball defenders.
     * Each defender absorbs a fraction of the damage.
     */
    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (distributeDamage && !this.level().isClientSide) {
            // Find nearby rooterball-type defenders
            AABB area = this.getBoundingBox().inflate(8.0);
            List<EntityParasiteBase> defenders = this.level().getEntitiesOfClass(
                    EntityParasiteBase.class, area, p -> p.isAlive() && p != this);

            if (!defenders.isEmpty()) {
                float redirectAmount = amount * damageRedirectFraction;
                float perDefender = redirectAmount / defenders.size();
                for (EntityParasiteBase defender : defenders) {
                    defender.hurt(source, perDefender);
                }
                amount -= redirectAmount;
            }
        }

        boolean result = super.hurt(source, amount);

        // Escalate colony alert when the rooter is attacked
        if (result && source.getEntity() instanceof LivingEntity attacker) {
            ColonyComponent colony = getColonyComponent();
            if (colony != null && colony.isColonyLeader()) {
                colony.onColonyAttacked(attacker);
            }
        }

        return result;
    }

    // ========== Structure Generation ==========

    /**
     * Generates a NexusProtection3 structure around this rooter.
     * Override in concrete subclasses for stage-specific structures.
     *
     * @param pos the center position
     */
    protected void generateNexusProtection3(BlockPos pos) {
        generateStructure(pos);
    }

    // ========== Colony Block Building & Maintenance ==========

    /**
     * Processes the colony's build queue, placing blocks incrementally.
     * Rooters are the primary builders of colony structures.
     * They process the build queue faster than beckons (every 30 ticks vs 40).
     */
    protected void tickColonyBuilding() {
        if (!ModConfigSystems.isColonyStructuresEnabled()) return;
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        ColonyComponent colony = getColonyComponent();
        if (colony == null || !colony.isColonyLeader()) return;

        // Rooters build faster than beckons
        if (this.srpTicks % 30 == 0 && !colony.getBuildQueue().isEmpty()) {
            int placed = ColonyStructureGenerator.processBuildQueue(serverLevel, colony.getBuildQueue());

            // Track placed blocks
            if (placed > 0 && ModConfigSystems.shouldLogColony() && this.srpTicks % 300 == 0) {
                SubspaceParasite.LOGGER.debug("Rooter colony at {} building: {} blocks remaining",
                        colony.getColonyCenter(), colony.getBuildQueue().size());
            }
        }
    }

    /**
     * Maintains colony blocks by scanning for destroyed blocks and
     * queuing them for regeneration. Rooters are the primary
     * maintainers of colony structure integrity.
     */
    protected void tickColonyMaintenance() {
        if (!ModConfigSystems.isColonyStructuresEnabled()) return;
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        ColonyComponent colony = getColonyComponent();
        if (colony == null || !colony.isColonyLeader()) return;

        // Process regen queue (faster than build queue — every 30 ticks)
        if (this.srpTicks % 30 == 0 && !colony.getRegenQueue().isEmpty()) {
            ColonyStructureGenerator.processBuildQueue(serverLevel, colony.getRegenQueue());
        }

        // Scan for missing blocks every 10 seconds
        if (this.srpTicks % 200 == 0 && !colony.getTrackedBlocks().isEmpty()) {
            int missing = ColonyStructureGenerator.scanForMissingBlocks(
                    serverLevel, colony.getTrackedBlocks(), colony.getRegenQueue(), 30);

            if (missing > 0 && ModConfigSystems.shouldLogColony()) {
                SubspaceParasite.LOGGER.debug("Rooter colony at {} found {} missing blocks, queued for regen",
                        colony.getColonyCenter(), missing);
            }
        }
    }

    /**
     * Extends colony infrastructure by infesting nearby vanilla blocks
     * and converting them to parasite equivalents. Rooters expand
     * the colony's footprint using the block infestation system.
     */
    protected void tickColonyExpansion() {
        ColonyComponent colony = getColonyComponent();
        if (colony == null || !colony.isColonyLeader() || colony.getColonyCenter() == null) return;
        if (this.srpTicks % 100 != 0) return;

        BlockPos center = colony.getColonyCenter();
        int radius = colony.getColonyRadius();
        Set<Block> colonyBlockTypes = ColonyStructureGenerator.getColonyBlockTypes();

        // Try to infest a random block near the colony perimeter
        for (int attempt = 0; attempt < 3; attempt++) {
            int dx = this.random.nextInt(radius * 2) - radius;
            int dz = this.random.nextInt(radius * 2) - radius;
            BlockPos candidate = center.offset(dx, -1, dz);

            BlockState current = this.level().getBlockState(candidate);
            if (!current.isAir() && !colonyBlockTypes.contains(current.getBlock())) {
                // Found a non-parasite block near the colony — infest it
                Block infested = com.subspaceparasite.common.block.BlockPurifyMappings.getInfestedBlock(current.getBlock());
                if (infested != null && this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.setBlock(candidate, infested.defaultBlockState(), 3);
                    colony.trackBlock(candidate);
                }
            }
        }
    }

    /**
     * Scans nearby blocks and tracks parasite blocks as colony structure.
     * Rooters are more thorough scanners than beckons.
     */
    protected void scanAndTrackColonyBlocks() {
        ColonyComponent colony = getColonyComponent();
        if (colony == null || colony.getColonyCenter() == null) return;

        // Scan every 5 seconds
        if (this.srpTicks % 100 != 0) return;

        BlockPos center = colony.getColonyCenter();
        int radius = colony.getColonyRadius();
        Set<Block> colonyBlockTypes = ColonyStructureGenerator.getColonyBlockTypes();

        // Rooters scan more positions than beckons
        int scansPerTick = 20;
        for (int i = 0; i < scansPerTick; i++) {
            int dx = this.random.nextInt(radius * 2) - radius;
            int dy = this.random.nextInt(8) - 2;
            int dz = this.random.nextInt(radius * 2) - radius;
            BlockPos checkPos = center.offset(dx, dy, dz);

            BlockState state = this.level().getBlockState(checkPos);
            if (colonyBlockTypes.contains(state.getBlock())) {
                colony.trackBlock(checkPos);
            }
        }
    }

    // ========== Tick Override ==========

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            buffTickCounter++;
            if (buffTickCounter >= buffInterval) {
                buffTickCounter = 0;
                applyBuffs();
            }

            // Colony building, maintenance, and expansion
            ColonyComponent colony = getColonyComponent();
            if (colony != null && colony.isColonyLeader()) {
                tickColonyBuilding();
                tickColonyMaintenance();
                tickColonyExpansion();
                scanAndTrackColonyBlocks();
            }
        }
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for rooter-tier parasites.
     * High armor and knockback resistance for a durable anchor.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.00)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }

    // ========== NBT Save/Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("BuffRange", buffRange);
        tag.putInt("BuffInterval", buffInterval);
        tag.putInt("BuffTickCounter", buffTickCounter);
        tag.putBoolean("DistributeDamage", distributeDamage);
        tag.putFloat("DamageRedirectFraction", damageRedirectFraction);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        buffRange = tag.contains("BuffRange") ? tag.getFloat("BuffRange") : 16.0F;
        buffInterval = tag.contains("BuffInterval") ? tag.getInt("BuffInterval") : 80;
        buffTickCounter = tag.getInt("BuffTickCounter");
        distributeDamage = tag.contains("DistributeDamage") ? tag.getBoolean("DistributeDamage") : true;
        damageRedirectFraction = tag.contains("DamageRedirectFraction") ? tag.getFloat("DamageRedirectFraction") : 0.25F;
    }
}
