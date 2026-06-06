package com.subspaceparasite.core;

import net.minecraft.world.entity.Mob;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.subspaceparasite.SubspaceParasite;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Sound event registry for the SubspaceParasite mod.
 * Contains all custom sounds from the original SRP mod.
 * 
 * Naming Convention: All sound IDs follow the pattern "SubSRP_<original_name>"
 * to ensure compatibility with provided texture and sound assets while maintaining
 * clear distinction from the original SRP mod.
 * 
 * Architecture Note:
 * This registry is designed for maximum extensibility and robustness.
 * Sounds are organized by category (Entity, Block, Item, Ambient, etc.)
 * with comprehensive coverage of all SRP entities and mechanics.
 */
public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SubspaceParasite.MOD_ID);

    // Cache for quick lookup during entity initialization - improves performance
    private static final Map<String, RegistryObject<SoundEvent>> SOUND_CACHE = new HashMap<>();

    // Helper method with caching and SubSRP prefix enforcement
    private static RegistryObject<SoundEvent> register(String name) {
        // Enforce SubSRP prefix in the actual resource path for asset matching
        String prefixedName = "subsrp_" + name;
        ResourceLocation loc = new ResourceLocation(SubspaceParasite.MOD_ID, prefixedName);
        RegistryObject<SoundEvent> event = SOUNDS.register(prefixedName, () -> SoundEvent.createVariableRangeEvent(loc));
        SOUND_CACHE.put(name, event); // Store with original key for easy lookup
        return event;
    }

    // === Parasite Sounds (Base Infected) ===
    public static final RegistryObject<SoundEvent> PARASITE_IDLE = register("entity.parasite.idle");
    public static final RegistryObject<SoundEvent> PARASITE_HURT = register("entity.parasite.hurt");
    public static final RegistryObject<SoundEvent> PARASITE_DEATH = register("entity.parasite.death");
    public static final RegistryObject<SoundEvent> PARASITE_ATTACK = register("entity.parasite.attack");
    public static final RegistryObject<SoundEvent> PARASITE_STEP = register("entity.parasite.step");

    // === Infected Human/Mob Sounds ===
    public static final RegistryObject<SoundEvent> INFECTED_IDLE = register("entity.infected.idle");
    public static final RegistryObject<SoundEvent> INFECTED_HURT = register("entity.infected.hurt");
    public static final RegistryObject<SoundEvent> INFECTED_DEATH = register("entity.infected.death");
    public static final RegistryObject<SoundEvent> INFECTED_ATTACK = register("entity.infected.attack");
    
    // Specific infected variants
    public static final RegistryObject<SoundEvent> INFECTED_HUMAN_IDLE = register("entity.infected_human.idle");
    public static final RegistryObject<SoundEvent> INFECTED_HUMAN_HURT = register("entity.infected_human.hurt");
    public static final RegistryObject<SoundEvent> INFECTED_HUMAN_DEATH = register("entity.infected_human.death");
    
    public static final RegistryObject<SoundEvent> INFECTED_COW_IDLE = register("entity.infected_cow.idle");
    public static final RegistryObject<SoundEvent> INFECTED_COW_HURT = register("entity.infected_cow.hurt");
    public static final RegistryObject<SoundEvent> INFECTED_COW_DEATH = register("entity.infected_cow.death");
    
    public static final RegistryObject<SoundEvent> INFECTED_PIG_IDLE = register("entity.infected_pig.idle");
    public static final RegistryObject<SoundEvent> INFECTED_PIG_HURT = register("entity.infected_pig.hurt");
    public static final RegistryObject<SoundEvent> INFECTED_PIG_DEATH = register("entity.infected_pig.death");
    
    public static final RegistryObject<SoundEvent> INFECTED_SHEEP_IDLE = register("entity.infected_sheep.idle");
    public static final RegistryObject<SoundEvent> INFECTED_SHEEP_HURT = register("entity.infected_sheep.hurt");
    public static final RegistryObject<SoundEvent> INFECTED_SHEEP_DEATH = register("entity.infected_sheep.death");

    // === Primitive Stage Sounds ===
    public static final RegistryObject<SoundEvent> BANO_IDLE = register("entity.bano.idle");
    public static final RegistryObject<SoundEvent> BANO_HURT = register("entity.bano.hurt");
    public static final RegistryObject<SoundEvent> BANO_DEATH = register("entity.bano.death");
    public static final RegistryObject<SoundEvent> BANO_ATTACK = register("entity.bano.attack");

    public static final RegistryObject<SoundEvent> CANRA_IDLE = register("entity.canra.idle");
    public static final RegistryObject<SoundEvent> CANRA_ATTACK = register("entity.canra.attack");

    public static final RegistryObject<SoundEvent> HULL_IDLE = register("entity.hull.idle");

    // === Crude Stage Sounds ===
    public static final RegistryObject<SoundEvent> HOST_IDLE = register("entity.host.idle");
    public static final RegistryObject<SoundEvent> HOST_ATTACK = register("entity.host.attack");

    public static final RegistryObject<SoundEvent> LEER_IDLE = register("entity.leer.idle");
    public static final RegistryObject<SoundEvent> LEER_ATTACK = register("entity.leer.attack");

    public static final RegistryObject<SoundEvent> CRUX_IDLE = register("entity.crux.idle");
    public static final RegistryObject<SoundEvent> CRUX_HURT = register("entity.crux.hurt");
    public static final RegistryObject<SoundEvent> CRUX_DEATH = register("entity.crux.death");

    // === Derived Stage Sounds ===
    public static final RegistryObject<SoundEvent> HEBLU_IDLE = register("entity.heblu.idle");
    public static final RegistryObject<SoundEvent> HEBLU_HURT = register("entity.heblu.hurt");
    public static final RegistryObject<SoundEvent> HEBLU_DEATH = register("entity.heblu.death");
    public static final RegistryObject<SoundEvent> HEBLU_SHOOT = register("entity.heblu.shoot");
    public static final RegistryObject<SoundEvent> HEBLU_ATTACK = register("entity.heblu.attack");

    public static final RegistryObject<SoundEvent> KIRIN_IDLE = register("entity.kirin.idle");
    public static final RegistryObject<SoundEvent> KIRIN_HURT = register("entity.kirin.hurt");
    public static final RegistryObject<SoundEvent> KIRIN_DEATH = register("entity.kirin.death");
    public static final RegistryObject<SoundEvent> KIRIN_ATTACK = register("entity.kirin.attack");

    // === Adapted Stage Sounds ===
    public static final RegistryObject<SoundEvent> ADAPTED_QUADRUPEDE_IDLE = register("entity.adapted_quadrupede.idle");
    public static final RegistryObject<SoundEvent> ADAPTED_QUADRUPEDE_HURT = register("entity.adapted_quadrupede.hurt");
    public static final RegistryObject<SoundEvent> ADAPTED_QUADRUPEDE_DEATH = register("entity.adapted_quadrupede.death");
    public static final RegistryObject<SoundEvent> ADAPTED_QUADRUPEDE_ATTACK = register("entity.adapted_quadrupede.attack");

    public static final RegistryObject<SoundEvent> ADAPTED_FLY_IDLE = register("entity.adapted_fly.idle");
    public static final RegistryObject<SoundEvent> ADAPTED_FLY_HURT = register("entity.adapted_fly.hurt");
    public static final RegistryObject<SoundEvent> ADAPTED_FLY_DEATH = register("entity.adapted_fly.death");
    public static final RegistryObject<SoundEvent> ADAPTED_FLY_ATTACK = register("entity.adapted_fly.attack");

    public static final RegistryObject<SoundEvent> ADAPTED_ARACHNID_IDLE = register("entity.adapted_arachnid.idle");
    public static final RegistryObject<SoundEvent> ADAPTED_ARACHNID_HURT = register("entity.adapted_arachnid.hurt");
    public static final RegistryObject<SoundEvent> ADAPTED_ARACHNID_DEATH = register("entity.adapted_arachnid.death");

    // === Beckon Sounds ===
    public static final RegistryObject<SoundEvent> BECKON_IDLE = register("entity.beckon.idle");
    public static final RegistryObject<SoundEvent> BECKON_HURT = register("entity.beckon.hurt");
    public static final RegistryObject<SoundEvent> BECKON_DEATH = register("entity.beckon.death");
    public static final RegistryObject<SoundEvent> BECKON_ATTACK = register("entity.beckon.attack");

    // === Nexus Sounds ===
    public static final RegistryObject<SoundEvent> NEXUS_IDLE = register("entity.nexus.idle");
    public static final RegistryObject<SoundEvent> NEXUS_HURT = register("entity.nexus.hurt");
    public static final RegistryObject<SoundEvent> NEXUS_DEATH = register("entity.nexus.death");
    public static final RegistryObject<SoundEvent> NEXUS_ATTACK = register("entity.nexus.attack");

    // === Rooter Sounds ===
    public static final RegistryObject<SoundEvent> ROOTER_IDLE = register("entity.rooter.idle");
    public static final RegistryObject<SoundEvent> ROOTER_HURT = register("entity.rooter.hurt");
    public static final RegistryObject<SoundEvent> ROOTER_ATTACK = register("entity.rooter.attack");

    // === Dispatcher Sounds ===
    public static final RegistryObject<SoundEvent> DISPATCHER_IDLE = register("entity.dispatcher.idle");
    public static final RegistryObject<SoundEvent> DISPATCHER_HURT = register("entity.dispatcher.hurt");
    public static final RegistryObject<SoundEvent> DISPATCHER_ATTACK = register("entity.dispatcher.attack");

    // === Assimilated Mob Sounds ===
    public static final RegistryObject<SoundEvent> ASSIMILATED_IDLE = register("entity.assimilated.idle");
    public static final RegistryObject<SoundEvent> ASSIMILATED_HURT = register("entity.assimilated.hurt");
    public static final RegistryObject<SoundEvent> ASSIMILATED_DEATH = register("entity.assimilated.death");
    public static final RegistryObject<SoundEvent> ASSIMILATED_ATTACK = register("entity.assimilated.attack");
    
    // Specific assimilated variants
    public static final RegistryObject<SoundEvent> ASSIMILATED_HUMAN_IDLE = register("entity.assimilated_human.idle");
    public static final RegistryObject<SoundEvent> ASSIMILATED_HUMAN_HURT = register("entity.assimilated_human.hurt");
    public static final RegistryObject<SoundEvent> ASSIMILATED_HUMAN_DEATH = register("entity.assimilated_human.death");
    public static final RegistryObject<SoundEvent> ASSIMILATED_HUMAN_ATTACK = register("entity.assimilated_human.attack");
    
    public static final RegistryObject<SoundEvent> ASSIMILATED_COW_IDLE = register("entity.assimilated_cow.idle");
    public static final RegistryObject<SoundEvent> ASSIMILATED_COW_HURT = register("entity.assimilated_cow.hurt");
    public static final RegistryObject<SoundEvent> ASSIMILATED_COW_DEATH = register("entity.assimilated_cow.death");
    public static final RegistryObject<SoundEvent> ASSIMILATED_COW_ATTACK = register("entity.assimilated_cow.attack");
    
    public static final RegistryObject<SoundEvent> ASSIMILATED_SHEEP_IDLE = register("entity.assimilated_sheep.idle");
    public static final RegistryObject<SoundEvent> ASSIMILATED_SHEEP_HURT = register("entity.assimilated_sheep.hurt");
    public static final RegistryObject<SoundEvent> ASSIMILATED_SHEEP_DEATH = register("entity.assimilated_sheep.death");
    public static final RegistryObject<SoundEvent> ASSIMILATED_SHEEP_ATTACK = register("entity.assimilated_sheep.attack");

    // === Feral Sounds ===
    public static final RegistryObject<SoundEvent> FERAL_IDLE = register("entity.feral.idle");
    public static final RegistryObject<SoundEvent> FERAL_HURT = register("entity.feral.hurt");
    public static final RegistryObject<SoundEvent> FERAL_ATTACK = register("entity.feral.attack");
    
    // Specific feral variants
    public static final RegistryObject<SoundEvent> FERAL_HUMAN_IDLE = register("entity.feral_human.idle");
    public static final RegistryObject<SoundEvent> FERAL_HUMAN_HURT = register("entity.feral_human.hurt");
    public static final RegistryObject<SoundEvent> FERAL_HUMAN_DEATH = register("entity.feral_human.death");
    public static final RegistryObject<SoundEvent> FERAL_HUMAN_ATTACK = register("entity.feral_human.attack");
    
    public static final RegistryObject<SoundEvent> FERAL_COW_IDLE = register("entity.feral_cow.idle");
    public static final RegistryObject<SoundEvent> FERAL_COW_HURT = register("entity.feral_cow.hurt");
    public static final RegistryObject<SoundEvent> FERAL_COW_DEATH = register("entity.feral_cow.death");
    public static final RegistryObject<SoundEvent> FERAL_COW_ATTACK = register("entity.feral_cow.attack");
    
    public static final RegistryObject<SoundEvent> FERAL_SHEEP_IDLE = register("entity.feral_sheep.idle");
    public static final RegistryObject<SoundEvent> FERAL_SHEEP_HURT = register("entity.feral_sheep.hurt");
    public static final RegistryObject<SoundEvent> FERAL_SHEEP_DEATH = register("entity.feral_sheep.death");
    public static final RegistryObject<SoundEvent> FERAL_SHEEP_ATTACK = register("entity.feral_sheep.attack");

    // === Ancient Sounds ===
    public static final RegistryObject<SoundEvent> ANCIENT_IDLE = register("entity.ancient.idle");
    public static final RegistryObject<SoundEvent> ANCIENT_HURT = register("entity.ancient.hurt");
    public static final RegistryObject<SoundEvent> ANCIENT_DEATH = register("entity.ancient.death");

    // === Preeminent Sounds ===
    public static final RegistryObject<SoundEvent> PREEMINENT_IDLE = register("entity.preeminent.idle");
    public static final RegistryObject<SoundEvent> PREEMINENT_HURT = register("entity.preeminent.hurt");
    public static final RegistryObject<SoundEvent> PREEMINENT_DEATH = register("entity.preeminent.death");

    // === Abomination Sounds ===
    public static final RegistryObject<SoundEvent> ABOMINATION_IDLE = register("entity.abomination.idle");
    public static final RegistryObject<SoundEvent> ABOMINATION_HURT = register("entity.abomination.hurt");
    public static final RegistryObject<SoundEvent> ABOMINATION_DEATH = register("entity.abomination.death");
    public static final RegistryObject<SoundEvent> ABOMINATION_ATTACK = register("entity.abomination.attack");

    // === Venkrol Boss Sounds ===
    public static final RegistryObject<SoundEvent> VENKROL_IDLE = register("entity.venkrol.idle");
    public static final RegistryObject<SoundEvent> VENKROL_HURT = register("entity.venkrol.hurt");
    public static final RegistryObject<SoundEvent> VENKROL_DEATH = register("entity.venkrol.death");
    public static final RegistryObject<SoundEvent> VENKROL_ATTACK = register("entity.venkrol.attack");

    // === Inborn Entity Sounds (SUBSRP_ENTITY_*) ===
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_ATA_AMBIENT = register("entity.ata.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_ATA_HURT = register("entity.ata.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_ATA_DEATH = register("entity.ata.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_BUTHOL_AMBIENT = register("entity.buthol.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_BUTHOL_HURT = register("entity.buthol.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_BUTHOL_DEATH = register("entity.buthol.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_GOTHOL_AMBIENT = register("entity.gothol.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_GOTHOL_HURT = register("entity.gothol.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_GOTHOL_DEATH = register("entity.gothol.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_RATHOL_AMBIENT = register("entity.rathol.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_RATHOL_HURT = register("entity.rathol.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_RATHOL_DEATH = register("entity.rathol.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_LESH_AMBIENT = register("entity.lesh.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_LESH_HURT = register("entity.lesh.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_LESH_DEATH = register("entity.lesh.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_VIIN_AMBIENT = register("entity.viin.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_VIIN_HURT = register("entity.viin.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_VIIN_DEATH = register("entity.viin.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_LODO_AMBIENT = register("entity.lodo.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_LODO_HURT = register("entity.lodo.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_LODO_DEATH = register("entity.lodo.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_MOR_AMBIENT = register("entity.mor.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_MOR_HURT = register("entity.mor.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_MOR_DEATH = register("entity.mor.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_MUDO_AMBIENT = register("entity.mudo.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_MUDO_HURT = register("entity.mudo.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_MUDO_DEATH = register("entity.mudo.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_KOL_AMBIENT = register("entity.kol.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_KOL_HURT = register("entity.kol.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_KOL_DEATH = register("entity.kol.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_NUUH_AMBIENT = register("entity.nuuh.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_NUUH_HURT = register("entity.nuuh.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_NUUH_DEATH = register("entity.nuuh.death");

    // === Crude Entity Sounds (SUBSRP_ENTITY_*) ===
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_CRUXA_AMBIENT = register("entity.cruxa.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_CRUXA_HURT = register("entity.cruxa.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_CRUXA_DEATH = register("entity.cruxa.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_CRUXB_AMBIENT = register("entity.cruxb.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_CRUXB_HURT = register("entity.cruxb.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_CRUXB_DEATH = register("entity.cruxb.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_QUAC_AMBIENT = register("entity.quac.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_QUAC_HURT = register("entity.quac.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_QUAC_DEATH = register("entity.quac.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_LEER_AMBIENT = register("entity.leer.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_LEER_HURT = register("entity.leer.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_LEER_DEATH = register("entity.leer.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_HOST_AMBIENT = register("entity.host.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_HOST_HURT = register("entity.host.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_HOST_DEATH = register("entity.host.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_HEED_AMBIENT = register("entity.heed.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_HEED_HURT = register("entity.heed.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_HEED_DEATH = register("entity.heed.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_INHOOS_AMBIENT = register("entity.inhoos.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_INHOOS_HURT = register("entity.inhoos.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_INHOOS_DEATH = register("entity.inhoos.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_DONE_AMBIENT = register("entity.done.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_DONE_HURT = register("entity.done.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_DONE_DEATH = register("entity.done.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_INHOOM_AMBIENT = register("entity.inhoom.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_INHOOM_HURT = register("entity.inhoom.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_INHOOM_DEATH = register("entity.inhoom.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_MES_AMBIENT = register("entity.mes.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_MES_HURT = register("entity.mes.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_MES_DEATH = register("entity.mes.death");

    // === Primitive Entity Sounds (SUBSRP_ENTITY_*) ===
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_SHYCO_AMBIENT = register("entity.shyco.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_SHYCO_HURT = register("entity.shyco.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_SHYCO_DEATH = register("entity.shyco.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_ZAA_AMBIENT = register("entity.zaa.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_ZAA_HURT = register("entity.zaa.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_ZAA_DEATH = register("entity.zaa.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_IKI_AMBIENT = register("entity.iki.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_IKI_HURT = register("entity.iki.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_IKI_DEATH = register("entity.iki.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_RANRAC_AMBIENT = register("entity.ranrac.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_RANRAC_HURT = register("entity.ranrac.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_RANRAC_DEATH = register("entity.ranrac.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_WYMO_AMBIENT = register("entity.wymo.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_WYMO_HURT = register("entity.wymo.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_WYMO_DEATH = register("entity.wymo.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_CANRA_AMBIENT = register("entity.canra.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_CANRA_HURT = register("entity.canra.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_CANRA_DEATH = register("entity.canra.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_NOGLA_AMBIENT = register("entity.nogla.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_NOGLA_HURT = register("entity.nogla.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_NOGLA_DEATH = register("entity.nogla.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_HULL_AMBIENT = register("entity.hull.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_HULL_HURT = register("entity.hull.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_HULL_DEATH = register("entity.hull.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_GIM_AMBIENT = register("entity.gim.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_GIM_HURT = register("entity.gim.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_GIM_DEATH = register("entity.gim.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_LUM_AMBIENT = register("entity.lum.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_LUM_HURT = register("entity.lum.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_LUM_DEATH = register("entity.lum.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_EMANA_AMBIENT = register("entity.emana.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_EMANA_HURT = register("entity.emana.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_EMANA_DEATH = register("entity.emana.death");

    // === Adapted Entity Sounds (SUBSRP_ENTITY_*) ===
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_BANO_ADAPTED_AMBIENT = register("entity.bano_adapted.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_BANO_ADAPTED_HURT = register("entity.bano_adapted.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_BANO_ADAPTED_DEATH = register("entity.bano_adapted.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_CANRA_ADAPTED_AMBIENT = register("entity.canra_adapted.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_CANRA_ADAPTED_HURT = register("entity.canra_adapted.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_CANRA_ADAPTED_DEATH = register("entity.canra_adapted.death");

    // === Pure Entity Sounds (SUBSRP_ENTITY_*) ===
    public static final RegistryObject<SoundEvent> PURE_IDLE = register("entity.pure.idle");
    public static final RegistryObject<SoundEvent> PURE_HURT = register("entity.pure.hurt");
    public static final RegistryObject<SoundEvent> PURE_DEATH = register("entity.pure.death");
    public static final RegistryObject<SoundEvent> PURE_ATTACK = register("entity.pure.attack");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_FLAM_AMBIENT = register("entity.flam.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_FLAM_HURT = register("entity.flam.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_FLAM_DEATH = register("entity.flam.death");

    // === Infected Variant Sounds (SUBSRP_ENTITY_*) ===
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_INFECTED_CREEPER_AMBIENT = register("entity.infected_creeper.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_INFECTED_CREEPER_HURT = register("entity.infected_creeper.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_INFECTED_CREEPER_DEATH = register("entity.infected_creeper.death");

    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_INFECTED_ENDERMAN_AMBIENT = register("entity.infected_enderman.ambient");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_INFECTED_ENDERMAN_HURT = register("entity.infected_enderman.hurt");
    public static final RegistryObject<SoundEvent> SUBSRP_ENTITY_INFECTED_ENDERMAN_DEATH = register("entity.infected_enderman.death");

    // === Block Sounds ===
    public static final RegistryObject<SoundEvent> BLOCK_INFEST = register("block.infest");
    public static final RegistryObject<SoundEvent> BLOCK_DEINFEST = register("block.deinfest");
    public static final RegistryObject<SoundEvent> BLOCK_PARASITE_BREAK = register("block.parasite.break");
    public static final RegistryObject<SoundEvent> BLOCK_PARASITE_STEP = register("block.parasite.step");
    public static final RegistryObject<SoundEvent> BLOCK_PARASITE_PLACE = register("block.parasite.place");
    
    // === Relay System Sounds ===
    public static final RegistryObject<SoundEvent> RELAY_ACTIVATE = register("block.relay.activate");
    public static final RegistryObject<SoundEvent> RELAY_PAPER = register("block.relay.paper");
    
    // === Biome Heart Sounds ===
    public static final RegistryObject<SoundEvent> BIOME_HEART_BEAT = register("block.biome_heart.beat");
    public static final RegistryObject<SoundEvent> BIOME_HEART_DEATH = register("block.biome_heart.death");

    // === Colony Sounds ===
    public static final RegistryObject<SoundEvent> COLONY_HEART_IDLE = register("block.colony_heart.idle");
    public static final RegistryObject<SoundEvent> COLONY_HEART_HURT = register("block.colony_heart.hurt");
    public static final RegistryObject<SoundEvent> COLONY_HEART_DEATH = register("block.colony_heart.death");
    public static final RegistryObject<SoundEvent> COLONY_OUTPOST_IDLE = register("block.colony_outpost.idle");
    
    // === Alveoli Sounds ===
    public static final RegistryObject<SoundEvent> ALVEOLI_HATCH = register("block.alveoli.hatch");
    public static final RegistryObject<SoundEvent> ALVEOLI_AMBIENT = register("block.alveoli.ambient");

    // === Item Sounds ===
    public static final RegistryObject<SoundEvent> ITEM_LIVING_ARMOR_EQUIP = register("item.living_armor.equip");
    public static final RegistryObject<SoundEvent> ITEM_SENTIENT_ARMOR_EQUIP = register("item.sentient_armor.equip");
    public static final RegistryObject<SoundEvent> ITEM_INFESTED_TOOL_EQUIP = register("item.infested_tool.equip");

    // === Evolution Sounds ===
    public static final RegistryObject<SoundEvent> EVOLUTION_UPGRADE = register("evolution.upgrade");
    public static final RegistryObject<SoundEvent> EVOLUTION_LEVEL = register("evolution.level");
    public static final RegistryObject<SoundEvent> EVOLUTION_COMPLETE = register("evolution.complete");

    // === Ambient Sounds ===
    public static final RegistryObject<SoundEvent> AMBIENT_PARASITE = register("ambient.parasite");
    public static final RegistryObject<SoundEvent> AMBIENT_SPORE = register("ambient.spore");
    public static final RegistryObject<SoundEvent> AMBIENT_COLONY = register("ambient.colony");
    public static final RegistryObject<SoundEvent> AMBIENT_WASTE = register("ambient.waste");

    // === Projectile Sounds ===
    public static final RegistryObject<SoundEvent> PROJECTILE_HIT = register("entity.projectile.hit");
    public static final RegistryObject<SoundEvent> PROJECTILE_SHOOT = register("entity.projectile.shoot");
    public static final RegistryObject<SoundEvent> PROJECTILE_FLY = register("entity.projectile.fly");

    // === Dislodgment/Infection Sounds ===
    public static final RegistryObject<SoundEvent> DISLODGEMENT_START = register("effect.dislodgement.start");
    public static final RegistryObject<SoundEvent> DISLODGEMENT_END = register("effect.dislodgement.end");
    public static final RegistryObject<SoundEvent> INFECTION_SPREAD = register("effect.infection.spread");

    private ModSounds() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Get a sound event by its original SRP key name.
     * Provides fallback to a default sound if the key is not found, preventing crashes.
     * 
     * @param originalKey The original SRP sound key (without prefix)
     * @return The RegistryObject for the sound event
     */
    public static RegistryObject<SoundEvent> getSafe(String originalKey) {
        return SOUND_CACHE.getOrDefault(originalKey, PARASITE_IDLE);
    }
    
    /**
     * Check if a sound event exists in the registry.
     * Useful for conditional sound playback.
     */
    public static boolean hasSound(String originalKey) {
        return SOUND_CACHE.containsKey(originalKey);
    }
}
