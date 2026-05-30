package com.subspaceparasite.client.particle;

import com.subspaceparasite.SubspaceParasite;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Particle type registration for the SubspaceParasite mod.
 * At minimum 10 particle types matching original SRP.
 */
public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SubspaceParasite.MOD_ID);

    // Spore particles - floating spore clouds
    public static final RegistryObject<SimpleParticleType> SPORE =
            PARTICLE_TYPES.register("spore", () -> new SimpleParticleType(true));

    // Corruption particles - spreading corruption effect
    public static final RegistryObject<SimpleParticleType> CORRUPTION =
            PARTICLE_TYPES.register("corruption", () -> new SimpleParticleType(true));

    // Bile particles - vomit/acid attack
    public static final RegistryObject<SimpleParticleType> BILE =
            PARTICLE_TYPES.register("bile", () -> new SimpleParticleType(true));

    // Evolution particles - phase evolution burst
    public static final RegistryObject<SimpleParticleType> EVOLUTION =
            PARTICLE_TYPES.register("evolution", () -> new SimpleParticleType(true));

    // Infestation particles - block infestation spread
    public static final RegistryObject<SimpleParticleType> INFESTATION =
            PARTICLE_TYPES.register("infestation", () -> new SimpleParticleType(true));

    // Dissolve particles - entity dissolve on death
    public static final RegistryObject<SimpleParticleType> DISSOLVE =
            PARTICLE_TYPES.register("dissolve", () -> new SimpleParticleType(true));

    // Viral particles - viral infection spread
    public static final RegistryObject<SimpleParticleType> VIRAL =
            PARTICLE_TYPES.register("viral", () -> new SimpleParticleType(true));

    // Primordial particles - ancient/primordial effects
    public static final RegistryObject<SimpleParticleType> PRIMORDIAL =
            PARTICLE_TYPES.register("primordial", () -> new SimpleParticleType(true));

    // Ancient particles - ancient parasite ambient
    public static final RegistryObject<SimpleParticleType> ANCIENT =
            PARTICLE_TYPES.register("ancient", () -> new SimpleParticleType(true));

    // Nexus particles - nexus/deterrent effects
    public static final RegistryObject<SimpleParticleType> NEXUS =
            PARTICLE_TYPES.register("nexus", () -> new SimpleParticleType(true));
}
