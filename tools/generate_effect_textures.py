#!/usr/bin/env python3
"""Generate placeholder effect texture files for all 36 SRP effects."""
import os

# Effect definitions with colors
effects = {
    # Infection Effects
    "coth": (0x8B4513, "Brown"),
    "infection_ii": (0x6B1A1A, "Dark Red"),
    "infection_iii": (0x8B2626, "Red"),
    "virulence": (0x9B3020, "Firebrick"),
    "coagulation": (0x7A0000, "Blood Red"),
    
    # Evolution Effects
    "evolution": (0xFFD700, "Gold"),
    "adaptation": (0x32CD32, "Lime Green"),
    "sentience": (0x4169E1, "Royal Blue"),
    "derivation": (0x9370DB, "Medium Purple"),
    
    # Corruption Effects
    "corruption": (0x4B0082, "Indigo"),
    "decay": (0x556B2F, "Dark Olive"),
    "decomposition": (0x3B5323, "Forest Green"),
    "putrefaction": (0x2F4F2F, "Dark Slate"),
    
    # Parasite Buffs
    "parasite_vitality": (0xFF6347, "Tomato"),
    "parasite_resistance": (0xDEB887, "Burlywood"),
    "parasite_strength": (0xDC143C, "Crimson"),
    "parasite_speed": (0x00BFFF, "Deep Sky Blue"),
    "parasite_regeneration": (0x00FA9A, "Medium Spring Green"),
    
    # Debuff Effects
    "fear": (0x363636, "Dark Gray"),
    "slowness_parasite": (0x5A5A8A, "Slate Blue"),
    "weakness_parasite": (0x484878, "Dim Gray"),
    "wither_parasite": (0x2E2E2E, "Very Dark Gray"),
    
    # Special Effects
    "gestation": (0xE6B800, "Goldenrod"),
    "incubation": (0xCCA300, "Dark Goldenrod"),
    "assimilation": (0x8B0000, "Dark Red"),
    "mutagenic": (0x9932CC, "Dark Orchid"),
    "corrosion": (0x5A7D5A, "Gray Green"),
    "viral": (0x228B22, "Forest Green"),
    "spore": (0x9ACD32, "Yellow Green"),
    "bleed": (0x8B0000, "Dark Red"),
    "novision": (0x1A1A1A, "Black"),
    "vomit": (0x6B8E23, "Olive Drab"),
    
    # Resistance/Purge
    "purge": (0xF0E68C, "Khaki"),
    "immunity": (0xFFFAF0, "Floral White"),
    "cleansing": (0xB0E0E6, "Powder Blue"),
    
    # Nexus Effects
    "nexus_link": (0x4169E1, "Royal Blue"),
    "nexus_command": (0x1E90FF, "Dodger Blue"),
    
    # Misc Effects
    "parasite_hunger": (0x8B4513, "Saddle Brown"),
    "leech": (0xA0522D, "Sienna"),
}

output_dir = "/workspace/src/main/resources/assets/subspaceparasite/textures/mob_effect"
os.makedirs(output_dir, exist_ok=True)

def create_minimal_png(filename, color_rgb):
    """Create a minimal 16x16 PNG with the specified color."""
    import struct
    import zlib
    
    r = (color_rgb >> 16) & 0xFF
    g = (color_rgb >> 8) & 0xFF
    b = color_rgb & 0xFF
    
    width, height = 16, 16
    
    def png_chunk(chunk_type, data):
        chunk_len = struct.pack(">I", len(data))
        chunk_crc = struct.pack(">I", zlib.crc32(chunk_type + data) & 0xffffffff)
        return chunk_len + chunk_type + data + chunk_crc
    
    signature = b'\x89PNG\r\n\x1a\n'
    
    ihdr_data = struct.pack(">IIBBBBB", width, height, 8, 2, 0, 0, 0)
    ihdr = png_chunk(b'IHDR', ihdr_data)
    
    raw_data = b''
    for y in range(height):
        raw_data += b'\x00'
        for x in range(width):
            raw_data += bytes([r, g, b])
    
    compressed = zlib.compress(raw_data, 9)
    idat = png_chunk(b'IDAT', compressed)
    
    iend = png_chunk(b'IEND', b'')
    
    with open(os.path.join(output_dir, filename), 'wb') as f:
        f.write(signature + ihdr + idat + iend)

for effect_name, (color, desc) in effects.items():
    filename = f"{effect_name}.png"
    create_minimal_png(filename, color)
    print(f"Created: {filename} ({desc})")

print(f"\nGenerated {len(effects)} effect textures in {output_dir}")
