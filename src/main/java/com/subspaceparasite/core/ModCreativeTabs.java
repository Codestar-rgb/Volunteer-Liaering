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
                    .title(net.minecraft.network.chat.Component.translatable("itemGroup.subspaceparasite.main"))
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

                        // === Infested Building Blocks ===
                        output.accept(ModItems.INFESTED_COBBLESTONE_ITEM.get());
                        output.accept(ModItems.INFESTED_PLANKS_ITEM.get());
                        output.accept(ModItems.INFESTED_STONE_BRICKS_ITEM.get());
                        output.accept(ModItems.INFESTED_TERRACOTTA_ITEM.get());
                        output.accept(ModItems.POLISHED_INFECTED_STONE_ITEM.get());
                        output.accept(ModItems.RESIDUE_BRICKS_ITEM.get());
                        output.accept(ModItems.INFESTED_COLUMN_ITEM.get());
                        output.accept(ModItems.INFESTED_SANDSTONE_ITEM.get());
                        output.accept(ModItems.INFESTED_SANDSTONE_2_ITEM.get());
                        output.accept(ModItems.INFESTED_SANDSTONE_3_ITEM.get());
                        output.accept(ModItems.INFESTED_SAND_ITEM.get());
                        output.accept(ModItems.INFESTED_LEAVES_ITEM.get());
                        output.accept(ModItems.INFESTED_ORE_ITEM.get());
                        output.accept(ModItems.INFESTED_FENCE_ITEM.get());

                        // === Parasite Building Blocks ===
                        output.accept(ModItems.PARASITE_TRUNK_ITEM.get());
                        output.accept(ModItems.PARASITE_PLANK_ITEM.get());
                        output.accept(ModItems.PARASITE_LOOT_ITEM.get());
                        output.accept(ModItems.PARASITE_RUBBLE_ITEM.get());
                        output.accept(ModItems.PARASITE_STRUCTURE_ITEM.get());
                        output.accept(ModItems.PARASITE_THIN_ITEM.get());
                        output.accept(ModItems.PARASITE_MOUTH_ITEM.get());
                        output.accept(ModItems.PARASITE_RUBBLE_DENSE_ITEM.get());
                        output.accept(ModItems.PARASITE_BUSH_ITEM.get());
                        output.accept(ModItems.PARASITE_CACTUS_ITEM.get());
                        output.accept(ModItems.PARASITE_CANISTER_ITEM.get());
                        output.accept(ModItems.PARASITE_CANISTER_ACTIVE_ITEM.get());

                        // === Spreading Blocks ===
                        output.accept(ModItems.HARLESKINN_BLOCK_ITEM.get());
                        output.accept(ModItems.POLAND_SKIN_BLOCK_ITEM.get());

                        // === Other Functional Blocks ===
                        output.accept(ModItems.BUGLIN_ITEM.get());
                        output.accept(ModItems.COOKED_FLESH_ITEM.get());

                        // === Assimilated Blocks ===
                        output.accept(ModItems.ASSIMILATED_PUMPKIN_ITEM.get());
                        output.accept(ModItems.ASSIMILATED_JACK_O_LANTERN_ITEM.get());
                        output.accept(ModItems.ASSIMILATED_SUGAR_CANE_ITEM.get());

                        // === Potted Blocks ===
                        output.accept(ModItems.INFESTED_POT_ITEM.get());
                        output.accept(ModItems.CONSUMED_POT_ITEM.get());

                        // === Flora ===
                        output.accept(ModItems.DISEASED_SPONGE_ITEM.get());

                        // === Hair Blocks ===
                        output.accept(ModItems.HAIR_FOLLICLE_BLOCK_ITEM.get());
                        output.accept(ModItems.HIRSUTE_HAIR_BLOCK_ITEM.get());
                        output.accept(ModItems.TRESSES_HAIR_BLOCK_ITEM.get());
                        output.accept(ModItems.LIPOPA_MASS_BLOCK_ITEM.get());
                        output.accept(ModItems.LOCS_BLOCK_ITEM.get());

                        // === Gore Blocks ===
                        output.accept(ModItems.GORE_SIM_ITEM.get());
                        output.accept(ModItems.GORE_PRI_ITEM.get());
                        output.accept(ModItems.GORE_ADA_ITEM.get());
                        output.accept(ModItems.GORE_PUR_ITEM.get());
                        output.accept(ModItems.GORE_FER_ITEM.get());
                        output.accept(ModItems.GORE_MAR_ITEM.get());

                        // === Semiorganiblock ===
                        output.accept(ModItems.SEMIORGANIBLOCK_ITEM.get());

                        // === Walls ===
                        output.accept(ModItems.INFESTED_STONE_BRICK_WALL_ITEM.get());
                        output.accept(ModItems.RESIDUE_BRICK_WALL_ITEM.get());
                        output.accept(ModItems.PARASITE_COBBLESTONE_WALL_ITEM.get());
                        output.accept(ModItems.POLISHED_INFECTED_STONE_WALL_ITEM.get());
                        output.accept(ModItems.INFESTED_SANDSTONE_WALL_ITEM.get());
                        output.accept(ModItems.GOTH_STEM_WALL_ITEM.get());

                        // === Stairs ===
                        output.accept(ModItems.INFESTED_STONE_BRICK_STAIRS_ITEM.get());
                        output.accept(ModItems.RESIDUE_BRICK_STAIRS_ITEM.get());
                        output.accept(ModItems.PARASITE_COBBLESTONE_STAIRS_ITEM.get());
                        output.accept(ModItems.POLISHED_INFECTED_STONE_STAIRS_ITEM.get());
                        output.accept(ModItems.INFESTED_PLANK_STAIRS_ITEM.get());
                        output.accept(ModItems.GOTH_PLANK_STAIRS_ITEM.get());
                        output.accept(ModItems.CONSUMED_PLANK_STAIRS_ITEM.get());
                        output.accept(ModItems.BRUSEWOOD_PLANK_STAIRS_ITEM.get());
                        output.accept(ModItems.INFESTED_SANDSTONE_STAIRS_ITEM.get());

                        // === Slabs ===
                        output.accept(ModItems.INFESTED_STONE_BRICK_SLAB_ITEM.get());
                        output.accept(ModItems.RESIDUE_BRICK_SLAB_ITEM.get());
                        output.accept(ModItems.PARASITE_COBBLESTONE_SLAB_ITEM.get());
                        output.accept(ModItems.POLISHED_INFECTED_STONE_SLAB_ITEM.get());
                        output.accept(ModItems.INFESTED_PLANK_SLAB_ITEM.get());
                        output.accept(ModItems.GOTH_PLANK_SLAB_ITEM.get());
                        output.accept(ModItems.CONSUMED_PLANK_SLAB_ITEM.get());
                        output.accept(ModItems.BRUSEWOOD_PLANK_SLAB_ITEM.get());
                        output.accept(ModItems.INFESTED_SANDSTONE_SLAB_ITEM.get());

                        // === Fences ===
                        output.accept(ModItems.GOTH_FENCE_ITEM.get());
                        output.accept(ModItems.CONSUMED_FENCE_ITEM.get());
                        output.accept(ModItems.BRUSEWOOD_FENCE_ITEM.get());
                        output.accept(ModItems.FLESH_FENCE_ITEM.get());

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

                        // ================================================================
                        // SPAWN EGGS - Organized by Parasite Category
                        // ================================================================

                        // --- Infected Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_INFECTED_CREEPER.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_SKELETON.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_ZOMBIE.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_SPIDER.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_ENDERMAN.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_PIG.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_HUMAN.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_COW.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_SHEEP.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_CHICKEN.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_VILLAGER.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_WOLF.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_HORSE.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_IRON_GOLEM.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_SNOW_GOLEM.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_BAT.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_BLAZE.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_WITCH.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_RAVAGER.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_PILLAGER.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_EVOKER.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_GHAST.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_PHANTOM.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_WARDEN.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_WITHER_SKELETON.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_STRAY.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_HUSK.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_DROWNED.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_CAVE_SPIDER.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_MOOSHROOM.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_LLAMA.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_POLAR_BEAR.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_PANDA.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_FOX.get());
                        output.accept(ModItems.SPAWN_EGG_INFECTED_BEE.get());

                        // --- Assimilated Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_ASSIMILATED_HUMAN.get());
                        output.accept(ModItems.SPAWN_EGG_ASSIMILATED_COW.get());
                        output.accept(ModItems.SPAWN_EGG_ASSIMILATED_SHEEP.get());

                        // --- Feral Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_FERAL_CREEPER.get());
                        output.accept(ModItems.SPAWN_EGG_FERAL_SKELETON.get());
                        output.accept(ModItems.SPAWN_EGG_FERAL_ZOMBIE.get());
                        output.accept(ModItems.SPAWN_EGG_FERAL_SPIDER.get());
                        output.accept(ModItems.SPAWN_EGG_FERAL_ENDERMAN.get());
                        output.accept(ModItems.SPAWN_EGG_FERAL_WOLF.get());
                        output.accept(ModItems.SPAWN_EGG_FERAL_IRON_GOLEM.get());
                        output.accept(ModItems.SPAWN_EGG_FERAL_COW.get());
                        output.accept(ModItems.SPAWN_EGG_FERAL_HUMAN.get());

                        // --- Hijacked Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_HIJACKED_CREEPER.get());
                        output.accept(ModItems.SPAWN_EGG_HIJACKED_SKELETON.get());
                        output.accept(ModItems.SPAWN_EGG_HIJACKED_ZOMBIE.get());
                        output.accept(ModItems.SPAWN_EGG_HIJACKED_SPIDER.get());
                        output.accept(ModItems.SPAWN_EGG_HIJACKED_ENDERMAN.get());
                        output.accept(ModItems.SPAWN_EGG_HIJACKED_WITCH.get());
                        output.accept(ModItems.SPAWN_EGG_HIJACKED_PILLAGER.get());
                        output.accept(ModItems.SPAWN_EGG_HIJACKED_EVOKER.get());
                        output.accept(ModItems.SPAWN_EGG_HIJACKED_RAVAGER.get());

                        // --- Inborn Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_INBORN_ALAFIN.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_OBUS.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_NORMAS.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_CANAL.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_GOTHOL.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_KOL.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_LESH.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_LODO.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_MOR.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_MUDO.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_NUUH.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_RATHOL.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_VIIN.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_ATA.get());
                        output.accept(ModItems.SPAWN_EGG_INBORN_BUTHOL.get());

                        // --- Crude Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_CRUDE_SCORCHER.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_MINDIM.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_EGAS.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_MOVING_FLESH.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_WORKER.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_CRUX_A.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_CRUX_B.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_DONE.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_HEED.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_HOST.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_INHOO_M.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_INHOO_S.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_LEER.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_LESH.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_MES.get());
                        output.accept(ModItems.SPAWN_EGG_CRUDE_QUAC.get());

                        // --- Primitive Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_BANO.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_CANRA.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_EMANA.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_GIM.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_HULL.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_IKI.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_LUM.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_NOGLA.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_RANRAC.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_SHYCO.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_WYMO.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_ZAA.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_LONGARMS.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_MANDUCATER.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_REEKER.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_YELLOWEYE.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_SUMMONER.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_BOLSTER.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_TOZON.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_ARACHNIDA.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_DEVOURER.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_VERMIN.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_VISCERA.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_BURROWER.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_BOMPH.get());
                        output.accept(ModItems.SPAWN_EGG_PRIMITIVE_WOLF.get());

                        // --- Adapted Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_COLONY.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_CREEPER.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_SKELETON.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_SPIDER.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_ZOMBIE.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_ENDERMAN.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_BANO.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_CANRA.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_EMANA.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_GIM.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_HULL.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_IKI.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_LUM.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_NOGLA.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_RANRAC.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_SHYCO.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_WYMO.get());
                        output.accept(ModItems.SPAWN_EGG_ADAPTED_ZAA.get());

                        // --- Beckon Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_BECKON_COMMON.get());
                        output.accept(ModItems.SPAWN_EGG_BECKON_UNCOMMON.get());
                        output.accept(ModItems.SPAWN_EGG_BECKON_RARE.get());
                        output.accept(ModItems.SPAWN_EGG_BECKON_EPIC.get());
                        output.accept(ModItems.SPAWN_EGG_BECKON_LEGENDARY.get());

                        // --- Dispatcher Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_DISPATCHER_COMMON.get());
                        output.accept(ModItems.SPAWN_EGG_DISPATCHER_UNCOMMON.get());
                        output.accept(ModItems.SPAWN_EGG_DISPATCHER_RARE.get());
                        output.accept(ModItems.SPAWN_EGG_DISPATCHER_EPIC.get());
                        output.accept(ModItems.SPAWN_EGG_DISPATCHER_LEGENDARY.get());

                        // --- Rooter Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_ROOTER_COMMON.get());
                        output.accept(ModItems.SPAWN_EGG_ROOTER_UNCOMMON.get());
                        output.accept(ModItems.SPAWN_EGG_ROOTER_RARE.get());
                        output.accept(ModItems.SPAWN_EGG_ROOTER_EPIC.get());
                        output.accept(ModItems.SPAWN_EGG_ROOTER_LEGENDARY.get());

                        // --- Nexus Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_NEXUS_GUARD.get());
                        output.accept(ModItems.SPAWN_EGG_NEXUS_OVERSEER.get());
                        output.accept(ModItems.SPAWN_EGG_NEXUS_CONSTRUCT.get());

                        // --- Deterrent Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_DETERRENT_SENTRY.get());
                        output.accept(ModItems.SPAWN_EGG_DETERRENT_OUTPOST.get());
                        output.accept(ModItems.SPAWN_EGG_DETERRENT_BASTION.get());
                        output.accept(ModItems.SPAWN_EGG_DETERRENT_VENKROL_SIV.get());

                        // --- Pure Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_PURE_CREEPER.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_SKELETON.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_ZOMBIE.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_SPIDER.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_ENDERMAN.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_WOLF.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_FLAM.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_FLOG.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_OMBOO.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_ALAFHA.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_GANRO.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_ESOR.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_ELVIA.get());
                        output.accept(ModItems.SPAWN_EGG_PURE_ANGED.get());

                        // --- Preeminent Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_PREEMINENT_MARAUDER.get());
                        output.accept(ModItems.SPAWN_EGG_PREEMINENT_WARDEN.get());
                        output.accept(ModItems.SPAWN_EGG_PREEMINENT_SOVEREIGN.get());

                        // --- Ancient Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_ANCIENT_DREADNOUGHT.get());
                        output.accept(ModItems.SPAWN_EGG_ANCIENT_LEVIATHAN.get());
                        output.accept(ModItems.SPAWN_EGG_ANCIENT_COLOSSUS.get());

                        // --- Derived Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_DERIVED_FLY.get());
                        output.accept(ModItems.SPAWN_EGG_DERIVED_SWARM.get());
                        output.accept(ModItems.SPAWN_EGG_DERIVED_CRAWLER.get());
                        output.accept(ModItems.SPAWN_EGG_DERIVED_LEAPER.get());
                        output.accept(ModItems.SPAWN_EGG_DERIVED_STALKER.get());

                        // --- Abomination Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_ABOMINATION_AMALGAM.get());
                        output.accept(ModItems.SPAWN_EGG_ABOMINATION_CHIMERA.get());
                        output.accept(ModItems.SPAWN_EGG_ABOMINATION_HYDRA.get());

                        // --- Misc Spawn Eggs ---
                        output.accept(ModItems.SPAWN_EGG_BUGLIN.get());
                        output.accept(ModItems.SPAWN_EGG_ALVEOLI_WORM.get());
                        output.accept(ModItems.SPAWN_EGG_PARASITE_LARVA.get());
                        output.accept(ModItems.SPAWN_EGG_VOID_ORB.get());
                        output.accept(ModItems.SPAWN_EGG_BOOM_ORB.get());


                                        })
                    .build());

    private ModCreativeTabs() {
        // Utility class - prevent instantiation
    }
}
