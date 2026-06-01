package com.subspaceparasite.core;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.subspaceparasite.SubspaceParasite;
import net.minecraftforge.registries.RegistryObject;

import com.subspaceparasite.common.effect.*;

/**
 * Effect registry for the SubspaceParasite mod.
 * Contains all 40 effects from the original SRP mod, each with custom implementations.
 * 
 * <h2>Effect Categories:</h2>
 * <ul>
 *   <li>Infection Effects (5): COTH, INFECTION_II/III, VIRULENCE, COAGULATION</li>
 *   <li>Evolution Effects (4): EVOLUTION, ADAPTATION, SENTIENCE, DERIVATION</li>
 *   <li>Corruption Effects (4): CORRUPTION, DECAY, DECOMPOSITION, PUTREFACTION</li>
 *   <li>Parasite Buffs (5): PARASITE_VITALITY, RESISTANCE, STRENGTH, SPEED, REGENERATION</li>
 *   <li>Debuff Effects (4): FEAR, SLOWNESS_PARASITE, WEAKNESS_PARASITE, WITHER_PARASITE</li>
 *   <li>Special Effects (9): GESTATION, INCUBATION, ASSIMILATION, MUTAGENIC, CORROSION, VIRAL, SPORE, BLEED, NOVISION, VOMIT</li>
 *   <li>Resistance/Purge (4): PURGE, IMMUNITY, CLEANSING, THE_SIGN</li>
 *   <li>Nexus Effects (2): NEXUS_LINK, NEXUS_COMMAND</li>
 *   <li>Misc Effects (2): PARASITE_HUNGER, LEECH</li>
 * </ul>
 * 
 * @author SRP Port Team
 */
public class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SubspaceParasite.MOD_ID);

    // ── Core Infection Effects ──
    
    /** Call of the Hive - Primary infection effect */
    public static final RegistryObject<MobEffect> COTH = EFFECTS.register("coth", CothEffect::new);
    
    /** Advanced infection stage II */
    public static final RegistryObject<MobEffect> INFECTION_II = EFFECTS.register("infection_ii", 
        () -> new InfectionStageEffect(2));
    
    /** Advanced infection stage III */
    public static final RegistryObject<MobEffect> INFECTION_III = EFFECTS.register("infection_iii", 
        () -> new InfectionStageEffect(3));
    
    /** Virulence - Increased infection spread rate */
    public static final RegistryObject<MobEffect> VIRULENCE = EFFECTS.register("virulence", VirulenceEffect::new);
    
    /** Coagulation - Blood clotting and healing reduction */
    public static final RegistryObject<MobEffect> COAGULATION = EFFECTS.register("coagulation", CoagulationEffect::new);

    // ── Evolution Effects ──
    
    /** Evolution - Parasite evolutionary progress */
    public static final RegistryObject<MobEffect> EVOLUTION = EFFECTS.register("evolution", EvolutionEffect::new);
    
    /** Adaptation - Environmental adaptation and resistance */
    public static final RegistryObject<MobEffect> ADAPTATION = EFFECTS.register("adaptation", AdaptationEffect::new);
    
    /** Sentience - Advanced cognitive enhancement */
    public static final RegistryObject<MobEffect> SENTIENCE = EFFECTS.register("sentience", SentienceEffect::new);
    
    /** Derivation - Branching evolution and specialization */
    public static final RegistryObject<MobEffect> DERIVATION = EFFECTS.register("derivation", DerivationEffect::new);

    // ── Corruption Effects ──
    
    /** Corruption - Continuous damage and max health reduction */
    public static final RegistryObject<MobEffect> CORRUPTION = EFFECTS.register("corruption", CorruptionEffect::new);
    
    /** Decay - Progressive armor degradation */
    public static final RegistryObject<MobEffect> DECAY = EFFECTS.register("decay", DecayEffect::new);
    
    /** Decomposition - Rapid hunger exhaustion */
    public static final RegistryObject<MobEffect> DECOMPOSITION = EFFECTS.register("decomposition", DecompositionEffect::new);
    
    /** Putrefaction - Compound effect of poison and slowness */
    public static final RegistryObject<MobEffect> PUTREFACTION = EFFECTS.register("putrefaction", PutrefactionEffect::new);

    // ── Parasite Buff Effects ──
    
    /** Parasite Vitality - Enhanced health and regeneration */
    public static final RegistryObject<MobEffect> PARASITE_VITALITY = EFFECTS.register("parasite_vitality", ParasiteVitalityEffect::new);
    
    /** Parasite Resistance - Enhanced damage resistance */
    public static final RegistryObject<MobEffect> PARASITE_RESISTANCE = EFFECTS.register("parasite_resistance", ParasiteResistanceEffect::new);
    
    /** Parasite Strength - Enhanced attack power */
    public static final RegistryObject<MobEffect> PARASITE_STRENGTH = EFFECTS.register("parasite_strength", ParasiteStrengthEffect::new);
    
    /** Parasite Speed - Enhanced movement speed */
    public static final RegistryObject<MobEffect> PARASITE_SPEED = EFFECTS.register("parasite_speed", ParasiteSpeedEffect::new);
    
    /** Parasite Regeneration - Enhanced healing */
    public static final RegistryObject<MobEffect> PARASITE_REGENERATION = EFFECTS.register("parasite_regeneration", ParasiteRegenerationEffect::new);

    // ── Debuff Effects ──
    
    /** Fear - Prevents targeting and causes trembling */
    public static final RegistryObject<MobEffect> FEAR = EFFECTS.register("fear", FearEffect::new);
    
    /** Parasitic Slowness - Stronger variant of vanilla Slowness */
    public static final RegistryObject<MobEffect> SLOWNESS_PARASITE = EFFECTS.register("slowness_parasite", SlownessParasiteEffect::new);
    
    /** Parasitic Weakness - Attack damage reduction from parasites */
    public static final RegistryObject<MobEffect> WEAKNESS_PARASITE = EFFECTS.register("weakness_parasite", WeaknessParasiteEffect::new);
    
    /** Parasitic Wither - Combined wither damage and infection spread */
    public static final RegistryObject<MobEffect> WITHER_PARASITE = EFFECTS.register("wither_parasite", WitherParasiteEffect::new);

    // ── Alveoli/Egg Effects ──
    
    /** Gestation - Tracks parasitic egg development */
    public static final RegistryObject<MobEffect> GESTATION = EFFECTS.register("gestation", GestationEffect::new);
    
    /** Incubation - Countdown to parasite egg hatching */
    public static final RegistryObject<MobEffect> INCUBATION = EFFECTS.register("incubation", IncubationEffect::new);

    // ── Special Effects ──
    
    /** Assimilation - Tracks assimilation progress into hive mind */
    public static final RegistryObject<MobEffect> ASSIMILATION = EFFECTS.register("assimilation", AssimilationEffect::new);
    
    /** Mutagenic - Triggers genetic mutations */
    public static final RegistryObject<MobEffect> MUTAGENIC = EFFECTS.register("mutagenic", MutagenicEffect::new);
    
    /** Corrosion - Armor degradation effect */
    public static final RegistryObject<MobEffect> CORROSION = EFFECTS.register("corrosion", CorrosionEffect::new);
    
    /** Viral - Inverts healing into damage */
    public static final RegistryObject<MobEffect> VIRAL = EFFECTS.register("viral", ViralEffect::new);
    
    /** Spore - Area infection cloud */
    public static final RegistryObject<MobEffect> SPORE = EFFECTS.register("spore", SporeEffect::new);
    
    /** Bleeding - Movement-based damage over time */
    public static final RegistryObject<MobEffect> BLEED = EFFECTS.register("bleed", BleedEffect::new);
    
    /** Novision - Complete blindness with auditory hints */
    public static final RegistryObject<MobEffect> NOVISION = EFFECTS.register("novision", NovisionEffect::new);
    
    /** Vomit - Nausea and random item drops */
    public static final RegistryObject<MobEffect> VOMIT = EFFECTS.register("vomit", VomitEffect::new);

    // ── Resistance/Purge Effects ──
    
    /** Purge - Infection cleansing and temporary immunity */
    public static final RegistryObject<MobEffect> PURGE = EFFECTS.register("purge", PurgeEffect::new);
    
    /** Immunity - Complete protection against infection */
    public static final RegistryObject<MobEffect> IMMUNITY = EFFECTS.register("immunity", ImmunityEffect::new);
    
    /** Cleansing - Continuous purification and resistance building */
    public static final RegistryObject<MobEffect> CLEANSING = EFFECTS.register("cleansing", CleansingEffect::new);
    
    /** The Sign - Powerful cleansing effect from original SRP */
    public static final RegistryObject<MobEffect> THE_SIGN = EFFECTS.register("the_sign", TheSignEffect::new);

    // ── Nexus Effects ──
    
    /** Nexus Link - Establishes hive mind connection */
    public static final RegistryObject<MobEffect> NEXUS_LINK = EFFECTS.register("nexus_link", NexusLinkEffect::new);
    
    /** Nexus Command - Grants colony command authority */
    public static final RegistryObject<MobEffect> NEXUS_COMMAND = EFFECTS.register("nexus_command", NexusCommandEffect::new);

    // ── Misc Effects ──
    
    /** Parasite Hunger - Accelerated nutrient consumption */
    public static final RegistryObject<MobEffect> PARASITE_HUNGER = EFFECTS.register("parasite_hunger", ParasiteHungerEffect::new);
    
    /** Leech - Life drain link to parasite entity */
    public static final RegistryObject<MobEffect> LEECH = EFFECTS.register("leech", LeechEffect::new);

    private ModEffects() {
        // Utility class - prevent instantiation
    }
}
