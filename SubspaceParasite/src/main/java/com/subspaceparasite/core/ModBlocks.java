package com.subspaceparasite.core;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.BlockSetType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.subspaceparasite.SubspaceParasite;
import net.minecraftforge.registries.RegistryObject;

/**
 * Block registry for the SubspaceParasite mod.
 * Contains ALL blocks from the original Scape and Run: Parasites mod.
 *
 * Block types now use proper MC 1.20.1 block classes for correct behavior:
 *   DOORS:     DoorBlock - doors open/close with redstone and player interaction
 *   TRAPDOORS: TrapDoorBlock - trapdoors open/close properly
 *   STAIRS:    StairBlock - facing/shape variants, corner connections
 *   SLABS:     SlabBlock - top/bottom/double slab placement
 *   WALLS:     WallBlock - wall connections and height variants
 *   GLASS:     GlassBlock - transparent rendering, no redstone conduction
 *   PANES:     IronBarsBlock - pane connections to adjacent blocks
 *   FENCES:    FenceBlock - fence connections to adjacent fences/gates
 *   SAPLING:   Block with randomTicks for future tree growth
 */
public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, SubspaceParasite.MOD_ID);

    // Helper: strong parasite block
    private static RegistryObject<Block> strongBlock(String name, float strength, float resistance) {
        return BLOCKS.register(name, () -> new Block(
                BlockBehaviour.Properties.of().strength(strength, resistance).requiresCorrectToolForDrops()));
    }

    // Helper: standard parasite block
    private static RegistryObject<Block> standardBlock(String name) {
        return BLOCKS.register(name, () -> new Block(
                BlockBehaviour.Properties.of().strength(2.0f, 6.0f)));
    }

    // Helper: organic/fleshy block
    private static RegistryObject<Block> organicBlock(String name) {
        return BLOCKS.register(name, () -> new Block(
                BlockBehaviour.Properties.of().strength(1.0f, 3.0f)));
    }

    // Helper: glass block (kept for backwards compat; glass blocks now use GlassBlock directly)
    private static RegistryObject<Block> glassBlock(String name) {
        return BLOCKS.register(name, () -> new Block(
                BlockBehaviour.Properties.of().strength(0.3f, 0.3f).noOcclusion().isRedstoneConductor((s, r, p) -> false)));
    }

    // Helper: pane block (kept for backwards compat; panes now use IronBarsBlock directly)
    private static RegistryObject<Block> paneBlock(String name) {
        return BLOCKS.register(name, () -> new Block(
                BlockBehaviour.Properties.of().strength(0.3f, 0.3f).noOcclusion().isRedstoneConductor((s, r, p) -> false)));
    }

    // ================================================================
    // SYSTEM BLOCKS
    // ================================================================
    public static final RegistryObject<Block> BIOME_HEART = strongBlock("biomeheart", 60f, 2500f);
    public static final RegistryObject<Block> COLONY_HEART = strongBlock("colonyheart", 30f, 1200f);
    public static final RegistryObject<Block> COLONY_OUTPOST = strongBlock("colonyoutpost", 20f, 800f);
    public static final RegistryObject<Block> BIOME_PURIFIER = strongBlock("biomepurifier", 10f, 400f);
    public static final RegistryObject<Block> RELAY_CONTROLLER = strongBlock("relaycontroller", 10f, 200f);
    public static final RegistryObject<Block> PARASITE_BARRIER = strongBlock("parasitebarrier", 50f, 1200f);
    public static final RegistryObject<Block> NODE_RELAY = standardBlock("noderelay");
    public static final RegistryObject<Block> RELAY_BASE = standardBlock("relaybase");
    public static final RegistryObject<Block> RELAY_MIDDLE = standardBlock("relaymiddle");
    public static final RegistryObject<Block> RELAY_ROOF = standardBlock("relayroof");

    // ================================================================
    // INFESTED BLOCKS
    // ================================================================
    public static final RegistryObject<Block> INFESTED_STAIN = organicBlock("infectedstain");
    public static final RegistryObject<Block> INFEST_REMAIN = organicBlock("infestremain");
    public static final RegistryObject<Block> INFESTED_TRUNK = standardBlock("infestedtrunk");
    public static final RegistryObject<Block> INFESTED_RUBBLE = organicBlock("infectedrubble");
    public static final RegistryObject<Block> INFESTED_BUSH = organicBlock("infectedbush");
    public static final RegistryObject<Block> INFESTED_COBBLESTONE = strongBlock("infestedcobblestone", 2.0f, 6.0f);
    public static final RegistryObject<Block> INFESTED_PLANKS = standardBlock("infestedplanks");
    public static final RegistryObject<Block> INFESTED_STONE_BRICKS = strongBlock("infestedstonebricks", 1.5f, 6.0f);
    public static final RegistryObject<Block> INFESTED_TERRACOTTA = strongBlock("infestedterracotta", 1.5f, 6.0f);
    public static final RegistryObject<Block> POLISHED_INFECTED_STONE = strongBlock("polishedinfestedstone", 2.0f, 6.0f);
    public static final RegistryObject<Block> RESIDUE_BRICKS = strongBlock("residuebricks", 2.0f, 6.0f);
    public static final RegistryObject<Block> INFECTED_COLUMN = strongBlock("infestedcolumn", 2.0f, 6.0f);
    public static final RegistryObject<Block> INFESTED_SANDSTONE = strongBlock("infestedsandstone", 0.8f, 0.8f);
    public static final RegistryObject<Block> INFESTED_SANDSTONE_2 = strongBlock("infestedsandstone2", 0.8f, 0.8f);
    public static final RegistryObject<Block> INFESTED_SANDSTONE_3 = strongBlock("infestedsandstone3", 0.8f, 0.8f);
    public static final RegistryObject<Block> INFESTED_SAND = organicBlock("infestedsand");
    public static final RegistryObject<Block> INFESTED_LEAVES = organicBlock("infestedleaves");
    public static final RegistryObject<Block> INFESTED_ORE = strongBlock("infestedore", 3.0f, 3.0f);
    public static final RegistryObject<Block> INFESTED_FENCE = standardBlock("infestedfence");

    // ================================================================
    // PARASITE BLOCKS
    // ================================================================
    public static final RegistryObject<Block> PARASITE_TRUNK = standardBlock("parasitetrunk");
    public static final RegistryObject<Block> PARASITE_PLANK = standardBlock("parasiteplank");
    public static final RegistryObject<Block> PARASITE_STAIN = organicBlock("parasitestain");
    public static final RegistryObject<Block> PARASITE_LOOT = organicBlock("parasiteloot");
    public static final RegistryObject<Block> PARASITE_RUBBLE = organicBlock("parasiterubble");
    public static final RegistryObject<Block> PARASITE_STRUCTURE = standardBlock("parasitestructure");
    public static final RegistryObject<Block> PARASITE_THIN = organicBlock("parasitethin");
    public static final RegistryObject<Block> PARASITE_SAPLING = BLOCKS.register("parasitesapling",
            () -> new Block(BlockBehaviour.Properties.of().strength(0.0F).sound(SoundType.GRASS).noCollission().randomTicks()));
    public static final RegistryObject<Block> PARASITE_MOUTH = organicBlock("parasitemouth");
    public static final RegistryObject<Block> PARASITE_RUBBLE_DENSE = organicBlock("parasiterubbledense");
    public static final RegistryObject<Block> PARASITE_BUSH = organicBlock("parasitebush");
    public static final RegistryObject<Block> PARASITE_CACTUS = organicBlock("parasitecactus");
    public static final RegistryObject<Block> PARASITE_CANISTER = standardBlock("parasitecanister");
    public static final RegistryObject<Block> PARASITE_CANISTER_ACTIVE = standardBlock("parasitecanisteractive");
    public static final RegistryObject<Block> PARASITE_VINE = organicBlock("parasitevine");
    public static final RegistryObject<Block> PARASITE_FOG = organicBlock("parasitefog");

    // ================================================================
    // SPREADING BLOCKS
    // ================================================================
    public static final RegistryObject<Block> HARLEQUINN_GRASS = organicBlock("harlequinngrass");
    public static final RegistryObject<Block> HARLESKINN_BLOCK = standardBlock("harleskinnblock");
    public static final RegistryObject<Block> POLAND_SKIN_BLOCK = standardBlock("polandskinblock");

    // ================================================================
    // FUNCTIONAL BLOCKS
    // ================================================================
    public static final RegistryObject<Block> INFESTED_FURNACE = strongBlock("infestedfurnace", 2.0f, 6.0f);
    public static final RegistryObject<Block> EVOLUTION_LURE = standardBlock("evolutionlure");
    public static final RegistryObject<Block> BUGLIN = organicBlock("buglin");
    public static final RegistryObject<Block> INFESTATION_PURIFIER = strongBlock("infestationpurifier", 5.0f, 200f);
    public static final RegistryObject<Block> FOG_NULLIFIER = standardBlock("fognullifier");
    public static final RegistryObject<Block> INFUSER_FURNACE = strongBlock("infuserfurnace", 2.0f, 6.0f);
    public static final RegistryObject<Block> BIOMASS_BLOCK = organicBlock("biomassblock");
    public static final RegistryObject<Block> RESIDUE_BLOCK = organicBlock("residueblock");
    public static final RegistryObject<Block> ALVEOLI = organicBlock("alveoli");
    public static final RegistryObject<Block> SICK_ALVEOLI = organicBlock("sickalveoli");
    public static final RegistryObject<Block> ALVEOLI_GROWTH = organicBlock("alveoligrowth");
    public static final RegistryObject<Block> SOLID_ALVEOLI = standardBlock("solidalveoli");
    public static final RegistryObject<Block> DERMOID_CYST = organicBlock("dermoidcyst");
    public static final RegistryObject<Block> DOD = organicBlock("dod");
    
    // ================================================================
    // HAIR BLOCKS (Missing from original port - adding now)
    // ================================================================
    public static final RegistryObject<Block> LIPOMA_MASS = organicBlock("lipomamass");
    public static final RegistryObject<Block> HAIR_FOLLICLE_PILLAR = BLOCKS.register("hairfolliclepillar",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.0f, 3.0f).sound(SoundType.WOOL)));

    // ================================================================
    // GLASS BLOCKS (8 colors — using GlassBlock for transparency)
    // ================================================================
    public static final RegistryObject<GlassBlock> ASHEN_GLASS = BLOCKS.register("ashenglass",
            () -> new GlassBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion().isRedstoneConductor((s, r, p) -> false)));
    public static final RegistryObject<GlassBlock> SHROUDED_GLASS = BLOCKS.register("shroudedglass",
            () -> new GlassBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion().isRedstoneConductor((s, r, p) -> false)));
    public static final RegistryObject<GlassBlock> HARLEQUINN_GLASS = BLOCKS.register("harlequinnglass",
            () -> new GlassBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion().isRedstoneConductor((s, r, p) -> false)));
    public static final RegistryObject<GlassBlock> BLOODY_GLASS = BLOCKS.register("bloodyglass",
            () -> new GlassBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion().isRedstoneConductor((s, r, p) -> false)));
    public static final RegistryObject<GlassBlock> INFESTED_GLASS = BLOCKS.register("infestedglass",
            () -> new GlassBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion().isRedstoneConductor((s, r, p) -> false)));
    public static final RegistryObject<GlassBlock> SHADE_GLASS = BLOCKS.register("shadeglass",
            () -> new GlassBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion().isRedstoneConductor((s, r, p) -> false)));
    public static final RegistryObject<GlassBlock> SEPIA_GLASS = BLOCKS.register("sepiaglass",
            () -> new GlassBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion().isRedstoneConductor((s, r, p) -> false)));
    public static final RegistryObject<GlassBlock> MOODY_GLASS = BLOCKS.register("moodyglass",
            () -> new GlassBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion().isRedstoneConductor((s, r, p) -> false)));

    // Glass Panes (using IronBarsBlock for pane connection behavior)
    public static final RegistryObject<IronBarsBlock> ASHEN_GLASS_PANE = BLOCKS.register("ashenglasspane",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistryObject<IronBarsBlock> SHROUDED_GLASS_PANE = BLOCKS.register("shroudedglasspane",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistryObject<IronBarsBlock> HARLEQUINN_GLASS_PANE = BLOCKS.register("harlequinnglasspane",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistryObject<IronBarsBlock> BLOODY_GLASS_PANE = BLOCKS.register("bloodyglasspane",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistryObject<IronBarsBlock> INFESTED_GLASS_PANE = BLOCKS.register("infestedglasspane",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistryObject<IronBarsBlock> SHADE_GLASS_PANE = BLOCKS.register("shadeglasspane",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistryObject<IronBarsBlock> SEPIA_GLASS_PANE = BLOCKS.register("sepiaglasspane",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistryObject<IronBarsBlock> MOODY_GLASS_PANE = BLOCKS.register("moodyglasspane",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion()));

    // ================================================================
    // ESCA BULBS (default + 16 colors)
    // ================================================================
    public static final RegistryObject<Block> ESCA_BULB = organicBlock("escabulb");
    public static final RegistryObject<Block> ESCA_BULB_WHITE = organicBlock("escabulbwhite");
    public static final RegistryObject<Block> ESCA_BULB_ORANGE = organicBlock("escabulborange");
    public static final RegistryObject<Block> ESCA_BULB_MAGENTA = organicBlock("escabulbmagenta");
    public static final RegistryObject<Block> ESCA_BULB_LIGHT_BLUE = organicBlock("escabulblightblue");
    public static final RegistryObject<Block> ESCA_BULB_YELLOW = organicBlock("escabulbyellow");
    public static final RegistryObject<Block> ESCA_BULB_LIME = organicBlock("escabulblime");
    public static final RegistryObject<Block> ESCA_BULB_PINK = organicBlock("escabulbpink");
    public static final RegistryObject<Block> ESCA_BULB_GRAY = organicBlock("escabulbgray");
    public static final RegistryObject<Block> ESCA_BULB_LIGHT_GRAY = organicBlock("escabulblightgray");
    public static final RegistryObject<Block> ESCA_BULB_CYAN = organicBlock("escabulbcyan");
    public static final RegistryObject<Block> ESCA_BULB_PURPLE = organicBlock("escabulbpurple");
    public static final RegistryObject<Block> ESCA_BULB_BLUE = organicBlock("escabulbblue");
    public static final RegistryObject<Block> ESCA_BULB_BROWN = organicBlock("escabulbbrown");
    public static final RegistryObject<Block> ESCA_BULB_GREEN = organicBlock("escabulbgreen");
    public static final RegistryObject<Block> ESCA_BULB_RED = organicBlock("escabulbred");
    public static final RegistryObject<Block> ESCA_BULB_BLACK = organicBlock("escabulbblack");

    // ================================================================
    // WOOD BLOCKS
    // ================================================================
    public static final RegistryObject<Block> GOTH_STEM = standardBlock("gothstem");
    public static final RegistryObject<Block> GOTH_PLANKS = standardBlock("gothplanks");
    public static final RegistryObject<Block> FLESH_PLANKS = organicBlock("fleshplanks");
    public static final RegistryObject<Block> COOKED_FLESH_PLANKS = standardBlock("cookedfleshplanks");
    public static final RegistryObject<Block> COOKED_FLESH = standardBlock("cookedflesh");
    public static final RegistryObject<Block> BRUSEWOOD_PLANKS = standardBlock("brusewoodplanks");
    public static final RegistryObject<Block> CONSUMED_PLANKS = standardBlock("consumedplanks");

    // ================================================================
    // DOORS (DoorBlock — open/close with interaction and redstone)
    // ================================================================
    public static final RegistryObject<DoorBlock> GOTH_DOOR = BLOCKS.register("gothdoor",
            () -> new DoorBlock(BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD).noOcclusion(), BlockSetType.OAK));
    public static final RegistryObject<DoorBlock> BRUSEWOOD_DOOR = BLOCKS.register("brusewooddoor",
            () -> new DoorBlock(BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD).noOcclusion(), BlockSetType.OAK));
    public static final RegistryObject<DoorBlock> CONSUMED_DOOR = BLOCKS.register("consumeddoor",
            () -> new DoorBlock(BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD).noOcclusion(), BlockSetType.OAK));

    // ================================================================
    // TRAPDOORS (TrapDoorBlock — open/close with interaction and redstone)
    // ================================================================
    public static final RegistryObject<TrapDoorBlock> BRUSEWOOD_TRAPDOOR = BLOCKS.register("brusewoodtrapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD).noOcclusion(), BlockSetType.OAK));
    public static final RegistryObject<TrapDoorBlock> CONSUMED_TRAPDOOR = BLOCKS.register("consumedtrapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD).noOcclusion(), BlockSetType.OAK));
    public static final RegistryObject<TrapDoorBlock> GOTH_TRAPDOOR = BLOCKS.register("gothtrapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD).noOcclusion(), BlockSetType.OAK));

    // ================================================================
    // WORKBENCHES
    // ================================================================
    public static final RegistryObject<Block> CONSUMED_WORKBENCH = standardBlock("consumedworkbench");
    public static final RegistryObject<Block> INFESTED_WORKBENCH = standardBlock("infestedworkbench");

    // ================================================================
    // ASSIMILATED BLOCKS
    // ================================================================
    public static final RegistryObject<Block> ASSIMILATED_PUMPKIN = organicBlock("assimilatedpumpkin");
    public static final RegistryObject<Block> ASSIMILATED_JACK_O_LANTERN = organicBlock("assimilatedjackolantern");
    public static final RegistryObject<Block> ASSIMILATED_SUGAR_CANE = organicBlock("assimilatedsugarcane");
    public static final RegistryObject<Block> ASSIMILATED_BLOSSOM = organicBlock("assimilatedblossom");

    // ================================================================
    // POTTED BLOCKS
    // ================================================================
    public static final RegistryObject<Block> INFESTED_POT = standardBlock("infestedpot");
    public static final RegistryObject<Block> CONSUMED_POT = standardBlock("consumedpot");
    public static final RegistryObject<Block> POTTED_ASSIMILATED_BLOSSOM = standardBlock("pottedassimilatedblossom");
    public static final RegistryObject<Block> POTTED_CONSUMED_ASSIMILATED_BLOSSOM = standardBlock("pottedconsumedassimilatedblossom");

    // ================================================================
    // FLORA
    // ================================================================
    public static final RegistryObject<Block> THORNSHADE = organicBlock("thornshade");
    public static final RegistryObject<Block> DISEASED_SPONGE = organicBlock("diseasedsponge");
    public static final RegistryObject<Block> GOTH_SHROOM = organicBlock("gothshroom");

    // ================================================================
    // HAIR BLOCKS
    // ================================================================
    public static final RegistryObject<Block> HAIR_FOLLICLE = organicBlock("hairfollicle");
    public static final RegistryObject<Block> HIRSUTE_HAIR = organicBlock("hirsutehair");
    public static final RegistryObject<Block> TRESSES_HAIR = organicBlock("tresseshair");
    public static final RegistryObject<Block> LIPOPA_MASS = organicBlock("lipopamass");
    public static final RegistryObject<Block> LOCS = organicBlock("locs");

    // ================================================================
    // GORE BLOCKS (6 types)
    // ================================================================
    public static final RegistryObject<Block> GORE_SIM = organicBlock("goresim");
    public static final RegistryObject<Block> GORE_PRI = organicBlock("gorepri");
    public static final RegistryObject<Block> GORE_ADA = organicBlock("goreada");
    public static final RegistryObject<Block> GORE_PUR = organicBlock("gorepur");
    public static final RegistryObject<Block> GORE_FER = organicBlock("gorefer");
    public static final RegistryObject<Block> GORE_MAR = organicBlock("goremar");

    // ================================================================
    // TROPHIES
    // ================================================================
    public static final RegistryObject<Block> TROPHY_VOID_ORB = standardBlock("trophyvoidorb");
    public static final RegistryObject<Block> TROPHY_BOOM_ORB = standardBlock("trophyboomb");

    // ================================================================
    // FLUID BLOCKS
    // ================================================================
    public static final RegistryObject<LiquidBlock> DEAD_BLOOD = BLOCKS.register("deadblood",
            () -> new LiquidBlock(ModFluids.DEAD_BLOOD_STILL, BlockBehaviour.Properties.of().strength(100f).noLootTable()));
    public static final RegistryObject<Block> BLOODY_ICE = strongBlock("bloodyice", 0.5f, 0.5f);

    // ================================================================
    // SEMIORGANIC BLOCK
    // ================================================================
    public static final RegistryObject<Block> SEMIORGANIC_BLOCK = standardBlock("semiorganiblock");

    // ================================================================
    // WALLS (WallBlock — proper wall connections and height variants)
    // ================================================================
    public static final RegistryObject<WallBlock> INFESTED_STONE_BRICK_WALL = BLOCKS.register("infestedstonebrickwall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.STONE)));
    public static final RegistryObject<WallBlock> RESIDUE_BRICK_WALL = BLOCKS.register("residuebrickwall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.STONE)));
    public static final RegistryObject<WallBlock> PARASITE_COBBLESTONE_WALL = BLOCKS.register("parasitecobblestonewall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.STONE)));
    public static final RegistryObject<WallBlock> POLISHED_INFECTED_STONE_WALL = BLOCKS.register("polishedinfestedstonewall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.STONE)));
    public static final RegistryObject<WallBlock> INFESTED_SANDSTONE_WALL = BLOCKS.register("infestedsandstonewall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(0.8F, 0.8F).sound(SoundType.STONE)));
    public static final RegistryObject<WallBlock> GOTH_STEM_WALL = BLOCKS.register("gothstemwall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.WOOD)));

    // ================================================================
    // STAIRS (StairBlock — facing/shape variants, corner connections)
    // ================================================================
    public static final RegistryObject<StairBlock> INFESTED_STONE_BRICK_STAIRS = BLOCKS.register("infestedstonebrickstairs",
            () -> new StairBlock(() -> INFESTED_STONE_BRICKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.STONE)));
    public static final RegistryObject<StairBlock> RESIDUE_BRICK_STAIRS = BLOCKS.register("residuebrickstairs",
            () -> new StairBlock(() -> RESIDUE_BRICKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.STONE)));
    public static final RegistryObject<StairBlock> PARASITE_COBBLESTONE_STAIRS = BLOCKS.register("parasitecobblestonestairs",
            () -> new StairBlock(() -> INFESTED_COBBLESTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.STONE)));
    public static final RegistryObject<StairBlock> POLISHED_INFECTED_STONE_STAIRS = BLOCKS.register("polishedinfestedstonestairs",
            () -> new StairBlock(() -> POLISHED_INFECTED_STONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.STONE)));
    public static final RegistryObject<StairBlock> INFESTED_PLANK_STAIRS = BLOCKS.register("infestedplankstairs",
            () -> new StairBlock(() -> INFESTED_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<StairBlock> GOTH_PLANK_STAIRS = BLOCKS.register("gothplankstairs",
            () -> new StairBlock(() -> GOTH_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<StairBlock> CONSUMED_PLANK_STAIRS = BLOCKS.register("consumedplankstairs",
            () -> new StairBlock(() -> CONSUMED_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<StairBlock> BRUSEWOOD_PLANK_STAIRS = BLOCKS.register("brusewoodplankstairs",
            () -> new StairBlock(() -> BRUSEWOOD_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<StairBlock> INFESTED_SANDSTONE_STAIRS = BLOCKS.register("infestedsandstonestairs",
            () -> new StairBlock(() -> INFESTED_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(0.8F, 0.8F).sound(SoundType.STONE)));

    // ================================================================
    // SLABS (SlabBlock — top/bottom/double slab placement)
    // ================================================================
    public static final RegistryObject<SlabBlock> INFESTED_STONE_BRICK_SLAB = BLOCKS.register("infestedstonebrickslab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.STONE)));
    public static final RegistryObject<SlabBlock> RESIDUE_BRICK_SLAB = BLOCKS.register("residuebrickslab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.STONE)));
    public static final RegistryObject<SlabBlock> PARASITE_COBBLESTONE_SLAB = BLOCKS.register("parasitecobblestoneslab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.STONE)));
    public static final RegistryObject<SlabBlock> POLISHED_INFECTED_STONE_SLAB = BLOCKS.register("polishedinfestedstoneslab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.STONE)));
    public static final RegistryObject<SlabBlock> INFESTED_PLANK_SLAB = BLOCKS.register("infestedplankslab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<SlabBlock> GOTH_PLANK_SLAB = BLOCKS.register("gothplankslab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<SlabBlock> CONSUMED_PLANK_SLAB = BLOCKS.register("consumedplankslab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<SlabBlock> BRUSEWOOD_PLANK_SLAB = BLOCKS.register("brusewoodplankslab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<SlabBlock> INFESTED_SANDSTONE_SLAB = BLOCKS.register("infestedsandstoneslab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(0.8F, 0.8F).sound(SoundType.STONE)));

    // ================================================================
    // FENCES (FenceBlock — fence connections to adjacent fences/gates)
    // ================================================================
    public static final RegistryObject<FenceBlock> GOTH_FENCE = BLOCKS.register("gothfence",
            () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<FenceBlock> CONSUMED_FENCE = BLOCKS.register("consumedfence",
            () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<FenceBlock> BRUSEWOOD_FENCE = BLOCKS.register("brusewoodfence",
            () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<FenceBlock> FLESH_FENCE = BLOCKS.register("fleshfence",
            () -> new FenceBlock(BlockBehaviour.Properties.of().strength(1.0F, 3.0F).sound(SoundType.WOOD)));

    // ================================================================
    // WEB
    // ================================================================
    public static final RegistryObject<Block> SRP_WEB = organicBlock("srpweb");

    // ================================================================
    // NODE LAMPS
    // ================================================================
    public static final RegistryObject<Block> NODE_REDSTONE_LAMP = standardBlock("noderedstonelamp");
    public static final RegistryObject<Block> NODE_LAMP = standardBlock("nodelamp");

    private ModBlocks() {
        // Utility class - prevent instantiation
    }
}
