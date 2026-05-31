package com.subspaceparasite.common.item.tool;

import com.subspaceparasite.config.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.UUID;

/**
 * Base class for melee weapons with reach and fear mechanics.
 * Ported from original SRP WeaponToolMeleeBase.
 */
public class WeaponToolMeleeBase extends Item implements IHaveReach {
    private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    private static final UUID REACH_DISTANCE_MODIFIER = UUID.fromString("845DB903-7DFB-44C9-8E3C-3C4E3F55D3CB");
    
    private final float addReach;
    private final float attackDamage;
    private final double attackSpeed;
    private final boolean hasFearEffect;
    private final byte toolId;
    private final Tier material;
    
    public WeaponToolMeleeBase(Tier material, String name, double attackSpeed, float reach, float attackDamage, 
                                boolean hasFearEffect, byte toolId, Properties properties) {
        super(properties.stacksTo(1));
        this.material = material;
        this.attackSpeed = attackSpeed;
        this.addReach = reach;
        this.attackDamage = attackDamage;
        this.hasFearEffect = hasFearEffect;
        this.toolId = toolId;
    }
    
    @Override
    public float getReach() {
        return this.addReach;
    }
    
    /**
     * Get the next tier weapon when upgrade condition is met.
     * Override in sentient weapons to return sentient version.
     */
    public Item getNextTier() {
        return null;
    }
    
    @Override
    public void onEquipTick(ItemStack stack, Level level, Entity entity, int slotIndex, EquipmentSlot equipmentSlot, 
                           int enchantmentLevel, Player player) {
        if (!level.isClientSide()) {
            // Fear effect: apply Prey effect periodically if configured
            if (this.hasFearEffect && ModConfig.WEAPON_FEAR_ENABLED.get() && entity instanceof LivingEntity living) {
                if (level.random.nextInt(100) == 0 && entity.tickCount % 40 == 0) {
                    // TODO: Apply Prey effect when potion system is implemented
                }
            }
            
            // Check for weapon upgrade based on kill count
            if (entity.tickCount % 80 == 0) {
                CompoundTag tag = stack.getTag();
                if (tag != null && this.getNextTier() != null) {
                    int kills = tag.getInt("srpkills");
                    int threshold = ModConfig.WEAPON_UPGRADE_KILLS_THRESHOLD.get();
                    
                    if (kills > threshold) {
                        tag.putInt("srpkills", 0);
                        stack.shrink(1);
                        
                        ItemStack nextStack = new ItemStack(this.getNextTier(), 1);
                        nextStack.setTag(tag);
                        
                        if (player != null) {
                            player.setItemInHand(player.getUsedItemHand(), nextStack);
                        }
                        
                        // Lightning effect on upgrade
                        if (ModConfig.WEAPON_UPGRADE_LIGHTNING.get()) {
                            level.lightningStrike(entity.blockPosition());
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean flag = super.hurtEnemy(stack, target, attacker);
        
        if (flag && target.getHealth() <= 0) {
            CompoundTag tag = stack.getOrCreateTag();
            int currentKills = tag.getInt("srpkills");
            int healthValue = (int) target.getMaxHealth();
            tag.putInt("srpkills", currentKills + healthValue);
            stack.setTag(tag);
        }
        
        return flag;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("srpkills")) {
            int kills = tag.getInt("srpkills");
            tooltip.add(Component.literal("---> " + kills).withStyle(ChatFormatting.BLUE));
            tooltip.add(Component.literal("  ").withStyle(ChatFormatting.BLUE));
        }
        
        if (this.hasFearEffect) {
            tooltip.add(Component.literal("§bHas Fear Effect").withStyle(ChatFormatting.AQUA));
        }
    }
    
    @Override
    public Rarity getRarity(ItemStack stack) {
        return this.hasFearEffect ? Rarity.RARE : Rarity.COMMON;
    }
    
    /**
     * Get attribute modifiers for the weapon.
     * Includes damage, attack speed, and custom reach.
     */
    @Override
    public java.util.multimap<net.minecraft.world.entity.ai.attributes.attribute, attributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        java.util.multimap<net.minecraft.world.entity.ai.attributes.attribute, attributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        
        if (slot == EquipmentSlot.MAINHAND) {
            multimap.clear();
            multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
            multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed, AttributeModifier.Operation.ADDITION));
            
            // Custom reach - only if config allows
            if (ModConfig.WEAPON_CUSTOM_REACH.get()) {
                multimap.put(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(
                    REACH_DISTANCE_MODIFIER, "custom_reach", this.addReach, AttributeModifier.Operation.ADDITION));
                multimap.put(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(
                    REACH_DISTANCE_MODIFIER, "custom_reach", this.addReach, AttributeModifier.Operation.ADDITION));
            }
        }
        
        return multimap;
    }
}
