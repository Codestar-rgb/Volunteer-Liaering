package com.subspaceparasite.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;

/**
 * Follow-the-leader behavior. Relay attack target to followed entity.
 */
public class ParasiteFollowLeaderGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final double followSpeed;
    protected final double minDistance;
    protected final double maxDistance;
    protected final boolean relayTarget;

    protected EntityParasiteBase leader;
    protected int recheckDelay;

    public ParasiteFollowLeaderGoal(EntityParasiteBase parasite, double followSpeed, double minDistance, double maxDistance, boolean relayTarget) {
        this.parasite = parasite;
        this.followSpeed = followSpeed;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.relayTarget = relayTarget;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.parasite.getTarget() != null) return false;
        if (this.parasite.isVehicle()) return false;

        this.leader = this.parasite.getLeader();
        return this.leader != null && !this.leader.isDeadOrDying();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.leader == null || this.leader.isDeadOrDying()) return false;
        if (this.parasite.getTarget() != null) return false;

        double dist = this.parasite.distanceToSqr(this.leader);
        return dist > this.minDistance * this.minDistance;
    }

    @Override
    public void start() {
        this.recheckDelay = 0;
    }

    @Override
    public void stop() {
        this.leader = null;
        this.parasite.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.leader == null) return;

        // Relay target to leader
        if (this.relayTarget && this.parasite.getTarget() != null && this.leader.getTarget() == null) {
            this.leader.setTarget(this.parasite.getTarget());
        }

        // Relay from leader to self
        if (this.relayTarget && this.leader.getTarget() != null && this.parasite.getTarget() == null) {
            this.parasite.setTarget(this.leader.getTarget());
            return;
        }

        this.recheckDelay--;
        if (this.recheckDelay <= 0) {
            this.recheckDelay = 10 + this.parasite.getRandom().nextInt(5);

            double dist = this.parasite.distanceToSqr(this.leader);
            if (dist > this.maxDistance * this.maxDistance) {
                // Teleport to leader if too far away
                this.parasite.getNavigation().moveTo(this.leader, this.followSpeed * 1.5D);
            } else {
                this.parasite.getNavigation().moveTo(this.leader, this.followSpeed);
            }

            this.parasite.getLookControl().setLookAt(this.leader, 30.0F, 30.0F);
        }
    }
}
