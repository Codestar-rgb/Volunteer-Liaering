package com.subspaceparasite.common.block.entity;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.network.ColonyHeartSyncPacket;
import com.subspaceparasite.config.ModConfigSystems;
import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

import com.subspaceparasite.network.ModNetwork;

/**
 * Block entity for the Colony Heart — the central block of a parasite colony.
 * <p>
 * Tracks colony level (1-5), colony points, parasite count, and colony radius.
 * Accumulates points over time and spawns parasite units when the threshold is reached.
 * Can send {@link ColonyHeartSyncPacket} to clients for GUI rendering.
 */
public class ColonyHeartBlockEntity extends BlockEntity {

    private int colonyLevel = 1;
    private float colonyPoints = 0.0f;
    private int parasiteCount = 0;
    private float colonyRadius = 16.0f;

    private int spawnCooldown = 0;
    private static final int SPAWN_COOLDOWN_TICKS = 2400;
    private static final float POINTS_PER_TICK = 0.02f;
    private static final float[] POINT_THRESHOLDS = {50f, 150f, 400f, 800f, 1500f};
    private static final float[] RADII_PER_LEVEL = {16f, 24f, 32f, 48f, 64f};

    public ColonyHeartBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ==================== Tick ====================

    public static void serverTick(Level level, BlockPos pos, BlockState state, ColonyHeartBlockEntity be) {
        if (level.isClientSide) return;

        // Accumulate colony points
        float pointRate = POINTS_PER_TICK * be.colonyLevel;
        be.colonyPoints += pointRate;

        // Update parasite count
        AABB area = new AABB(pos).inflate(be.colonyRadius);
        be.parasiteCount = level.getEntitiesOfClass(EntityParasiteBase.class, area, e -> e.isAlive()).size();

        // Level up when points exceed threshold
        if (be.colonyLevel < 5 && be.colonyPoints >= POINT_THRESHOLDS[be.colonyLevel - 1]) {
            be.colonyLevel++;
            be.colonyRadius = RADII_PER_LEVEL[be.colonyLevel - 1];
            be.colonyPoints -= POINT_THRESHOLDS[be.colonyLevel - 2];
            level.playSound(null, pos, ModSounds.COLONY_HEART_IDLE.get(),
                    SoundSource.BLOCKS, 1.0f, 1.2f);
            be.syncToClients((ServerLevel) level);
        }

        // Spawn parasite units when threshold is reached
        be.spawnCooldown--;
        int maxUnits = 4 + be.colonyLevel * 4;
        if (be.spawnCooldown <= 0 && be.parasiteCount < maxUnits && be.colonyPoints >= 50f) {
            be.trySpawnUnit((ServerLevel) level, pos);
            be.spawnCooldown = SPAWN_COOLDOWN_TICKS / be.colonyLevel;
            be.colonyPoints -= 30f;
        }

        // Periodic sync
        if (level.getGameTime() % 200 == 0) {
            be.syncToClients((ServerLevel) level);
        }

        be.setChanged();
    }

    /**
     * Attempts to spawn a crude parasite unit near the colony heart.
     */
    private void trySpawnUnit(ServerLevel level, BlockPos pos) {
        EntityType<? extends EntityParasiteBase> unitType = switch (colonyLevel) {
            case 1, 2 -> ModEntities.CRUDE_MOVING_FLESH.get();
            case 3, 4 -> ModEntities.CRUDE_WORKER.get();
            default -> ModEntities.CRUDE_MOVING_FLESH.get();
        };

        BlockPos spawnPos = pos.offset(
                level.random.nextInt(6) - 3, 1, level.random.nextInt(6) - 3);
        if (!level.getBlockState(spawnPos).isAir() || !level.getBlockState(spawnPos.below()).isSolidRender(level, spawnPos.below())) {
            return;
        }

        EntityParasiteBase unit = unitType.create(level);
        if (unit != null) {
            unit.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                    level.random.nextFloat() * 360f, 0);
            unit.setColonySpawned(true);
            unit.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos),
                    MobSpawnType.SPAWNER, null, null);
            level.addFreshEntity(unit);
        }
    }

    /**
     * Sends colony data to all tracking clients.
     */
    private void syncToClients(ServerLevel level) {
        ColonyHeartSyncPacket packet = new ColonyHeartSyncPacket(
                this.worldPosition.hashCode(), colonyLevel, parasiteCount);
        ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(
                () -> level.getChunkAt(worldPosition)), packet);
    }

    // ==================== NBT ====================

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ColonyLevel", colonyLevel);
        tag.putFloat("ColonyPoints", colonyPoints);
        tag.putInt("ParasiteCount", parasiteCount);
        tag.putFloat("ColonyRadius", colonyRadius);
        tag.putInt("SpawnCooldown", spawnCooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("ColonyLevel")) colonyLevel = tag.getInt("ColonyLevel");
        if (tag.contains("ColonyPoints")) colonyPoints = tag.getFloat("ColonyPoints");
        if (tag.contains("ParasiteCount")) parasiteCount = tag.getInt("ParasiteCount");
        if (tag.contains("ColonyRadius")) colonyRadius = tag.getFloat("ColonyRadius");
        if (tag.contains("SpawnCooldown")) spawnCooldown = tag.getInt("SpawnCooldown");
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    // ==================== Accessors ====================

    public int getColonyLevel() { return colonyLevel; }
    public void setColonyLevel(int level) { this.colonyLevel = Math.max(1, Math.min(5, level)); setChanged(); }

    public float getColonyPoints() { return colonyPoints; }
    public void setColonyPoints(float points) { this.colonyPoints = points; setChanged(); }

    public int getParasiteCount() { return parasiteCount; }

    public float getColonyRadius() { return colonyRadius; }
    public void setColonyRadius(float radius) { this.colonyRadius = radius; setChanged(); }
}
