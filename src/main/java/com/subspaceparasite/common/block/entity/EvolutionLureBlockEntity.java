package com.subspaceparasite.common.block.entity;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.core.ModParticleTypes;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Block entity for the Evolution Lure — speeds up nearby parasite evolution.
 * <p>
 * Tracks lure radius, evolution boost rate, and active state.
 * Each tick it boosts evolution points of nearby EntityParasiteBase entities.
 */
public class EvolutionLureBlockEntity extends BlockEntity {

    private float lureRadius = 32.0f;
    private float evolutionBoostRate = 0.1f;
    private boolean active = true;

    private int boostCooldown = 0;
    private static final int BOOST_INTERVAL = 20; // boost every second

    public EvolutionLureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ==================== Tick ====================

    public static void serverTick(Level level, BlockPos pos, BlockState state, EvolutionLureBlockEntity be) {
        if (level.isClientSide) return;
        if (!be.active) return;

        be.boostCooldown--;
        if (be.boostCooldown <= 0) {
            be.boostCooldown = BOOST_INTERVAL;
            be.boostNearbyParasites((ServerLevel) level, pos);
        }

        // Ambient particles
        if (level.getGameTime() % 30 == 0) {
            ((ServerLevel) level).sendParticles(
                    ModParticleTypes.EVOLUTION_SPARK.get(),
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    3, 0.5, 0.5, 0.5, 0.02);
        }

        // Ambient sound
        if (level.getGameTime() % 400 == 0) {
            level.playSound(null, pos, ModSounds.EVOLUTION_UPGRADE.get(),
                    SoundSource.BLOCKS, 0.3f, 1.0f + level.random.nextFloat() * 0.3f);
        }
    }

    /**
     * Boosts evolution points of all nearby EntityParasiteBase entities.
     */
    private void boostNearbyParasites(ServerLevel level, BlockPos pos) {
        AABB area = new AABB(pos).inflate(lureRadius);
        var nearby = level.getEntitiesOfClass(EntityParasiteBase.class, area, e -> e.isAlive());

        for (EntityParasiteBase parasite : nearby) {
            if (parasite.getEvolutionComponent() != null) {
                parasite.getEvolutionComponent().addEvolutionPoints(evolutionBoostRate);
            }
        }
    }

    // ==================== NBT ====================

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("LureRadius", lureRadius);
        tag.putFloat("EvolutionBoostRate", evolutionBoostRate);
        tag.putBoolean("Active", active);
        tag.putInt("BoostCooldown", boostCooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("LureRadius")) lureRadius = tag.getFloat("LureRadius");
        if (tag.contains("EvolutionBoostRate")) evolutionBoostRate = tag.getFloat("EvolutionBoostRate");
        if (tag.contains("Active")) active = tag.getBoolean("Active");
        if (tag.contains("BoostCooldown")) boostCooldown = tag.getInt("BoostCooldown");
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    // ==================== Accessors ====================

    public float getLureRadius() { return lureRadius; }
    public void setLureRadius(float radius) { this.lureRadius = radius; setChanged(); }

    public float getEvolutionBoostRate() { return evolutionBoostRate; }
    public void setEvolutionBoostRate(float rate) { this.evolutionBoostRate = rate; setChanged(); }

    public boolean isActive() { return active; }
    public void setActive(boolean value) { this.active = value; setChanged(); }
}
