# SRP 1.20.1 Port - Implementation Plan

## Current Status
- MCMOS.zip extracted with all GeckoLib models and textures
- Basic entity framework in place (EntityParasiteBase)
- sounds.json defined but missing .ogg files and complete entries
- Model files present but not properly integrated with textures

## Tasks to Complete

### 1. Sound System Completion
- Generate complete sounds.json with all SRP sounds
- Create placeholder sound generation script (since .ogg files need extraction from original SRP)
- Map all ModSounds entries to sounds.json

### 2. Model & Texture Integration
- Copy MCMOS textures to proper locations in assets
- Update geo.json files to reference correct texture paths
- Ensure renderer binding is correct

### 3. Heblu Entity Implementation
- Create EntityHeblu class with full SRP mechanics
- Implement ranged attack, cloaking, adaptation behaviors
- Integrate GeckoLib animations
- Add sound event calls

### 4. Kirin Entity Implementation  
- Create EntityKirin class
- Enhance ParasiteKirinBlinkGoal with full mechanics
- Implement health steal, teleportation, charging
- Integrate animations and sounds

### 5. Orb Entity Implementation
- Create EntityVoidOrb class
- Implement projectile mechanics
- Add explosion/damage effects
- Particle effects integration

### 6. Global Mechanisms
- Infection system completion
- Evolution system
- Colony behavior
- Adaptation mechanics

## File Structure Target
```
assets/subspaceparasite/
├── sounds.json (complete)
├── sounds/
│   └── subsrp_/*.ogg (placeholders)
├── gecko_models/
│   └── derived/
│       ├── heblu/
│       │   ├── textures/
│       │   └── *.json
│       └── kirin/
│           ├── textures/
│           └── *.json
└── textures/
    └── entity/
        └── derived/
```
