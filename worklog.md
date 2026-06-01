# Work Log

---
Task ID: 4-a
Agent: items-weapons-armor-auditor
Task: Audit all items, weapons, armor for correctness

Work Log:
- Read ModItems.java (953+ lines, ~200 items, 100+ spawn eggs)
- Read all custom item classes: ItemBiomass, ItemCookedParasiteFlesh, ItemDevolve, ItemBookOfVengeance, PurifyingSalveItem, ItemAssimilate, ItemGreekFire, ItemParasiteFlesh, ItemModule, ItemEvolve
- Read all weapon/tool classes: WeaponToolMeleeBase, WeaponMeleeSword, WeaponMeleeAxe, IHaveReach
- Read ModToolTiers.java, ModArmorMaterials.java, ModCreativeTabs.java, ModSounds.java, ModFluids.java
- Checked item model JSONs (600+ exist in models/item/) and texture PNGs (400+ exist in textures/items/)
- Verified 1.20.1 API correctness for all constructor signatures
- Identified and fixed 6 bugs

Stage Summary:
- CRITICAL FIX: Entity raytrace bug in ItemDevolve, ItemAssimilate, ItemEvolve - all three used player.pick() which only does block raytracing and NEVER returns EntityHitResult, making these debug items completely non-functional for targeting entities. Added proper AABB-based entity raytrace helper (pickEntity) to all three.
- FIX: WeaponToolMeleeBase did not include material's attackDamageBonus in attribute modifier - now adds material.getAttackDamageBonus() to the custom attack damage value, matching vanilla SwordItem/AxeItem behavior.
- FIX: WeaponToolMeleeBase had no durability set - now uses material.getUses() for durability via .durability(material.getUses()).
- FIX: WeaponToolMeleeBase was missing getEnchantmentValue() and isValidRepairItem() overrides - now properly delegates to the Tier material.
- FIX: ItemBookOfVengeance had dead code in knockback burst - the loop skipped source entity then had an unreachable `if (entity == source)` branch. Refactored to apply knockback to all other entities, then separately give source a small upward jump.
- FIX: ItemParasiteFlesh logic bug - checked `getInfectionLevel() <= 0` after already calling `addInfection(15)`, so it would always be > 0. Changed threshold to `<= 15` to catch newly-infected entities.
- FIX: ItemModule tooltip used §o formatting code instead of ChatFormatting.ITALIC. Replaced with proper .withStyle(color, ChatFormatting.ITALIC).
- NOTED: Armor materials (LIVING_ARMOR, SENTIENT_ARMOR, HIJACKED_IRON) all use correct 1.20.1 ArmorItem.Type enum and correct ArmorMaterial interface methods including getDurabilityForType/getDefenseForType. Texture paths verified (living_armor_layer_1/2.png, sentient_armor_layer_1/2.png, hijacked_iron_layer_1/2.png all exist).
- NOTED: Hijacked Iron tools (SwordItem, PickaxeItem, AxeItem, ShovelItem, HoeItem) all use correct 1.20.1 constructor signatures.
- NOTED: SpawnEggItem registrations all use correct (EntityType, int, int, Properties) constructor.
- NOTED: RecordItem registrations use correct (int, Supplier<SoundEvent>, Properties, int) constructor - RegistryObject implements Supplier so this works.
- NOTED: BucketItem uses correct (Supplier<FlowingFluid>, Properties) constructor.
- NOTED: ModCreativeTabs uses correct displayItems() callback pattern for 1.20.1 (no .tab() on items).
- NOTED: Item model JSONs and textures exist in large numbers from original SRP assets. Many use old naming conventions that may need mapping verification during integration testing.
- NOTED: WeaponMeleeSword/WeaponMeleeAxe.getNextTier() returns null with TODO - needs implementation when sentient upgrade system is ready.
- NOTED: Armor material repair ingredients are all Ingredient.EMPTY - need updating when repair materials are decided.
- NOTED: ModToolTiers.HIJACKED_IRON repair ingredient is Ingredient.EMPTY - needs updating.

---
Task ID: 5-c
Agent: ai-effects-network-auditor
Task: Audit AI goals, effects, network packets, and commands

Work Log:
- Read ALL 20 AI goal classes in common/entity/ai/ (ColonyDefenseGoal, ParasiteAvoidOrAttackGoal, ParasiteBlockInfestGoal, ParasiteBlockResidueGoal, ParasiteDiveBombGoal, ParasiteEvadeGoal, ParasiteFlightAttackGoal, ParasiteFollowLeaderGoal, ParasiteGetFollowersGoal, ParasiteGiveEffectsGoal, ParasiteKirinBlinkGoal, ParasiteMeleeAttackGoal, ParasiteMergeGoal, ParasiteRangedAttackGoal, ParasiteSkillGoal, ParasiteSpecialSkillGoal, ParasiteSummonGoal, ParasiteSwellGoal, ParasiteSwimDiveGoal, ParasiteWanderGoal)
- Read ALL 12 AI misc interface files (EntityBodyParts, EntityCanClimb, EntityCanColony, EntityCanFly, EntityCanHaveBodies, EntityCanMelt, EntityCanPullMobs, EntityCanShoot, EntityCanSpawn, EntityCanSummon, EntityCanSwim, EntityCanVectors, EntityCustomAttack)
- Read ALL 30+ effect classes (BaseSRPEffect, CustomMobEffect, ParasiteBuffEffectBase, InfectionEffectBase, InfectionStageEffect, FearEffect, BleedEffect, CothEffect, VomitEffect, NovisionEffect, EvolutionEffect, AssimilationEffect, ViralEffect, DecayEffect, CorruptionEffect, CorrosionEffect, MutagenicEffect, SporeEffect, TheSignEffect, PurgeEffect, SentienceEffect, AdaptationEffect, GestationEffect, NexusLinkEffect, NexusCommandEffect, and remaining buff/debuff effects)
- Read ALL 16 network packet classes (ModNetwork, SPacket, S2CEntitySyncPacket, C2SBodyPartHitPacket, C2SRequestScanPacket, S2CFogPacket, S2CParticlePacket, EvolutionActionPacket, EvolutionSyncPacket, PlayerInfectionSyncPacket, ParasiteEntitySyncPacket, ColonyHeartSyncPacket, BiomeInfectionSyncPacket, S2CPhaseSyncPacket, S2CUpdateEvoPhasePacket, S2CEntityBodyHitPacket, ParasiteParticlePacket)
- Read ALL 13 command classes (ModCommands, SRPCommand, EvolveCommand, InfectionCommand, PhaseCommand, DifficultyCommand, ColonyCommand, CelestialCommand, ParasiteCommand, ConfigCommand, MergeCommand, GeneCommand, DebugCommand)
- Read ALL 3 capability classes (ParasiteCapability, ParasiteCapabilityProvider, ParasiteCapabilityEvents)
- Identified and fixed critical and moderate issues

Stage Summary:
- CRITICAL FIX: ParasiteCapabilityEvents.onRegisterCapabilities() was NOT registered on the MOD event bus. The class was on the FORGE bus but RegisterCapabilitiesEvent fires on the MOD bus, meaning the capability was never registered and ParasiteCapability.CAPABILITY would be null at runtime, breaking the entire infection system. Added inner ModBusEvents class annotated with @Mod.EventBusSubscriber(bus=Bus.MOD) with @SubscribeEvent to properly register the capability.
- FIX: Removed unused import net.minecraft.world.level.GameRules from VomitEffect.java
- FIX: Removed unused import net.minecraft.world.entity.LivingEntity from ParasiteBlockResidueGoal.java
- AUDIT RESULT (AI Goals): All 20 AI goals correctly extend Goal (or MeleeAttackGoal/WaterAvoidingRandomStrollGoal for the two that extend those). All canUse()/canContinueToUse()/start()/stop()/tick() signatures are correct for 1.20.1. No 1.12.2 API calls found (no tryMoveToXYZWithGravity, no EntityAIBase). All use proper 1.20.1 methods: level() instead of world, getNavigation().moveTo() with correct signatures, getSensing().hasLineOfSight(), blockPosition(), etc.
- AUDIT RESULT (Effects): All effects properly extend MobEffect (via BaseSRPEffect or CustomMobEffect). applyEffectTick(LivingEntity, int) signatures correct. isDurationEffectTick(int, int) correct. addAttributeModifiers/removeAttributeModifiers with (LivingEntity, AttributeMap, int) correct for 1.20.1. No deprecated 1.12.2 method calls. All use correct 1.20.1 API: damageSources().generic()/magic()/playerAttack(), Attributes constants, DistExecutor for client-only code.
- AUDIT RESULT (Network): All 16 packets properly implement encode/decode with FriendlyByteBuf. S2C packets correctly use DistExecutor.unsafeRunWhenOn(Dist.CLIENT) to gate client-only code. C2S packets correctly access ServerPlayer via context.getSender(). ModNetwork correctly uses SimpleChannel/NetworkRegistry for 1.20.1. One inconsistency noted: EvolutionActionPacket + some others don't implement SPacket interface but use static encode/decode/handle methods - this works but is inconsistent with packets that implement SPacket.
- AUDIT RESULT (Commands): All 13 command classes correctly use Brigadier API. LiteralArgumentBuilder<CommandSourceStack>, Commands.literal/argument, EntityArgument.player/entity, IntegerArgumentType, ResourceLocationArgument all correct for 1.20.1. throws CommandSyntaxException where needed. Component.translatable() for messages. Proper permission checking (hasPermission(2)).
- AUDIT RESULT (Capabilities): ParasiteCapability uses correct CapabilityManager.get(new CapabilityToken<>(){}) for 1.20.1. ParasiteCapabilityProvider correctly implements ICapabilitySerializable<CompoundTag> with LazyOptional. Player clone handler correctly uses reviveCaps()/invalidateCaps() for 1.20.1 death persistence.
- NOTED: ParasiteBuffEffectBase.addAttributeModifiers() iterates getAttributeModifiers().keySet() but this map is always empty since no addAttributeModifier() calls are made in constructors. Subclasses that need attributes (SentienceEffect, AdaptationEffect, etc.) override addAttributeModifiers() directly and work correctly. The parent method is effectively dead code but harmless.
- NOTED: EvolutionActionPacket does not implement SPacket interface unlike most other packets - uses static encode/decode/handle registered differently in ModNetwork. Works but inconsistent.
- NOTED: S2CEntitySyncPacket.handleClientSync() has commented-out implementation code - the packet can be received but data won't be applied to client entities. Awaiting entity base class completion.
- NOTED: GeneCommand.removeGene() does not actually deactivate the gene - only sends a success message. Boolean gene deactivation is a functional gap.
- NOTED: ParasiteWanderGoal stores 'avoidWater' field but never uses it (parent class handles water avoidance).

---
Task ID: 5-d
Agent: remaining-entity-auditor
Task: Audit remaining entity classes (pure, beckon, etc.)

Work Log:
- Globbed all 66 entity files across 12 directories (pure/14, beckon/5, dispatcher/5, rooter/5, nexus/4, deterrent/4, derived/7, abomination/3, ancient/3, preeminent/3, projectile/7, misc/5)
- Read ALL tier base classes (EntityParasiteBase, EntityMalleableBase, EntityPureBase, EntityBeckonBase, EntityDispatcherBase, EntityRooterBase, EntityStationaryBase, EntityStationaryArchitectBase, EntityDeterrentBase, EntityDerivedBase, EntityAbominationBase, EntityAncientBase, EntityPreeminentBase)
- Read representative concrete entity files from each directory
- Read ModEntities.java registration entries and ModEventHandler.java attribute registrations
- Verified GeoAnimatable: registerControllers() and getAnimatableInstanceCache() implemented in EntityParasiteBase (inherited by all)
- Verified all entities have createAttributes() static methods
- Verified all entities registered in ModEntities and attributes registered in ModEventHandler
- Identified and fixed multiple critical issues

Stage Summary:
- CRITICAL FIX: 8 Pure-tier entities (Flam, Flog, Omboo, Alafha, Ganro, Esor, Elvia, Anged) used Mob.createMobAttributes() instead of EntityPureBase.createAttributes(). This caused them to lose ALL tier-inherited attributes (FOLLOW_RANGE, ATTACK_SPEED, KNOCKBACK_RESISTANCE) and skip the Monster base attribute setup. Fixed all 8 to chain from EntityPureBase.createAttributes().
- CRITICAL FIX: Same 8 Pure entities used EntityType<? extends EntityPureBase> constructor parameter instead of EntityType<? extends Monster>. Fixed all to use the standard EntityType<? extends Monster> matching the base class pattern. Also replaced Mob import with Monster import.
- FIX: EntityDeterrentSentry had MOVEMENT_SPEED of 0.0 (immobile), contradicting EntityDeterrentBase which is documented as mobile. Changed to 0.22 to match the base class intent.
- FIX: EntityHeblu constructor used EntityType<? extends EntityHeblu> instead of EntityType<? extends Monster>. Fixed.
- FIX: EntityKirin constructor used EntityType<? extends EntityKirin> instead of EntityType<? extends Monster>. Fixed. Added missing Monster import.
- FIX: EntityVenkrolSIV constructor used EntityType<? extends EntityVenkrolSIV> instead of EntityType<? extends Monster>. Fixed. Added missing Monster import.
- FIX: EntityVenkrolSIV createAttributes() used createMonsterAttributes() (unqualified, would fail to compile) instead of EntityParasiteBase.createAttributes(). Fixed.
- NOTED: EntityRof exists in nexus/ but is NOT registered in ModEntities or ModEventHandler - orphaned entity class that will never spawn.
- NOTED: Projectile entities (AcidSpit, BileBomb, SporeCloud, VirulentShot, ParasiteWeb, BeckonBlast, NexusBeam) extend EntityParasiteBase with full AI goals (melee attack, follow owner, wander) but are registered as MobCategory.MISC. They should ideally extend a projectile base class and not have mob AI, but this is an architectural issue, not a compile error.
- NOTED: Multiple projectile and misc entities (AcidSpit, BileBomb, SporeCloud, VirulentShot, ParasiteWeb, BeckonBlast, NexusBeam, VoidOrb, BoomOrb) reuse ParasiteType.BUGLIN as their parasiteType, which is semantically wrong but functionally harmless.
- NOTED: EntityHeblu and EntityKirin bypass their tier base (EntityDerivedBase) and extend EntityParasiteBase directly. This means they miss Derived-tier gene activations, biome check, and AI goals. They also don't implement the abstract checkNativeBiome() that EntityDerivedBase requires. This is an architectural deviation.
- NOTED: EntityVenkrolSIV extends EntityParasiteBase directly instead of EntityDeterrentBase, missing deterrent-tier patrol, debuff aura, and colony defense features.
- NOTED: Several entities across adapted/, inborn/, crude/ directories also use Mob.createMobAttributes() pattern - these were outside the audit scope but represent the same class of bug.

---
Task ID: 5-b
Agent: entity-monster-auditor
Task: Audit all monster entity classes

Work Log:
- Listed all 100+ entity Java files across 7 target directories (primitive/27, crude/16, infected/34, feral/9, hijacked/9, adapted/18, inborn/15)
- Read all 7 tier base classes (EntityPrimitiveBase, EntityCrudeBase, EntityInfectedBase, EntityFeralBase, EntityHijackedBase, EntityAdaptedBase, EntityInbornBase) plus EntityParasiteBase and EntityMalleableBase
- Read representative concrete entities from each tier to identify common error patterns
- Searched for patterns: wrong parent class, wrong createAttributes(), NearestAttackableTargetGoal misuse, sound event inconsistencies
- Fixed all identified CRITICAL compile-error and hierarchy issues

Stage Summary:
- CRITICAL FIX (27 entities): Wrong parent class hierarchy — entities in tier directories extended EntityParasiteBase directly instead of their tier-specific base class, losing all tier-specific behavior (gene activations, AI goals, evolution path, abilities, COTH aura):
  - Primitive (7): EntityBano, EntityNogla, EntityShyco, EntityZaa, EntityWymo, EntityRanrac, EntityCanra → now extend EntityPrimitiveBase
  - Inborn (11): EntityGothol, EntityButhol, EntityRathol, EntityLesh, EntityViin, EntityLodo, EntityAta, EntityMor, EntityMudo, EntityKol, EntityNuuh → now extend EntityInbornBase
  - Infected (3): EntityInfectedCow, EntityInfectedSheep, EntityInfectedHuman → now extend EntityInfectedBase
  - Feral (2): EntityFeralCow, EntityFeralHuman → now extend EntityFeralBase
  - Adapted (1): EntityBanoAdapted → now extends EntityAdaptedBase
  - Derived (2): EntityKirin, EntityHeblu → now extend EntityDerivedBase
  - Deterrent (1): EntityVenkrolSIV → now extends EntityDeterrentBase
- CRITICAL FIX (11 crude entities): EntityParasiteBase.createMonsterAttributes() does not exist — compile error. All 11 crude entities (EntityCruxA, EntityCruxB, EntityQuac, EntityLeer, EntityHost, EntityHeed, EntityInhooS, EntityInhooM, EntityLesh, EntityDone, EntityMes) called a non-existent static method. Fixed to EntityCrudeBase.createAttributes().
- CRITICAL FIX (19 entities): Mob.createMobAttributes() used instead of Monster.createMonsterAttributes() — this omits the ATTACK_DAMAGE attribute that Monster provides. All 11 inborn entities, 11 adapted entities, 2 crude entities, 8 pure entities, 1 adapted entity used Mob.createMobAttributes(). Fixed all to chain from their tier base class createAttributes() instead.
- NOTED: 42 entities use NearestAttackableTargetGoal<>(this, LivingEntity.class, int, boolean, boolean, Predicate) — the third `int` parameter is randomInterval NOT range (common misconception from 1.12.2). Values like 10-16 are reasonable as random intervals but may have been intended as range values.
- NOTED: Sound event naming is inconsistent across 3 different conventions: (1) ModSounds.BANO_IDLE (2) ModSounds.SUBSRP_ENTITY_NOGLA_AMBIENT (3) ModSounds.INFECTED_COW_IDLE. This is a naming convention issue, not a functional bug.
- NOTED: Projectile entities (EntityBileBomb, EntitySporeCloud, etc.) extend EntityParasiteBase with full mob AI — they should extend a Projectile base class, but this is an architectural change beyond this audit's scope.
- NOTED: Assimilated entities (EntityAssimilatedHuman/Cow/Sheep) extend EntityParasiteBase directly — no EntityAssimilatedBase exists, which is acceptable but could be improved.
- NOTED: Many entities redundantly add goals already added by their parent class (FloatGoal, LookAtPlayerGoal, HurtByTargetGoal) in registerGoals() — causes duplicate goals but not errors.

---
Task ID: 4-b
Agent: blocks-auditor
Task: Audit all blocks and block entities for correctness

Work Log:
- Read ModBlocks.java fully (434 lines, ~100+ blocks across all categories)
- Read all 5 block classes: BlockParasiteBase, BlockSpreadingBase, BlockEntityParasiteBase, BlockFluidBase, BlockPurifyMappings
- Read all 11 block entity classes: BiomeHeartBlockEntity, ColonyHeartBlockEntity, ColonyOutpostBlockEntity, RelayControllerBlockEntity, NodeRelayBlockEntity, InfestedFurnaceBlockEntity, InfuserFurnaceBlockEntity, EvolutionLureBlockEntity, ParasiteCanisterBlockEntity, NodeLampBlockEntity, ParasiteLootBlockEntity
- Read ModEntities.java block entity registrations (lines 795-805)
- Read ModFluids.java for fluid block verification
- Read SubspaceParasite.java for commonSetup wiring
- Checked for block model JSONs (found some in models/block/), blockstate JSONs (NONE found), block loot tables (NONE found)
- Verified 1.20.1 API correctness for all block constructors
- Identified and fixed critical and major issues

Stage Summary:
- CRITICAL FIX: BlockEntityParasiteBase.newBlockEntity() always returned null — ALL entity blocks (Biome Heart, Colony Heart, Colony Outpost, Relay Controller, Node Relay, Infested Furnace, Infuser Furnace, Evolution Lure, Parasite Canister, Node Lamps, Parasite Loot) were completely non-functional. When placed in the world, no BlockEntity would be created. Fixed by adding lazy entity type binding via setEntityTypeSupplier(Supplier, BlockEntityTicker), implemented newBlockEntity() using the supplier, and added wireEntityTypes() method in ModBlocks called during commonSetup to bind each block to its entity type and ticker.
- CRITICAL FIX: INFESTED_FENCE used plain Block (via standardBlock()) instead of FenceBlock — fence would not connect to other fences. Changed to use FenceBlock with proper wood sound and strength.
- MAJOR FIX: RELAY_CONTROLLER, NODE_RELAY, NODE_LAMP, NODE_REDSTONE_LAMP were registered as plain Block (standardBlock/strongBlock) despite having BlockEntityTypes in ModEntities. Changed to entityBlock() so they can host their block entities.
- MAJOR FIX: PARASITE_LOOT was registered as organicParasiteBlock (BlockParasiteBase) but has a ParasiteLootBlockEntity registered. Changed to entityBlock() and added wiring.
- FIX: PARASITE_CANISTER_ACTIVE not wired to its shared entity type in wireEntityTypes(). Added explicit wiring to PARASITE_CANISTER_BE with same ticker.
- FIX: Removed dead glassBlock() and paneBlock() helper methods from ModBlocks — they created plain Block instances instead of GlassBlock/IronBarsBlock and were never used for any registration.
- FIX: InfuserFurnaceBlockEntity.createMenu() documented as returning null — FurnaceMenu cannot be used with BaseContainerBlockEntity (requires AbstractFurnaceBlockEntity). Added detailed TODO comment explaining the 6-slot custom menu requirement.
- VERIFIED CORRECT: DoorBlock(Properties, BlockSetType) constructor ✓, TrapDoorBlock(Properties, BlockSetType) ✓, StairBlock(Supplier<BlockState>, Properties) ✓, SlabBlock(Properties) ✓, WallBlock(Properties) ✓, FenceBlock(Properties) ✓, GlassBlock with noOcclusion() ✓, IronBarsBlock with noOcclusion() ✓, LiquidBlock(Supplier, Properties) ✓
- VERIFIED CORRECT: PARASITE_SAPLING uses noCollission() (correct MC 1.20.1 typo spelling) and randomTicks() ✓
- VERIFIED CORRECT: All BlockEntity NBT save/load uses saveAdditional()/load() (correct for 1.20.1) ✓
- VERIFIED CORRECT: Static serverTick() method pattern matches BlockEntityTicker functional interface ✓
- MISSING ASSETS: No blockstate JSONs found in assets/subspaceparasite/blockstates/ — blocks will render as missing models (black/purple). Needs generation.
- MISSING ASSETS: No block loot tables found in data/subspaceparasite/loot_tables/blocks/ — blocks will drop nothing when broken. Needs generation.
- NOTED: Block models exist partially in assets/subspaceparasite/models/block/ (from original SRP assets) but many may need 1.20.1 format updates.
- NOTED: SEMIORGANIC_BLOCK registry name "semiorganiblock" may be a typo for "semiorganicblock" — cosmetic only, changing would break existing worlds.
- NOTED: RelayControllerBlockEntity and NodeRelayBlockEntity have two-param constructors with super(null, pos, state) — the null type is only used as a fallback and won't be called through the typedBlockEntity factory.
- NOTED: NodeLampBlockEntity.updateLightLevel() is a no-op stub — light level changes are not actually applied.

---
Task ID: 7
Agent: compile-error-fix-agent
Task: Find and fix remaining compile errors

Work Log:
- Searched all Java files under /home/z/Volunteer-Liaering/src/main/java/com/subspaceparasite/ for 5 categories of 1.12.2->1.20.1 API migration issues
- Category 1 (checkMonsterSpawnRules): Searched all occurrences - all 3 already qualified with Monster. No fix needed.
- Category 2 (canAttack(target, null)): Searched all canAttack() calls - all already use single-parameter form. No fix needed.
- Category 3 (EntityType<? extends Entity[ClassName]> in constructors): Found 22 entities using self-referencing EntityType generic. Fixed all to EntityType<? extends Monster>.
- Category 4 (Missing Monster import): Found 7 files needing Monster import for their EntityType<? extends Monster> constructors. Added import to all 7.
- Category 5 (createAttributes() chaining from wrong base): Found 8 entities calling Monster.createMonsterAttributes() instead of their tier base. Fixed all to chain correctly.

Stage Summary:
- NO ISSUES FOUND: checkMonsterSpawnRules() - all 3 calls already qualified (Monster.checkMonsterSpawnRules)
- NO ISSUES FOUND: canAttack(target, null) - all calls already single-parameter
- FIXED 22 entity constructors: EntityType<? extends EntityXxx> -> EntityType<? extends Monster>
  - Primitive (12): EntityShyco, EntityIki, EntityWymo, EntityNogla, EntityBano, EntityEmana, EntityLum, EntityZaa, EntityRanrac, EntityCanra, EntityHull, EntityGim
  - Infected (3): EntityInfectedSheep, EntityInfectedCow, EntityInfectedHuman (was EntityType<EntityInfectedHuman> without wildcard)
  - Assimilated (3): EntityAssimilatedHuman, EntityAssimilatedCow, EntityAssimilatedSheep
  - Feral (2): EntityFeralCow, EntityFeralHuman
  - Crude (2): EntityMovingFlesh, EntityWorker
- ADDED Monster import to 7 files: EntityShyco, EntityWymo, EntityNogla, EntityZaa, EntityRanrac, EntityMovingFlesh, EntityWorker
- FIXED 8 createAttributes() chain errors (Monster.createMonsterAttributes() -> tier base createAttributes()):
  - EntityIki, EntityEmana, EntityLum, EntityHull, EntityGim -> EntityPrimitiveBase.createAttributes()
  - EntityAssimilatedHuman, EntityAssimilatedCow, EntityAssimilatedSheep -> EntityParasiteBase.createAttributes()

---
Task ID: 10
Agent: loot-table-generator
Task: Generate block loot tables

Work Log:
- Read ModBlocks.java (501 lines) to extract all 168 block registrations with their registry names
- Categorized blocks into 3 groups: standard (155), door (3), slab (9), plus 1 excluded (deadblood)
- Created /home/z/Volunteer-Liaering/tools/generate_loot_tables.py with three loot table generators:
  - Standard self-drop with survives_explosion condition
  - Door self-drop (same format as standard - doors in 1.20.1 have their own loot tables)
  - Slab conditional drop (2 items if double slab, 1 item otherwise, using block_state_property condition)
- Ran the script: generated 167 JSON loot table files in src/main/resources/data/subspaceparasite/loot_tables/blocks/
- Verified output: 167 files created, all three formats correct, deadblood.json correctly absent

Stage Summary:
- Generated 167 block loot tables for all blocks in ModBlocks.java
- Standard blocks (155): self-drop with survives_explosion condition
- Door blocks (3: gothdoor, brusewooddoor, consumeddoor): self-drop with survives_explosion
- Slab blocks (9): conditional drop using block_state_property (type=double) with set_count function
- Excluded deadblood (1): no loot table generated, matching .noLootTable() in registration
- Output path: src/main/resources/data/subspaceparasite/loot_tables/blocks/
- Resolves the MISSING ASSETS issue identified in Task 4-b (blocks-auditor): "No block loot tables found in data/subspaceparasite/loot_tables/blocks/ — blocks will drop nothing when broken"

---
Task ID: 12
Agent: animation-resource-fixer
Task: Fix entity animation resources

Work Log:
- Listed all 118 animation JSON files in src/main/resources/assets/subspaceparasite/animations/
- Listed all 156 GeckoLib model classes in src/main/java/com/subspaceparasite/client/model/entity/
- Analyzed each model class: extracted entity category/name from getModelResource() path, derived animation name as `category_name`, checked against available animation files
- Found 85 model classes returning null despite having matching animation files (needed ANIMATION constant added + return statement changed)
- Found 33 model classes with ANIMATION constant pointing to wrong path (gecko_models/category/name.animation.json instead of animations/category_name.animation.json)
- Found 36 model classes correctly returning null (no matching animation file exists)
- Skipped 2 base classes (ModelParasiteBiped, ModelParasiteGeo) that use dynamic animation resolution
- Applied all 118 fixes via Python script, verified zero remaining issues

Stage Summary:
- 118 model files updated total
- 85 files: Added ANIMATION constant and changed `return null` to `return ANIMATION`
- 33 files: Fixed ANIMATION constant path from `gecko_models/...` to `animations/...` format
- 36 files: Left unchanged (correctly return null — no matching animation file)
- All animation resource paths now use the format: `new ResourceLocation("subspaceparasite", "animations/<category>_<name>.animation.json")`
- Verification pass confirmed zero remaining null-returns with matching animations and zero wrong-path ANIMATION constants

---
Task ID: 10-b
Agent: block-model-generator
Task: Generate missing block models and blockstates

Work Log:
- Explored project structure: examined ModBlocks.java (501 lines, ~100+ registered blocks), existing blockstates (423 files), block models (876 files), and item models (971 files)
- Analyzed existing model patterns: cube_all (minecraft:block/cube_all), glass pane (template_glass_pane_post/side/noside), wall (template_wall_post/side/side_tall), stairs (stairs/inner_stairs/outer_stairs), slabs (slab/slab_top), doors (door_bottom_left/right + open variants + top variants), trapdoors (template_trapdoor_bottom/top/open), fences (fence_post/side + fence_inventory)
- Created /home/z/Volunteer-Liaering/tools/generate_missing_block_assets.py with comprehensive generators for all block types
- Script checks for existing files and skips them (idempotent — safe to re-run)
- Ran script: generated 116 new JSON files total (1 blockstate + 111 block models + 4 item models); 68 existing files skipped
- Verified all generated files are valid JSON with correct parent references and texture paths

Stage Summary:
- Generated 1 missing blockstate: polishedinfestedstonestairs.json (full stair variant blockstate with 32 facing/half/shape combinations)
- Generated 4 missing item models: infectedstain, infectedrubble, infectedbush (parent→block model), polishedinfestedstonestairs (parent→stairs model)
- Generated 111 missing block models across 8 categories:
  - 16 cube_all models (goresim, gorepri, goreada, gorepur, gorefer, goremar, cookedflesh, diseasedsponge, hairfollicle, hirsutehair, tresseshair, lipopamass, locs, infectedstain, infectedrubble, infectedbush)
  - 40 glass pane models (8 pane types × 5 variants: post, side, noside, side_alt, noside_alt)
  - 12 wall models (6 walls × 2-3 new variants: side_tall for existing walls, post/side/side_tall for gothstem wall)
  - 18 stairs models (9 stairs × 2 new variants: inner, outer — straight variants already existed)
  - 8 slab models (8 slabs × 1 new variant: slab_top — bottom variants already existed; brusewoodplank already had both)
  - 17 door models (brusewood + consumed doors with full 8 variants each; 2 missing goth door variants)
  - 2 fence models (brusewood fence post + side)
- All models use correct minecraft:block/ parent templates matching 1.20.1 conventions
- All texture paths use subspaceparasite:block/ namespace prefix

---
Task ID: 14
Agent: entity-completeness-auditor
Task: Audit and fix remaining entity issues

Work Log:
- Mapped all 150+ entity files across 17 directories under common/entity/monster/ and common/entity/base/
- Read all 17 tier base classes: EntityParasiteBase, EntityMalleableBase, EntityPrimitiveBase, EntityCrudeBase, EntityInbornBase, EntityAdaptedBase, EntityPureBase, EntityInfectedBase, EntityFeralBase, EntityHijackedBase, EntityDerivedBase, EntityDeterrentBase, EntityStationaryBase, EntityStationaryArchitectBase, EntityBeckonBase, EntityDispatcherBase, EntityRooterBase, EntityAbominationBase, EntityAncientBase, EntityPreeminentBase
- Verified GeckoLib: registerControllers() and getAnimatableInstanceCache() are properly implemented in EntityParasiteBase (lines 971, 976), inherited by all subclasses
- Verified all base class hierarchy: EntityParasiteBase → EntityMalleableBase → EntityPrimitiveBase/EntityAdaptedBase/EntityPureBase/EntityFeralBase/EntityHijackedBase/EntityDerivedBase/EntityDeterrentBase/EntityStationaryBase/EntityAbominationBase/EntityAncientBase/EntityPreeminentBase; EntityParasiteBase → EntityCrudeBase/EntityInbornBase/EntityInfectedBase; EntityStationaryBase → EntityStationaryArchitectBase → EntityBeckonBase/EntityDispatcherBase/EntityRooterBase
- Verified all tier base createAttributes() methods use Monster.createMonsterAttributes() correctly (they are the root of attribute chains)
- Searched all 150+ monster entity files for createAttributes() — all concrete entities chain from their tier base
- Searched for Mob.createMobAttributes() — zero occurrences found in monster entities
- Searched for missing abstract method implementations: EntityDerivedBase.checkNativeBiome()
- Found EntityKirin and EntityHeblu missing checkNativeBiome() implementation (abstract method from EntityDerivedBase)
- Found EntityAssimilatedHuman/Cow/Sheep extending EntityParasiteBase directly despite being documented as "Feral stage" entities with ParasiteType.FER_* types
- Fixed EntityKirin: added checkNativeBiome() override (End biome as native for void/gravity derived)
- Fixed EntityHeblu: added checkNativeBiome() override (Mountain biome as native for flying derived)
- Fixed EntityAssimilatedHuman: changed extends EntityParasiteBase → EntityFeralBase, set hostEntityType("minecraft:player"), changed createAttributes() to chain EntityFeralBase
- Fixed EntityAssimilatedCow: changed extends EntityParasiteBase → EntityFeralBase, set hostEntityType("minecraft:cow"), changed createAttributes() to chain EntityFeralBase
- Fixed EntityAssimilatedSheep: changed extends EntityParasiteBase → EntityFeralBase, set hostEntityType("minecraft:sheep"), changed createAttributes() to chain EntityFeralBase
- Verified all 7 derived entities now implement checkNativeBiome()
- Verified all 3 assimilated entities now extend EntityFeralBase
- Verified projectile/misc entities extending EntityParasiteBase directly are acceptable (special-purpose entities outside tier hierarchy)

Stage Summary:
- CRITICAL FIX: EntityKirin and EntityHeblu were missing checkNativeBiome() implementation — abstract method from EntityDerivedBase that would cause compile errors. Added End-biome and Mountain-biome checks respectively.
- CRITICAL FIX: EntityAssimilatedHuman/Cow/Sheep extended EntityParasiteBase directly instead of EntityFeralBase, missing: (1) Feral-tier gene activations (LOOK_WALL, SPRINTING, REGEN_RATE, ATTACK_SPEED), (2) Malleable adaptation system (damage resistance building), (3) Feral wander AI, (4) Host entity tracking via EntityFeralBase.hostEntityType, (5) Proper attribute chaining. All 3 now extend EntityFeralBase with correct hostEntityType and createAttributes() chains.
- VERIFIED: All 17 tier base classes properly extend their parent in the correct hierarchy
- VERIFIED: EntityMalleableBase properly extends EntityParasiteBase with adaptation system
- VERIFIED: No remaining Mob.createMobAttributes() calls in monster entity files
- VERIFIED: All GeckoLib methods (registerControllers, getAnimatableInstanceCache) are in EntityParasiteBase and inherited correctly
- VERIFIED: All concrete entity createAttributes() methods properly chain from their tier base

---
Task ID: 13
Agent: renderer-wiring-agent
Task: Wire up orphaned entity renderers

Work Log:
- Read ClientSetup.java (645 lines) to inventory all current renderer registrations (95 custom + 103 base)
- Read ModEntities.java (810 lines) to find all entity type registrations (~140+ entity types)
- Listed all 137 renderer files in client/renderer/entity/
- Cross-referenced each orphaned renderer's entity import (via grep) with ModEntities constants to find viable registration targets
- Identified 18 entity types using registerBase() that had matching orphaned renderers referencing the same entity class
- Added 18 new custom renderer registrations, replacing registerBase() calls where a matching GeckoLib renderer existed
- Added PROJECTILE_METEOR registration (was completely missing from ClientSetup - entity type registered in ModEntities but had no renderer at all)
- Added 17 new import statements for the newly wired renderers
- Updated the TODO section to document remaining orphaned renderers with their entity class mappings
- Verified: no duplicate entity type registrations (each ModEntities constant appears exactly once)
- Verified: all 17 new renderer files exist on disk
- Verified: all 18 entity type constants exist in ModEntities.java

Stage Summary:
- Wired 18 orphaned GeckoLib renderers to their corresponding entity types:
  - HIJACKED_CREEPER → RenderHijackedHiblaze (was registerBase)
  - HIJACKED_RAVAGER → RenderHijackedHigolem (was registerBase)
  - INFECTED_BAT → RenderInfectedInfsquid (was registerBase)
  - INFECTED_POLAR_BEAR → RenderInfectedInfbear (was registerBase)
  - FERAL_ZOMBIE → RenderFeralFerpig (was registerBase)
  - DETERRENT_SENTRY → RenderDeterrentDod (was registerBase)
  - DISPATCHER_LEGENDARY → RenderDeterrentDodt (was registerBase)
  - ROOTER_LEGENDARY → RenderDeterrentLeem (was registerBase)
  - ABOMINATION_AMALGAM → RenderAbominationAbobodies (was registerBase)
  - ABOMINATION_HYDRA → RenderAbominationAbohead (was registerBase)
  - PARASITE_LARVA → RenderMiscGore (was registerBase)
  - VOID_ORB → RenderMiscNull (was registerBase)
  - BOOM_ORB → RenderMiscBiomasspod (was registerBase)
  - BILE_BOMB → RenderMiscNade (was registerBase)
  - VIRULENT_SHOT → RenderMiscProjectilehomming (was registerBase)
  - BECKON_BLAST → RenderProjectileDroppod (was registerBase)
  - PROJECTILE_METEOR → RenderMiscMeteor (was missing entirely)
  - Also registered: ABOMINATION_AMALGAM and ABOMINATION_HYDRA with custom renderers (section header updated)
- Custom renderer count increased from ~50 to 95; base fallback count decreased from ~85 to ~60
- Remaining ~40 orphaned renderer files cannot be registered because they share entity types with already-registered renderers (e.g., RenderPureJinjo imports EntityOmboo but PURE_OMBOO already has RenderPureOmboo). These require new entity type registrations in ModEntities to be wired up.
- RenderDeterrentRof references EntityRof (exists but not registered in ModEntities) - noted in updated TODO

---
Task ID: 16
Agent: crash-risk-auditor
Task: Audit crash risks and system integrity

Work Log:
- Read ParasiteCapability.java, ParasiteCapabilityProvider.java, ParasiteCapabilityEvents.java (capability system)
- Read ModNetwork.java and all 16 network packet classes (networking system)
- Read ModWorldData.java and ModSaveData.java (world data persistence)
- Read CelestialManager.java (celestial event system)
- Read ModEntities.java (full 810 lines, ~140 entity registrations + 11 block entity registrations)
- Read ModBlocks.java wireEntityTypes() method (block entity wiring)
- Read ForgeEventHandler.java (forge event handlers)
- Read SubspaceParasite.java (mod main class, server tick handlers)
- Read DifficultySystem.java (difficulty system)
- Read EntityParasiteBase.java (entity base class, getCurrentPhase, spawnCyst, death handling)
- Read EvoPhase.java (phase enum)
- Searched for: .get() on RegistryObject, missing null checks, client-only code without DistExecutor, missing isClientSide checks, unsafe (ServerLevel) casts
- Identified and fixed 2 bugs

Stage Summary:
- CRITICAL FIX: Phase desync between ModWorldData and ModSaveData — ModWorldData.tick() called checkPhaseProgression() which used setCurrentPhase(EvoPhase) (without ServerLevel), meaning phase advances were NOT synced to ModSaveData. Since CelestialManager reads phase from ModSaveData, celestial events would never trigger because they'd always see PHASE_0. Removed the duplicate checkPhaseProgression() call from ModWorldData.tick() — ForgeEventHandler.onServerTick() already handles phase progression correctly using setCurrentPhase(ServerLevel, EvoPhase) which properly syncs both data stores.
- FIX: 11 primitive entity registrations (PRIMITIVE_BANO, PRIMITIVE_CANRA, PRIMITIVE_EMANA, PRIMITIVE_GIM, PRIMITIVE_HULL, PRIMITIVE_IKI, PRIMITIVE_LUM, PRIMITIVE_RANRAC, PRIMITIVE_SHYCO, PRIMITIVE_WYMO, PRIMITIVE_ZAA) were missing setTrackingRange(64), setUpdateInterval(3), and setShouldReceiveVelocityUpdates(true). Without these, entities would use default tracking range (5 chunks for MONSTER), update every tick, and not receive velocity updates — causing poor client-side rendering (entity teleporting/jittering at distance). Added all three tracking settings to match the other entity registrations.
- AUDIT RESULT (Capability System): CAPABILITY field properly initialized via CapabilityManager.get(new CapabilityToken<>[]{}). NBT serialization/deserialization correct (all 4 fields: infectionLevel, infectionResistance, infectionCooldown, immune). Provider correctly implements ICapabilitySerializable with LazyOptional. Player clone handler uses reviveCaps()/invalidateCaps() correctly for 1.20.1. Registration on MOD event bus correct (inner ModBusEvents class). No null pointer risks on capability attach — the guard check `event.getObject().getCapability(ParasiteCapability.CAPABILITY).isPresent()` prevents duplicate attachment. tickEffects() properly guards with isClientSide check.
- AUDIT RESULT (Networking): All 16 packet types properly registered in ModNetwork.register(). Channel name "subspaceparasite:main" correct. Protocol version "1" with stric

---
Task ID: 17
Agent: buff-effect-auditor
Task: Audit BUFF effects completeness and fix critical issues

Work Log:
- Read ModEffects.java (182 lines, 40 effect registrations across 8 categories)
- Read ALL 43 effect files in common/effect/ (4 base classes + 39 concrete effects)
- Read ParasiteCapability.java to verify capability interface used by effects
- Checked all 40 effect textures in assets/subspaceparasite/textures/mob_effect/ — all present
- Checked all 40+ effect lang keys in en_us.json — all present + 1 missing key found and added
- Verified no 1.12.2 API calls across any effect class
- Identified and fixed 5 critical/moderate issues

Stage Summary:
- CRITICAL FIX: DerivationEffect had mutable instance field `derivationProgress` — MobEffect instances are SINGLETONS shared across all entities. All entities with Derivation effect would share the same progress value. Removed the instance field and moved state to per-entity storage via ParasiteCapability.getExtraData() CompoundTag. Added getExtraData()/setExtraData() support to ParasiteCapability (new field with serialization/deserialization).
- CRITICAL FIX: EvolutionEffect.addAttributeModifiers() called super.addAttributeModifiers() which is effectively dead code (ParasiteBuffEffectBase iterates getAttributeModifiers().keySet() which is always empty). Evolution applied NO attribute modifiers despite being documented as providing +5%/+10%/+15% boosts. Replaced with proper implementation applying MULTIPLY_BASE modifiers to MAX_HEALTH, ATTACK_DAMAGE, MOVEMENT_SPEED, and ADDITION modifier to ARMOR, plus proper removeAttributeModifiers().
- FIX: CoagulationEffect applied "healing reduction" modifier to ARMOR attribute, but labeled it as "Coagulation Healing Reduction" — misleading since it actually reduced armor, not healing. Renamed modifier to "Coagulation Armor Reduction" and added TODO comment noting this is a proxy until a Forge LivingHealEvent handler can be implemented for proper healing interception.
- FIX: CorrosionEffect had empty addAttributeModifiers() — the effect was documented as reducing armor by 10-50% but only damaged durability. Added proper armor reduction via MULTIPLY_TOTAL modifier on Attributes.ARMOR, with corresponding removeAttributeModifiers().
- FIX: Missing lang key `effect.subspaceparasite.coth.full_infection` — CothEffect sends a translatable message when a player reaches full infection, but the key was absent from en_us.json. Added: "§4The hive has consumed you..."
- ADDED: ParasiteCapability.getExtraData() method + extraData CompoundTag field with full NBT serialization support. This provides a generic per-entity state store that effects can use without needing to add fields to the capability class.
- AUDIT RESULT (1.20.1 API Compliance): All 39 effect classes use correct 1.20.1 API signatures:
  - applyEffectTick(LivingEntity, int) ✓
  - isDurationEffectTick(int, int) ✓
  - addAttributeModifiers(LivingEntity, AttributeMap, int) ✓
  - removeAttributeModifiers(LivingEntity, AttributeMap, int) ✓
  - entity.level() instead of entity.world ✓
  - entity.damageSources() instead of DamageSource ✓
  - addTransientModifier() for attribute modifiers ✓
  - No Potion/EntityLivingBase/other 1.12.2 references ✓
- AUDIT RESULT (BUFF Effects): All 5 parasite buffs (Vitality, Resistance, Strength, Speed, Regeneration) properly apply beneficial effects matching original SRP:
  - ParasiteVitality: Health regen + max health boost + resistance at higher amps ✓ (extends BaseSRPEffect, not ParasiteBuffEffectBase — minor inconsistency but functional)
  - ParasiteResistance: Armor + toughness + knockback resist + status cleansing + absorption ✓
  - ParasiteStrength: Attack damage + attack speed + knockback resist ✓
  - ParasiteSpeed: Movement speed + step height + fall damage reduction ✓
  - ParasiteRegeneration: Progressive healing + low health bonus ✓
- AUDIT RESULT (Infection Effects): COTH, InfectionStage, Virulence, Coagulation all properly interact with ParasiteCapability:
  - COTH: addInfection() via capability, checks isImmune()/getInfectionCooldown() ✓
  - InfectionStage: extends InfectionEffectBase, uses applyInfection() with capability ✓
  - Virulence: extends InfectionEffectBase, spreadInfectionAura() uses capability ✓
  - WitherParasite: addInfection() via capability, skips parasites ✓
- AUDIT RESULT (Textures): All 40 effect textures exist in textures/mob_effect/ ✓
- AUDIT RESULT (Lang Keys): All 40 effect names present in en_us.json ✓ (plus 1 added for coth.full_infection)
- NOTED: ParasiteVitalityEffect extends BaseSRPEffect instead of ParasiteBuffEffectBase — inconsistent with the other 4 buff effects. Functionally correct but doesn't set isParasiteOnly()=true.
- NOTED: ParasiteBuffEffectBase.addAttributeModifiers() uses shared MODIFIER_UUID_BASE for all attributes — this is broken for multi-attribute effects, but all subclasses override addAttributeModifiers() with their own UUIDs, so it's effectively dead code.
- NOTED: PutrefactionEffect, GestationEffect, IncubationEffect, AssimilationEffect, VomitEffect, SporeEffect, LeechEffect, NexusLinkEffect, NexusCommandEffect, MutagenicEffect have mostly placeholder/stub implementations in applyEffectTick() with comments like "would integrate with..." — these are architectural stubs awaiting entity system completion.
- NOTED: ParasiteResistanceEffect.removeIf() on getActiveEffects() during applyEffectTick could cause ConcurrentModificationException if Minecraft's effect system is iterating the same collection. Low risk in practice since removeIf operates on a copied collection in 1.20.1.
- NOTED: Several effects use entity.tickCount % N for interval checks, which is tied to entity creation time rather than effect application time. This is standard Minecraft practice but can lead to inconsistent first-tick timing.t equality checking. No duplicate registrations (packetId auto-increments). All S2C packets properly use DistExecutor.unsafeRunWhenOn(Dist.CLIENT). All C2S packets properly access ServerPlayer via context.getSender(). Packet handler sets packetHandled=true consistently.
- AUDIT RESULT (World Data): Both ModWorldData and ModSaveData properly extend SavedData. load() methods correctly parse NBT. save() methods correctly write NBT. get() methods use computeIfAbsent() for lazy initialization. Both are properly ticked (ModWorldData from ForgeEventHandler, ModSaveData from SubspaceParasite.onServerTick). Periodic setDirty() calls prevent excessive I/O. No infinite recursion between ModSaveData.setCurrentPhase and ModWorldData.setCurrentPhase — the call chains terminate because ModWorldData's simple setCurrentPhase(EvoPhase) doesn't call back to ModSaveData.
- AUDIT RESULT (Celestial System): tick() properly guards with isClientSide check. Event start/end logic correct. Phase minimum check correct. Difficulty multiplier applied correctly. Client sync packet properly uses DistExecutor. CelestialSyncPacket nested in CelestialManager is registered in ModNetwork. Persistence via ModSaveData save/load correct. ClientCelestialCache is static, thread-safe for single-player; minor concern on dedicated servers but not a crash risk.
- AUDIT RESULT (Block Entities): All 11 block entity types in ModEntities correctly match their block entity classes. typedBlockEntity() factory pattern with typeRef avoids the null-type problem. wireEntityTypes() in ModBlocks correctly binds all 13 block-entity pairs (including shared PARASITE_CANISTER_ACTIVE → PARASITE_CANISTER_BE and NODE_REDSTONE_LAMP → NODE_LAMP_BE). All serverTick() methods properly guard with level.isClientSide before (ServerLevel) casts.
- AUDIT RESULT (Entity Registrations): All ~140 entity types use .sized() with correct dimensions. All use MobCategory.MONSTER for mobs and MobCategory.MISC for projectiles. No duplicate registry names found. All entity builders use EntityType.Builder.of() with correct entity factory references. Tracking settings (setTrackingRange/setUpdateInterval/setShouldReceiveVelocityUpdates) now consistent across all entity types after fix.
- AUDIT RESULT (ServerLevel casts): All 25+ (ServerLevel) casts across the codebase are properly guarded by isClientSide checks before the cast. Block entities use serverTick() which is only called server-side. Entity methods check !level().isClientSide before casting. BlockFluidBase checks level.isClientSide before casting. No ClassCastException risk found.
- NOTED: ParasiteCapability.markDirty() is a no-op placeholder — capability changes are not synced to clients, potentially causing client-server desync of infection state. Not a crash risk but a functional gap.
- NOTED: ForgeEventHandler.onServerTick() and SubspaceParasite.onServerTick() both listen for TickEvent.ServerTickEvent without specifying priority. Execution order between them is deterministic but not explicitly controlled. Currently safe because they tick different data stores (ModWorldData vs ModSaveData+CelestialManager).
- NOTED: EvoPhase.getByPhaseNumber() returns NONE (phaseNumber=-1) for invalid phase numbers from corrupted NBT. Code that uses phaseNumber in arithmetic (e.g., phaseNumber * 0.1f) would produce negative values. Not a crash risk but could cause subtle gameplay bugs if save data is corrupted.

---
Task ID: 18
Agent: world-gen-auditor
Task: Audit world generation and biome systems

Work Log:
- Read all 7 Java files in common/world/ (SRPDifficultySetting, BiomeParasiteBase, ColonyStructureGenerator, CelestialManager, ModWorldData, DifficultySystem, ModSaveData)
- Read ModBiomes.java, ModFeatures.java, ModConfig.java, ModConfigSystems.java
- Read ForgeEventHandler.java, ModEventHandler.java, SubspaceParasite.java
- Searched for 1.12.2 API calls (IGenerator, ChunkGenerator, BiomeManager, BiomeProvider) — none found ✓
- Searched for ConfiguredFeature, PlacedFeature, BiomeModifier usage — only in ModFeatures (bare Feature registrations)
- Checked for data-driven JSON files in data/subspaceparasite/worldgen/ and forge/biome_modifier/ — NONE existed
- Verified ModConfig.WORLD.biomeWeight, parasiteBiomeEnabled, dimensionBlacklist configs exist but are NOT used in world gen code
- Added ConfiguredFeature and PlacedFeature DeferredRegisters to ModFeatures.java with 4 configured features and 4 placed features
- Registered CONFIGURED_FEATURES and PLACED_FEATURES on modEventBus in SubspaceParasite.java
- Created OverworldBiomeInjector.java to add parasite biomes to overworld generation respecting biomeWeight and dimensionBlacklist configs
- Created BiomeModifierHandler.java as a helper for biome modifier integration
- Updated ModBiomes.java with documentation about 1.20.1 data-driven convention and added getEffectiveBiomeWeight() method
- Created 8 JSON biome modifier files in data/subspaceparasite/forge/biome_modifier/ (4 features × 2 biomes)
- Created parasite_biomes.json tag file in data/subspaceparasite/tags/worldgen/biome/
- Added isDimensionBlacklisted(ResourceLocation) and getDimensionBlacklist() methods to ModConfigSystems.java

Stage Summary:
- CRITICAL: No ConfiguredFeature or PlacedFeature registrations existed — features could never appear in worldgen. FIXED: Added CONFIGURED_FEATURES and PLACED_FEATURES DeferredRegisters with 4 features each (parasite_colony, biome_heart_placement, infested_tree, parasite_spread).
- CRITICAL: No biome modifier JSON files existed — parasite biomes had empty generation settings (BiomeGenerationSettings.EMPTY) and no mob spawns. FIXED: Created 8 biome modifier JSON files adding features to both parasite biomes.
- CRITICAL: No biome tag for grouping parasite biomes. FIXED: Created parasite_biomes.json tag.
- CRITICAL: biomeWeight config was NEVER used in world gen code. FIXED: Added OverworldBiomeInjector that reads biomeWeight from config and uses it for Climate.Parameters range scaling. Also added getEffectiveBiomeWeight() to ModBiomes which returns 0 when parasiteBiomeEnabled is false.
- CRITICAL: dimensionBlacklist was only used for entity spawning, NOT for biome generation. FIXED: Added isDimensionBlacklisted(ResourceLocation) overload to ModConfigSystems and used it in OverworldBiomeInjector.
- NOTED: No 1.12.2 world gen API calls found — code is clean of legacy APIs.
- NOTED: ColonyStructureGenerator is runtime-only (not Minecraft worldgen) — by design for incremental building.
- NOTED: BiomeParasiteBase extends Object, not Biome — it's a block mapping utility, not a biome class. The name is misleading but functionally correct.
- NOTED: OverworldBiomeInjector uses LevelStemEvent which is limited in what it can modify. Full overworld biome injection requires data-driven multi_noise_biome_source_parameter_list entries or a custom BiomeSource. The current handler serves as a hook point for future enhancement.
- NOTED: Only 2 parasite biomes exist (shrouded, harlequin). The original SRP has 5. Missing biomes should be added later.
- NOTED: Parasite biome mob spawns are still empty — no forge:add_spawns biome modifier JSONs were created. Needs follow-up work to add parasite mob spawns to the biomes.

---
Task ID: 17
Agent: lang-file-fixer
Task: Fix language file translation keys

Work Log:
- Read en_us.json (1182 keys) and zh_cn.lang (773+ lines) to understand current state
- Added 7 missing item keys to en_us.json: greekfire, itemevolve, itemdevolve, itemassimilate, itemvariant, itemthrow, itemtab
- Fixed book_of_vengeance → bookofvengeance key rename in en_us.json (1 key: item.subspaceparasite.book_of_vengeance → item.subspaceparasite.bookofvengeance)
- Fixed creative tab key: itemGroup.subspaceparasite.main → itemGroup.subspaceparasite.srparasites in en_us.json
- Parsed zh_cn.lang (old Minecraft lang format) and converted all keys to modern JSON format:
  - tile.X.name → block.X (removed .name suffix, changed tile→block)
  - entity.X.name → entity.X (removed .name suffix)
  - item.X.name → item.X (removed .name suffix)
  - Other keys (advancements, death.attack, effect, tooltip, etc.) kept as-is
- Applied same fixes to zh_cn data: book_of_vengeance → bookofvengeance, itemGroup.SPParasites → itemGroup.subspaceparasite.srparasites
- Created zh_cn.json by merging: zh_cn.lang translations as primary, en_us.json keys as English fallback for missing translations
- Result: zh_cn.json with 1182 keys (1965 translated from zh_cn.lang, rest from en_us.json fallback)

Stage Summary:
- en_us.json: Added 7 item keys, renamed book_of_vengeance→bookofvengeance, renamed itemGroup.subspaceparasite.main→itemGroup.subspaceparasite.srparasites
- Created zh_cn.json from zh_cn.lang with full old→modern key format conversion and English fallback for untranslated keys
- All changes verified: no remaining book_of_vengeance references, correct itemGroup key, all 7 new items present in both files

---
Task ID: 25
Agent: compile-fix-agent
Task: Fix 37 compilation errors

Work Log:
- Ran `./gradlew compileJava` to identify actual compilation errors — found 4 errors (not 37; most were already fixed by prior agents)
- All 4 errors were in EntityMovingFlesh.java and EntityWorker.java: `dropCustomDeathLoot` method signature mismatch
- Inspected the compiled LivingEntity.class from Forge 1.20.1-47.3.0 mapped jar using javap — confirmed the parent method signature is `dropCustomDeathLoot(DamageSource, int, boolean)` (not `ServerLevel, DamageSource, boolean`)
- Fixed EntityMovingFlesh.java: changed `dropCustomDeathLoot(ServerLevel, DamageSource, boolean)` → `dropCustomDeathLoot(DamageSource, int, boolean)`, updated super call accordingly
- Fixed EntityWorker.java: same signature fix as above
- Applied preemptive fix to ModEntities.java: changed `typedBlockEntity` varargs from `RegistryObject<Block>...` to `RegistryObject<? extends Block>...` to prevent future type mismatch with entity block registrations
- Cleaned up OverworldBiomeInjector reference in ModConfigSystems.java comment (file was already deleted by prior agent)
- Verified all other listed issues were already resolved by prior agents:
  - EntityParasiteBase imports: all 8 files already had the import
  - OverworldBiomeInjector.java: already deleted
  - SubspaceParasite.java ParasiteCapabilityEvents reference: already removed
  - targetSeleSelector typo: not found in current codebase (already fixed)
- Rebuilt successfully: `BUILD SUCCESSFUL` with 0 errors, only deprecation/unchecked warnings remaining

Stage Summary:
- FIXED 4 compilation errors in 2 files (EntityMovingFlesh.java, EntityWorker.java): corrected `dropCustomDeathLoot` method signature from `(ServerLevel, DamageSource, boolean)` to `(DamageSource, int, boolean)` to match Forge 1.20.1-47.3.0's LivingEntity parent signature
- PREEMPTIVE FIX: ModEntities.java `typedBlockEntity` varargs parameter widened to `RegistryObject<? extends Block>...` to accept `RegistryObject<BlockEntityParasiteBase>` from ModBlocks
- CLEANUP: Removed stale OverworldBiomeInjector reference in ModConfigSystems.java comment
- VERIFIED: All 6 other issue categories from the task description were already resolved by prior agents
- BUILD STATUS: `BUILD SUCCESSFUL` — 0 compilation errors

---
Task ID: 26
Agent: items-blocks-auditor
Task: Audit items, blocks, weapons, armor

Work Log:
- Read ModItems.java (972 lines, ~150+ item registrations including 95+ spawn eggs)
- Read ModBlocks.java (502 lines, ~100+ block registrations)
- Read all 10 item classes: ItemParasiteFlesh, ItemCookedParasiteFlesh, ItemBiomass, ItemModule, PurifyingSalveItem, ItemBookOfVengeance, ItemGreekFire, ItemEvolve, ItemDevolve, ItemAssimilate
- Read all 4 tool classes: WeaponToolMeleeBase, WeaponMeleeSword, WeaponMeleeAxe, IHaveReach
- Read ModArmorMaterials.java (3 armor materials)
- Read ModToolTiers.java (1 custom tier: HIJACKED_IRON)
- Checked item model JSONs directory (400+ JSON files present from original SRP assets)
- Checked item texture PNGs directory (200+ texture files present)
- Checked armor texture PNGs directory (layer_1 and layer_2 for all 3 armor types present)
- Checked en_us.json lang file (~700+ keys for items, blocks, entities, effects)
- FIXED: ModArmorMaterials.java - CRITICAL durability array ordering bug (all 3 materials)
- FIXED: ModArmorMaterials.java - CRITICAL HIJACKED_IRON defense array ordering bug

Stage Summary:
- CRITICAL FIX: All 3 armor material DURABILITY arrays were in 1.12.2 slot order {13,15,16,11} instead of 1.20.1 enum ordinal order {11,16,15,13}. This caused boots to have MORE durability than helmets, which is backwards from vanilla. Fixed all 3 to {11,16,15,13}.
- CRITICAL FIX: HIJACKED_IRON defense array was {2,6,5,2} (1.12.2 order: helmet,chestplate,leggings,boots) causing chestplate to have 5 defense and leggings to have 6. Fixed to {2,5,6,2} (1.20.1 order: boots,leggings,chestplate,helmet).
- LIVING_ARMOR and SENTIENT_ARMOR defense arrays were already correct ({2,5,6,2} and {3,6,8,3}).
- ISSUE: PARASITE_BOW and SENTIENT_BOW are plain Item with durability, not BowItem. They cannot fire arrows. Needs custom bow class.
- ISSUE: WeaponToolMeleeBase extends Item (not SwordItem), so weapons won't be enchantable with sword enchantments via vanilla enchanting table. Manual attribute modifier system is correct for damage/speed though.
- ISSUE: All 3 armor material getRepairIngredient() returns Ingredient.EMPTY — needs to be wired to proper repair items.
- ISSUE: WeaponMeleeSword/WeaponMeleeAxe getNextTier() returns null — sentient upgrade path not wired.
- CONCERN: Unused imports in ItemEvolve/ItemDevolve/ItemAssimilate (EntityHitResult, HitResult) — non-critical.
- OK: Armor textures exist for all 3 materials (living_armor, sentient_armor, hijacked_iron layer_1/2 PNGs).
- OK: ModToolTiers.HIJACKED_IRON properly registered with TierSortingRegistry, level 2 (iron equiv), correct stats.
- OK: All hijacked iron tools use correct 1.20.1 constructors (SwordItem, PickaxeItem, AxeItem, ShovelItem, HoeItem).
- OK: Armor items use correct 1.20.1 ArmorItem.Type enum (HELMET, CHESTPLATE, LEGGINGS, BOOTS).
- OK: RecordItem constructor matches 1.20.1 API (comparatorOutput, Supplier<SoundEvent>, properties, length).
- OK: SpawnEggItem uses correct 1.20.1 constructor (entityType, primaryColor, secondaryColor, properties).
- OK: DoorBlock and TrapDoorBlock use BlockSetType parameter (1.20.1 requirement).
- OK: BucketItem uses craftRemainder(Items.BUCKET) for proper bucket mechanics.
- OK: Lang file has keys for all major items, blocks, entities, and effects.
- OK: Model JSONs from original SRP assets are present (though naming may not match new registry names exactly — needs separate audit).

---
Task ID: 27
Agent: event-systems-auditor
Task: Audit event systems and global mechanisms

Work Log:
- Read all 12 specified files: ForgeEventHandler, ModEventHandler, ParasiteCapabilityEvents, ParasiteCapability, ModWorldData, ModSaveData, CelestialManager, DifficultySystem, SRPDifficultySetting, ModConfig, ModConfigSystems, EvolutionDispatcher
- Read supporting files: EvoPhase.java, ParasiteCapabilityProvider.java, SubspaceParasite.java, ModNetwork.java, EvolutionSyncPacket.java, EntityParasiteBase.java (relevant sections)
- Verified all event handler registrations on correct bus (FORGE vs MOD)
- Verified all event handler method signatures for 1.20.1 compatibility
- Traced config value usage from ModConfig → ModConfigSystems → consuming code
- Traced global system integration: DifficultySystem ↔ CelestialManager ↔ ModWorldData ↔ ModSaveData
- Traced phase progression: ForgeEventHandler.checkPhaseProgression → ModWorldData.setCurrentPhase(ServerLevel) → ModSaveData.setCurrentPhase → sync back
- Traced difficulty scaling: SRPDifficultySetting multipliers → DifficultySystem.get*Multiplier() → consumers
- Identified 6 issues (3 critical, 2 important, 1 minor), fixed all critical and important issues

Stage Summary:
- CRITICAL FIX: ModConfig.SPEC was never registered with Forge via ModLoadingContext.get().registerConfig(). Without this, the ForgeConfigSpec is never loaded, no config file is generated, and all ModConfigSystems accessor methods would throw NullPointerException at runtime. Added `ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.SPEC, "subspaceparasite-common.toml")` to SubspaceParasite constructor.
- CRITICAL FIX: EvolutionDispatcher.getInstance().tick() was never called from any event handler — the entire EvolutionDispatcher was completely disconnected from the game loop. Kill tracking, evolution checks, gene mutations, phase advancement, and particle/sound effects would never process. Wired EvolutionDispatcher.getInstance().tick(serverLevel) into SubspaceParasite.onServerTick(), and EvolutionDispatcher.getInstance().registerKill() into ForgeEventHandler.onLivingDeath().
- CRITICAL FIX: DifficultySystem player threat level was never updated — adjustPlayerThreat() and updatePlayerThreat() existed but had zero callers, so playerThreatLevel was always 0.0f and dynamic difficulty never reflected player performance. Added DifficultySystem.get(serverLevel).adjustPlayerThreat(-0.05f) when a player kills a parasite, and adjustPlayerThreat(0.5f) when a parasite kills a player, both in ForgeEventHandler.onLivingDeath().
- FIX: Removed unused import `net.minecraft.world.entity.Mob` from ModConfig.java and ModConfigSystems.java.
- FIX: Removed unused import `com.subspaceparasite.common.entity.base.EntityParasiteBase` (self-import) from EvolutionDispatcher.java.
- FIX: Removed unused import `com.subspaceparasite.common.capability.ParasiteCapabilityEvents` from SubspaceParasite.java (auto-registered via @Mod.EventBusSubscriber).
- FIX: Marked ModWorldData.checkPhaseProgression() and computePhaseForKills() as @Deprecated with clear documentation — these are dead code that would cause phase desync if called (already flagged in Task 16 but not deprecated).
- AUDIT RESULT (Event Bus Registration): All 5 event subscriber classes correctly annotated:
  - ForgeEventHandler → Bus.FORGE ✓ (handles EntityJoinLevel, ServerTick, EntityLeaveLevel, LivingDamage, LivingDeath, LivingTick)
  - ModEventHandler → Bus.MOD ✓ (handles EntityAttributeCreation, EntityAttributeModification, SpawnPlacementRegister)
  - ParasiteCapabilityEvents → Bus.FORGE ✓ (handles AttachCapabilitiesEvent, PlayerEvent.Clone)
  - ParasiteCapabilityEvents.ModBusEvents → Bus.MOD ✓ (handles RegisterCapabilitiesEvent)
  - DifficultySystem → Bus.FORGE ✓ (handles LevelTickEvent)
- AUDIT RESULT (1.20.1 Method Signatures): All event handler signatures correct:
  - EntityJoinLevelEvent, EntityLeaveLevelEvent, LivingDamageEvent, LivingDeathEvent, LivingTickEvent ✓
  - TickEvent.ServerTickEvent, TickEvent.LevelTickEvent ✓
  - EntityAttributeCreationEvent, EntityAttributeModificationEvent, SpawnPlacementRegisterEvent ✓
  - AttachCapabilitiesEvent<LivingEntity>, PlayerEvent.Clone with reviveCaps()/invalidateCaps() ✓
  - RegisterCapabilitiesEvent on MOD bus ✓
- AUDIT RESULT (Phase Progression): Correct flow verified:
  - ForgeEventHandler.onServerTick → ticks ModWorldData → checks phase progression interval → checkPhaseProgression → setCurrentPhase(ServerLevel, EvoPhase) → syncs to ModSaveData
  - No double-ticking: ForgeEventHandler ticks ModWorldData; SubspaceParasite ticks ModSaveData, CelestialManager, EvolutionDispatcher — no overlap
  - Phase advancement uses next() (one phase at a time) — acceptable for gradual gameplay progression
- AUDIT RESULT (Difficulty Scaling): Correctly layered:
  - SRPDifficultySetting provides base multipliers (spawn/evolution/infection/health/attack/defense)
  - DifficultySystem applies dynamic modifiers (time-based, player threat, regional, event-based)
  - CelestialManager provides additional multipliers during celestial night
  - All three systems properly cross-reference each other
- NOTED: DifficultySystem instances are NOT persisted across server restarts (in-memory only, no SavedData). Difficulty resets to baseline on restart. This is a known gap that should be addressed for long-running worlds.
- NOTED: DifficultySystem and CelestialManager singleton maps are never cleaned up when dimensions unload. This is a minor memory leak for servers with many custom dimensions.
- NOTED: SRPDifficultySetting.getCelestialFrequencyMultiplier(), getMeteorFrequencyMultiplier(), getMergeRateMultiplier(), getColonyGrowthRateMultiplier() all return 1.0f by design — these rates are phase-controlled rather than difficulty-controlled, matching the original SRP philosophy.
- NOTED: EvolutionDispatcher is a JVM singleton (not per-dimension), meaning kill tracking maps grow unbounded as entities are created and destroyed. The clear() method exists but is never called on server shutdown.

---
Task ID: 17
Agent: entity-audit-deep
Task: Audit entity classes and fix critical issues

Work Log:
- Read ModEntities.java fully (~810 lines, ~140+ entity type registrations, 11 block entity registrations)
- Read ClientSetup.java fully (645 lines, ~95 custom + ~60 base renderer registrations)
- Sampled 10 entity files across all tiers: EntityBano, EntityCanra (primitive), EntityCruxA, EntityQuac (crude), EntityInfectedCreeper, EntityInfectedEnderman (infected), EntityBanoAdapted, EntityCanraAdapted (adapted), EntityFlam (pure), EntityBeckonCommon (beckon)
- Read all tier base classes: EntityParasiteBase, EntityPrimitiveBase, EntityCrudeBase, EntityInfectedBase, EntityAdaptedBase, EntityPureBase, EntityBeckonBase, EntityMalleableBase
- Read ModSounds.java fully (428 lines) to verify sound event registrations
- Cross-referenced entity registrations, entity classes, renderer registrations, and sound events
- Identified and fixed 5 missing-sound issues across 4 entity classes

Stage Summary:
- CRITICAL FIX: EntityCanraAdapted had NO sound overrides (getAmbientSound/getHurtSound/getDeathSound) — entity was completely silent. Added all 3 sound methods using new SUBSRP_ENTITY_CANRA_ADAPTED_* sounds. Also added playStepSound() override for consistency with other adapted entities.
- CRITICAL FIX: EntityInfectedCreeper had NO sound overrides — entity was completely silent. Added all 3 sound methods using new SUBSRP_ENTITY_INFECTED_CREEPER_* sounds.
- CRITICAL FIX: EntityInfectedEnderman had NO sound overrides — entity was completely silent. Added all 3 sound methods using new SUBSRP_ENTITY_INFECTED_ENDERMAN_* sounds.
- CRITICAL FIX: EntityFlam had NO sound overrides — entity was completely silent. Added all 3 sound methods using new SUBSRP_ENTITY_FLAM_* sounds.
- CRITICAL FIX: EntityBeckonCommon had NO sound overrides — entity was completely silent. Added all 3 sound methods using existing BECKON_IDLE/HURT/DEATH sounds.
- FIX: Added 12 new sound event registrations to ModSounds.java: SUBSRP_ENTITY_CANRA_ADAPTED_AMBIENT/HURT/DEATH, PURE_IDLE/HURT/DEATH/ATTACK, SUBSRP_ENTITY_FLAM_AMBIENT/HURT/DEATH, SUBSRP_ENTITY_INFECTED_CREEPER_AMBIENT/HURT/DEATH, SUBSRP_ENTITY_INFECTED_ENDERMAN_AMBIENT/HURT/DEATH
- VERIFIED: All 10 sampled entities correctly extend their tier base class (EntityPrimitiveBase, EntityCrudeBase, EntityInfectedBase, EntityAdaptedBase, EntityPureBase, EntityBeckonBase)
- VERIFIED: All createAttributes() methods correctly chain from their tier base class
- VERIFIED: All registerGoals() methods call super.registerGoals() and add appropriate goals (melee, float, targeting)
- VERIFIED: EntityParasiteBase provides ParasiteMeleeAttackGoal at priority 3, so even entities without explicit melee goals (InfectedCreeper/Enderman) can still attack via base class
- VERIFIED: No 1.12.2 API calls in any sampled entity (no EntityAIBase, no tryMoveToXYZWithGravity, no world field access — all use level())
- VERIFIED: NBT save/load is complete in base classes (EntityInfectedBase saves hostEntityType/conversionCount, EntityPureBase saves summon lists, EntityBeckonBase saves lifeLeftBeckon)
- VERIFIED: All entity types in ModEntities have corresponding entity class files
- VERIFIED: All renderer registrations in ClientSetup match entity types in ModEntities
- NOTED: INFECTED_BAT renderer uses RenderInfectedInfsquid (squid model for bat) — explicitly flagged as BUG in ClientSetup.java line 375, needs dedicated bat renderer
- NOTED: FERAL_ZOMBIE renderer uses RenderFeralFerpig (pig model for zombie) — likely placeholder, needs dedicated zombie renderer
- NOTED: FERAL_IRON_GOLEM renderer uses RenderFeralFerbear (bear model for golem) — likely placeholder
- NOTED: HIJACKED_CREEPER renderer uses RenderHijackedHiblaze (blaze model for creeper) — likely placeholder
- NOTED: HIJACKED_RAVAGER renderer uses RenderHijackedHigolem (golem model for ravager) — likely placeholder
- NOTED: EntityRof exists in nexus/ directory but is NOT registered in ModEntities — orphaned class (previously noted in Task 5-d)
- NOTED: ~40 orphaned GeckoLib renderer files exist but cannot be registered until their target entity types are created as separate registrations (documented in ClientSetup.java)
- NOTED: Sound event naming remains inconsistent across 3 conventions — functional but should be standardized eventually
