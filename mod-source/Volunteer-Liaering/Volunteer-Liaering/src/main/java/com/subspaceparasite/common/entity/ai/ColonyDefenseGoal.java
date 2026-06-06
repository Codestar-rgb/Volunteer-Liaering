package com.subspaceparasite.common.entity.ai;

import com.subspaceparasite.common.entity.base.ColonyComponent;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * AI Goal for colony defense behaviors.
 * <p>
 * Provides five defense mechanisms for colony members:
 * <ul>
 *   <li><b>Alert System:</b> When a non-parasite entity enters the colony radius,
 *       alert all colony members and set them as hostile toward the intruder.</li>
 *   <li><b>Patrol:</b> Deterrents and defensive entities patrol the colony perimeter,
 *       moving between waypoints around the colony boundary.</li>
 *   <li><b>Reinforcement:</b> When the colony is under attack, spawn additional
 *       defensive entities to assist (costs colony points).</li>
 *   <li><b>Retreat:</b> Damaged parasites retreat to the colony center for healing,
 *       receiving increased regeneration while inside the colony core.</li>
 *   <li><b>Block Regeneration:</b> Colony can regenerate damaged parasite blocks
 *       over time, restoring destroyed structure blocks.</li>
 * </ul>
 */
public class ColonyDefenseGoal extends Goal {

    private final EntityParasiteBase parasite;

    // ========== Alert System ==========
    /** Ticks between alert scans. */
    private static final int ALERT_SCAN_INTERVAL = 40;
    /** Range beyond colony radius to detect intruders. */
    private static final float ALERT_DETECT_BUFFER = 4.0F;

    // ========== Patrol System ==========
    /** Number of patrol waypoints around the perimeter. */
    private static final int PATROL_WAYPOINT_COUNT = 8;
    /** Range within which a waypoint is considered reached. */
    private static final double WAYPOINT_REACH_DIST = 3.0;
    /** Movement speed modifier when patrolling. */
    private static final double PATROL_SPEED = 1.0;

    // ========== Reinforcement System ==========
    /** Minimum alert level to trigger reinforcement spawns. */
    private static final int REINFORCEMENT_ALERT_THRESHOLD = 3;
    /** Colony point cost for spawning a reinforcement unit. */
    private static final double REINFORCEMENT_POINT_COST = 50.0;
    /** Cooldown between reinforcement spawns (ticks). */
    private static final int REINFORCEMENT_COOLDOWN = 600;

    // ========== Retreat System ==========
    /** Health fraction below which a parasite retreats. */
    private static final float RETREAT_HEALTH_THRESHOLD = 0.3F;
    /** Range from colony center considered "safe zone" for healing. */
    private static final double RETREAT_SAFE_ZONE = 5.0;
    /** Movement speed modifier when retreating. */
    private static final double RETREAT_SPEED = 1.4;

    // ========== Block Regeneration ==========
    /** Ticks between block regeneration scans. */
    private static final int REGEN_SCAN_INTERVAL = 200;
    /** Maximum blocks to regenerate per scan. */
    private static final int MAX_BLOCKS_PER_REGEN = 2;

    // ========== Internal State ==========
    private int alertScanTimer = 0;
    private int currentWaypointIndex = 0;
    private int reinforcementCooldown = 0;
    private int regenScanTimer = 0;
    private BlockPos[] patrolWaypoints = null;

    public ColonyDefenseGoal(EntityParasiteBase parasite) {
        this.parasite = parasite;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
    }

    // ========== Goal Overrides ==========

    @Override
    public boolean canUse() {
        if (parasite.level().isClientSide) return false;
        if (!ModConfigSystems.isColonySystemEnabled()) return false;

        ColonyComponent colony = parasite.getColonyComponent();
        if (colony == null) return false;
        if (!colony.isColonyMember()) return false;
        if (colony.getColonyCenter() == null) return false;

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        if (parasite.level().isClientSide) return;

        ColonyComponent colony = parasite.getColonyComponent();
        if (colony == null || colony.getColonyCenter() == null) return;

        alertScanTimer++;
        regenScanTimer++;
        if (reinforcementCooldown > 0) reinforcementCooldown--;

        // ── 1. Retreat (highest priority for damaged parasites) ──
        if (shouldRetreat()) {
            tickRetreat(colony);
            return; // Don't do anything else while retreating
        }

        // ── 2. Alert System ──
        if (alertScanTimer >= ALERT_SCAN_INTERVAL) {
            alertScanTimer = 0;
            tickAlertScan(colony);
        }

        // ── 3. Patrol (for deterrents and mobile defenders) ──
        if (isPatrolEntity()) {
            tickPatrol(colony);
        }

        // ── 4. Reinforcement (for colony leaders only) ──
        if (colony.isColonyLeader() && colony.getAlertLevel() >= REINFORCEMENT_ALERT_THRESHOLD) {
            tickReinforcement(colony);
        }

        // ── 5. Block Regeneration (for colony leaders only) ──
        if (colony.isColonyLeader() && regenScanTimer >= REGEN_SCAN_INTERVAL) {
            regenScanTimer = 0;
            tickBlockRegeneration(colony);
        }
    }

    // ========== Alert System ==========

    /**
     * Scans for non-parasite entities within the colony radius and
     * alerts all colony members to their presence.
     */
    protected void tickAlertScan(ColonyComponent colony) {
        BlockPos center = colony.getColonyCenter();
        float radius = colony.getColonyRadius() + ALERT_DETECT_BUFFER;

        AABB scanArea = new AABB(
                center.getX() - radius, center.getY() - 16, center.getZ() - radius,
                center.getX() + radius, center.getY() + 16, center.getZ() + radius);

        // Find non-parasite entities in colony territory
        List<LivingEntity> intruders = parasite.level().getEntitiesOfClass(
                LivingEntity.class, scanArea,
                e -> e.isAlive() && !(e instanceof EntityParasiteBase));

        if (!intruders.isEmpty()) {
            // Escalate alert level
            colony.incrementAlertLevel();
            LivingEntity primaryThreat = intruders.get(0);

            // Alert all colony members
            AABB alertArea = new AABB(
                    center.getX() - radius, center.getY() - 16, center.getZ() - radius,
                    center.getX() + radius, center.getY() + 16, center.getZ() + radius);

            List<EntityParasiteBase> colonyMembers = parasite.level().getEntitiesOfClass(
                    EntityParasiteBase.class, alertArea,
                    p -> p.isAlive() && p.getColonyComponent() != null &&
                         p.getColonyComponent().isColonyMember());

            for (EntityParasiteBase member : colonyMembers) {
                if (member.getTarget() == null) {
                    member.setTarget(primaryThreat);
                }
            }
        } else {
            // No threats — de-escalate
            colony.decrementAlertLevel();
        }
    }

    // ========== Patrol ==========

    /**
     * Patrols the colony perimeter, moving between waypoints.
     * Only used by mobile entities (deterrents).
     */
    protected void tickPatrol(ColonyComponent colony) {
        BlockPos center = colony.getColonyCenter();

        // Don't patrol if we have a target (fighting)
        if (parasite.getTarget() != null) return;

        // Initialize patrol waypoints if needed
        if (patrolWaypoints == null) {
            generatePatrolWaypoints(center, colony.getColonyRadius());
        }

        if (patrolWaypoints == null || patrolWaypoints.length == 0) return;

        // Get current waypoint
        BlockPos waypoint = patrolWaypoints[currentWaypointIndex];
        double distToWaypoint = parasite.blockPosition().distSqr(waypoint);

        // If we reached the waypoint, advance to next
        if (distToWaypoint < WAYPOINT_REACH_DIST * WAYPOINT_REACH_DIST) {
            currentWaypointIndex = (currentWaypointIndex + 1) % patrolWaypoints.length;
            waypoint = patrolWaypoints[currentWaypointIndex];
        }

        // Move toward waypoint
        parasite.getNavigation().moveTo(
                waypoint.getX() + 0.5, waypoint.getY(), waypoint.getZ() + 0.5, PATROL_SPEED);
    }

    /**
     * Generates patrol waypoints around the colony perimeter.
     */
    protected void generatePatrolWaypoints(BlockPos center, int radius) {
        patrolWaypoints = new BlockPos[PATROL_WAYPOINT_COUNT];
        for (int i = 0; i < PATROL_WAYPOINT_COUNT; i++) {
            double angle = (2.0 * Math.PI * i) / PATROL_WAYPOINT_COUNT;
            int dx = (int) Math.round(Math.cos(angle) * radius);
            int dz = (int) Math.round(Math.sin(angle) * radius);
            patrolWaypoints[i] = center.offset(dx, 0, dz);
        }
    }

    // ========== Reinforcement ==========

    /**
     * Spawns reinforcement parasite units when the colony is under heavy attack.
     * Only colony leaders can call reinforcements.
     */
    protected void tickReinforcement(ColonyComponent colony) {
        if (reinforcementCooldown > 0) return;
        if (colony.getColonyPoints() < REINFORCEMENT_POINT_COST) return;
        if (!(parasite.level() instanceof ServerLevel serverLevel)) return;

        // Count current colony units
        int currentUnits = colony.countColonyUnits();
        int maxUnits = colony.getMaxColonyUnits();
        if (currentUnits >= maxUnits) return;

        // Attempt to spawn a reinforcement
        EntityParasiteBase reinforcement = colony.createColonyUnit(serverLevel);
        if (reinforcement != null) {
            BlockPos spawnPos = colony.findUnitSpawnPos();
            if (spawnPos != null) {
                reinforcement.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                        parasite.getYRot(), 0);
                reinforcement.setColonySpawned(true);
                reinforcement.setOwner(parasite);

                // Set reinforcement to target the current threat
                if (parasite.getTarget() != null) {
                    reinforcement.setTarget(parasite.getTarget());
                }

                serverLevel.addFreshEntity(reinforcement);

                // Deduct points and set cooldown
                colony.spendColonyPoints(REINFORCEMENT_POINT_COST);
                reinforcementCooldown = REINFORCEMENT_COOLDOWN;
            }
        }
    }

    // ========== Retreat ==========

    /**
     * Checks if this parasite should retreat to the colony center.
     * Retreats when health drops below the threshold.
     */
    protected boolean shouldRetreat() {
        float healthFraction = parasite.getHealth() / parasite.getMaxHealth();
        return healthFraction <= RETREAT_HEALTH_THRESHOLD;
    }

    /**
     * Moves toward the colony center for healing.
     * Parasites in the safe zone receive increased regeneration.
     */
    protected void tickRetreat(ColonyComponent colony) {
        BlockPos center = colony.getColonyCenter();
        double distToCenter = parasite.blockPosition().distSqr(center);

        // If in safe zone, stay still and heal
        if (distToCenter <= RETREAT_SAFE_ZONE * RETREAT_SAFE_ZONE) {
            parasite.getNavigation().stop();
            // Clear target while retreating (survival instinct)
            if (parasite.getTarget() != null && parasite.getHealth() < parasite.getMaxHealth() * 0.5F) {
                parasite.setTarget(null);
            }
            // Extra healing is handled by the beacon/leader buff system
            return;
        }

        // Move toward colony center
        parasite.getNavigation().moveTo(
                center.getX() + 0.5, center.getY(), center.getZ() + 0.5, RETREAT_SPEED);
    }

    // ========== Block Regeneration ==========

    /**
     * Scans tracked colony blocks and regenerates any that have been destroyed.
     * Only colony leaders perform block regeneration.
     */
    protected void tickBlockRegeneration(ColonyComponent colony) {
        if (!(parasite.level() instanceof ServerLevel serverLevel)) return;

        com.subspaceparasite.common.world.ColonyStructureGenerator.scanForMissingBlocks(
                serverLevel,
                colony.getTrackedBlocks(),
                colony.getRegenQueue(),
                20 // scan up to 20 blocks per tick
        );

        // Process the regen queue
        com.subspaceparasite.common.world.ColonyStructureGenerator.processBuildQueue(
                serverLevel, colony.getRegenQueue());
    }

    // ========== Utility ==========

    /**
     * Returns whether this entity should patrol (deterrents are patrol entities).
     */
    protected boolean isPatrolEntity() {
        return parasite instanceof com.subspaceparasite.common.entity.base.EntityDeterrentBase;
    }

    @Override
    public void start() {
        alertScanTimer = 0;
        regenScanTimer = 0;
        reinforcementCooldown = 0;
        currentWaypointIndex = 0;
        patrolWaypoints = null;
    }

    @Override
    public void stop() {
        patrolWaypoints = null;
    }
}
