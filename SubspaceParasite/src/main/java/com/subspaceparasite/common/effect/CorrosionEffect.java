package com.subspaceparasite.common.effect;

import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Corrosion effect - degrades armor and reduces protection.
 * <p>
 * This harmful effect damages armor durability and reduces armor value,
 * making entities increasingly vulnerable over time.
 * </p>
 * 
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public class CorrosionEffect extends CustomMobEffect {
    
    /** Armor reduction per amplifier level */
    private static final float ARMOR_REDUCTION_PER_LEVEL = 1.0F;
    
    /** Armor toughness reduction per level */
    private static final float TOUGHNESS_REDUCTION_PER_LEVEL = 0.5F;
    
    /** Durability damage per tick */
    private static final int DURABILITY_DAMAGE_PER_TICK = 1;
    
    /** Tick interval for durability damage */
    private static final int DURABILITY_INTERVAL = 40; // Every 2 seconds
    
    public CorrosionEffect() {
        super(MobEffectCategory.HARMFUL, 0x006400, 4, true);
    }
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        
        int tickCount = entity.tickCount;
        
        // Damage armor durability periodically
        if (tickCount % DURABILITY_INTERVAL == 0) {
            damageArmor(entity, amplifier);
        }
        
        // Apply armor reduction modifiers
        applyArmorReduction(entity, amplifier);
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Damages equipped armor pieces.
     * 
     * @param entity the affected entity
     * @param amplifier the effect amplifier
     */
    protected void damageArmor(LivingEntity entity, int amplifier) {
        int damageAmount = DURABILITY_DAMAGE_PER_TICK * (amplifier + 1);
        
        // Damage each armor piece
        for (net.minecraft.world.item.ItemStack armorStack : entity.getArmorSlots()) {
            if (!armorStack.isEmpty() && armorStack.isDamageableItem()) {
                // Apply corrosion damage
                int actualDamage = Math.min(damageAmount, armorStack.getMaxDamage() / 10);
                armorStack.setDamageValue(armorStack.getDamageValue() + actualDamage);
                
                // Break armor if fully damaged
                if (armorStack.getDamageValue() >= armorStack.getMaxDamage()) {
                    onArmorBroken(entity, armorStack);
                }
            }
        }
    }
    
    /**
     * Applies armor value reduction through attribute modifiers.
     * 
     * @param entity the affected entity
     * @param amplifier the effect amplifier
     */
    protected void applyArmorReduction(LivingEntity entity, int amplifier) {
        updateArmorModifier(entity, amplifier);
        updateToughnessModifier(entity, amplifier);
    }
    
    /**
     * Updates the armor reduction modifier.
     * 
     * @param entity the affected entity
     * @param amplifier the effect amplifier
     */
    protected void updateArmorModifier(LivingEntity entity, int amplifier) {
        java.util.UUID modifierId = java.util.UUID.fromString("corrosion-armor-reduction-0001");
        
        // Remove old modifier
        entity.getAttribute(Attributes.ARMOR).removeModifier(modifierId);
        
        // Calculate armor reduction (negative value)
        float armorReduction = -ARMOR_REDUCTION_PER_LEVEL * (amplifier + 1);
        
        // Add new modifier
        AttributeModifier modifier = new AttributeModifier(
            modifierId,
            "Corrosion armor reduction",
            armorReduction,
            AttributeModifier.Operation.ADDITION
        );
        
        entity.getAttribute(Attributes.ARMOR).addTransientModifier(modifier);
    }
    
    /**
     * Updates the armor toughness reduction modifier.
     * 
     * @param entity the affected entity
     * @param amplifier the effect amplifier
     */
    protected void updateToughnessModifier(LivingEntity entity, int amplifier) {
        java.util.UUID modifierId = java.util.UUID.fromString("corrosion-toughness-reduction-0001");
        
        // Remove old modifier
        entity.getAttribute(Attributes.ARMOR_TOUGHNESS).removeModifier(modifierId);
        
        // Calculate toughness reduction (negative value)
        float toughnessReduction = -TOUGHNESS_REDUCTION_PER_LEVEL * (amplifier + 1);
        
        // Add new modifier
        AttributeModifier modifier = new AttributeModifier(
            modifierId,
            "Corrosion toughness reduction",
            toughnessReduction,
            AttributeModifier.Operation.ADDITION
        );
        
        entity.getAttribute(Attributes.ARMOR_TOUGHNESS).addTransientModifier(modifier);
    }
    
    /**
     * Called when an armor piece breaks due to corrosion.
     * 
     * @param entity the affected entity
     * @param brokenStack the broken armor stack
     */
    protected void onArmorBroken(LivingEntity entity, net.minecraft.world.item.ItemStack brokenStack) {
        // Spawn particles and play sound
        if (!entity.level().isClientSide) {
            // Particle effects will be added in client handler
            // Sound: BlockAnvilBreak
            entity.level().levelEvent(null, 1039, entity.blockPosition(), 0);
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                         net.minecraft.world.entity.ai.attributes.AttributeMap attributes,
                                         int amplifier) {
        // Clean up modifiers when effect ends
        entity.getAttribute(Attributes.ARMOR).removeModifier(
            java.util.UUID.fromString("corrosion-armor-reduction-0001")
        );
        entity.getAttribute(Attributes.ARMOR_TOUGHNESS).removeModifier(
            java.util.UUID.fromString("corrosion-toughness-reduction-0001")
        );
        
        super.removeAttributeModifiers(entity, attributes, amplifier);
    }
    
    /**
     * Gets the estimated time until armor breaks.
     * 
     * @param armorDurability current armor durability
     * @param amplifier effect amplifier
     * @return ticks until break
     */
    public static int getTicksUntilArmorBreak(int armorDurability, int amplifier) {
        int damagePerTick = DURABILITY_DAMAGE_PER_TICK * (amplifier + 1);
        if (damagePerTick <= 0) return Integer.MAX_VALUE;
        
        return armorDurability / damagePerTick;
    }
}
