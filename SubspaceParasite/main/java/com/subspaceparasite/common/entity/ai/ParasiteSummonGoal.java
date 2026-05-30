package com.subspaceparasite.common.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import com.subspaceparasite.common.entity.ai.misc.EntityCanSummon;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;

/**
 * Summon minions from point budget. Checks EntityCanSummon interface.
 */
public class ParasiteSummonGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final int cooldown;
    protected final int range;
    protected final int maxSummons;

    protected int summonCooldown;
    protected boolean checkedSummonInterface;

    public ParasiteSummonGoal(EntityParasiteBase parasite, int cooldown, int range, int maxSummons) {
        this.parasite = parasite;
        this.cooldown = cooldown;
        this.range = range;
        this.maxSummons = maxSummons;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!(this.parasite instanceof EntityCanSummon)) return false;

        if (this.summonCooldown > 0) {
            this.summonCooldown--;
            return false;
        }

        LivingEntity target = this.parasite.getTarget();
        if (target == null || !target.isAlive()) return false;

        EntityCanSummon summoner = (EntityCanSummon) this.parasite;
        return summoner.getActualParasites() < this.maxSummons && summoner.checkID();
    }

    @Override
    public boolean canContinueToUse() {
        return false; // one-shot per cooldown
    }

    @Override
    public void start() {
        EntityCanSummon summoner = (EntityCanSummon) this.parasite;

        if (summoner.getIDList() == null || summoner.getIDList().isEmpty()) return;

        // Calculate spawn positions around the target
        LivingEntity target = this.parasite.getTarget();
        if (target == null) return;

        for (int i = 0; i < Math.min(3, summoner.getPointList().size()); i++) {
            BlockPos spawnPos = findSpawnPosition(target.blockPosition());
            if (spawnPos != null) {
                int id = summoner.getIDList().isEmpty() ? -1 : summoner.getIDList().get(0);
                if (id >= 0) {
                    summoner.addID(id, 1);
                    summoner.setActualParasites(summoner.getActualParasites() + 1);
                    // Actual spawning would be handled by the entity implementation
                    this.parasite.onSummonMinion(spawnPos, id);
                }
            }
        }

        this.summonCooldown = this.cooldown;
    }

    @Override
    public void stop() {
        this.summonCooldown = this.cooldown;
    }

    @Nullable
    protected BlockPos findSpawnPosition(BlockPos center) {
        for (int attempts = 0; attempts < 10; attempts++) {
            int x = center.getX() + this.parasite.getRandom().nextInt(this.range * 2) - this.range;
            int z = center.getZ() + this.parasite.getRandom().nextInt(this.range * 2) - this.range;
            int y = center.getY() + this.parasite.getRandom().nextInt(6) - 3;

            BlockPos candidate = new BlockPos(x, y, z);
            if (this.parasite.level().getBlockState(candidate).isAir() &&
                this.parasite.level().getBlockState(candidate.below()).isCollisionShapeFullBlock(this.parasite.level(), candidate.below())) {
                return candidate;
            }
        }
        return null;
    }
}
