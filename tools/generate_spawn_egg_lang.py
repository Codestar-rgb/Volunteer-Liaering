import os
import json

# All spawn egg translations
spawn_eggs_lang = {
    # Infected
    "item.subspaceparasite.spawneigginfectedcreeper": "Infected Creeper Spawn Egg",
    "item.subspaceparasite.spawneigginfectedskeleton": "Infected Skeleton Spawn Egg",
    "item.subspaceparasite.spawneigginfectedzombie": "Infected Zombie Spawn Egg",
    "item.subspaceparasite.spawneigginfectedspider": "Infected Spider Spawn Egg",
    "item.subspaceparasite.spawneigginfectedenderman": "Infected Enderman Spawn Egg",
    "item.subspaceparasite.spawneigginfectedpig": "Infected Pig Spawn Egg",
    "item.subspaceparasite.spawneigginfectedcow": "Infected Cow Spawn Egg",
    "item.subspaceparasite.spawneigginfectedsheep": "Infected Sheep Spawn Egg",
    "item.subspaceparasite.spawneigginfectedchicken": "Infected Chicken Spawn Egg",
    "item.subspaceparasite.spawneigginfectedvillager": "Infected Villager Spawn Egg",
    "item.subspaceparasite.spawneigginfectedwolf": "Infected Wolf Spawn Egg",
    "item.subspaceparasite.spawneigginfectedhorse": "Infected Horse Spawn Egg",
    "item.subspaceparasite.spawneigginfectedirongolem": "Infected Iron Golem Spawn Egg",
    "item.subspaceparasite.spawneigginfectedsnowgolem": "Infected Snow Golem Spawn Egg",
    "item.subspaceparasite.spawneigginfectedbat": "Infected Bat Spawn Egg",
    "item.subspaceparasite.spawneigginfectedblaze": "Infected Blaze Spawn Egg",
    "item.subspaceparasite.spawneigginfectedwitch": "Infected Witch Spawn Egg",
    "item.subspaceparasite.spawneigginfectedravager": "Infected Ravager Spawn Egg",
    "item.subspaceparasite.spawneigginfectedpillager": "Infected Pillager Spawn Egg",
    "item.subspaceparasite.spawneigginfectedevoker": "Infected Evoker Spawn Egg",
    "item.subspaceparasite.spawneigginfectedghast": "Infected Ghast Spawn Egg",
    "item.subspaceparasite.spawneigginfectedphantom": "Infected Phantom Spawn Egg",
    "item.subspaceparasite.spawneigginfectedwarden": "Infected Warden Spawn Egg",
    "item.subspaceparasite.spawneigginfectedwitherskeleton": "Infected Wither Skeleton Spawn Egg",
    "item.subspaceparasite.spawneigginfectedstray": "Infected Stray Spawn Egg",
    "item.subspaceparasite.spawneigginfectedhusk": "Infected Husk Spawn Egg",
    "item.subspaceparasite.spawneigginfecteddrowned": "Infected Drowned Spawn Egg",
    "item.subspaceparasite.spawneigginfectedcavespider": "Infected Cave Spider Spawn Egg",
    "item.subspaceparasite.spawneigginfectedmooshroom": "Infected Mooshroom Spawn Egg",
    "item.subspaceparasite.spawneigginfectedllama": "Infected Llama Spawn Egg",
    "item.subspaceparasite.spawneigginfectedpolarbear": "Infected Polar Bear Spawn Egg",
    "item.subspaceparasite.spawneigginfectedpanda": "Infected Panda Spawn Egg",
    "item.subspaceparasite.spawneigginfectedfox": "Infected Fox Spawn Egg",
    "item.subspaceparasite.spawneigginfectedbee": "Infected Bee Spawn Egg",
    "item.subspaceparasite.spawneigginfectedhuman": "Infected Human Spawn Egg",
    # Feral
    "item.subspaceparasite.spawneiggferalcreeper": "Feral Creeper Spawn Egg",
    "item.subspaceparasite.spawneiggferalskeleton": "Feral Skeleton Spawn Egg",
    "item.subspaceparasite.spawneiggferalzombie": "Feral Zombie Spawn Egg",
    "item.subspaceparasite.spawneiggferalspider": "Feral Spider Spawn Egg",
    "item.subspaceparasite.spawneiggferalenderman": "Feral Enderman Spawn Egg",
    "item.subspaceparasite.spawneiggferalwolf": "Feral Wolf Spawn Egg",
    "item.subspaceparasite.spawneiggferalirongolem": "Feral Iron Golem Spawn Egg",
    # Hijacked
    "item.subspaceparasite.spawneigghijackedcreeper": "Hijacked Creeper Spawn Egg",
    "item.subspaceparasite.spawneigghijackedskeleton": "Hijacked Skeleton Spawn Egg",
    "item.subspaceparasite.spawneigghijackedzombie": "Hijacked Zombie Spawn Egg",
    "item.subspaceparasite.spawneigghijackedspider": "Hijacked Spider Spawn Egg",
    "item.subspaceparasite.spawneigghijackedenderman": "Hijacked Enderman Spawn Egg",
    "item.subspaceparasite.spawneigghijackedwitch": "Hijacked Witch Spawn Egg",
    "item.subspaceparasite.spawneigghijackedpillager": "Hijacked Pillager Spawn Egg",
    "item.subspaceparasite.spawneigghijackedevoker": "Hijacked Evoker Spawn Egg",
    "item.subspaceparasite.spawneigghijackedravager": "Hijacked Ravager Spawn Egg",
    # Inborn
    "item.subspaceparasite.spawneigginborn1": "Inborn Alafin Spawn Egg",
    "item.subspaceparasite.spawneigginborn2": "Inborn Obus Spawn Egg",
    "item.subspaceparasite.spawneigginborn3": "Inborn Normas Spawn Egg",
    "item.subspaceparasite.spawneigginborn4": "Inborn Canal Spawn Egg",
    # Crude
    "item.subspaceparasite.spawneiggcrude1": "Crude Scorcher Spawn Egg",
    "item.subspaceparasite.spawneiggcrude2": "Crude Mindim Spawn Egg",
    "item.subspaceparasite.spawneiggcrude3": "Crude Egas Spawn Egg",
    # Primitive
    "item.subspaceparasite.spawneiggprimitive1": "Primitive Bano Spawn Egg",
    "item.subspaceparasite.spawneiggprimitive2": "Primitive Bomph Spawn Egg",
    # Adapted
    "item.subspaceparasite.spawneiggadapted1": "Adapted Colony Spawn Egg",
    "item.subspaceparasite.spawneiggadapted2": "Adapted Creeper Spawn Egg",
    "item.subspaceparasite.spawneiggadapted3": "Adapted Skeleton Spawn Egg",
    # Nexus
    "item.subspaceparasite.spawneiggnexusbeckon": "Nexus Beckon Spawn Egg",
    "item.subspaceparasite.spawneiggnexusdispatcher": "Nexus Dispatcher Spawn Egg",
    "item.subspaceparasite.spawneiggnexusrooter": "Nexus Rooter Spawn Egg",
    "item.subspaceparasite.spawneiggnexusother": "Nexus Guard Spawn Egg",
    # Deterrent
    "item.subspaceparasite.spawneiggdeterrent1": "Deterrent Sentry Spawn Egg",
    "item.subspaceparasite.spawneiggdeterrent2": "Deterrent Outpost Spawn Egg",
    # Pure
    "item.subspaceparasite.spawneiggpure1": "Pure Creeper Spawn Egg",
    "item.subspaceparasite.spawneiggpure2": "Pure Skeleton Spawn Egg",
    # Preeminent
    "item.subspaceparasite.spawneiggpreeminent1": "Preeminent Marauder Spawn Egg",
    "item.subspaceparasite.spawneiggpreeminent2": "Preeminent Warden Spawn Egg",
    # Ancient
    "item.subspaceparasite.spawneiggancient1": "Ancient Dreadnought Spawn Egg",
    "item.subspaceparasite.spawneiggancient2": "Ancient Leviathan Spawn Egg",
    # Derived
    "item.subspaceparasite.spawneiggderived1": "Derived Heblu Spawn Egg",
    "item.subspaceparasite.spawneiggderived2": "Derived Kirin Spawn Egg",
    # Abomination
    "item.subspaceparasite.spawneiggabomination1": "Abomination Amalgam Spawn Egg",
    "item.subspaceparasite.spawneiggabomination2": "Abomination Chimera Spawn Egg",
}

output_dir = "src/main/resources/assets/subspaceparasite/lang"
os.makedirs(output_dir, exist_ok=True)

# Read existing lang file and merge
lang_path = os.path.join(output_dir, "en_us.json")
existing_lang = {}
if os.path.exists(lang_path):
    with open(lang_path, 'r') as f:
        existing_lang = json.load(f)

# Merge spawn egg translations
existing_lang.update(spawn_eggs_lang)

with open(lang_path, 'w') as f:
    json.dump(existing_lang, f, indent=2, ensure_ascii=False)

print(f"Added {len(spawn_eggs_lang)} spawn egg translations to en_us.json")
