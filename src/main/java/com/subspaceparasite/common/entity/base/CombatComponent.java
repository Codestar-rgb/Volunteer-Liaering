package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Component handling combat-related logic for parasite entities.
 * Manages damage caps, adaptation tracking, and damage resistance building.
 */
public class CombatComponent {

    private final EntityParasiteBase parasite;

    // Damage cap system
    private float lastDamageAmount;
    private DamageSource lastDamageSource;
    private int sameSourceHitCount;

    // Adaptation system
    private float adaptationLevel;      // 0.0 - 1.0, reduces incoming damage
    private int adaptationDecayTimer;   // Ticks until adaptation starts decaying
    private static final int ADAPTATION_DECAY_DELAY = 200;
    private static final float ADAPTATION_DECAY_RATE = 0.005F;
    private static final float ADAPTATION_GAIN_PER_HIT = 0.03F;
    private static final float MAX_ADAPTATION = 0.75F;

    // Attack tracking
    private int consecutiveHits;
    private long lastHitTime;

    public CombatComponent(EntityParasiteBase parasite) {
        this.parasite = parasite;
        this.adaptationLevel = 0.0F;
        this.adaptationDecayTimer = 0;
        this.sameSourceHitCount = 0;
        this.lastDamageAmount = 0.0F;
        this.consecutiveHits = 0;
        this.lastHitTime = 0;
    }

    public void tick() {
        if (adaptationLevel > 0) {
            if (adaptationDecayTimer > 0) {
                adaptationDecayTimer--;
            } else {
                adaptationLevel -= ADAPTATION_DECAY_RATE;
                if (adaptationLevel < 0) adaptationLevel = 0;
            }
        }

        if (consecutiveHits > 0 && parasite.srpTicks - lastHitTime > 100) {
            consecutiveHits = 0;
        }
    }

    /**
     * Apply the damage cap system.
     * Damage cap = maxHealth / damageCap + remainder * 0.5
     */
    public float applyDamageCap(DamageSource source, float amount) {
        if (!ModConfigSystems.isDamageCapEnabled()) return amount;

        int capValue = parasite.getDamageCapValue();
        if (capValue <= 0) return amount;

        float maxHealth = parasite.getMaxHealth();
        float capDamage = maxHealth / capValue;

        float remainder = maxHealth % capValue;
        capDamage += remainder * 0.5F / capValue;

        if (adaptationLevel > 0) {
            float reduction = amount * adaptationLevel;
            amount -= reduction;
        }

        if (amount > capDamage) amount = capDamage;

        return Math.max(amount, 0.0F);
    }

    public void onHurt(DamageSource source, float amount) {
        lastDamageAmount = amount;
        lastHitTime = parasite.srpTicks;
        consecutiveHits++;

        if (isSameSourceType(source)) {
            sameSourceHitCount++;
            if (sameSourceHitCount > 2) {
                float maxAdapt = (float) ModConfigSystems.getAdaptationMax();
                adaptationLevel = Math.min(maxAdapt,
                        adaptationLevel + ADAPTATION_GAIN_PER_HIT * sameSourceHitCount);
            }
        } else {
            sameSourceHitCount = 1;
        }

        lastDamageSource = source;
        adaptationDecayTimer = ModConfigSystems.getAdaptationDecayDelay();

        if (source.getEntity() instanceof LivingEntity) {
            // Combat experience from being hurt (NOT a kill count)
            // Removed: parasite.addKillCount(amount * 0.01);
        }
    }

    private boolean isSameSourceType(DamageSource source) {
        if (lastDamageSource == null) return false;
        // Compare damage source types by their message ID (most reliable in MC 1.20.1)
        return source.getMsgId().equals(lastDamageSource.getMsgId());
    }

    public float getAdaptationLevel() { return adaptationLevel; }
    public int getConsecutiveHits() { return consecutiveHits; }
    public boolean isAdapted() { return adaptationLevel > 0.25F; }

    public void resetAdaptation() {
        adaptationLevel = 0;
        sameSourceHitCount = 0;
        consecutiveHits = 0;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("AdaptationLevel", adaptationLevel);
        tag.putInt("AdaptationDecayTimer", adaptationDecayTimer);
        tag.putInt("SameSourceHitCount", sameSourceHitCount);
        tag.putFloat("LastDamageAmount", lastDamageAmount);
        tag.putInt("ConsecutiveHits", consecutiveHits);
        tag.putLong("LastHitTime", lastHitTime);
        return tag;
    }

    public void load(CompoundTag tag) {
        adaptationLevel = tag.getFloat("AdaptationLevel");
        adaptationDecayTimer = tag.getInt("AdaptationDecayTimer");
        sameSourceHitCount = tag.getInt("SameSourceHitCount");
        lastDamageAmount = tag.getFloat("LastDamageAmount");
        consecutiveHits = tag.getInt("ConsecutiveHits");
        lastHitTime = tag.getLong("LastHitTime");
    }
}
