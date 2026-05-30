package com.subspaceparasite.common.entity.ai;

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import javax.annotation.Nullable;

/**
 * Wander respecting parasite status. Reduced range when following.
 */
public class ParasiteWanderGoal extends WaterAvoidingRandomStrollGoal {

    protected final EntityParasiteBase parasite;
    protected final float probability;
    protected final boolean avoidWater;
    protected final double normalSpeed;
    protected static final double FOLLOWING_RANGE_MULTIPLIER = 0.3D;

    public ParasiteWanderGoal(EntityParasiteBase parasite, double speed, float probability, boolean avoidWater) {
        super(parasite, speed, probability);
        this.parasite = parasite;
        this.probability = probability;
        this.avoidWater = avoidWater;
        this.normalSpeed = speed;
    }

    @Override
    public boolean canUse() {
        if (this.parasite.isPassive()) return false;
        if (this.parasite.getTarget() != null) return false;
        return super.canUse();
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        boolean isFollowing = this.parasite.getLeader() != null;

        if (isFollowing && this.parasite.getLeader() != null) {
            // Reduced range when following a leader - stay close
            Vec3 leaderPos = this.parasite.getLeader().position();
            double dx = (this.parasite.getRandom().nextDouble() - 0.5) * 10.0;
            double dz = (this.parasite.getRandom().nextDouble() - 0.5) * 10.0;
            return new Vec3(leaderPos.x + dx, leaderPos.y, leaderPos.z + dz);
        }

        return super.getPosition();
    }
}
