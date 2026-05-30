#!/usr/bin/env python3
"""Generate complete sounds.json for SRP mod based on ModSounds.java"""

import json
import re

# Read ModSounds.java to extract all sound registrations
with open('/workspace/src/main/java/com/subspaceparasite/core/ModSounds.java', 'r') as f:
    content = f.read()

# Find all sound registrations using regex
pattern = r'register\("([^"]+)"\)'
matches = re.findall(pattern, content)

# Build sounds.json structure
sounds_list = []
for sound_name in sorted(set(matches)):
    # Convert to subsrp prefix format
    sounds_list.append({
        "name": f"subsrp:{sound_name}",
        "stream": False
    })

# Create the final JSON structure
sounds_json = {"sounds": sounds_list}

# Write to file
with open('/workspace/src/main/resources/assets/subspaceparasite/sounds.json', 'w') as f:
    json.dump(sounds_json, f, indent=2)

print(f"Generated sounds.json with {len(sounds_list)} sound entries")
print("First 10 entries:")
for s in sounds_list[:10]:
    print(f"  - {s['name']}")
