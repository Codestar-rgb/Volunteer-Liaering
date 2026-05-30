package com.subspaceparasite.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;

/**
 * Place residue blocks when idle (no target, gene mod required).
 */
public class ParasiteBlockResidueGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final int range;

    protected int residueCooldown;
    protected static final int BASE_COOLDOWN = 60;

    public ParasiteBlockResidueGoal(EntityParasiteBase parasite, int range) {
        this.parasite = parasite;
        this.range = range;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // Must not have a target and must have gene modification
        if (this.parasite.getTarget() != null) return false;
        if (this.parasite.getGene() <= 0) return false;

        if (this.residueCooldown > 0) {
            this.residueCooldown--;
            return false;
        }

        return this.parasite.onGround();
    }

    @Override
    public boolean canContinueToUse() {
        return false; // one-shot per cooldown
    }

    @Override
    public void start() {
        placeResidueNearby();
        this.residueCooldown = BASE_COOLDOWN + this.parasite.getRandom().nextInt(40);
    }

    @Override
    public void stop() {
        this.residueCooldown = BASE_COOLDOWN + this.parasite.getRandom().nextInt(40);
    }

    protected void placeResidueNearby() {
        if (this.parasite.level().isClientSide) return;
        BlockPos origin = this.parasite.blockPosition();

        for (int attempts = 0; attempts < 5; attempts++) {
            int x = origin.getX() + this.parasite.getRandom().nextInt(this.range * 2 + 1) - this.range;
            int y = origin.getY() + this.parasite.getRandom().nextInt(3) - 1;
            int z = origin.getZ() + this.parasite.getRandom().nextInt(this.range * 2 + 1) - this.range;

            BlockPos target = new BlockPos(x, y, z);
            BlockState state = this.parasite.level().getBlockState(target);

            if (state.isAir() && !this.parasite.level().getBlockState(target.below()).isAir()) {
                BlockState residue = this.parasite.getResidueBlock();
                if (residue != null) {
                    this.parasite.level().setBlockAndUpdate(target, residue);
                    return;
                }
            }
        }
    }
}
