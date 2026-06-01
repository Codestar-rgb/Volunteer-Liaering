package com.subspaceparasite.core;

import com.subspaceparasite.core.ModEntities;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.common.item.ItemAssimilate;
import com.subspaceparasite.common.item.ItemBiomass;
import com.subspaceparasite.common.item.ItemBookOfVengeance;
import com.subspaceparasite.common.item.ItemCookedParasiteFlesh;
import com.subspaceparasite.common.item.ItemDevolve;
import com.subspaceparasite.common.item.ItemEvolve;
import com.subspaceparasite.common.item.ItemGreekFire;
import com.subspaceparasite.common.item.ItemModule;
import com.subspaceparasite.common.item.ItemParasiteFlesh;
import com.subspaceparasite.common.item.PurifyingSalveItem;
import com.subspaceparasite.common.item.tool.WeaponMeleeAxe;
import com.subspaceparasite.common.item.tool.WeaponMeleeSword;
import net.minecraft.ChatFormatting;
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

    // Helper: purifying salve item (reduces infection on use)
    private static RegistryObject<Item> purifyingItem(String name, int infectionReduction) {
        return ITEMS.register(name, () -> new PurifyingSalveItem(infectionReduction, new Item.Properties().stacksTo(16)));
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
            () -> new WeaponMeleeSword(ModToolTiers.HIJACKED_IRON, "parasiteclaw", -2.4, 0.0f, 5.5f, false, (byte)0, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_FANG = ITEMS.register("parasitefang",
            () -> new WeaponMeleeSword(ModToolTiers.HIJACKED_IRON, "parasitefang", -2.8, 0.0f, 6.5f, false, (byte)1, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_SPIKE = ITEMS.register("parasitespike",
            () -> new WeaponMeleeSword(ModToolTiers.HIJACKED_IRON, "parasitespike", -1.8, 0.0f, 4.5f, false, (byte)2, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_BLADE = ITEMS.register("parasiteblade",
            () -> new WeaponMeleeSword(ModToolTiers.HIJACKED_IRON, "parasiteblade", -3.0, 0.0f, 7.5f, false, (byte)3, new Item.Properties()));
    public static final RegistryObject<Item> BECKON_WEAPON = ITEMS.register("beckonweapon",
            () -> new WeaponMeleeSword(ModToolTiers.HIJACKED_IRON, "beckonweapon", -3.2, 0.0f, 8.5f, false, (byte)4, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_AXE = ITEMS.register("parasiteaxe",
            () -> new WeaponMeleeAxe(ModToolTiers.HIJACKED_IRON, "parasiteaxe", -3.2, 0.0f, 9.5f, false, (byte)10, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_BOW = ITEMS.register("parasitebow",
            () -> new Item(new Item.Properties().durability(384)));

    // Sentient Weapons
    public static final RegistryObject<Item> SENTIENT_CLAW = ITEMS.register("sentientclaw",
            () -> new WeaponMeleeSword(ModToolTiers.HIJACKED_IRON, "sentientclaw", -2.2, 0.0f, 6.5f, true, (byte)5, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_FANG = ITEMS.register("sentientfang",
            () -> new WeaponMeleeSword(ModToolTiers.HIJACKED_IRON, "sentientfang", -2.6, 0.0f, 7.5f, true, (byte)6, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_SPIKE = ITEMS.register("sentientspike",
            () -> new WeaponMeleeSword(ModToolTiers.HIJACKED_IRON, "sentientspike", -1.6, 0.0f, 5.5f, true, (byte)7, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_BLADE = ITEMS.register("sentientblade",
            () -> new WeaponMeleeSword(ModToolTiers.HIJACKED_IRON, "sentientblade", -2.8, 0.0f, 8.5f, true, (byte)8, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_BECKON_WEAPON = ITEMS.register("sentientbeckonweapon",
            () -> new WeaponMeleeSword(ModToolTiers.HIJACKED_IRON, "sentientbeckonweapon", -3.0, 0.0f, 9.5f, true, (byte)9, new Item.Properties()));
    public static final RegistryObject<Item> SENTIENT_AXE = ITEMS.register("sentientaxe",
            () -> new WeaponMeleeAxe(ModToolTiers.HIJACKED_IRON, "sentientaxe", -3.0, 0.0f, 10.5f, true, (byte)11, new Item.Properties()));
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

    // Hijacked Iron Armor (custom material with hijacked_iron texture)
    public static final RegistryObject<Item> HIJACKED_IRON_HELMET = ITEMS.register("hijackedironhelmet",
            () -> new ArmorItem(ModArmorMaterials.HIJACKED_IRON, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_CHESTPLATE = ITEMS.register("hijackedironchestplate",
            () -> new ArmorItem(ModArmorMaterials.HIJACKED_IRON, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_LEGGINGS = ITEMS.register("hijackedironleggings",
            () -> new ArmorItem(ModArmorMaterials.HIJACKED_IRON, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_BOOTS = ITEMS.register("hijackedironboots",
            () -> new ArmorItem(ModArmorMaterials.HIJACKED_IRON, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ================================================================
    // HIJACKED IRON TOOLS (5)
    // ================================================================
    public static final RegistryObject<Item> HIJACKED_IRON_SWORD = ITEMS.register("hijackedironsword",
            () -> new SwordItem(ModToolTiers.HIJACKED_IRON, 3, -2.4f, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_PICKAXE = ITEMS.register("hijackedironpickaxe",
            () -> new PickaxeItem(ModToolTiers.HIJACKED_IRON, 1, -2.8f, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_AXE = ITEMS.register("hijackedironaxe",
            () -> new AxeItem(ModToolTiers.HIJACKED_IRON, 5.0f, -3.1f, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_SHOVEL = ITEMS.register("hijackedironshovel",
            () -> new ShovelItem(ModToolTiers.HIJACKED_IRON, 1.5f, -3.0f, new Item.Properties()));
    public static final RegistryObject<Item> HIJACKED_IRON_HOE = ITEMS.register("hijackedironhoe",
            () -> new HoeItem(ModToolTiers.HIJACKED_IRON, -1, -2.0f, new Item.Properties()));

    // ================================================================
    // MODULE ITEMS (22)
    // ================================================================
    public static final RegistryObject<Item> MODULE_ADAPTER = ITEMS.register("moduleadapter",
            () -> new ItemModule("adapter", ChatFormatting.WHITE));
    public static final RegistryObject<Item> MODULE_BARRICADE = ITEMS.register("modulebarricade",
            () -> new ItemModule("barricade", ChatFormatting.GRAY));
    public static final RegistryObject<Item> MODULE_DYNAMO = ITEMS.register("moduledynamo",
            () -> new ItemModule("dynamo", ChatFormatting.RED));
    public static final RegistryObject<Item> MODULE_EXOTHERMIC = ITEMS.register("moduleexothermic",
            () -> new ItemModule("exothermic", ChatFormatting.GOLD));
    public static final RegistryObject<Item> MODULE_FERROMAGNETIC = ITEMS.register("moduleferromagnetic",
            () -> new ItemModule("ferromagnetic", ChatFormatting.DARK_GRAY));
    public static final RegistryObject<Item> MODULE_GRAVITATIONAL = ITEMS.register("modulegravitational",
            () -> new ItemModule("gravitational", ChatFormatting.DARK_PURPLE));
    public static final RegistryObject<Item> MODULE_HYPERTHREAT = ITEMS.register("modulehyperthreat",
            () -> new ItemModule("hyperthreat", ChatFormatting.DARK_RED));
    public static final RegistryObject<Item> MODULE_INSULATING = ITEMS.register("moduleinsulating",
            () -> new ItemModule("insulating", ChatFormatting.AQUA));
    public static final RegistryObject<Item> MODULE_KINETIC = ITEMS.register("modulekinetic",
            () -> new ItemModule("kinetic", ChatFormatting.YELLOW));
    public static final RegistryObject<Item> MODULE_LUMINOUS = ITEMS.register("moduleluminous",
            () -> new ItemModule("luminous", ChatFormatting.WHITE));
    public static final RegistryObject<Item> MODULE_MOTILE = ITEMS.register("modulemotile",
            () -> new ItemModule("motile", ChatFormatting.GREEN));
    public static final RegistryObject<Item> MODULE_NUTRIENT = ITEMS.register("modulenutrient",
            () -> new ItemModule("nutrient", ChatFormatting.DARK_GREEN));
    public static final RegistryObject<Item> MODULE_OUTREACH = ITEMS.register("moduleoutreach",
            () -> new ItemModule("outreach", ChatFormatting.BLUE));
    public static final RegistryObject<Item> MODULE_PHEROMONE = ITEMS.register("modulepheromone",
            () -> new ItemModule("pheromone", ChatFormatting.LIGHT_PURPLE));
    public static final RegistryObject<Item> MODULE_QUANTUM = ITEMS.register("modulequantum",
            () -> new ItemModule("quantum", ChatFormatting.DARK_BLUE));
    public static final RegistryObject<Item> MODULE_RESILIENT = ITEMS.register("moduleresilient",
            () -> new ItemModule("resilient", ChatFormatting.DARK_AQUA));
    public static final RegistryObject<Item> MODULE_SIEGE = ITEMS.register("modulesiege",
            () -> new ItemModule("siege", ChatFormatting.DARK_RED));
    public static final RegistryObject<Item> MODULE_THORNIAN = ITEMS.register("modulethornian",
            () -> new ItemModule("thornian", ChatFormatting.GREEN));
    public static final RegistryObject<Item> MODULE_UMBRELLA = ITEMS.register("moduleumbrella",
            () -> new ItemModule("umbrella", ChatFormatting.BLUE));
    public static final RegistryObject<Item> MODULE_VENOMOUS = ITEMS.register("modulevenomous",
            () -> new ItemModule("venomous", ChatFormatting.DARK_GREEN));
    public static final RegistryObject<Item> MODULE_WANDERER = ITEMS.register("modulewanderer",
            () -> new ItemModule("wanderer", ChatFormatting.GRAY));
    public static final RegistryObject<Item> MODULE_XENOLITHIC = ITEMS.register("modulexenolithic",
            () -> new ItemModule("xenolithic", ChatFormatting.DARK_PURPLE));

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
    public static final RegistryObject<Item> PARASITE_FLESH = ITEMS.register("parasiteflesh",
            () -> new ItemParasiteFlesh());
    public static final RegistryObject<Item> COOKED_PARASITE_FLESH = ITEMS.register("cookedparasiteflesh",
            () -> new ItemCookedParasiteFlesh());
    public static final RegistryObject<Item> PARASITE_TENDON = item("parasitetendon");
    public static final RegistryObject<Item> PARASITE_BONE = item("parasitebone");
    public static final RegistryObject<Item> PARASITE_MEMBRANE = item("parasitemembrane");
    public static final RegistryObject<Item> PARASITE_SHELL = item("parasiteshell");
    public static final RegistryObject<Item> PARASITE_CORE = item("parasitecore");
    public static final RegistryObject<Item> BIOMASS = ITEMS.register("biomass",
            () -> new ItemBiomass());
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
    public static final RegistryObject<Item> PURIFYING_SALVE = purifyingItem("purifyingsalve", 30);
    public static final RegistryObject<Item> ANTIDOTE = item("antidote");
    public static final RegistryObject<Item> CLEANSING_TOTEM = item("cleansingtotem");
    public static final RegistryObject<Item> INFESTATION_NEEDLE = item("infestationneedle");
    public static final RegistryObject<Item> EVOLUTION_CATALYST = item("evolutioncatalyst");
    public static final RegistryObject<Item> CALL_OF_THE_HIVE = item("callofthehive");
    public static final RegistryObject<Item> PARASITE_SYRINGE = item("parasitesyringe");
    public static final RegistryObject<Item> BECKON_ESSENCE = item("beckonessence");
    public static final RegistryObject<Item> BOOK_OF_VENGEANCE = ITEMS.register("bookofvengeance",
            () -> new ItemBookOfVengeance());
    public static final RegistryObject<Item> GREEK_FIRE = ITEMS.register("greekfire",
            () -> new ItemGreekFire());

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
    // FLUID BUCKET
    // ================================================================
    public static final RegistryObject<Item> DEAD_BLOOD_BUCKET = ITEMS.register("deadblood_bucket",
            () -> new BucketItem(ModFluids.DEAD_BLOOD_STILL,
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
    // ADDITIONAL VISIBLE BLOCK ITEMS (building blocks, slabs, stairs, walls, etc.)
    // ================================================================

    // Infested building blocks
    public static final RegistryObject<Item> INFESTED_COBBLESTONE_ITEM = blockItem("infestedcobblestone", ModBlocks.INFESTED_COBBLESTONE);
    public static final RegistryObject<Item> INFESTED_PLANKS_ITEM = blockItem("infestedplanks", ModBlocks.INFESTED_PLANKS);
    public static final RegistryObject<Item> INFESTED_STONE_BRICKS_ITEM = blockItem("infestedstonebricks", ModBlocks.INFESTED_STONE_BRICKS);
    public static final RegistryObject<Item> INFESTED_TERRACOTTA_ITEM = blockItem("infestedterracotta", ModBlocks.INFESTED_TERRACOTTA);
    public static final RegistryObject<Item> POLISHED_INFECTED_STONE_ITEM = blockItem("polishedinfestedstone", ModBlocks.POLISHED_INFECTED_STONE);
    public static final RegistryObject<Item> RESIDUE_BRICKS_ITEM = blockItem("residuebricks", ModBlocks.RESIDUE_BRICKS);
    public static final RegistryObject<Item> INFESTED_COLUMN_ITEM = blockItem("infestedcolumn", ModBlocks.INFESTED_COLUMN);
    public static final RegistryObject<Item> INFESTED_SANDSTONE_ITEM = blockItem("infestedsandstone", ModBlocks.INFESTED_SANDSTONE);
    public static final RegistryObject<Item> INFESTED_SANDSTONE_2_ITEM = blockItem("infestedsandstone2", ModBlocks.INFESTED_SANDSTONE_2);
    public static final RegistryObject<Item> INFESTED_SANDSTONE_3_ITEM = blockItem("infestedsandstone3", ModBlocks.INFESTED_SANDSTONE_3);
    public static final RegistryObject<Item> INFESTED_SAND_ITEM = blockItem("infestedsand", ModBlocks.INFESTED_SAND);
    public static final RegistryObject<Item> INFESTED_LEAVES_ITEM = blockItem("infestedleaves", ModBlocks.INFESTED_LEAVES);
    public static final RegistryObject<Item> INFESTED_ORE_ITEM = blockItem("infestedore", ModBlocks.INFESTED_ORE);
    public static final RegistryObject<Item> INFESTED_FENCE_ITEM = blockItem("infestedfence", ModBlocks.INFESTED_FENCE);

    // Parasite building blocks
    public static final RegistryObject<Item> PARASITE_TRUNK_ITEM = blockItem("parasitetrunk", ModBlocks.PARASITE_TRUNK);
    public static final RegistryObject<Item> PARASITE_PLANK_ITEM = blockItem("parasiteplank", ModBlocks.PARASITE_PLANK);
    public static final RegistryObject<Item> PARASITE_LOOT_ITEM = blockItem("parasiteloot", ModBlocks.PARASITE_LOOT);
    public static final RegistryObject<Item> PARASITE_RUBBLE_ITEM = blockItem("parasiterubble", ModBlocks.PARASITE_RUBBLE);
    public static final RegistryObject<Item> PARASITE_STRUCTURE_ITEM = blockItem("parasitestructure", ModBlocks.PARASITE_STRUCTURE);
    public static final RegistryObject<Item> PARASITE_THIN_ITEM = blockItem("parasitethin", ModBlocks.PARASITE_THIN);
    public static final RegistryObject<Item> PARASITE_MOUTH_ITEM = blockItem("parasitemouth", ModBlocks.PARASITE_MOUTH);
    public static final RegistryObject<Item> PARASITE_RUBBLE_DENSE_ITEM = blockItem("parasiterubbledense", ModBlocks.PARASITE_RUBBLE_DENSE);
    public static final RegistryObject<Item> PARASITE_BUSH_ITEM = blockItem("parasitebush", ModBlocks.PARASITE_BUSH);
    public static final RegistryObject<Item> PARASITE_CACTUS_ITEM = blockItem("parasitecactus", ModBlocks.PARASITE_CACTUS);
    public static final RegistryObject<Item> PARASITE_CANISTER_ITEM = blockItem("parasitecanister", ModBlocks.PARASITE_CANISTER);
    public static final RegistryObject<Item> PARASITE_CANISTER_ACTIVE_ITEM = blockItem("parasitecanisteractive", ModBlocks.PARASITE_CANISTER_ACTIVE);

    // Spreading blocks
    public static final RegistryObject<Item> HARLESKINN_BLOCK_ITEM = blockItem("harleskinnblock", ModBlocks.HARLESKINN_BLOCK);
    public static final RegistryObject<Item> POLAND_SKIN_BLOCK_ITEM = blockItem("polandskinblock", ModBlocks.POLAND_SKIN_BLOCK);

    // Functional block
    public static final RegistryObject<Item> BUGLIN_ITEM = blockItem("buglin", ModBlocks.BUGLIN);

    // Cooked flesh block
    public static final RegistryObject<Item> COOKED_FLESH_ITEM = blockItem("cookedflesh", ModBlocks.COOKED_FLESH);

    // Assimilated blocks
    public static final RegistryObject<Item> ASSIMILATED_PUMPKIN_ITEM = blockItem("assimilatedpumpkin", ModBlocks.ASSIMILATED_PUMPKIN);
    public static final RegistryObject<Item> ASSIMILATED_JACK_O_LANTERN_ITEM = blockItem("assimilatedjackolantern", ModBlocks.ASSIMILATED_JACK_O_LANTERN);
    public static final RegistryObject<Item> ASSIMILATED_SUGAR_CANE_ITEM = blockItem("assimilatedsugarcane", ModBlocks.ASSIMILATED_SUGAR_CANE);

    // Potted blocks
    public static final RegistryObject<Item> INFESTED_POT_ITEM = blockItem("infestedpot", ModBlocks.INFESTED_POT);
    public static final RegistryObject<Item> CONSUMED_POT_ITEM = blockItem("consumedpot", ModBlocks.CONSUMED_POT);

    // Flora
    public static final RegistryObject<Item> DISEASED_SPONGE_ITEM = blockItem("diseasedsponge", ModBlocks.DISEASED_SPONGE);

    // Hair blocks (block items — different from the material items with "item" suffix)
    public static final RegistryObject<Item> HAIR_FOLLICLE_BLOCK_ITEM = blockItem("hairfollicle", ModBlocks.HAIR_FOLLICLE);
    public static final RegistryObject<Item> HIRSUTE_HAIR_BLOCK_ITEM = blockItem("hirsutehair", ModBlocks.HIRSUTE_HAIR);
    public static final RegistryObject<Item> TRESSES_HAIR_BLOCK_ITEM = blockItem("tresseshair", ModBlocks.TRESSES_HAIR);
    public static final RegistryObject<Item> LIPOPA_MASS_BLOCK_ITEM = blockItem("lipopamass", ModBlocks.LIPOPA_MASS);
    public static final RegistryObject<Item> LOCS_BLOCK_ITEM = blockItem("locs", ModBlocks.LOCS);

    // Gore blocks
    public static final RegistryObject<Item> GORE_SIM_ITEM = blockItem("goresim", ModBlocks.GORE_SIM);
    public static final RegistryObject<Item> GORE_PRI_ITEM = blockItem("gorepri", ModBlocks.GORE_PRI);
    public static final RegistryObject<Item> GORE_ADA_ITEM = blockItem("goreada", ModBlocks.GORE_ADA);
    public static final RegistryObject<Item> GORE_PUR_ITEM = blockItem("gorepur", ModBlocks.GORE_PUR);
    public static final RegistryObject<Item> GORE_FER_ITEM = blockItem("gorefer", ModBlocks.GORE_FER);
    public static final RegistryObject<Item> GORE_MAR_ITEM = blockItem("goremar", ModBlocks.GORE_MAR);

    // Semiorganiblock
    public static final RegistryObject<Item> SEMIORGANIBLOCK_ITEM = blockItem("semiorganiblock", ModBlocks.SEMIORGANIC_BLOCK);

    // Walls
    public static final RegistryObject<Item> INFESTED_STONE_BRICK_WALL_ITEM = blockItem("infestedstonebrickwall", ModBlocks.INFESTED_STONE_BRICK_WALL);
    public static final RegistryObject<Item> RESIDUE_BRICK_WALL_ITEM = blockItem("residuebrickwall", ModBlocks.RESIDUE_BRICK_WALL);
    public static final RegistryObject<Item> PARASITE_COBBLESTONE_WALL_ITEM = blockItem("parasitecobblestonewall", ModBlocks.PARASITE_COBBLESTONE_WALL);
    public static final RegistryObject<Item> POLISHED_INFECTED_STONE_WALL_ITEM = blockItem("polishedinfestedstonewall", ModBlocks.POLISHED_INFECTED_STONE_WALL);
    public static final RegistryObject<Item> INFESTED_SANDSTONE_WALL_ITEM = blockItem("infestedsandstonewall", ModBlocks.INFESTED_SANDSTONE_WALL);
    public static final RegistryObject<Item> GOTH_STEM_WALL_ITEM = blockItem("gothstemwall", ModBlocks.GOTH_STEM_WALL);

    // Stairs
    public static final RegistryObject<Item> INFESTED_STONE_BRICK_STAIRS_ITEM = blockItem("infestedstonebrickstairs", ModBlocks.INFESTED_STONE_BRICK_STAIRS);
    public static final RegistryObject<Item> RESIDUE_BRICK_STAIRS_ITEM = blockItem("residuebrickstairs", ModBlocks.RESIDUE_BRICK_STAIRS);
    public static final RegistryObject<Item> PARASITE_COBBLESTONE_STAIRS_ITEM = blockItem("parasitecobblestonestairs", ModBlocks.PARASITE_COBBLESTONE_STAIRS);
    public static final RegistryObject<Item> POLISHED_INFECTED_STONE_STAIRS_ITEM = blockItem("polishedinfectedstonestairs", ModBlocks.POLISHED_INFECTED_STONE_STAIRS);
    public static final RegistryObject<Item> INFESTED_PLANK_STAIRS_ITEM = blockItem("infestedplankstairs", ModBlocks.INFESTED_PLANK_STAIRS);
    public static final RegistryObject<Item> GOTH_PLANK_STAIRS_ITEM = blockItem("gothplankstairs", ModBlocks.GOTH_PLANK_STAIRS);
    public static final RegistryObject<Item> CONSUMED_PLANK_STAIRS_ITEM = blockItem("consumedplankstairs", ModBlocks.CONSUMED_PLANK_STAIRS);
    public static final RegistryObject<Item> BRUSEWOOD_PLANK_STAIRS_ITEM = blockItem("brusewoodplankstairs", ModBlocks.BRUSEWOOD_PLANK_STAIRS);
    public static final RegistryObject<Item> INFESTED_SANDSTONE_STAIRS_ITEM = blockItem("infestedsandstonestairs", ModBlocks.INFESTED_SANDSTONE_STAIRS);

    // Slabs
    public static final RegistryObject<Item> INFESTED_STONE_BRICK_SLAB_ITEM = blockItem("infestedstonebrickslab", ModBlocks.INFESTED_STONE_BRICK_SLAB);
    public static final RegistryObject<Item> RESIDUE_BRICK_SLAB_ITEM = blockItem("residuebrickslab", ModBlocks.RESIDUE_BRICK_SLAB);
    public static final RegistryObject<Item> PARASITE_COBBLESTONE_SLAB_ITEM = blockItem("parasitecobblestoneslab", ModBlocks.PARASITE_COBBLESTONE_SLAB);
    public static final RegistryObject<Item> POLISHED_INFECTED_STONE_SLAB_ITEM = blockItem("polishedinfestedstoneslab", ModBlocks.POLISHED_INFECTED_STONE_SLAB);
    public static final RegistryObject<Item> INFESTED_PLANK_SLAB_ITEM = blockItem("infestedplankslab", ModBlocks.INFESTED_PLANK_SLAB);
    public static final RegistryObject<Item> GOTH_PLANK_SLAB_ITEM = blockItem("gothplankslab", ModBlocks.GOTH_PLANK_SLAB);
    public static final RegistryObject<Item> CONSUMED_PLANK_SLAB_ITEM = blockItem("consumedplankslab", ModBlocks.CONSUMED_PLANK_SLAB);
    public static final RegistryObject<Item> BRUSEWOOD_PLANK_SLAB_ITEM = blockItem("brusewoodplankslab", ModBlocks.BRUSEWOOD_PLANK_SLAB);
    public static final RegistryObject<Item> INFESTED_SANDSTONE_SLAB_ITEM = blockItem("infestedsandstoneslab", ModBlocks.INFESTED_SANDSTONE_SLAB);

    // Fences
    public static final RegistryObject<Item> GOTH_FENCE_ITEM = blockItem("gothfence", ModBlocks.GOTH_FENCE);
    public static final RegistryObject<Item> CONSUMED_FENCE_ITEM = blockItem("consumedfence", ModBlocks.CONSUMED_FENCE);
    public static final RegistryObject<Item> BRUSEWOOD_FENCE_ITEM = blockItem("brusewoodfence", ModBlocks.BRUSEWOOD_FENCE);
    public static final RegistryObject<Item> FLESH_FENCE_ITEM = blockItem("fleshfence", ModBlocks.FLESH_FENCE);

    // ================================================================
    // ADDITIONAL HIDDEN BLOCK ITEMS (internal/technical blocks)
    // ================================================================
    public static final RegistryObject<Item> NODE_RELAY_ITEM = hiddenBlockItem("noderelay", ModBlocks.NODE_RELAY);
    public static final RegistryObject<Item> RELAY_BASE_ITEM = hiddenBlockItem("relaybase", ModBlocks.RELAY_BASE);
    public static final RegistryObject<Item> RELAY_MIDDLE_ITEM = hiddenBlockItem("relaymiddle", ModBlocks.RELAY_MIDDLE);
    public static final RegistryObject<Item> RELAY_ROOF_ITEM = hiddenBlockItem("relayroof", ModBlocks.RELAY_ROOF);
    public static final RegistryObject<Item> POTTED_ASSIMILATED_BLOSSOM_ITEM = hiddenBlockItem("pottedassimilatedblossom", ModBlocks.POTTED_ASSIMILATED_BLOSSOM);
    public static final RegistryObject<Item> POTTED_CONSUMED_ASSIMILATED_BLOSSOM_ITEM = hiddenBlockItem("pottedconsumedassimilatedblossom", ModBlocks.POTTED_CONSUMED_ASSIMILATED_BLOSSOM);

    // ================================================================
    // SPAWN EGGS - One per mob entity type
    // ================================================================

    // --- Infected Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_CREEPER = ITEMS.register("spawneigginfectedcreeper",
            () -> new SpawnEggItem(ModEntities.INFECTED_CREEPER.get(), 0x2D5A1E, 0x4A7A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_SKELETON = ITEMS.register("spawneigginfectedskeleton",
            () -> new SpawnEggItem(ModEntities.INFECTED_SKELETON.get(), 0x2D5A1E, 0x7A6B52, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_ZOMBIE = ITEMS.register("spawneigginfectedzombie",
            () -> new SpawnEggItem(ModEntities.INFECTED_ZOMBIE.get(), 0x2D5A1E, 0x5F6B3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_SPIDER = ITEMS.register("spawneigginfectedspider",
            () -> new SpawnEggItem(ModEntities.INFECTED_SPIDER.get(), 0x2D5A1E, 0x3B3B3B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_ENDERMAN = ITEMS.register("spawneigginfectedenderman",
            () -> new SpawnEggItem(ModEntities.INFECTED_ENDERMAN.get(), 0x2D5A1E, 0x1A1A2E, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_PIG = ITEMS.register("spawneigginfectedpig",
            () -> new SpawnEggItem(ModEntities.INFECTED_PIG.get(), 0x2D5A1E, 0xEFAEB0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_HUMAN = ITEMS.register("spawneigginfectedhuman",
            () -> new SpawnEggItem(ModEntities.INFECTED_HUMAN.get(), 0x2D5A1E, 0x8B6B4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_COW = ITEMS.register("spawneigginfectedcow",
            () -> new SpawnEggItem(ModEntities.INFECTED_COW.get(), 0x2D5A1E, 0x6B4226, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_SHEEP = ITEMS.register("spawneigginfectedsheep",
            () -> new SpawnEggItem(ModEntities.INFECTED_SHEEP.get(), 0x2D5A1E, 0xD4C8B0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_CHICKEN = ITEMS.register("spawneigginfectedchicken",
            () -> new SpawnEggItem(ModEntities.INFECTED_CHICKEN.get(), 0x2D5A1E, 0xF5F0D0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_VILLAGER = ITEMS.register("spawneigginfectedvillager",
            () -> new SpawnEggItem(ModEntities.INFECTED_VILLAGER.get(), 0x2D5A1E, 0x8B6B4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_WOLF = ITEMS.register("spawneigginfectedwolf",
            () -> new SpawnEggItem(ModEntities.INFECTED_WOLF.get(), 0x2D5A1E, 0xC0C0C0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_HORSE = ITEMS.register("spawneigginfectedhorse",
            () -> new SpawnEggItem(ModEntities.INFECTED_HORSE.get(), 0x2D5A1E, 0x8B6914, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_IRON_GOLEM = ITEMS.register("spawneigginfectedirongolem",
            () -> new SpawnEggItem(ModEntities.INFECTED_IRON_GOLEM.get(), 0x2D5A1E, 0xA0A0A0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_SNOW_GOLEM = ITEMS.register("spawneigginfectedsnowgolem",
            () -> new SpawnEggItem(ModEntities.INFECTED_SNOW_GOLEM.get(), 0x2D5A1E, 0xD0D0D0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_BAT = ITEMS.register("spawneigginfectedbat",
            () -> new SpawnEggItem(ModEntities.INFECTED_BAT.get(), 0x2D5A1E, 0x5A4A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_BLAZE = ITEMS.register("spawneigginfectedblaze",
            () -> new SpawnEggItem(ModEntities.INFECTED_BLAZE.get(), 0x2D5A1E, 0xE0A020, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_WITCH = ITEMS.register("spawneigginfectedwitch",
            () -> new SpawnEggItem(ModEntities.INFECTED_WITCH.get(), 0x2D5A1E, 0x6B3A8B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_RAVAGER = ITEMS.register("spawneigginfectedravager",
            () -> new SpawnEggItem(ModEntities.INFECTED_RAVAGER.get(), 0x2D5A1E, 0x5A3A2A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_PILLAGER = ITEMS.register("spawneigginfectedpillager",
            () -> new SpawnEggItem(ModEntities.INFECTED_PILLAGER.get(), 0x2D5A1E, 0x5A6B3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_EVOKER = ITEMS.register("spawneigginfectedevoker",
            () -> new SpawnEggItem(ModEntities.INFECTED_EVOKER.get(), 0x2D5A1E, 0x3A5A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_GHAST = ITEMS.register("spawneigginfectedghast",
            () -> new SpawnEggItem(ModEntities.INFECTED_GHAST.get(), 0x2D5A1E, 0xE0D0D0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_PHANTOM = ITEMS.register("spawneigginfectedphantom",
            () -> new SpawnEggItem(ModEntities.INFECTED_PHANTOM.get(), 0x2D5A1E, 0x4A5A8B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_WARDEN = ITEMS.register("spawneigginfectedwarden",
            () -> new SpawnEggItem(ModEntities.INFECTED_WARDEN.get(), 0x2D5A1E, 0x1A3A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_WITHER_SKELETON = ITEMS.register("spawneigginfectedwitherskeleton",
            () -> new SpawnEggItem(ModEntities.INFECTED_WITHER_SKELETON.get(), 0x2D5A1E, 0x2A2A2A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_STRAY = ITEMS.register("spawneigginfectedstray",
            () -> new SpawnEggItem(ModEntities.INFECTED_STRAY.get(), 0x2D5A1E, 0x7A8B9A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_HUSK = ITEMS.register("spawneigginfectedhusk",
            () -> new SpawnEggItem(ModEntities.INFECTED_HUSK.get(), 0x2D5A1E, 0x8B7B4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_DROWNED = ITEMS.register("spawneigginfecteddrowned",
            () -> new SpawnEggItem(ModEntities.INFECTED_DROWNED.get(), 0x2D5A1E, 0x3A6B8B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_CAVE_SPIDER = ITEMS.register("spawneigginfectedcavespider",
            () -> new SpawnEggItem(ModEntities.INFECTED_CAVE_SPIDER.get(), 0x2D5A1E, 0x2A2A2A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_MOOSHROOM = ITEMS.register("spawneigginfectedmooshroom",
            () -> new SpawnEggItem(ModEntities.INFECTED_MOOSHROOM.get(), 0x2D5A1E, 0x8B3A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_LLAMA = ITEMS.register("spawneigginfectedllama",
            () -> new SpawnEggItem(ModEntities.INFECTED_LLAMA.get(), 0x2D5A1E, 0x8B7B5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_POLAR_BEAR = ITEMS.register("spawneigginfectedpolarbear",
            () -> new SpawnEggItem(ModEntities.INFECTED_POLAR_BEAR.get(), 0x2D5A1E, 0xD0D0D0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_PANDA = ITEMS.register("spawneigginfectedpanda",
            () -> new SpawnEggItem(ModEntities.INFECTED_PANDA.get(), 0x2D5A1E, 0x3A3A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_FOX = ITEMS.register("spawneigginfectedfox",
            () -> new SpawnEggItem(ModEntities.INFECTED_FOX.get(), 0x2D5A1E, 0xC08030, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INFECTED_BEE = ITEMS.register("spawneigginfectedbee",
            () -> new SpawnEggItem(ModEntities.INFECTED_BEE.get(), 0x2D5A1E, 0xE0C020, new Item.Properties()));

    // --- Assimilated Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ASSIMILATED_HUMAN = ITEMS.register("spawneiggassimilatedhuman",
            () -> new SpawnEggItem(ModEntities.ASSIMILATED_HUMAN.get(), 0x8A9A7A, 0x6A7A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ASSIMILATED_COW = ITEMS.register("spawneiggassimilatedcow",
            () -> new SpawnEggItem(ModEntities.ASSIMILATED_COW.get(), 0x8A9A7A, 0x5A4A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ASSIMILATED_SHEEP = ITEMS.register("spawneiggassimilatedsheep",
            () -> new SpawnEggItem(ModEntities.ASSIMILATED_SHEEP.get(), 0x8A9A7A, 0xC0B8A0, new Item.Properties()));

    // --- Feral Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_FERAL_CREEPER = ITEMS.register("spawneiggferalcreeper",
            () -> new SpawnEggItem(ModEntities.FERAL_CREEPER.get(), 0x6B1A1A, 0x3A0A0A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_FERAL_SKELETON = ITEMS.register("spawneiggferalskeleton",
            () -> new SpawnEggItem(ModEntities.FERAL_SKELETON.get(), 0x6B1A1A, 0x7A7A6B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_FERAL_ZOMBIE = ITEMS.register("spawneiggferalzombie",
            () -> new SpawnEggItem(ModEntities.FERAL_ZOMBIE.get(), 0x6B1A1A, 0x5F6B3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_FERAL_SPIDER = ITEMS.register("spawneiggferalspider",
            () -> new SpawnEggItem(ModEntities.FERAL_SPIDER.get(), 0x6B1A1A, 0x3B3B3B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_FERAL_ENDERMAN = ITEMS.register("spawneiggferalenderman",
            () -> new SpawnEggItem(ModEntities.FERAL_ENDERMAN.get(), 0x6B1A1A, 0x1A1A2E, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_FERAL_WOLF = ITEMS.register("spawneiggferalwolf",
            () -> new SpawnEggItem(ModEntities.FERAL_WOLF.get(), 0x6B1A1A, 0xA0A0A0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_FERAL_IRON_GOLEM = ITEMS.register("spawneiggferalirongolem",
            () -> new SpawnEggItem(ModEntities.FERAL_IRON_GOLEM.get(), 0x6B1A1A, 0xA0A0A0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_FERAL_COW = ITEMS.register("spawneiggferalcow",
            () -> new SpawnEggItem(ModEntities.FERAL_COW.get(), 0x6B1A1A, 0x6B4226, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_FERAL_HUMAN = ITEMS.register("spawneiggferalhuman",
            () -> new SpawnEggItem(ModEntities.FERAL_HUMAN.get(), 0x6B1A1A, 0x8B6B4A, new Item.Properties()));

    // --- Hijacked Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_HIJACKED_CREEPER = ITEMS.register("spawneigghijackedcreeper",
            () -> new SpawnEggItem(ModEntities.HIJACKED_CREEPER.get(), 0x5A1A6B, 0x3A0A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_HIJACKED_SKELETON = ITEMS.register("spawneigghijackedskeleton",
            () -> new SpawnEggItem(ModEntities.HIJACKED_SKELETON.get(), 0x5A1A6B, 0x7A8B9A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_HIJACKED_ZOMBIE = ITEMS.register("spawneigghijackedzombie",
            () -> new SpawnEggItem(ModEntities.HIJACKED_ZOMBIE.get(), 0x5A1A6B, 0x5F7A6B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_HIJACKED_SPIDER = ITEMS.register("spawneigghijackedspider",
            () -> new SpawnEggItem(ModEntities.HIJACKED_SPIDER.get(), 0x5A1A6B, 0x3B3B3B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_HIJACKED_ENDERMAN = ITEMS.register("spawneigghijackedenderman",
            () -> new SpawnEggItem(ModEntities.HIJACKED_ENDERMAN.get(), 0x5A1A6B, 0x1A1A2E, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_HIJACKED_WITCH = ITEMS.register("spawneigghijackedwitch",
            () -> new SpawnEggItem(ModEntities.HIJACKED_WITCH.get(), 0x5A1A6B, 0x6B3A8B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_HIJACKED_PILLAGER = ITEMS.register("spawneigghijackedpillager",
            () -> new SpawnEggItem(ModEntities.HIJACKED_PILLAGER.get(), 0x5A1A6B, 0x5A6B3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_HIJACKED_EVOKER = ITEMS.register("spawneigghijackedevoker",
            () -> new SpawnEggItem(ModEntities.HIJACKED_EVOKER.get(), 0x5A1A6B, 0x3A5A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_HIJACKED_RAVAGER = ITEMS.register("spawneigghijackedravager",
            () -> new SpawnEggItem(ModEntities.HIJACKED_RAVAGER.get(), 0x5A1A6B, 0x5A3A2A, new Item.Properties()));

    // --- Inborn Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_ALAFIN = ITEMS.register("spawneigginbornalafin",
            () -> new SpawnEggItem(ModEntities.INBORN_ALAFIN.get(), 0x1A5A6B, 0x0A3A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_OBUS = ITEMS.register("spawneigginbornobus",
            () -> new SpawnEggItem(ModEntities.INBORN_OBUS.get(), 0x1A5A6B, 0x2A4A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_NORMAS = ITEMS.register("spawneigginbornnormas",
            () -> new SpawnEggItem(ModEntities.INBORN_NORMAS.get(), 0x1A5A6B, 0x3A5A6A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_CANAL = ITEMS.register("spawneigginborncanal",
            () -> new SpawnEggItem(ModEntities.INBORN_CANAL.get(), 0x1A5A6B, 0x4A6A7A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_GOTHOL = ITEMS.register("spawneigginborn_gothol",
            () -> new SpawnEggItem(ModEntities.INBORN_GOTHOL.get(), 0x1A5A6B, 0x5A3A2A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_KOL = ITEMS.register("spawneigginborn_kol",
            () -> new SpawnEggItem(ModEntities.INBORN_KOL.get(), 0x1A5A6B, 0x6A4A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_LESH = ITEMS.register("spawneigginborn_lesh",
            () -> new SpawnEggItem(ModEntities.INBORN_LESH.get(), 0x1A5A6B, 0x3A6A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_LODO = ITEMS.register("spawneigginborn_lodo",
            () -> new SpawnEggItem(ModEntities.INBORN_LODO.get(), 0x1A5A6B, 0x4A5A6A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_MOR = ITEMS.register("spawneigginborn_mor",
            () -> new SpawnEggItem(ModEntities.INBORN_MOR.get(), 0x1A5A6B, 0x7A5A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_MUDO = ITEMS.register("spawneigginborn_mudo",
            () -> new SpawnEggItem(ModEntities.INBORN_MUDO.get(), 0x1A5A6B, 0x5A7A6A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_NUUH = ITEMS.register("spawneigginborn_nuuh",
            () -> new SpawnEggItem(ModEntities.INBORN_NUUH.get(), 0x1A5A6B, 0x8A6A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_RATHOL = ITEMS.register("spawneigginborn_rathol",
            () -> new SpawnEggItem(ModEntities.INBORN_RATHOL.get(), 0x1A5A6B, 0x6A3A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_VIIN = ITEMS.register("spawneigginborn_viin",
            () -> new SpawnEggItem(ModEntities.INBORN_VIIN.get(), 0x1A5A6B, 0x4A7A8A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_ATA = ITEMS.register("spawneigginborn_ata",
            () -> new SpawnEggItem(ModEntities.INBORN_ATA.get(), 0x1A5A6B, 0x5A8A7A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_INBORN_BUTHOL = ITEMS.register("spawneigginborn_buthol",
            () -> new SpawnEggItem(ModEntities.INBORN_BUTHOL.get(), 0x1A5A6B, 0x3A4A5A, new Item.Properties()));

    // --- Crude Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_SCORCHER = ITEMS.register("spawneiggcrudescorcher",
            () -> new SpawnEggItem(ModEntities.CRUDE_SCORCHER.get(), 0x6B4A1A, 0x4A2A0A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_MINDIM = ITEMS.register("spawneiggcrudemindim",
            () -> new SpawnEggItem(ModEntities.CRUDE_MINDIM.get(), 0x6B4A1A, 0x5A3A1A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_EGAS = ITEMS.register("spawneiggcrudeegas",
            () -> new SpawnEggItem(ModEntities.CRUDE_EGAS.get(), 0x6B4A1A, 0x7A5A2A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_MOVING_FLESH = ITEMS.register("spawneiggcrude_moving_flesh",
            () -> new SpawnEggItem(ModEntities.CRUDE_MOVING_FLESH.get(), 0x6B4A1A, 0x8B6B3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_WORKER = ITEMS.register("spawneiggcrude_worker",
            () -> new SpawnEggItem(ModEntities.CRUDE_WORKER.get(), 0x6B4A1A, 0x6A5A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_CRUX_A = ITEMS.register("spawneiggcrude_crux_a",
            () -> new SpawnEggItem(ModEntities.CRUDE_CRUX_A.get(), 0x6B4A1A, 0x5A7A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_CRUX_B = ITEMS.register("spawneiggcrude_crux_b",
            () -> new SpawnEggItem(ModEntities.CRUDE_CRUX_B.get(), 0x6B4A1A, 0x4A6A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_DONE = ITEMS.register("spawneiggcrude_done",
            () -> new SpawnEggItem(ModEntities.CRUDE_DONE.get(), 0x6B4A1A, 0x9A8A6A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_HEED = ITEMS.register("spawneiggcrude_heed",
            () -> new SpawnEggItem(ModEntities.CRUDE_HEED.get(), 0x6B4A1A, 0x7A6A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_HOST = ITEMS.register("spawneiggcrude_host",
            () -> new SpawnEggItem(ModEntities.CRUDE_HOST.get(), 0x6B4A1A, 0x8A7A6A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_INHOO_M = ITEMS.register("spawneiggcrude_inhoo_m",
            () -> new SpawnEggItem(ModEntities.CRUDE_INHOO_M.get(), 0x6B4A1A, 0x5A4A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_INHOO_S = ITEMS.register("spawneiggcrude_inhoo_s",
            () -> new SpawnEggItem(ModEntities.CRUDE_INHOO_S.get(), 0x6B4A1A, 0x4A3A2A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_LEER = ITEMS.register("spawneiggcrude_leer",
            () -> new SpawnEggItem(ModEntities.CRUDE_LEER.get(), 0x6B4A1A, 0x6A7A8A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_LESH = ITEMS.register("spawneiggcrude_lesh",
            () -> new SpawnEggItem(ModEntities.CRUDE_LESH.get(), 0x6B4A1A, 0x8B6B3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_MES = ITEMS.register("spawneiggcrude_mes",
            () -> new SpawnEggItem(ModEntities.CRUDE_MES.get(), 0x6B4A1A, 0x7A5A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_CRUDE_QUAC = ITEMS.register("spawneiggcrude_quac",
            () -> new SpawnEggItem(ModEntities.CRUDE_QUAC.get(), 0x6B4A1A, 0x6A4A5A, new Item.Properties()));

    // --- Primitive Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_BANO = ITEMS.register("spawneiggprimitive_bano",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_BANO.get(), 0x1A4A2A, 0x0A2A1A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_CANRA = ITEMS.register("spawneiggprimitive_canra",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_CANRA.get(), 0x1A4A2A, 0x2A3A2A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_EMANA = ITEMS.register("spawneiggprimitive_emana",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_EMANA.get(), 0x1A4A2A, 0x3A4A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_GIM = ITEMS.register("spawneiggprimitive_gim",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_GIM.get(), 0x1A4A2A, 0x4A5A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_HULL = ITEMS.register("spawneiggprimitive_hull",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_HULL.get(), 0x1A4A2A, 0x5A6A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_IKI = ITEMS.register("spawneiggprimitive_iki",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_IKI.get(), 0x1A4A2A, 0x6A7A6A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_LUM = ITEMS.register("spawneiggprimitive_lum",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_LUM.get(), 0x1A4A2A, 0x7A8A7A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_NOGLA = ITEMS.register("spawneiggprimitive_nogla",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_NOGLA.get(), 0x1A4A2A, 0x8A9A8A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_RANRAC = ITEMS.register("spawneiggprimitive_ranrac",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_RANRAC.get(), 0x1A4A2A, 0x3A3A2A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_SHYCO = ITEMS.register("spawneiggprimitive_shyco",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_SHYCO.get(), 0x1A4A2A, 0x4A4A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_WYMO = ITEMS.register("spawneiggprimitive_wymo",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_WYMO.get(), 0x1A4A2A, 0x5A5A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_ZAA = ITEMS.register("spawneiggprimitive_zaa",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_ZAA.get(), 0x1A4A2A, 0x6A6A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_LONGARMS = ITEMS.register("spawneiggprimitivelongarms",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_LONGARMS.get(), 0x1A4A2A, 0x7A7A6A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_MANDUCATER = ITEMS.register("spawneiggprimitivemanducater",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_MANDUCATER.get(), 0x1A4A2A, 0x8A8A7A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_REEKER = ITEMS.register("spawneiggprimitivereeker",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_REEKER.get(), 0x1A4A2A, 0x9A9A8A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_YELLOWEYE = ITEMS.register("spawneiggprimitiveyelloweye",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_YELLOWEYE.get(), 0x1A4A2A, 0xAA9A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_SUMMONER = ITEMS.register("spawneiggprimitivesummoner",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_SUMMONER.get(), 0x1A4A2A, 0x5A7A9A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_BOLSTER = ITEMS.register("spawneiggprimitivebolster",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_BOLSTER.get(), 0x1A4A2A, 0x4A6A8A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_TOZON = ITEMS.register("spawneiggprimitivetozon",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_TOZON.get(), 0x1A4A2A, 0x6A8AAA, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_ARACHNIDA = ITEMS.register("spawneiggprimitivearachnida",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_ARACHNIDA.get(), 0x1A4A2A, 0x3A3A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_DEVOURER = ITEMS.register("spawneiggprimitivedevourer",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_DEVOURER.get(), 0x1A4A2A, 0x7A5A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_VERMIN = ITEMS.register("spawneiggprimitivevermin",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_VERMIN.get(), 0x1A4A2A, 0x5A5A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_VISCERA = ITEMS.register("spawneiggprimitiveviscera",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_VISCERA.get(), 0x1A4A2A, 0x8A6A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_BURROWER = ITEMS.register("spawneiggprimitiveburrower",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_BURROWER.get(), 0x1A4A2A, 0x6A4A2A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_BOMPH = ITEMS.register("spawneiggprimitivebomph",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_BOMPH.get(), 0x1A4A2A, 0x3B7B3B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PRIMITIVE_WOLF = ITEMS.register("spawneiggprimitivewolf",
            () -> new SpawnEggItem(ModEntities.PRIMITIVE_WOLF.get(), 0x1A4A2A, 0x5B9B5B, new Item.Properties()));

    // --- Adapted Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_COLONY = ITEMS.register("spawneiggadaptedcolony",
            () -> new SpawnEggItem(ModEntities.ADAPTED_COLONY.get(), 0x6B4A1A, 0x8A5A2A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_CREEPER = ITEMS.register("spawneiggadaptedcreeper",
            () -> new SpawnEggItem(ModEntities.ADAPTED_CREEPER.get(), 0x6B4A1A, 0x3A0A0A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_SKELETON = ITEMS.register("spawneiggadaptedskeleton",
            () -> new SpawnEggItem(ModEntities.ADAPTED_SKELETON.get(), 0x6B4A1A, 0x7A8B9A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_SPIDER = ITEMS.register("spawneiggadaptedspider",
            () -> new SpawnEggItem(ModEntities.ADAPTED_SPIDER.get(), 0x6B4A1A, 0x3B3B3B, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_ZOMBIE = ITEMS.register("spawneiggadaptedzombie",
            () -> new SpawnEggItem(ModEntities.ADAPTED_ZOMBIE.get(), 0x6B4A1A, 0x5F6B3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_ENDERMAN = ITEMS.register("spawneiggadaptedenderman",
            () -> new SpawnEggItem(ModEntities.ADAPTED_ENDERMAN.get(), 0x6B4A1A, 0x1A1A2E, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_BANO = ITEMS.register("spawneiggadapted_bano",
            () -> new SpawnEggItem(ModEntities.ADAPTED_BANO.get(), 0x6B4A1A, 0x4A6A8A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_CANRA = ITEMS.register("spawneiggadapted_canra",
            () -> new SpawnEggItem(ModEntities.ADAPTED_CANRA.get(), 0x6B4A1A, 0x5A7A9A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_EMANA = ITEMS.register("spawneiggadapted_emana",
            () -> new SpawnEggItem(ModEntities.ADAPTED_EMANA.get(), 0x6B4A1A, 0xAA9A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_GIM = ITEMS.register("spawneiggadapted_gim",
            () -> new SpawnEggItem(ModEntities.ADAPTED_GIM.get(), 0x6B4A1A, 0x8A6A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_HULL = ITEMS.register("spawneiggadapted_hull",
            () -> new SpawnEggItem(ModEntities.ADAPTED_HULL.get(), 0x6B4A1A, 0x8A8A7A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_IKI = ITEMS.register("spawneiggadapted_iki",
            () -> new SpawnEggItem(ModEntities.ADAPTED_IKI.get(), 0x6B4A1A, 0x5A5A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_LUM = ITEMS.register("spawneiggadapted_lum",
            () -> new SpawnEggItem(ModEntities.ADAPTED_LUM.get(), 0x6B4A1A, 0x7A5A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_NOGLA = ITEMS.register("spawneiggadapted_nogla",
            () -> new SpawnEggItem(ModEntities.ADAPTED_NOGLA.get(), 0x6B4A1A, 0x9A9A8A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_RANRAC = ITEMS.register("spawneiggadapted_ranrac",
            () -> new SpawnEggItem(ModEntities.ADAPTED_RANRAC.get(), 0x6B4A1A, 0x3A3A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_SHYCO = ITEMS.register("spawneiggadapted_shyco",
            () -> new SpawnEggItem(ModEntities.ADAPTED_SHYCO.get(), 0x6B4A1A, 0x7A7A6A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_WYMO = ITEMS.register("spawneiggadapted_wymo",
            () -> new SpawnEggItem(ModEntities.ADAPTED_WYMO.get(), 0x6B4A1A, 0x6A8AAA, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ADAPTED_ZAA = ITEMS.register("spawneiggadapted_zaa",
            () -> new SpawnEggItem(ModEntities.ADAPTED_ZAA.get(), 0x6B4A1A, 0x6A4A2A, new Item.Properties()));

    // --- Beckon Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_BECKON_COMMON = ITEMS.register("spawneiggbeckoncommon",
            () -> new SpawnEggItem(ModEntities.BECKON_COMMON.get(), 0x8A8A1A, 0x5A5A0A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_BECKON_UNCOMMON = ITEMS.register("spawneiggbeckonuncommon",
            () -> new SpawnEggItem(ModEntities.BECKON_UNCOMMON.get(), 0x8A8A1A, 0x6A6A1A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_BECKON_RARE = ITEMS.register("spawneiggbeckonrare",
            () -> new SpawnEggItem(ModEntities.BECKON_RARE.get(), 0x8A8A1A, 0x7A7A2A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_BECKON_EPIC = ITEMS.register("spawneiggbeckonepic",
            () -> new SpawnEggItem(ModEntities.BECKON_EPIC.get(), 0x8A8A1A, 0x9A9A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_BECKON_LEGENDARY = ITEMS.register("spawneiggbeckonlegendary",
            () -> new SpawnEggItem(ModEntities.BECKON_LEGENDARY.get(), 0x8A8A1A, 0xAAAA4A, new Item.Properties()));

    // --- Dispatcher Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DISPATCHER_COMMON = ITEMS.register("spawneiggdispatchercommon",
            () -> new SpawnEggItem(ModEntities.DISPATCHER_COMMON.get(), 0x5A5A5A, 0x3A3A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DISPATCHER_UNCOMMON = ITEMS.register("spawneiggdispatcheruncommon",
            () -> new SpawnEggItem(ModEntities.DISPATCHER_UNCOMMON.get(), 0x5A5A5A, 0x4A4A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DISPATCHER_RARE = ITEMS.register("spawneiggdispatcherrare",
            () -> new SpawnEggItem(ModEntities.DISPATCHER_RARE.get(), 0x5A5A5A, 0x6A6A6A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DISPATCHER_EPIC = ITEMS.register("spawneiggdispatcherepic",
            () -> new SpawnEggItem(ModEntities.DISPATCHER_EPIC.get(), 0x5A5A5A, 0x7A7A7A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DISPATCHER_LEGENDARY = ITEMS.register("spawneiggdispatcherlegendary",
            () -> new SpawnEggItem(ModEntities.DISPATCHER_LEGENDARY.get(), 0x5A5A5A, 0x8A8A8A, new Item.Properties()));

    // --- Rooter Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ROOTER_COMMON = ITEMS.register("spawneiggrootercommon",
            () -> new SpawnEggItem(ModEntities.ROOTER_COMMON.get(), 0x6B4A2A, 0x4A2A1A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ROOTER_UNCOMMON = ITEMS.register("spawneiggrooteruncommon",
            () -> new SpawnEggItem(ModEntities.ROOTER_UNCOMMON.get(), 0x6B4A2A, 0x5A3A2A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ROOTER_RARE = ITEMS.register("spawneiggrooterrare",
            () -> new SpawnEggItem(ModEntities.ROOTER_RARE.get(), 0x6B4A2A, 0x7A5A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ROOTER_EPIC = ITEMS.register("spawneiggrooterepic",
            () -> new SpawnEggItem(ModEntities.ROOTER_EPIC.get(), 0x6B4A2A, 0x8A6A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ROOTER_LEGENDARY = ITEMS.register("spawneiggrooterlegendary",
            () -> new SpawnEggItem(ModEntities.ROOTER_LEGENDARY.get(), 0x6B4A2A, 0x9A7A5A, new Item.Properties()));

    // --- Nexus Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_NEXUS_GUARD = ITEMS.register("spawneiggnexusguard",
            () -> new SpawnEggItem(ModEntities.NEXUS_GUARD.get(), 0x3A1A5A, 0x1A0A3A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_NEXUS_OVERSEER = ITEMS.register("spawneiggnexusoverseer",
            () -> new SpawnEggItem(ModEntities.NEXUS_OVERSEER.get(), 0x3A1A5A, 0x4A2A6A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_NEXUS_CONSTRUCT = ITEMS.register("spawneiggnexusconstruct",
            () -> new SpawnEggItem(ModEntities.NEXUS_CONSTRUCT.get(), 0x3A1A5A, 0x5A3A7A, new Item.Properties()));

    // --- Deterrent Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DETERRENT_SENTRY = ITEMS.register("spawneiggdeterrentsentry",
            () -> new SpawnEggItem(ModEntities.DETERRENT_SENTRY.get(), 0x4A5A6A, 0x2A3A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DETERRENT_OUTPOST = ITEMS.register("spawneiggdeterrentoutpost",
            () -> new SpawnEggItem(ModEntities.DETERRENT_OUTPOST.get(), 0x4A5A6A, 0x3A4A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DETERRENT_BASTION = ITEMS.register("spawneiggdeterrentbastion",
            () -> new SpawnEggItem(ModEntities.DETERRENT_BASTION.get(), 0x4A5A6A, 0x5A6A7A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DETERRENT_VENKROL_SIV = ITEMS.register("spawneiggdeterrent_venkrol_siv",
            () -> new SpawnEggItem(ModEntities.DETERRENT_VENKROL_SIV.get(), 0x4A5A6A, 0x6A7A8A, new Item.Properties()));

    // --- Pure Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_CREEPER = ITEMS.register("spawneiggpurecreeper",
            () -> new SpawnEggItem(ModEntities.PURE_CREEPER.get(), 0xE0D8C0, 0xB0A080, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_SKELETON = ITEMS.register("spawneiggpureskeleton",
            () -> new SpawnEggItem(ModEntities.PURE_SKELETON.get(), 0xE0D8C0, 0xA09070, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_ZOMBIE = ITEMS.register("spawneiggpurezombie",
            () -> new SpawnEggItem(ModEntities.PURE_ZOMBIE.get(), 0xE0D8C0, 0x908060, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_SPIDER = ITEMS.register("spawneiggpurespider",
            () -> new SpawnEggItem(ModEntities.PURE_SPIDER.get(), 0xE0D8C0, 0x807050, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_ENDERMAN = ITEMS.register("spawneiggpureenderman",
            () -> new SpawnEggItem(ModEntities.PURE_ENDERMAN.get(), 0xE0D8C0, 0x706040, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_WOLF = ITEMS.register("spawneiggpurewolf",
            () -> new SpawnEggItem(ModEntities.PURE_WOLF.get(), 0xE0D8C0, 0xC0B090, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_FLAM = ITEMS.register("spawneiggpure_flam",
            () -> new SpawnEggItem(ModEntities.PURE_FLAM.get(), 0xE0D8C0, 0xD0A060, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_FLOG = ITEMS.register("spawneiggpure_flog",
            () -> new SpawnEggItem(ModEntities.PURE_FLOG.get(), 0xE0D8C0, 0xB09070, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_OMBOO = ITEMS.register("spawneiggpure_omboo",
            () -> new SpawnEggItem(ModEntities.PURE_OMBOO.get(), 0xE0D8C0, 0xA08060, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_ALAFHA = ITEMS.register("spawneiggpure_alafha",
            () -> new SpawnEggItem(ModEntities.PURE_ALAFHA.get(), 0xE0D8C0, 0xC09050, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_GANRO = ITEMS.register("spawneiggpure_ganro",
            () -> new SpawnEggItem(ModEntities.PURE_GANRO.get(), 0xE0D8C0, 0x907050, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_ESOR = ITEMS.register("spawneiggpure_esor",
            () -> new SpawnEggItem(ModEntities.PURE_ESOR.get(), 0xE0D8C0, 0xB08060, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_ELVIA = ITEMS.register("spawneiggpure_elvia",
            () -> new SpawnEggItem(ModEntities.PURE_ELVIA.get(), 0xE0D8C0, 0x8060A0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PURE_ANGED = ITEMS.register("spawneiggpure_anged",
            () -> new SpawnEggItem(ModEntities.PURE_ANGED.get(), 0xE0D8C0, 0xA07050, new Item.Properties()));

    // --- Preeminent Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PREEMINENT_MARAUDER = ITEMS.register("spawneiggpreeminentmarauder",
            () -> new SpawnEggItem(ModEntities.PREEMINENT_MARAUDER.get(), 0xD4A030, 0xF0E0A0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PREEMINENT_WARDEN = ITEMS.register("spawneiggpreeminentwarden",
            () -> new SpawnEggItem(ModEntities.PREEMINENT_WARDEN.get(), 0xD4A030, 0xE0D090, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PREEMINENT_SOVEREIGN = ITEMS.register("spawneiggpreeminentsovereign",
            () -> new SpawnEggItem(ModEntities.PREEMINENT_SOVEREIGN.get(), 0xD4A030, 0xC09020, new Item.Properties()));

    // --- Ancient Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ANCIENT_DREADNOUGHT = ITEMS.register("spawneiggancientdreadnought",
            () -> new SpawnEggItem(ModEntities.ANCIENT_DREADNOUGHT.get(), 0xA08040, 0x604020, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ANCIENT_LEVIATHAN = ITEMS.register("spawneiggancientleviathan",
            () -> new SpawnEggItem(ModEntities.ANCIENT_LEVIATHAN.get(), 0xA08040, 0x705030, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ANCIENT_COLOSSUS = ITEMS.register("spawneiggancientcolossus",
            () -> new SpawnEggItem(ModEntities.ANCIENT_COLOSSUS.get(), 0xA08040, 0x806040, new Item.Properties()));

    // --- Derived Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DERIVED_FLY = ITEMS.register("spawneiggderivedfly",
            () -> new SpawnEggItem(ModEntities.DERIVED_FLY.get(), 0x1A3A6B, 0x0A2A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DERIVED_SWARM = ITEMS.register("spawneiggderivedswarm",
            () -> new SpawnEggItem(ModEntities.DERIVED_SWARM.get(), 0x1A3A6B, 0x2A4A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DERIVED_CRAWLER = ITEMS.register("spawneiggderivedcrawler",
            () -> new SpawnEggItem(ModEntities.DERIVED_CRAWLER.get(), 0x1A3A6B, 0x3A5A6A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DERIVED_LEAPER = ITEMS.register("spawneiggderivedleaper",
            () -> new SpawnEggItem(ModEntities.DERIVED_LEAPER.get(), 0x1A3A6B, 0x4A6A7A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_DERIVED_STALKER = ITEMS.register("spawneiggderivedstalker",
            () -> new SpawnEggItem(ModEntities.DERIVED_STALKER.get(), 0x1A3A6B, 0x5A7A8A, new Item.Properties()));

    // --- Abomination Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ABOMINATION_AMALGAM = ITEMS.register("spawneiggabominationamalgam",
            () -> new SpawnEggItem(ModEntities.ABOMINATION_AMALGAM.get(), 0x5A0A2A, 0x3A1A4A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ABOMINATION_CHIMERA = ITEMS.register("spawneiggabominationchimera",
            () -> new SpawnEggItem(ModEntities.ABOMINATION_CHIMERA.get(), 0x5A0A2A, 0x4A2A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ABOMINATION_HYDRA = ITEMS.register("spawneiggabominationhydra",
            () -> new SpawnEggItem(ModEntities.ABOMINATION_HYDRA.get(), 0x5A0A2A, 0x5A3A6A, new Item.Properties()));

    // --- Misc Spawn Eggs ---
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_BUGLIN = ITEMS.register("spawneiggbuglin",
            () -> new SpawnEggItem(ModEntities.BUGLIN_ENTITY.get(), 0x6B5A3A, 0x8A7A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_ALVEOLI_WORM = ITEMS.register("spawneiggalveoliworm",
            () -> new SpawnEggItem(ModEntities.ALVEOLI_WORM.get(), 0x5A4A3A, 0x7A6A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_PARASITE_LARVA = ITEMS.register("spawneiggparasitelarva",
            () -> new SpawnEggItem(ModEntities.PARASITE_LARVA.get(), 0x4A6A3A, 0x6A8A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_VOID_ORB = ITEMS.register("spawneiggvoidorb",
            () -> new SpawnEggItem(ModEntities.VOID_ORB.get(), 0x1A0A3A, 0x3A2A5A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SPAWN_EGG_BOOM_ORB = ITEMS.register("spawneiggboomb",
            () -> new SpawnEggItem(ModEntities.BOOM_ORB.get(), 0x6B1A0A, 0x8A3A2A, new Item.Properties()));

        // ================================================================
    // DEBUG ITEMS (NOT in creative tab)
    // ================================================================
    public static final RegistryObject<Item> ITEM_EVOLVE = ITEMS.register("itemevolve",
            () -> new ItemEvolve());
    public static final RegistryObject<Item> ITEM_DEVOLVE = ITEMS.register("itemdevolve",
            () -> new ItemDevolve());
    public static final RegistryObject<Item> ITEM_VARIANT = item("itemvariant");
    public static final RegistryObject<Item> ITEM_ASSIMILATE = ITEMS.register("itemassimilate",
            () -> new ItemAssimilate());
    public static final RegistryObject<Item> ITEM_THROW = item("itemthrow");
    public static final RegistryObject<Item> ITEM_TAB = item("itemtab");

    private ModItems() {
        // Utility class - prevent instantiation
    }
}
