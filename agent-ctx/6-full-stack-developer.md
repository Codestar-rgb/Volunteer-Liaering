# Task 6 - Improve block/item/effect systems

## Agent: full-stack-developer

## Task: Improve common/block, common/item, and common/effect systems with SRP-specific behavior

## Work Completed

### Effects (9 files enhanced)

1. **EffectCOTH.java** — Major rewrite with full IInfectable integration:
   - Infection rate scaling per amplifier (0.002/0.005/0.008/0.012+)
   - IInfectable.addInfection() calls each tick cycle
   - The Sign protection awareness
   - Phase-aware COTH spread rate
   - Visual debuffs (Slowness amp 1+, Nausea amp 3+, Weakness amp 5+)
   - Aura range scaling (2/3/4+ blocks per amplifier)
   - Force-infection at critically low HP
   - COTH popping now also converts nearby blocks
   - Static utility methods: getInfectionRate(), getAuraRange()

2. **EffectBleed.java** — Stackable damage with flat+percentage:
   - Flat damage + percentage damage
   - POISON_HEALING gene awareness
   - Resistance interaction

3. **EffectCorrosion.java** — Armor reduction on all entities:
   - ARMOR attribute modifier (UUID-based)
   - Parasite immunity
   - Hand item durability damage for players

4. **EffectFear.java** — Player effects and targeting prevention:
   - Player debuffs (Slowness, Mining Fatigue, Weakness)
   - Mobs clear parasite targets when feared
   - Flee speed cap

5. **EffectFoster.java** — Gene synergy:
   - REGEN_RATE gene multiplier
   - Heal cap (5.0/tick)
   - Regeneration bonus at amp 3+

6. **EffectPrey.java** — Damage vulnerability:
   - getDamageIncrease() for event handlers
   - Spotted effect on distant prey

7. **EffectSpotted.java** — Damage vulnerability:
   - getDamageVulnerability() for event handlers
   - Mob.setAggressive() for LOOK_WALL targeting

8. **EffectTheSign.java** — Active COTH curing:
   - COTH duration reduction at amp 6+
   - Minor Regeneration at amp 3+
   - curesCOTH() utility

9. **EffectVirulence.java** — Phase awareness:
   - Spread multiplier cap (5.0)
   - Phase COTH spread integration
   - The Sign protection check

### Blocks (5 files enhanced)

1. **BlockParasiteBase.java** — COTH aura + phase-aware spreading
2. **BlockSpreadingBase.java** — Phase-aware multi-spread + BlockPurifyMappings
3. **BlockEntityParasiteBase.java** — Colony management + COTH aura + evolution points
4. **BlockFluidBase.java** — Infection on touch + parasite healing
5. **BlockPurifyMappings.java** — State preservation + StateConverter interface

### Items (5 files enhanced)

1. **ItemParasiteBase.java** — Glow + custom rarity
2. **ItemSpawnerBase.java** — Phase check + COTH burst on spawn
3. **ItemWeaponMeleeBase.java** — COTH on hit + sentient evolution + Bleed
4. **ItemWeaponRangedBase.java** — COTH projectiles + applyProjectileEffects()
5. **ItemModuleBase.java** — Evolution point application + compatibility checks

## Key Design Decisions

- All effects are **phase-aware**: COTH spread rate scales with evolution phase
- All effects interact with **The Sign** protection system
- Blocks have **COTH aura** that applies infection to nearby entities
- Block spreading uses **BlockPurifyMappings** for bidirectional conversion
- Block entities contribute **evolution points** to drive phase advancement
- Melee weapons apply **COTH directly on hit** with IInfectable integration
- Ranged weapons use **NBT-tagged projectiles** for deferred COTH application
- Module items have **two use modes**: targeted (on parasite) and global (in air)
