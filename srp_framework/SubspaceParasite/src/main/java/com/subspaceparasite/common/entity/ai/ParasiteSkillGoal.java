package com.subspaceparasite.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.entity.ai.misc.EntityCanPullMobs;

import java.util.EnumSet;

/**
 * Generic special skill execution with cooldown, distance, LOS checks.
 * Enhanced to support original SRP EntityAISkill complete functionality:
 * - Attack timer and skill execution state tracking
 * - Multiple skill IDs (original uses byte attID)
 * - ignoreStatus flag support
 * - Gene modifier integration (getGeneMod(5))
 */
public class ParasiteSkillGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final int cooldown;
    protected final float minDistance;
    protected final float maxDistance;
    protected final byte skillId;
    protected final boolean needsLOS;
    protected final boolean ignoreStatus;

    protected int skillCooldown;
    protected int attackTimer;
    protected boolean isExecuting;
    protected byte currentAttID;

    public ParasiteSkillGoal(EntityParasiteBase parasite, int cooldown, float minDistance, float maxDistance, byte skillId, boolean needsLOS) {
        this(parasite, cooldown, minDistance, maxDistance, skillId, needsLOS, false);
    }

    public ParasiteSkillGoal(EntityParasiteBase parasite, int cooldown, float minDistance, float maxDistance, byte skillId, boolean needsLOS, boolean ignoreStatus) {
        this.parasite = parasite;
        this.cooldown = cooldown;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.skillId = skillId;
        this.needsLOS = needsLOS;
        this.ignoreStatus = ignoreStatus;
        this.skillCooldown = 0;
        this.attackTimer = 0;
        this.isExecuting = false;
        this.currentAttID = -1;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.skillCooldown > 0) {
            this.skillCooldown--;
            return false;
        }

        // Check ignoreStatus flag - if false, check parasite status
        if (!this.ignoreStatus && !this.parasite.canUseSkill(this.skillId)) {
            return false;
        }

        LivingEntity target = this.parasite.getTarget();
        if (target == null || !target.isAlive()) {
            // Also check multi-targets if entity supports it
            if (this.parasite instanceof EntityCanPullMobs pullMob) {
                if (pullMob.getTargetedEntityVictims().isEmpty()) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (target != null) {
            double dist = this.parasite.distanceToSqr(target);
            double minSq = this.minDistance * this.minDistance;
            double maxSq = this.maxDistance * this.maxDistance;

            if (dist < minSq || dist > maxSq) {
                return false;
            }

            if (this.needsLOS && !this.parasite.getSensing().hasLineOfSight(target)) {
                return false;
            }
        }

        // Check gene modifier at index 5 (original SRP uses getGeneMod(5))
        float geneMod = this.parasite.getGeneFloat(5);
        if (geneMod <= 0 && this.skillId != 0) {
            // Gene modifier check failed for non-zero skill
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        // Continue executing if attack timer is still active
        return this.isExecuting && this.attackTimer > 0;
    }

    @Override
    public void start() {
        LivingEntity target = this.parasite.getTarget();
        if (target != null) {
            this.currentAttID = this.skillId;
            this.isExecuting = true;
            this.attackTimer = 20; // Default 1 second execution time
            this.parasite.executeSkill(this.skillId, target);
        }
        this.skillCooldown = this.cooldown;
    }

    @Override
    public void tick() {
        super.tick();
        
        // Update attack timer
        if (this.attackTimer > 0) {
            this.attackTimer--;
        }
        
        // End execution if timer expires
        if (this.attackTimer <= 0 && this.isExecuting) {
            this.isExecuting = false;
            this.currentAttID = -1;
        }
    }

    @Override
    public void stop() {
        this.isExecuting = false;
        this.currentAttID = -1;
        this.attackTimer = 0;
        this.skillCooldown = this.cooldown;
    }

    /**
     * Get the current attack timer value.
     */
    public int getAttackTimer() {
        return this.attackTimer;
    }

    /**
     * Check if a skill is currently being executed.
     */
    public boolean isExecutingSkill() {
        return this.isExecuting;
    }

    /**
     * Get the current attack/skill ID being executed.
     */
    public byte getCurrentAttID() {
        return this.currentAttID;
    }

    /**
     * Set a specific attack ID for execution.
     */
    public void setCurrentAttID(byte attID) {
        this.currentAttID = attID;
    }

    /**
     * Check if the specified attack ID has finished execution.
     * Matches original SRP getFinished(byte attID) behavior.
     */
    public boolean getFinished(byte attID) {
        return this.currentAttID != attID || !this.isExecuting;
    }

    /**
     * Set the finished state for a specific attack ID.
     * Matches original SRP setFinished(byte attID, boolean) behavior.
     */
    public void setFinished(byte attID, boolean finished) {
        if (finished && this.currentAttID == attID) {
            this.isExecuting = false;
            this.currentAttID = -1;
            this.attackTimer = 0;
        } else if (!finished && this.currentAttID != attID) {
            this.currentAttID = attID;
            this.isExecuting = true;
            this.attackTimer = 20;
        }
    }
}
