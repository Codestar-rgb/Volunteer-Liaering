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
    public static final ArmorMaterial LIVING_ARMOR = new ArmorMaterial() {
        private final int[] DURABILITY = {13, 15, 16, 11};
        private final int[] DEFENSE = {2, 5, 6, 2};

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
    public static final ArmorMaterial SENTIENT_ARMOR = new ArmorMaterial() {
        private final int[] DURABILITY = {13, 15, 16, 11};
        private final int[] DEFENSE = {3, 6, 8, 3};

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

    private ModArmorMaterials() {
        // Utility class - prevent instantiation
    }
}
