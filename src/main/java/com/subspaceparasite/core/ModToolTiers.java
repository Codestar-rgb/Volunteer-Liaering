package com.subspaceparasite.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.TierSortingRegistry;

import com.subspaceparasite.SubspaceParasite;

import java.util.List;

/**
 * Custom tool tiers for the SubspaceParasite mod.
 */
public class ModToolTiers {

    /**
     * Hijacked Iron tier - slightly better than iron.
     * Level 2, 1561 durability, 7.0 speed, 2.5 damage, 14 enchantability.
     */
    public static final Tier HIJACKED_IRON = TierSortingRegistry.registerTier(
            new Tier() {
                @Override
                public int getLevel() {
                    return 2;
                }

                @Override
                public int getUses() {
                    return 1561;
                }

                @Override
                public float getSpeed() {
                    return 7.0f;
                }

                @Override
                public float getAttackDamageBonus() {
                    return 2.5f;
                }

                @Override
                public int getEnchantmentValue() {
                    return 14;
                }

                @Override
                public Ingredient getRepairIngredient() {
                    return Ingredient.EMPTY; // Will be updated when repair material is registered
                }
            },
            new ResourceLocation(SubspaceParasite.MOD_ID, "hijacked_iron"),
            List.of(net.minecraft.world.item.Tiers.IRON),
            List.of(net.minecraft.world.item.Tiers.DIAMOND)
    );

    private ModToolTiers() {
        // Utility class - prevent instantiation
    }
}
