package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.world.ColonyStructureGenerator;
import com.subspaceparasite.common.world.ModWorldData;
import com.subspaceparasite.common.world.SRPDifficultySetting;
import com.subspaceparasite.config.ModConfigSystems;
import com.subspaceparasite.core.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.*;

/**
 * Component handling colony-related logic for parasite entities.
 * <p>
 * Manages:
 * <ul>
 *   <li>Colony point tracking and resource management</li>
 *   <li>Spawn eligibility and unit caps based on colony level</li>
 *   <li>Structure generation via build queue system</li>
 *   <li>Structure tracking (what blocks belong to the colony)</li>
 *   <li>Defense coordination (alert level, threatened status)</li>
 *   <li>Building queue (what structures to build next)</li>
 *   <li>Colony level progression (1-5)</li>
 *   <li>Colony merge logic</li>
 * </ul>
 */
public class ColonyComponent {

    private final EntityParasiteBase parasite;

    // ========== Core Colony State ==========
    private double colonyPoints;
    private double colonyPointThreshold;
    private boolean isColonyMember;
    private boolean isColonyLeader;
    private BlockPos colonyCenter;
    private int colonyRadius;
    private int spawnEligibilityTimer;
    private int spawnCooldown;
    private int maxColonyUnits;
    private int mergeCheckTimer;

    // ========== Colony Level ==========
    /** Colony level (1-5). Determines structure size, unit cap, and available entity types. */
    private int colonyLevel;

    /** Point thresholds for each level-up: level 1->2, 2->3, 3->4, 4->5. */
    private static final double[] LEVEL_THRESHOLDS = {200.0, 600.0, 1500.0, 4000.0};

    /** Unit cap per colony level: level 1=8, 2=12, 3=18, 4=24, 5=32. */
    private static final int[] UNIT_CAPS_PER_LEVEL = {8, 12, 18, 24, 32};

    /** Radius per colony level. */
    private static final int[] RADIUS_PER_LEVEL = {16, 24, 32, 48, 64};

    // ========== Structure Tracking ==========
    /** Set of block positions that belong to this colony's structure. */
    private final Set<BlockPos> trackedBlocks = new HashSet<>();

    // ========== Defense Coordination ==========
    /** Current alert level (0=calm, 1=aware, 2=alert, 3=threatened, 4=critical, 5=siege). */
    private int alertLevel;

    /** Whether the colony is currently under active attack. */
    private boolean isThreatened;

    /** Ticks since last threat was detected. Used for alert decay. */
    private int ticksSinceLastThreat;

    /** Maximum alert level. */
    private static final int MAX_ALERT_LEVEL = 5;

    /** Ticks before alert level decays by 1. */
    private static final int ALERT_DECAY_INTERVAL = 300;

    // ========== Building Queue ==========
    /** Queue of blocks to be built for the colony structure. */
    private final LinkedList<ColonyStructureGenerator.BuildEntry> buildQueue = new LinkedList<>();

    /** Queue of blocks to be regenerated (repaired). */
    private final LinkedList<ColonyStructureGenerator.BuildEntry> regenQueue = new LinkedList<>();

    /** Ticks between build processing. */
    private int buildTickTimer;

    /** Whether the initial structure has been generated. */
    private boolean hasInitialStructure;

    // ========== Constants ==========
    private static final int MERGE_CHECK_INTERVAL = 6000;
    private static final double BASE_COLONY_THRESHOLD = 500.0;
    private static final int SPAWN_COOLDOWN_TICKS = 2400;
    private static final int BUILD_TICK_INTERVAL = 40;
    private static final int BLOCK_REGEN_SCAN_INTERVAL = 200;

    public ColonyComponent(EntityParasiteBase parasite) {
        this.parasite = parasite;
        this.colonyPoints = 0.0;
        this.colonyPointThreshold = BASE_COLONY_THRESHOLD;
        this.isColonyMember = false;
        this.isColonyLeader = false;
        this.colonyCenter = null;
        this.colonyRadius = RADIUS_PER_LEVEL[0];
        this.spawnEligibilityTimer = 0;
        this.spawnCooldown = 0;
        this.maxColonyUnits = UNIT_CAPS_PER_LEVEL[0];
        this.mergeCheckTimer = 0;
        this.colonyLevel = 1;
        this.alertLevel = 0;
        this.isThreatened = false;
        this.ticksSinceLastThreat = 0;
        this.buildTickTimer = 0;
        this.hasInitialStructure = false;
    }

    // ========== Tick ==========

    public void tick() {
        if (parasite.level().isClientSide) return;
        if (!ModConfigSystems.isColonySystemEnabled()) return;

        // Accumulate colony points
        if (parasite.srpTicks % 200 == 0) addColonyPoints(ModConfigSystems.getColonyPointRate());

        // Decrement cooldowns
        if (spawnCooldown > 0) spawnCooldown--;
        if (spawnEligibilityTimer > 0) spawnEligibilityTimer--;

        // Colony leader logic
        if (isColonyLeader && colonyCenter != null) tickColonyLeader();

        // Merge check
        mergeCheckTimer++;
        if (mergeCheckTimer >= MERGE_CHECK_INTERVAL) {
            mergeCheckTimer = 0;
            checkColonyMerge();
        }

        // Check for new colony formation
        if (colonyPoints >= colonyPointThreshold && spawnCooldown <= 0) {
            checkColonySpawn();
        }
    }

    // ========== Colony Leader Tick ==========

    protected void tickColonyLeader() {
        if (colonyCenter == null) return;

        int currentUnits = countColonyUnits();

        // Leader returns to colony center if too far
        double distToCenter = parasite.blockPosition().distManhattan(colonyCenter);
        if (distToCenter > colonyRadius * 2) {
            parasite.getNavigation().moveTo(
                    colonyCenter.getX() + 0.5, colonyCenter.getY(), colonyCenter.getZ() + 0.5, 1.0);
        }

        // Update world data with colony status
        if (parasite.level() instanceof ServerLevel serverLevel) {
            ModWorldData worldData = ModWorldData.get(serverLevel);
            worldData.updateColonyData(colonyCenter, currentUnits, true);
        }

        // ── Structure Generation ──
        if (ModConfigSystems.isColonyStructuresEnabled()) {
            tickStructureGeneration();
        }

        // ── Defense Coordination ──
        tickDefenseCoordination();

        // ── Colony Level Progression ──
        tickColonyLevelProgression();

        // Attempt to spawn colony units if eligible
        if (isEligibleToSpawn() && parasite.srpTicks % 100 == 0) {
            attemptSpawnColonyUnit();
        }
    }

    // ========== Structure Generation ==========

    /**
     * Processes the build queue incrementally, placing a few blocks per tick.
     */
    protected void tickStructureGeneration() {
        if (!(parasite.level() instanceof ServerLevel serverLevel)) return;

        // Generate initial structure if not yet built
        if (!hasInitialStructure && colonyCenter != null) {
            LinkedList<ColonyStructureGenerator.BuildEntry> initialQueue =
                    ColonyStructureGenerator.createInitialBuildQueue(serverLevel, colonyCenter, colonyLevel);
            buildQueue.addAll(initialQueue);
            hasInitialStructure = true;

            if (ModConfigSystems.shouldLogColony()) {
                SubspaceParasite.LOGGER.debug("Colony at {} generating initial structure (level {}, {} blocks queued)",
                        colonyCenter, colonyLevel, initialQueue.size());
            }
        }

        // Process build queue
        buildTickTimer++;
        if (buildTickTimer >= BUILD_TICK_INTERVAL) {
            buildTickTimer = 0;

            if (!buildQueue.isEmpty()) {
                int placed = ColonyStructureGenerator.processBuildQueue(serverLevel, buildQueue);

                // Track placed blocks
                for (int i = 0; i < placed; i++) {
                    // The build queue removes entries as they're placed;
                    // we track them by scanning from the structure blueprint
                }
            }

            // Process regen queue
            if (!regenQueue.isEmpty()) {
                ColonyStructureGenerator.processBuildQueue(serverLevel, regenQueue);
            }
        }
    }

    // ========== Defense Coordination ==========

    /**
     * Manages alert level decay and threatened status.
     */
    protected void tickDefenseCoordination() {
        // Decay alert level over time when no threats
        ticksSinceLastThreat++;
        if (ticksSinceLastThreat >= ALERT_DECAY_INTERVAL && alertLevel > 0) {
            alertLevel--;
            ticksSinceLastThreat = 0;
        }

        // Clear threatened status when alert drops to 0
        if (alertLevel == 0) {
            isThreatened = false;
        }

        // Periodic block regeneration scan (for colony leaders)
        if (parasite.srpTicks % BLOCK_REGEN_SCAN_INTERVAL == 0 && !trackedBlocks.isEmpty()) {
            if (parasite.level() instanceof ServerLevel serverLevel) {
                ColonyStructureGenerator.scanForMissingBlocks(
                        serverLevel, trackedBlocks, regenQueue, 20);
            }
        }
    }

    // ========== Colony Level Progression ==========

    /**
     * Checks if the colony should advance to the next level.
     * Level-up requires sufficient colony points and current units.
     */
    protected void tickColonyLevelProgression() {
        if (colonyLevel >= 5) return;
        if (colonyPoints < LEVEL_THRESHOLDS[colonyLevel - 1]) return;

        int currentUnits = countColonyUnits();
        int requiredUnits = UNIT_CAPS_PER_LEVEL[colonyLevel - 1] / 2;
        if (currentUnits < requiredUnits) return;

        // Level up!
        int oldLevel = colonyLevel;
        colonyLevel++;
        colonyPoints -= LEVEL_THRESHOLDS[oldLevel - 1];

        // Update colony parameters
        this.maxColonyUnits = UNIT_CAPS_PER_LEVEL[colonyLevel - 1];
        this.colonyRadius = RADIUS_PER_LEVEL[colonyLevel - 1];

        // Queue new structure additions for the level-up
        if (ModConfigSystems.isColonyStructuresEnabled() && colonyCenter != null) {
            List<ColonyStructureGenerator.BuildEntry> additions =
                    ColonyStructureGenerator.generateLevelUpAdditions(colonyCenter, colonyLevel, oldLevel);
            buildQueue.addAll(additions);

            if (ModConfigSystems.shouldLogColony()) {
                SubspaceParasite.LOGGER.debug("Colony at {} advanced to level {} ({} new blocks queued)",
                        colonyCenter, colonyLevel, additions.size());
            }
        }

        // Update world data
        if (parasite.level() instanceof ServerLevel serverLevel) {
            ModWorldData worldData = ModWorldData.get(serverLevel);
            worldData.updateColonyData(colonyCenter, countColonyUnits(), true);
        }
    }

    // ========== Colony Points ==========

    public void addColonyPoints(double points) {
        float phaseMult = 1.0F + parasite.getPhaseCreated().getPhaseNumber() * 0.3F;
        // Apply difficulty multiplier from SRPDifficultySetting
        float difficultyMult = 1.0F;
        if (parasite.level() instanceof ServerLevel serverLevel) {
            SRPDifficultySetting difficulty = ModWorldData.get(serverLevel).getSRPDifficulty();
            difficultyMult = difficulty.getColonyGrowthRateMultiplier();
        }
        this.colonyPoints += points * phaseMult * difficultyMult;
    }

    /**
     * Spends colony points if sufficient are available.
     *
     * @param cost the point cost
     * @return true if points were successfully spent
     */
    public boolean spendColonyPoints(double cost) {
        if (colonyPoints < cost) return false;
        colonyPoints -= cost;
        return true;
    }

    public void onKill() {
        addColonyPoints(ModConfigSystems.getKillColonyPoints());
    }

    /**
     * Called when a colony member makes a kill.
     * Contributes points to the colony leader's pool.
     */
    public void onMemberKill(LivingEntity victim) {
        if (!isColonyLeader) return;

        double basePoints = ModConfigSystems.getKillColonyPoints() * 0.5;
        float phaseMult = 1.0F + parasite.getPhaseCreated().getPhaseNumber() * 0.2F;

        // Bonus for valuable targets
        if (victim instanceof net.minecraft.world.entity.player.Player) {
            basePoints *= 3.0;
        } else if (victim.getMaxHealth() > 20.0F) {
            basePoints *= 1.5;
        }

        addColonyPoints(basePoints * phaseMult);
    }

    // ========== Alert System ==========

    /**
     * Increments the alert level (capped at MAX_ALERT_LEVEL).
     * Resets the threat decay timer.
     */
    public void incrementAlertLevel() {
        if (alertLevel < MAX_ALERT_LEVEL) {
            alertLevel++;
        }
        isThreatened = true;
        ticksSinceLastThreat = 0;
    }

    /**
     * Decrements the alert level (floored at 0).
     */
    public void decrementAlertLevel() {
        if (alertLevel > 0) {
            alertLevel--;
        }
    }

    /**
     * Called when a colony member takes damage from a non-parasite entity.
     * Escalates alert level and marks the colony as threatened.
     *
     * @param attacker the attacking entity
     */
    public void onColonyAttacked(LivingEntity attacker) {
        incrementAlertLevel();

        // Alert all colony members
        if (colonyCenter != null) {
            AABB territory = new AABB(
                    colonyCenter.getX() - colonyRadius, colonyCenter.getY() - 16, colonyCenter.getZ() - colonyRadius,
                    colonyCenter.getX() + colonyRadius, colonyCenter.getY() + 16, colonyCenter.getZ() + colonyRadius);

            List<EntityParasiteBase> members = parasite.level().getEntitiesOfClass(
                    EntityParasiteBase.class, territory,
                    p -> p.isAlive() && p.getColonyComponent() != null &&
                         p.getColonyComponent().isColonyMember() && p != parasite);

            for (EntityParasiteBase member : members) {
                if (member.getTarget() == null) {
                    member.setTarget(attacker);
                }
            }
        }
    }

    // ========== Structure Tracking ==========

    /**
     * Adds a block position to the tracked colony blocks.
     */
    public void trackBlock(BlockPos pos) {
        trackedBlocks.add(pos.immutable());
    }

    /**
     * Removes a block position from tracked colony blocks.
     */
    public void untrackBlock(BlockPos pos) {
        trackedBlocks.remove(pos);
    }

    /**
     * Returns the set of tracked colony block positions.
     */
    public Set<BlockPos> getTrackedBlocks() {
        return trackedBlocks;
    }

    /**
     * Returns the build queue for this colony.
     */
    public LinkedList<ColonyStructureGenerator.BuildEntry> getBuildQueue() {
        return buildQueue;
    }

    /**
     * Returns the regeneration queue for this colony.
     */
    public LinkedList<ColonyStructureGenerator.BuildEntry> getRegenQueue() {
        return regenQueue;
    }

    // ========== Unit Spawning ==========

    /**
     * Attempts to spawn a new colony unit near the colony center.
     */
    protected void attemptSpawnColonyUnit() {
        if (!isEligibleToSpawn() || colonyCenter == null) return;
        if (!(parasite.level() instanceof ServerLevel serverLevel)) return;

        BlockPos spawnPos = findUnitSpawnPos();
        if (spawnPos == null) return;

        EntityParasiteBase unit = createColonyUnit(serverLevel);
        if (unit != null) {
            unit.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                       parasite.getYRot(), 0);
            unit.setColonySpawned(true);
            unit.setOwner(parasite);

            // Inherit phase from leader
            unit.setPhaseCreated(parasite.getPhaseCreated());

            // Set as colony member
            ColonyComponent unitColony = unit.getColonyComponent();
            if (unitColony != null) {
                unitColony.setColonyMember(true);
                unitColony.setColonyCenter(colonyCenter);
            }

            // Deterrent units get patrol anchors
            if (unit instanceof EntityDeterrentBase deterrent) {
                deterrent.setPatrolAnchor(colonyCenter);
            }

            serverLevel.addFreshEntity(unit);

            // Reset spawn cooldown
            spawnCooldown = SPAWN_COOLDOWN_TICKS;

            // Deduct points for spawning
            colonyPoints -= colonyPointThreshold * 0.3;
        }
    }

    /**
     * Finds a valid spawn position for a colony unit.
     */
    public BlockPos findUnitSpawnPos() {
        if (colonyCenter == null) return null;

        for (int attempt = 0; attempt < 8; attempt++) {
            int dx = parasite.getRandom().nextInt(colonyRadius * 2) - colonyRadius;
            int dz = parasite.getRandom().nextInt(colonyRadius * 2) - colonyRadius;
            BlockPos candidate = colonyCenter.offset(dx, 0, dz);
            candidate = parasite.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, candidate);

            // Check if position is valid for spawning
            if (parasite.level().getBlockState(candidate.below()).isSolidRender(
                    parasite.level(), candidate.below()) &&
                parasite.level().getBlockState(candidate).isAir() &&
                parasite.level().getBlockState(candidate.above()).isAir()) {

                // Check distance from other parasites to avoid overcrowding
                AABB checkArea = new AABB(
                    candidate.offset(-2, -1, -2),
                    candidate.offset(2, 2, 2)
                );
                List<EntityParasiteBase> nearby = parasite.level().getEntitiesOfClass(
                    EntityParasiteBase.class, checkArea, Entity::isAlive);

                if (nearby.size() < 3) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Creates a colony unit entity based on the leader's phase and colony level.
     * Higher colony levels can spawn more advanced unit types.
     */
    public EntityParasiteBase createColonyUnit(ServerLevel level) {
        EvoPhase phase = parasite.getPhaseCreated();

        // Colony level determines what unit types can be spawned:
        // Level 1-2: Crude (MovingFlesh, Worker)
        // Level 3: + Primitive (Hull, Iki)
        // Level 4: + Adapted types
        // Level 5: All types including deterrents

        EntityType<? extends EntityParasiteBase> unitType = switch (colonyLevel) {
            case 1 -> ModEntities.CRUDE_MOVING_FLESH.isPresent() ?
                    ModEntities.CRUDE_MOVING_FLESH.get() : null;
            case 2 -> parasite.getRandom().nextBoolean() ?
                    (ModEntities.CRUDE_WORKER.isPresent() ? ModEntities.CRUDE_WORKER.get() : null) :
                    (ModEntities.CRUDE_MOVING_FLESH.isPresent() ? ModEntities.CRUDE_MOVING_FLESH.get() : null);
            case 3 -> ModEntities.PRIMITIVE_HULL.isPresent() ?
                    ModEntities.PRIMITIVE_HULL.get() : null;
            case 4 -> ModEntities.INBORN_ALAFIN.isPresent() ?
                    ModEntities.INBORN_ALAFIN.get() : null;
            default -> {
                // Level 5: can spawn deterrents
                if (parasite.getRandom().nextFloat() < 0.2F) {
                    yield ModEntities.DETERRENT_SENTRY.isPresent() ?
                            ModEntities.DETERRENT_SENTRY.get() : null;
                }
                yield ModEntities.ADAPTED_COLONY.isPresent() ?
                        ModEntities.ADAPTED_COLONY.get() : null;
            }
        };

        if (unitType == null) {
            return null;
        }

        return unitType.create(level);
    }

    public int countColonyUnits() {
        if (colonyCenter == null) return 0;
        AABB territory = new AABB(
                colonyCenter.getX() - colonyRadius, colonyCenter.getY() - 16, colonyCenter.getZ() - colonyRadius,
                colonyCenter.getX() + colonyRadius, colonyCenter.getY() + 16, colonyCenter.getZ() + colonyRadius);
        return parasite.level().getEntitiesOfClass(EntityParasiteBase.class, territory,
                p -> p != parasite && p.isColonySpawned() && p.isAlive()).size();
    }

    // ========== Colony Creation ==========

    protected void checkColonySpawn() {
        if (!ModConfigSystems.isColonySystemEnabled()) return;
        if (!(parasite.level() instanceof ServerLevel)) return;
        if (isColonyLeader) return;

        ServerLevel serverLevel = (ServerLevel) parasite.level();
        ModWorldData worldData = ModWorldData.get(serverLevel);
        if (worldData.getColonyCount() >= ModConfigSystems.getMaxColonies()) return;

        BlockPos spawnPos = findColonySpawnPos();
        if (spawnPos == null) return;

        colonyPoints -= colonyPointThreshold;
        createColony(spawnPos);
    }

    protected BlockPos findColonySpawnPos() {
        BlockPos basePos = parasite.blockPosition();
        for (int attempt = 0; attempt < 10; attempt++) {
            int dx = parasite.getRandom().nextInt(32) - 16;
            int dz = parasite.getRandom().nextInt(32) - 16;
            BlockPos candidate = basePos.offset(dx, 0, dz);
            candidate = parasite.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, candidate);

            if (parasite.level() instanceof ServerLevel serverLevel) {
                ModWorldData worldData = ModWorldData.get(serverLevel);
                boolean tooClose = false;
                for (BlockPos existingCenter : worldData.getColonyCenters()) {
                    if (existingCenter.distManhattan(candidate) < ModConfigSystems.getMinColonySpacing()) {
                        tooClose = true;
                        break;
                    }
                }
                if (!tooClose) return candidate;
            }
        }
        return null;
    }

    protected void createColony(BlockPos pos) {
        this.colonyCenter = pos;
        this.isColonyLeader = true;
        this.isColonyMember = true;
        this.colonyLevel = 1;
        this.colonyRadius = RADIUS_PER_LEVEL[0];
        this.maxColonyUnits = UNIT_CAPS_PER_LEVEL[0];
        this.spawnCooldown = SPAWN_COOLDOWN_TICKS;
        this.alertLevel = 0;
        this.isThreatened = false;
        this.hasInitialStructure = false;
        this.parasite.setCanDespawn(false);

        if (parasite.level() instanceof ServerLevel serverLevel) {
            ModWorldData worldData = ModWorldData.get(serverLevel);
            worldData.addColony(pos, parasite.getUUID());
        }

        if (ModConfigSystems.shouldLogColony()) {
            SubspaceParasite.LOGGER.debug("New colony created at {} by {}", pos, parasite.getName().getString());
        }
    }

    // ========== Spawn Eligibility ==========

    public boolean isEligibleToSpawn() {
        if (!isColonyLeader || spawnCooldown > 0 || colonyCenter == null) return false;
        return countColonyUnits() < maxColonyUnits;
    }

    // ========== Colony Merge ==========

    protected void checkColonyMerge() {
        if (!isColonyLeader || colonyCenter == null) return;
        if (!(parasite.level() instanceof ServerLevel)) return;

        AABB searchArea = new AABB(
                colonyCenter.getX() - 64, colonyCenter.getY() - 32, colonyCenter.getZ() - 64,
                colonyCenter.getX() + 64, colonyCenter.getY() + 32, colonyCenter.getZ() + 64);

        List<EntityParasiteBase> nearbyLeaders = parasite.level().getEntitiesOfClass(
                EntityParasiteBase.class, searchArea,
                p -> p != parasite && p.isAlive()
                        && p.getColonyComponent() != null
                        && p.getColonyComponent().isColonyLeader);

        for (EntityParasiteBase otherLeader : nearbyLeaders) {
            ColonyComponent otherColony = otherLeader.getColonyComponent();
            if (otherColony.colonyCenter == null) continue;
            double distance = colonyCenter.distManhattan(otherColony.colonyCenter);
            if (distance < ModConfigSystems.getMinColonySpacing() * 1.5) {
                mergeWith(otherColony);
                break;
            }
        }
    }

    protected void mergeWith(ColonyComponent other) {
        this.colonyPoints += other.colonyPoints;
        this.maxColonyUnits += other.maxColonyUnits / 2;
        this.colonyRadius = Math.min(64, this.colonyRadius + 8);
        this.colonyLevel = Math.max(this.colonyLevel, other.colonyLevel);

        // Merge tracked blocks
        this.trackedBlocks.addAll(other.trackedBlocks);

        // Merge build queues
        this.buildQueue.addAll(other.buildQueue);

        // Absorb the other colony's alert level
        this.alertLevel = Math.max(this.alertLevel, other.alertLevel);

        other.isColonyLeader = false;
        other.colonyCenter = null;
        other.colonyRadius = 0;
        other.trackedBlocks.clear();
        other.buildQueue.clear();
        other.regenQueue.clear();

        if (parasite.level() instanceof ServerLevel serverLevel) {
            ModWorldData worldData = ModWorldData.get(serverLevel);
            worldData.removeColony(other.parasite.getUUID());
        }

        if (ModConfigSystems.shouldLogColony()) {
            SubspaceParasite.LOGGER.debug("Colony at {} merged with colony at {}",
                    this.colonyCenter, other.parasite.blockPosition());
        }
    }

    // ========== Death Handling ==========

    public void onDeath() {
        if (isColonyLeader && colonyCenter != null) {
            EntityParasiteBase successor = findSuccessor();
            if (successor != null) {
                transferLeadership(successor);
            } else {
                dissolveColony();
            }
        }
        if (isColonyMember) isColonyMember = false;
    }

    protected EntityParasiteBase findSuccessor() {
        if (colonyCenter == null) return null;
        AABB territory = new AABB(
                colonyCenter.getX() - colonyRadius, colonyCenter.getY() - 16, colonyCenter.getZ() - colonyRadius,
                colonyCenter.getX() + colonyRadius, colonyCenter.getY() + 16, colonyCenter.getZ() + colonyRadius);

        List<EntityParasiteBase> candidates = parasite.level().getEntitiesOfClass(
                EntityParasiteBase.class, territory,
                p -> p != parasite && p.isColonySpawned() && p.isAlive());

        if (!candidates.isEmpty()) {
            candidates.sort((a, b) -> {
                int ea = a.getEvolutionComponent() != null ? a.getEvolutionComponent().getEvolutionLevel() : 0;
                int eb = b.getEvolutionComponent() != null ? b.getEvolutionComponent().getEvolutionLevel() : 0;
                return eb - ea;
            });
            return candidates.get(0);
        }
        return null;
    }

    protected void transferLeadership(EntityParasiteBase successor) {
        ColonyComponent sc = successor.getColonyComponent();
        if (sc != null) {
            sc.isColonyLeader = true;
            sc.colonyCenter = this.colonyCenter;
            sc.colonyRadius = this.colonyRadius;
            sc.maxColonyUnits = this.maxColonyUnits;
            sc.colonyPoints = this.colonyPoints;
            sc.colonyLevel = this.colonyLevel;
            sc.alertLevel = this.alertLevel;
            sc.isThreatened = this.isThreatened;
            sc.hasInitialStructure = this.hasInitialStructure;
            sc.trackedBlocks.addAll(this.trackedBlocks);
            sc.buildQueue.addAll(this.buildQueue);
            sc.regenQueue.addAll(this.regenQueue);
            successor.setCanDespawn(false);
        }
    }

    protected void dissolveColony() {
        if (parasite.level() instanceof ServerLevel serverLevel) {
            ModWorldData worldData = ModWorldData.get(serverLevel);
            worldData.removeColony(parasite.getUUID());
        }
        trackedBlocks.clear();
        buildQueue.clear();
        regenQueue.clear();

        if (ModConfigSystems.shouldLogColony()) {
            SubspaceParasite.LOGGER.debug("Colony dissolved at {}", colonyCenter);
        }
    }

    // ========== Accessors ==========

    public double getColonyPoints() { return colonyPoints; }
    public boolean isColonyMember() { return isColonyMember; }
    public boolean isColonyLeader() { return isColonyLeader; }
    public BlockPos getColonyCenter() { return colonyCenter; }
    public int getColonyRadius() { return colonyRadius; }
    public int getMaxColonyUnits() { return maxColonyUnits; }
    public int getColonyLevel() { return colonyLevel; }
    public int getAlertLevel() { return alertLevel; }
    public boolean isThreatened() { return isThreatened; }
    public boolean hasInitialStructure() { return hasInitialStructure; }
    public int getBuildQueueSize() { return buildQueue.size(); }
    public int getRegenQueueSize() { return regenQueue.size(); }
    public int getTrackedBlockCount() { return trackedBlocks.size(); }

    public void setColonyMember(boolean value) { this.isColonyMember = value; }
    public void setColonyLeader(boolean value) { this.isColonyLeader = value; }
    public void setColonyCenter(BlockPos pos) { this.colonyCenter = pos; }
    public void setMaxColonyUnits(int value) { this.maxColonyUnits = value; }
    public void setColonyLevel(int level) { this.colonyLevel = Math.max(1, Math.min(5, level)); }
    public void setAlertLevel(int level) { this.alertLevel = Math.max(0, Math.min(MAX_ALERT_LEVEL, level)); }
    public void setThreatened(boolean value) { this.isThreatened = value; }

    // ========== NBT Save/Load ==========

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("ColonyPoints", colonyPoints);
        tag.putDouble("ColonyPointThreshold", colonyPointThreshold);
        tag.putBoolean("IsColonyMember", isColonyMember);
        tag.putBoolean("IsColonyLeader", isColonyLeader);
        if (colonyCenter != null) {
            tag.putInt("ColonyCenterX", colonyCenter.getX());
            tag.putInt("ColonyCenterY", colonyCenter.getY());
            tag.putInt("ColonyCenterZ", colonyCenter.getZ());
        }
        tag.putInt("ColonyRadius", colonyRadius);
        tag.putInt("SpawnEligibilityTimer", spawnEligibilityTimer);
        tag.putInt("SpawnCooldown", spawnCooldown);
        tag.putInt("MaxColonyUnits", maxColonyUnits);
        tag.putInt("MergeCheckTimer", mergeCheckTimer);

        // Colony Level
        tag.putInt("ColonyLevel", colonyLevel);

        // Defense Coordination
        tag.putInt("AlertLevel", alertLevel);
        tag.putBoolean("IsThreatened", isThreatened);
        tag.putInt("TicksSinceLastThreat", ticksSinceLastThreat);

        // Structure Tracking
        tag.putInt("TrackedBlockCount", trackedBlocks.size());
        int idx = 0;
        for (BlockPos pos : trackedBlocks) {
            tag.put("TrackedBlock" + idx, NbtUtils.writeBlockPos(pos));
            idx++;
        }

        // Build State
        tag.putBoolean("HasInitialStructure", hasInitialStructure);
        tag.putInt("BuildTickTimer", buildTickTimer);

        return tag;
    }

    public void load(CompoundTag tag) {
        colonyPoints = tag.getDouble("ColonyPoints");
        colonyPointThreshold = tag.getDouble("ColonyPointThreshold");
        isColonyMember = tag.getBoolean("IsColonyMember");
        isColonyLeader = tag.getBoolean("IsColonyLeader");
        if (tag.contains("ColonyCenterX")) {
            colonyCenter = new BlockPos(
                    tag.getInt("ColonyCenterX"), tag.getInt("ColonyCenterY"), tag.getInt("ColonyCenterZ"));
        }
        colonyRadius = tag.getInt("ColonyRadius");
        spawnEligibilityTimer = tag.getInt("SpawnEligibilityTimer");
        spawnCooldown = tag.getInt("SpawnCooldown");
        maxColonyUnits = tag.getInt("MaxColonyUnits");
        mergeCheckTimer = tag.getInt("MergeCheckTimer");

        // Colony Level
        colonyLevel = tag.contains("ColonyLevel") ? tag.getInt("ColonyLevel") : 1;

        // Defense Coordination
        alertLevel = tag.contains("AlertLevel") ? tag.getInt("AlertLevel") : 0;
        isThreatened = tag.contains("IsThreatened") && tag.getBoolean("IsThreatened");
        ticksSinceLastThreat = tag.contains("TicksSinceLastThreat") ? tag.getInt("TicksSinceLastThreat") : 0;

        // Structure Tracking
        trackedBlocks.clear();
        int trackedCount = tag.contains("TrackedBlockCount") ? tag.getInt("TrackedBlockCount") : 0;
        for (int i = 0; i < trackedCount; i++) {
            if (tag.contains("TrackedBlock" + i)) {
                trackedBlocks.add(NbtUtils.readBlockPos(tag.getCompound("TrackedBlock" + i)));
            }
        }

        // Build State
        hasInitialStructure = tag.contains("HasInitialStructure") && tag.getBoolean("HasInitialStructure");
        buildTickTimer = tag.contains("BuildTickTimer") ? tag.getInt("BuildTickTimer") : 0;
    }
}
