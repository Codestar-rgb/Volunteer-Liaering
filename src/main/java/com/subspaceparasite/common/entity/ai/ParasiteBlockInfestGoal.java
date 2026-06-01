package com.subspaceparasite.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;

/**
 * Periodically infest the block the parasite is standing on using spreading logic.
 * Stage parameter controls infestation tier.
 */
public class ParasiteBlockInfestGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final int stage;
    protected int infestCooldown;
    protected static final int BASE_COOLDOWN = 40;

    public ParasiteBlockInfestGoal(EntityParasiteBase parasite, int stage) {
        this.parasite = parasite;
        this.stage = stage;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.parasite.getTarget() != null) return false;
        if (this.infestCooldown > 0) {
            this.infestCooldown--;
            return false;
        }
        return this.parasite.onGround() && !this.parasite.isVehicle();
    }

    @Override
    public boolean canContinueToUse() {
        return false; // one-shot per cooldown
    }

    @Override
    public void start() {
        if (this.parasite.level().isClientSide) return;
        BlockPos standingPos = this.parasite.blockPosition().below();
        BlockState standingState = this.parasite.level().getBlockState(standingPos);

        if (!standingState.isAir() && canInfest(standingState)) {
            BlockState infestedState = getInfestedBlockForStage(standingState, this.stage);
            if (infestedState != null) {
                this.parasite.level().setBlockAndUpdate(standingPos, infestedState);
            }
        }

        this.infestCooldown = BASE_COOLDOWN + this.parasite.getRandom().nextInt(40);
    }

    /**
     * Check if the given block state can be infested.
     */
    protected boolean canInfest(BlockState state) {
        // Delegate to the parasite's spreading logic
        return this.parasite.canInfestBlock(state);
    }

    /**
     * Get the infested version of the block for the given stage.
     * Subclasses or the parasite can override spreading logic.
     */
    protected BlockState getInfestedBlockForStage(BlockState original, int stage) {
        return this.parasite.getInfestedBlock(original, stage);
    }
}
