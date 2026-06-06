package com.subspaceparasite.common.item.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

/**
 * Base class for melee weapons with reach and fear mechanics.
 * Ported from original SRP WeaponToolMeleeBase for Forge 1.20.1.
 */
public class WeaponToolMeleeBase extends Item implements IHaveReach {
    private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    private final float addReach;
    private final float attackDamage;
    private final double attackSpeed;
    protected final boolean hasFearEffect;
    private final byte toolId;
    private final Tier material;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public WeaponToolMeleeBase(Tier material, String name, double attackSpeed, float reach, float attackDamage,
                                boolean hasFearEffect, byte toolId, Properties properties) {
        super(properties.stacksTo(1).durability(material.getUses()));
        this.material = material;
        this.attackSpeed = attackSpeed;
        this.addReach = reach;
        this.attackDamage = attackDamage;
        this.hasFearEffect = hasFearEffect;
        this.toolId = toolId;

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage + material.getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public float getReach() {
        return this.addReach;
    }

    @Override
    public int getEnchantmentValue() {
        return this.material.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairMaterial) {
        return this.material.getRepairIngredient().test(repairMaterial) || super.isValidRepairItem(stack, repairMaterial);
    }

    /**
     * Get the next tier weapon when upgrade condition is met.
     * Override in sentient weapons to return sentient version.
     */
    public Item getNextTier() {
        return null;
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
        }

        if (this.hasFearEffect) {
            tooltip.add(Component.literal("Has Fear Effect").withStyle(ChatFormatting.AQUA));
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }
}
