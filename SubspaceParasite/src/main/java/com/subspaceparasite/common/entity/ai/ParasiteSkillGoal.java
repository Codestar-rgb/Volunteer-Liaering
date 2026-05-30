package com.subspaceparasite.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;

/**
 * Generic special skill execution with cooldown, distance, LOS checks.
 */
public class ParasiteSkillGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final int cooldown;
    protected final float minDistance;
    protected final float maxDistance;
    protected final byte skillId;
    protected final boolean needsLOS;

    protected int skillCooldown;

    public ParasiteSkillGoal(EntityParasiteBase parasite, int cooldown, float minDistance, float maxDistance, byte skillId, boolean needsLOS) {
        this.parasite = parasite;
        this.cooldown = cooldown;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.skillId = skillId;
        this.needsLOS = needsLOS;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.skillCooldown > 0) {
            this.skillCooldown--;
            return false;
        }

        LivingEntity target = this.parasite.getTarget();
        if (target == null || !target.isAlive()) return false;

        double dist = this.parasite.distanceToSqr(target);
        double minSq = this.minDistance * this.minDistance;
        double maxSq = this.maxDistance * this.maxDistance;

        if (dist < minSq || dist > maxSq) return false;

        if (this.needsLOS && !this.parasite.getSensing().hasLineOfSight(target)) {
            return false;
        }

        return this.parasite.canUseSkill(this.skillId);
    }

    @Override
    public boolean canContinueToUse() {
        return false; // one-shot skill execution
    }

    @Override
    public void start() {
        LivingEntity target = this.parasite.getTarget();
        if (target != null) {
            this.parasite.executeSkill(this.skillId, target);
        }
        this.skillCooldown = this.cooldown;
    }

    @Override
    public void stop() {
        this.skillCooldown = this.cooldown;
    }
}
