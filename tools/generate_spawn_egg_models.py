import os
import json

# All spawn egg item names from ModItems.java
spawn_eggs = [
    # Infected
    "spawneigginfectedcreeper", "spawneigginfectedskeleton", "spawneigginfectedzombie",
    "spawneigginfectedspider", "spawneigginfectedenderman", "spawneigginfectedpig",
    "spawneigginfectedcow", "spawneigginfectedsheep", "spawneigginfectedchicken",
    "spawneigginfectedvillager", "spawneigginfectedwolf", "spawneigginfectedhorse",
    "spawneigginfectedirongolem", "spawneigginfectedsnowgolem", "spawneigginfectedbat",
    "spawneigginfectedblaze", "spawneigginfectedwitch", "spawneigginfectedravager",
    "spawneigginfectedpillager", "spawneigginfectedevoker", "spawneigginfectedghast",
    "spawneigginfectedphantom", "spawneigginfectedwarden", "spawneigginfectedwitherskeleton",
    "spawneigginfectedstray", "spawneigginfectedhusk", "spawneigginfecteddrowned",
    "spawneigginfectedcavespider", "spawneigginfectedmooshroom", "spawneigginfectedllama",
    "spawneigginfectedpolarbear", "spawneigginfectedpanda", "spawneigginfectedfox",
    "spawneigginfectedbee", "spawneigginfectedhuman",
    # Feral
    "spawneiggferalcreeper", "spawneiggferalskeleton", "spawneiggferalzombie",
    "spawneiggferalspider", "spawneiggferalenderman", "spawneiggferalwolf",
    "spawneiggferalirongolem",
    # Hijacked
    "spawneigghijackedcreeper", "spawneigghijackedskeleton", "spawneigghijackedzombie",
    "spawneigghijackedspider", "spawneigghijackedenderman", "spawneigghijackedwitch",
    "spawneigghijackedpillager", "spawneigghijackedevoker", "spawneigghijackedravager",
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
    "spawneiggabomination1", "spawneiggabomination2",
]

output_dir = "src/main/resources/assets/subspaceparasite/models/item"
os.makedirs(output_dir, exist_ok=True)

for egg_name in spawn_eggs:
    model_data = {
        "parent": "minecraft:item/template_spawn_egg"
    }
    
    output_path = os.path.join(output_dir, f"{egg_name}.json")
    with open(output_path, 'w') as f:
        json.dump(model_data, f, indent=2)
    
print(f"Generated {len(spawn_eggs)} spawn egg model files")
