package com.subspaceparasite.core;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.core.registries.Registries;
import com.subspaceparasite.SubspaceParasite;
import net.minecraftforge.registries.RegistryObject;

/**
 * Creative tab registry for the SubspaceParasite mod.
 * Single creative tab matching original SRP: "Scape and Run: Parasites"
 * Only items explicitly set with this tab will appear.
 */
public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SubspaceParasite.MOD_ID);

    public static final RegistryObject<CreativeModeTab> SRPARASITES = CREATIVE_MODE_TABS.register("srparasites",
            () -> CreativeModeTab.builder()
                    .title(net.minecraft.network.chat.Component.translatable("itemGroup.subspaceparasite"))
                    .icon(() -> new ItemStack(ModItems.BIOME_HEART_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Only items explicitly added below appear in the tab.
                        // No automatic additions from other tabs.

                        // === System Blocks ===
                        output.accept(ModItems.BIOME_HEART_ITEM.get());
                        output.accept(ModItems.COLONY_HEART_ITEM.get());
                        output.accept(ModItems.COLONY_OUTPOST_ITEM.get());
                        output.accept(ModItems.BIOME_PURIFIER_ITEM.get());
                        output.accept(ModItems.RELAY_CONTROLLER_ITEM.get());
                        output.accept(ModItems.PARASITE_BARRIER_ITEM.get());

                        // === Functional Blocks ===
                        output.accept(ModItems.EVOLUTION_LURE_ITEM.get());
                        output.accept(ModItems.INFESTATION_PURIFIER_ITEM.get());
                        output.accept(ModItems.FOG_NULLIFIER_ITEM.get());
                        output.accept(ModItems.INFESTED_FURNACE_ITEM.get());
                        output.accept(ModItems.INFUSER_FURNACE_ITEM.get());
                        output.accept(ModItems.CONSUMED_WORKBENCH_ITEM.get());
                        output.accept(ModItems.INFESTED_WORKBENCH_ITEM.get());

                        // === Escal Bulbs ===
                        output.accept(ModItems.ESCA_BULB_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_WHITE_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_ORANGE_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_MAGENTA_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_LIGHT_BLUE_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_YELLOW_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_LIME_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_PINK_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_GRAY_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_LIGHT_GRAY_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_CYAN_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_PURPLE_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_BLUE_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_BROWN_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_GREEN_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_RED_ITEM.get());
                        output.accept(ModItems.ESCA_BULB_BLACK_ITEM.get());

                        // === Wood & Planks ===
                        output.accept(ModItems.GOTH_STEM_ITEM.get());
                        output.accept(ModItems.GOTH_PLANKS_ITEM.get());
                        output.accept(ModItems.FLESH_PLANKS_ITEM.get());
                        output.accept(ModItems.COOKED_FLESH_PLANKS_ITEM.get());
                        output.accept(ModItems.BRUSEWOOD_PLANKS_ITEM.get());
                        output.accept(ModItems.CONSUMED_PLANKS_ITEM.get());

                        // === Doors ===
                        output.accept(ModItems.GOTH_DOOR_ITEM.get());
                        output.accept(ModItems.BRUSEWOOD_DOOR_ITEM.get());
                        output.accept(ModItems.CONSUMED_DOOR_ITEM.get());

                        // === Trapdoors ===
                        output.accept(ModItems.BRUSEWOOD_TRAPDOOR_ITEM.get());
                        output.accept(ModItems.CONSUMED_TRAPDOOR_ITEM.get());
                        output.accept(ModItems.GOTH_TRAPDOOR_ITEM.get());

                        // === Glass ===
                        output.accept(ModItems.ASHEN_GLASS_ITEM.get());
                        output.accept(ModItems.SHROUDED_GLASS_ITEM.get());
                        output.accept(ModItems.HARLEQUINN_GLASS_ITEM.get());
                        output.accept(ModItems.BLOODY_GLASS_ITEM.get());
                        output.accept(ModItems.INFESTED_GLASS_ITEM.get());
                        output.accept(ModItems.SHADE_GLASS_ITEM.get());
                        output.accept(ModItems.SEPIA_GLASS_ITEM.get());
                        output.accept(ModItems.MOODY_GLASS_ITEM.get());

                        // === Glass Panes ===
                        output.accept(ModItems.ASHEN_GLASS_PANE_ITEM.get());
                        output.accept(ModItems.SHROUDED_GLASS_PANE_ITEM.get());
                        output.accept(ModItems.HARLEQUINN_GLASS_PANE_ITEM.get());
                        output.accept(ModItems.BLOODY_GLASS_PANE_ITEM.get());
                        output.accept(ModItems.INFESTED_GLASS_PANE_ITEM.get());
                        output.accept(ModItems.SHADE_GLASS_PANE_ITEM.get());
                        output.accept(ModItems.SEPIA_GLASS_PANE_ITEM.get());
                        output.accept(ModItems.MOODY_GLASS_PANE_ITEM.get());

                        // === Trophies ===
                        output.accept(ModItems.TROPHY_VOID_ORB_ITEM.get());
                        output.accept(ModItems.TROPHY_BOOM_ORB_ITEM.get());

                        // === Flora & Sapling ===
                        output.accept(ModItems.PARASITE_SAPLING_ITEM.get());
                        output.accept(ModItems.ASSIMILATED_BLOSSOM_ITEM.get());
                        output.accept(ModItems.THORNSHADE_ITEM.get());
                        output.accept(ModItems.GOTH_SHROOM_ITEM.get());

                        // === Lamps ===
                        output.accept(ModItems.NODE_REDSTONE_LAMP_ITEM.get());
                        output.accept(ModItems.NODE_LAMP_ITEM.get());
                        output.accept(ModItems.BLOODY_ICE_ITEM.get());

                        // === Module Items (22) ===
                        output.accept(ModItems.MODULE_ADAPTER.get());
                        output.accept(ModItems.MODULE_BARRICADE.get());
                        output.accept(ModItems.MODULE_DYNAMO.get());
                        output.accept(ModItems.MODULE_EXOTHERMIC.get());
                        output.accept(ModItems.MODULE_FERROMAGNETIC.get());
                        output.accept(ModItems.MODULE_GRAVITATIONAL.get());
                        output.accept(ModItems.MODULE_HYPERTHREAT.get());
                        output.accept(ModItems.MODULE_INSULATING.get());
                        output.accept(ModItems.MODULE_KINETIC.get());
                        output.accept(ModItems.MODULE_LUMINOUS.get());
                        output.accept(ModItems.MODULE_MOTILE.get());
                        output.accept(ModItems.MODULE_NUTRIENT.get());
                        output.accept(ModItems.MODULE_OUTREACH.get());
                        output.accept(ModItems.MODULE_PHEROMONE.get());
                        output.accept(ModItems.MODULE_QUANTUM.get());
                        output.accept(ModItems.MODULE_RESILIENT.get());
                        output.accept(ModItems.MODULE_SIEGE.get());
                        output.accept(ModItems.MODULE_THORNIAN.get());
                        output.accept(ModItems.MODULE_UMBRELLA.get());
                        output.accept(ModItems.MODULE_VENOMOUS.get());
                        output.accept(ModItems.MODULE_WANDERER.get());
                        output.accept(ModItems.MODULE_XENOLITHIC.get());

                        // === Weapons ===
                        output.accept(ModItems.PARASITE_CLAW.get());
                        output.accept(ModItems.PARASITE_FANG.get());
                        output.accept(ModItems.PARASITE_SPIKE.get());
                        output.accept(ModItems.PARASITE_BLADE.get());
                        output.accept(ModItems.BECKON_WEAPON.get());
                        output.accept(ModItems.PARASITE_AXE.get());
                        output.accept(ModItems.PARASITE_BOW.get());
                        output.accept(ModItems.SENTIENT_CLAW.get());
                        output.accept(ModItems.SENTIENT_FANG.get());
                        output.accept(ModItems.SENTIENT_SPIKE.get());
                        output.accept(ModItems.SENTIENT_BLADE.get());
                        output.accept(ModItems.SENTIENT_BECKON_WEAPON.get());
                        output.accept(ModItems.SENTIENT_AXE.get());
                        output.accept(ModItems.SENTIENT_BOW.get());

                        // === Living Armor ===
                        output.accept(ModItems.LIVING_HELMET.get());
                        output.accept(ModItems.LIVING_CHESTPLATE.get());
                        output.accept(ModItems.LIVING_LEGGINGS.get());
                        output.accept(ModItems.LIVING_BOOTS.get());

                        // === Sentient Armor ===
                        output.accept(ModItems.SENTIENT_HELMET.get());
                        output.accept(ModItems.SENTIENT_CHESTPLATE.get());
                        output.accept(ModItems.SENTIENT_LEGGINGS.get());
                        output.accept(ModItems.SENTIENT_BOOTS.get());

                        // === Hijacked Iron Armor ===
                        output.accept(ModItems.HIJACKED_IRON_HELMET.get());
                        output.accept(ModItems.HIJACKED_IRON_CHESTPLATE.get());
                        output.accept(ModItems.HIJACKED_IRON_LEGGINGS.get());
                        output.accept(ModItems.HIJACKED_IRON_BOOTS.get());

                        // === Hijacked Iron Tools ===
                        output.accept(ModItems.HIJACKED_IRON_SWORD.get());
                        output.accept(ModItems.HIJACKED_IRON_PICKAXE.get());
                        output.accept(ModItems.HIJACKED_IRON_AXE.get());
                        output.accept(ModItems.HIJACKED_IRON_SHOVEL.get());
                        output.accept(ModItems.HIJACKED_IRON_HOE.get());

                        // === Evolution/Development Clocks & Compasses ===
                        output.accept(ModItems.EVOLUTION_CLOCK.get());
                        output.accept(ModItems.DEVELOPMENT_CLOCK.get());
                        output.accept(ModItems.EVOLUTION_COMPASS.get());
                        output.accept(ModItems.DEVELOPMENT_COMPASS.get());
                        output.accept(ModItems.PARASITE_COMPASS.get());

                        // === Field Guide & Pearl ===
                        output.accept(ModItems.FIELD_GUIDE.get());
                        output.accept(ModItems.PARASITE_PEARL.get());

                        // === Drops & Craft Materials ===
                        output.accept(ModItems.PARASITE_FLESH.get());
                        output.accept(ModItems.COOKED_PARASITE_FLESH.get());
                        output.accept(ModItems.PARASITE_TENDON.get());
                        output.accept(ModItems.PARASITE_BONE.get());
                        output.accept(ModItems.PARASITE_MEMBRANE.get());
                        output.accept(ModItems.PARASITE_SHELL.get());
                        output.accept(ModItems.PARASITE_CORE.get());
                        output.accept(ModItems.BIOMASS.get());
                        output.accept(ModItems.RESIDUE.get());
                        output.accept(ModItems.VIRULENT_RESIDUE.get());
                        output.accept(ModItems.INFESTED_RESIDUE.get());
                        output.accept(ModItems.PARASITE_NODULE.get());
                        output.accept(ModItems.PARASITE_GLAND.get());
                        output.accept(ModItems.WEAK_NODE.get());
                        output.accept(ModItems.STRONG_NODE.get());
                        output.accept(ModItems.PARASITE_CLAW_DROP.get());
                        output.accept(ModItems.PARASITE_HEART.get());
                        output.accept(ModItems.GOOTH.get());
                        output.accept(ModItems.SPORE.get());
                        output.accept(ModItems.MUCUS.get());
                        output.accept(ModItems.ACID.get());
                        output.accept(ModItems.BILE.get());
                        output.accept(ModItems.HAIR_FOLLICLE_ITEM.get());
                        output.accept(ModItems.HIRSUTE_HAIR_ITEM.get());
                        output.accept(ModItems.TRESSES_HAIR_ITEM.get());
                        output.accept(ModItems.LIPOPA_MASS_ITEM.get());
                        output.accept(ModItems.LOCS_ITEM.get());

                        // === Food Items ===
                        output.accept(ModItems.RAW_PARASITE.get());
                        output.accept(ModItems.COOKED_PARASITE.get());
                        output.accept(ModItems.BIOMASS_FOOD.get());
                        output.accept(ModItems.BLOODY_MEAT.get());
                        output.accept(ModItems.DERMOID_CYST_FOOD.get());

                        // === Lure Components ===
                        output.accept(ModItems.LURE_BASE.get());
                        output.accept(ModItems.LURE_PRIMORDIAL.get());
                        output.accept(ModItems.LURE_ADAPTIVE.get());
                        output.accept(ModItems.LURE_FERAL.get());
                        output.accept(ModItems.LURE_PURE.get());

                        // === Functional Items ===
                        output.accept(ModItems.PARASITE_BOMB.get());
                        output.accept(ModItems.PURIFYING_SALVE.get());
                        output.accept(ModItems.ANTIDOTE.get());
                        output.accept(ModItems.CLEANSING_TOTEM.get());
                        output.accept(ModItems.INFESTATION_NEEDLE.get());
                        output.accept(ModItems.EVOLUTION_CATALYST.get());
                        output.accept(ModItems.CALL_OF_THE_HIVE.get());
                        output.accept(ModItems.PARASITE_SYRINGE.get());
                        output.accept(ModItems.BECKON_ESSENCE.get());

                        // === Music Discs ===
                        output.accept(ModItems.MUSIC_DISC_EVO.get());
                        output.accept(ModItems.MUSIC_DISC_ASSIMILATE.get());

                        // === Fluid Bucket ===
                        output.accept(ModItems.DEAD_BLOOD_BUCKET.get());

                        // === Spawn Eggs (Sorted by Category) ===
                        // Infected Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_INFECTED_CREEPER.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_SKELETON.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_ZOMBIE.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_SPIDER.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_ENDERMAN.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_PIG.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_COW.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_SHEEP.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_CHICKEN.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_VILLAGER.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_WOLF.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_HORSE.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_HUMAN.get());
                        
                        // Feral Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_FERAL_CREEPER.get());
                        output.accept(ModItems.SPAWN_EGG_FERAL_SKELETON.get());
                        output.accept(ModItems.SPAWN_EGG_FERAL_ZOMBIE.get());
                        output.accept(ModItems.SPAWN_EGG_FERAL_SPIDER.get());
                        
                        // Hijacked Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_HIJACKED_CREEPER.get());
                        output.accept(ModItems.SPAWN_EGG_HIJACKED_SKELETON.get());
                        output.accept(ModItems.SPAWN_EGG_HIJACKED_ZOMBIE.get());
                        
                        // Inborn Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_INBORN_1.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_2.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_3.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_4.get());
                        
                        // Crude Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_CRUDE_1.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_2.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_3.get());
                        
                        // Primitive Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_1.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_2.get());
                        
                        // Adapted Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_1.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_2.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_3.get());
                        
                        // Nexus Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_NEXUS_BECKON.get());
                        output.accept(ModItems.SPAWN_EGG_NEXUS_DISPATCHER.get());
                        output.accept(ModItems.SPAWN_EGG_NEXUS_ROOTER.get());
                        output.accept(ModItems.SPAWN_EGG_NEXUS_OTHER.get());
                        
                        // Deterrent Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_DETERRENT_1.get());
                        output.accept(ModItems.SPAWN_EGG_DETERRENT_2.get());
                        
                        // Pure Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_PURE_1.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_2.get());
                        
                        // Preeminent Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_PREEMINENT_1.get());
                        output.accept(ModItems.SPAWN_EGG_PREEMINENT_2.get());
                        
                        // Ancient Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_ANCIENT_1.get());
                        output.accept(ModItems.SPAWN_EGG_ANCIENT_2.get());
                        
                        // Derived Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_DERIVED_1.get());
                        output.accept(ModItems.SPAWN_EGG_DERIVED_2.get());
                        
                        // Abomination Spawn Eggs
                        output.accept(ModItems.SPAWN_EGG_ABOMINATION_1.get());
                        output.accept(ModItems.SPAWN_EGG_ABOMINATION_2.get());
                    })
                    .build());

    private ModCreativeTabs() {
        // Utility class - prevent instantiation
    }
}
