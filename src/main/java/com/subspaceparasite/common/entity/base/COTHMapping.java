package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.core.ModEntities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Static mapping of entity types to their COTH conversion results.
 * <p>
 * When a COTH-infected entity converts, it spawns the mapped parasite
 * entity type as a replacement. The mapping uses three layers:
 * <ol>
 *   <li>{@code conversionMap} — direct EntityType→EntityType lookup (fastest path)</li>
 *   <li>{@code categoryMap} — Class→ParasiteType for categorization and fallback</li>
 *   <li>{@code parasiteTypeEntityMap} — ParasiteType→EntityType for resolving fallback conversions</li>
 * </ol>
 * <p>
 * Conversion tiers follow the original Scape and Run: Parasites design:
 * <ul>
 *   <li>Common passive/hostile mobs → Infected (mimic stage)</li>
 *   <li>Iron Golem → Feral (fully transformed, no disguise)</li>
 *   <li>Special-ability mobs (Witch, Pillager, Evoker, Ravager) → Hijacked (co-opted abilities)</li>
 * </ul>
 */
public class COTHMapping {

    /** Direct EntityType → EntityType conversion mapping (primary lookup). */
    private static final Map<EntityType<?>, EntityType<?>> conversionMap = new HashMap<>();

    /** Entity class → ParasiteType for categorization and fallback resolution. */
    private static final Map<Class<?>, ParasiteType> categoryMap = new HashMap<>();

    /** ParasiteType → EntityType for resolving category-based fallback conversions. */
    private static final Map<ParasiteType, EntityType<?>> parasiteTypeEntityMap = new HashMap<>();

    /** EvolutionPath → EntityType default mapping for evolution transformations. */
    private static final Map<EvolutionPath, EntityType<?>> evolutionPathEntityMap = new HashMap<>();

    /**
     * Initialize the COTH mapping.
     * Called during mod initialization after entity types are registered.
     */
    public static void init() {
        conversionMap.clear();
        categoryMap.clear();
        parasiteTypeEntityMap.clear();
        evolutionPathEntityMap.clear();

        // ================================================================
        //  PARASITE TYPE → ENTITY TYPE RESOLVER
        //  Used by getConversionResult() as a fallback when no direct
        //  EntityType mapping exists but a ParasiteType is known.
        // ================================================================
        // ── Infected ──
        parasiteTypeEntityMap.put(ParasiteType.SIM_CHICKEN,     ModEntities.INFECTED_CHICKEN.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_COW,         ModEntities.INFECTED_COW.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_PIG,         ModEntities.INFECTED_PIG.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_SHEEP,       ModEntities.INFECTED_SHEEP.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_WOLF,        ModEntities.INFECTED_WOLF.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_VILLAGER,    ModEntities.INFECTED_VILLAGER.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_HUMAN,       ModEntities.INFECTED_HUMAN.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_ZOMBIE,      ModEntities.INFECTED_ZOMBIE.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_SKELETON,    ModEntities.INFECTED_SKELETON.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_SPIDER,      ModEntities.INFECTED_SPIDER.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_BIGSPIDER,   ModEntities.INFECTED_CAVE_SPIDER.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_CREEPER,     ModEntities.INFECTED_CREEPER.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_ENDERMAN,    ModEntities.INFECTED_ENDERMAN.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_HORSE,       ModEntities.INFECTED_HORSE.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_BAT,         ModEntities.INFECTED_BAT.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_BLAZE,       ModEntities.INFECTED_BLAZE.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_WITCH,       ModEntities.INFECTED_WITCH.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_RAVAGER,     ModEntities.INFECTED_RAVAGER.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_PILLAGER,    ModEntities.INFECTED_PILLAGER.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_EVOKER,      ModEntities.INFECTED_EVOKER.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_IRONGOLEM,   ModEntities.INFECTED_IRON_GOLEM.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_SNOWGOLEM,   ModEntities.INFECTED_SNOW_GOLEM.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_BEAR,        ModEntities.INFECTED_POLAR_BEAR.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_GHAST,       ModEntities.INFECTED_GHAST.get());
        parasiteTypeEntityMap.put(ParasiteType.SIM_PHANTOM,     ModEntities.INFECTED_PHANTOM.get());
        parasiteTypeEntityMap.put(ParasiteType.GNAT,            ModEntities.INFECTED_CHICKEN.get()); // small flying fallback
        parasiteTypeEntityMap.put(ParasiteType.SIM_SQUID,       ModEntities.INFECTED_BAT.get());     // aquatic→flying fallback

        // ── Feral ──
        parasiteTypeEntityMap.put(ParasiteType.FER_COW,         ModEntities.FERAL_COW.get());
        parasiteTypeEntityMap.put(ParasiteType.FER_HUMAN,       ModEntities.FERAL_HUMAN.get());
        parasiteTypeEntityMap.put(ParasiteType.FER_CREEPER,     ModEntities.FERAL_CREEPER.get());
        parasiteTypeEntityMap.put(ParasiteType.FER_SKELETON,    ModEntities.FERAL_SKELETON.get());
        parasiteTypeEntityMap.put(ParasiteType.FER_ZOMBIE,      ModEntities.FERAL_ZOMBIE.get());
        parasiteTypeEntityMap.put(ParasiteType.FER_SPIDER,      ModEntities.FERAL_SPIDER.get());
        parasiteTypeEntityMap.put(ParasiteType.FER_ENDERMAN,    ModEntities.FERAL_ENDERMAN.get());
        parasiteTypeEntityMap.put(ParasiteType.FER_WOLF,        ModEntities.FERAL_WOLF.get());
        parasiteTypeEntityMap.put(ParasiteType.FER_IRONGOLEM,   ModEntities.FERAL_IRON_GOLEM.get());

        // ── Hijacked ──
        parasiteTypeEntityMap.put(ParasiteType.HI_SKELETON,     ModEntities.HIJACKED_SKELETON.get());
        parasiteTypeEntityMap.put(ParasiteType.HI_CREEPER,      ModEntities.HIJACKED_CREEPER.get());
        parasiteTypeEntityMap.put(ParasiteType.HI_ZOMBIE,       ModEntities.HIJACKED_ZOMBIE.get());
        parasiteTypeEntityMap.put(ParasiteType.HI_SPIDER,       ModEntities.HIJACKED_SPIDER.get());
        parasiteTypeEntityMap.put(ParasiteType.HI_ENDERMAN,     ModEntities.HIJACKED_ENDERMAN.get());
        parasiteTypeEntityMap.put(ParasiteType.HI_WITCH,        ModEntities.HIJACKED_WITCH.get());
        parasiteTypeEntityMap.put(ParasiteType.HI_PILLAGER,     ModEntities.HIJACKED_PILLAGER.get());
        parasiteTypeEntityMap.put(ParasiteType.HI_EVOKER,       ModEntities.HIJACKED_EVOKER.get());
        parasiteTypeEntityMap.put(ParasiteType.HI_RAVAGER,      ModEntities.HIJACKED_RAVAGER.get());

        // ── Marauder (special infected) ──
        parasiteTypeEntityMap.put(ParasiteType.MAR_ENDERMAN,    ModEntities.INFECTED_ENDERMAN.get());
        parasiteTypeEntityMap.put(ParasiteType.MAR_COW,         ModEntities.INFECTED_COW.get());
        parasiteTypeEntityMap.put(ParasiteType.MAR_VILLAGER,    ModEntities.INFECTED_VILLAGER.get());
        parasiteTypeEntityMap.put(ParasiteType.MAR_HUMAN,       ModEntities.INFECTED_HUMAN.get());
        parasiteTypeEntityMap.put(ParasiteType.MAR_SHEEP,       ModEntities.INFECTED_SHEEP.get());
        parasiteTypeEntityMap.put(ParasiteType.MAR_BEAR,        ModEntities.INFECTED_POLAR_BEAR.get());

        // ================================================================
        //  CATEGORY MAP — Class → ParasiteType
        //  Used by getConversionParasiteType() for categorization and
        //  as a fallback path in getConversionResult().
        // ================================================================

        // ── Passive Animals ──
        categoryMap.put(Chicken.class,    ParasiteType.SIM_CHICKEN);
        categoryMap.put(Cow.class,        ParasiteType.SIM_COW);
        categoryMap.put(Pig.class,        ParasiteType.SIM_PIG);
        categoryMap.put(Sheep.class,      ParasiteType.SIM_SHEEP);
        categoryMap.put(Wolf.class,       ParasiteType.SIM_WOLF);
        categoryMap.put(Horse.class,      ParasiteType.SIM_HORSE);
        categoryMap.put(Llama.class,      ParasiteType.SIM_HORSE); // Llama → horse-like parasite
        categoryMap.put(Villager.class,   ParasiteType.SIM_VILLAGER);
        categoryMap.put(MushroomCow.class,ParasiteType.SIM_COW);   // Mooshroom → cow-like parasite
        categoryMap.put(PolarBear.class,  ParasiteType.SIM_BEAR);
        categoryMap.put(Panda.class,      ParasiteType.SIM_BEAR);  // Panda → bear-like parasite
        categoryMap.put(Fox.class,        ParasiteType.SIM_WOLF);  // Fox → wolf-like parasite
        categoryMap.put(Bee.class,        ParasiteType.GNAT);      // Bee → small flying parasite
        categoryMap.put(Bat.class,        ParasiteType.SIM_BAT);
        categoryMap.put(Rabbit.class,     ParasiteType.SIM_PIG);   // Rabbit → small passive parasite
        categoryMap.put(Ocelot.class,     ParasiteType.SIM_WOLF);  // Ocelot → wolf-like parasite
        categoryMap.put(Parrot.class,     ParasiteType.GNAT);      // Parrot → small flying parasite
        categoryMap.put(Turtle.class,     ParasiteType.SIM_PIG);   // Turtle → small passive parasite

        // ── Hostile Mobs ──
        categoryMap.put(Zombie.class,          ParasiteType.SIM_ZOMBIE);
        categoryMap.put(Husk.class,            ParasiteType.SIM_ZOMBIE);
        categoryMap.put(Drowned.class,         ParasiteType.SIM_ZOMBIE);
        categoryMap.put(Skeleton.class,        ParasiteType.SIM_SKELETON);
        categoryMap.put(Stray.class,           ParasiteType.SIM_SKELETON);
        categoryMap.put(WitherSkeleton.class,  ParasiteType.SIM_SKELETON);
        categoryMap.put(Spider.class,          ParasiteType.SIM_SPIDER);
        categoryMap.put(CaveSpider.class,      ParasiteType.SIM_BIGSPIDER);
        categoryMap.put(Creeper.class,         ParasiteType.SIM_CREEPER);
        categoryMap.put(EnderMan.class,        ParasiteType.SIM_ENDERMAN);
        categoryMap.put(Blaze.class,           ParasiteType.SIM_BLAZE);
        categoryMap.put(Ghast.class,           ParasiteType.SIM_GHAST);
        categoryMap.put(Phantom.class,         ParasiteType.SIM_PHANTOM);
        categoryMap.put(Witch.class,           ParasiteType.HI_WITCH);
        categoryMap.put(Pillager.class,        ParasiteType.HI_PILLAGER);
        categoryMap.put(Evoker.class,          ParasiteType.HI_EVOKER);
        categoryMap.put(Ravager.class,         ParasiteType.HI_RAVAGER);
        categoryMap.put(Warden.class,          ParasiteType.SIM_ENDERMAN); // Warden → powerful humanoid parasite

        // ── Golems ──
        categoryMap.put(IronGolem.class,   ParasiteType.FER_IRONGOLEM);
        categoryMap.put(SnowGolem.class,   ParasiteType.SIM_SNOWGOLEM);

        // ── Players (no direct conversion — infection debuffs only) ──
        categoryMap.put(Player.class,      ParasiteType.SIM_ADVENTURER);

        // ── Generic fallbacks for unhandled Animal subclasses ──
        categoryMap.put(Animal.class,      ParasiteType.SIM_PIG);

        // ── Illager fallback ──
        categoryMap.put(AbstractIllager.class, ParasiteType.HI_PILLAGER);

        // ================================================================
        //  CONVERSION MAP — EntityType → EntityType
        //  Direct, fast lookup for all vanilla mobs that have parasite
        //  equivalents. This is the primary path used by
        //  getConversionResult().
        // ================================================================

        // ── Passive Animals → Infected ──
        conversionMap.put(EntityType.CHICKEN,      ModEntities.INFECTED_CHICKEN.get());
        conversionMap.put(EntityType.COW,          ModEntities.INFECTED_COW.get());
        conversionMap.put(EntityType.PIG,          ModEntities.INFECTED_PIG.get());
        conversionMap.put(EntityType.SHEEP,        ModEntities.INFECTED_SHEEP.get());
        conversionMap.put(EntityType.WOLF,         ModEntities.INFECTED_WOLF.get());
        conversionMap.put(EntityType.HORSE,        ModEntities.INFECTED_HORSE.get());
        conversionMap.put(EntityType.LLAMA,        ModEntities.INFECTED_LLAMA.get());
        conversionMap.put(EntityType.VILLAGER,     ModEntities.INFECTED_VILLAGER.get());
        conversionMap.put(EntityType.BAT,          ModEntities.INFECTED_BAT.get());
        conversionMap.put(EntityType.BEE,          ModEntities.INFECTED_BEE.get());
        conversionMap.put(EntityType.FOX,          ModEntities.INFECTED_FOX.get());
        conversionMap.put(EntityType.PANDA,        ModEntities.INFECTED_PANDA.get());
        conversionMap.put(EntityType.POLAR_BEAR,   ModEntities.INFECTED_POLAR_BEAR.get());
        conversionMap.put(EntityType.MOOSHROOM,    ModEntities.INFECTED_MOOSHROOM.get());
        conversionMap.put(EntityType.RABBIT,       ModEntities.INFECTED_PIG.get());  // No InfectedRabbit; fallback to pig
        conversionMap.put(EntityType.OCELOT,       ModEntities.INFECTED_WOLF.get()); // No InfectedOcelot; fallback to wolf
        conversionMap.put(EntityType.PARROT,       ModEntities.INFECTED_CHICKEN.get()); // No InfectedParrot; fallback to chicken
        conversionMap.put(EntityType.TURTLE,       ModEntities.INFECTED_PIG.get());  // No InfectedTurtle; fallback to pig

        // ── Hostile Mobs → Infected ──
        conversionMap.put(EntityType.ZOMBIE,           ModEntities.INFECTED_ZOMBIE.get());
        conversionMap.put(EntityType.HUSK,             ModEntities.INFECTED_HUSK.get());
        conversionMap.put(EntityType.DROWNED,          ModEntities.INFECTED_DROWNED.get());
        conversionMap.put(EntityType.ZOMBIE_VILLAGER,  ModEntities.INFECTED_VILLAGER.get());
        conversionMap.put(EntityType.SKELETON,         ModEntities.INFECTED_SKELETON.get());
        conversionMap.put(EntityType.STRAY,            ModEntities.INFECTED_STRAY.get());
        conversionMap.put(EntityType.WITHER_SKELETON,  ModEntities.INFECTED_WITHER_SKELETON.get());
        conversionMap.put(EntityType.SPIDER,           ModEntities.INFECTED_SPIDER.get());
        conversionMap.put(EntityType.CAVE_SPIDER,      ModEntities.INFECTED_CAVE_SPIDER.get());
        conversionMap.put(EntityType.CREEPER,          ModEntities.INFECTED_CREEPER.get());
        conversionMap.put(EntityType.ENDERMAN,         ModEntities.INFECTED_ENDERMAN.get());
        conversionMap.put(EntityType.BLAZE,            ModEntities.INFECTED_BLAZE.get());
        conversionMap.put(EntityType.GHAST,            ModEntities.INFECTED_GHAST.get());
        conversionMap.put(EntityType.PHANTOM,          ModEntities.INFECTED_PHANTOM.get());
        conversionMap.put(EntityType.SILVERFISH,       ModEntities.INFECTED_SPIDER.get());  // No InfectedSilverfish; fallback to spider
        conversionMap.put(EntityType.ENDERMITE,       ModEntities.INFECTED_SPIDER.get());  // No InfectedEndermite; fallback to spider

        // ── Golems → Feral/Special ──
        conversionMap.put(EntityType.IRON_GOLEM,   ModEntities.FERAL_IRON_GOLEM.get());
        conversionMap.put(EntityType.SNOW_GOLEM,   ModEntities.INFECTED_SNOW_GOLEM.get());

        // ── Special Hostiles → Hijacked ──
        // In the original SRP, mobs with special abilities are hijacked
        // (their abilities are co-opted by the parasite) rather than
        // simply infected. These mobs retain their special powers but
        // serve the parasite collective.
        conversionMap.put(EntityType.WITCH,        ModEntities.HIJACKED_WITCH.get());
        conversionMap.put(EntityType.PILLAGER,     ModEntities.HIJACKED_PILLAGER.get());
        conversionMap.put(EntityType.EVOKER,       ModEntities.HIJACKED_EVOKER.get());
        conversionMap.put(EntityType.RAVAGER,      ModEntities.HIJACKED_RAVAGER.get());

        // ── Warden → Infected (1.19+ mob) ──
        conversionMap.put(EntityType.WARDEN,       ModEntities.INFECTED_WARDEN.get());

        // ── Aquatic → Infected (1.13+ mobs) ──
        conversionMap.put(EntityType.DOLPHIN,      ModEntities.INFECTED_POLAR_BEAR.get()); // No InfectedDolphin; fallback
        conversionMap.put(EntityType.SQUID,        ModEntities.INFECTED_BAT.get());  // No InfectedSquid; closest flying fallback
        conversionMap.put(EntityType.GLOW_SQUID,   ModEntities.INFECTED_BAT.get());  // Same as squid

        // ── Nether mobs → Infected ──
        conversionMap.put(EntityType.ZOMBIFIED_PIGLIN, ModEntities.INFECTED_ZOMBIE.get()); // No InfectedPiglin; fallback to zombie

        // ── 1.19+ mobs → Infected ──
        conversionMap.put(EntityType.FROG,         ModEntities.INFECTED_PIG.get());    // No InfectedFrog; fallback
        conversionMap.put(EntityType.TADPOLE,      ModEntities.INFECTED_PIG.get());    // No InfectedTadpole; fallback
        conversionMap.put(EntityType.GOAT,         ModEntities.INFECTED_COW.get());    // No InfectedGoat; fallback to cow
        conversionMap.put(EntityType.AXOLOTL,      ModEntities.INFECTED_PIG.get());    // No InfectedAxolotl; fallback
        conversionMap.put(EntityType.ALLAY,        ModEntities.INFECTED_BAT.get());    // No InfectedAllay; fallback

        // ================================================================
        //  EVOLUTION PATH → ENTITY TYPE DEFAULT MAPPING
        //  Used by EvolutionComponent.attemptEvolution() to determine
        //  which entity type to spawn when a parasite evolves to the
        //  next tier. Each path maps to a "default" entity type for
        //  that tier. Subclasses of EntityParasiteBase can override
        //  getNextEvolutionEntityType() for species-specific evolution.
        // ================================================================
        evolutionPathEntityMap.put(EvolutionPath.INFECTED,         ModEntities.INFECTED_HUMAN.get());
        evolutionPathEntityMap.put(EvolutionPath.SPECIAL_INFECTED, ModEntities.INFECTED_ENDERMAN.get());
        evolutionPathEntityMap.put(EvolutionPath.FERAL,            ModEntities.FERAL_HUMAN.get());
        evolutionPathEntityMap.put(EvolutionPath.HIJACKED,         ModEntities.HIJACKED_ZOMBIE.get());
        evolutionPathEntityMap.put(EvolutionPath.INBORN,           ModEntities.CRUDE_MOVING_FLESH.get()); // MovingFlesh is the basic inborn
        evolutionPathEntityMap.put(EvolutionPath.CRUDE,            ModEntities.CRUDE_HOST.get());
        evolutionPathEntityMap.put(EvolutionPath.PRIMITIVE,        ModEntities.PRIMITIVE_BANO.get());
        evolutionPathEntityMap.put(EvolutionPath.ADAPTED,          ModEntities.ADAPTED_BANO.get());
        evolutionPathEntityMap.put(EvolutionPath.NEXUS,            ModEntities.BECKON_COMMON.get());
        evolutionPathEntityMap.put(EvolutionPath.DETERRENT,        ModEntities.DETERRENT_SENTRY.get());
        evolutionPathEntityMap.put(EvolutionPath.PURE,             ModEntities.PURE_FLAM.get());
        evolutionPathEntityMap.put(EvolutionPath.PREEMINENT,       ModEntities.PREEMINENT_MARAUDER.get());
        evolutionPathEntityMap.put(EvolutionPath.ANCIENT,          ModEntities.ANCIENT_DREADNOUGHT.get());
        evolutionPathEntityMap.put(EvolutionPath.DERIVED,          ModEntities.DERIVED_CRAWLER.get());
        evolutionPathEntityMap.put(EvolutionPath.ABOMINATION,      ModEntities.ABOMINATION_AMALGAM.get());
    }

    /**
     * Register a custom conversion mapping.
     *
     * @param source the vanilla entity type to convert from
     * @param result the parasite entity type to convert to
     */
    public static void registerConversion(EntityType<?> source, EntityType<?> result) {
        conversionMap.put(source, result);
    }

    /**
     * Get the conversion result for a given source entity type.
     * Only checks the direct conversion map — does not perform class-based
     * fallback (since EntityType does not expose its entity class in MC 1.20.1).
     * For full resolution including category fallback, use
     * {@link #getConversionResult(LivingEntity)}.
     *
     * @param sourceType the entity type to look up
     * @return the parasite entity type to convert to, or null if no mapping exists
     */
    @Nullable
    public static EntityType<?> getConversionResult(EntityType<?> sourceType) {
        return conversionMap.get(sourceType);
    }

    /**
     * Get the conversion result for a given living entity.
     * <p>
     * Resolution order:
     * <ol>
     *   <li>Direct {@code conversionMap} lookup (EntityType → EntityType)</li>
     *   <li>Category-based fallback: resolve the entity's class to a
     *       ParasiteType via {@code categoryMap}, then resolve that
     *       ParasiteType to an EntityType via {@code parasiteTypeEntityMap}</li>
     * </ol>
     * This overload is preferred because it supports modded mobs that
     * extend vanilla entity classes but have no direct EntityType mapping.
     *
     * @param entity the living entity to look up
     * @return the parasite entity type to convert to, or null if no mapping exists
     */
    @Nullable
    public static EntityType<?> getConversionResult(LivingEntity entity) {
        // 1. Check explicit conversion map first (fastest path)
        EntityType<?> specific = conversionMap.get(entity.getType());
        if (specific != null) return specific;

        // 2. Category-based fallback: resolve entity class → ParasiteType → EntityType
        // This handles modded mobs that extend vanilla classes but have no
        // direct EntityType mapping.
        ParasiteType pType = getConversionParasiteType(entity.getClass());
        if (pType != null) {
            EntityType<?> resolved = parasiteTypeEntityMap.get(pType);
            if (resolved != null) return resolved;
        }

        return null;
    }

    /**
     * Get the ParasiteType that a given entity class would convert to.
     * Traverses the class hierarchy from most specific to least specific,
     * returning the first match found.
     *
     * @param entityClass the entity class to look up
     * @return the matching ParasiteType, or null if no mapping exists
     */
    @Nullable
    public static ParasiteType getConversionParasiteType(Class<?> entityClass) {
        // Direct class match
        ParasiteType directResult = categoryMap.get(entityClass);
        if (directResult != null) return directResult;

        // Walk up the class hierarchy for superclass matches
        Class<?> superClass = entityClass.getSuperclass();
        while (superClass != null && superClass != Object.class) {
            ParasiteType superResult = categoryMap.get(superClass);
            if (superResult != null) return superResult;
            superClass = superClass.getSuperclass();
        }

        return null;
    }

    /**
     * Resolve a ParasiteType to its registered EntityType.
     *
     * @param parasiteType the parasite type to resolve
     * @return the corresponding EntityType, or null if not registered
     */
    @Nullable
    public static EntityType<?> getEntityTypeForParasiteType(ParasiteType parasiteType) {
        return parasiteTypeEntityMap.get(parasiteType);
    }

    /**
     * Resolve an EvolutionPath to its default registered EntityType.
     * Used by the evolution system to determine what entity type to
     * spawn when a parasite evolves to the next tier.
     *
     * @param path the evolution path to look up
     * @return the default EntityType for that path, or null if not registered
     */
    @Nullable
    public static EntityType<?> getEntityTypeForEvolutionPath(EvolutionPath path) {
        return evolutionPathEntityMap.get(path);
    }

    /**
     * Register a custom evolution path mapping.
     *
     * @param path the evolution path
     * @param type the entity type to map to
     */
    public static void registerEvolutionPathMapping(EvolutionPath path, EntityType<?> type) {
        evolutionPathEntityMap.put(path, type);
    }

    /**
     * Check whether a conversion mapping exists for the given entity type.
     *
     * @param sourceType the entity type to check
     * @return true if a direct conversion mapping exists
     */
    public static boolean hasConversion(EntityType<?> sourceType) {
        return conversionMap.containsKey(sourceType);
    }

    /**
     * Check whether any conversion (direct or category-based) exists
     * for the given entity type and class.
     *
     * @param sourceType the entity type to check
     * @param entityClass the entity's runtime class
     * @return true if a conversion of any kind can be resolved
     */
    public static boolean hasAnyConversion(EntityType<?> sourceType, Class<?> entityClass) {
        if (conversionMap.containsKey(sourceType)) return true;
        ParasiteType pType = getConversionParasiteType(entityClass);
        return pType != null && parasiteTypeEntityMap.containsKey(pType);
    }

    /**
     * Get an unmodifiable view of the direct conversion map.
     */
    public static Map<EntityType<?>, EntityType<?>> getConversionMap() {
        return Collections.unmodifiableMap(conversionMap);
    }

    /**
     * Get an unmodifiable view of the category map.
     */
    public static Map<Class<?>, ParasiteType> getCategoryMap() {
        return Collections.unmodifiableMap(categoryMap);
    }

    /**
     * Get an unmodifiable view of the ParasiteType → EntityType resolver map.
     */
    public static Map<ParasiteType, EntityType<?>> getParasiteTypeEntityMap() {
        return Collections.unmodifiableMap(parasiteTypeEntityMap);
    }

    /**
     * Get the number of direct EntityType → EntityType conversion mappings.
     */
    public static int getConversionCount() {
        return conversionMap.size();
    }

    /**
     * Remove a direct conversion mapping.
     *
     * @param sourceType the entity type to remove
     */
    public static void removeConversion(EntityType<?> sourceType) {
        conversionMap.remove(sourceType);
    }
}
