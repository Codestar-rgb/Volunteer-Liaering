package com.subspaceparasite.core;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.common.item.ItemBough;
import com.subspaceparasite.common.item.ItemThornshadeBerry;
import com.subspaceparasite.common.item.ItemThornshadeDecanter;
import com.subspaceparasite.common.item.ItemPhaseReport;
import com.subspaceparasite.common.item.ItemDislodgementReport;
import com.subspaceparasite.common.item.ItemLure;
import com.subspaceparasite.common.item.ItemDiscRecord;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Item registry for the SubspaceParasite mod.
 * Contains ALL items from the original Scape and Run: Parasites mod.
 *
 * IMPORTANT: Items do NOT use .tab() because creative tabs are registered after items
 * in Forge 1.20.1. Instead, all tab visibility is controlled by ModCreativeTabs'
 * displayItems() callback, which explicitly adds each item.
 */
public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SubspaceParasite.MOD_ID);

    // Helper: standard item (no creative tab - added via displayItems if needed)
    private static RegistryObject<Item> item(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    // Helper: block item (visible in creative tab via displayItems)
    private static RegistryObject<Item> blockItem(String name, RegistryObject<? extends net.minecraft.world.level.block.Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // Helper: block item NOT in creative tab
    private static RegistryObject<Item> hiddenBlockItem(String name, RegistryObject<? extends net.minecraft.world.level.block.Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // ================================================================
    // WEAPONS (14: 7 normal + 7 sentient)
    // ================================================================

    // Normal Weapons
    public static final RegistryObject<Item> PARASITE_CLAW = ITEMS.register("parasiteclaw",
            () -> new SwordItem(ModToolTiers.HIJACKED_IRON, 3, -2.4f, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_FANG = ITEMS.register("parasitefang",
            () -> new SwordItem(ModToolTiers.HIJACKED_IRON, 4, -2.8f, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_SPIKE = ITEMS.register("parasitespike",
            () -> new SwordItem(ModToolTiers.HIJACKED_IRON, 2, -1.8f, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_BLADE = ITEMS.register("parasiteblade",
            () -> new SwordItem(ModToolTiers.HIJACKED_IRON, 5, -3.0f, new Item.Properties()));
    public static final RegistryObject<Item> BECKON_WEAPON = ITEMS.register("beckonweapon",
            () -> new SwordItem(ModToolTiers.HIJACKED_IRON, 6, -3.2f, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_AXE = ITEMS.register("parasiteaxe",
            () -> new AxeItem(ModToolTiers.HIJACKED_IRON, 7, -3.2f, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_BOW = ITEMS.register("parasitebow",
            () -> new Item(new Item.Properties().durability(384)));

    // Sentient Weapons
    public static final RegistryObject<Item> SENTIENT_CLAW = ITEMS.register("sentientclaw",
            () -> new SwordItem(ModToolTiers.HIJACKED_IRON, 4, -2.2f, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_FANG = ITEMS.register("sentientfang",
            () -> new SwordItem(ModToolTiers.HIJACKED_IRON, 5, -2.6f, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_SPIKE = ITEMS.register("sentientspike",
            () -> new SwordItem(ModToolTiers.HIJACKED_IRON, 3, -1.6f, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_BLADE = ITEMS.register("sentientblade",
            () -> new SwordItem(ModToolTiers.HIJACKED_IRON, 6, -2.8f, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_BECKON_WEAPON = ITEMS.register("sentientbeckonweapon",
            () -> new SwordItem(ModToolTiers.HIJACKED_IRON, 7, -3.0f, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_AXE = ITEMS.register("sentientaxe",
            () -> new AxeItem(ModToolTiers.HIJACKED_IRON, 8, -3.0f, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_BOW = ITEMS.register("sentientbow",
            () -> new Item(new Item.Properties().durability(512)));

    // ================================================================
    // ARMOR
    // ================================================================

    // Living Armor
    public static final RegistryObject<Item> LIVING_HELMET = ITEMS.register("livinghelmet",
            () -> new ArmorItem(ModArmorMaterials.LIVING_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> LIVING_CHESTPLATE = ITEMS.register("livingchestplate",
            () -> new ArmorItem(ModArmorMaterials.LIVING_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> LIVING_LEGGINGS = ITEMS.register("livingleggings",
            () -> new ArmorItem(ModArmorMaterials.LIVING_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> LIVING_BOOTS = ITEMS.register("livingboots",
            () -> new ArmorItem(ModArmorMaterials.LIVING_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Sentient Armor
    public static final RegistryObject<Item> SENTIENT_HELMET = ITEMS.register("sentienthelmet",
            () -> new ArmorItem(ModArmorMaterials.SENTIENT_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_CHESTPLATE = ITEMS.register("sentientchestplate",
            () -> new ArmorItem(ModArmorMaterials.SENTIENT_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_LEGGINGS = ITEMS.register("sentientleggings",
            () -> new ArmorItem(ModArmorMaterials.SENTIENT_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_BOOTS = ITEMS.register("sentientboots",
            () -> new ArmorItem(ModArmorMaterials.SENTIENT_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Hijacked Iron Armor
    public static final RegistryObject<Item> HIJACKED_IRON_HELMET = ITEMS.register("hijackedironhelmet",
            () -> new ArmorItem(net.minecraft.world.item.ArmorMaterials.IRON, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_CHESTPLATE = ITEMS.register("hijackedironchestplate",
            () -> new ArmorItem(net.minecraft.world.item.ArmorMaterials.IRON, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_LEGGINGS = ITEMS.register("hijackedironleggings",
            () -> new ArmorItem(net.minecraft.world.item.ArmorMaterials.IRON, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_BOOTS = ITEMS.register("hijackedironboots",
            () -> new ArmorItem(net.minecraft.world.item.ArmorMaterials.IRON, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ================================================================
    // HIJACKED IRON TOOLS (5)
    // ================================================================
    public static final RegistryObject<Item> HIJACKED_IRON_SWORD = ITEMS.register("hijackedironsword",
            () -> new SwordItem(ModToolTiers.HIJACKED_IRON, 3, -2.4f, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_PICKAXE = ITEMS.register("hijackedironpickaxe",
            () -> new PickaxeItem(ModToolTiers.HIJACKED_IRON, 1, -2.8f, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_AXE = ITEMS.register("hijackedironaxe",
            () -> new AxeItem(ModToolTiers.HIJACKED_IRON, 5, -3.1f, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_SHOVEL = ITEMS.register("hijackedironshovel",
            () -> new ShovelItem(ModToolTiers.HIJACKED_IRON, 1.5f, -3.0f, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_HOE = ITEMS.register("hijackedironhoe",
            () -> new HoeItem(ModToolTiers.HIJACKED_IRON, -2, -1.0f, new Item.Properties()));

    // ================================================================
    // MODULE ITEMS (22)
    // ================================================================
    public static final RegistryObject<Item> MODULE_ADAPTER = item("moduleadapter");
    public static final RegistryObject<Item> MODULE_BARRICADE = item("modulebarricade");
    public static final RegistryObject<Item> MODULE_DYNAMO = item("moduledynamo");
    public static final RegistryObject<Item> MODULE_EXOTHERMIC = item("moduleexothermic");
    public static final RegistryObject<Item> MODULE_FERROMAGNETIC = item("moduleferromagnetic");
    public static final RegistryObject<Item> MODULE_GRAVITATIONAL = item("modulegravitational");
    public static final RegistryObject<Item> MODULE_HYPERTHREAT = item("modulehyperthreat");
    public static final RegistryObject<Item> MODULE_INSULATING = item("moduleinsulating");
    public static final RegistryObject<Item> MODULE_KINETIC = item("modulekinetic");
    public static final RegistryObject<Item> MODULE_LUMINOUS = item("moduleluminous");
    public static final RegistryObject<Item> MODULE_MOTILE = item("modulemotile");
    public static final RegistryObject<Item> MODULE_NUTRIENT = item("modulenutrient");
    public static final RegistryObject<Item> MODULE_OUTREACH = item("moduleoutreach");
    public static final RegistryObject<Item> MODULE_PHEROMONE = item("modulepheromone");
    public static final RegistryObject<Item> MODULE_QUANTUM = item("modulequantum");
    public static final RegistryObject<Item> MODULE_RESILIENT = item("moduleresilient");
    public static final RegistryObject<Item> MODULE_SIEGE = item("modulesiege");
    public static final RegistryObject<Item> MODULE_THORNIAN = item("modulethornian");
    public static final RegistryObject<Item> MODULE_UMBRELLA = item("moduleumbrella");
    public static final RegistryObject<Item> MODULE_VENOMOUS = item("modulevenomous");
    public static final RegistryObject<Item> MODULE_WANDERER = item("modulewanderer");
    public static final RegistryObject<Item> MODULE_XENOLITHIC = item("modulexenolithic");

    // ================================================================
    // EVOLUTION/DEVELOPMENT CLOCKS & COMPASSES (5)
    // ================================================================
    public static final RegistryObject<Item> EVOLUTION_CLOCK = item("evolutionclock");
    public static final RegistryObject<Item> DEVELOPMENT_CLOCK = item("developmentclock");
    public static final RegistryObject<Item> EVOLUTION_COMPASS = item("evolutioncompass");
    public static final RegistryObject<Item> DEVELOPMENT_COMPASS = item("developmentcompass");
    public static final RegistryObject<Item> PARASITE_COMPASS = item("parasitecompass");

    // ================================================================
    // FIELD GUIDE & PEARL
    // ================================================================
    public static final RegistryObject<Item> FIELD_GUIDE = item("fieldguide");
    public static final RegistryObject<Item> PARASITE_PEARL = item("parasitepearl");

    // ================================================================
    // DROPS & CRAFT MATERIALS
    // ================================================================
    public static final RegistryObject<Item> PARASITE_FLESH = item("parasiteflesh");
    public static final RegistryObject<Item> COOKED_PARASITE_FLESH = item("cookedparasiteflesh");
    public static final RegistryObject<Item> PARASITE_TENDON = item("parasitetendon");
    public static final RegistryObject<Item> PARASITE_BONE = item("parasitebone");
    public static final RegistryObject<Item> PARASITE_MEMBRANE = item("parasitemembrane");
    public static final RegistryObject<Item> PARASITE_SHELL = item("parasiteshell");
    public static final RegistryObject<Item> PARASITE_CORE = item("parasitecore");
    public static final RegistryObject<Item> BIOMASS = item("biomass");
    public static final RegistryObject<Item> RESIDUE = item("residue");
    public static final RegistryObject<Item> VIRULENT_RESIDUE = item("virulentresidue");
    public static final RegistryObject<Item> INFESTED_RESIDUE = item("infestedresidue");
    public static final RegistryObject<Item> PARASITE_NODULE = item("parasitenodule");
    public static final RegistryObject<Item> PARASITE_GLAND = item("parasitegland");
    public static final RegistryObject<Item> WEAK_NODE = item("weaknode");
    public static final RegistryObject<Item> STRONG_NODE = item("strongnode");
    public static final RegistryObject<Item> PARASITE_CLAW_DROP = item("parasiteclawdrop");
    public static final RegistryObject<Item> PARASITE_HEART = item("parasiteheart");
    public static final RegistryObject<Item> GOOTH = item("gooth");
    public static final RegistryObject<Item> SPORE = item("spore");
    public static final RegistryObject<Item> MUCUS = item("mucus");
    public static final RegistryObject<Item> ACID = item("acid");
    public static final RegistryObject<Item> BILE = item("bile");
    public static final RegistryObject<Item> HAIR_FOLLICLE_ITEM = item("hairfollicleitem");
    public static final RegistryObject<Item> HIRSUTE_HAIR_ITEM = item("hirsutehairitem");
    public static final RegistryObject<Item> TRESSES_HAIR_ITEM = item("tresseshairitem");
    public static final RegistryObject<Item> LIPOPA_MASS_ITEM = item("lipopamassitem");
    public static final RegistryObject<Item> LOCS_ITEM = item("locsitem");

    // ================================================================
    // FOOD ITEMS
    // ================================================================
    public static final RegistryObject<Item> RAW_PARASITE = ITEMS.register("rawparasite",
            () -> new Item(new Item.Properties().food(
                    new net.minecraft.world.food.FoodProperties.Builder().nutrition(2).saturationMod(0.3f).meat().build())));
    public static final RegistryObject<Item> COOKED_PARASITE = ITEMS.register("cookedparasite",
            () -> new Item(new Item.Properties().food(
                    new net.minecraft.world.food.FoodProperties.Builder().nutrition(6).saturationMod(0.6f).meat().build())));
    public static final RegistryObject<Item> BIOMASS_FOOD = ITEMS.register("biomassfood",
            () -> new Item(new Item.Properties().food(
                    new net.minecraft.world.food.FoodProperties.Builder().nutrition(1).saturationMod(0.1f).build())));
    public static final RegistryObject<Item> BLOODY_MEAT = ITEMS.register("bloodymeat",
            () -> new Item(new Item.Properties().food(
                    new net.minecraft.world.food.FoodProperties.Builder().nutrition(4).saturationMod(0.4f).meat().build())));
    public static final RegistryObject<Item> DERMOID_CYST_FOOD = ITEMS.register("dermoidcystfood",
            () -> new Item(new Item.Properties().food(
                    new net.minecraft.world.food.FoodProperties.Builder().nutrition(3).saturationMod(0.2f).build())));

    // ================================================================
    // LURE COMPONENTS
    // ================================================================
    public static final RegistryObject<Item> LURE_BASE = item("lurebase");
    public static final RegistryObject<Item> LURE_PRIMORDIAL = item("lureprimordial");
    public static final RegistryObject<Item> LURE_ADAPTIVE = item("lureadaptive");
    public static final RegistryObject<Item> LURE_FERAL = item("lureferal");
    public static final RegistryObject<Item> LURE_PURE = item("lurepure");

    // ================================================================
    // FUNCTIONAL ITEMS
    // ================================================================
    public static final RegistryObject<Item> PARASITE_BOMB = item("parasitebomb");
    public static final RegistryObject<Item> PURIFYING_SALVE = item("purifyingsalve");
    public static final RegistryObject<Item> ANTIDOTE = item("antidote");
    public static final RegistryObject<Item> CLEANSING_TOTEM = item("cleansingtotem");
    public static final RegistryObject<Item> INFESTATION_NEEDLE = item("infestationneedle");
    public static final RegistryObject<Item> EVOLUTION_CATALYST = item("evolutioncatalyst");
    public static final RegistryObject<Item> CALL_OF_THE_HIVE = item("callofthehive");
    public static final RegistryObject<Item> PARASITE_SYRINGE = item("parasitesyringe");
    public static final RegistryObject<Item> BECKON_ESSENCE = item("beckonessence");

    // ================================================================
    // MUSIC DISCS
    // ================================================================
    public static final RegistryObject<Item> MUSIC_DISC_EVO = ITEMS.register("music_disc_evo",
            () -> new RecordItem(1, ModSounds.BIOME_HEART_BEAT,
                    new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.RARE), 120));
    public static final RegistryObject<Item> MUSIC_DISC_ASSIMILATE = ITEMS.register("music_disc_assimilate",
            () -> new RecordItem(2, ModSounds.AMBIENT_PARASITE,
                    new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.RARE), 180));

    // ================================================================
    // DOOR ITEMS
    // ================================================================
    public static final RegistryObject<Item> GOTH_DOOR_ITEM = blockItem("gothdoor", ModBlocks.GOTH_DOOR);
    public static final RegistryObject<Item> BRUSEWOOD_DOOR_ITEM = blockItem("brusewooddoor", ModBlocks.BRUSEWOOD_DOOR);
    public static final RegistryObject<Item> CONSUMED_DOOR_ITEM = blockItem("consumeddoor", ModBlocks.CONSUMED_DOOR);

    // ================================================================
    // FLUID BUCKETS
    // ================================================================
    public static final RegistryObject<Item> DEAD_BLOOD_BUCKET = ITEMS.register("deadblood_bucket",
            () -> new BucketItem(ModFluids.DEAD_BLOOD_STILL,
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final RegistryObject<Item> ALVEOLAR_FLUID_BUCKET = ITEMS.register("alveolar_fluid_bucket",
            () -> new BucketItem(ModFluids.ALVEOLAR_FLUID_STILL,
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    // ================================================================
    // VISIBLE BLOCK ITEMS (appear in creative tab via displayItems)
    // ================================================================
    public static final RegistryObject<Item> BIOME_HEART_ITEM = blockItem("biomeheart", ModBlocks.BIOME_HEART);
    public static final RegistryObject<Item> COLONY_HEART_ITEM = blockItem("colonyheart", ModBlocks.COLONY_HEART);
    public static final RegistryObject<Item> COLONY_OUTPOST_ITEM = blockItem("colonyoutpost", ModBlocks.COLONY_OUTPOST);
    public static final RegistryObject<Item> BIOME_PURIFIER_ITEM = blockItem("biomepurifier", ModBlocks.BIOME_PURIFIER);
    public static final RegistryObject<Item> RELAY_CONTROLLER_ITEM = blockItem("relaycontroller", ModBlocks.RELAY_CONTROLLER);
    public static final RegistryObject<Item> PARASITE_BARRIER_ITEM = blockItem("parasitebarrier", ModBlocks.PARASITE_BARRIER);
    public static final RegistryObject<Item> EVOLUTION_LURE_ITEM = blockItem("evolutionlure", ModBlocks.EVOLUTION_LURE);
    public static final RegistryObject<Item> INFESTATION_PURIFIER_ITEM = blockItem("infestationpurifier", ModBlocks.INFESTATION_PURIFIER);
    public static final RegistryObject<Item> FOG_NULLIFIER_ITEM = blockItem("fognullifier", ModBlocks.FOG_NULLIFIER);
    public static final RegistryObject<Item> INFESTED_FURNACE_ITEM = blockItem("infestedfurnace", ModBlocks.INFESTED_FURNACE);
    public static final RegistryObject<Item> INFUSER_FURNACE_ITEM = blockItem("infuserfurnace", ModBlocks.INFUSER_FURNACE);
    public static final RegistryObject<Item> CONSUMED_WORKBENCH_ITEM = blockItem("consumedworkbench", ModBlocks.CONSUMED_WORKBENCH);
    public static final RegistryObject<Item> INFESTED_WORKBENCH_ITEM = blockItem("infestedworkbench", ModBlocks.INFESTED_WORKBENCH);

    // Escal Bulb items
    public static final RegistryObject<Item> ESCA_BULB_ITEM = blockItem("escabulb", ModBlocks.ESCA_BULB);
    public static final RegistryObject<Item> ESCA_BULB_WHITE_ITEM = blockItem("escabulbwhite", ModBlocks.ESCA_BULB_WHITE);
    public static final RegistryObject<Item> ESCA_BULB_ORANGE_ITEM = blockItem("escabulborange", ModBlocks.ESCA_BULB_ORANGE);
    public static final RegistryObject<Item> ESCA_BULB_MAGENTA_ITEM = blockItem("escabulbmagenta", ModBlocks.ESCA_BULB_MAGENTA);
    public static final RegistryObject<Item> ESCA_BULB_LIGHT_BLUE_ITEM = blockItem("escabulblightblue", ModBlocks.ESCA_BULB_LIGHT_BLUE);
    public static final RegistryObject<Item> ESCA_BULB_YELLOW_ITEM = blockItem("escabulbyellow", ModBlocks.ESCA_BULB_YELLOW);
    public static final RegistryObject<Item> ESCA_BULB_LIME_ITEM = blockItem("escabulblime", ModBlocks.ESCA_BULB_LIME);
    public static final RegistryObject<Item> ESCA_BULB_PINK_ITEM = blockItem("escabulbpink", ModBlocks.ESCA_BULB_PINK);
    public static final RegistryObject<Item> ESCA_BULB_GRAY_ITEM = blockItem("escabulbgray", ModBlocks.ESCA_BULB_GRAY);
    public static final RegistryObject<Item> ESCA_BULB_LIGHT_GRAY_ITEM = blockItem("escabulblightgray", ModBlocks.ESCA_BULB_LIGHT_GRAY);
    public static final RegistryObject<Item> ESCA_BULB_CYAN_ITEM = blockItem("escabulbcyan", ModBlocks.ESCA_BULB_CYAN);
    public static final RegistryObject<Item> ESCA_BULB_PURPLE_ITEM = blockItem("escabulbpurple", ModBlocks.ESCA_BULB_PURPLE);
    public static final RegistryObject<Item> ESCA_BULB_BLUE_ITEM = blockItem("escabulbblue", ModBlocks.ESCA_BULB_BLUE);
    public static final RegistryObject<Item> ESCA_BULB_BROWN_ITEM = blockItem("escabulbbrown", ModBlocks.ESCA_BULB_BROWN);
    public static final RegistryObject<Item> ESCA_BULB_GREEN_ITEM = blockItem("escabulbgreen", ModBlocks.ESCA_BULB_GREEN);
    public static final RegistryObject<Item> ESCA_BULB_RED_ITEM = blockItem("escabulbred", ModBlocks.ESCA_BULB_RED);
    public static final RegistryObject<Item> ESCA_BULB_BLACK_ITEM = blockItem("escabulbblack", ModBlocks.ESCA_BULB_BLACK);

    // Wood/Plank items
    public static final RegistryObject<Item> GOTH_STEM_ITEM = blockItem("gothstem", ModBlocks.GOTH_STEM);
    public static final RegistryObject<Item> GOTH_PLANKS_ITEM = blockItem("gothplanks", ModBlocks.GOTH_PLANKS);
    public static final RegistryObject<Item> FLESH_PLANKS_ITEM = blockItem("fleshplanks", ModBlocks.FLESH_PLANKS);
    public static final RegistryObject<Item> COOKED_FLESH_PLANKS_ITEM = blockItem("cookedfleshplanks", ModBlocks.COOKED_FLESH_PLANKS);
    public static final RegistryObject<Item> BRUSEWOOD_PLANKS_ITEM = blockItem("brusewoodplanks", ModBlocks.BRUSEWOOD_PLANKS);
    public static final RegistryObject<Item> CONSUMED_PLANKS_ITEM = blockItem("consumedplanks", ModBlocks.CONSUMED_PLANKS);

    // Trapdoor items
    public static final RegistryObject<Item> BRUSEWOOD_TRAPDOOR_ITEM = blockItem("brusewoodtrapdoor", ModBlocks.BRUSEWOOD_TRAPDOOR);
    public static final RegistryObject<Item> CONSUMED_TRAPDOOR_ITEM = blockItem("consumedtrapdoor", ModBlocks.CONSUMED_TRAPDOOR);
    public static final RegistryObject<Item> GOTH_TRAPDOOR_ITEM = blockItem("gothtrapdoor", ModBlocks.GOTH_TRAPDOOR);

    // Glass items
    public static final RegistryObject<Item> ASHEN_GLASS_ITEM = blockItem("ashenglass", ModBlocks.ASHEN_GLASS);
    public static final RegistryObject<Item> SHROUDED_GLASS_ITEM = blockItem("shroudedglass", ModBlocks.SHROUDED_GLASS);
    public static final RegistryObject<Item> HARLEQUINN_GLASS_ITEM = blockItem("harlequinnglass", ModBlocks.HARLEQUINN_GLASS);
    public static final RegistryObject<Item> BLOODY_GLASS_ITEM = blockItem("bloodyglass", ModBlocks.BLOODY_GLASS);
    public static final RegistryObject<Item> INFESTED_GLASS_ITEM = blockItem("infestedglass", ModBlocks.INFESTED_GLASS);
    public static final RegistryObject<Item> SHADE_GLASS_ITEM = blockItem("shadeglass", ModBlocks.SHADE_GLASS);
    public static final RegistryObject<Item> SEPIA_GLASS_ITEM = blockItem("sepiaglass", ModBlocks.SEPIA_GLASS);
    public static final RegistryObject<Item> MOODY_GLASS_ITEM = blockItem("moodyglass", ModBlocks.MOODY_GLASS);
    public static final RegistryObject<Item> ASHEN_GLASS_PANE_ITEM = blockItem("ashenglasspane", ModBlocks.ASHEN_GLASS_PANE);
    public static final RegistryObject<Item> SHROUDED_GLASS_PANE_ITEM = blockItem("shroudedglasspane", ModBlocks.SHROUDED_GLASS_PANE);
    public static final RegistryObject<Item> HARLEQUINN_GLASS_PANE_ITEM = blockItem("harlequinnglasspane", ModBlocks.HARLEQUINN_GLASS_PANE);
    public static final RegistryObject<Item> BLOODY_GLASS_PANE_ITEM = blockItem("bloodyglasspane", ModBlocks.BLOODY_GLASS_PANE);
    public static final RegistryObject<Item> INFESTED_GLASS_PANE_ITEM = blockItem("infestedglasspane", ModBlocks.INFESTED_GLASS_PANE);
    public static final RegistryObject<Item> SHADE_GLASS_PANE_ITEM = blockItem("shadeglasspane", ModBlocks.SHADE_GLASS_PANE);
    public static final RegistryObject<Item> SEPIA_GLASS_PANE_ITEM = blockItem("sepiaglasspane", ModBlocks.SEPIA_GLASS_PANE);
    public static final RegistryObject<Item> MOODY_GLASS_PANE_ITEM = blockItem("moodyglasspane", ModBlocks.MOODY_GLASS_PANE);

    // Trophy items
    public static final RegistryObject<Item> TROPHY_VOID_ORB_ITEM = blockItem("trophyvoidorb", ModBlocks.TROPHY_VOID_ORB);
    public static final RegistryObject<Item> TROPHY_BOOM_ORB_ITEM = blockItem("trophyboomb", ModBlocks.TROPHY_BOOM_ORB);

    // Other visible block items
    public static final RegistryObject<Item> PARASITE_SAPLING_ITEM = blockItem("parasitesapling", ModBlocks.PARASITE_SAPLING);
    public static final RegistryObject<Item> ASSIMILATED_BLOSSOM_ITEM = blockItem("assimilatedblossom", ModBlocks.ASSIMILATED_BLOSSOM);
    public static final RegistryObject<Item> THORNSHADE_ITEM = blockItem("thornshade", ModBlocks.THORNSHADE);
    public static final RegistryObject<Item> GOTH_SHROOM_ITEM = blockItem("gothshroom", ModBlocks.GOTH_SHROOM);
    public static final RegistryObject<Item> BLOODY_ICE_ITEM = blockItem("bloodyice", ModBlocks.BLOODY_ICE);
    public static final RegistryObject<Item> NODE_REDSTONE_LAMP_ITEM = blockItem("noderedstonelamp", ModBlocks.NODE_REDSTONE_LAMP);
    public static final RegistryObject<Item> NODE_LAMP_ITEM = blockItem("nodelamp", ModBlocks.NODE_LAMP);

    // ================================================================
    // HIDDEN BLOCK ITEMS (NOT in creative tab - auto-spread/internal)
    // ================================================================
    public static final RegistryObject<Item> INFESTED_STAIN_ITEM = hiddenBlockItem("infestedstain", ModBlocks.INFESTED_STAIN);
    public static final RegistryObject<Item> INFEST_REMAIN_ITEM = hiddenBlockItem("infestremain", ModBlocks.INFEST_REMAIN);
    public static final RegistryObject<Item> INFESTED_TRUNK_ITEM = hiddenBlockItem("infestedtrunk", ModBlocks.INFESTED_TRUNK);
    public static final RegistryObject<Item> INFESTED_RUBBLE_ITEM = hiddenBlockItem("infestedrubble", ModBlocks.INFESTED_RUBBLE);
    public static final RegistryObject<Item> INFESTED_BUSH_ITEM = hiddenBlockItem("infestedbush", ModBlocks.INFESTED_BUSH);
    public static final RegistryObject<Item> PARASITE_STAIN_ITEM = hiddenBlockItem("parasitestain", ModBlocks.PARASITE_STAIN);
    public static final RegistryObject<Item> PARASITE_VINE_ITEM = hiddenBlockItem("parasitevine", ModBlocks.PARASITE_VINE);
    public static final RegistryObject<Item> PARASITE_FOG_ITEM = hiddenBlockItem("parasitefog", ModBlocks.PARASITE_FOG);
    public static final RegistryObject<Item> HARLEQUINN_GRASS_ITEM = hiddenBlockItem("harlequinngrass", ModBlocks.HARLEQUINN_GRASS);
    public static final RegistryObject<Item> BIOMASS_BLOCK_ITEM = hiddenBlockItem("biomassblock", ModBlocks.BIOMASS_BLOCK);
    public static final RegistryObject<Item> RESIDUE_BLOCK_ITEM = hiddenBlockItem("residueblock", ModBlocks.RESIDUE_BLOCK);
    public static final RegistryObject<Item> ALVEOLI_ITEM = hiddenBlockItem("alveoli", ModBlocks.ALVEOLI);
    public static final RegistryObject<Item> SICK_ALVEOLI_ITEM = hiddenBlockItem("sickalveoli", ModBlocks.SICK_ALVEOLI);
    public static final RegistryObject<Item> ALVEOLI_GROWTH_ITEM = hiddenBlockItem("alveoligrowth", ModBlocks.ALVEOLI_GROWTH);
    public static final RegistryObject<Item> SOLID_ALVEOLI_ITEM = hiddenBlockItem("solidalveoli", ModBlocks.SOLID_ALVEOLI);
    public static final RegistryObject<Item> DERMOID_CYST_ITEM = hiddenBlockItem("dermoidcyst", ModBlocks.DERMOID_CYST);
    public static final RegistryObject<Item> DOD_ITEM = hiddenBlockItem("dod", ModBlocks.DOD);
    public static final RegistryObject<Item> DEAD_BLOOD_ITEM = hiddenBlockItem("deadblood", ModBlocks.DEAD_BLOOD);
    public static final RegistryObject<Item> SRP_WEB_ITEM = hiddenBlockItem("srpweb", ModBlocks.SRP_WEB);

    // ================================================================
    // MOB SPAWNER ITEMS (NOT in creative tab) - ~100+ spawn eggs
    // ================================================================

    // Infected spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_CREEPER = ITEMS.register("spawneigginfectedcreeper",
            () -> new SpawnEggItem(ModEntities.INFECTED_CREEPER.get(), 0x4A0E0E, 0x8B4513, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_SKELETON = ITEMS.register("spawneigginfectedskeleton",
            () -> new SpawnEggItem(ModEntities.INFECTED_SKELETON.get(), 0x4A0E0E, 0x7A6B52, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_ZOMBIE = ITEMS.register("spawneigginfectedzombie",
            () -> new SpawnEggItem(ModEntities.INFECTED_ZOMBIE.get(), 0x4A0E0E, 0x5F6B3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_SPIDER = ITEMS.register("spawneigginfectedspider",
            () -> new SpawnEggItem(ModEntities.INFECTED_SPIDER.get(), 0x4A0E0E, 0x3B3B3B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_ENDERMAN = ITEMS.register("spawneigginfectedenderman",
            () -> new SpawnEggItem(ModEntities.INFECTED_ENDERMAN.get(), 0x4A0E0E, 0x1A1A2E, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_PIG = ITEMS.register("spawneigginfectedpig",
            () -> new SpawnEggItem(ModEntities.INFECTED_PIG.get(), 0x4A0E0E, 0xEFAEB0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_COW = ITEMS.register("spawneigginfectedcow",
            () -> new SpawnEggItem(ModEntities.INFECTED_COW.get(), 0x4A0E0E, 0x6B4226, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_SHEEP = ITEMS.register("spawneigginfectedsheep",
            () -> new SpawnEggItem(ModEntities.INFECTED_SHEEP.get(), 0x4A0E0E, 0xD4C8B0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_CHICKEN = ITEMS.register("spawneigginfectedchicken",
            () -> new SpawnEggItem(ModEntities.INFECTED_CHICKEN.get(), 0x4A0E0E, 0xF5F0D0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_VILLAGER = ITEMS.register("spawneigginfectedvillager",
            () -> new SpawnEggItem(ModEntities.INFECTED_VILLAGER.get(), 0x4A0E0E, 0x8B6B4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_WOLF = ITEMS.register("spawneigginfectedwolf",
            () -> new SpawnEggItem(ModEntities.INFECTED_WOLF.get(), 0x4A0E0E, 0xC0C0C0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_HORSE = ITEMS.register("spawneigginfectedhorse",
            () -> new SpawnEggItem(ModEntities.INFECTED_HORSE.get(), 0x4A0E0E, 0x8B6914, new Item.Properties()));

    // Feral spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_FERAL_CREEPER = ITEMS.register("spawneiggferalcreeper",
            () -> new SpawnEggItem(ModEntities.FERAL_CREEPER.get(), 0x2E4A0E, 0x6B8E23, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_FERAL_SKELETON = ITEMS.register("spawneiggferalskeleton",
            () -> new SpawnEggItem(ModEntities.FERAL_SKELETON.get(), 0x2E4A0E, 0x7A7A6B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_FERAL_ZOMBIE = ITEMS.register("spawneiggferalzombie",
            () -> new SpawnEggItem(ModEntities.FERAL_ZOMBIE.get(), 0x2E4A0E, 0x5F6B3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_FERAL_SPIDER = ITEMS.register("spawneiggferalspider",
            () -> new SpawnEggItem(ModEntities.FERAL_SPIDER.get(), 0x2E4A0E, 0x3B3B3B, new Item.Properties()));

    // Hijacked spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_HIJACKED_CREEPER = ITEMS.register("spawneigghijackedcreeper",
            () -> new SpawnEggItem(ModEntities.HIJACKED_CREEPER.get(), 0x0E2A4A, 0x4A6B8E, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_HIJACKED_SKELETON = ITEMS.register("spawneigghijackedskeleton",
            () -> new SpawnEggItem(ModEntities.HIJACKED_SKELETON.get(), 0x0E2A4A, 0x7A8B9A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_HIJACKED_ZOMBIE = ITEMS.register("spawneigghijackedzombie",
            () -> new SpawnEggItem(ModEntities.HIJACKED_ZOMBIE.get(), 0x0E2A4A, 0x5F7A6B, new Item.Properties()));

    // Inborn spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_1 = ITEMS.register("spawneigginborn1",
            () -> new SpawnEggItem(ModEntities.INBORN_ALAFIN.get(), 0x3B1A3B, 0x7B4F7B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_2 = ITEMS.register("spawneigginborn2",
            () -> new SpawnEggItem(ModEntities.INBORN_OBUS.get(), 0x3B1A3B, 0x8B5F8B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_3 = ITEMS.register("spawneigginborn3",
            () -> new SpawnEggItem(ModEntities.INBORN_NORMAS.get(), 0x3B1A3B, 0x6B3F6B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_4 = ITEMS.register("spawneigginborn4",
            () -> new SpawnEggItem(ModEntities.INBORN_CANAL.get(), 0x3B1A3B, 0x9B6F9B, new Item.Properties()));

    // Crude spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_1 = ITEMS.register("spawneiggcrude1",
            () -> new SpawnEggItem(ModEntities.CRUDE_SCORCHER.get(), 0x4A3A0E, 0x8B7D3C, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_2 = ITEMS.register("spawneiggcrude2",
            () -> new SpawnEggItem(ModEntities.CRUDE_MINDIM.get(), 0x4A3A0E, 0x7B6D2C, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_3 = ITEMS.register("spawneiggcrude3",
            () -> new SpawnEggItem(ModEntities.CRUDE_EGAS.get(), 0x4A3A0E, 0x9B8D4C, new Item.Properties()));

    // Primitive spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_1 = ITEMS.register("spawneiggprimitive1",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_BOMPH.get(), 0x1A3B1A, 0x3B7B3B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_2 = ITEMS.register("spawneiggprimitive2",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_WOLF.get(), 0x1A3B1A, 0x5B9B5B, new Item.Properties()));

    // Adapted spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_1 = ITEMS.register("spawneiggadapted1",
            () -> new SpawnEggItem(ModEntities.ADAPTED_COLONY.get(), 0x0E4A3B, 0x3B8B7B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_2 = ITEMS.register("spawneiggadapted2",
            () -> new SpawnEggItem(ModEntities.ADAPTED_CREEPER.get(), 0x0E4A3B, 0x4B9B8B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_3 = ITEMS.register("spawneiggadapted3",
            () -> new SpawnEggItem(ModEntities.ADAPTED_SKELETON.get(), 0x0E4A3B, 0x2B7B6B, new Item.Properties()));

    // Nexus spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_NEXUS_BECKON = ITEMS.register("spawneiggnexusbeckon",
            () -> new SpawnEggItem(ModEntities.BECKON_COMMON.get(), 0x4A0E4A, 0x8B3B8B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_NEXUS_DISPATCHER = ITEMS.register("spawneiggnexusdispatcher",
            () -> new SpawnEggItem(ModEntities.DISPATCHER_COMMON.get(), 0x3B0E4A, 0x7B3B8B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_NEXUS_ROOTER = ITEMS.register("spawneiggnexusrooter",
            () -> new SpawnEggItem(ModEntities.ROOTER_COMMON.get(), 0x0E3B4A, 0x3B7B8B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_NEXUS_OTHER = ITEMS.register("spawneiggnexusother",
            () -> new SpawnEggItem(ModEntities.NEXUS_GUARD.get(), 0x4A3B0E, 0x8B7B3B, new Item.Properties()));

    // Deterrent spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DETERRENT_1 = ITEMS.register("spawneiggdeterrent1",
            () -> new SpawnEggItem(ModEntities.DETERRENT_SENTRY.get(), 0x4A1A0E, 0x8B4B3B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DETERRENT_2 = ITEMS.register("spawneiggdeterrent2",
            () -> new SpawnEggItem(ModEntities.DETERRENT_OUTPOST.get(), 0x4A1A0E, 0x9B5B4B, new Item.Properties()));

    // Pure spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_1 = ITEMS.register("spawneiggpure1",
            () -> new SpawnEggItem(ModEntities.PURE_CREEPER.get(), 0x1A1A4A, 0x4B4B8B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_2 = ITEMS.register("spawneiggpure2",
            () -> new SpawnEggItem(ModEntities.PURE_SKELETON.get(), 0x1A1A4A, 0x5B5B9B, new Item.Properties()));

    // Preeminent spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PREEMINENT_1 = ITEMS.register("spawneiggpreeminent1",
            () -> new SpawnEggItem(ModEntities.PREEMINENT_MARAUDER.get(), 0x4A0E2A, 0x8B3B5B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PREEMINENT_2 = ITEMS.register("spawneiggpreeminent2",
            () -> new SpawnEggItem(ModEntities.PREEMINENT_WARDEN.get(), 0x4A0E2A, 0x9B4B6B, new Item.Properties()));

    // Ancient spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ANCIENT_1 = ITEMS.register("spawneiggancient1",
            () -> new SpawnEggItem(ModEntities.ANCIENT_DREADNOUGHT.get(), 0x2A0E4A, 0x5B3B8B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ANCIENT_2 = ITEMS.register("spawneiggancient2",
            () -> new SpawnEggItem(ModEntities.ANCIENT_LEVIATHAN.get(), 0x2A0E4A, 0x6B4B9B, new Item.Properties()));

    // Derived spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DERIVED_1 = ITEMS.register("spawneiggderived1",
            () -> new SpawnEggItem(ModEntities.DERIVED_FLY.get(), 0x0E4A1A, 0x3B8B4B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DERIVED_2 = ITEMS.register("spawneiggderived2",
            () -> new SpawnEggItem(ModEntities.DERIVED_SWARM.get(), 0x0E4A1A, 0x4B9B5B, new Item.Properties()));

    // Abomination spawn eggs
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ABOMINATION_1 = ITEMS.register("spawneiggabomination1",
            () -> new SpawnEggItem(ModEntities.ABOMINATION_AMALGAM.get(), 0x4A0E0E, 0x8B0000, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ABOMINATION_2 = ITEMS.register("spawneiggabomination2",
            () -> new SpawnEggItem(ModEntities.ABOMINATION_CHIMERA.get(), 0x4A0E0E, 0x9B1010, new Item.Properties()));

    // ================================================================
    // SPECIAL ITEMS (added via displayItems)
    // ================================================================
    // Thornshade Decanter - gives Thorns effect for 400 ticks (20 seconds)
    public static final RegistryObject<Item> THORNSHADE_DECANTER = ITEMS.register("thornshade_decanter",
            () -> new ItemThornshadeDecanter());

    // Bough - special tool for spreading thornshade
    public static final RegistryObject<Item> BOUGH = ITEMS.register("bough",
            () -> new ItemBough());

    // Thornshade Berry - edible item with negative effects
    public static final RegistryObject<Item> THORNSHADE_BERRY = ITEMS.register("thornshade_berry",
            () -> new ItemThornshadeBerry());

    // Phase Report - displays current infection phase info
    public static final RegistryObject<Item> PHASE_REPORT = ITEMS.register("phase_report",
            () -> new ItemPhaseReport());

    // Dislodgement Report - displays parasite removal history
    public static final RegistryObject<Item> DISLODGEMENT_REPORT = ITEMS.register("dislodgement_report",
            () -> new ItemDislodgementReport());

    // Lure Components (5 variants)
    public static final RegistryObject<Item> LURE_BASE = ITEMS.register("lure_base",
            () -> new ItemLure(ItemLure.LureType.BASE));
    public static final RegistryObject<Item> LURE_PRIMORDIAL = ITEMS.register("lure_primordial",
            () -> new ItemLure(ItemLure.LureType.PRIMORDIAL));
    public static final RegistryObject<Item> LURE_ADAPTIVE = ITEMS.register("lure_adaptive",
            () -> new ItemLure(ItemLure.LureType.ADAPTIVE));
    public static final RegistryObject<Item> LURE_FERAL = ITEMS.register("lure_feral",
            () -> new ItemLure(ItemLure.LureType.FERAL));
    public static final RegistryObject<Item> LURE_PURE = ITEMS.register("lure_pure",
            () -> new ItemLure(ItemLure.LureType.PURE));

    // Music Discs (6 variants)
    public static final RegistryObject<Item> MUSIC_DISC_EVO_ITEM = ITEMS.register("music_disc_evo_item",
            () -> new ItemDiscRecord(ItemDiscRecord.DiscType.EVO));
    public static final RegistryObject<Item> MUSIC_DISC_ASSIMILATE_ITEM = ITEMS.register("music_disc_assimilate_item",
            () -> new ItemDiscRecord(ItemDiscRecord.DiscType.ASSIMILATE));
    public static final RegistryObject<Item> MUSIC_DISC_INFESTATION = ITEMS.register("music_disc_infestation",
            () -> new ItemDiscRecord(ItemDiscRecord.DiscType.INFESTATION));
    public static final RegistryObject<Item> MUSIC_DISC_COLONY = ITEMS.register("music_disc_colony",
            () -> new ItemDiscRecord(ItemDiscRecord.DiscType.COLONY));
    public static final RegistryObject<Item> MUSIC_DISC_BECKON = ITEMS.register("music_disc_beckon",
            () -> new ItemDiscRecord(ItemDiscRecord.DiscType.BECKON));
    public static final RegistryObject<Item> MUSIC_DISC_FINAL_PHASE = ITEMS.register("music_disc_final_phase",
            () -> new ItemDiscRecord(ItemDiscRecord.DiscType.FINAL_PHASE));

    // Greek Fire - already registered in ModItems, class exists
    // Book of Vengeance - already registered in ModItems, class exists

    // TODO: Add remaining special items from original SRP:
    // - ItemAlveolarFluid (肺泡液桶)
    // - ItemDeadBlood (死血桶 - already registered as DEAD_BLOOD_BUCKET)

    // ================================================================
    // DEBUG ITEMS (NOT in creative tab)
    // ================================================================
    public static final RegistryObject<Item> ITEM_EVOLVE = item("itemevolve");
    public static final RegistryObject<Item> ITEM_DEVOLVE = item("itemdevolve");
    public static final RegistryObject<Item> ITEM_VARIANT = item("itemvariant");
    public static final RegistryObject<Item> ITEM_ASSIMILATE = item("itemassimilate");
    public static final RegistryObject<Item> ITEM_THROW = item("itemthrow");
    public static final RegistryObject<Item> ITEM_TAB = item("itemtab");

    private ModItems() {
        // Utility class - prevent instantiation
    }
}
