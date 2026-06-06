package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import java.util.LinkedList;
import java.util.List;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ColonyDefenseGoal;
import com.subspaceparasite.common.entity.ai.ParasiteGiveEffectsGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSpecialSkillGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSummonGoal;
import com.subspaceparasite.common.world.ColonyStructureGenerator;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Abstract base class for Beckon-tier parasites.
 * <p>
 * Beckons are colony nexus structures that lure and convert nearby entities.
 * They possess a life timer and generate NexusProtection2 structures.
 * When damaged, they alert nearby parasites to converge on the attacker.
 * <p>
 * Extends {@link EntityStationaryArchitectBase} and adds:
 * <ul>
 *   <li>Life timer — beckons expire after a set duration</li>
 *   <li>NexusProtection2 structure generation</li>
 *   <li>Beckon alert system — attracts nearby parasites to threats</li>
 * </ul>
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 120.0</li>
 *   <li>Attack Damage: 8.0</li>
 *   <li>Movement Speed: 0.00</li>
 *   <li>Armor: 8.0</li>
 *   <li>Follow Range: 48.0</li>
 *   <li>Knockback Resistance: 0.9</li>
 * </ul>
 */
public abstract class EntityBeckonBase extends EntityStationaryArchitectBase {

    // ========== Life Timer ==========

    /** Remaining life ticks before this beckon expires. -1 = infinite. */
    protected int lifeLeftBeckon = -1;

    /** Default life duration in ticks (48000 = 40 minutes). */
    protected static final int DEFAULT_BECKON_LIFE = 48000;

    /** Alert range for beckon system (blocks). */
    protected float beckonAlertRange = 24.0F;

    // ========== Constructor ==========

    protected EntityBeckonBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.lifeLeftBeckon = DEFAULT_BECKON_LIFE;

        // Beckon-tier gene activations
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.8F);
        geneFloats[GeneType.MOB_HEALING.getIndex()] = Math.max(
                geneFloats[GeneType.MOB_HEALING.getIndex()], 0.5F);

        // Beckon-tier abilities — summon, block placement, healing aura, infection aura
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.SUMMON, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.BLOCK_PLACE, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.HEAL_AURA, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.INFECT_AURA, true);
    }

    // ========== AI Goals ==========

    /**
     * Registers goals for beckon-tier parasites.
     * Adds summon, COTH aura, and look at player on top of
     * stationary architect base goals. Beckons lure and convert nearby
     * entities while defending their position with summoned parasites.
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteSummonGoal(this, 160, 24, 8));
        this.goalSelector.addGoal(3, new ParasiteSpecialSkillGoal(this));
        this.goalSelector.addGoal(5, new ColonyDefenseGoal(this));
        this.goalSelector.addGoal(6, new ParasiteGiveEffectsGoal(this, 40, 24, new String[]{"COTH", "VIRULENCE"}));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    // ========== Beckon Alert System ==========

    /**
     * Alerts nearby parasites to the presence of a threat.
     * Called when this beckon or a nearby parasite is damaged.
     *
     * @param threat the threatening entity
     */
    protected void beckonAlert(LivingEntity threat) {
        if (this.level().isClientSide) return;

        AABB area = this.getBoundingBox().inflate(beckonAlertRange);
        List<EntityParasiteBase> nearby = this.level().getEntitiesOfClass(
                EntityParasiteBase.class, area, p -> p.isAlive() && p != this);

        for (EntityParasiteBase parasite : nearby) {
            parasite.setTarget(threat);
        }
    }

    // ========== Colony Structure Generation ==========

    /**
     * Generates a NexusProtection2 structure around this beckon.
     * Override in concrete subclasses for stage-specific structures.
     *
     * @param pos the center position
     */
    protected void generateNexusProtection2(BlockPos pos) {
        // Default: delegate to generateStructure
        generateStructure(pos);
    }

    /**
     * Called when this beckon first becomes a colony leader.
     * Initializes the colony structure build queue at the beckon's position.
     * The structure builds incrementally over time via {@link #tickColonyConstruction()}.
     */
    protected void initializeColonyStructure() {
        if (!ModConfigSystems.isColonyStructuresEnabled()) return;
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        ColonyComponent colony = getColonyComponent();
        if (colony == null || colony.getColonyCenter() == null) return;

        // Generate initial build queue for the colony
        BlockPos center = colony.getColonyCenter();
        int colonyLevel = colony.getColonyLevel();

        LinkedList<ColonyStructureGenerator.BuildEntry> initialQueue =
                ColonyStructureGenerator.createInitialBuildQueue(serverLevel, center, colonyLevel);

        colony.getBuildQueue().addAll(initialQueue);

        if (ModConfigSystems.shouldLogColony()) {
            SubspaceParasite.LOGGER.debug("Beckon at {} initialized colony structure (level {}, {} blocks queued)",
                    center, colonyLevel, initialQueue.size());
        }
    }

    /**
     * Processes colony structure construction incrementally.
     * Called each tick from {@link #tick()}.
     * Places a few blocks per tick from the colony's build queue.
     */
    protected void tickColonyConstruction() {
        if (!ModConfigSystems.isColonyStructuresEnabled()) return;
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        ColonyComponent colony = getColonyComponent();
        if (colony == null || !colony.isColonyLeader()) return;

        // Process the build queue every BUILD_TICK_INTERVAL ticks
        if (this.srpTicks % 40 == 0 && !colony.getBuildQueue().isEmpty()) {
            int placed = ColonyStructureGenerator.processBuildQueue(serverLevel, colony.getBuildQueue());

            // Track placed blocks in the colony
            if (placed > 0) {
                if (ModConfigSystems.shouldLogColony() && this.srpTicks % 400 == 0) {
                    SubspaceParasite.LOGGER.debug("Beckon colony at {} building: {} blocks remaining in queue",
                            colony.getColonyCenter(), colony.getBuildQueue().size());
                }
            }
        }

        // Process regen queue
        if (this.srpTicks % 60 == 0 && !colony.getRegenQueue().isEmpty()) {
            ColonyStructureGenerator.processBuildQueue(serverLevel, colony.getRegenQueue());
        }
    }

    /**
     * Directs colony construction by prioritizing certain build categories.
     * Beckons prioritize: Foundation > Core > Tendrils > Pods > Walls > Decoration.
     * This ensures the colony's essential infrastructure is built first.
     */
    protected void directColonyConstruction() {
        ColonyComponent colony = getColonyComponent();
        if (colony == null) return;

        LinkedList<ColonyStructureGenerator.BuildEntry> queue = colony.getBuildQueue();

        // Re-sort the queue by category priority if it gets disordered
        // (e.g., after a merge added entries out of order)
        if (this.srpTicks % 600 == 0 && queue.size() > 50) {
            queue.sort(java.util.Comparator
                    .comparingInt((ColonyStructureGenerator.BuildEntry e) -> e.category.sortOrder)
                    .thenComparingInt(e -> e.priority));
        }
    }

    /**
     * Scans nearby blocks and tracks parasite blocks as colony structure.
     * Called periodically to ensure the tracked blocks set stays current.
     */
    protected void scanAndTrackColonyBlocks() {
        ColonyComponent colony = getColonyComponent();
        if (colony == null || colony.getColonyCenter() == null) return;

        // Scan every 10 seconds
        if (this.srpTicks % 200 != 0) return;

        BlockPos center = colony.getColonyCenter();
        int radius = colony.getColonyRadius();
        java.util.Set<net.minecraft.world.level.block.Block> colonyBlockTypes =
                ColonyStructureGenerator.getColonyBlockTypes();

        // Scan a random subset of positions each tick (performance)
        int scansPerTick = 10;
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
            // Life timer
            if (lifeLeftBeckon > 0) {
                lifeLeftBeckon--;
                if (lifeLeftBeckon <= 0) {
                    onBeckonExpire();
                }
            }

            // Colony structure construction — beckons direct building
            ColonyComponent colony = getColonyComponent();
            if (colony != null && colony.isColonyLeader()) {
                tickColonyConstruction();
                directColonyConstruction();
                scanAndTrackColonyBlocks();
            }
        }
    }

    /**
     * Called when the beckon's life timer expires.
     * Default behavior: begin self-destruct sequence.
     */
    protected void onBeckonExpire() {
        this.setSelfDestructState(60);
    }

    // ========== Damage Override ==========

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);

        if (result && source.getEntity() instanceof LivingEntity attacker) {
            beckonAlert(attacker);

            // Escalate colony alert level when the beckon is attacked
            ColonyComponent colony = getColonyComponent();
            if (colony != null && colony.isColonyLeader()) {
                colony.onColonyAttacked(attacker);
            }
        }

        return result;
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for beckon-tier parasites.
     * High health and armor for a durable nexus structure.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 120.0)
                .add(Attributes.MOVEMENT_SPEED, 0.00)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }

    // ========== Accessors ==========

    /** Returns the remaining life ticks. -1 = infinite. */
    public int getLifeLeftBeckon() { return lifeLeftBeckon; }

    /** Sets the remaining life ticks. */
    public void setLifeLeftBeckon(int ticks) { this.lifeLeftBeckon = ticks; }

    /** Returns the beckon alert range in blocks. */
    public float getBeckonAlertRange() { return beckonAlertRange; }

    // ========== NBT Save/Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("LifeLeftBeckon", lifeLeftBeckon);
        tag.putFloat("BeckonAlertRange", beckonAlertRange);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        lifeLeftBeckon = tag.getInt("LifeLeftBeckon");
        beckonAlertRange = tag.contains("BeckonAlertRange") ? tag.getFloat("BeckonAlertRange") : 24.0F;
    }
}
