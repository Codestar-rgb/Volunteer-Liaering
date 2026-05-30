package com.subspaceparasite.core;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.subspaceparasite.SubspaceParasite;
import net.minecraftforge.registries.RegistryObject;

/**
 * Particle type registry for the SubspaceParasite mod.
 */
public class ModParticleTypes {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SubspaceParasite.MOD_ID);

    // Helper method for always-showing particles
    private static RegistryObject<SimpleParticleType> register(String name) {
        return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(false));
    }

    // Helper method for override-limiter particles (respect reduced particle setting)
    private static RegistryObject<SimpleParticleType> registerOverride(String name) {
        return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(true));
    }

    // === Spore Particles ===
    public static final RegistryObject<SimpleParticleType> SPORE = register("spore");
    public static final RegistryObject<SimpleParticleType> SPORE_AMBIENT = registerOverride("spore_ambient");
    public static final RegistryObject<SimpleParticleType> VIRULENT_SPORE = register("virulent_spore");

    // === Blood Particles ===
    public static final RegistryObject<SimpleParticleType> DEAD_BLOOD_DRIP = register("dead_blood_drip");
    public static final RegistryObject<SimpleParticleType> DEAD_BLOOD_FALL = register("dead_blood_fall");
    public static final RegistryObject<SimpleParticleType> DEAD_BLOOD_LAND = register("dead_blood_land");

    // === Infection Particles ===
    public static final RegistryObject<SimpleParticleType> INFEST = register("infest");
    public static final RegistryObject<SimpleParticleType> DEINFEST = register("deinfest");

    // === Evolution Particles ===
    public static final RegistryObject<SimpleParticleType> EVOLUTION_SPARK = register("evolution_spark");
    public static final RegistryObject<SimpleParticleType> EVOLUTION_BURST = register("evolution_burst");

    // === Nexus Particles ===
    public static final RegistryObject<SimpleParticleType> NEXUS_BEAM = register("nexus_beam");
    public static final RegistryObject<SimpleParticleType> NEXUS_PULSE = register("nexus_pulse");

    // === Biome Particles ===
    public static final RegistryObject<SimpleParticleType> BIOME_FOG = registerOverride("biome_fog");
    public static final RegistryObject<SimpleParticleType> SHROUDED_FOG = registerOverride("shrouded_fog");
    public static final RegistryObject<SimpleParticleType> HARLEQUIN_SPORE = register("harlequin_spore");

    // === Block Break Particles ===
    public static final RegistryObject<SimpleParticleType> PARASITE_BLOCK_BREAK = register("parasite_block_break");
    public static final RegistryObject<SimpleParticleType> FLESH_BLOCK_BREAK = register("flesh_block_break");

    // === Combat Particles ===
    public static final RegistryObject<SimpleParticleType> ACID_SPIT = register("acid_spit");
    public static final RegistryObject<SimpleParticleType> CAUSTIC_SPLASH = register("caustic_splash");

    // === Alveoli Particles ===
    public static final RegistryObject<SimpleParticleType> ALVEOLI_HATCH = register("alveoli_hatch");

    // === Assimilation Particles ===
    public static final RegistryObject<SimpleParticleType> ASSIMILATE = register("assimilate");

    // === Trophy Particles ===
    public static final RegistryObject<SimpleParticleType> VOID_ORB = register("void_orb");
    public static final RegistryObject<SimpleParticleType> BOOM_ORB = register("boom_orb");

    private ModParticleTypes() {
        // Utility class - prevent instantiation
    }
}
