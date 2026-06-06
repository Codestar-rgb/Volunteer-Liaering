package com.subspaceparasite.common.block;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.capability.ParasiteCapability;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.world.ModWorldData;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Base class for parasite-themed blocks that spread COTH aura.
 * <p>
 * These blocks periodically infect nearby non-parasite entities with
 * COTH (Call of the Hive). The spread rate and range are influenced
 * by the current evolution phase and SRP difficulty setting.
 * <p>
 * Used for organic/fleshy blocks like INFESTED_STAIN, PARASITE_STAIN,
 * PARASITE_VINE, PARASITE_FOG, and similar aura-spreading blocks.
 */
public class BlockParasiteBase extends Block {

    /** Default range (in blocks) for COTH aura spreading from this block. */
    private static final float DEFAULT_AURA_RANGE = 4.0F;

    /** Default chance per random tick to attempt infecting an entity. */
    private static final float DEFAULT_SPREAD_CHANCE = 0.15F;

    /** Infection amount added per successful spread. */
    private static final int INFECTION_AMOUNT = 5;

    public BlockParasiteBase(Properties properties) {
        super(properties);
    }

    /**
     * Random tick: attempts to spread COTH aura to nearby entities.
     * Only runs on the server side.
     */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!ModConfigSystems.isInfectionEnabled()) return;

        // Phase-aware spread chance
        EvoPhase phase = ModWorldData.get(level).getCurrentPhase();
        float chance = DEFAULT_SPREAD_CHANCE + phase.getPhaseNumber() * 0.03F;
        if (random.nextFloat() > chance) return;

        // Find nearby non-parasite entities
        float range = DEFAULT_AURA_RANGE + phase.getPhaseNumber() * 0.5F;
        AABB area = new AABB(pos).inflate(range);
        List<LivingEntity> nearby = level.getEntitiesOfClass(
                LivingEntity.class, area,
                e -> e.isAlive() && !(e instanceof EntityParasiteBase));

        for (LivingEntity entity : nearby) {
            if (isImmune(entity)) continue;

            // Apply COTH infection via capability
            entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(cap -> {
                if (!cap.isImmune() && cap.getInfectionCooldown() <= 0) {
                    cap.addInfection(INFECTION_AMOUNT);
                    cap.markDirty();
                }
            });

            // Also apply wither as the visual COTH indicator
            int duration = ModConfigSystems.getCOTHDuration();
            if (!entity.hasEffect(MobEffects.WITHER)) {
                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, duration, 0));
            }

            break; // Only infect one entity per tick
        }
    }

    /**
     * Checks if an entity is immune to COTH from this block.
     */
    protected boolean isImmune(LivingEntity entity) {
        if (entity instanceof EntityParasiteBase) return true;
        if (entity instanceof net.minecraft.world.entity.player.Player player) {
            if (player.isCreative() || player.isSpectator()) return true;
        }
        if (ModConfigSystems.isEntityCOTHImmune(entity.getType())) return true;
        return false;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return ModConfigSystems.isInfectionEnabled();
    }
}
