package com.subspaceparasite.common.block;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.capability.ParasiteCapability;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.world.ModWorldData;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

/**
 * Base class for parasite fluid blocks that infect entities on touch.
 * <p>
 * When a non-parasite entity touches or walks through this fluid,
 * it receives COTH infection via ParasiteCapability and a wither
 * effect as the visual indicator. The infection rate is influenced
 * by the current evolution phase.
 * <p>
 * Used for the DEAD_BLOOD fluid block.
 */
public class BlockFluidBase extends LiquidBlock {

    /** Infection amount added per touch tick. */
    private static final int TOUCH_INFECTION_AMOUNT = 3;

    /** COTH effect duration applied on touch. */
    private static final int COTH_DURATION = 200;

    public BlockFluidBase(Supplier<? extends FlowingFluid> fluid, Properties properties) {
        super(fluid, properties);
    }

    /**
     * Called when an entity steps into or touches this fluid.
     * Applies COTH infection and wither effect to non-parasite entities.
     */
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) return;
        if (!ModConfigSystems.isInfectionEnabled()) return;
        if (!(entity instanceof LivingEntity living)) return;
        if (isImmune(living)) return;

        // Phase-aware infection rate
        EvoPhase phase = ModWorldData.get((net.minecraft.server.level.ServerLevel) level).getCurrentPhase();
        int infectionAmount = TOUCH_INFECTION_AMOUNT + phase.getPhaseNumber();

        // Apply infection via capability
        living.getCapability(ParasiteCapability.CAPABILITY).ifPresent(cap -> {
            if (!cap.isImmune() && cap.getInfectionCooldown() <= 0) {
                cap.addInfection(infectionAmount);
                cap.markDirty();
            }
        });

        // Apply wither as the visual COTH indicator
        if (!living.hasEffect(MobEffects.WITHER)) {
            living.addEffect(new MobEffectInstance(MobEffects.WITHER, COTH_DURATION, 0));
        }

        super.entityInside(state, level, pos, entity);
    }

    /**
     * Checks if an entity is immune to COTH from this fluid.
     */
    protected boolean isImmune(LivingEntity entity) {
        if (entity instanceof EntityParasiteBase) return true;
        if (entity instanceof Player player) {
            if (player.isCreative() || player.isSpectator()) return true;
        }
        if (ModConfigSystems.isEntityCOTHImmune(entity.getType())) return true;
        return false;
    }
}
