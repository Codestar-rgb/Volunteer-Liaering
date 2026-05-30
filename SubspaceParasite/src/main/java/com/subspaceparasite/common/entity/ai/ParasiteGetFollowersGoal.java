package com.subspaceparasite.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;
import java.util.List;

/**
 * Recruit nearby unaffiliated parasites. Version tiers control recruitment range.
 */
public class ParasiteGetFollowersGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final int versionTier;
    protected final int range;

    protected int recruitCooldown;
    protected static final int BASE_COOLDOWN = 200;

    public ParasiteGetFollowersGoal(EntityParasiteBase parasite, int versionTier, int range) {
        this.parasite = parasite;
        this.versionTier = versionTier;
        this.range = range;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.recruitCooldown > 0) {
            this.recruitCooldown--;
            return false;
        }
        return this.parasite.getTarget() == null && !this.parasite.isDeadOrDying();
    }

    @Override
    public boolean canContinueToUse() {
        return false; // one-shot per cooldown
    }

    @Override
    public void start() {
        double recruitRange = this.range * (1.0 + this.versionTier * 0.5);

        List<EntityParasiteBase> nearby = this.parasite.level().getNearbyEntities(
                EntityParasiteBase.class,
                TargetingConditions.forNonCombat().range(recruitRange),
                this.parasite,
                this.parasite.getBoundingBox().inflate(recruitRange)
        );

        int recruited = 0;
        int maxRecruits = 2 + this.versionTier;

        for (EntityParasiteBase candidate : nearby) {
            if (candidate == this.parasite) continue;
            if (candidate.isDeadOrDying()) continue;
            if (candidate.getLeader() != null) continue; // already following someone
            if (candidate.getParasiteTier() >= this.parasite.getParasiteTier()) continue; // can't recruit equals/superiors

            // Recruit this parasite
            candidate.setLeader(this.parasite);
            recruited++;

            if (recruited >= maxRecruits) break;
        }

        this.recruitCooldown = BASE_COOLDOWN + this.parasite.getRandom().nextInt(100);
    }

    @Override
    public void stop() {
        this.recruitCooldown = BASE_COOLDOWN + this.parasite.getRandom().nextInt(100);
    }
}
