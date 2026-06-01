package com.subspaceparasite.common.block.entity;

import com.subspaceparasite.common.block.BlockPurifyMappings;
import com.subspaceparasite.core.ModBlocks;
import com.subspaceparasite.core.ModParticleTypes;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Block entity for the Biome Heart — the core of a parasite biome.
 * <p>
 * Tracks infection radius, infection strength, and active state.
 * Each tick it spreads infection to nearby blocks and increases
 * infection strength over time.
 */
public class BiomeHeartBlockEntity extends BlockEntity {

    private float infectionRadius = 16.0f;
    private float infectionStrength = 0.0f;
    private boolean active = true;

    private int spreadCooldown = 0;
    private static final int SPREAD_INTERVAL = 100; // ticks between spread attempts
    private static final float STRENGTH_PER_TICK = 0.001f;
    private static final float MAX_STRENGTH = 10.0f;
    private static final float RADIUS_GROWTH = 0.01f;
    private static final float MAX_RADIUS = 64.0f;

    public BiomeHeartBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ==================== Tick ====================

    public static void serverTick(Level level, BlockPos pos, BlockState state, BiomeHeartBlockEntity be) {
        if (level.isClientSide) return;
        if (!be.active) return;

        // Increase infection strength over time
        if (be.infectionStrength < MAX_STRENGTH) {
            be.infectionStrength = Math.min(MAX_STRENGTH, be.infectionStrength + STRENGTH_PER_TICK);
        }

        // Slowly grow infection radius with strength
        if (be.infectionStrength > 1.0f && be.infectionRadius < MAX_RADIUS) {
            be.infectionRadius = Math.min(MAX_RADIUS, be.infectionRadius + RADIUS_GROWTH);
        }

        // Spread infection to nearby blocks on cooldown
        be.spreadCooldown--;
        if (be.spreadCooldown <= 0) {
            be.spreadCooldown = SPREAD_INTERVAL;
            be.spreadInfection((ServerLevel) level, pos);
        }

        // Ambient particles and sound
        if (level.getGameTime() % 40 == 0) {
            ((ServerLevel) level).sendParticles(ParticleTypes.PORTAL,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    4, 0.3, 0.5, 0.3, 0.02);
        }

        if (level.getGameTime() % 200 == 0) {
            level.playSound(null, pos, ModSounds.BIOME_HEART_BEAT.get(),
                    SoundSource.BLOCKS, 0.5f, 0.8f + level.random.nextFloat() * 0.4f);
        }

        be.setChanged();
    }

    /**
     * Spreads infection to nearby blocks by converting them via BlockPurifyMappings.
     */
    private void spreadInfection(ServerLevel level, BlockPos origin) {
        int radiusInt = (int) infectionRadius;
        float spreadChance = 0.05f * infectionStrength;

        for (int dx = -radiusInt; dx <= radiusInt; dx += 4) {
            for (int dy = -radiusInt; dy <= radiusInt; dy += 4) {
                for (int dz = -radiusInt; dz <= radiusInt; dz += 4) {
                    if (dx * dx + dy * dy + dz * dz > infectionRadius * infectionRadius) continue;
                    if (level.random.nextFloat() > spreadChance) continue;

                    BlockPos target = origin.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);
                    Block infectedBlock = BlockPurifyMappings.getInfestedBlock(targetState.getBlock());
                    if (infectedBlock != null) {
                        BlockState infectedState = infectedBlock.defaultBlockState();
                        if (infectedState != targetState) {
                            level.setBlockAndUpdate(target, infectedState);
                        }
                    }
                }
            }
        }
    }

    // ==================== NBT ====================

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("InfectionRadius", infectionRadius);
        tag.putFloat("InfectionStrength", infectionStrength);
        tag.putBoolean("Active", active);
        tag.putInt("SpreadCooldown", spreadCooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("InfectionRadius")) infectionRadius = tag.getFloat("InfectionRadius");
        if (tag.contains("InfectionStrength")) infectionStrength = tag.getFloat("InfectionStrength");
        if (tag.contains("Active")) active = tag.getBoolean("Active");
        if (tag.contains("SpreadCooldown")) spreadCooldown = tag.getInt("SpreadCooldown");
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    // ==================== Accessors ====================

    public float getInfectionRadius() { return infectionRadius; }
    public void setInfectionRadius(float radius) { this.infectionRadius = radius; setChanged(); }

    public float getInfectionStrength() { return infectionStrength; }
    public void setInfectionStrength(float strength) { this.infectionStrength = strength; setChanged(); }

    public boolean isActive() { return active; }
    public void setActive(boolean value) { this.active = value; setChanged(); }
}
