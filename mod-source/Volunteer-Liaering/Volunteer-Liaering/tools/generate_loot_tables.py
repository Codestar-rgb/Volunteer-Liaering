#!/usr/bin/env python3
"""
SRP 1.20.1 Block Loot Table Generator
Generates loot table JSON files for ALL blocks in the SubspaceParasite mod.

Rules:
  - Standard blocks: self-drop with survives_explosion condition
  - Slab blocks: conditional drop (2 if double, 1 otherwise)
  - Door blocks: self-drop (doors in 1.20.1 have their own loot tables)
  - deadblood: EXCLUDED (has .noLootTable() in registration)
"""

import os
import json

MOD_ID = "subspaceparasite"
BASE_PATH = "src/main/resources/data/subspaceparasite/loot_tables/blocks"

# ======================================================================
# Block definitions - extracted from ModBlocks.java
# ======================================================================

# Blocks that should NOT get a loot table
EXCLUDED_BLOCKS = {"deadblood"}

# Slab blocks (need conditional double-slab drop format)
SLAB_BLOCKS = [
    "infestedstonebrickslab",
    "residuebrickslab",
    "parasitecobblestoneslab",
    "polishedinfestedstoneslab",
    "infestedplankslab",
    "gothplankslab",
    "consumedplankslab",
    "brusewoodplankslab",
    "infestedsandstoneslab",
]

# Door blocks (self-drop, standard format - doors in 1.20.1 have their own loot tables)
DOOR_BLOCKS = [
    "gothdoor",
    "brusewooddoor",
    "consumeddoor",
]

# All other blocks (standard self-drop with survives_explosion)
STANDARD_BLOCKS = [
    # System blocks
    "biomeheart", "colonyheart", "colonyoutpost", "biomepurifier",
    "relaycontroller", "parasitebarrier", "noderelay", "relaybase",
    "relaymiddle", "relayroof",

    # Infested blocks
    "infectedstain", "infestremain", "infestedtrunk", "infectedrubble",
    "infectedbush", "infestedcobblestone", "infestedplanks",
    "infestedstonebricks", "infestedterracotta", "polishedinfestedstone",
    "residuebricks", "infestedcolumn", "infestedsandstone",
    "infestedsandstone2", "infestedsandstone3", "infestedsand",
    "infestedleaves", "infestedore", "infestedfence",

    # Parasite blocks
    "parasitetrunk", "parasiteplank", "parasitestain", "parasiteloot",
    "parasiterubble", "parasitestructure", "parasitethin",
    "parasitesapling", "parasitemouth", "parasiterubbledense",
    "parasitebush", "parasitecactus", "parasitecanister",
    "parasitecanisteractive", "parasitevine", "parasitefog",

    # Spreading blocks
    "harlequinngrass", "harleskinnblock", "polandskinblock",

    # Functional blocks
    "infestedfurnace", "evolutionlure", "buglin", "infestationpurifier",
    "fognullifier", "infuserfurnace", "biomassblock", "residueblock",
    "alveoli", "sickalveoli", "alveoligrowth", "solidalveoli",
    "dermoidcyst", "dod",

    # Glass blocks
    "ashenglass", "shroudedglass", "harlequinnglass", "bloodyglass",
    "infestedglass", "shadeglass", "sepiaglass", "moodyglass",

    # Glass panes
    "ashenglasspane", "shroudedglasspane", "harlequinnglasspane",
    "bloodyglasspane", "infestedglasspane", "shadeglasspane",
    "sepiaglasspane", "moodyglasspane",

    # Esca bulbs (default + 16 colors)
    "escabulb", "escabulbwhite", "escabulborange", "escabulbmagenta",
    "escabulblightblue", "escabulbyellow", "escabulblime", "escabulbpink",
    "escabulbgray", "escabulblightgray", "escabulbcyan", "escabulbpurple",
    "escabulbblue", "escabulbbrown", "escabulbgreen", "escabulbred",
    "escabulbblack",

    # Wood blocks
    "gothstem", "gothplanks", "fleshplanks", "cookedfleshplanks",
    "cookedflesh", "brusewoodplanks", "consumedplanks",

    # Trapdoors
    "brusewoodtrapdoor", "consumedtrapdoor", "gothtrapdoor",

    # Workbenches
    "consumedworkbench", "infestedworkbench",

    # Assimilated blocks
    "assimilatedpumpkin", "assimilatedjackolantern",
    "assimilatedsugarcane", "assimilatedblossom",

    # Potted blocks
    "infestedpot", "consumedpot", "pottedassimilatedblossom",
    "pottedconsumedassimilatedblossom",

    # Flora
    "thornshade", "diseasedsponge", "gothshroom",

    # Hair blocks
    "hairfollicle", "hirsutehair", "tresseshair", "lipopamass", "locs",

    # Gore blocks
    "goresim", "gorepri", "goreada", "gorepur", "gorefer", "goremar",

    # Trophies
    "trophyvoidorb", "trophyboomb",

    # Fluid blocks (excluding deadblood)
    "bloodyice",

    # Semiorganic block
    "semiorganiblock",

    # Walls
    "infestedstonebrickwall", "residuebrickwall",
    "parasitecobblestonewall", "polishedinfestedstonewall",
    "infestedsandstonewall", "gothstemwall",

    # Stairs
    "infestedstonebrickstairs", "residuebrickstairs",
    "parasitecobblestonestairs", "polishedinfestedstonestairs",
    "infestedplankstairs", "gothplankstairs", "consumedplankstairs",
    "brusewoodplankstairs", "infestedsandstonestairs",

    # Fences
    "gothfence", "consumedfence", "brusewoodfence", "fleshfence",

    # Web
    "srpweb",

    # Node lamps
    "noderedstonelamp", "nodelamp",
]


def write_json_file(filepath, data):
    """Write JSON data to file with consistent formatting."""
    os.makedirs(os.path.dirname(filepath), exist_ok=True)
    with open(filepath, 'w') as f:
        json.dump(data, f, indent=2)
    print(f"  Created: {filepath}")


def make_standard_loot_table(block_name):
    """Create a standard self-drop loot table with survives_explosion."""
    return {
        "type": "minecraft:block",
        "pools": [
            {
                "rolls": 1,
                "bonus_rolls": 0.0,
                "entries": [
                    {
                        "type": "minecraft:item",
                        "name": f"{MOD_ID}:{block_name}"
                    }
                ],
                "conditions": [
                    {
                        "condition": "minecraft:survives_explosion"
                    }
                ]
            }
        ]
    }


def make_slab_loot_table(block_name):
    """Create a slab loot table with conditional double-slab drop.

    Drops 2 slabs when the block is a double slab (type=double),
    drops 1 slab otherwise (single slab).
    """
    return {
        "type": "minecraft:block",
        "pools": [
            {
                "rolls": 1,
                "bonus_rolls": 0.0,
                "entries": [
                    {
                        "type": "minecraft:item",
                        "name": f"{MOD_ID}:{block_name}",
                        "functions": [
                            {
                                "function": "minecraft:set_count",
                                "count": {
                                    "type": "minecraft:fixed",
                                    "min": 2.0,
                                    "max": 2.0
                                },
                                "add": False
                            }
                        ],
                        "conditions": [
                            {
                                "condition": "minecraft:block_state_property",
                                "block": f"{MOD_ID}:{block_name}",
                                "properties": {
                                    "type": "double"
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }


def main():
    os.makedirs(BASE_PATH, exist_ok=True)

    total = 0

    # Generate standard self-drop loot tables
    print("Generating standard self-drop loot tables...")
    for block_name in STANDARD_BLOCKS:
        if block_name in EXCLUDED_BLOCKS:
            print(f"  SKIPPED (excluded): {block_name}")
            continue
        loot_table = make_standard_loot_table(block_name)
        write_json_file(f"{BASE_PATH}/{block_name}.json", loot_table)
        total += 1

    # Generate door loot tables (self-drop, same format as standard)
    print("\nGenerating door loot tables...")
    for block_name in DOOR_BLOCKS:
        loot_table = make_standard_loot_table(block_name)
        write_json_file(f"{BASE_PATH}/{block_name}.json", loot_table)
        total += 1

    # Generate slab loot tables (conditional drop)
    print("\nGenerating slab loot tables (conditional double-slab drop)...")
    for block_name in SLAB_BLOCKS:
        loot_table = make_slab_loot_table(block_name)
        write_json_file(f"{BASE_PATH}/{block_name}.json", loot_table)
        total += 1

    # Report excluded blocks
    print("\nExcluded blocks (no loot table):")
    for block_name in sorted(EXCLUDED_BLOCKS):
        print(f"  EXCLUDED: {block_name}")

    print(f"\nTotal loot tables generated: {total}")
    print(f"Output directory: {BASE_PATH}/")


if __name__ == "__main__":
    main()
