#!/usr/bin/env python3
"""
Generate comprehensive item model JSON files for SubspaceParasite mod.
Creates models for all registered items including weapons, armor, tools, modules, and materials.
"""

import json
import os

# Base paths
BASE_PATH = "/workspace/srp_framework/SubspaceParasite/src/main/resources/assets/subspaceparasite"
ITEM_MODELS_PATH = os.path.join(BASE_PATH, "models/item")
BLOCKSTATES_PATH = os.path.join(BASE_PATH, "blockstates")
BLOCK_MODELS_PATH = os.path.join(BASE_PATH, "models/block")

# Ensure directories exist
os.makedirs(ITEM_MODELS_PATH, exist_ok=True)
os.makedirs(BLOCKSTATES_PATH, exist_ok=True)
os.makedirs(BLOCK_MODELS_PATH, exist_ok=True)

def create_item_model(item_name, parent="minecraft:item/generated", textures=None, overrides=None):
    """Create an item model JSON file."""
    if textures is None:
        textures = {"layer0": f"subspaceparasite:item/{item_name}"}
    
    model_data = {
        "parent": parent,
        "textures": textures
    }
    
    if overrides:
        model_data["overrides"] = overrides
    
    output_path = os.path.join(ITEM_MODELS_PATH, f"{item_name}.json")
    with open(output_path, 'w') as f:
        json.dump(model_data, f, indent=2)
    return output_path

def create_handheld_model(item_name):
    """Create a handheld item model (for tools/weapons)."""
    return create_item_model(item_name, parent="minecraft:item/handheld")

def create_block_item_model(block_name):
    """Create an item model for a block item."""
    return create_item_model(block_name, parent="minecraft:item/generated", 
                            textures={"layer0": f"subspaceparasite:block/{block_name}"})

def create_blockstate_simple(block_name):
    """Create a simple blockstate JSON file."""
    blockstate_data = {
        "variants": {
            "": {"model": f"subspaceparasite:block/{block_name}"}
        }
    }
    output_path = os.path.join(BLOCKSTATES_PATH, f"{block_name}.json")
    with open(output_path, 'w') as f:
        json.dump(blockstate_data, f, indent=2)
    return output_path

def create_block_model_simple(block_name, texture_name=None, parent="minecraft:block/cube_all"):
    """Create a simple block model JSON file."""
    if texture_name is None:
        texture_name = block_name
    
    if parent == "minecraft:block/cube_all":
        model_data = {
            "parent": parent,
            "textures": {
                "all": f"subspaceparasite:block/{texture_name}"
            }
        }
    else:
        model_data = {
            "parent": parent,
            "textures": {}
        }
    
    output_path = os.path.join(BLOCK_MODELS_PATH, f"{block_name}.json")
    with open(output_path, 'w') as f:
        json.dump(model_data, f, indent=2)
    return output_path

# ============================================================
# WEAPONS - Normal (7)
# ============================================================
print("Generating weapon models...")
weapons_normal = [
    "parasiteclaw", "parasitefang", "parasitespike", "parasiteblade",
    "beckonweapon", "parasiteaxe", "parasitebow"
]
for weapon in weapons_normal:
    if weapon.endswith("bow"):
        # Bow needs special model with pulling states
        bow_model = {
            "parent": "minecraft:item/bow",
            "textures": {
                "layer0": f"subspaceparasite:item/{weapon}"
            },
            "overrides": [
                {"predicate": {"pulling": 1}, "model": f"subspaceparasite:item/{weapon}_pulling_0"},
                {"predicate": {"pulling": 1, "pull": 0.65}, "model": f"subspaceparasite:item/{weapon}_pulling_1"},
                {"predicate": {"pulling": 1, "pull": 0.9}, "model": f"subspaceparasite:item/{weapon}_pulling_2"}
            ]
        }
        with open(os.path.join(ITEM_MODELS_PATH, f"{weapon}.json"), 'w') as f:
            json.dump(bow_model, f, indent=2)
    else:
        create_handheld_model(weapon)

# ============================================================
# WEAPONS - Sentient (7)
# ============================================================
weapons_sentient = [
    "sentientclaw", "sentientfang", "sentientspike", "sentientblade",
    "sentientbeckonweapon", "sentientaxe", "sentientbow"
]
for weapon in weapons_sentient:
    if weapon.endswith("bow"):
        bow_model = {
            "parent": "minecraft:item/bow",
            "textures": {
                "layer0": f"subspaceparasite:item/{weapon}"
            },
            "overrides": [
                {"predicate": {"pulling": 1}, "model": f"subspaceparasite:item/{weapon}_pulling_0"},
                {"predicate": {"pulling": 1, "pull": 0.65}, "model": f"subspaceparasite:item/{weapon}_pulling_1"},
                {"predicate": {"pulling": 1, "pull": 0.9}, "model": f"subspaceparasite:item/{weapon}_pulling_2"}
            ]
        }
        with open(os.path.join(ITEM_MODELS_PATH, f"{weapon}.json"), 'w') as f:
            json.dump(bow_model, f, indent=2)
    else:
        create_handheld_model(weapon)

# ============================================================
# HIJACKED IRON TOOLS (5)
# ============================================================
hijacked_tools = [
    "hijackedironsword", "hijackedironpickaxe", "hijackedironaxe",
    "hijackedironshovel", "hijackedironhoe"
]
for tool in hijacked_tools:
    create_handheld_model(tool)

# ============================================================
# ARMOR - Living (4)
# ============================================================
print("Generating armor models...")
living_armor = ["livinghelmet", "livingchestplate", "livingleggings", "livingboots"]
for armor in living_armor:
    create_item_model(armor)

# ============================================================
# ARMOR - Sentient (4)
# ============================================================
sentient_armor = ["sentienthelmet", "sentientchestplate", "sentientleggings", "sentientboots"]
for armor in sentient_armor:
    create_item_model(armor)

# ============================================================
# ARMOR - Hijacked Iron (4)
# ============================================================
hijacked_armor = ["hijackedironhelmet", "hijackedironchestplate", "hijackedironleggings", "hijackedironboots"]
for armor in hijacked_armor:
    create_item_model(armor)

# ============================================================
# MODULE ITEMS (22)
# ============================================================
print("Generating module models...")
modules = [
    "moduleadapter", "modulebarricade", "moduledynamo", "moduleexothermic",
    "moduleferromagnetic", "modulegravitational", "modulehyperthreat", "moduleinsulating",
    "modulekinetic", "moduleluminous", "modulemotile", "modulenutrient",
    "moduleoutreach", "modulepheromone", "modulequantum", "moduleresilient",
    "modulesiege", "modulethornian", "moduleumbrella", "modulevenomous",
    "modulewanderer", "modulexenolithic"
]
for module in modules:
    create_item_model(module)

# ============================================================
# EVOLUTION/DEVELOPMENT ITEMS (5)
# ============================================================
print("Generating evolution item models...")
evo_items = ["evolutionclock", "developmentclock", "evolutioncompass", "developmentcompass", "parasitecompass"]
for item in evo_items:
    create_item_model(item)

# ============================================================
# SPECIAL ITEMS
# ============================================================
special_items = ["fieldguide", "parasitepearl"]
for item in special_items:
    create_item_model(item)

# ============================================================
# DROPS & MATERIALS (28+)
# ============================================================
print("Generating material models...")
materials = [
    "parasiteflesh", "cookedparasiteflesh", "parasitetendon", "parasitebone",
    "parasitemembrane", "parasiteshell", "parasitecore", "biomass",
    "residue", "virulentresidue", "infestedresidue", "parasitenodule",
    "parasitegland", "weaknode", "strongnode", "parasiteclawdrop",
    "parasiteheart", "gooth", "spore", "mucus",
    "acid", "bile", "hairfollicleitem", "hirsutehairitem",
    "tresseshairitem", "lipopamassitem", "locsitem"
]
for mat in materials:
    create_item_model(mat)

# ============================================================
# FOOD ITEMS (5)
# ============================================================
food_items = ["rawparasite", "cookedparasite", "biomassfood", "bloodymeat", "dermoidcystfood"]
for food in food_items:
    create_item_model(food)

# ============================================================
# LURE COMPONENTS (5)
# ============================================================
lure_components = ["lurebase", "lureprimordial", "lureadaptive", "lureferal", "lurepure"]
for lure in lure_components:
    create_item_model(lure)

# ============================================================
# FUNCTIONAL ITEMS (9)
# ============================================================
functional_items = [
    "parasitebomb", "purifyingsalve", "antidote", "cleansingtotem",
    "infestationneedle", "evolutioncatalyst", "callofthehive",
    "parasitesyringe", "beckonessence"
]
for item in functional_items:
    create_item_model(item)

# ============================================================
# MUSIC DISCS (2)
# ============================================================
music_discs = ["music_disc_evo", "music_disc_assimilate"]
for disc in music_discs:
    create_item_model(disc, parent="minecraft:item/music_disc")

# ============================================================
# FLUID BUCKET
# ============================================================
create_item_model("deadblood_bucket", parent="minecraft:item/generated")

# ============================================================
# BLOCK ITEMS - System Blocks (6)
# ============================================================
print("Generating block item models...")
system_blocks = [
    "biomeheart", "colonyheart", "colonyoutpost", "biomepurifier",
    "relaycontroller", "parasitebarrier"
]
for block in system_blocks:
    create_block_item_model(block)
    create_blockstate_simple(block)
    create_block_model_simple(block)

# ============================================================
# BLOCK ITEMS - Functional Blocks (7)
# ============================================================
functional_blocks = [
    "evolutionlure", "infestationpurifier", "fognullifier",
    "infestedfurnace", "infuserfurnace", "consumedworkbench", "infestedworkbench"
]
for block in functional_blocks:
    create_block_item_model(block)
    create_blockstate_simple(block)
    create_block_model_simple(block)

# ============================================================
# ESCAL BULBS (16 colors)
# ============================================================
escal_bulbs = [
    "escabulb", "escabulbwhite", "escabulborange", "escabulbmagenta",
    "escabulblightblue", "escabulbyellow", "escabulblime", "escabulbpink",
    "escabulbgray", "escabulblightgray", "escabulbcyan", "escabulbpurple",
    "escabulbblue", "escabulbbrown", "escabulbgreen", "escabulbred", "escabulbblack"
]
for bulb in escal_bulbs:
    create_block_item_model(bulb)
    create_blockstate_simple(bulb)
    create_block_model_simple(bulb)

# ============================================================
# WOOD & PLANKS (6)
# ============================================================
wood_planks = [
    "gothstem", "gothplanks", "fleshplanks", "cookedfleshplanks",
    "brusewoodplanks", "consumedplanks"
]
for block in wood_planks:
    create_block_item_model(block)
    create_blockstate_simple(block)
    create_block_model_simple(block)

# ============================================================
# DOORS (3) - Special handling
# ============================================================
doors = ["gothdoor", "brusewooddoor", "consumeddoor"]
for door in doors:
    # Door items use a different model structure
    door_model = {
        "parent": "minecraft:item/generated",
        "textures": {
            "layer0": f"subspaceparasite:item/{door}"
        }
    }
    with open(os.path.join(ITEM_MODELS_PATH, f"{door}.json"), 'w') as f:
        json.dump(door_model, f, indent=2)

# ============================================================
# TRAPDOORS (3)
# ============================================================
trapdoors = ["brusewoodtrapdoor", "consumedtrapdoor", "gothtrapdoor"]
for trapdoor in trapdoors:
    create_block_item_model(trapdoor)
    create_blockstate_simple(trapdoor)
    create_block_model_simple(trapdoor)

# ============================================================
# GLASS (8 types)
# ============================================================
glass_types = [
    "ashenglass", "shroudedglass", "harlequinnglass", "bloodyglass",
    "infestedglass", "shadeglass", "sepiaglass", "moodyglass"
]
for glass in glass_types:
    create_block_item_model(glass)
    create_blockstate_simple(glass)
    # Glass uses transparent model
    create_block_model_simple(glass, parent="minecraft:block/glass")

# ============================================================
# GLASS PANES (8 types)
# ============================================================
glass_panes = [
    "ashenglasspane", "shroudedglasspane", "harlequinnglasspane", "bloodyglasspane",
    "infestedglasspane", "shadeglasspane", "sepiaglasspane", "moodyglasspane"
]
for pane in glass_panes:
    create_block_item_model(pane)
    create_blockstate_simple(pane)
    # Glass panes use special model
    create_block_model_simple(pane, parent="minecraft:block/template_glass_pane")

# ============================================================
# TROPHIES (2)
# ============================================================
trophies = ["trophyvoidorb", "trophyboomb"]
for trophy in trophies:
    create_block_item_model(trophy)
    create_blockstate_simple(trophy)
    create_block_model_simple(trophy)

# ============================================================
# FLORA (4)
# ============================================================
flora = ["parasitesapling", "assimilatedblossom", "thornshade", "gothshroom"]
for plant in flora:
    create_block_item_model(plant)
    create_blockstate_simple(plant)
    # Plants use cross or flower model
    create_block_model_simple(plant, parent="minecraft:block/cross")

# ============================================================
# LAMPS (3)
# ============================================================
lamps = ["noderedstonelamp", "nodelamp", "bloodyice"]
for lamp in lamps:
    create_block_item_model(lamp)
    create_blockstate_simple(lamp)
    create_block_model_simple(lamp)

# ============================================================
# SPAWN EGGS (~50)
# ============================================================
print("Generating spawn egg models...")
spawn_eggs = [
    # Infected
    "spawneigginfectedcreeper", "spawneigginfectedskeleton", "spawneigginfectedzombie",
    "spawneigginfectedspider", "spawneigginfectedenderman", "spawneigginfectedpig",
    "spawneigginfectedcow", "spawneigginfectedsheep", "spawneigginfectedchicken",
    "spawneigginfectedvillager", "spawneigginfectedwolf", "spawneigginfectedhorse",
    # Feral
    "spawneiggferalcreeper", "spawneiggferalskeleton", "spawneiggferalzombie",
    "spawneiggferalspider",
    # Hijacked
    "spawneigghijackedcreeper", "spawneigghijackedskeleton", "spawneigghijackedzombie",
    # Inborn
    "spawneigginborn1", "spawneigginborn2", "spawneigginborn3", "spawneigginborn4",
    # Crude
    "spawneiggcrude1", "spawneiggcrude2", "spawneiggcrude3",
    # Primitive
    "spawneiggprimitive1", "spawneiggprimitive2",
    # Adapted
    "spawneiggadapted1", "spawneiggadapted2", "spawneiggadapted3",
    # Nexus
    "spawneiggnexusbeckon", "spawneiggnexusdispatcher", "spawneiggnexusrooter", "spawneiggnexusother",
    # Deterrent
    "spawneiggdeterrent1", "spawneiggdeterrent2",
    # Pure
    "spawneiggpure1", "spawneiggpure2",
    # Preeminent
    "spawneiggpreeminent1", "spawneiggpreeminent2",
    # Ancient
    "spawneiggancient1", "spawneiggancient2",
    # Derived
    "spawneiggderived1", "spawneiggderived2",
    # Abomination
    "spawneiggabomination1", "spawneiggabomination2"
]
for egg in spawn_eggs:
    # Spawn eggs use a special template model
    egg_model = {
        "parent": "minecraft:item/template_spawn_egg",
        "textures": {
            "layer0": f"subspaceparasite:item/{egg}"
        }
    }
    with open(os.path.join(ITEM_MODELS_PATH, f"{egg}.json"), 'w') as f:
        json.dump(egg_model, f, indent=2)

# ============================================================
# DEBUG ITEMS (6)
# ============================================================
debug_items = ["itemevolve", "itemdevolve", "itemvariant", "itemassimilate", "itemthrow", "itemtab"]
for item in debug_items:
    create_item_model(item)

print(f"\n✅ Generated {len(os.listdir(ITEM_MODELS_PATH))} item model files")
print(f"✅ Generated {len(os.listdir(BLOCKSTATES_PATH))} blockstate files")
print(f"✅ Generated {len(os.listdir(BLOCK_MODELS_PATH))} block model files")
print("\nModel generation complete!")
