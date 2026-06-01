package com.subspaceparasite.core;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

/**
 * Custom armor materials for the SubspaceParasite mod.
 */
public class ModArmorMaterials {

    // Living Armor - organic parasite armor
    // NOTE: In 1.20.1, ArmorItem.Type ordinals are BOOTS(0), LEGGINGS(1), CHESTPLATE(2), HELMET(3)
    // Durability multipliers must follow this order, matching vanilla: {boots, leggings, chestplate, helmet}
    public static final ArmorMaterial LIVING_ARMOR = new ArmorMaterial() {
        private final int[] DURABILITY = {11, 16, 15, 13}; // boots, leggings, chestplate, helmet
        private final int[] DEFENSE = {2, 5, 6, 2}; // boots=2, leggings=5, chestplate=6, helmet=2

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return DURABILITY[type.ordinal()] * 25;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return DEFENSE[type.ordinal()];
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public net.minecraft.sounds.SoundEvent getEquipSound() {
            return ModSounds.ITEM_LIVING_ARMOR_EQUIP.get();
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY; // Will be updated when items are registered
        }

        @Override
        public String getName() {
            return "living_armor";
        }

        @Override
        public float getToughness() {
            return 1.0f;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0f;
        }
    };

    // Sentient Armor - evolved parasite armor
    // NOTE: In 1.20.1, ArmorItem.Type ordinals are BOOTS(0), LEGGINGS(1), CHESTPLATE(2), HELMET(3)
    public static final ArmorMaterial SENTIENT_ARMOR = new ArmorMaterial() {
        private final int[] DURABILITY = {11, 16, 15, 13}; // boots, leggings, chestplate, helmet
        private final int[] DEFENSE = {3, 6, 8, 3}; // boots=3, leggings=6, chestplate=8, helmet=3

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return DURABILITY[type.ordinal()] * 33;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return DEFENSE[type.ordinal()];
        }

        @Override
        public int getEnchantmentValue() {
            return 18;
        }

        @Override
        public net.minecraft.sounds.SoundEvent getEquipSound() {
            return ModSounds.ITEM_SENTIENT_ARMOR_EQUIP.get();
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY; // Will be updated when items are registered
        }

        @Override
        public String getName() {
            return "sentient_armor";
        }

        @Override
        public float getToughness() {
            return 2.0f;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.05f;
        }
    };

    // Hijacked Iron Armor - corrupted iron armor with custom textures
    // NOTE: In 1.20.1, ArmorItem.Type ordinals are BOOTS(0), LEGGINGS(1), CHESTPLATE(2), HELMET(3)
    // DEFENSE was incorrectly {2,6,5,2} (1.12.2 order: helmet,chestplate,leggings,boots)
    // Corrected to {2,5,6,2} (1.20.1 order: boots,leggings,chestplate,helmet)
    public static final ArmorMaterial HIJACKED_IRON = new ArmorMaterial() {
        private final int[] DURABILITY = {11, 16, 15, 13}; // boots, leggings, chestplate, helmet
        private final int[] DEFENSE = {2, 5, 6, 2}; // boots=2, leggings=5, chestplate=6, helmet=2 (same as vanilla iron)

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return DURABILITY[type.ordinal()] * 15;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return DEFENSE[type.ordinal()];
        }

        @Override
        public int getEnchantmentValue() {
            return 9;
        }

        @Override
        public net.minecraft.sounds.SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY; // Will be updated when items are registered
        }

        @Override
        public String getName() {
            return "hijacked_iron";
        }

        @Override
        public float getToughness() {
            return 0.0f;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0f;
        }
    };

    private ModArmorMaterials() {
        // Utility class - prevent instantiation
    }
}
