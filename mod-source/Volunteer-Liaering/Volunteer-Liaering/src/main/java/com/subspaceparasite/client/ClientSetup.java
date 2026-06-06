package com.subspaceparasite.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.client.model.entity.ModelParasiteBiped;
import com.subspaceparasite.client.overlay.InfectionOverlayHandler;
import com.subspaceparasite.client.particle.ParasiteParticle;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModParticleTypes;
import com.subspaceparasite.client.renderer.entity.RenderParasiteBase;

// Individual GeckoLib renderers - Primitive
import com.subspaceparasite.client.renderer.entity.RenderPrimitiveBano;
import com.subspaceparasite.client.renderer.entity.RenderPrimitiveCanra;
import com.subspaceparasite.client.renderer.entity.RenderPrimitiveEmana;
import com.subspaceparasite.client.renderer.entity.RenderPrimitiveGim;
import com.subspaceparasite.client.renderer.entity.RenderPrimitiveHull;
import com.subspaceparasite.client.renderer.entity.RenderPrimitiveIki;
import com.subspaceparasite.client.renderer.entity.RenderPrimitiveLum;
import com.subspaceparasite.client.renderer.entity.RenderPrimitiveNogla;
import com.subspaceparasite.client.renderer.entity.RenderPrimitiveRanrac;
import com.subspaceparasite.client.renderer.entity.RenderPrimitiveShyco;
import com.subspaceparasite.client.renderer.entity.RenderPrimitiveWymo;
import com.subspaceparasite.client.renderer.entity.RenderPrimitiveZaa;

// Individual GeckoLib renderers - Adapted
import com.subspaceparasite.client.renderer.entity.RenderAdaptedBanoadapted;
import com.subspaceparasite.client.renderer.entity.RenderAdaptedCanraadapted;
import com.subspaceparasite.client.renderer.entity.RenderAdaptedEmanaadapted;
import com.subspaceparasite.client.renderer.entity.RenderAdaptedGimadapted;
import com.subspaceparasite.client.renderer.entity.RenderAdaptedHulladapted;
import com.subspaceparasite.client.renderer.entity.RenderAdaptedIkiadapted;
import com.subspaceparasite.client.renderer.entity.RenderAdaptedLumadapted;
import com.subspaceparasite.client.renderer.entity.RenderAdaptedNoglaadapted;
import com.subspaceparasite.client.renderer.entity.RenderAdaptedRanracadapted;
import com.subspaceparasite.client.renderer.entity.RenderAdaptedShycoadapted;
import com.subspaceparasite.client.renderer.entity.RenderAdaptedWymoadapted;
import com.subspaceparasite.client.renderer.entity.RenderAdaptedZaaadapted;

// Individual GeckoLib renderers - Pure
import com.subspaceparasite.client.renderer.entity.RenderPureFlam;
import com.subspaceparasite.client.renderer.entity.RenderPureFlog;
import com.subspaceparasite.client.renderer.entity.RenderPureOmboo;
import com.subspaceparasite.client.renderer.entity.RenderPureAlafha;
import com.subspaceparasite.client.renderer.entity.RenderPureGanro;
import com.subspaceparasite.client.renderer.entity.RenderPureEsor;
import com.subspaceparasite.client.renderer.entity.RenderPureElvia;
import com.subspaceparasite.client.renderer.entity.RenderPureAnged;

// Individual GeckoLib renderers - Crude
import com.subspaceparasite.client.renderer.entity.RenderCrudeCruxa;
import com.subspaceparasite.client.renderer.entity.RenderCrudeCruxb;
import com.subspaceparasite.client.renderer.entity.RenderCrudeDone;
import com.subspaceparasite.client.renderer.entity.RenderCrudeHeed;
import com.subspaceparasite.client.renderer.entity.RenderCrudeHost;
import com.subspaceparasite.client.renderer.entity.RenderCrudeInhoom;
import com.subspaceparasite.client.renderer.entity.RenderCrudeInhoos;
import com.subspaceparasite.client.renderer.entity.RenderCrudeLeer;
import com.subspaceparasite.client.renderer.entity.RenderCrudeMes;
import com.subspaceparasite.client.renderer.entity.RenderCrudeQuac;

// Individual GeckoLib renderers - Inborn
import com.subspaceparasite.client.renderer.entity.RenderInbornGothol;
import com.subspaceparasite.client.renderer.entity.RenderInbornKol;
import com.subspaceparasite.client.renderer.entity.RenderInbornLesh;
import com.subspaceparasite.client.renderer.entity.RenderInbornLodo;
import com.subspaceparasite.client.renderer.entity.RenderInbornMor;
import com.subspaceparasite.client.renderer.entity.RenderInbornMudo;
import com.subspaceparasite.client.renderer.entity.RenderInbornNuuh;
import com.subspaceparasite.client.renderer.entity.RenderInbornRathol;
import com.subspaceparasite.client.renderer.entity.RenderInbornViin;
import com.subspaceparasite.client.renderer.entity.RenderInbornAta;
import com.subspaceparasite.client.renderer.entity.RenderInbornButhol;

// Individual GeckoLib renderers - Infected
import com.subspaceparasite.client.renderer.entity.RenderInfectedInfhuman;
import com.subspaceparasite.client.renderer.entity.RenderInfectedInfcow;
import com.subspaceparasite.client.renderer.entity.RenderInfectedInfsheep;
import com.subspaceparasite.client.renderer.entity.RenderInfectedInfpig;
import com.subspaceparasite.client.renderer.entity.RenderInfectedInfvillager;
import com.subspaceparasite.client.renderer.entity.RenderInfectedInfwolf;
import com.subspaceparasite.client.renderer.entity.RenderInfectedInfhorse;
import com.subspaceparasite.client.renderer.entity.RenderInfectedInfenderman;

// Individual GeckoLib renderers - Feral
import com.subspaceparasite.client.renderer.entity.RenderFeralFerhuman;
import com.subspaceparasite.client.renderer.entity.RenderFeralFercow;
import com.subspaceparasite.client.renderer.entity.RenderFeralFerenderman;
import com.subspaceparasite.client.renderer.entity.RenderFeralFerwolf;
import com.subspaceparasite.client.renderer.entity.RenderFeralFerbear;
import com.subspaceparasite.client.renderer.entity.RenderFeralFerpig;

// Individual GeckoLib renderers - Hijacked
import com.subspaceparasite.client.renderer.entity.RenderHijackedHiskeleton;
import com.subspaceparasite.client.renderer.entity.RenderHijackedHiblaze;
import com.subspaceparasite.client.renderer.entity.RenderHijackedHigolem;

// Individual GeckoLib renderers - Derived
import com.subspaceparasite.client.renderer.entity.RenderDerivedHeblu;
import com.subspaceparasite.client.renderer.entity.RenderDerivedKirin;

// Individual GeckoLib renderers - Deterrent
import com.subspaceparasite.client.renderer.entity.RenderDeterrentVenkrol;
import com.subspaceparasite.client.renderer.entity.RenderDeterrentDod;
import com.subspaceparasite.client.renderer.entity.RenderDeterrentDodt;
import com.subspaceparasite.client.renderer.entity.RenderDeterrentLeem;

// Individual GeckoLib renderers - Ancient
import com.subspaceparasite.client.renderer.entity.RenderAncientOronco;
import com.subspaceparasite.client.renderer.entity.RenderAncientOroncoten;
import com.subspaceparasite.client.renderer.entity.RenderAncientTerla;

// Individual GeckoLib renderers - Nexus (using deterrent model naming convention)
import com.subspaceparasite.client.renderer.entity.RenderDeterrentNak;
import com.subspaceparasite.client.renderer.entity.RenderDeterrentTonro;
import com.subspaceparasite.client.renderer.entity.RenderDeterrentUnvo;

// Individual GeckoLib renderers - Infected (additional heads/variants)
import com.subspaceparasite.client.renderer.entity.RenderInfectedInfbear;
import com.subspaceparasite.client.renderer.entity.RenderInfectedInfsquid;

// Individual GeckoLib renderers - Abomination
import com.subspaceparasite.client.renderer.entity.RenderAbominationAbobodies;
import com.subspaceparasite.client.renderer.entity.RenderAbominationAbohead;

// Individual GeckoLib renderers - Misc / Projectile
import com.subspaceparasite.client.renderer.entity.RenderMiscOrbscary;
import com.subspaceparasite.client.renderer.entity.RenderMiscOrbvoid;
import com.subspaceparasite.client.renderer.entity.RenderMiscBiomasspod;
import com.subspaceparasite.client.renderer.entity.RenderMiscGore;
import com.subspaceparasite.client.renderer.entity.RenderMiscMeteor;
import com.subspaceparasite.client.renderer.entity.RenderMiscNade;
import com.subspaceparasite.client.renderer.entity.RenderMiscNull;
import com.subspaceparasite.client.renderer.entity.RenderMiscProjectilehomming;
import com.subspaceparasite.client.renderer.entity.RenderProjectileDroppod;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import org.lwjgl.glfw.GLFW;

/**
 * Client-side setup and registration for the SubspaceParasite mod.
 * Handles particle factories, entity renderers, key bindings, GUI overlays, and fog.
 */
@Mod.EventBusSubscriber(modid = SubspaceParasite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    // Key bindings
    public static final String KEY_CATEGORY = "key.categories.subspaceparasite";
    public static KeyMapping SCAN_KEY;
    public static KeyMapping BESTIARY_KEY;

    /**
     * Register particle factories.
     */
    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        // === ModParticleTypes (consolidated from legacy ModParticles) registrations ===
        // Spore particles - float upward
        event.registerSpriteSet(ModParticleTypes.SPORE.get(), ParasiteParticle.FloatFactory::new);
        event.registerSpriteSet(ModParticleTypes.SPORE_AMBIENT.get(), ParasiteParticle.FloatFactory::new);
        event.registerSpriteSet(ModParticleTypes.VIRULENT_SPORE.get(), ParasiteParticle.FloatFactory::new);

        // Blood particles - fall/drip
        event.registerSpriteSet(ModParticleTypes.DEAD_BLOOD_DRIP.get(), ParasiteParticle.FallFactory::new);
        event.registerSpriteSet(ModParticleTypes.DEAD_BLOOD_FALL.get(), ParasiteParticle.FallFactory::new);
        event.registerSpriteSet(ModParticleTypes.DEAD_BLOOD_LAND.get(), ParasiteParticle.SpreadFactory::new);

        // Infection particles - spread/float
        event.registerSpriteSet(ModParticleTypes.INFEST.get(), ParasiteParticle.SpreadFactory::new);
        event.registerSpriteSet(ModParticleTypes.DEINFEST.get(), ParasiteParticle.ExpandFactory::new);

        // Evolution particles - expand/burst
        event.registerSpriteSet(ModParticleTypes.EVOLUTION_SPARK.get(), ParasiteParticle.ExpandFactory::new);
        event.registerSpriteSet(ModParticleTypes.EVOLUTION_BURST.get(), ParasiteParticle.ExpandFactory::new);

        // Nexus particles - orbit/expand
        event.registerSpriteSet(ModParticleTypes.NEXUS_BEAM.get(), ParasiteParticle.OrbitFactory::new);
        event.registerSpriteSet(ModParticleTypes.NEXUS_PULSE.get(), ParasiteParticle.ExpandFactory::new);

        // Biome particles - float/spread
        event.registerSpriteSet(ModParticleTypes.BIOME_FOG.get(), ParasiteParticle.FloatFactory::new);
        event.registerSpriteSet(ModParticleTypes.SHROUDED_FOG.get(), ParasiteParticle.FloatFactory::new);
        event.registerSpriteSet(ModParticleTypes.HARLEQUIN_SPORE.get(), ParasiteParticle.FloatFactory::new);

        // Block break particles - expand/spread
        event.registerSpriteSet(ModParticleTypes.PARASITE_BLOCK_BREAK.get(), ParasiteParticle.ExpandFactory::new);
        event.registerSpriteSet(ModParticleTypes.FLESH_BLOCK_BREAK.get(), ParasiteParticle.ExpandFactory::new);

        // Combat particles - fall/expand
        event.registerSpriteSet(ModParticleTypes.ACID_SPIT.get(), ParasiteParticle.FallFactory::new);
        event.registerSpriteSet(ModParticleTypes.CAUSTIC_SPLASH.get(), ParasiteParticle.SpreadFactory::new);

        // Alveoli particles - expand
        event.registerSpriteSet(ModParticleTypes.ALVEOLI_HATCH.get(), ParasiteParticle.ExpandFactory::new);

        // Assimilation particles - expand
        event.registerSpriteSet(ModParticleTypes.ASSIMILATE.get(), ParasiteParticle.ExpandFactory::new);

        // Trophy particles - orbit
        event.registerSpriteSet(ModParticleTypes.VOID_ORB.get(), ParasiteParticle.OrbitFactory::new);
        event.registerSpriteSet(ModParticleTypes.BOOM_ORB.get(), ParasiteParticle.OrbitFactory::new);

        // === Consolidated from legacy ModParticles ===
        event.registerSpriteSet(ModParticleTypes.CORRUPTION.get(), ParasiteParticle.ExpandFactory::new);
        event.registerSpriteSet(ModParticleTypes.BILE.get(), ParasiteParticle.FallFactory::new);
        event.registerSpriteSet(ModParticleTypes.INFESTATION.get(), ParasiteParticle.SpreadFactory::new);
        event.registerSpriteSet(ModParticleTypes.DISSOLVE.get(), ParasiteParticle.FallFactory::new);
        event.registerSpriteSet(ModParticleTypes.VIRAL.get(), ParasiteParticle.FloatFactory::new);
        event.registerSpriteSet(ModParticleTypes.PRIMORDIAL.get(), ParasiteParticle.RiseFactoryProvider::new);
        event.registerSpriteSet(ModParticleTypes.ANCIENT.get(), ParasiteParticle.FloatFactory::new);
        event.registerSpriteSet(ModParticleTypes.NEXUS_LEGACY.get(), ParasiteParticle.OrbitFactory::new);
    }

    /**
     * Register entity renderers.
     * Called during EntityRenderersEvent.RegisterRenderers on the MOD bus.
     * Entities with individual GeckoLib renderers use their specific model class.
     * Entities without GeckoLib models use RenderParasiteBase as a biped fallback.
     * All entity types are explicitly registered — no reflection used.
     */
    @SubscribeEvent
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // ================================================================
        // PRIMITIVE - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.PRIMITIVE_BANO.get(), RenderPrimitiveBano::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_CANRA.get(), RenderPrimitiveCanra::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_EMANA.get(), RenderPrimitiveEmana::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_GIM.get(), RenderPrimitiveGim::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_HULL.get(), RenderPrimitiveHull::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_IKI.get(), RenderPrimitiveIki::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_LUM.get(), RenderPrimitiveLum::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_NOGLA.get(), RenderPrimitiveNogla::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_RANRAC.get(), RenderPrimitiveRanrac::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_SHYCO.get(), RenderPrimitiveShyco::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_WYMO.get(), RenderPrimitiveWymo::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_ZAA.get(), RenderPrimitiveZaa::new);

        // PRIMITIVE - Base renderers (no GeckoLib model yet)
        registerBase(event, ModEntities.PRIMITIVE_LONGARMS);
        registerBase(event, ModEntities.PRIMITIVE_MANDUCATER);
        registerBase(event, ModEntities.PRIMITIVE_REEKER);
        registerBase(event, ModEntities.PRIMITIVE_YELLOWEYE);
        registerBase(event, ModEntities.PRIMITIVE_SUMMONER);
        registerBase(event, ModEntities.PRIMITIVE_BOLSTER);
        registerBase(event, ModEntities.PRIMITIVE_TOZON);
        registerBase(event, ModEntities.PRIMITIVE_ARACHNIDA);
        registerBase(event, ModEntities.PRIMITIVE_DEVOURER);
        registerBase(event, ModEntities.PRIMITIVE_VERMIN);
        registerBase(event, ModEntities.PRIMITIVE_VISCERA);
        registerBase(event, ModEntities.PRIMITIVE_BURROWER);
        registerBase(event, ModEntities.PRIMITIVE_BOMPH);
        registerBase(event, ModEntities.PRIMITIVE_WOLF);

        // ================================================================
        // ADAPTED - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.ADAPTED_BANO.get(), RenderAdaptedBanoadapted::new);
        event.registerEntityRenderer(ModEntities.ADAPTED_CANRA.get(), RenderAdaptedCanraadapted::new);
        event.registerEntityRenderer(ModEntities.ADAPTED_EMANA.get(), RenderAdaptedEmanaadapted::new);
        event.registerEntityRenderer(ModEntities.ADAPTED_GIM.get(), RenderAdaptedGimadapted::new);
        event.registerEntityRenderer(ModEntities.ADAPTED_HULL.get(), RenderAdaptedHulladapted::new);
        event.registerEntityRenderer(ModEntities.ADAPTED_IKI.get(), RenderAdaptedIkiadapted::new);
        event.registerEntityRenderer(ModEntities.ADAPTED_LUM.get(), RenderAdaptedLumadapted::new);
        event.registerEntityRenderer(ModEntities.ADAPTED_NOGLA.get(), RenderAdaptedNoglaadapted::new);
        event.registerEntityRenderer(ModEntities.ADAPTED_RANRAC.get(), RenderAdaptedRanracadapted::new);
        event.registerEntityRenderer(ModEntities.ADAPTED_SHYCO.get(), RenderAdaptedShycoadapted::new);
        event.registerEntityRenderer(ModEntities.ADAPTED_WYMO.get(), RenderAdaptedWymoadapted::new);
        event.registerEntityRenderer(ModEntities.ADAPTED_ZAA.get(), RenderAdaptedZaaadapted::new);

        // ADAPTED - Base renderers (no GeckoLib model yet)
        registerBase(event, ModEntities.ADAPTED_COLONY);
        registerBase(event, ModEntities.ADAPTED_CREEPER);
        registerBase(event, ModEntities.ADAPTED_SKELETON);
        registerBase(event, ModEntities.ADAPTED_SPIDER);
        registerBase(event, ModEntities.ADAPTED_ZOMBIE);
        registerBase(event, ModEntities.ADAPTED_ENDERMAN);

        // ================================================================
        // PURE - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.PURE_FLAM.get(), RenderPureFlam::new);
        event.registerEntityRenderer(ModEntities.PURE_FLOG.get(), RenderPureFlog::new);
        event.registerEntityRenderer(ModEntities.PURE_OMBOO.get(), RenderPureOmboo::new);
        event.registerEntityRenderer(ModEntities.PURE_ALAFHA.get(), RenderPureAlafha::new);
        event.registerEntityRenderer(ModEntities.PURE_GANRO.get(), RenderPureGanro::new);
        event.registerEntityRenderer(ModEntities.PURE_ESOR.get(), RenderPureEsor::new);
        event.registerEntityRenderer(ModEntities.PURE_ELVIA.get(), RenderPureElvia::new);
        event.registerEntityRenderer(ModEntities.PURE_ANGED.get(), RenderPureAnged::new);

        // PURE - Base renderers (no GeckoLib model yet)
        registerBase(event, ModEntities.PURE_CREEPER);
        registerBase(event, ModEntities.PURE_SKELETON);
        registerBase(event, ModEntities.PURE_ZOMBIE);
        registerBase(event, ModEntities.PURE_SPIDER);
        registerBase(event, ModEntities.PURE_ENDERMAN);
        registerBase(event, ModEntities.PURE_WOLF);

        // ================================================================
        // CRUDE - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.CRUDE_CRUX_A.get(), RenderCrudeCruxa::new);
        event.registerEntityRenderer(ModEntities.CRUDE_CRUX_B.get(), RenderCrudeCruxb::new);
        event.registerEntityRenderer(ModEntities.CRUDE_DONE.get(), RenderCrudeDone::new);
        event.registerEntityRenderer(ModEntities.CRUDE_HEED.get(), RenderCrudeHeed::new);
        event.registerEntityRenderer(ModEntities.CRUDE_HOST.get(), RenderCrudeHost::new);
        event.registerEntityRenderer(ModEntities.CRUDE_INHOO_M.get(), RenderCrudeInhoom::new);
        event.registerEntityRenderer(ModEntities.CRUDE_INHOO_S.get(), RenderCrudeInhoos::new);
        event.registerEntityRenderer(ModEntities.CRUDE_LEER.get(), RenderCrudeLeer::new);
        event.registerEntityRenderer(ModEntities.CRUDE_MES.get(), RenderCrudeMes::new);
        event.registerEntityRenderer(ModEntities.CRUDE_QUAC.get(), RenderCrudeQuac::new);
        registerBase(event, ModEntities.CRUDE_LESH);

        // CRUDE - Base renderers (no GeckoLib model yet)
        registerBase(event, ModEntities.CRUDE_MOVING_FLESH);
        registerBase(event, ModEntities.CRUDE_WORKER);
        registerBase(event, ModEntities.CRUDE_SCORCHER);
        registerBase(event, ModEntities.CRUDE_MINDIM);
        registerBase(event, ModEntities.CRUDE_EGAS);

        // ================================================================
        // INBORN - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.INBORN_GOTHOL.get(), RenderInbornGothol::new);
        event.registerEntityRenderer(ModEntities.INBORN_KOL.get(), RenderInbornKol::new);
        event.registerEntityRenderer(ModEntities.INBORN_LESH.get(), RenderInbornLesh::new);
        event.registerEntityRenderer(ModEntities.INBORN_LODO.get(), RenderInbornLodo::new);
        event.registerEntityRenderer(ModEntities.INBORN_MOR.get(), RenderInbornMor::new);
        event.registerEntityRenderer(ModEntities.INBORN_MUDO.get(), RenderInbornMudo::new);
        event.registerEntityRenderer(ModEntities.INBORN_NUUH.get(), RenderInbornNuuh::new);
        event.registerEntityRenderer(ModEntities.INBORN_RATHOL.get(), RenderInbornRathol::new);
        event.registerEntityRenderer(ModEntities.INBORN_VIIN.get(), RenderInbornViin::new);
        event.registerEntityRenderer(ModEntities.INBORN_ATA.get(), RenderInbornAta::new);
        event.registerEntityRenderer(ModEntities.INBORN_BUTHOL.get(), RenderInbornButhol::new);

        // INBORN - Base renderers (no GeckoLib model yet)
        registerBase(event, ModEntities.INBORN_ALAFIN);
        registerBase(event, ModEntities.INBORN_OBUS);
        registerBase(event, ModEntities.INBORN_NORMAS);
        registerBase(event, ModEntities.INBORN_CANAL);

        // ================================================================
        // INFECTED - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.INFECTED_HUMAN.get(), RenderInfectedInfhuman::new);
        event.registerEntityRenderer(ModEntities.INFECTED_COW.get(), RenderInfectedInfcow::new);
        event.registerEntityRenderer(ModEntities.INFECTED_SHEEP.get(), RenderInfectedInfsheep::new);
        event.registerEntityRenderer(ModEntities.INFECTED_PIG.get(), RenderInfectedInfpig::new);
        event.registerEntityRenderer(ModEntities.INFECTED_VILLAGER.get(), RenderInfectedInfvillager::new);
        event.registerEntityRenderer(ModEntities.INFECTED_WOLF.get(), RenderInfectedInfwolf::new);
        event.registerEntityRenderer(ModEntities.INFECTED_HORSE.get(), RenderInfectedInfhorse::new);
        event.registerEntityRenderer(ModEntities.INFECTED_ENDERMAN.get(), RenderInfectedInfenderman::new);

        // INFECTED - Base renderers (no GeckoLib model yet)
        registerBase(event, ModEntities.INFECTED_CREEPER);
        registerBase(event, ModEntities.INFECTED_SKELETON);
        registerBase(event, ModEntities.INFECTED_ZOMBIE);
        registerBase(event, ModEntities.INFECTED_SPIDER);
        registerBase(event, ModEntities.INFECTED_CHICKEN);
        registerBase(event, ModEntities.INFECTED_IRON_GOLEM);
        registerBase(event, ModEntities.INFECTED_SNOW_GOLEM);
        event.registerEntityRenderer(ModEntities.INFECTED_BAT.get(), RenderInfectedInfsquid::new);  // BUG: Using squid renderer for bat — needs dedicated bat renderer
        registerBase(event, ModEntities.INFECTED_BLAZE);
        registerBase(event, ModEntities.INFECTED_WITCH);
        registerBase(event, ModEntities.INFECTED_RAVAGER);
        registerBase(event, ModEntities.INFECTED_PILLAGER);
        registerBase(event, ModEntities.INFECTED_EVOKER);
        registerBase(event, ModEntities.INFECTED_GHAST);
        registerBase(event, ModEntities.INFECTED_PHANTOM);
        registerBase(event, ModEntities.INFECTED_WARDEN);
        registerBase(event, ModEntities.INFECTED_WITHER_SKELETON);
        registerBase(event, ModEntities.INFECTED_STRAY);
        registerBase(event, ModEntities.INFECTED_HUSK);
        registerBase(event, ModEntities.INFECTED_DROWNED);
        registerBase(event, ModEntities.INFECTED_CAVE_SPIDER);
        registerBase(event, ModEntities.INFECTED_MOOSHROOM);
        registerBase(event, ModEntities.INFECTED_LLAMA);
        event.registerEntityRenderer(ModEntities.INFECTED_POLAR_BEAR.get(), RenderInfectedInfbear::new);
        registerBase(event, ModEntities.INFECTED_PANDA);
        registerBase(event, ModEntities.INFECTED_FOX);
        registerBase(event, ModEntities.INFECTED_BEE);

        // ================================================================
        // ASSIMILATED - Base renderers (no GeckoLib model yet)
        // ================================================================
        registerBase(event, ModEntities.ASSIMILATED_HUMAN);
        registerBase(event, ModEntities.ASSIMILATED_COW);
        registerBase(event, ModEntities.ASSIMILATED_SHEEP);

        // ================================================================
        // FERAL - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.FERAL_HUMAN.get(), RenderFeralFerhuman::new);
        event.registerEntityRenderer(ModEntities.FERAL_COW.get(), RenderFeralFercow::new);
        event.registerEntityRenderer(ModEntities.FERAL_ENDERMAN.get(), RenderFeralFerenderman::new);
        event.registerEntityRenderer(ModEntities.FERAL_WOLF.get(), RenderFeralFerwolf::new);
        event.registerEntityRenderer(ModEntities.FERAL_IRON_GOLEM.get(), RenderFeralFerbear::new);

        // FERAL - Base renderers (no GeckoLib model yet)
        registerBase(event, ModEntities.FERAL_CREEPER);
        registerBase(event, ModEntities.FERAL_SKELETON);
        event.registerEntityRenderer(ModEntities.FERAL_ZOMBIE.get(), RenderFeralFerpig::new);
        registerBase(event, ModEntities.FERAL_SPIDER);

        // ================================================================
        // HIJACKED - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.HIJACKED_SKELETON.get(), RenderHijackedHiskeleton::new);

        // HIJACKED - Individual GeckoLib renderers (variant models)
        event.registerEntityRenderer(ModEntities.HIJACKED_CREEPER.get(), RenderHijackedHiblaze::new);
        event.registerEntityRenderer(ModEntities.HIJACKED_RAVAGER.get(), RenderHijackedHigolem::new);

        // HIJACKED - Base renderers (no GeckoLib model yet)
        registerBase(event, ModEntities.HIJACKED_ZOMBIE);
        registerBase(event, ModEntities.HIJACKED_SPIDER);
        registerBase(event, ModEntities.HIJACKED_ENDERMAN);
        registerBase(event, ModEntities.HIJACKED_WITCH);
        registerBase(event, ModEntities.HIJACKED_PILLAGER);
        registerBase(event, ModEntities.HIJACKED_EVOKER);

        // ================================================================
        // DERIVED - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.DERIVED_HEBLU.get(), RenderDerivedHeblu::new);
        event.registerEntityRenderer(ModEntities.DERIVED_KIRIN.get(), RenderDerivedKirin::new);

        // DERIVED - Base renderers (no GeckoLib model yet)
        registerBase(event, ModEntities.DERIVED_FLY);
        registerBase(event, ModEntities.DERIVED_SWARM);
        registerBase(event, ModEntities.DERIVED_CRAWLER);
        registerBase(event, ModEntities.DERIVED_LEAPER);
        registerBase(event, ModEntities.DERIVED_STALKER);

        // ================================================================
        // DETERRENT - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.DETERRENT_VENKROL_SIV.get(), RenderDeterrentVenkrol::new);

        // DETERRENT - Individual GeckoLib renderers (variant models)
        event.registerEntityRenderer(ModEntities.DETERRENT_SENTRY.get(), RenderDeterrentDod::new);

        // DETERRENT - Base renderers (no GeckoLib model yet)
        registerBase(event, ModEntities.DETERRENT_OUTPOST);
        registerBase(event, ModEntities.DETERRENT_BASTION);

        // ================================================================
        // ANCIENT - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.ANCIENT_COLOSSUS.get(), RenderAncientOronco::new);
        event.registerEntityRenderer(ModEntities.ANCIENT_DREADNOUGHT.get(), RenderAncientOroncoten::new);
        event.registerEntityRenderer(ModEntities.ANCIENT_LEVIATHAN.get(), RenderAncientTerla::new);

        // ================================================================
        // BECKON - Base renderers
        // ================================================================
        registerBase(event, ModEntities.BECKON_COMMON);
        registerBase(event, ModEntities.BECKON_UNCOMMON);
        registerBase(event, ModEntities.BECKON_RARE);
        registerBase(event, ModEntities.BECKON_EPIC);
        registerBase(event, ModEntities.BECKON_LEGENDARY);

        // ================================================================
        // DISPATCHER - Base renderers
        // ================================================================
        registerBase(event, ModEntities.DISPATCHER_COMMON);
        registerBase(event, ModEntities.DISPATCHER_UNCOMMON);
        registerBase(event, ModEntities.DISPATCHER_RARE);
        registerBase(event, ModEntities.DISPATCHER_EPIC);
        event.registerEntityRenderer(ModEntities.DISPATCHER_LEGENDARY.get(), RenderDeterrentDodt::new);

        // ================================================================
        // ROOTER - Base renderers
        // ================================================================
        registerBase(event, ModEntities.ROOTER_COMMON);
        registerBase(event, ModEntities.ROOTER_UNCOMMON);
        registerBase(event, ModEntities.ROOTER_RARE);
        registerBase(event, ModEntities.ROOTER_EPIC);
        event.registerEntityRenderer(ModEntities.ROOTER_LEGENDARY.get(), RenderDeterrentLeem::new);

        // ================================================================
        // NEXUS - Individual GeckoLib renderers
        // (Models use deterrent/ naming convention: nak, unvo, tonro)
        // ================================================================
        event.registerEntityRenderer(ModEntities.NEXUS_GUARD.get(), RenderDeterrentNak::new);
        event.registerEntityRenderer(ModEntities.NEXUS_OVERSEER.get(), RenderDeterrentUnvo::new);
        event.registerEntityRenderer(ModEntities.NEXUS_CONSTRUCT.get(), RenderDeterrentTonro::new);

        // ================================================================
        // PREEMINENT - Base renderers
        // ================================================================
        registerBase(event, ModEntities.PREEMINENT_MARAUDER);
        registerBase(event, ModEntities.PREEMINENT_WARDEN);
        registerBase(event, ModEntities.PREEMINENT_SOVEREIGN);

        // ================================================================
        // ABOMINATION - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.ABOMINATION_AMALGAM.get(), RenderAbominationAbobodies::new);
        registerBase(event, ModEntities.ABOMINATION_CHIMERA);
        event.registerEntityRenderer(ModEntities.ABOMINATION_HYDRA.get(), RenderAbominationAbohead::new);

        // ================================================================
        // MISC - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.PROJECTILE_ORB_SCARY.get(), RenderMiscOrbscary::new);
        event.registerEntityRenderer(ModEntities.PROJECTILE_ORB_VOID.get(), RenderMiscOrbvoid::new);
        event.registerEntityRenderer(ModEntities.PARASITE_LARVA.get(), RenderMiscGore::new);
        event.registerEntityRenderer(ModEntities.VOID_ORB.get(), RenderMiscNull::new);
        event.registerEntityRenderer(ModEntities.BOOM_ORB.get(), RenderMiscBiomasspod::new);

        // MISC - Base renderers
        registerBase(event, ModEntities.BUGLIN_ENTITY);
        registerBase(event, ModEntities.ALVEOLI_WORM);

        // ================================================================
        // PROJECTILE - Individual GeckoLib renderers
        // ================================================================
        event.registerEntityRenderer(ModEntities.BILE_BOMB.get(), RenderMiscNade::new);
        event.registerEntityRenderer(ModEntities.VIRULENT_SHOT.get(), RenderMiscProjectilehomming::new);
        event.registerEntityRenderer(ModEntities.BECKON_BLAST.get(), RenderProjectileDroppod::new);
        event.registerEntityRenderer(ModEntities.PROJECTILE_METEOR.get(), RenderMiscMeteor::new);

        // PROJECTILE - Base renderers
        registerBase(event, ModEntities.ACID_SPIT);
        registerBase(event, ModEntities.SPORE_CLOUD);
        registerBase(event, ModEntities.PARASITE_WEB);
        registerBase(event, ModEntities.NEXUS_BEAM_ENTITY);

        // ================================================================
        // REMAINING ORPHANED GECKOLIB RENDERERS
        // These renderer files exist on disk but share entity types with
        // already-registered renderers. They cannot be registered until
        // their target entity types are created as separate registrations.
        //
        // Pure:     RenderPureJinjo (→Omboo), RenderPureLencia (→Esor),
        //           RenderPureOrch (→Flog), RenderPurePheon (→Ganro),
        //           RenderPureRond (→Ganro), RenderPureTenn (→Anged),
        //           RenderPureVesta (→Flam)
        // Feral:    RenderFeralFerhorse (→FeralCow), RenderFeralFersheep (→FeralCow),
        //           RenderFeralFervillager (→FeralHuman)
        // Focused:  RenderFocusedBanofocused (→BanoAdapted), RenderFocusedShycofocused (→ShycoAdapted)
        // Awakened: RenderAwakenedOroncoaw (→AncientColossus), RenderAwakenedOroncoawfl (→AncientDreadnought)
        // Crude:    RenderCrudeHostii (→InhooM)
        // Deterrent: RenderDeterrentDodsii/iii (→DeterrentSentry),
        //            RenderDeterrentDodsiv/ivh (→VenkrolSIV),
        //            RenderDeterrentLeemb/msii/msiii/msiv (→LeemB),
        //            RenderDeterrentVenkrolsii/siii/siv/sv (→VenkrolSIV),
        //            RenderDeterrentRof (→EntityRof, not registered in ModEntities)
        // Infected: RenderInfectedDorpa (→InfectedPig),
        //           RenderInfectedInfcowhead (→InfectedCow),
        //           RenderInfectedInfdragone/head (→InfectedEnderman),
        //           RenderInfectedInfendermanhead (→InfectedEnderman),
        //           RenderInfectedInfhorsehead (→InfectedHorse),
        //           RenderInfectedInfhumanhead (→InfectedHuman),
        //           RenderInfectedInfpighead (→InfectedPig),
        //           RenderInfectedInfplayer/head (→InfectedHuman),
        //           RenderInfectedInfsheephead (→InfectedSheep),
        //           RenderInfectedInfvillagerhead (→InfectedVillager),
        //           RenderInfectedInfwolfhead (→InfectedWolf),
        //           RenderInfectedSpebear (→InfectedPolarBear),
        //           RenderInfectedSpecow/enderman/human/sheep/villager
        // Misc:     RenderMiscBiomassvenkrol (→BoomOrb),
        //           RenderMiscBombhost/jinjo/omboo (→BileBomb),
        //           RenderMiscTendril* (8) (→ParasiteLarva)
        // ================================================================
    }

    /**
     * Helper to register the base biped fallback renderer for entity types
     * that don't yet have GeckoLib .geo.json models.
     * Uses an unchecked cast since RenderParasiteBase handles all EntityParasiteBase subtypes.
     */
    @SuppressWarnings("unchecked")
    private static <T extends EntityParasiteBase> void registerBase(
            EntityRenderersEvent.RegisterRenderers event,
            RegistryObject<EntityType<T>> type) {
        EntityRendererProvider<EntityParasiteBase> baseProvider = RenderParasiteBase::new;
        event.registerEntityRenderer(type.get(), (EntityRendererProvider<T>) (EntityRendererProvider<?>) baseProvider);
    }

    /**
     * Register key bindings.
     */
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        SCAN_KEY = new KeyMapping(
                "key.subspaceparasite.scan",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KEY_CATEGORY
        );
        BESTIARY_KEY = new KeyMapping(
                "key.subspaceparasite.bestiary",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                KEY_CATEGORY
        );

        event.register(SCAN_KEY);
        event.register(BESTIARY_KEY);
    }

    /**
     * Register GUI overlays.
     */
    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll(
                SubspaceParasite.MOD_ID + ":infection_overlay",
                InfectionOverlayHandler.INSTANCE
        );
    }

    /**
     * Register fog color handler and other client-side event listeners.
     * Called during FMLClientSetupEvent.
     */
    public static void registerClientEventHandlers() {
        // FogHandler and InfectionOverlayHandler are registered via @SubscribeEvent
        // on their respective classes with the Forge event bus
    }

    /**
     * Register model layer locations.
     * Called during EntityRenderersEvent.RegisterLayerDefinitions on the MOD bus.
     */
    @SubscribeEvent
    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModelParasiteBiped.LAYER_LOCATION, ModelParasiteBiped::createBodyLayer);
    }
}
