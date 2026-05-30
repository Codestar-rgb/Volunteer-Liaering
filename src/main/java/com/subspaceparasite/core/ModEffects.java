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
 * Contains all 36 effects from the original SRP mod.
 * 
 * <h2>Effect Categories:</h2>
 * <ul>
 *   <li>Infection Effects (5): COTH, INFECTION_II/III, VIRULENCE, COAGULATION</li>
 *   <li>Evolution Effects (4): EVOLUTION, ADAPTATION, SENTIENCE, DERIVATION</li>
 *   <li>Corruption Effects (4): CORRUPTION, DECAY, DECOMPOSITION, PUTREFACTION</li>
 *   <li>Parasite Buffs (5): PARASITE_VITALITY, RESISTANCE, STRENGTH, SPEED, REGENERATION</li>
 *   <li>Debuff Effects (4): FEAR, SLOWNESS_PARASITE, WEAKNESS_PARASITE, WITHER_PARASITE</li>
 *   <li>Special Effects (9): GESTATION, INCUBATION, ASSIMILATION, MUTAGENIC, CORROSION, VIRAL, SPORE, BLEED, NOVISION, VOMIT</li>
 *   <li>Resistance/Purge (3): PURGE, IMMUNITY, CLEANSING</li>
 *   <li>Nexus Effects (2): NEXUS_LINK, NEXUS_COMMAND</li>
 *   <li>Misc Effects (2): PARASITE_HUNGER, LEECH</li>
 * </ul>
 * 
 * @author SRP Port Team
 */
public class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SubspaceParasite.MOD_ID);

    // ── Helper Methods ──
    
    /**
     * Registers a harmful effect with custom implementation.
     */
    private static RegistryObject<MobEffect> registerHarmful(String name, int color) {
        return EFFECTS.register(name, () -> new MobEffect(MobEffectCategory.HARMFUL, color) {});
    }

    /**
     * Registers a beneficial effect with custom implementation.
     */
    private static RegistryObject<MobEffect> registerBeneficial(String name, int color) {
        return EFFECTS.register(name, () -> new MobEffect(MobEffectCategory.BENEFICIAL, color) {});
    }

    /**
     * Registers a neutral effect with custom implementation.
     */
    private static RegistryObject<MobEffect> registerNeutral(String name, int color) {
        return EFFECTS.register(name, () -> new MobEffect(MobEffectCategory.NEUTRAL, color) {});
    }
    
    /**
     * Registers a custom SRP effect implementation.
     */
    private static <T extends BaseSRPEffect> RegistryObject<MobEffect> registerCustom(String name, T effect) {
        return EFFECTS.register(name, () -> effect);
    }

    // ── Core Infection Effects (With Custom Implementations) ──
    
    /** Call of the Hive - Primary infection effect with full implementation */
    public static final RegistryObject<MobEffect> COTH = registerCustom("coth", new CothEffect());
    
    /** Advanced infection stage II */
    public static final RegistryObject<MobEffect> INFECTION_II = registerHarmful("infection_ii", 0x6B1A1A);
    
    /** Advanced infection stage III */
    public static final RegistryObject<MobEffect> INFECTION_III = registerHarmful("infection_iii", 0x8B2626);
    
    /** Virulence - Increased infection spread rate */
    public static final RegistryObject<MobEffect> VIRULENCE = registerHarmful("virulence", 0x9B3020);
    
    /** Coagulation - Blood clotting effect */
    public static final RegistryObject<MobEffect> COAGULATION = registerHarmful("coagulation", 0x7A0000);

    // ── Evolution Effects ──
    
    public static final RegistryObject<MobEffect> EVOLUTION = registerNeutral("evolution", 0x3CB371);
    public static final RegistryObject<MobEffect> ADAPTATION = registerBeneficial("adaptation", 0x2E8B57);
    public static final RegistryObject<MobEffect> SENTIENCE = registerBeneficial("sentience", 0x9370DB);
    public static final RegistryObject<MobEffect> DERIVATION = registerNeutral("derivation", 0x6A5ACD);

    // ── Corruption Effects ──
    
    public static final RegistryObject<MobEffect> CORRUPTION = registerHarmful("corruption", 0x4B0082);
    public static final RegistryObject<MobEffect> DECAY = registerHarmful("decay", 0x556B2F);
    public static final RegistryObject<MobEffect> DECOMPOSITION = registerHarmful("decomposition", 0x3B5323);
    public static final RegistryObject<MobEffect> PUTREFACTION = registerHarmful("putrefaction", 0x2F4F2F);

    // ── Parasite Buff Effects (With Custom Implementations) ──
    
    /** Parasite Vitality - Enhanced health and regeneration */
    public static final RegistryObject<MobEffect> PARASITE_VITALITY = registerCustom("parasite_vitality", new ParasiteVitalityEffect());
    public static final RegistryObject<MobEffect> PARASITE_RESISTANCE = registerBeneficial("parasite_resistance", 0xCD853F);
    public static final RegistryObject<MobEffect> PARASITE_STRENGTH = registerBeneficial("parasite_strength", 0xDC143C);
    public static final RegistryObject<MobEffect> PARASITE_SPEED = registerBeneficial("parasite_speed", 0xFF8C00);
    public static final RegistryObject<MobEffect> PARASITE_REGENERATION = registerBeneficial("parasite_regeneration", 0xFF69B4);

    // ── Debuff Effects ──
    
    public static final RegistryObject<MobEffect> FEAR = registerHarmful("fear", 0x363636);
    public static final RegistryObject<MobEffect> SLOWNESS_PARASITE = registerHarmful("slowness_parasite", 0x5A5A8A);
    public static final RegistryObject<MobEffect> WEAKNESS_PARASITE = registerHarmful("weakness_parasite", 0x484878);
    public static final RegistryObject<MobEffect> WITHER_PARASITE = registerHarmful("wither_parasite", 0x2E2E2E);

    // ── Alveoli/Egg Effects ──
    
    public static final RegistryObject<MobEffect> GESTATION = registerNeutral("gestation", 0xE6B800);
    public static final RegistryObject<MobEffect> INCUBATION = registerNeutral("incubation", 0xCCA300);

    // ── Special Effects (With Custom Implementations) ──
    
    public static final RegistryObject<MobEffect> ASSIMILATION = registerNeutral("assimilation", 0x8B0000);
    public static final RegistryObject<MobEffect> MUTAGENIC = registerHarmful("mutagenic", 0x9932CC);
    
    /** Corrosion - Armor degradation effect */
    public static final RegistryObject<MobEffect> CORROSION = registerCustom("corrosion", new CorrosionEffect());
    
    public static final RegistryObject<MobEffect> VIRAL = registerHarmful("viral", 0x228B22);
    public static final RegistryObject<MobEffect> SPORE = registerHarmful("spore", 0x9ACD32);
    
    /** Bleeding - Movement-based damage over time */
    public static final RegistryObject<MobEffect> BLEED = registerCustom("bleed", new BleedEffect());
    
    public static final RegistryObject<MobEffect> NOVISION = registerHarmful("novision", 0x1A1A1A);
    public static final RegistryObject<MobEffect> VOMIT = registerHarmful("vomit", 0x6B8E23);

    // ── Resistance/Purge Effects (With Custom Implementations) ──
    
    /** Purge - Infection cleansing and immunity */
    public static final RegistryObject<MobEffect> PURGE = registerCustom("purge", new PurgeEffect());
    
    public static final RegistryObject<MobEffect> IMMUNITY = registerBeneficial("immunity", 0x00CED1);
    public static final RegistryObject<MobEffect> CLEANSING = registerBeneficial("cleansing", 0xE0FFFF);

    // ── Nexus Effects ──
    
    public static final RegistryObject<MobEffect> NEXUS_LINK = registerNeutral("nexus_link", 0x4169E1);
    public static final RegistryObject<MobEffect> NEXUS_COMMAND = registerNeutral("nexus_command", 0x1E90FF);

    // ── Misc Effects ──
    
    public static final RegistryObject<MobEffect> PARASITE_HUNGER = registerHarmful("parasite_hunger", 0x8B4513);
    public static final RegistryObject<MobEffect> LEECH = registerHarmful("leech", 0xA0522D);

    private ModEffects() {
        // Utility class - prevent instantiation
    }
}
