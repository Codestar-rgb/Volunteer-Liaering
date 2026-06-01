package com.subspaceparasite.common.entity.projectile;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.world.ModWorldData;
import com.subspaceparasite.common.world.SRPDifficultySetting;
import com.subspaceparasite.config.ModConfig;
import com.subspaceparasite.core.ModBlocks;
import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModSounds;
import com.subspaceparasite.core.ModParticleTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

/**
 * EntityMeteor - Meteor Projectile Entity
 * <p>
 * Key SRP mechanic: meteors fall from the sky, crash, and spread COTH/parasites.
 * <p>
 * Features:
 * - Falls from sky at random intervals (controlled by difficulty + phase)
 * - Impact creates a crater (breaks blocks in radius)
 * - Spawns COTH/infested blocks at impact site
 * - Spawns parasite entities at impact site
 * - Creates a new colony at impact site (for medium+ meteors)
 * - Impact particles and sound effects
 * - Warning before impact (visual/audio cue)
 * - Meteor size varies (small=1, medium=2, large=3) based on evolution phase
 * - Large meteors create permanent infestation zones (on NIGHTMARE/OBLIVION)
 * - Meteor frequency increases with EvoPhase and difficulty
 * - Meteors can be disabled via config
 */
public class EntityMeteor extends EntityOrbBase {

    // ========== SynchedEntityData Accessors ==========
    private static final EntityDataAccessor<Integer> DATA_METEOR_SIZE =
            SynchedEntityData.defineId(EntityMeteor.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_EXPLODED =
            SynchedEntityData.defineId(EntityMeteor.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_IMPACT_TIMER =
            SynchedEntityData.defineId(EntityMeteor.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WARNING_TICKS =
            SynchedEntityData.defineId(EntityMeteor.class, EntityDataSerializers.INT);

    // ========== Core Fields ==========
    private int meteorSize = 1; // 1=small, 2=medium, 3=large
    private boolean hasExploded = false;
    private int impactTimer = 0;

    /** Ticks remaining before impact for the warning system. 0 = no warning / already past. */
    private int warningTicks = 0;

    /** Whether this meteor should create a permanent infestation zone. */
    private boolean permanentInfestation = false;

    // ========== Damage / Radius Constants ==========
    private static final float BASE_DAMAGE_SMALL = 20.0f;
    private static final float BASE_DAMAGE_MEDIUM = 40.0f;
    private static final float BASE_DAMAGE_LARGE = 80.0f;
    private static final double EXPLOSION_RADIUS_SMALL = 4.0;
    private static final double EXPLOSION_RADIUS_MEDIUM = 7.0;
    private static final double EXPLOSION_RADIUS_LARGE = 12.0;
    private static final int FIRE_DURATION = 100; // ticks

    /** Number of COTH/infested blocks to place at impact. */
    private static final int COTH_COUNT_SMALL = 5;
    private static final int COTH_COUNT_MEDIUM = 12;
    private static final int COTH_COUNT_LARGE = 25;

    /** Number of parasite entities to spawn at impact. */
    private static final int PARASITE_COUNT_SMALL = 1;
    private static final int PARASITE_COUNT_MEDIUM = 3;
    private static final int PARASITE_COUNT_LARGE = 6;

    // ========== Constructors ==========

    public EntityMeteor(EntityType<? extends EntityMeteor> type, Level level) {
        super(type, level);
        this.orbType = 3; // Meteor orb type
        this.setNoGravity(true); // Meteors follow their own trajectory
    }

    /**
     * Creates a meteor shot by a living entity (e.g. boss attack).
     */
    public EntityMeteor(Level level, LivingEntity shooter, int size) {
        super(level, shooter, 3, size); // Type 3 = Meteor
        this.meteorSize = Math.max(1, Math.min(3, size));
        this.damageMultiplier = 1.0F + (size * 0.5F);
        this.setNoGravity(true);

        // Set velocity for downward trajectory
        Vec3 motion = new Vec3(0, -2.5, 0);
        this.setDeltaMovement(motion);
    }

    /**
     * Creates a meteor at a specific position with a given size.
     * This is the primary constructor used by the meteor spawning system.
     *
     * @param level the level
     * @param x     spawn X
     * @param y     spawn Y (typically 200-256)
     * @param z     spawn Z
     * @param size  meteor size (1=small, 2=medium, 3=large)
     */
    public EntityMeteor(Level level, double x, double y, double z, int size) {
        this(level, x, y, z, size, false);
    }

    /**
     * Creates a meteor at a specific position with a given size and optional
     * permanent infestation flag.
     *
     * @param level                the level
     * @param x                    spawn X
     * @param y                    spawn Y
     * @param z                    spawn Z
     * @param size                 meteor size (1-3)
     * @param permanentInfestation if true, large meteor creates a permanent infestation zone
     */
    public EntityMeteor(Level level, double x, double y, double z, int size, boolean permanentInfestation) {
        this((EntityType<? extends EntityMeteor>) ModEntities.PROJECTILE_METEOR.get(), level);
        this.setPos(x, y, z);
        this.meteorSize = Math.max(1, Math.min(3, size));
        this.damageMultiplier = 1.0F + (size * 0.5F);
        this.permanentInfestation = permanentInfestation;
        this.setNoGravity(true);

        Vec3 motion = new Vec3(0, -3.0, 0);
        this.setDeltaMovement(motion);

        // Warning ticks: larger meteors give more warning time
        this.warningTicks = switch (this.meteorSize) {
            case 3 -> 100; // 5 seconds warning for large
            case 2 -> 60;  // 3 seconds for medium
            default -> 30; // 1.5 seconds for small
        };
    }

    // ========== Data Registration ==========

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_METEOR_SIZE, 1);
        this.entityData.define(DATA_EXPLODED, false);
        this.entityData.define(DATA_IMPACT_TIMER, 0);
        this.entityData.define(DATA_WARNING_TICKS, 0);
    }

    // ========== NBT Persistence ==========

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("MeteorSize", this.meteorSize);
        compound.putBoolean("HasExploded", this.hasExploded);
        compound.putInt("ImpactTimer", this.impactTimer);
        compound.putInt("WarningTicks", this.warningTicks);
        compound.putBoolean("PermanentInfestation", this.permanentInfestation);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("MeteorSize")) {
            this.meteorSize = compound.getInt("MeteorSize");
        }
        if (compound.contains("HasExploded")) {
            this.hasExploded = compound.getBoolean("HasExploded");
        }
        if (compound.contains("ImpactTimer")) {
            this.impactTimer = compound.getInt("ImpactTimer");
        }
        if (compound.contains("WarningTicks")) {
            this.warningTicks = compound.getInt("WarningTicks");
        }
        if (compound.contains("PermanentInfestation")) {
            this.permanentInfestation = compound.getBoolean("PermanentInfestation");
        }
    }

    // ========== Main Tick ==========

    @Override
    public void tick() {
        super.tick();

        if (hasExploded) {
            this.discard();
            return;
        }

        impactTimer++;

        // Server-side logic
        if (!level().isClientSide) {
            // Warning system: alert nearby players before impact
            if (warningTicks > 0) {
                tickWarning();
            }

            // Check for impact: ground collision or timeout
            if (this.onGround() || impactTimer > 400) {
                explode();
            }
        }

        // Client-side effects
        if (level().isClientSide) {
            spawnMeteorParticles();

            // Warning visual: red streak particles when in warning phase
            if (warningTicks > 0) {
                spawnWarningParticles();
            }
        }
    }

    // ========== Warning System ==========

    /**
     * Ticks the warning system. Plays a gradually intensifying sound
     * and visual cue to alert players of the incoming meteor.
     */
    private void tickWarning() {
        warningTicks--;

        // Play escalating warning sounds
        if (warningTicks > 0 && warningTicks % 20 == 0) {
            // Every second, play a warning sound that gets louder as impact nears
            float intensity = 1.0f - ((float) warningTicks / (warningTicks + impactTimer));
            float volume = 0.5f + intensity * 1.5f;
            float pitch = 0.5f + intensity * 1.0f;

            level().playSound(null, getX(), getY(), getZ(),
                    ModSounds.PROJECTILE_FLY.get(), SoundSource.HOSTILE,
                    volume, pitch);
        }
    }

    private void spawnWarningParticles() {
        // Red dust particles as warning indicator
        for (int i = 0; i < 3; i++) {
            level().addParticle(new DustParticleOptions(new Vector3f(1.0F, 0.2F, 0.0F), 1.0F),
                    this.getX() + (random.nextDouble() - 0.5) * 2.0,
                    this.getY() + random.nextDouble() * 2.0,
                    this.getZ() + (random.nextDouble() - 0.5) * 2.0,
                    0.0, 0.0, 0.0);
        }
    }

    // ========== Explosion Logic ==========

    private void explode() {
        if (level().isClientSide || hasExploded) {
            return;
        }

        hasExploded = true;
        entityData.set(DATA_EXPLODED, true);

        ServerLevel serverLevel = (ServerLevel) level();

        // Play explosion sound
        playImpactSound();

        // Spawn explosion particles
        spawnImpactParticles();

        // Apply AOE damage to nearby entities
        applyExplosionDamage();

        // Create crater (terrain deformation - block breaking)
        createCrater(serverLevel);

        // Place COTH/infested blocks at impact site
        placeCOTHBlocks(serverLevel);

        // Create fire effects at impact site
        createFireEffects(serverLevel);

        // Spawn parasite entities at impact site
        spawnParasites(serverLevel);

        // Create colony at impact site (medium+ meteors)
        if (meteorSize >= 2) {
            createColony(serverLevel);
        }

        // Fire game event for sculk sensors etc.
        serverLevel.gameEvent(this, GameEvent.EXPLODE, this.blockPosition());

        SubspaceParasite.LOGGER.debug("Meteor impact at [{}, {}, {}] size={} permanent={}",
                (int) getX(), (int) getY(), (int) getZ(), meteorSize, permanentInfestation);

        this.discard();
    }

    // ========== Explosion Damage ==========

    private void applyExplosionDamage() {
        double explosionRadius = getExplosionRadius();
        AABB explosionBox = getBoundingBox().inflate(explosionRadius);

        LivingEntity ownerEntity = getOwner() instanceof LivingEntity le ? le : null;
        List<LivingEntity> affectedEntities = level().getNearbyEntities(
                LivingEntity.class,
                TargetingConditions.DEFAULT,
                ownerEntity,
                explosionBox
        );

        for (LivingEntity entity : affectedEntities) {
            // Parasites are immune to meteor damage
            if (entity instanceof EntityParasiteBase) {
                continue;
            }

            // Calculate damage based on distance from impact center
            double distance = entity.position().distanceTo(position());
            double damageFactor = 1.0 - (distance / explosionRadius);

            if (damageFactor > 0) {
                float baseDamage = getBaseDamage();
                float damage = baseDamage * (float) damageFactor * damageMultiplier;
                entity.hurt(damageSources().explosion(this, getOwner()), damage);

                // Strong knockback away from impact point
                Vec3 knockbackDir = entity.position().subtract(position()).normalize();
                double knockbackStrength = 2.0 + (meteorSize * 0.5);
                entity.setDeltaMovement(entity.getDeltaMovement().add(
                        knockbackDir.x * knockbackStrength,
                        knockbackStrength * 0.5,
                        knockbackDir.z * knockbackStrength
                ));

                // Set on fire
                entity.setSecondsOnFire(FIRE_DURATION / 20);
            }
        }
    }

    // ========== Crater Creation ==========

    /**
     * Creates a crater at the impact site by removing blocks in a sphere.
     * The crater size is proportional to the meteor size.
     */
    private void createCrater(ServerLevel level) {
        double craterRadius = getExplosionRadius() * 0.6;
        int craterDepth = meteorSize * 2;
        BlockPos center = BlockPos.containing(getX(), getY(), getZ());

        int radiusInt = (int) Math.ceil(craterRadius);
        for (int dx = -radiusInt; dx <= radiusInt; dx++) {
            for (int dy = -craterDepth; dy <= craterDepth; dy++) {
                for (int dz = -radiusInt; dz <= radiusInt; dz++) {
                    double distSq = (dx * dx + (dy * dy * 0.5) + dz * dz);
                    double radiusSq = craterRadius * craterRadius;

                    if (distSq <= radiusSq) {
                        BlockPos targetPos = center.offset(dx, dy, dz);
                        BlockState state = level.getBlockState(targetPos);

                        // Don't break bedrock or other unbreakable blocks
                        if (!state.isAir() && state.getBlock() != Blocks.BEDROCK
                                && state.getDestroySpeed(level, targetPos) >= 0
                                && state.getDestroySpeed(level, targetPos) < 100) {
                            // Drop loot for some blocks, destroy others
                            if (random.nextFloat() < 0.3f) {
                                Block.dropResources(state, level, targetPos, null);
                            }
                            level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    }
                }
            }
        }
    }

    // ========== COTH Block Placement ==========

    /**
     * Places COTH/infested blocks at and around the impact site.
     * The number and spread depends on meteor size.
     */
    private void placeCOTHBlocks(ServerLevel level) {
        int cothCount = switch (meteorSize) {
            case 1 -> COTH_COUNT_SMALL;
            case 2 -> COTH_COUNT_MEDIUM;
            default -> COTH_COUNT_LARGE;
        };

        double spreadRadius = getExplosionRadius() * 0.5;
        BlockPos center = BlockPos.containing(getX(), getY(), getZ());

        // Use infested stain as the primary COTH surface block
        Block cothBlock = ModBlocks.INFESTED_STAIN.get();
        Block biomassBlock = ModBlocks.BIOMASS_BLOCK.get();
        Block parasiteFog = ModBlocks.PARASITE_FOG.get();

        for (int i = 0; i < cothCount; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = random.nextDouble() * spreadRadius;
            int dx = (int) Math.round(Math.cos(angle) * radius);
            int dz = (int) Math.round(Math.sin(angle) * radius);

            BlockPos targetPos = center.offset(dx, 0, dz);

            // Find the ground level at this position
            BlockPos groundPos = findGround(level, targetPos);
            if (groundPos == null) continue;

            // Place COTH block on the ground surface
            BlockPos surfacePos = groundPos.above();
            if (level.getBlockState(surfacePos).isAir()) {
                Block blockToPlace;
                float roll = random.nextFloat();
                if (roll < 0.6f) {
                    blockToPlace = cothBlock;
                } else if (roll < 0.85f) {
                    blockToPlace = biomassBlock;
                } else {
                    blockToPlace = parasiteFog;
                }
                level.setBlock(surfacePos, blockToPlace.defaultBlockState(), Block.UPDATE_ALL);
            }

            // For permanent infestation, also replace ground blocks with infested variants
            if (permanentInfestation && random.nextFloat() < 0.4f) {
                BlockState groundState = level.getBlockState(groundPos);
                if (groundState.is(Blocks.GRASS_BLOCK) || groundState.is(Blocks.DIRT)
                        || groundState.is(Blocks.STONE) || groundState.is(Blocks.COBBLESTONE)) {
                    level.setBlock(groundPos, cothBlock.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    /**
     * Finds the highest solid ground block at the given XZ position.
     */
    private BlockPos findGround(ServerLevel level, BlockPos pos) {
        for (int y = level.getMaxBuildHeight(); y >= level.getMinBuildHeight(); y--) {
            BlockPos checkPos = pos.atY(y);
            if (!level.getBlockState(checkPos).isAir() && level.getBlockState(checkPos).isSolid()) {
                return checkPos;
            }
        }
        return null;
    }

    // ========== Fire Effects ==========

    /**
     * Places fire blocks around the impact site.
     */
    private void createFireEffects(ServerLevel level) {
        double fireRadius = getExplosionRadius() * 0.5;
        int fireCount = meteorSize * 4;
        BlockPos center = BlockPos.containing(getX(), getY(), getZ());

        for (int i = 0; i < fireCount; i++) {
            double angle = (i / (double) fireCount) * Math.PI * 2;
            double radius = random.nextDouble() * fireRadius;
            int dx = (int) Math.round(Math.cos(angle) * radius);
            int dz = (int) Math.round(Math.sin(angle) * radius);

            BlockPos targetPos = center.offset(dx, 0, dz);
            BlockPos groundPos = findGround(level, targetPos);
            if (groundPos == null) continue;

            BlockPos firePos = groundPos.above();
            if (level.getBlockState(firePos).isAir()) {
                level.setBlock(firePos, BaseFireBlock.getState(level, firePos), Block.UPDATE_ALL);
            }
        }
    }

    // ========== Parasite Spawning ==========

    /**
     * Spawns parasite entities at the impact site.
     * The type and count of parasites depends on the evolution phase
     * and meteor size.
     */
    private void spawnParasites(ServerLevel level) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        int count = switch (meteorSize) {
            case 1 -> PARASITE_COUNT_SMALL;
            case 2 -> PARASITE_COUNT_MEDIUM;
            default -> PARASITE_COUNT_LARGE;
        };

        // Determine which parasites to spawn based on current phase
        EvoPhase currentPhase = EvoPhase.PHASE_0;
        if (level instanceof ServerLevel sl) {
            currentPhase = ModWorldData.get(sl).getCurrentPhase();
        }

        for (int i = 0; i < count; i++) {
            spawnRandomParasite(serverLevel, currentPhase);
        }
    }

    /**
     * Spawns a single random parasite appropriate for the current phase.
     */
    private void spawnRandomParasite(ServerLevel level, EvoPhase phase) {
        EntityType<? extends net.minecraft.world.entity.Mob> entityType = null;

        // Select entity type based on evolution phase
        // Phases: 0=Pre-assimilation, 1=Phase 1, 2=Phase 2, 3=Phase 3, 4=Phase 4
        if (phase.isAtLeast(EvoPhase.PHASE_3)) {
            // Phase 3/4: spawn adapted or primitive types
            entityType = random.nextBoolean()
                    ? (EntityType<? extends net.minecraft.world.entity.Mob>) ModEntities.INFECTED_ZOMBIE.get()
                    : (EntityType<? extends net.minecraft.world.entity.Mob>) ModEntities.PRIMITIVE_NOGLA.get();
        } else if (phase.isAtLeast(EvoPhase.PHASE_2)) {
            // Phase 2: spawn primitive types
            entityType = (EntityType<? extends net.minecraft.world.entity.Mob>) ModEntities.PRIMITIVE_BANO.get();
        } else {
            // Pre-assimilation/Phase 1: spawn basic infected
            entityType = (EntityType<? extends net.minecraft.world.entity.Mob>) ModEntities.INFECTED_ZOMBIE.get();
        }

        if (entityType != null) {
            double offsetX = (random.nextDouble() - 0.5) * 4.0;
            double offsetZ = (random.nextDouble() - 0.5) * 4.0;
            BlockPos spawnPos = BlockPos.containing(getX() + offsetX, getY() + 1, getZ() + offsetZ);

            net.minecraft.world.entity.Mob entity = entityType.create(level);
            if (entity != null) {
                entity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                        random.nextFloat() * 360.0f, 0.0f);
                entity.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos),
                        MobSpawnType.EVENT, null, null);
                level.addFreshEntity(entity);
            }
        }
    }

    // ========== Colony Creation ==========

    /**
     * Creates a new colony at the impact site.
     * Only called for medium and large meteors.
     */
    private void createColony(ServerLevel level) {
        if (!ModConfig.COLONY.colonySystemEnabled.get()) return;

        BlockPos impactPos = BlockPos.containing(getX(), getY(), getZ());
        ModWorldData worldData = ModWorldData.get(level);

        // Check colony count limit
        int maxColonies = ModConfig.COLONY.maxColonies.get();
        if (worldData.getColonyCount() >= maxColonies) return;

        // Check minimum spacing from existing colonies
        int minSpacing = ModConfig.COLONY.minColonySpacing.get();
        for (BlockPos existing : worldData.getColonyCenters()) {
            if (existing.distManhattan(impactPos) < minSpacing) {
                return; // Too close to existing colony
            }
        }

        // Place a colony heart block at the impact site
        BlockPos heartPos = findGround(level, impactPos);
        if (heartPos != null) {
            BlockPos surfacePos = heartPos.above();
            if (level.getBlockState(surfacePos).canBeReplaced()) {
                level.setBlock(surfacePos, ModBlocks.COLONY_HEART.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    // ========== Particle Effects ==========

    private void spawnMeteorParticles() {
        // Trail particles during descent
        for (int i = 0; i < 8; i++) {
            level().addParticle(ModParticleTypes.VOID_ORB.get(),
                    this.getX() + (random.nextDouble() - 0.5) * getBbWidth(),
                    this.getY() + random.nextDouble() * getBbHeight(),
                    this.getZ() + (random.nextDouble() - 0.5) * getBbWidth(),
                    (random.nextDouble() - 0.5) * 0.2,
                    0.3,
                    (random.nextDouble() - 0.5) * 0.2);
        }

        // Smoke trail
        for (int i = 0; i < 4; i++) {
            level().addParticle(ParticleTypes.SMOKE,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    0.0, 0.1, 0.0);
        }
    }

    @Override
    protected void spawnChargingParticles() {
        // Glowing hot particles
        for (int i = 0; i < 6; i++) {
            level().addParticle(ParticleTypes.FLAME,
                    this.getX() + (random.nextDouble() - 0.5) * getBbWidth(),
                    this.getY() + random.nextDouble() * getBbHeight(),
                    this.getZ() + (random.nextDouble() - 0.5) * getBbWidth(),
                    0.0, 0.05, 0.0);
        }
    }

    @Override
    protected void spawnTrailParticles() {
        // Already handled in spawnMeteorParticles
    }

    @Override
    protected void spawnImpactParticles() {
        // Massive explosion particles
        int particleCount = 50 + (meteorSize * 20);

        for (int i = 0; i < particleCount; i++) {
            double speed = 2.0 + random.nextDouble() * 3.0;
            level().addParticle(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    (random.nextDouble() - 0.5) * speed,
                    (random.nextDouble() - 0.5) * speed,
                    (random.nextDouble() - 0.5) * speed);
        }

        // Additional fire particles
        for (int i = 0; i < 30; i++) {
            level().addParticle(ParticleTypes.FLAME,
                    this.getX() + (random.nextDouble() - 0.5) * 5,
                    this.getY() + (random.nextDouble() - 0.5) * 5,
                    this.getZ() + (random.nextDouble() - 0.5) * 5,
                    (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.5);
        }

        // Void/celestial particles
        for (int i = 0; i < 20; i++) {
            level().addParticle(ModParticleTypes.VOID_ORB.get(),
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    (random.nextDouble() - 0.5) * 1.5,
                    (random.nextDouble() - 0.5) * 1.5,
                    (random.nextDouble() - 0.5) * 1.5);
        }

        // Green spore particles for COTH spread visual
        if (meteorSize >= 2) {
            for (int i = 0; i < 15; i++) {
                level().addParticle(ParticleTypes.HAPPY_VILLAGER,
                        this.getX() + (random.nextDouble() - 0.5) * 8,
                        this.getY() + random.nextDouble() * 3,
                        this.getZ() + (random.nextDouble() - 0.5) * 8,
                        0.0, 0.05, 0.0);
            }
        }
    }

    // ========== Sound Effects ==========

    @Override
    protected void playImpactSound() {
        float volume = 1.5f + meteorSize * 0.5f;
        float pitch = 0.6f + random.nextFloat() * 0.2f;
        level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.PROJECTILE_HIT.get(), SoundSource.HOSTILE,
                volume, pitch);
    }

    // ========== Collision Handling ==========

    @Override
    protected void onHit(HitResult result) {
        if (!level().isClientSide && !hasExploded) {
            explode();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        // Direct hit before explosion
        if (!hasExploded && result.getEntity() instanceof LivingEntity livingTarget) {
            // Don't damage parasites with direct hits
            if (livingTarget instanceof EntityParasiteBase) return;
            float damage = getBaseDamage() * 0.3f * damageMultiplier;
            livingTarget.hurt(damageSources().fall(), damage);
        }
    }

    // ========== Getters and Setters ==========

    public int getMeteorSize() {
        return this.entityData.get(DATA_METEOR_SIZE);
    }

    public void setMeteorSize(int size) {
        this.meteorSize = Math.max(1, Math.min(3, size));
        this.entityData.set(DATA_METEOR_SIZE, this.meteorSize);
        this.damageMultiplier = 1.0F + (this.meteorSize * 0.5F);
    }

    public boolean hasExploded() {
        return this.entityData.get(DATA_EXPLODED);
    }

    public boolean isPermanentInfestation() {
        return permanentInfestation;
    }

    public void setPermanentInfestation(boolean permanent) {
        this.permanentInfestation = permanent;
    }

    public int getWarningTicks() {
        return warningTicks;
    }

    // ========== Utility Methods ==========

    @Override
    protected float getBaseDamage() {
        return switch (meteorSize) {
            case 1 -> BASE_DAMAGE_SMALL;
            case 2 -> BASE_DAMAGE_MEDIUM;
            default -> BASE_DAMAGE_LARGE;
        };
    }

    private double getExplosionRadius() {
        return switch (meteorSize) {
            case 1 -> EXPLOSION_RADIUS_SMALL;
            case 2 -> EXPLOSION_RADIUS_MEDIUM;
            default -> EXPLOSION_RADIUS_LARGE;
        };
    }

    /**
     * Determines the appropriate meteor size based on the current evolution phase
     * and difficulty setting.
     *
     * @param phase       current evolution phase
     * @param difficulty  current SRP difficulty setting
     * @return a meteor size (1=small, 2=medium, 3=large)
     */
    public static int determineMeteorSize(EvoPhase phase, SRPDifficultySetting difficulty) {
        int maxAllowed = difficulty.getMaxMeteorSize();
        float rand = SubspaceParasite.RANDOM.nextFloat();

        // Large meteors only at Apex phase (Phase 4)
        if (phase.isAtLeast(EvoPhase.PHASE_4) && maxAllowed >= 3 && rand < 0.15f) {
            return 3;
        }
        // Medium meteors from Evolved phase (Phase 3)
        if (phase.isAtLeast(EvoPhase.PHASE_3) && maxAllowed >= 2 && rand < 0.4f) {
            return 2;
        }
        return 1;
    }
}
