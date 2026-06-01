package com.subspaceparasite.common.entity.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Component handling combat-related logic for parasite entities.
 * <p>
 * Manages:
 * <ul>
 *   <li>Per-damage-source-type adaptation tracking ({@link #adaptationMap})</li>
 *   <li>Damage cap system that limits incoming burst damage</li>
 *   <li>Adaptation gain rate scaling with EvoPhase and ADAPTATION_SPEED gene</li>
 *   <li>Adaptation decay over time when not taking that damage type</li>
 *   <li>Adaptation data sync to client for visual effects</li>
 *   <li>Warning nearby parasites about damage source types</li>
 * </ul>
 * <p>
 * Adaptation system: When hit by the same type of damage source repeatedly,
 * this component builds up resistance to that damage type. Adaptation is tracked
 * per damage-source message ID (fire, projectile, melee, magic, fall, etc.) in
 * {@link #adaptationMap}, and decays over time when not being hit.
 * <p>
 * In the original SRP, parasites gradually gained resistance to damage types
 * they'd been hit by. This port preserves that per-source-type behavior while
 * integrating it with the gene and phase systems.
 */
public class CombatComponent {

    private final EntityParasiteBase parasite;

    // ========== Damage Cap System ==========

    private float lastDamageAmount;
    private int consecutiveHits;
    private long lastHitTime;

    // ========== Per-Source Adaptation System ==========

    /** Per-damage-source adaptation levels. Key = DamageSource.getMsgId(), Value = resistance 0.0–1.0. */
    private final Map<String, Float> adaptationMap = new HashMap<>();

    /** Ticks since last damage taken, used to trigger adaptation decay. */
    private int adaptationDecayTimer = 0;

    /** How many ticks before adaptation starts decaying. Read from config. */
    private int adaptationDecayDelay;

    /** How much adaptation is lost per tick once decay begins. */
    private static final float ADAPTATION_DECAY_RATE = 0.002F;

    /** Base adaptation gain per hit from the same source type. Read from config. */
    private float adaptationGainPerHit;

    /** Track the last damage source msgId for consecutive-hit detection. */
    private String lastDamageSourceMsgId = null;
    private int sameSourceHitCount = 0;

    // ========== Sync Support ==========

    /** Dirty flag: set when adaptation data changes and needs syncing to client. */
    private boolean adaptationDirty = false;

    public CombatComponent(EntityParasiteBase parasite) {
        this.parasite = parasite;
        this.adaptationDecayDelay = (int) ModConfigSystems.getAdaptationDecayDelay();
        this.adaptationGainPerHit = (float) ModConfigSystems.getAdaptationGainPerHit();
    }

    // ========== Tick ==========

    public void tick() {
        // Adaptation decay
        if (!adaptationMap.isEmpty()) {
            if (adaptationDecayTimer > 0) {
                adaptationDecayTimer--;
            } else {
                decayAdaptation();
            }
        }

        // Reset combat state after inactivity
        if (consecutiveHits > 0 && parasite.srpTicks - lastHitTime > 100) {
            consecutiveHits = 0;
        }
    }

    // ========== Damage Cap ==========

    /**
     * Apply the damage cap system.
     * Damage cap = maxHealth / damageCapValue, with adaptation-based reduction
     * applied BEFORE the cap.
     */
    public float applyDamageCap(DamageSource source, float amount) {
        if (!ModConfigSystems.isDamageCapEnabled()) return amount;

        // Apply adaptation-based damage reduction from per-source map
        if (source != null) {
            float adaptation = getAdaptationLevel(source);
            if (adaptation > 0.0F) {
                float reduction = amount * adaptation;
                amount -= reduction;
                if (amount < 0) amount = 0;
            }
        }

        // Apply damage cap
        int capValue = parasite.getDamageCapValue();
        if (capValue <= 0) return amount;

        float maxHealth = parasite.getMaxHealth();
        float capDamage = maxHealth / capValue;

        float remainder = maxHealth % capValue;
        capDamage += remainder * 0.5F / capValue;

        if (amount > capDamage) amount = capDamage;

        return Math.max(amount, 0.0F);
    }

    // ========== Adaptation - On Hurt ==========

    /**
     * Called when the parasite takes damage. Builds adaptation resistance
     * for the damage source type.
     * <p>
     * Adaptation gain rate is:
     * <ol>
     *   <li>Base rate from config ({@code adaptationGainPerHit})</li>
     *   <li>Multiplied by ADAPTATION_SPEED gene value</li>
     *   <li>Scaled by EvoPhase (higher phases = faster adaptation)</li>
     *   <li>Boosted by consecutive hits from the same source</li>
     * </ol>
     */
    public void onHurt(DamageSource source, float amount) {
        lastDamageAmount = amount;
        lastHitTime = parasite.srpTicks;
        consecutiveHits++;

        if (source == null) return;

        String msgId = source.getMsgId();

        // Track consecutive hits from same source type
        if (msgId.equals(lastDamageSourceMsgId)) {
            sameSourceHitCount++;
        } else {
            sameSourceHitCount = 1;
            lastDamageSourceMsgId = msgId;
        }

        // Build adaptation for this damage source type
        buildAdaptation(source);

        // Reset decay timer
        adaptationDecayTimer = adaptationDecayDelay;

        // Warn nearby parasites about this damage source type
        warnNearbyParasites(source);
    }

    /**
     * Builds adaptation resistance for the given damage source.
     * Gain rate scales with EvoPhase and ADAPTATION_SPEED gene.
     */
    private void buildAdaptation(DamageSource source) {
        String key = source.getMsgId();
        float current = adaptationMap.getOrDefault(key, 0.0F);
        float maxAdapt = (float) ModConfigSystems.getAdaptationMax();

        if (current >= maxAdapt) return; // Already at cap

        // Base gain rate from config
        float gainRate = (float) ModConfigSystems.getAdaptationGainPerHit();

        // Multiply by ADAPTATION_SPEED gene value
        float adaptationSpeed = parasite.getGeneFloat(GeneType.ADAPTATION_SPEED.getIndex());
        if (adaptationSpeed > 0.0F) {
            gainRate *= adaptationSpeed;
        }

        // Scale with current world phase (higher phases = faster adaptation)
        EvoPhase worldPhase = EntityParasiteBase.getCurrentPhase(parasite.level());
        float phaseMult = 1.0F + worldPhase.getPhaseNumber() * 0.15F;
        gainRate *= phaseMult;

        // Bonus for consecutive hits from same source type
        if (sameSourceHitCount > 2) {
            gainRate *= (1.0F + (sameSourceHitCount - 2) * 0.1F);
        }

        // Apply gain
        float newLevel = Math.min(maxAdapt, current + gainRate);
        adaptationMap.put(key, newLevel);
        adaptationDirty = true;
    }

    /**
     * Decays all adaptation levels by ADAPTATION_DECAY_RATE per tick.
     * Removes entries that have decayed to zero.
     */
    private void decayAdaptation() {
        adaptationMap.entrySet().removeIf(entry -> {
            float newValue = entry.getValue() - ADAPTATION_DECAY_RATE;
            if (newValue <= 0.0F) {
                return true;
            }
            entry.setValue(newValue);
            return false;
        });
        adaptationDirty = true;
    }

    // ========== Adaptation - Warning ==========

    /**
     * Warns nearby parasites about a damage source type this entity has
     * adapted to. Adapted entities share awareness with nearby parasites
     * so they can prepare or change tactics.
     */
    private void warnNearbyParasites(DamageSource source) {
        float adaptation = getAdaptationLevel(source);
        if (adaptation < 0.25F) return; // Only warn if significantly adapted

        // Only warn periodically to avoid spam
        if (parasite.srpTicks % 60 != 0) return;

        String msgId = source.getMsgId();
        double range = 16.0 + adaptation * 16.0; // 16-32 blocks based on adaptation

        net.minecraft.world.phys.AABB area = parasite.getBoundingBox().inflate(range);
        List<EntityParasiteBase> nearby = parasite.level().getEntitiesOfClass(
                EntityParasiteBase.class, area,
                p -> p != parasite && p.isAlive());

        for (EntityParasiteBase nearbyParasite : nearby) {
            // Transfer partial adaptation awareness to nearby parasites
            CombatComponent otherCombat = nearbyParasite.getCombatComponent();
            if (otherCombat != null) {
                float current = otherCombat.adaptationMap.getOrDefault(msgId, 0.0F);
                float sharedAdaptation = adaptation * 0.1F; // Share 10% of adaptation
                if (sharedAdaptation > current) {
                    otherCombat.adaptationMap.put(msgId, Math.min(
                            (float) ModConfigSystems.getAdaptationMax(), sharedAdaptation));
                    otherCombat.adaptationDirty = true;
                }
            }
        }
    }

    // ========== Adaptation - Query ==========

    /**
     * Returns the current adaptation level for the given damage source.
     *
     * @param source the damage source to query
     * @return adaptation level from 0.0 (no resistance) to max (configurable)
     */
    public float getAdaptationLevel(DamageSource source) {
        if (source == null) return 0.0F;
        return adaptationMap.getOrDefault(source.getMsgId(), 0.0F);
    }

    /**
     * Returns the adaptation level for a specific damage source message ID.
     *
     * @param msgId the damage source message ID
     * @return adaptation level from 0.0 to max
     */
    public float getAdaptationLevel(String msgId) {
        return adaptationMap.getOrDefault(msgId, 0.0F);
    }

    /**
     * Returns the overall average adaptation level across all tracked sources.
     */
    public float getAverageAdaptationLevel() {
        if (adaptationMap.isEmpty()) return 0.0F;
        float total = 0.0F;
        for (float level : adaptationMap.values()) {
            total += level;
        }
        return total / adaptationMap.size();
    }

    /**
     * Returns the total adaptation level across all tracked sources.
     * Used for determining if this entity is "highly adapted".
     */
    public float getTotalAdaptationLevel() {
        float total = 0.0F;
        for (float level : adaptationMap.values()) {
            total += level;
        }
        return total;
    }

    /**
     * Returns true if this entity has significant adaptation to any damage type.
     * Used by AI to decide whether to use adapted tactics (e.g., teleport away from magic).
     */
    public boolean isAdapted() {
        return getAverageAdaptationLevel() > 0.25F;
    }

    /**
     * Returns true if this entity has high adaptation to the specific damage source type.
     * Used by AI to decide tactic changes (e.g., avoid the attacker type).
     *
     * @param source the damage source to check
     * @return true if adaptation to this source exceeds 0.4
     */
    public boolean isHighlyAdaptedTo(DamageSource source) {
        return getAdaptationLevel(source) > 0.4F;
    }

    /**
     * Returns the adaptation map for sync/rendering purposes.
     *
     * @return unmodifiable view of the adaptation map
     */
    public Map<String, Float> getAdaptationMap() {
        return Map.copyOf(adaptationMap);
    }

    /**
     * Returns whether adaptation data has changed and needs to be synced.
     */
    public boolean isAdaptationDirty() {
        return adaptationDirty;
    }

    /**
     * Clears the adaptation dirty flag after syncing.
     */
    public void clearAdaptationDirty() {
        adaptationDirty = false;
    }

    /**
     * Sets adaptation data from a sync packet (client-side).
     */
    public void setAdaptationMap(Map<String, Float> map) {
        adaptationMap.clear();
        adaptationMap.putAll(map);
    }

    public int getConsecutiveHits() { return consecutiveHits; }

    // ========== Adaptation - Reset ==========

    /**
     * Clears all adaptation tracking, resetting resistance to zero for all
     * damage sources. Called on certain status changes or evolution events.
     */
    public void resetAdaptation() {
        adaptationMap.clear();
        adaptationDecayTimer = 0;
        sameSourceHitCount = 0;
        lastDamageSourceMsgId = null;
        consecutiveHits = 0;
        adaptationDirty = true;
    }

    // ========== Adaptation - Tactics ==========

    /**
     * Determines if this entity should avoid the attacker based on high adaptation
     * to their damage type. Highly adapted entities may flee or use special tactics.
     * <p>
     * In the original SRP, entities with high magic adaptation would teleport away.
     * This method provides the hook for that behavior.
     *
     * @param attacker the attacking entity
     * @param source   the damage source used
     * @return true if the entity should use avoidance tactics
     */
    public boolean shouldAvoidAttacker(LivingEntity attacker, DamageSource source) {
        if (!isHighlyAdaptedTo(source)) return false;
        // Magic users → teleport away; Projectile users → close distance; Melee → keep distance
        String msgId = source.getMsgId();
        float adaptation = getAdaptationLevel(source);
        // At very high adaptation (>0.6), actively avoid that damage type
        return adaptation > 0.6F;
    }

    // ========== NBT Save/Load ==========

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        // Save adaptation map
        CompoundTag adaptTag = new CompoundTag();
        for (Map.Entry<String, Float> entry : adaptationMap.entrySet()) {
            adaptTag.putFloat(entry.getKey(), entry.getValue());
        }
        tag.put("AdaptationMap", adaptTag);
        tag.putInt("AdaptationDecayTimer", adaptationDecayTimer);
        tag.putInt("SameSourceHitCount", sameSourceHitCount);
        if (lastDamageSourceMsgId != null) {
            tag.putString("LastDamageSourceMsgId", lastDamageSourceMsgId);
        }
        tag.putFloat("LastDamageAmount", lastDamageAmount);
        tag.putInt("ConsecutiveHits", consecutiveHits);
        tag.putLong("LastHitTime", lastHitTime);

        return tag;
    }

    public void load(CompoundTag tag) {
        // Load adaptation map
        adaptationMap.clear();
        if (tag.contains("AdaptationMap")) {
            CompoundTag adaptTag = tag.getCompound("AdaptationMap");
            for (String key : adaptTag.getAllKeys()) {
                adaptationMap.put(key, adaptTag.getFloat(key));
            }
        }
        adaptationDecayTimer = tag.getInt("AdaptationDecayTimer");
        sameSourceHitCount = tag.getInt("SameSourceHitCount");
        lastDamageSourceMsgId = tag.contains("LastDamageSourceMsgId")
                ? tag.getString("LastDamageSourceMsgId") : null;
        lastDamageAmount = tag.getFloat("LastDamageAmount");
        consecutiveHits = tag.getInt("ConsecutiveHits");
        lastHitTime = tag.getLong("LastHitTime");
    }
}
