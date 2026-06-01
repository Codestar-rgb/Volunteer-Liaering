package com.subspaceparasite.common.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import com.subspaceparasite.api.ICanAbility;
import com.subspaceparasite.common.entity.ai.misc.EntityCanSummon;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;

/**
 * Summon minions from point budget. Supports both the {@link EntityCanSummon}
 * interface (legacy point-budget system) and the new ability-based system
 * via {@link ICanAbility.AbilityType#SUMMON}.
 * <p>
 * When the parasite implements {@link EntityCanSummon}, this goal uses the
 * point-budget system with ID lists and point costs. Otherwise, it delegates
 * to {@link EntityParasiteBase#trySummon()} for ability-based summoning.
 * <p>
 * Summon trigger conditions:
 * <ul>
 *   <li>Legacy (EntityCanSummon): when getActualParasites() &lt; maxSummons and checkID()</li>
 *   <li>Ability-based: when health &lt; 50% or multiple enemies nearby</li>
 * </ul>
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
        if (this.summonCooldown > 0) {
            this.summonCooldown--;
            return false;
        }

        LivingEntity target = this.parasite.getTarget();
        if (target == null || !target.isAlive()) return false;

        // Legacy EntityCanSummon path
        if (this.parasite instanceof EntityCanSummon summoner) {
            this.checkedSummonInterface = true;
            return summoner.getActualParasites() < this.maxSummons && summoner.checkID();
        }

        // New ability-based path
        this.checkedSummonInterface = false;
        if (!this.parasite.hasAbility(ICanAbility.AbilityType.SUMMON)) return false;

        float healthPercent = this.parasite.getHealth() / this.parasite.getMaxHealth();

        // Low health trigger
        if (healthPercent < 0.50F) return true;

        // Multiple enemies nearby trigger
        net.minecraft.world.phys.AABB area = this.parasite.getBoundingBox().inflate(this.range);
        java.util.List<LivingEntity> enemies = this.parasite.level().getEntitiesOfClass(
                LivingEntity.class, area,
                e -> e != this.parasite && e.isAlive() && !(e instanceof EntityParasiteBase));
        return enemies.size() >= 2;
    }

    @Override
    public boolean canContinueToUse() {
        return false; // one-shot per cooldown
    }

    @Override
    public void start() {
        if (this.checkedSummonInterface && this.parasite instanceof EntityCanSummon summoner) {
            startLegacySummon(summoner);
        } else {
            startAbilitySummon();
        }

        this.summonCooldown = this.cooldown;
    }

    /**
     * Legacy summon path using the EntityCanSummon point-budget system.
     */
    protected void startLegacySummon(EntityCanSummon summoner) {
        if (summoner.getIDList() == null || summoner.getIDList().isEmpty()) return;

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
    }

    /**
     * Ability-based summon path using trySummon().
     * Delegates to the parasite's new summon system which creates
     * temporary minions with configurable types and lifespans.
     */
    protected void startAbilitySummon() {
        this.parasite.trySummon();
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
