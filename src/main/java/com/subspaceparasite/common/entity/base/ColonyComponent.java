package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.world.ModWorldData;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * Component handling colony-related logic for parasite entities.
 * Manages colony point tracking, spawn eligibility, structure generation
 * triggers, and colony merge logic.
 */
public class ColonyComponent {

    private final EntityParasiteBase parasite;

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
    private static final int MERGE_CHECK_INTERVAL = 6000;
    private static final double BASE_COLONY_THRESHOLD = 500.0;
    private static final int BASE_MAX_COLONY_UNITS = 8;
    private static final int SPAWN_COOLDOWN_TICKS = 2400;
    private static final int DEFAULT_COLONY_RADIUS = 32;

    public ColonyComponent(EntityParasiteBase parasite) {
        this.parasite = parasite;
        this.colonyPoints = 0.0;
        this.colonyPointThreshold = BASE_COLONY_THRESHOLD;
        this.isColonyMember = false;
        this.isColonyLeader = false;
        this.colonyCenter = null;
        this.colonyRadius = DEFAULT_COLONY_RADIUS;
        this.spawnEligibilityTimer = 0;
        this.spawnCooldown = 0;
        this.maxColonyUnits = BASE_MAX_COLONY_UNITS;
        this.mergeCheckTimer = 0;
    }

    public void tick() {
        if (parasite.level().isClientSide) return;
        if (!ModConfigSystems.isColonySystemEnabled()) return;

        if (parasite.srpTicks % 200 == 0) addColonyPoints(ModConfigSystems.getColonyPointRate());
        if (spawnCooldown > 0) spawnCooldown--;
        if (spawnEligibilityTimer > 0) spawnEligibilityTimer--;

        if (isColonyLeader && colonyCenter != null) tickColonyLeader();

        mergeCheckTimer++;
        if (mergeCheckTimer >= MERGE_CHECK_INTERVAL) {
            mergeCheckTimer = 0;
            checkColonyMerge();
        }

        if (colonyPoints >= colonyPointThreshold && spawnCooldown <= 0) {
            checkColonySpawn();
        }
    }

    public void addColonyPoints(double points) {
        float phaseMult = 1.0F + parasite.getPhaseCreated().getPhaseNumber() * 0.3F;
        this.colonyPoints += points * phaseMult;
    }

    public void onKill() {
        addColonyPoints(ModConfigSystems.getKillColonyPoints());
    }

    protected void tickColonyLeader() {
        int currentUnits = countColonyUnits();
        if (colonyCenter != null) {
            double distToCenter = parasite.blockPosition().distManhattan(colonyCenter);
            if (distToCenter > colonyRadius * 2) {
                parasite.getNavigation().moveTo(
                        colonyCenter.getX() + 0.5, colonyCenter.getY(), colonyCenter.getZ() + 0.5, 1.0);
            }
        }
        if (parasite.level() instanceof ServerLevel serverLevel) {
            ModWorldData worldData = ModWorldData.get(serverLevel);
            worldData.updateColonyData(colonyCenter, currentUnits, isColonyLeader);
        }
    }

    protected int countColonyUnits() {
        if (colonyCenter == null) return 0;
        AABB territory = new AABB(
                colonyCenter.getX() - colonyRadius, colonyCenter.getY() - 16, colonyCenter.getZ() - colonyRadius,
                colonyCenter.getX() + colonyRadius, colonyCenter.getY() + 16, colonyCenter.getZ() + colonyRadius);
        return parasite.level().getEntitiesOfClass(EntityParasiteBase.class, territory,
                p -> p != parasite && p.isColonySpawned() && p.isAlive()).size();
    }

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
        this.colonyRadius = DEFAULT_COLONY_RADIUS;
        this.maxColonyUnits = BASE_MAX_COLONY_UNITS + parasite.getPhaseCreated().getPhaseNumber() * 2;
        this.spawnCooldown = SPAWN_COOLDOWN_TICKS;
        this.parasite.setCanDespawn(false);

        if (parasite.level() instanceof ServerLevel serverLevel) {
            ModWorldData worldData = ModWorldData.get(serverLevel);
            worldData.addColony(pos, parasite.getUUID());
        }
    }

    public boolean isEligibleToSpawn() {
        if (!isColonyLeader || spawnCooldown > 0 || colonyCenter == null) return false;
        return countColonyUnits() < maxColonyUnits;
    }

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
        other.isColonyLeader = false;
        other.colonyCenter = null;
        other.colonyRadius = 0;
        if (parasite.level() instanceof ServerLevel serverLevel) {
            ModWorldData worldData = ModWorldData.get(serverLevel);
            worldData.removeColony(other.parasite.getUUID());
        }
    }

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
            successor.setCanDespawn(false);
        }
    }

    protected void dissolveColony() {
        if (parasite.level() instanceof ServerLevel serverLevel) {
            ModWorldData worldData = ModWorldData.get(serverLevel);
            worldData.removeColony(parasite.getUUID());
        }
    }

    public double getColonyPoints() { return colonyPoints; }
    public boolean isColonyMember() { return isColonyMember; }
    public boolean isColonyLeader() { return isColonyLeader; }
    public BlockPos getColonyCenter() { return colonyCenter; }
    public int getColonyRadius() { return colonyRadius; }
    public int getMaxColonyUnits() { return maxColonyUnits; }
    public void setColonyMember(boolean value) { this.isColonyMember = value; }
    public void setColonyLeader(boolean value) { this.isColonyLeader = value; }
    public void setColonyCenter(BlockPos pos) { this.colonyCenter = pos; }
    public void setMaxColonyUnits(int value) { this.maxColonyUnits = value; }

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
    }
}
