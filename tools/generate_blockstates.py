#!/usr/bin/env python3
"""
SRP 1.20.1 Blockstate Generator
Generates all blockstate JSON files for the SubspaceParasite mod.
"""

import os
import json

MOD_ID = "subspaceparasite"
BASE_PATH = "src/main/resources/assets/subspaceparasite/blockstates"

os.makedirs(BASE_PATH, exist_ok=True)

def write_json_file(filepath, data):
    """Write JSON data to file."""
    os.makedirs(os.path.dirname(filepath), exist_ok=True)
    with open(filepath, 'w') as f:
        json.dump(data, f, indent=2)
    print(f"  Created: {filepath}")

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

print("Generating blockstates for standard blocks...")
for block in standard_blocks:
    blockstate = {
        "variants": {
            "": {"model": f"{MOD_ID}:block/{block}"}
        }
    }
    write_json_file(f"{BASE_PATH}/{block}.json", blockstate)

# Glass blocks
glass_blocks = [
    "ashenglass", "shroudedglass", "harlequinnglass", "bloodyglass",
    "infestedglass", "shadeglass", "sepiaglass", "moodyglass"
]

print("Generating blockstates for glass blocks...")
for block in glass_blocks:
    blockstate = {
        "variants": {
            "": {"model": f"{MOD_ID}:block/{block}"}
        }
    }
    write_json_file(f"{BASE_PATH}/{block}.json", blockstate)

# Sandstone variants
sandstone_blocks = ["infestedsandstone", "infestedsandstone2", "infestedsandstone3"]

print("Generating blockstates for sandstone blocks...")
for block in sandstone_blocks:
    blockstate = {
        "variants": {
            "": {"model": f"{MOD_ID}:block/{block}"}
        }
    }
    write_json_file(f"{BASE_PATH}/{block}.json", blockstate)

# Sapling (cross)
print("Generating blockstate for parasitesapling...")
sapling_state = {
    "variants": {
        "": {"model": f"{MOD_ID}:block/parasitesapling"}
    }
}
write_json_file(f"{BASE_PATH}/parasitesapling.json", sapling_state)

# Trophies
print("Generating blockstates for trophies...")
for trophy in ["trophyvoidorb", "trophyboomorb"]:
    blockstate = {
        "variants": {
            "": {"model": f"{MOD_ID}:block/{trophy}"}
        }
    }
    write_json_file(f"{BASE_PATH}/{trophy}.json", blockstate)

# Stairs (multiple variants)
stair_bases = [
    "infestedstonebrick", "residuebrick", "parasitecobblestone",
    "polishedinfestedstone", "infestedplank", "gothplank",
    "consumedplank", "brusewoodplank", "infestedsandstone"
]

print("Generating blockstates for stairs...")
for base in stair_bases:
    blockstate = {
        "variants": {
            "facing=east,half=bottom,shape=inner_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 270},
            "facing=east,half=bottom,shape=inner_right": {"model": f"{MOD_ID}:block/{base}_stairs"},
            "facing=east,half=bottom,shape=outer_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 270},
            "facing=east,half=bottom,shape=outer_right": {"model": f"{MOD_ID}:block/{base}_stairs"},
            "facing=east,half=bottom,shape=straight": {"model": f"{MOD_ID}:block/{base}_stairs"},
            "facing=east,half=top,shape=inner_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180},
            "facing=east,half=top,shape=inner_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 90},
            "facing=east,half=top,shape=outer_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180},
            "facing=east,half=top,shape=outer_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 90},
            "facing=east,half=top,shape=straight": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180},
            "facing=north,half=bottom,shape=inner_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 180},
            "facing=north,half=bottom,shape=inner_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 270},
            "facing=north,half=bottom,shape=outer_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 180},
            "facing=north,half=bottom,shape=outer_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 270},
            "facing=north,half=bottom,shape=straight": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 270},
            "facing=north,half=top,shape=inner_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 270},
            "facing=north,half=top,shape=inner_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180},
            "facing=north,half=top,shape=outer_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 270},
            "facing=north,half=top,shape=outer_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180},
            "facing=north,half=top,shape=straight": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 270},
            "facing=south,half=bottom,shape=inner_left": {"model": f"{MOD_ID}:block/{base}_stairs"},
            "facing=south,half=bottom,shape=inner_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 90},
            "facing=south,half=bottom,shape=outer_left": {"model": f"{MOD_ID}:block/{base}_stairs"},
            "facing=south,half=bottom,shape=outer_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 90},
            "facing=south,half=bottom,shape=straight": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 90},
            "facing=south,half=top,shape=inner_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 90},
            "facing=south,half=top,shape=inner_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 180},
            "facing=south,half=top,shape=outer_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 90},
            "facing=south,half=top,shape=outer_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 180},
            "facing=south,half=top,shape=straight": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 90},
            "facing=west,half=bottom,shape=inner_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 90},
            "facing=west,half=bottom,shape=inner_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 180},
            "facing=west,half=bottom,shape=outer_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 90},
            "facing=west,half=bottom,shape=outer_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 180},
            "facing=west,half=bottom,shape=straight": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "y": 180},
            "facing=west,half=top,shape=inner_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 180},
            "facing=west,half=top,shape=inner_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 270},
            "facing=west,half=top,shape=outer_left": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 180},
            "facing=west,half=top,shape=outer_right": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 270},
            "facing=west,half=top,shape=straight": {"model": f"{MOD_ID}:block/{base}_stairs", "uvlock": True, "x": 180, "y": 180}
        }
    }
    write_json_file(f"{BASE_PATH}/{base}_stairs.json", blockstate)

# Slabs (multiple variants)
slab_bases = [
    "infestedstonebrick", "residuebrick", "parasitecobblestone",
    "polishedinfestedstone", "infestedplank", "gothplank",
    "consumedplank", "brusewoodplank", "infestedsandstone"
]

print("Generating blockstates for slabs...")
for base in slab_bases:
    blockstate = {
        "variants": {
            "type=bottom": {"model": f"{MOD_ID}:block/{base}_slab"},
            "type=double": {"model": f"{MOD_ID}:block/{base}"},
            "type=top": {"model": f"{MOD_ID}:block/{base}_slab", "x": 180}
        }
    }
    write_json_file(f"{BASE_PATH}/{base}_slab.json", blockstate)

# Walls (multiple variants)
wall_bases = [
    "infestedstonebrick", "residuebrick", "parasitecobblestone",
    "polishedinfestedstone", "infestedsandstone", "gothstem"
]

print("Generating blockstates for walls...")
for base in wall_bases:
    blockstate = {
        "multipart": [
            {"apply": {"model": f"{MOD_ID}:block/{base}_wall", "uvlock": True}, "when": {"up": "true"}},
            {"apply": {"model": f"{MOD_ID}:block/{base}_wall", "uvlock": True, "y": 90}, "when": {"north": "low"}},
            {"apply": {"model": f"{MOD_ID}:block/{base}_wall", "uvlock": True, "y": 90}, "when": {"east": "low"}},
            {"apply": {"model": f"{MOD_ID}:block/{base}_wall", "uvlock": True, "y": 180}, "when": {"south": "low"}},
            {"apply": {"model": f"{MOD_ID}:block/{base}_wall", "uvlock": True, "y": 270}, "when": {"west": "low"}},
            {"apply": {"model": f"{MOD_ID}:block/{base}_wall_tall", "uvlock": True}, "when": {"up": "false", "north": "tall"}},
            {"apply": {"model": f"{MOD_ID}:block/{base}_wall_tall", "uvlock": True, "y": 90}, "when": {"up": "false", "east": "tall"}},
            {"apply": {"model": f"{MOD_ID}:block/{base}_wall_tall", "uvlock": True, "y": 180}, "when": {"up": "false", "south": "tall"}},
            {"apply": {"model": f"{MOD_ID}:block/{base}_wall_tall", "uvlock": True, "y": 270}, "when": {"up": "false", "west": "tall"}}
        ]
    }
    # Simple variant for inventory
    blockstate["variants"] = {"": {"model": f"{MOD_ID}:block/{base}_wall"}}
    write_json_file(f"{BASE_PATH}/{base}_wall.json", blockstate)

# Doors
door_bases = ["goth", "brusewood", "consumed"]

print("Generating blockstates for doors...")
for base in door_bases:
    blockstate = {
        "variants": {
            "facing=east,half=lower,hinge=left,open=false": {"model": f"{MOD_ID}:block/{base}_door_bottom"},
            "facing=east,half=lower,hinge=left,open=true": {"model": f"{MOD_ID}:block/{base}_door_bottom", "y": 90},
            "facing=east,half=lower,hinge=right,open=false": {"model": f"{MOD_ID}:block/{base}_door_bottom"},
            "facing=east,half=lower,hinge=right,open=true": {"model": f"{MOD_ID}:block/{base}_door_bottom", "y": 270},
            "facing=east,half=upper,hinge=left,open=false": {"model": f"{MOD_ID}:block/{base}_door_top"},
            "facing=east,half=upper,hinge=left,open=true": {"model": f"{MOD_ID}:block/{base}_door_top", "y": 90},
            "facing=east,half=upper,hinge=right,open=false": {"model": f"{MOD_ID}:block/{base}_door_top"},
            "facing=east,half=upper,hinge=right,open=true": {"model": f"{MOD_ID}:block/{base}_door_top", "y": 270},
            "facing=north,half=lower,hinge=left,open=false": {"model": f"{MOD_ID}:block/{base}_door_bottom", "y": 270},
            "facing=north,half=lower,hinge=left,open=true": {"model": f"{MOD_ID}:block/{base}_door_bottom"},
            "facing=north,half=lower,hinge=right,open=false": {"model": f"{MOD_ID}:block/{base}_door_bottom", "y": 270},
            "facing=north,half=lower,hinge=right,open=true": {"model": f"{MOD_ID}:block/{base}_door_bottom", "y": 180},
            "facing=north,half=upper,hinge=left,open=false": {"model": f"{MOD_ID}:block/{base}_door_top", "y": 270},
            "facing=north,half=upper,hinge=left,open=true": {"model": f"{MOD_ID}:block/{base}_door_top"},
            "facing=north,half=upper,hinge=right,open=false": {"model": f"{MOD_ID}:block/{base}_door_top", "y": 270},
            "facing=north,half=upper,hinge=right,open=true": {"model": f"{MOD_ID}:block/{base}_door_top", "y": 180},
            "facing=south,half=lower,hinge=left,open=false": {"model": f"{MOD_ID}:block/{base}_door_bottom", "y": 90},
            "facing=south,half=lower,hinge=left,open=true": {"model": f"{MOD_ID}:block/{base}_door_bottom", "y": 180},
            "facing=south,half=lower,hinge=right,open=false": {"model": f"{MOD_ID}:block/{base}_door_bottom", "y": 90},
            "facing=south,half=lower,hinge=right,open=true": {"model": f"{MOD_ID}:block/{base}_door_bottom"},
            "facing=south,half=upper,hinge=left,open=false": {"model": f"{MOD_ID}:block/{base}_door_top", "y": 90},
            "facing=south,half=upper,hinge=left,open=true": {"model": f"{MOD_ID}:block/{base}_door_top", "y": 180},
            "facing=south,half=upper,hinge=right,open=false": {"model": f"{MOD_ID}:block/{base}_door_top", "y": 90},
            "facing=south,half=upper,hinge=right,open=true": {"model": f"{MOD_ID}:block/{base}_door_top"},
            "facing=west,half=lower,hinge=left,open=false": {"model": f"{MOD_ID}:block/{base}_door_bottom", "y": 180},
            "facing=west,half=lower,hinge=left,open=true": {"model": f"{MOD_ID}:block/{base}_door_bottom", "y": 270},
            "facing=west,half=lower,hinge=right,open=false": {"model": f"{MOD_ID}:block/{base}_door_bottom", "y": 180},
            "facing=west,half=lower,hinge=right,open=true": {"model": f"{MOD_ID}:block/{base}_door_bottom", "y": 90},
            "facing=west,half=upper,hinge=left,open=false": {"model": f"{MOD_ID}:block/{base}_door_top", "y": 180},
            "facing=west,half=upper,hinge=left,open=true": {"model": f"{MOD_ID}:block/{base}_door_top", "y": 270},
            "facing=west,half=upper,hinge=right,open=false": {"model": f"{MOD_ID}:block/{base}_door_top", "y": 180},
            "facing=west,half=upper,hinge=right,open=true": {"model": f"{MOD_ID}:block/{base}_door_top", "y": 90}
        }
    }
    write_json_file(f"{BASE_PATH}/{base}_door.json", blockstate)

# Trapdoors
trapdoor_bases = ["goth", "brusewood", "consumed"]

print("Generating blockstates for trapdoors...")
for base in trapdoor_bases:
    blockstate = {
        "variants": {
            "facing=east,half=bottom,open=false": {"model": f"{MOD_ID}:block/{base}_trapdoor_bottom"},
            "facing=east,half=bottom,open=true": {"model": f"{MOD_ID}:block/{base}_trapdoor_open"},
            "facing=east,half=top,open=false": {"model": f"{MOD_ID}:block/{base}_trapdoor_top"},
            "facing=east,half=top,open=true": {"model": f"{MOD_ID}:block/{base}_trapdoor_open", "x": 180, "y": 180},
            "facing=north,half=bottom,open=false": {"model": f"{MOD_ID}:block/{base}_trapdoor_bottom", "y": 270},
            "facing=north,half=bottom,open=true": {"model": f"{MOD_ID}:block/{base}_trapdoor_open", "y": 270},
            "facing=north,half=top,open=false": {"model": f"{MOD_ID}:block/{base}_trapdoor_top", "y": 270},
            "facing=north,half=top,open=true": {"model": f"{MOD_ID}:block/{base}_trapdoor_open", "x": 180, "y": 270},
            "facing=south,half=bottom,open=false": {"model": f"{MOD_ID}:block/{base}_trapdoor_bottom", "y": 90},
            "facing=south,half=bottom,open=true": {"model": f"{MOD_ID}:block/{base}_trapdoor_open", "y": 90},
            "facing=south,half=top,open=false": {"model": f"{MOD_ID}:block/{base}_trapdoor_top", "y": 90},
            "facing=south,half=top,open=true": {"model": f"{MOD_ID}:block/{base}_trapdoor_open", "x": 180, "y": 90},
            "facing=west,half=bottom,open=false": {"model": f"{MOD_ID}:block/{base}_trapdoor_bottom", "y": 180},
            "facing=west,half=bottom,open=true": {"model": f"{MOD_ID}:block/{base}_trapdoor_open", "y": 180},
            "facing=west,half=top,open=false": {"model": f"{MOD_ID}:block/{base}_trapdoor_top", "y": 180},
            "facing=west,half=top,open=true": {"model": f"{MOD_ID}:block/{base}_trapdoor_open", "x": 180}
        }
    }
    write_json_file(f"{BASE_PATH}/{base}_trapdoor.json", blockstate)

# Fences
fence_bases = ["goth", "consumed", "brusewood", "flesh"]

print("Generating blockstates for fences...")
for base in fence_bases:
    blockstate = {
        "multipart": [
            {"apply": {"model": f"{MOD_ID}:block/{base}_fence", "uvlock": True}, "when": {"north": "true"}},
            {"apply": {"model": f"{MOD_ID}:block/{base}_fence", "uvlock": True, "y": 90}, "when": {"east": "true"}},
            {"apply": {"model": f"{MOD_ID}:block/{base}_fence", "uvlock": True, "y": 180}, "when": {"south": "true"}},
            {"apply": {"model": f"{MOD_ID}:block/{base}_fence", "uvlock": True, "y": 270}, "when": {"west": "true"}}
        ],
        "variants": {
            "": {"model": f"{MOD_ID}:block/{base}_fence"}
        }
    }
    write_json_file(f"{BASE_PATH}/{base}_fence.json", blockstate)

# Workbenches
print("Generating blockstates for workbenches...")
for workbench in ["consumedworkbench", "infestedworkbench"]:
    blockstate = {
        "variants": {
            "": {"model": f"{MOD_ID}:block/{workbench}"}
        }
    }
    write_json_file(f"{BASE_PATH}/{workbench}.json", blockstate)

# Pots
print("Generating blockstate for consumedpot...")
pot_state = {
    "variants": {
        "": {"model": f"{MOD_ID}:block/consumedpot"}
    }
}
write_json_file(f"{BASE_PATH}/consumedpot.json", pot_state)

# Potted plants
print("Generating blockstate for pottedconsumedassimilatedblossom...")
potted_state = {
    "variants": {
        "": {"model": f"{MOD_ID}:block/pottedconsumedassimilatedblossom"}
    }
}
write_json_file(f"{BASE_PATH}/pottedconsumedassimilatedblossom.json", potted_state)

print("\n✅ Blockstate generation complete!")
print(f"Total blockstates created: {len(os.listdir(BASE_PATH))}")
