# SRP 1.20.1 Port - Development Roadmap & Architecture Guide

## Project Vision
A high-quality, performant, and maintainable port of Scape and Run: Parasites from Minecraft 1.12.2 to 1.20.1 Forge, with enhanced architecture for future development.

---

## Architecture Principles

### 1. **Component-Based Entity Design**
- `EntityParasiteBase` delegates behavior to components:
  - `CombatComponent`: Damage handling, attack logic, damage caps
  - `EvolutionComponent`: Phase progression, evolution points, scaling
  - `InfectionComponent`: Infection spread, resistance, COTH mechanics
  - `ColonyComponent`: Hierarchy, leadership, colony spawning

### 2. **Interface-Driven API**
- `IParasite`: Core parasite behaviors
- `IEvolvable`: Evolution mechanics
- `IInfectable`: Infection system
- `IHitboxedEntity`: Multi-part hitbox support
- `ICanAbility`: Capability flags (fly, swim, climb, etc.)

### 3. **Data-Driven Configuration**
- All stats loaded from JSON configs
- Gene system for procedural variation
- Biome tags for spawn control

### 4. **Network Efficiency**
- Minimal sync via `SynchedEntityData`
- Packet batching for colony updates
- Client-side prediction where applicable

---

## Development Phases

### Phase 1: Core Infrastructure ✅ (IN PROGRESS)
- [x] Basic mod setup (build.gradle, mods.toml)
- [x] Registry infrastructure (ModBlocks, ModItems, ModEntities)
- [x] Entity base class with components
- [x] API interfaces
- [x] Network channel setup
- [ ] Capability system registration
- [ ] Config system implementation
- [ ] Save data structures (ModWorldData, ModSaveData)

### Phase 2: Entity Implementation Priority
#### Tier 1: Essential Entities (First Playable)
1. **Infected Humans** - Basic melee enemy
2. **Primitive Bano** - First unique parasite (already implemented)
3. **Moving Flesh** - Basic resource unit
4. **Worker** - Colony builder
5. **Carrier (Light/Heavy)** - Spawning units

#### Tier 2: Colony Mechanics
6. **Host/Host II** - Colony structures
7. **Rupter** - Defense unit
8. **Gnat/Lice** - Swarm units
9. **Mangler** - Heavy attacker

#### Tier 3: Advanced Forms
10. **Feral variants** - Transformed hosts
11. **Hijacked variants** - Special abilities
12. **Inborn variants** - Pure parasites
13. **Crude forms** - Early colony
14. **Adapted forms** - Late game
15. **Caller/Kirin/Bosses** - End game

### Phase 3: Systems Implementation
- [ ] Evolution phase system (ZERO → FOUR)
- [ ] Global kill counter tracking
- [ ] Celestial manager (blood moon events)
- [ ] Biome corruption system
- [ ] Block infestation mechanics
- [ ] COTH (Call of the Hive) spread
- [ ] Dislodgment effect system
- [ ] Gene expression system

### Phase 4: Content Completion
- [ ] All 140+ entities implemented
- [ ] All blocks (infested variants, colony blocks)
- [ ] All items (tools, weapons, materials)
- [ ] All fluids (bio mass, etc.)
- [ ] All effects (infection, buffs, debuffs)
- [ ] All particles
- [ ] All sounds

### Phase 5: Polish & Optimization
- [ ] Model/animation integration (when provided)
- [ ] Texture optimization
- [ ] Performance profiling
- [ ] Memory optimization
- [ ] Network optimization
- [ ] Config balancing
- [ ] Compatibility testing

---

## Technical Standards

### Code Quality
1. **Null Safety**: Use `@Nullable` and `Objects.requireNonNull()`
2. **Logging**: Use `SubspaceParasite.LOGGER` at appropriate levels
3. **Documentation**: Javadoc for all public APIs
4. **Testing**: Unit tests for core logic, in-game testing for entities

### Performance Guidelines
1. **Entity Ticks**: Minimize allocations in tick methods
2. **Pathfinding**: Cache paths, use custom goals
3. **Rendering**: Batch draw calls, LOD for distant entities
4. **Network**: Sync only essential data, use compression

### File Organization
```
src/main/java/com/subspaceparasite/
├── api/                    # Public API interfaces
│   └── parasite/           # Enum types (ParasiteType, EvoPhase, etc.)
├── client/                 # Client-side rendering, overlays
│   ├── model/              # Entity models (GeckoLib when added)
│   ├── renderer/           # Entity renderers
│   └── particle/           # Custom particles
├── common/                 # Common game logic
│   ├── block/              # Blocks and block entities
│   ├── entity/             # Entity classes
│   │   ├── base/           # Base classes and components
│   │   ├── ai/             # AI goals
│   │   └── monster/        # Specific entity implementations
│   ├── item/               # Items
│   ├── world/              # World gen, biomes, structures
│   └── network/            # Packet definitions
├── config/                 # Config system
├── core/                   # Registry objects
├── handler/                # Event handlers
└── util/                   # Utilities
```

---

## Future Enhancement Opportunities

### 1. GeckoLib Integration
- Smooth animations for complex entities
- Model hierarchy for multi-part entities
- Animation state machines

### 2. Data Pack Support
- Custom parasite types via JSON
- Configurable evolution paths
- Biome tag customization

### 3. Compatibilities
- JEI integration for recipes
- TOP/WAILA for entity info
- Mob spawning tweaks (YUNG's, etc.)

### 4. Performance Features
- Entity culling for large colonies
- Async pathfinding for groups
- Instanced rendering for identical entities

---

## Immediate Next Steps

1. **Complete Config System**: Implement Forge config for all tunables
2. **Implement Tier 1 Entities**: Get basic gameplay loop working
3. **Add Save Data**: Persist world evolution state
4. **Networking**: Sync infection and evolution data
5. **Models/Textures**: Integrate when provided

---

## Key Design Decisions

### Why Component Pattern?
- Separation of concerns
- Easier testing and debugging
- Flexible composition over inheritance
- Clear boundaries for future contributors

### Why DeferredRegister?
- Forge standard for 1.20.1
- Automatic registry handling
- Clean lifecycle management

### Why Interface-Based API?
- Clear contracts for implementors
- Easier to mock for testing
- Allows multiple inheritance of type

---

## Contributing Guidelines

1. **Branch Naming**: `feature/entity-name`, `fix/issue-description`
2. **Commit Messages**: Conventional commits (`feat:`, `fix:`, `refactor:`)
3. **Code Style**: Follow existing formatting, run formatter before PR
4. **Testing**: Test in-game before submitting, note any edge cases

---

## References

- Original SRP Source: https://github.com/Codestar-rgb/Qom-Inseac
- Current Port: https://github.com/Codestar-rgb/Volunteer-Liaering
- Forge Documentation: https://mcforge.readthedocs.io/
- MCP Mappings: Parchment for 1.20.1

---

*Last Updated: Development in Progress*
*Version Target: Minecraft 1.20.1 Forge 47.3.0*
