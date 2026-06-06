package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import java.util.List;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ColonyDefenseGoal;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Abstract base class for Deterrent-tier parasites.
 * <p>
 * Deterrents are mobile defender entities — NOT stationary. They patrol
 * around nexus structures and debilitate nearby threats with debuff auras.
 * They are area-denial parasites that weaken and slow enemies approaching
 * colony structures.
 * <p>
 * Unlike other nexus entities, deterrents are mobile and can chase threats.
 * They extend {@link EntityMalleableBase} rather than {@link EntityStationaryBase}.
 * <p>
 * Type category: {@link EvolutionPath#DETERRENT}
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 36.0</li>
 *   <li>Attack Damage: 5.0</li>
 *   <li>Movement Speed: 0.28</li>
 *   <li>Armor: 0.0</li>
 *   <li>Follow Range: 36.0</li>
 *   <li>Knockback Resistance: 0.4</li>
 * </ul>
 */
public abstract class EntityDeterrentBase extends EntityMalleableBase {

    // ========== Patrol System ==========

    /** The anchor position this deterrent patrols around. */
    protected BlockPos patrolAnchor = null;

    /** Maximum distance from the patrol anchor. */
    protected float patrolRadius = 16.0F;

    /** Range of the debuff aura in blocks. */
    protected float debuffAuraRange = 8.0F;

    /** Ticks between debuff aura applications. */
    protected int debuffAuraInterval = 60;

    // ========== Constructor ==========

    protected EntityDeterrentBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.DETERRENT;

        // Deterrent-tier gene activations — defense and debuff focused
        geneBooleans[GeneType.DAMAGE_CAP.getIndex()] = true;
        geneBooleans[GeneType.LOOK_WALL.getIndex()] = true;
        geneBooleans[GeneType.SPRINTING.getIndex()] = true;
        geneFloats[GeneType.ANTI_KNOCKBACK.getIndex()] = Math.max(
                geneFloats[GeneType.ANTI_KNOCKBACK.getIndex()], 0.4F);
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.2F);
        geneFloats[GeneType.INFECTIOUSNESS.getIndex()] = Math.max(
                geneFloats[GeneType.INFECTIOUSNESS.getIndex()], 0.3F);
    }

    // ========== AI Goals ==========

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new ColonyDefenseGoal(this));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    // ========== Patrol System ==========

    /**
     * Sets the patrol anchor position. The deterrent will patrol around
     * this point within its patrol radius.
     *
     * @param pos the anchor position
     */
    public void setPatrolAnchor(BlockPos pos) {
        this.patrolAnchor = pos;
    }

    /**
     * Returns the patrol anchor position.
     *
     * @return the anchor position, or null if not set
     */
    public BlockPos getPatrolAnchor() {
        return patrolAnchor;
    }

    /**
     * Checks whether this deterrent is within its patrol radius of the anchor.
     *
     * @return true if within patrol range
     */
    public boolean isWithinPatrolRange() {
        if (patrolAnchor == null) return true;
        double dist = this.blockPosition().distSqr(patrolAnchor);
        return dist <= patrolRadius * patrolRadius;
    }

    // ========== Debuff Aura ==========

    /**
     * Applies debuff effects to nearby non-parasite entities.
     * Override in concrete subclasses for specific debuff types.
     * Debuff aura range is extended when the colony is on high alert.
     */
    protected void applyDebuffAura() {
        if (this.level().isClientSide) return;

        // Extend debuff range when colony is threatened
        float effectiveRange = debuffAuraRange;
        ColonyComponent colony = getColonyComponent();
        if (colony != null && colony.isThreatened()) {
            effectiveRange *= 1.5F;
        }

        AABB area = this.getBoundingBox().inflate(effectiveRange);
        List<LivingEntity> nearby = this.level().getEntitiesOfClass(
                LivingEntity.class, area,
                e -> e.isAlive() && !(e instanceof EntityParasiteBase));

        for (LivingEntity target : nearby) {
            applyDeterrentDebuff(target);
            // Also alert the colony if an intruder is detected
            if (colony != null && colony.isColonyMember()) {
                colony.incrementAlertLevel();
            }
        }
    }

    /**
     * Applies a specific debuff to a target entity.
     * Default: slowness I for 3 seconds. Override in subclasses.
     *
     * @param target the entity to debuff
     */
    protected void applyDeterrentDebuff(LivingEntity target) {
        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
    }

    // ========== Tick Override ==========

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // Apply debuff aura periodically
            if (this.srpTicks % debuffAuraInterval == 0) {
                applyDebuffAura();
            }

            // Initialize patrol anchor from colony center if available
            if (patrolAnchor == null) {
                ColonyComponent colony = getColonyComponent();
                if (colony != null && colony.getColonyCenter() != null) {
                    patrolAnchor = colony.getColonyCenter();
                    patrolRadius = colony.getColonyRadius();
                } else {
                    patrolAnchor = this.blockPosition();
                }
            }

            // Colony defense coordination
            tickColonyDefense();
        }
    }

    // ========== Colony Defense ==========

    /**
     * Coordinates colony defense activities for this deterrent.
     * Deterrents are the primary patrol and defense entities:
     * <ul>
     *   <li>Patrol the colony perimeter when not engaged in combat</li>
     *   <li>Alert colony members when intruders are detected</li>
     *   <li>Retreat to colony center when heavily damaged</li>
     *   <li>Chase threats that enter colony territory</li>
     * </ul>
     */
    protected void tickColonyDefense() {
        ColonyComponent colony = getColonyComponent();
        if (colony == null || !colony.isColonyMember()) return;

        // If outside patrol range with no target, return to patrol area
        if (!isWithinPatrolRange() && getTarget() == null) {
            if (patrolAnchor != null) {
                getNavigation().moveTo(
                        patrolAnchor.getX() + 0.5, patrolAnchor.getY(), patrolAnchor.getZ() + 0.5, 1.2);
            }
        }

        // Scan for intruders in the colony area every 3 seconds
        if (this.srpTicks % 60 == 0 && colony.getColonyCenter() != null) {
            BlockPos center = colony.getColonyCenter();
            float scanRange = colony.getColonyRadius();

            AABB scanArea = new AABB(
                    center.getX() - scanRange, center.getY() - 16, center.getZ() - scanRange,
                    center.getX() + scanRange, center.getY() + 16, center.getZ() + scanRange);

            List<LivingEntity> intruders = this.level().getEntitiesOfClass(
                    LivingEntity.class, scanArea,
                    e -> e.isAlive() && !(e instanceof EntityParasiteBase));

            if (!intruders.isEmpty()) {
                // Alert the colony
                colony.incrementAlertLevel();

                // Target the nearest intruder if we don't have a target
                if (getTarget() == null) {
                    LivingEntity nearest = null;
                    double nearestDist = Double.MAX_VALUE;
                    for (LivingEntity intruder : intruders) {
                        double dist = distanceToSqr(intruder);
                        if (dist < nearestDist) {
                            nearestDist = dist;
                            nearest = intruder;
                        }
                    }
                    if (nearest != null) {
                        setTarget(nearest);
                    }
                }
            }
        }

        // Retreat to colony center when heavily damaged
        if (getHealth() < getMaxHealth() * 0.3F && colony.getColonyCenter() != null) {
            BlockPos center = colony.getColonyCenter();
            double distToCenter = blockPosition().distSqr(center);
            if (distToCenter > 25.0) { // More than 5 blocks from center
                getNavigation().moveTo(
                        center.getX() + 0.5, center.getY(), center.getZ() + 0.5, 1.4);
                // Clear target while retreating for survival
                if (getHealth() < getMaxHealth() * 0.2F) {
                    setTarget(null);
                }
            }
        }
    }

    /**
     * When a deterrent is hurt, alert the colony.
     */
    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);

        if (result && source.getEntity() instanceof LivingEntity attacker) {
            ColonyComponent colony = getColonyComponent();
            if (colony != null) {
                colony.incrementAlertLevel();
                // Alert other colony members
                if (colony.getColonyCenter() != null) {
                    BlockPos center = colony.getColonyCenter();
                    float alertRange = colony.getColonyRadius();
                    AABB alertArea = new AABB(
                            center.getX() - alertRange, center.getY() - 16, center.getZ() - alertRange,
                            center.getX() + alertRange, center.getY() + 16, center.getZ() + alertRange);

                    List<EntityParasiteBase> members = this.level().getEntitiesOfClass(
                            EntityParasiteBase.class, alertArea,
                            p -> p.isAlive() && p != this && p.getColonyComponent() != null &&
                                 p.getColonyComponent().isColonyMember());

                    for (EntityParasiteBase member : members) {
                        if (member.getTarget() == null) {
                            member.setTarget(attacker);
                        }
                    }
                }
            }
        }

        return result;
    }

    // ========== Push Resistance ==========

    /**
     * Deterrents are harder to push due to their anchoring behavior.
     */
    @Override
    public boolean isPushable() {
        return false;
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for deterrent-tier parasites.
     * Moderate health, good knockback resistance, decent speed for patrolling.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 36.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.FOLLOW_RANGE, 36.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }

    // ========== NBT Save/Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (patrolAnchor != null) {
            tag.putInt("PatrolAnchorX", patrolAnchor.getX());
            tag.putInt("PatrolAnchorY", patrolAnchor.getY());
            tag.putInt("PatrolAnchorZ", patrolAnchor.getZ());
        }
        tag.putFloat("PatrolRadius", patrolRadius);
        tag.putFloat("DebuffAuraRange", debuffAuraRange);
        tag.putInt("DebuffAuraInterval", debuffAuraInterval);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("PatrolAnchorX")) {
            patrolAnchor = new BlockPos(
                    tag.getInt("PatrolAnchorX"), tag.getInt("PatrolAnchorY"), tag.getInt("PatrolAnchorZ"));
        }
        patrolRadius = tag.contains("PatrolRadius") ? tag.getFloat("PatrolRadius") : 16.0F;
        debuffAuraRange = tag.contains("DebuffAuraRange") ? tag.getFloat("DebuffAuraRange") : 8.0F;
        debuffAuraInterval = tag.contains("DebuffAuraInterval") ? tag.getInt("DebuffAuraInterval") : 60;
    }
}
