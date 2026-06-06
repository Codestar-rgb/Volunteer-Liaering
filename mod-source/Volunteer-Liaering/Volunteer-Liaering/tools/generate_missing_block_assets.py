#!/usr/bin/env python3
"""
Generate missing block model, blockstate, and item model JSON files
for the SubspaceParasite mod.

Generates:
  - 1 missing blockstate:  polishedinfestedstonestairs
  - 4 missing item models: infectedstain, infectedrubble, infectedbush,
                            polishedinfestedstonestairs
  - 61 missing block models for cube_all, glass pane, wall, stair,
    slab, door, trapdoor, and fence block types.
"""

import json
import os

# ── Paths ────────────────────────────────────────────────────────────────
PROJECT = "/home/z/Volunteer-Liaering"
ASSETS  = f"{PROJECT}/src/main/resources/assets/subspaceparasite"
BLOCKSTATES  = f"{ASSETS}/blockstates"
BLOCK_MODELS = f"{ASSETS}/models/block"
ITEM_MODELS  = f"{ASSETS}/models/item"

MOD = "subspaceparasite"

# ── Helpers ──────────────────────────────────────────────────────────────
generated = {"blockstate": 0, "block_model": 0, "item_model": 0, "skipped": 0}

def write_json(path, data):
    """Write JSON only if the file does not already exist."""
    os.makedirs(os.path.dirname(path), exist_ok=True)
    if os.path.exists(path):
        generated["skipped"] += 1
        return False
    with open(path, "w") as f:
        json.dump(data, f, indent=2)
        f.write("\n")
    return True

def bs(name):
    """Blockstate path."""
    return f"{BLOCKSTATES}/{name}.json"

def bm(name):
    """Block model path."""
    return f"{BLOCK_MODELS}/{name}.json"

def im(name):
    """Item model path."""
    return f"{ITEM_MODELS}/{name}.json"


# ══════════════════════════════════════════════════════════════════════════
# 1.  MISSING BLOCKSTATE  –  polishedinfestedstonestairs
# ══════════════════════════════════════════════════════════════════════════
print("=== Generating missing blockstates ===")

def make_stairs_blockstate(registry_name, model_prefix):
    """Create a full stair blockstate (facing × half × shape = 32 variants)."""
    facing_y = {"east": 0, "south": 90, "west": 180, "north": 270}
    variants = {}
    for facing, base_y in facing_y.items():
        for half in ("bottom", "top"):
            for shape in ("straight", "inner_left", "inner_right",
                          "outer_left", "outer_right"):
                # Determine model and rotations
                if shape == "straight":
                    model_name = f"{model_prefix}"
                    x = 180 if half == "top" else 0
                    y = base_y if half == "bottom" else (base_y + 180) % 360
                elif shape == "inner_left":
                    model_name = f"{model_prefix}_inner"
                    if half == "bottom":
                        x, y = 0, (base_y + 270) % 360
                    else:
                        x, y = 180, (base_y + 270) % 360
                elif shape == "inner_right":
                    model_name = f"{model_prefix}"
                    if half == "bottom":
                        x, y = 0, base_y
                    else:
                        x, y = 180, (base_y + 90) % 360
                elif shape == "outer_left":
                    model_name = f"{model_prefix}_outer"
                    if half == "bottom":
                        x, y = 0, (base_y + 270) % 360
                    else:
                        x, y = 180, (base_y + 270) % 360
                elif shape == "outer_right":
                    model_name = f"{model_prefix}_outer"
                    if half == "bottom":
                        x, y = 0, base_y
                    else:
                        x, y = 180, (base_y + 90) % 360

                entry = {"model": f"{MOD}:block/{model_name}", "uvlock": True}
                if x:
                    entry["x"] = x
                if y:
                    entry["y"] = y
                variants[f"facing={facing},half={half},shape={shape}"] = entry
    return {"variants": variants}

stair_bs = make_stairs_blockstate(
    "polishedinfestedstonestairs",
    "polishedinfestedstone_stairs"
)
if write_json(bs("polishedinfestedstonestairs"), stair_bs):
    generated["blockstate"] += 1
    print("  + polishedinfestedstonestairs.json")


# ══════════════════════════════════════════════════════════════════════════
# 2.  MISSING ITEM MODELS  (4)
# ══════════════════════════════════════════════════════════════════════════
print("\n=== Generating missing item models ===")

# Simple block-item models (parent points to block model)
simple_item_blocks = ["infectedstain", "infectedrubble", "infectedbush"]
for name in simple_item_blocks:
    data = {
        "parent": f"{MOD}:block/{name}",
    }
    if write_json(im(name), data):
        generated["item_model"] += 1
        print(f"  + {name}.json (item)")

# Stairs item model
stairs_item_data = {
    "parent": f"{MOD}:block/polishedinfestedstone_stairs"
}
if write_json(im("polishedinfestedstonestairs"), stairs_item_data):
    generated["item_model"] += 1
    print("  + polishedinfestedstonestairs.json (item)")


# ══════════════════════════════════════════════════════════════════════════
# 3.  MISSING BLOCK MODELS  (61)
# ══════════════════════════════════════════════════════════════════════════
print("\n=== Generating missing block models ===")

# ── 3a. cube_all blocks ──────────────────────────────────────────────────
cube_all_blocks = [
    # Gore blocks
    "goresim", "gorepri", "goreada", "gorepur", "gorefer", "goremar",
    # Other cube_all blocks
    "cookedflesh", "diseasedsponge",
    "hairfollicle", "hirsutehair", "tresseshair", "lipopamass", "locs",
    # Infected simple blocks (registered as infectedstain, infectedrubble, infectedbush)
    "infectedstain", "infectedrubble", "infectedbush",
]

for name in cube_all_blocks:
    data = {
        "parent": "minecraft:block/cube_all",
        "textures": {
            "all": f"{MOD}:block/{name}"
        }
    }
    if write_json(bm(name), data):
        generated["block_model"] += 1
        print(f"  + {name}.json  (cube_all)")


# ── 3b. Glass panes (5 models per pane type) ────────────────────────────
# Each pane needs: _post, _side, _noside, _side_alt, _noside_alt
# Also: _post_ends variant for some
glass_pane_types = [
    # (registry_name, pane_texture, edge_texture)
    ("ashenglasspane",       "ashen_glass",       "ashen_glass_pane"),
    ("shroudedglasspane",    "shrouded_glass",    "shrouded_glass_pane"),
    ("harlequinnglasspane",  "harlequinn_glass",  "harlequinn_glass_pane"),
    ("bloodyglasspane",      "bloody_glass",      "bloody_glass_pane"),
    ("infestedglasspane",    "infested_glass",    "infested_glass_pane"),
    ("shadeglasspane",       "shade_glass",       "shade_glass_pane"),
    ("sepiaglasspane",       "sepia_glass",       "sepia_glass_pane"),
    ("moodyglasspane",       "moody_glass",       "moody_glass_pane"),
]

pane_variants = [
    # (suffix, parent)
    ("_post",       "minecraft:block/template_glass_pane_post"),
    ("_side",       "minecraft:block/template_glass_pane_side"),
    ("_noside",     "minecraft:block/template_glass_pane_noside"),
    ("_side_alt",   "minecraft:block/template_glass_pane_side_alt"),
    ("_noside_alt", "minecraft:block/template_glass_pane_noside_alt"),
]

for reg_name, pane_tex, edge_tex in glass_pane_types:
    for suffix, parent in pane_variants:
        model_name = f"{reg_name}{suffix}"
        data = {
            "parent": parent,
            "textures": {
                "pane": f"{MOD}:block/{pane_tex}",
                "edge": f"{MOD}:block/{edge_tex}"
            }
        }
        if write_json(bm(model_name), data):
            generated["block_model"] += 1
            print(f"  + {model_name}.json  (glass pane)")


# ── 3c. Walls (3 block models + 1 item model per wall) ──────────────────
wall_types = [
    # (registry_name, texture_name, model_prefix)
    ("infestedstonebrickwall",      "infested_stone_bricks",      "infested_stone_brick_wall"),
    ("residuebrickwall",            "residue_bricks",             "residue_brick_wall"),
    ("parasitecobblestonewall",     "infested_cobblestone",       "parasite_cobblestone_wall"),
    ("polishedinfestedstonewall",   "infested_stone_polished",    "polished_infested_stone_wall"),
    ("infestedsandstonewall",       "infested_sandstone",         "infested_sandstone_wall"),
    ("gothstemwall",                "goth_stem",                  "gothstem_wall"),
]

wall_block_variants = [
    # (suffix, parent)
    ("_post",      "minecraft:block/template_wall_post"),
    ("_side",      "minecraft:block/template_wall_side"),
    ("_side_tall", "minecraft:block/template_wall_side_tall"),
]

for reg_name, tex_name, model_prefix in wall_types:
    # Block models
    for suffix, parent in wall_block_variants:
        model_name = f"{model_prefix}{suffix}"
        data = {
            "parent": parent,
            "textures": {
                "wall": f"{MOD}:block/{tex_name}"
            }
        }
        if write_json(bm(model_name), data):
            generated["block_model"] += 1
            print(f"  + {model_name}.json  (wall)")

    # Item model (wall_inventory)
    inv_data = {
        "parent": "minecraft:block/wall_inventory",
        "textures": {
            "wall": f"{MOD}:block/{tex_name}"
        }
    }
    if write_json(im(reg_name), inv_data):
        generated["item_model"] += 1
        print(f"  + {reg_name}.json  (wall item)")


# ── 3d. Stairs (3 block models per stair type) ──────────────────────────
stair_types = [
    # (registry_name, texture_name, model_prefix)
    ("infestedstonebrickstairs",     "infested_stone_bricks",      "infestedstonebrick_stairs"),
    ("residuebrickstairs",           "residue_bricks",             "residuebrick_stairs"),
    ("parasitecobblestonestairs",    "infested_cobblestone",       "parasitecobblestone_stairs"),
    ("polishedinfestedstonestairs",  "infested_stone_polished",    "polishedinfestedstone_stairs"),
    ("infestedplankstairs",          "infested_planks",            "infestedplank_stairs"),
    ("gothplankstairs",              "goth_planks",                "gothplank_stairs"),
    ("consumedplankstairs",          "consumed_planks",            "consumedplank_stairs"),
    ("brusewoodplankstairs",         "brusewood_planks",           "brusewoodplank_stairs"),
    ("infestedsandstonestairs",      "infested_sandstone",         "infestedsandstone_stairs"),
]

stair_block_variants = [
    # (suffix, parent)
    ("",          "minecraft:block/stairs"),
    ("_inner",    "minecraft:block/inner_stairs"),
    ("_outer",    "minecraft:block/outer_stairs"),
]

for reg_name, tex_name, model_prefix in stair_types:
    for suffix, parent in stair_block_variants:
        model_name = f"{model_prefix}{suffix}"
        data = {
            "parent": parent,
            "textures": {
                "bottom": f"{MOD}:block/{tex_name}",
                "top": f"{MOD}:block/{tex_name}",
                "side": f"{MOD}:block/{tex_name}"
            }
        }
        if write_json(bm(model_name), data):
            generated["block_model"] += 1
            print(f"  + {model_name}.json  (stairs)")


# ── 3e. Slabs (2 block models per slab type) ────────────────────────────
slab_types = [
    # (registry_name, texture_name, model_prefix, full_block_model)
    ("infestedstonebrickslab",     "infested_stone_bricks",      "infestedstonebrick_slab",     "infestedstonebricks"),
    ("residuebrickslab",           "residue_bricks",             "residuebrick_slab",           "residuebricks"),
    ("parasitecobblestoneslab",    "infested_cobblestone",       "parasitecobblestone_slab",    "infestedcobblestone"),
    ("polishedinfestedstoneslab",  "infested_stone_polished",    "polishedinfestedstone_slab",  "polishedinfestedstone"),
    ("infestedplankslab",          "infested_planks",            "infestedplank_slab",          "infestedplanks"),
    ("gothplankslab",              "goth_planks",                "gothplank_slab",              "gothplanks"),
    ("consumedplankslab",          "consumed_planks",            "consumedplank_slab",          "consumedplanks"),
    ("brusewoodplankslab",         "brusewood_planks",           "brusewoodplank_slab",         "brusewoodplanks"),
    ("infestedsandstoneslab",      "infested_sandstone",         "infestedsandstone_slab",      "infestedsandstone"),
]

slab_block_variants = [
    # (suffix, parent)
    ("",      "minecraft:block/slab"),
    ("_top",  "minecraft:block/slab_top"),
]

for reg_name, tex_name, model_prefix, _full in slab_types:
    for suffix, parent in slab_block_variants:
        model_name = f"{model_prefix}{suffix}"
        data = {
            "parent": parent,
            "textures": {
                "bottom": f"{MOD}:block/{tex_name}",
                "top": f"{MOD}:block/{tex_name}",
                "side": f"{MOD}:block/{tex_name}"
            }
        }
        if write_json(bm(model_name), data):
            generated["block_model"] += 1
            print(f"  + {model_name}.json  (slab)")


# ── 3f. Doors (8 block models per door + item model) ────────────────────
door_types = [
    # (registry_name, texture_bottom, texture_top, model_prefix)
    ("gothdoor",       "goth_door_bottom",       "goth_door_top",       "goth_door"),
    ("brusewooddoor",  "brusewood_door_bottom",  "brusewood_door_top",  "brusewood_door"),
    ("consumeddoor",   "consumed_door_bottom",   "consumed_door_top",   "consumed_door"),
]

door_block_variants = [
    # (suffix, parent, texture_key)
    ("_bottom_left",       "minecraft:block/door_bottom_left",       "bottom"),
    ("_bottom_left_open",  "minecraft:block/door_bottom_left_open",  "bottom"),
    ("_bottom_right",      "minecraft:block/door_bottom_right",      "bottom"),
    ("_bottom_right_open", "minecraft:block/door_bottom_right_open", "bottom"),
    ("_top_left",          "minecraft:block/door_top_left",          "top"),
    ("_top_left_open",     "minecraft:block/door_top_left_open",     "top"),
    ("_top_right",         "minecraft:block/door_top_right",         "top"),
    ("_top_right_open",    "minecraft:block/door_top_right_open",    "top"),
]

for reg_name, tex_bottom, tex_top, model_prefix in door_types:
    for suffix, parent, tex_key in door_block_variants:
        model_name = f"{model_prefix}{suffix}"
        tex_val = tex_bottom if tex_key == "bottom" else tex_top
        data = {
            "parent": parent,
            "textures": {
                tex_key: f"{MOD}:block/{tex_val}"
            }
        }
        if write_json(bm(model_name), data):
            generated["block_model"] += 1
            print(f"  + {model_name}.json  (door)")

    # Door item model
    item_data = {
        "parent": "minecraft:item/generated",
        "textures": {
            "layer0": f"{MOD}:item/{reg_name}"
        }
    }
    if write_json(im(reg_name), item_data):
        generated["item_model"] += 1
        print(f"  + {reg_name}.json  (door item)")


# ── 3g. Trapdoors (3 block models per trapdoor + item model) ────────────
trapdoor_types = [
    # (registry_name, texture_name, model_prefix)
    ("brusewoodtrapdoor",  "brusewood_trapdoor",  "brusewood_trapdoor"),
    ("consumedtrapdoor",   "consumed_trapdoor",    "consumed_trapdoor"),
    ("gothtrapdoor",       "goth_trapdoor",        "goth_trapdoor"),
]

trapdoor_block_variants = [
    # (suffix, parent)
    ("_bottom", "minecraft:block/template_trapdoor_bottom"),
    ("_top",    "minecraft:block/template_trapdoor_top"),
    ("_open",   "minecraft:block/template_trapdoor_open"),
]

for reg_name, tex_name, model_prefix in trapdoor_types:
    for suffix, parent in trapdoor_block_variants:
        model_name = f"{model_prefix}{suffix}"
        data = {
            "parent": parent,
            "textures": {
                "texture": f"{MOD}:block/{tex_name}"
            }
        }
        if write_json(bm(model_name), data):
            generated["block_model"] += 1
            print(f"  + {model_name}.json  (trapdoor)")

    # Trapdoor item model
    item_data = {
        "parent": f"{MOD}:block/{model_prefix}_bottom"
    }
    if write_json(im(reg_name), item_data):
        generated["item_model"] += 1
        print(f"  + {reg_name}.json  (trapdoor item)")


# ── 3h. Fences (2 block models per fence + 1 item model) ────────────────
fence_types = [
    # (registry_name, texture_name, model_prefix)
    ("gothfence",       "goth_planks",        "goth_fence"),
    ("consumedfence",   "consumed_planks",    "consumed_fence"),
    ("brusewoodfence",  "brusewood_planks",   "brusewood_fence"),
    ("fleshfence",      "flesh_planks",       "flesh_fence"),
    ("infestedfence",   "infested_planks",    "infested_fence"),
]

fence_block_variants = [
    # (suffix, parent)
    ("_post", "minecraft:block/fence_post"),
    ("_side", "minecraft:block/fence_side"),
]

for reg_name, tex_name, model_prefix in fence_types:
    for suffix, parent in fence_block_variants:
        model_name = f"{model_prefix}{suffix}"
        data = {
            "parent": parent,
            "textures": {
                "texture": f"{MOD}:block/{tex_name}"
            }
        }
        if write_json(bm(model_name), data):
            generated["block_model"] += 1
            print(f"  + {model_name}.json  (fence)")

    # Fence item model (fence_inventory)
    item_data = {
        "parent": "minecraft:block/fence_inventory",
        "textures": {
            "texture": f"{MOD}:block/{tex_name}"
        }
    }
    if write_json(im(reg_name), item_data):
        generated["item_model"] += 1
        print(f"  + {reg_name}.json  (fence item)")


# ══════════════════════════════════════════════════════════════════════════
# SUMMARY
# ══════════════════════════════════════════════════════════════════════════
print("\n" + "=" * 60)
print("GENERATION SUMMARY")
print("=" * 60)
print(f"  Blockstates generated : {generated['blockstate']}")
print(f"  Block models generated: {generated['block_model']}")
print(f"  Item models generated : {generated['item_model']}")
print(f"  Files skipped (exist) : {generated['skipped']}")
print(f"  Total new files       : {generated['blockstate'] + generated['block_model'] + generated['item_model']}")
print("=" * 60)
