package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.world.ModWorldData;
import com.subspaceparasite.common.world.SRPDifficultySetting;
import com.subspaceparasite.config.ModConfigSystems;
import com.subspaceparasite.core.ModEffects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Component handling infection (COTH) logic for parasite entities.
 * Manages COTH aura spreading, infection conversion, infection resistance,
 * and spread cooldowns.
 */
public class InfectionComponent {

    /**
     * Static check: is the given LivingEntity infected with COTH?
     * Used by target selection to filter out already-infected entities.
     *
     * @param living the entity to check
     * @return true if the entity has the COTH/wither effect
     */
    public static boolean isInfected(LivingEntity living) {
        if (living == null) return false;
        if (living instanceof EntityParasiteBase) return true;
        // Check for COTH effect (wither is used as the COTH indicator)
        return living.hasEffect(MobEffects.WITHER);
    }

    private final EntityParasiteBase parasite;

    private int spreadCooldown;
    private int spreadCooldownMax;
    private float spreadRange;
    private float spreadChance;
    private int conversionCheckTimer;
    private float conversionChance;
    private boolean auraEnabled;
    private float auraRange;
    private int auraTickInterval;
    private int cothKills;

    private static final int DEFAULT_SPREAD_COOLDOWN = 100;
    private static final float DEFAULT_SPREAD_RANGE = 4.0F;
    private static final float DEFAULT_SPREAD_CHANCE = 0.15F;
    private static final float DEFAULT_CONVERSION_CHANCE = 0.05F;
    private static final float DEFAULT_AURA_RANGE = 6.0F;
    private static final int DEFAULT_AURA_INTERVAL = 40;

    public InfectionComponent(EntityParasiteBase parasite) {
        this.parasite = parasite;
        this.spreadCooldown = 0;
        this.spreadCooldownMax = DEFAULT_SPREAD_COOLDOWN;
        this.spreadRange = DEFAULT_SPREAD_RANGE;
        this.spreadChance = DEFAULT_SPREAD_CHANCE;
        this.conversionCheckTimer = 0;
        this.conversionChance = DEFAULT_CONVERSION_CHANCE;
        this.auraEnabled = false;
        this.auraRange = DEFAULT_AURA_RANGE;
        this.auraTickInterval = DEFAULT_AURA_INTERVAL;
        this.cothKills = 0;
    }

    public void tick() {
        if (parasite.level().isClientSide) return;
        if (spreadCooldown > 0) spreadCooldown--;
        if (conversionCheckTimer > 0) conversionCheckTimer--;
        if (auraEnabled && parasite.srpTicks % auraTickInterval == 0) {
            doAuraSpread();
        }
    }

    public boolean spreadCOTH(LivingEntity target) {
        if (!canSpread()) return false;
        if (isImmuneToCOTH(target)) return false;

        float chance = getEffectiveSpreadChance(target);
        if (parasite.getRandom().nextFloat() < chance) {
            applyCOTH(target);
            spreadCooldown = spreadCooldownMax;
            return true;
        }
        return false;
    }

    public boolean canSpread() {
        return spreadCooldown <= 0 && ModConfigSystems.isInfectionEnabled();
    }

    protected void applyCOTH(LivingEntity target) {
        int duration = ModConfigSystems.getCOTHDuration();
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, duration, 0));

        EvoPhase phase = parasite.getPhaseCreated();
        // Phase 3 and above: COTH also slows targets
        if (phase.isAtLeast(EvoPhase.PHASE_3)) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 1));
        }
        // Phase 4: COTH also weakens targets
        if (phase.isAtLeast(EvoPhase.PHASE_4)) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, 0));
        }
    }

    protected boolean isImmuneToCOTH(LivingEntity target) {
        if (target instanceof EntityParasiteBase) return true;
        if (target.canChangeDimensions() && target.getMaxHealth() > 100.0F) return true;
        if (target instanceof Player player && (player.isCreative() || player.isSpectator())) return true;
        if (ModConfigSystems.isEntityCOTHImmune(target.getType())) return true;
        return false;
    }

    protected float getEffectiveSpreadChance(LivingEntity target) {
        float chance = spreadChance * (float) ModConfigSystems.getInfectionSpreadMultiplier();
        chance += parasite.getPhaseCreated().getPhaseNumber() * 0.02F;
        // Apply difficulty multiplier from SRPDifficultySetting
        if (parasite.level() instanceof ServerLevel serverLevel) {
            SRPDifficultySetting difficulty = ModWorldData.get(serverLevel).getSRPDifficulty();
            chance *= difficulty.getInfectionRateMultiplier();
        }
        float healthRatio = target.getHealth() / target.getMaxHealth();
        if (healthRatio < 0.3F) chance *= 2.0F;
        float armor = target.getArmorValue();
        chance *= Math.max(0.1F, 1.0F - armor * 0.02F);
        return Math.min(chance, 1.0F);
    }

    protected void doAuraSpread() {
        if (!auraEnabled || !canSpread()) return;
        AABB area = parasite.getBoundingBox().inflate(auraRange);
        List<LivingEntity> nearby = parasite.level().getEntitiesOfClass(
                LivingEntity.class, area,
                e -> e != parasite && !(e instanceof EntityParasiteBase) && e.isAlive());

        for (LivingEntity entity : nearby) {
            if (!isImmuneToCOTH(entity)) {
                float chance = getEffectiveSpreadChance(entity) * 0.3F;
                if (parasite.getRandom().nextFloat() < chance) {
                    applyCOTH(entity);
                    spreadCooldown = spreadCooldownMax;
                    break;
                }
            }
        }
    }

    public boolean checkConversion(LivingEntity target) {
        if (conversionCheckTimer > 0) return false;
        if (!ModConfigSystems.isInfectionEnabled()) return false;
        if (isImmuneToCOTH(target)) return false;
        if (!target.hasEffect(MobEffects.WITHER)) return false;

        float chance = conversionChance * (float) ModConfigSystems.getInfectionConversionMultiplier();
        float healthRatio = target.getHealth() / target.getMaxHealth();
        if (healthRatio < 0.2F) chance *= 3.0F;
        else if (healthRatio < 0.5F) chance *= 1.5F;
        chance += parasite.getPhaseCreated().getPhaseNumber() * 0.01F;
        conversionCheckTimer = 200;

        if (parasite.getRandom().nextFloat() < chance) {
            return convertEntity(target);
        }
        return false;
    }

    protected boolean convertEntity(LivingEntity target) {
        if (!(parasite.level() instanceof ServerLevel serverLevel)) return false;

        EntityType<?> conversionType = COTHMapping.getConversionResult(target);
        if (conversionType == null) return false;

        if (conversionType.create(serverLevel) instanceof EntityParasiteBase converted) {
            converted.moveTo(target.getX(), target.getY(), target.getZ(),
                    target.getYRot(), target.getXRot());
            converted.setOwner(this.parasite);
            converted.setColonySpawned(true);

            if (serverLevel.addFreshEntity(converted)) {
                target.kill();
                cothKills++;
                return true;
            }
        }
        return false;
    }

    public void onHurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof LivingEntity attacker) {
            if (parasite.getRandom().nextFloat() < 0.05F) {
                spreadCOTH(attacker);
            }
        }
    }

    public void onDeath(DamageSource source) {
        if (ModConfigSystems.isDeathBurstEnabled()) {
            AABB area = parasite.getBoundingBox().inflate((float) ModConfigSystems.getDeathBurstRange());
            List<LivingEntity> nearby = parasite.level().getEntitiesOfClass(
                    LivingEntity.class, area,
                    e -> e != parasite && !(e instanceof EntityParasiteBase));
            for (LivingEntity entity : nearby) {
                if (!isImmuneToCOTH(entity)) applyCOTH(entity);
            }
        }
    }

    public int getSpreadCooldown() { return spreadCooldown; }
    public void setSpreadCooldown(int ticks) { this.spreadCooldown = ticks; }
    public void setSpreadCooldownMax(int ticks) { this.spreadCooldownMax = ticks; }
    public void setSpreadRange(float range) { this.spreadRange = range; }
    public void setSpreadChance(float chance) { this.spreadChance = chance; }
    public void setConversionChance(float chance) { this.conversionChance = chance; }
    public void setAuraEnabled(boolean enabled) { this.auraEnabled = enabled; }
    public void setAuraRange(float range) { this.auraRange = range; }
    public int getCothKills() { return cothKills; }
    public boolean isAuraEnabled() { return auraEnabled; }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("SpreadCooldown", spreadCooldown);
        tag.putInt("SpreadCooldownMax", spreadCooldownMax);
        tag.putFloat("SpreadRange", spreadRange);
        tag.putFloat("SpreadChance", spreadChance);
        tag.putInt("ConversionCheckTimer", conversionCheckTimer);
        tag.putFloat("ConversionChance", conversionChance);
        tag.putBoolean("AuraEnabled", auraEnabled);
        tag.putFloat("AuraRange", auraRange);
        tag.putInt("AuraTickInterval", auraTickInterval);
        tag.putInt("CothKills", cothKills);
        return tag;
    }

    public void load(CompoundTag tag) {
        spreadCooldown = tag.getInt("SpreadCooldown");
        spreadCooldownMax = tag.getInt("SpreadCooldownMax");
        spreadRange = tag.getFloat("SpreadRange");
        spreadChance = tag.getFloat("SpreadChance");
        conversionCheckTimer = tag.getInt("ConversionCheckTimer");
        conversionChance = tag.getFloat("ConversionChance");
        auraEnabled = tag.getBoolean("AuraEnabled");
        auraRange = tag.getFloat("AuraRange");
        auraTickInterval = tag.getInt("AuraTickInterval");
        cothKills = tag.getInt("CothKills");
    }
}
