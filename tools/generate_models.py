#!/usr/bin/env python3
"""
SRP 1.20.1 Model JSON Generator
Generates all item and block model JSON files for the SubspaceParasite mod.
Ensures proper mapping between items/blocks and their textures.
"""

import os
import json

MOD_ID = "subspaceparasite"
BASE_PATH = "src/main/resources/assets/subspaceparasite/models"

# Items that need custom models (weapons, tools, armor, etc.)
CUSTOM_ITEMS = [
    # Weapons - Normal
    "parasiteclaw", "parasitefang", "parasitespike", "parasiteblade",
    "beckonweapon", "parasiteaxe", "parasitebow",
    # Weapons - Sentient
    "sentientclaw", "sentientfang", "sentientspike", "sentientblade",
    "sentientbeckonweapon", "sentientaxe", "sentientbow",
    # Hijacked Iron Tools
    "hijackedironsword", "hijackedironpickaxe", "hijackedironaxe",
    "hijackedironshovel", "hijackedironhoe",
    # Armor - Living
    "livinghelmet", "livingchestplate", "livingleggings", "livingboots",
    # Armor - Sentient
    "sentienthelmet", "sentientchestplate", "sentientleggings", "sentientboots",
    # Armor - Hijacked Iron
    "hijackedironhelmet", "hijackedironchestplate", "hijackedironleggings", "hijackedironboots",
    # Modules
    "moduleadapter", "modulebarricade", "moduledynamo", "moduleexothermic",
    "moduleferromagnetic", "modulegravitational", "modulehyperthreat",
    "moduleinsulating", "modulekinetic", "moduleluminous", "modulemotile",
    "modulenutrient", "moduleoutreach", "modulepheromone", "modulequantum",
    "moduleresilient", "modulesiege", "modulethornian", "moduleumbrella",
    "modulevenomous", "modulewanderer", "modulexenolithic",
    # Special Items
    "evolutionclock", "developmentclock", "evolutioncompass", "developmentcompass",
    "parasitecompass", "fieldguide", "parasitepearl",
    # Drops & Materials
    "parasiteflesh", "cookedparasiteflesh", "parasitetendon", "parasitebone",
    "parasitemembrane", "parasiteshell", "parasitecore", "biomass", "residue",
    "virulentresidue", "infestedresidue", "parasitenodule", "parasitegland",
    "weaknode", "strongnode", "parasiteclawdrop", "parasiteheart",
    "gooth", "spore", "mucus", "acid", "bile",
    "hairfollicleitem", "hirsutehairitem", "tresseshairitem",
    "lipopamassitem", "locsitem",
    # Food
    "rawparasite", "cookedparasite", "biomassfood", "bloodymeat", "dermoidcystfood",
    # Lure Components
    "lurebase", "lureprimordial", "lureadaptive", "lureferal", "lurepure",
    # Functional Items
    "parasitebomb", "purifyingsalve", "antidote", "cleansingtotem",
    "infestationneedle", "evolutioncatalyst", "callofthehive",
    "parasitesyringe", "beckonessence",
    # Music Discs
    "music_disc_evo", "music_disc_assimilate",
    # Debug Items
    "itemevolve", "itemdevolve", "itemvariant", "itemassimilate",
    "itemthrow", "itemtab"
]

# Blocks that need custom block models
CUSTOM_BLOCKS = [
    # System Blocks
    "biomeheart", "colonyheart", "colonyoutpost", "biomepurifier",
    "relaycontroller", "parasitebarrier", "noderelay", "relaybase",
    "relaymiddle", "relayroof",
    # Infested Blocks
    "infectedstain", "infestremain", "infestedtrunk", "infectedrubble",
    "infectedbush", "infestedcobblestone", "infestedplanks", "infestedstonebricks",
    "infestedterracotta", "polishedinfestedstone", "residuebricks", "infestedcolumn",
    "infestedsandstone", "infestedsandstone2", "infestedsandstone3",
    "infestedsand", "infestedleaves", "infestedore", "infestedfence",
    # Parasite Blocks
    "parasitetrunk", "parasiteplank", "parasitestain", "parasiteloot",
    "parasiterubble", "parasitestructure", "parasitethin", "parasitesapling",
    "parasitemouth", "parasiterubbledense", "parasitebush", "parasitecactus",
    "parasitecanister", "parasitecanisteractive", "parasitevine", "parasitefog",
    # Spreading Blocks
    "harlequinngrass", "harleskinnblock", "polandskinblock",
    # Functional Blocks
    "infestedfurnace", "evolutionlure", "buglin", "infestationpurifier",
    "fognullifier", "infuserfurnace", "biomassblock", "residueblock",
    "alveoli", "sickalveoli", "alveoligrowth", "solidalveoli",
    "dermoidcyst", "dod",
    # Glass Blocks
    "ashenglass", "shroudedglass", "harlequinnglass", "bloodyglass",
    "infestedglass", "shadeglass", "sepiaglass", "moodyglass",
    # Escal Bulbs
    "escabulb", "escabulbwhite", "escabulborange", "escabulbmagenta",
    "escabulblightblue", "escabulbyellow", "escabulblime", "escabulbpink",
    "escabulbgray", "escabulblightgray", "escabulbcyan", "escabulbpurple",
    "escabulbblue", "escabulbbrown", "escabulbgreen", "escabulbred", "escabulbblack",
    # Wood Types
    "gothstem", "gothplanks", "fleshplanks", "cookedfleshplanks",
    "brusewoodplanks", "consumedplanks",
    # Trophies
    "trophyvoidorb", "trophyboomorb",
    # Flora
    "assimilatedblossom", "thornshade", "gothshroom",
    # Ice & Web
    "bloodyice", "srpweb",
    # Lamps
    "noderedstonelamp", "nodelamp",
    # Semiorganic
    "semiorganiblock",
    # Dead Blood
    "deadblood"
]

# Block variants (stairs, slabs, walls, doors, trapdoors)
STAIR_BLOCKS = [
    "infestedstonebrick", "residuebrick", "parasitecobblestone",
    "polishedinfestedstone", "infestedplank", "gothplank",
    "consumedplank", "brusewoodplank", "infestedsandstone"
]

SLAB_BLOCKS = [
    "infestedstonebrick", "residuebrick", "parasitecobblestone",
    "polishedinfestedstone", "infestedplank", "gothplank",
    "consumedplank", "brusewoodplank", "infestedsandstone"
]

WALL_BLOCKS = [
    "infestedstonebrick", "residuebrick", "parasitecobblestone",
    "polishedinfestedstone", "infestedsandstone", "gothstem"
]

DOOR_BLOCKS = ["goth", "brusewood", "consumed"]
TRAPDOOR_BLOCKS = ["goth", "brusewood", "consumed"]
FENCE_BLOCKS = ["goth", "consumed", "brusewood", "flesh"]


def create_item_model(item_name, parent="minecraft:item/generated", texture_override=None):
    """Create an item model JSON file."""
    model = {
        "parent": parent
    }
    
    if parent == "minecraft:item/generated":
        model["textures"] = {
            "layer0": f"{MOD_ID}:item/{texture_override if texture_override else item_name}"
        }
    elif parent == "minecraft:item/handheld":
        model["textures"] = {
            "layer0": f"{MOD_ID}:item/{texture_override if texture_override else item_name}"
        }
    elif parent.startswith("minecraft:item/"):
        model["textures"] = {
            "layer0": f"{MOD_ID}:item/{texture_override if texture_override else item_name}"
        }
    
    return model


def create_block_model(block_name, texture_name=None, parent="minecraft:block/cube_all"):
    """Create a block model JSON file."""
    tex_name = texture_name if texture_name else block_name
    
    if parent == "minecraft:block/cube_all":
        return {
            "parent": parent,
            "textures": {
                "all": f"{MOD_ID}:block/{tex_name}"
            }
        }
    elif parent == "minecraft:block/cube_bottom_top":
        return {
            "parent": parent,
            "textures": {
                "side": f"{MOD_ID}:block/{tex_name}_side",
                "bottom": f"{MOD_ID}:block/{tex_name}_bottom",
                "top": f"{MOD_ID}:block/{tex_name}_top"
            }
        }
    elif parent == "minecraft:block/cross":
        return {
            "parent": parent,
            "textures": {
                "cross": f"{MOD_ID}:block/{tex_name}"
            }
        }
    elif parent == "minecraft:block/carpet":
        return {
            "parent": parent,
            "textures": {
                "wool": f"{MOD_ID}:block/{tex_name}"
            }
        }
    elif parent == "minecraft:block/glass":
        return {
            "parent": parent,
            "textures": {
                "all": f"{MOD_ID}:block/{tex_name}"
            }
        }
    elif parent == "minecraft:block/pane":
        return {
            "parent": parent,
            "textures": {
                "edge": f"{MOD_ID}:block/{tex_name}_edge",
                "pane": f"{MOD_ID}:block/{tex_name}"
            }
        }
    else:
        return {
            "parent": parent,
            "textures": {
                "all": f"{MOD_ID}:block/{tex_name}"
            }
        }


def create_stair_model(base_name):
    """Create stair block model."""
    return {
        "parent": "minecraft:block/stairs",
        "textures": {
            "bottom": f"{MOD_ID}:block/{base_name}",
            "side": f"{MOD_ID}:block/{base_name}",
            "top": f"{MOD_ID}:block/{base_name}"
        }
    }


def create_slab_model(base_name):
    """Create slab block model."""
    return {
        "parent": "minecraft:block/slab",
        "textures": {
            "bottom": f"{MOD_ID}:block/{base_name}",
            "side": f"{MOD_ID}:block/{base_name}",
            "top": f"{MOD_ID}:block/{base_name}"
        }
    }


def create_wall_model(base_name):
    """Create wall block model."""
    return {
        "parent": "minecraft:block/wall_inventory",
        "textures": {
            "wall": f"{MOD_ID}:block/{base_name}"
        }
    }


def create_door_item_model(door_name):
    """Create door item model (3D standing door)."""
    return {
        "parent": "minecraft:item/generated",
        "textures": {
            "layer0": f"{MOD_ID}:item/{door_name}_door"
        }
    }


def create_trapdoor_item_model(trapdoor_name):
    """Create trapdoor item model."""
    return {
        "parent": "minecraft:item/generated",
        "textures": {
            "layer0": f"{MOD_ID}:item/{trapdoor_name}_trapdoor"
        }
    }


def write_json_file(filepath, data):
    """Write JSON data to file with proper formatting."""
    os.makedirs(os.path.dirname(filepath), exist_ok=True)
    with open(filepath, 'w') as f:
        json.dump(data, f, indent=2)
    print(f"  Created: {filepath}")


def generate_all_models():
    """Generate all item and block models."""
    print("=" * 60)
    print("SRP Model JSON Generator")
    print("=" * 60)
    
    item_path = f"{BASE_PATH}/item"
    block_path = f"{BASE_PATH}/block"
    
    os.makedirs(item_path, exist_ok=True)
    os.makedirs(block_path, exist_ok=True)
    
    # Generate custom item models
    print("\n📦 Generating Item Models...")
    
    # Weapons and tools (handheld)
    handheld_items = [
        "parasiteclaw", "parasitefang", "parasitespike", "parasiteblade",
        "beckonweapon", "parasiteaxe",
        "sentientclaw", "sentientfang", "sentientspike", "sentientblade",
        "sentientbeckonweapon", "sentientaxe",
        "hijackedironsword", "hijackedironpickaxe", "hijackedironaxe",
        "hijackedironshovel", "hijackedironhoe"
    ]
    
    for item in handheld_items:
        model = create_item_model(item, parent="minecraft:item/handheld")
        write_json_file(f"{item_path}/{item}.json", model)
    
    # Bows (special model)
    for bow in ["parasitebow", "sentientbow"]:
        # Main bow model
        model = {
            "parent": "minecraft:item/bow",
            "overrides": [
                {"predicate": {"pulling": 1}, "model": f"{MOD_ID}:item/{bow}_pulling_0"},
                {"predicate": {"pulling": 1, "pull": 0.65}, "model": f"{MOD_ID}:item/{bow}_pulling_1"},
                {"predicate": {"pulling": 1, "pull": 0.9}, "model": f"{MOD_ID}:item/{bow}_pulling_2"}
            ]
        }
        write_json_file(f"{item_path}/{bow}.json", model)
        
        # Bow pulling states
        for i, pull in enumerate(["0", "1", "2"]):
            pull_model = create_item_model(f"{bow}_pulling_{pull}", texture_override=f"{bow}_pulling_{pull}")
            write_json_file(f"{item_path}/{bow}_pulling_{pull}.json", pull_model)
    
    # Armor (generated)
    armor_items = [
        "livinghelmet", "livingchestplate", "livingleggings", "livingboots",
        "sentienthelmet", "sentientchestplate", "sentientleggings", "sentientboots",
        "hijackedironhelmet", "hijackedironchestplate", "hijackedironleggings", "hijackedironboots"
    ]
    
    for item in armor_items:
        model = create_item_model(item)
        write_json_file(f"{item_path}/{item}.json", model)
    
    # Modules (generated)
    module_items = [
        "moduleadapter", "modulebarricade", "moduledynamo", "moduleexothermic",
        "moduleferromagnetic", "modulegravitational", "modulehyperthreat",
        "moduleinsulating", "modulekinetic", "moduleluminous", "modulemotile",
        "modulenutrient", "moduleoutreach", "modulepheromone", "modulequantum",
        "moduleresilient", "modulesiege", "modulethornian", "moduleumbrella",
        "modulevenomous", "modulewanderer", "modulexenolithic"
    ]
    
    for item in module_items:
        model = create_item_model(item)
        write_json_file(f"{item_path}/{item}.json", model)
    
    # Other special items
    special_items = [
        "evolutionclock", "developmentclock", "evolutioncompass", "developmentcompass",
        "parasitecompass", "fieldguide", "parasitepearl",
        "parasiteflesh", "cookedparasiteflesh", "parasitetendon", "parasitebone",
        "parasitemembrane", "parasiteshell", "parasitecore", "biomass", "residue",
        "virulentresidue", "infestedresidue", "parasitenodule", "parasitegland",
        "weaknode", "strongnode", "parasiteclawdrop", "parasiteheart",
        "gooth", "spore", "mucus", "acid", "bile",
        "hairfollicleitem", "hirsutehairitem", "tresseshairitem",
        "lipopamassitem", "locsitem",
        "rawparasite", "cookedparasite", "biomassfood", "bloodymeat", "dermoidcystfood",
        "lurebase", "lureprimordial", "lureadaptive", "lureferal", "lurepure",
        "parasitebomb", "purifyingsalve", "antidote", "cleansingtotem",
        "infestationneedle", "evolutioncatalyst", "callofthehive",
        "parasitesyringe", "beckonessence",
        "itemevolve", "itemdevolve", "itemvariant", "itemassimilate",
        "itemthrow", "itemtab"
    ]
    
    for item in special_items:
        model = create_item_model(item)
        write_json_file(f"{item_path}/{item}.json", model)
    
    # Music discs
    for disc in ["music_disc_evo", "music_disc_assimilate"]:
        model = {
            "parent": "minecraft:item/generated",
            "textures": {
                "layer0": f"{MOD_ID}:item/{disc}"
            }
        }
        write_json_file(f"{item_path}/{disc}.json", model)
    
    # Spawn eggs
    spawn_eggs = [
        "spawneigginfectedcreeper", "spawneigginfectedskeleton", "spawneigginfectedzombie",
        "spawneigginfectedspider", "spawneigginfectedenderman", "spawneigginfectedpig",
        "spawneigginfectedcow", "spawneigginfectedsheep", "spawneigginfectedchicken",
        "spawneigginfectedvillager", "spawneigginfectedwolf", "spawneigginfectedhorse",
        "spawneiggferalcreeper", "spawneiggferalskeleton", "spawneiggferalzombie",
        "spawneiggferalspider",
        "spawneigghijackedcreeper", "spawneigghijackedskeleton", "spawneigghijackedzombie",
        "spawneigginborn1", "spawneigginborn2", "spawneigginborn3", "spawneigginborn4",
        "spawneiggcrude1", "spawneiggcrude2", "spawneiggcrude3",
        "spawneiggprimitive1", "spawneiggprimitive2",
        "spawneiggadapted1", "spawneiggadapted2", "spawneiggadapted3",
        "spawneiggnexusbeckon", "spawneiggnexusdispatcher", "spawneiggnexusrooter", "spawneiggnexusother",
        "spawneiggdeterrent1", "spawneiggdeterrent2",
        "spawneiggpure1", "spawneiggpure2",
        "spawneiggpreeminent1", "spawneiggpreeminent2",
        "spawneiggancient1", "spawneiggancient2",
        "spawneiggderived1", "spawneiggderived2",
        "spawneiggabomination1", "spawneiggabomination2"
    ]
    
    for egg in spawn_eggs:
        model = create_item_model(egg)
        write_json_file(f"{item_path}/{egg}.json", model)
    
    # Bucket item
    bucket_model = {
        "parent": "minecraft:item/generated",
        "textures": {
            "layer0": f"{MOD_ID}:item/dead_blood_bucket"
        }
    }
    write_json_file(f"{item_path}/dead_blood_bucket.json", bucket_model)
    
    print(f"  ✓ Generated {len(handheld_items) + 2 + 8 + len(module_items) + len(special_items) + len(spawn_eggs) + 1} item models")
    
    # Generate block models
    print("\n📦 Generating Block Models...")
    
    # Standard cube blocks
    standard_blocks = [
        "biomeheart", "colonyheart", "colonyoutpost", "biomepurifier",
        "relaycontroller", "parasitebarrier", "noderelay", "relaybase",
        "relaymiddle", "relayroof",
        "infectedstain", "infestremain", "infestedtrunk", "infectedrubble",
        "infectedbush", "infestedcobblestone", "infestedplanks", "infestedstonebricks",
        "infestedterracotta", "polishedinfestedstone", "residuebricks", "infestedcolumn",
        "infestedsand", "infestedleaves", "infestedore", "infestedfence",
        "parasitetrunk", "parasiteplank", "parasitestain", "parasiteloot",
        "parasiterubble", "parasitestructure", "parasitethin",
        "parasiterubbledense", "parasitebush", "parasitecactus",
        "parasitecanister", "parasitecanisteractive", "parasitevine", "parasitefog",
        "harlequinngrass", "harleskinnblock", "polandskinblock",
        "infestedfurnace", "evolutionlure", "buglin", "infestationpurifier",
        "fognullifier", "infuserfurnace", "biomassblock", "residueblock",
        "alveoli", "sickalveoli", "alveoligrowth", "solidalveoli",
        "dermoidcyst", "dod",
        "escabulb", "escabulbwhite", "escabulborange", "escabulbmagenta",
        "escabulblightblue", "escabulbyellow", "escabulblime", "escabulbpink",
        "escabulbgray", "escabulblightgray", "escabulbcyan", "escabulbpurple",
        "escabulbblue", "escabulbbrown", "escabulbgreen", "escabulbred", "escabulbblack",
        "gothstem", "gothplanks", "fleshplanks", "cookedfleshplanks",
        "brusewoodplanks", "consumedplanks",
        "assimilatedblossom", "thornshade", "gothshroom",
        "bloodyice", "srpweb",
        "noderedstonelamp", "nodelamp",
        "semiorganiblock", "deadblood"
    ]
    
    for block in standard_blocks:
        model = create_block_model(block)
        write_json_file(f"{block_path}/{block}.json", model)
    
    # Glass blocks (transparent)
    glass_blocks = [
        "ashenglass", "shroudedglass", "harlequinnglass", "bloodyglass",
        "infestedglass", "shadeglass", "sepiaglass", "moodyglass"
    ]
    
    for block in glass_blocks:
        model = create_block_model(block, parent="minecraft:block/glass")
        write_json_file(f"{block_path}/{block}.json", model)
    
    # Sandstone variants (cube_bottom_top)
    sandstone_blocks = ["infestedsandstone", "infestedsandstone2", "infestedsandstone3"]
    
    for block in sandstone_blocks:
        model = create_block_model(block, parent="minecraft:block/cube_bottom_top")
        write_json_file(f"{block_path}/{block}.json", model)
    
    # Sapling (cross)
    sapling_model = create_block_model("parasitesapling", parent="minecraft:block/cross")
    write_json_file(f"{block_path}/parasitesapling.json", sapling_model)
    
    # Trophies (cross)
    for trophy in ["trophyvoidorb", "trophyboomorb"]:
        model = create_block_model(trophy, parent="minecraft:block/cross")
        write_json_file(f"{block_path}/{trophy}.json", model)
    
    # Stairs
    for base in STAIR_BLOCKS:
        model = create_stair_model(base)
        write_json_file(f"{block_path}/{base}_stairs.json", model)
    
    # Slabs
    for base in SLAB_BLOCKS:
        model = create_slab_model(base)
        write_json_file(f"{block_path}/{base}_slab.json", model)
    
    # Walls
    for base in WALL_BLOCKS:
        model = create_wall_model(base)
        write_json_file(f"{block_path}/{base}_wall.json", model)
    
    # Doors (block models)
    for door in DOOR_BLOCKS:
        # Top door
        top_model = {
            "parent": "minecraft:block/door_top",
            "textures": {
                "bottom": f"{MOD_ID}:block/{door}_door_bottom",
                "top": f"{MOD_ID}:block/{door}_door_top"
            }
        }
        write_json_file(f"{block_path}/{door}_door_top.json", top_model)
        
        # Bottom door
        bottom_model = {
            "parent": "minecraft:block/door_bottom",
            "textures": {
                "bottom": f"{MOD_ID}:block/{door}_door_bottom",
                "top": f"{MOD_ID}:block/{door}_door_top"
            }
        }
        write_json_file(f"{block_path}/{door}_door_bottom.json", bottom_model)
    
    # Trapdoors (block models)
    for trapdoor in TRAPDOOR_BLOCKS:
        trap_model = {
            "parent": "minecraft:block/template_trapdoor_bottom",
            "textures": {
                "texture": f"{MOD_ID}:block/{trapdoor}_trapdoor"
            }
        }
        write_json_file(f"{block_path}/{trapdoor}_trapdoor_bottom.json", trap_model)
        
        trap_open = {
            "parent": "minecraft:block/template_trapdoor_open",
            "textures": {
                "texture": f"{MOD_ID}:block/{trapdoor}_trapdoor"
            }
        }
        write_json_file(f"{block_path}/{trapdoor}_trapdoor_open.json", trap_open)
        
        trap_top = {
            "parent": "minecraft:block/template_trapdoor_top",
            "textures": {
                "texture": f"{MOD_ID}:block/{trapdoor}_trapdoor"
            }
        }
        write_json_file(f"{block_path}/{trapdoor}_trapdoor_top.json", trap_top)
    
    # Fences
    for fence in FENCE_BLOCKS:
        fence_model = {
            "parent": "minecraft:block/fence_inventory",
            "textures": {
                "texture": f"{MOD_ID}:block/{fence}_fence"
            }
        }
        write_json_file(f"{block_path}/{fence}_fence.json", fence_model)
    
    # Workbenches
    for workbench in ["consumedworkbench", "infestedworkbench"]:
        model = create_block_model(workbench, parent="minecraft:block/cube_bottom_top")
        write_json_file(f"{block_path}/{workbench}.json", model)
    
    # Pots
    pot_model = create_block_model("consumedpot", parent="minecraft:block/flower_pot_cross")
    write_json_file(f"{block_path}/consumedpot.json", pot_model)
    
    # Potted plants
    potted_model = create_block_model("pottedconsumedassimilatedblossom", parent="minecraft:block/flower_pot_cross")
    write_json_file(f"{block_path}/pottedconsumedassimilatedblossom.json", potted_model)
    
    print(f"  ✓ Generated {len(standard_blocks) + len(glass_blocks) + len(sandstone_blocks) + 1 + 2 + len(STAIR_BLOCKS) + len(SLAB_BLOCKS) + len(WALL_BLOCKS) + len(DOOR_BLOCKS)*2 + len(TRAPDOOR_BLOCKS)*3 + len(FENCE_BLOCKS) + 2 + 1 + 1} block models")
    
    # Generate item blockstate references (for BlockItems)
    print("\n📦 Generating Blockstate References...")
    
    # All blocks need item model references in blockstates
    all_blocks = standard_blocks + glass_blocks + sandstone_blocks + ["parasitesapling"] + \
                 [f"{b}_stairs" for b in STAIR_BLOCKS] + \
                 [f"{b}_slab" for b in SLAB_BLOCKS] + \
                 [f"{b}_wall" for b in WALL_BLOCKS]
    
    print(f"  ✓ Will generate blockstates for {len(all_blocks)} blocks")
    
    print("\n" + "=" * 60)
    print("✅ Model generation complete!")
    print("=" * 60)


if __name__ == "__main__":
    generate_all_models()
