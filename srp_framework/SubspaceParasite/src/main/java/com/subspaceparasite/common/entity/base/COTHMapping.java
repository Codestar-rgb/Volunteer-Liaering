package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.api.parasite.ParasiteType;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Static mapping of entity types to their COTH conversion results.
 * When a COTH-infected entity converts, it spawns the mapped parasite
 * entity type as a replacement.
 */
public class COTHMapping {

    private static final Map<EntityType<?>, EntityType<?>> conversionMap = new HashMap<>();
    private static final Map<Class<?>, ParasiteType> categoryMap = new HashMap<>();

    /**
     * Initialize the COTH mapping.
     * Called during mod initialization after entity types are registered.
     */
    public static void init() {
        conversionMap.clear();
        categoryMap.clear();

        // Category-based mappings using the API's ParasiteType enum
        categoryMap.put(Pig.class, ParasiteType.SIM_PIG);
        categoryMap.put(Sheep.class, ParasiteType.SIM_SHEEP);
        categoryMap.put(Cow.class, ParasiteType.SIM_COW);
        categoryMap.put(Chicken.class, ParasiteType.SIM_BIGSPIDER); // Placeholder
        categoryMap.put(Wolf.class, ParasiteType.SIM_WOLF);
        categoryMap.put(Horse.class, ParasiteType.SIM_HORSE);
        categoryMap.put(Villager.class, ParasiteType.SIM_VILLAGER);

        categoryMap.put(Zombie.class, ParasiteType.SIM_HUMAN);
        categoryMap.put(Skeleton.class, ParasiteType.HI_SKELETON);
        categoryMap.put(Spider.class, ParasiteType.SIM_BIGSPIDER);
        categoryMap.put(Creeper.class, ParasiteType.SIM_BIGSPIDER); // Placeholder
        categoryMap.put(EnderMan.class, ParasiteType.SIM_ENDERMAN);
        categoryMap.put(Blaze.class, ParasiteType.HI_BLAZE);
        categoryMap.put(Silverfish.class, ParasiteType.BUGLIN);
        categoryMap.put(Slime.class, ParasiteType.MOVING_FLESH);

        categoryMap.put(IronGolem.class, ParasiteType.HI_GOLEM);
        categoryMap.put(Player.class, ParasiteType.SIM_ADVENTURER);

        categoryMap.put(Animal.class, ParasiteType.SIM_PIG);
    }

    public static void registerConversion(EntityType<?> source, EntityType<?> result) {
        conversionMap.put(source, result);
    }

    /**
     * Get the conversion result for a given source entity type.
     * First checks the explicit conversion map, then falls back to
     * category-based lookup via categoryMap and superclass traversal.
     *
     * @param sourceType the entity type to look up
     * @return the parasite entity type to convert to, or null if no mapping exists
     */
    public static EntityType<?> getConversionResult(EntityType<?> sourceType) {
        // Check explicit conversion map first
        EntityType<?> specific = conversionMap.get(sourceType);
        if (specific != null) return specific;

        // Fallback: no EntityType-level mapping available from categoryMap alone
        // (categoryMap maps Class<?> to ParasiteType, not EntityType)
        // The caller should use getConversionParasiteType() for class-based lookup
        return null;
    }

    /**
     * Get the ParasiteType that a given entity class would convert to.
     */
    public static ParasiteType getConversionParasiteType(Class<?> entityClass) {
        String direct = null;
        ParasiteType directResult = categoryMap.get(entityClass);
        if (directResult != null) return directResult;

        Class<?> superClass = entityClass.getSuperclass();
        while (superClass != null && superClass != Object.class) {
            ParasiteType superResult = categoryMap.get(superClass);
            if (superResult != null) return superResult;
            superClass = superClass.getSuperclass();
        }

        return null;
    }

    public static boolean hasConversion(EntityType<?> sourceType) {
        return conversionMap.containsKey(sourceType);
    }

    public static Map<EntityType<?>, EntityType<?>> getConversionMap() {
        return Collections.unmodifiableMap(conversionMap);
    }

    public static int getConversionCount() {
        return conversionMap.size();
    }

    public static void removeConversion(EntityType<?> sourceType) {
        conversionMap.remove(sourceType);
    }
}
