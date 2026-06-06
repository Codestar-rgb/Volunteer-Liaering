package com.subspaceparasite.core;

import com.subspaceparasite.common.block.entity.BiomeHeartBlockEntity;
import com.subspaceparasite.common.block.entity.ColonyHeartBlockEntity;
import com.subspaceparasite.common.block.entity.ColonyOutpostBlockEntity;
import com.subspaceparasite.common.block.entity.EvolutionLureBlockEntity;
import com.subspaceparasite.common.block.entity.InfestedFurnaceBlockEntity;
import com.subspaceparasite.common.block.entity.InfuserFurnaceBlockEntity;
import com.subspaceparasite.common.block.entity.NodeLampBlockEntity;
import com.subspaceparasite.common.block.entity.NodeRelayBlockEntity;
import com.subspaceparasite.common.block.entity.ParasiteCanisterBlockEntity;
import com.subspaceparasite.common.block.entity.ParasiteLootBlockEntity;
import com.subspaceparasite.common.block.entity.RelayControllerBlockEntity;
import com.subspaceparasite.common.entity.monster.deterrent.EntityVenkrolSIV;
import com.subspaceparasite.common.entity.PlaceholderBlockEntity;
import com.subspaceparasite.common.entity.monster.assimilated.EntityAssimilatedHuman;
import com.subspaceparasite.common.entity.monster.assimilated.EntityAssimilatedCow;
import com.subspaceparasite.common.entity.monster.assimilated.EntityAssimilatedSheep;
import com.subspaceparasite.common.entity.monster.crude.EntityMovingFlesh;
import com.subspaceparasite.common.entity.monster.crude.EntityWorker;
import com.subspaceparasite.common.entity.monster.crude.EntityCruxA;
import com.subspaceparasite.common.entity.monster.crude.EntityCruxB;
import com.subspaceparasite.common.entity.monster.crude.EntityDone;
import com.subspaceparasite.common.entity.monster.crude.EntityHeed;
import com.subspaceparasite.common.entity.monster.crude.EntityHost;
import com.subspaceparasite.common.entity.monster.crude.EntityInhooM;
import com.subspaceparasite.common.entity.monster.crude.EntityInhooS;
import com.subspaceparasite.common.entity.monster.crude.EntityLeer;
import com.subspaceparasite.common.entity.monster.crude.EntityMes;
import com.subspaceparasite.common.entity.monster.crude.EntityQuac;
import com.subspaceparasite.common.entity.monster.feral.EntityFeralHuman;
import com.subspaceparasite.common.entity.monster.feral.EntityFeralCow;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedHuman;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedCow;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedSheep;
import com.subspaceparasite.common.entity.monster.inborn.EntityGothol;
import com.subspaceparasite.common.entity.monster.inborn.EntityKol;
import com.subspaceparasite.common.entity.monster.inborn.EntityLesh;
import com.subspaceparasite.common.entity.monster.inborn.EntityLodo;
import com.subspaceparasite.common.entity.monster.inborn.EntityMor;
import com.subspaceparasite.common.entity.monster.inborn.EntityMudo;
import com.subspaceparasite.common.entity.monster.inborn.EntityNuuh;
import com.subspaceparasite.common.entity.monster.inborn.EntityRathol;
import com.subspaceparasite.common.entity.monster.inborn.EntityViin;
import com.subspaceparasite.common.entity.monster.inborn.EntityAta;
import com.subspaceparasite.common.entity.monster.inborn.EntityButhol;
import com.subspaceparasite.common.entity.monster.primitive.EntityBano;
import com.subspaceparasite.common.entity.monster.primitive.EntityCanra;
import com.subspaceparasite.common.entity.monster.primitive.EntityEmana;
import com.subspaceparasite.common.entity.monster.primitive.EntityGim;
import com.subspaceparasite.common.entity.monster.primitive.EntityHull;
import com.subspaceparasite.common.entity.monster.primitive.EntityIki;
import com.subspaceparasite.common.entity.monster.primitive.EntityLum;
import com.subspaceparasite.common.entity.monster.primitive.EntityNogla;
import com.subspaceparasite.common.entity.monster.primitive.EntityRanrac;
import com.subspaceparasite.common.entity.monster.primitive.EntityShyco;
import com.subspaceparasite.common.entity.monster.primitive.EntityWymo;
import com.subspaceparasite.common.entity.monster.primitive.EntityZaa;
import com.subspaceparasite.common.entity.monster.pure.EntityFlam;
import com.subspaceparasite.common.entity.monster.pure.EntityFlog;
import com.subspaceparasite.common.entity.monster.pure.EntityOmboo;
import com.subspaceparasite.common.entity.monster.pure.EntityAlafha;
import com.subspaceparasite.common.entity.monster.pure.EntityGanro;
import com.subspaceparasite.common.entity.monster.pure.EntityEsor;
import com.subspaceparasite.common.entity.monster.pure.EntityElvia;
import com.subspaceparasite.common.entity.monster.pure.EntityAnged;
import com.subspaceparasite.common.entity.monster.adapted.EntityBanoAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityCanraAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityEmanaAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityGimAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityHullAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityIkiAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityLumAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityNoglaAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityRanracAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityShycoAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityWymoAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityZaaAdapted;
import com.subspaceparasite.common.entity.projectile.EntityMeteor;
import com.subspaceparasite.common.entity.projectile.EntityOrbBase;
import com.subspaceparasite.common.entity.projectile.EntityOrbScary;
import com.subspaceparasite.common.entity.projectile.EntityOrbVoid;
import com.subspaceparasite.common.entity.monster.abomination.EntityAbominationAmalgam;
import com.subspaceparasite.common.entity.monster.abomination.EntityAbominationChimera;
import com.subspaceparasite.common.entity.monster.abomination.EntityAbominationHydra;
import com.subspaceparasite.common.entity.monster.adapted.EntityColonyAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityCreeperAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityEndermanAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntitySkeletonAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntitySpiderAdapted;
import com.subspaceparasite.common.entity.monster.adapted.EntityZombieAdapted;
import com.subspaceparasite.common.entity.monster.ancient.EntityAncientColossus;
import com.subspaceparasite.common.entity.monster.ancient.EntityAncientDreadnought;
import com.subspaceparasite.common.entity.monster.ancient.EntityAncientLeviathan;
import com.subspaceparasite.common.entity.monster.beckon.EntityBeckonCommon;
import com.subspaceparasite.common.entity.monster.beckon.EntityBeckonEpic;
import com.subspaceparasite.common.entity.monster.beckon.EntityBeckonLegendary;
import com.subspaceparasite.common.entity.monster.beckon.EntityBeckonRare;
import com.subspaceparasite.common.entity.monster.beckon.EntityBeckonUncommon;
import com.subspaceparasite.common.entity.monster.crude.EntityEgas;
import com.subspaceparasite.common.entity.monster.crude.EntityMindim;
import com.subspaceparasite.common.entity.monster.crude.EntityScorcher;
import com.subspaceparasite.common.entity.monster.derived.EntityDerivedCrawler;
import com.subspaceparasite.common.entity.monster.derived.EntityDerivedFly;
import com.subspaceparasite.common.entity.monster.derived.EntityDerivedLeaper;
import com.subspaceparasite.common.entity.monster.derived.EntityDerivedStalker;
import com.subspaceparasite.common.entity.monster.derived.EntityDerivedSwarm;
import com.subspaceparasite.common.entity.monster.deterrent.EntityDeterrentBastion;
import com.subspaceparasite.common.entity.monster.deterrent.EntityDeterrentOutpost;
import com.subspaceparasite.common.entity.monster.deterrent.EntityDeterrentSentry;
import com.subspaceparasite.common.entity.monster.dispatcher.EntityDispatcherCommon;
import com.subspaceparasite.common.entity.monster.dispatcher.EntityDispatcherEpic;
import com.subspaceparasite.common.entity.monster.dispatcher.EntityDispatcherRare;
import com.subspaceparasite.common.entity.monster.dispatcher.EntityDispatcherUncommon;
import com.subspaceparasite.common.entity.monster.dispatcher.EntityDodT;
import com.subspaceparasite.common.entity.monster.feral.EntityFeralCreeper;
import com.subspaceparasite.common.entity.monster.feral.EntityFeralEnderman;
import com.subspaceparasite.common.entity.monster.feral.EntityFeralIronGolem;
import com.subspaceparasite.common.entity.monster.feral.EntityFeralSkeleton;
import com.subspaceparasite.common.entity.monster.feral.EntityFeralSpider;
import com.subspaceparasite.common.entity.monster.feral.EntityFeralWolf;
import com.subspaceparasite.common.entity.monster.feral.EntityFeralZombie;
import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedCreeper;
import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedEnderman;
import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedEvoker;
import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedPillager;
import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedRavager;
import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedSkeleton;
import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedSpider;
import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedWitch;
import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedZombie;
import com.subspaceparasite.common.entity.monster.inborn.EntityAlafin;
import com.subspaceparasite.common.entity.monster.inborn.EntityCanal;
import com.subspaceparasite.common.entity.monster.inborn.EntityNormas;
import com.subspaceparasite.common.entity.monster.inborn.EntityObus;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedBat;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedBee;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedBlaze;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedCaveSpider;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedChicken;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedCreeper;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedDrowned;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedEnderman;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedEvoker;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedFox;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedGhast;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedHorse;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedHusk;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedIronGolem;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedLlama;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedMooshroom;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedPanda;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedPhantom;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedPig;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedPillager;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedPolarBear;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedRavager;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedSkeleton;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedSnowGolem;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedSpider;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedStray;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedVillager;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedWarden;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedWitch;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedWitherSkeleton;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedWolf;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedZombie;
import com.subspaceparasite.common.entity.monster.misc.EntityAlveoliWorm;
import com.subspaceparasite.common.entity.monster.misc.EntityBoomOrb;
import com.subspaceparasite.common.entity.monster.misc.EntityBuglin;
import com.subspaceparasite.common.entity.monster.misc.EntityParasiteLarva;
import com.subspaceparasite.common.entity.monster.misc.EntityVoidOrb;
import com.subspaceparasite.common.entity.monster.nexus.EntityNak;
import com.subspaceparasite.common.entity.monster.nexus.EntityTonro;
import com.subspaceparasite.common.entity.monster.nexus.EntityUnvo;
import com.subspaceparasite.common.entity.monster.preeminent.EntityPreeminentMarauder;
import com.subspaceparasite.common.entity.monster.preeminent.EntityPreeminentSovereign;
import com.subspaceparasite.common.entity.monster.preeminent.EntityPreeminentWarden;
import com.subspaceparasite.common.entity.monster.primitive.EntityArachnida;
import com.subspaceparasite.common.entity.monster.primitive.EntityBolster;
import com.subspaceparasite.common.entity.monster.primitive.EntityBomph;
import com.subspaceparasite.common.entity.monster.primitive.EntityBurrower;
import com.subspaceparasite.common.entity.monster.primitive.EntityDevourer;
import com.subspaceparasite.common.entity.monster.primitive.EntityLongarms;
import com.subspaceparasite.common.entity.monster.primitive.EntityManducater;
import com.subspaceparasite.common.entity.monster.primitive.EntityPrimitiveWolf;
import com.subspaceparasite.common.entity.monster.primitive.EntityReeker;
import com.subspaceparasite.common.entity.monster.primitive.EntitySummoner;
import com.subspaceparasite.common.entity.monster.primitive.EntityTozon;
import com.subspaceparasite.common.entity.monster.primitive.EntityVermin;
import com.subspaceparasite.common.entity.monster.primitive.EntityViscera;
import com.subspaceparasite.common.entity.monster.primitive.EntityYelloweye;
import com.subspaceparasite.common.entity.monster.projectile.EntityAcidSpit;
import com.subspaceparasite.common.entity.monster.projectile.EntityBeckonBlast;
import com.subspaceparasite.common.entity.monster.projectile.EntityBileBomb;
import com.subspaceparasite.common.entity.monster.projectile.EntityNexusBeam;
import com.subspaceparasite.common.entity.monster.projectile.EntityParasiteWeb;
import com.subspaceparasite.common.entity.monster.projectile.EntitySporeCloud;
import com.subspaceparasite.common.entity.monster.projectile.EntityVirulentShot;
import com.subspaceparasite.common.entity.monster.pure.EntityCreeperPure;
import com.subspaceparasite.common.entity.monster.pure.EntityEndermanPure;
import com.subspaceparasite.common.entity.monster.pure.EntitySkeletonPure;
import com.subspaceparasite.common.entity.monster.pure.EntitySpiderPure;
import com.subspaceparasite.common.entity.monster.pure.EntityWolfPure;
import com.subspaceparasite.common.entity.monster.pure.EntityZombiePure;
import com.subspaceparasite.common.entity.monster.rooter.EntityLeemB;
import com.subspaceparasite.common.entity.monster.rooter.EntityRooterCommon;
import com.subspaceparasite.common.entity.monster.rooter.EntityRooterEpic;
import com.subspaceparasite.common.entity.monster.rooter.EntityRooterRare;
import com.subspaceparasite.common.entity.monster.rooter.EntityRooterUncommon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.subspaceparasite.SubspaceParasite;
import net.minecraftforge.registries.RegistryObject;

/**
 * Entity and BlockEntity registry for the SubspaceParasite mod.
 * Contains ALL entities (~140+) from the original Scape and Run: Parasites mod.
 * All parasite entities now have specific implementations.
 * No longer uses EntityParasitePlaceholder for any entity type.
 */
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SubspaceParasite.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SubspaceParasite.MOD_ID);

    // Note: All placeholder entity helper methods have been removed.
    // Each entity type now uses its specific entity class directly.

    // Helper: block entity with placeholder
    @SafeVarargs
    private static RegistryObject<BlockEntityType<PlaceholderBlockEntity>> blockEntity(String name, RegistryObject<Block>... blocks) {
        return BLOCK_ENTITIES.register(name, () -> {
            Block[] blockArray = new Block[blocks.length];
            for (int i = 0; i < blocks.length; i++) {
                blockArray[i] = blocks[i].get();
            }
            BlockEntityType<PlaceholderBlockEntity>[] typeRef = new BlockEntityType[1];
            BlockEntityType<PlaceholderBlockEntity> result = BlockEntityType.Builder.of(
                    (pos, state) -> new PlaceholderBlockEntity(typeRef[0], pos, state),
                    blockArray
            ).build(null);
            typeRef[0] = result;
            return result;
        });
    }

    // Helper: typed block entity factory
    @FunctionalInterface
    private interface BEFactory<T extends net.minecraft.world.level.block.entity.BlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }

    @SafeVarargs
    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> RegistryObject<BlockEntityType<T>> typedBlockEntity(
            String name, BEFactory<T> factory, RegistryObject<? extends Block>... blocks) {
        return BLOCK_ENTITIES.register(name, () -> {
            Block[] blockArray = new Block[blocks.length];
            for (int i = 0; i < blocks.length; i++) {
                blockArray[i] = blocks[i].get();
            }
            BlockEntityType<T>[] typeRef = new BlockEntityType[1];
            BlockEntityType<T> result = BlockEntityType.Builder.of(
                    (pos, state) -> factory.create(typeRef[0], pos, state),
                    blockArray
            ).build(null);
            typeRef[0] = result;
            return result;
        });
    }

    // ================================================================
    // INFECTED ENTITIES (~20)
    // ================================================================
    public static final RegistryObject<EntityType<EntityInfectedCreeper>> INFECTED_CREEPER = ENTITIES.register("infectedcreeper", () -> EntityType.Builder.of(EntityInfectedCreeper::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedcreeper"));
    public static final RegistryObject<EntityType<EntityInfectedSkeleton>> INFECTED_SKELETON = ENTITIES.register("infectedskeleton", () -> EntityType.Builder.of(EntityInfectedSkeleton::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedskeleton"));
    public static final RegistryObject<EntityType<EntityInfectedZombie>> INFECTED_ZOMBIE = ENTITIES.register("infectedzombie", () -> EntityType.Builder.of(EntityInfectedZombie::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedzombie"));
    public static final RegistryObject<EntityType<EntityInfectedSpider>> INFECTED_SPIDER = ENTITIES.register("infectedspider", () -> EntityType.Builder.of(EntityInfectedSpider::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedspider"));
    public static final RegistryObject<EntityType<EntityInfectedEnderman>> INFECTED_ENDERMAN = ENTITIES.register("infectedenderman", () -> EntityType.Builder.of(EntityInfectedEnderman::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedenderman"));
    public static final RegistryObject<EntityType<EntityInfectedPig>> INFECTED_PIG = ENTITIES.register("infectedpig", () -> EntityType.Builder.of(EntityInfectedPig::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedpig"));
    public static final RegistryObject<EntityType<EntityInfectedHuman>> INFECTED_HUMAN = ENTITIES.register("infectedhuman", 
        () -> EntityType.Builder.of(EntityInfectedHuman::new, MobCategory.MONSTER).sized(0.6F, 1.8F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":infectedhuman"));
    public static final RegistryObject<EntityType<EntityInfectedCow>> INFECTED_COW = ENTITIES.register("infectedcow",
        () -> EntityType.Builder.of(EntityInfectedCow::new, MobCategory.MONSTER).sized(1.0F, 1.4F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":infectedcow"));
    public static final RegistryObject<EntityType<EntityInfectedSheep>> INFECTED_SHEEP = ENTITIES.register("infectedsheep",
        () -> EntityType.Builder.of(EntityInfectedSheep::new, MobCategory.MONSTER).sized(0.9F, 1.3F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":infectedsheep"));
    
    // Assimilated (Feral) entities
    public static final RegistryObject<EntityType<EntityAssimilatedHuman>> ASSIMILATED_HUMAN = ENTITIES.register("assimilatedhuman",
        () -> EntityType.Builder.of(EntityAssimilatedHuman::new, MobCategory.MONSTER).sized(0.6F, 1.8F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":assimilatedhuman"));
    public static final RegistryObject<EntityType<EntityAssimilatedCow>> ASSIMILATED_COW = ENTITIES.register("assimilatedcow",
        () -> EntityType.Builder.of(EntityAssimilatedCow::new, MobCategory.MONSTER).sized(1.0F, 1.4F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":assimilatedcow"));
    public static final RegistryObject<EntityType<EntityAssimilatedSheep>> ASSIMILATED_SHEEP = ENTITIES.register("assimilatedsheep",
        () -> EntityType.Builder.of(EntityAssimilatedSheep::new, MobCategory.MONSTER).sized(0.9F, 1.3F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":assimilatedsheep"));
    
    // Feral entities
    public static final RegistryObject<EntityType<EntityFeralCow>> FERAL_COW = ENTITIES.register("feralcow",
        () -> EntityType.Builder.of(EntityFeralCow::new, MobCategory.MONSTER).sized(1.0F, 1.4F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":feralcow"));
    public static final RegistryObject<EntityType<EntityFeralHuman>> FERAL_HUMAN = ENTITIES.register("feralhuman",
        () -> EntityType.Builder.of(EntityFeralHuman::new, MobCategory.MONSTER).sized(0.6F, 1.8F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":feralhuman"));
    public static final RegistryObject<EntityType<EntityInfectedChicken>> INFECTED_CHICKEN = ENTITIES.register("infectedchicken", () -> EntityType.Builder.of(EntityInfectedChicken::new, MobCategory.MONSTER).sized(0.6F, 0.6F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedchicken"));
    public static final RegistryObject<EntityType<EntityInfectedVillager>> INFECTED_VILLAGER = ENTITIES.register("infectedvillager", () -> EntityType.Builder.of(EntityInfectedVillager::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedvillager"));
    public static final RegistryObject<EntityType<EntityInfectedWolf>> INFECTED_WOLF = ENTITIES.register("infectedwolf", () -> EntityType.Builder.of(EntityInfectedWolf::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedwolf"));
    public static final RegistryObject<EntityType<EntityInfectedHorse>> INFECTED_HORSE = ENTITIES.register("infectedhorse", () -> EntityType.Builder.of(EntityInfectedHorse::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedhorse"));
    public static final RegistryObject<EntityType<EntityInfectedIronGolem>> INFECTED_IRON_GOLEM = ENTITIES.register("infectedirongolem", () -> EntityType.Builder.of(EntityInfectedIronGolem::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedirongolem"));
    public static final RegistryObject<EntityType<EntityInfectedSnowGolem>> INFECTED_SNOW_GOLEM = ENTITIES.register("infectedsnowgolem", () -> EntityType.Builder.of(EntityInfectedSnowGolem::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedsnowgolem"));
    public static final RegistryObject<EntityType<EntityInfectedBat>> INFECTED_BAT = ENTITIES.register("infectedbat", () -> EntityType.Builder.of(EntityInfectedBat::new, MobCategory.MONSTER).sized(0.3F, 0.3F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedbat"));
    public static final RegistryObject<EntityType<EntityInfectedBlaze>> INFECTED_BLAZE = ENTITIES.register("infectedblaze", () -> EntityType.Builder.of(EntityInfectedBlaze::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedblaze"));
    public static final RegistryObject<EntityType<EntityInfectedWitch>> INFECTED_WITCH = ENTITIES.register("infectedwitch", () -> EntityType.Builder.of(EntityInfectedWitch::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedwitch"));
    public static final RegistryObject<EntityType<EntityInfectedRavager>> INFECTED_RAVAGER = ENTITIES.register("infectedravager", () -> EntityType.Builder.of(EntityInfectedRavager::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedravager"));
    public static final RegistryObject<EntityType<EntityInfectedPillager>> INFECTED_PILLAGER = ENTITIES.register("infectedpillager", () -> EntityType.Builder.of(EntityInfectedPillager::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedpillager"));
    public static final RegistryObject<EntityType<EntityInfectedEvoker>> INFECTED_EVOKER = ENTITIES.register("infectedevoker", () -> EntityType.Builder.of(EntityInfectedEvoker::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedevoker"));

    // ================================================================
    // SPECIAL INFECTED
    // ================================================================
    public static final RegistryObject<EntityType<EntityInfectedGhast>> INFECTED_GHAST = ENTITIES.register("infectedghast", () -> EntityType.Builder.of(EntityInfectedGhast::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedghast"));
    public static final RegistryObject<EntityType<EntityInfectedPhantom>> INFECTED_PHANTOM = ENTITIES.register("infectedphantom", () -> EntityType.Builder.of(EntityInfectedPhantom::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedphantom"));
    public static final RegistryObject<EntityType<EntityInfectedWarden>> INFECTED_WARDEN = ENTITIES.register("infectedwarden", () -> EntityType.Builder.of(EntityInfectedWarden::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedwarden"));
    public static final RegistryObject<EntityType<EntityInfectedWitherSkeleton>> INFECTED_WITHER_SKELETON = ENTITIES.register("infectedwitherskeleton", () -> EntityType.Builder.of(EntityInfectedWitherSkeleton::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedwitherskeleton"));
    public static final RegistryObject<EntityType<EntityInfectedStray>> INFECTED_STRAY = ENTITIES.register("infectedstray", () -> EntityType.Builder.of(EntityInfectedStray::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedstray"));
    public static final RegistryObject<EntityType<EntityInfectedHusk>> INFECTED_HUSK = ENTITIES.register("infectedhusk", () -> EntityType.Builder.of(EntityInfectedHusk::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedhusk"));
    public static final RegistryObject<EntityType<EntityInfectedDrowned>> INFECTED_DROWNED = ENTITIES.register("infecteddrowned", () -> EntityType.Builder.of(EntityInfectedDrowned::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infecteddrowned"));
    public static final RegistryObject<EntityType<EntityInfectedCaveSpider>> INFECTED_CAVE_SPIDER = ENTITIES.register("infectedcavespider", () -> EntityType.Builder.of(EntityInfectedCaveSpider::new, MobCategory.MONSTER).sized(0.6F, 0.6F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedcavespider"));
    public static final RegistryObject<EntityType<EntityInfectedMooshroom>> INFECTED_MOOSHROOM = ENTITIES.register("infectedmooshroom", () -> EntityType.Builder.of(EntityInfectedMooshroom::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedmooshroom"));
    public static final RegistryObject<EntityType<EntityInfectedLlama>> INFECTED_LLAMA = ENTITIES.register("infectedllama", () -> EntityType.Builder.of(EntityInfectedLlama::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedllama"));
    public static final RegistryObject<EntityType<EntityInfectedPolarBear>> INFECTED_POLAR_BEAR = ENTITIES.register("infectedpolarbear", () -> EntityType.Builder.of(EntityInfectedPolarBear::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedpolarbear"));
    public static final RegistryObject<EntityType<EntityInfectedPanda>> INFECTED_PANDA = ENTITIES.register("infectedpanda", () -> EntityType.Builder.of(EntityInfectedPanda::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedpanda"));  // quadruped
    public static final RegistryObject<EntityType<EntityInfectedFox>> INFECTED_FOX = ENTITIES.register("infectedfox", () -> EntityType.Builder.of(EntityInfectedFox::new, MobCategory.MONSTER).sized(0.6F, 0.6F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedfox"));
    public static final RegistryObject<EntityType<EntityInfectedBee>> INFECTED_BEE = ENTITIES.register("infectedbee", () -> EntityType.Builder.of(EntityInfectedBee::new, MobCategory.MONSTER).sized(0.3F, 0.3F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":infectedbee"));

    // ================================================================
    // FERAL ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityFeralCreeper>> FERAL_CREEPER = ENTITIES.register("feralcreeper", () -> EntityType.Builder.of(EntityFeralCreeper::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":feralcreeper"));
    public static final RegistryObject<EntityType<EntityFeralSkeleton>> FERAL_SKELETON = ENTITIES.register("feralskeleton", () -> EntityType.Builder.of(EntityFeralSkeleton::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":feralskeleton"));
    public static final RegistryObject<EntityType<EntityFeralZombie>> FERAL_ZOMBIE = ENTITIES.register("feralzombie", () -> EntityType.Builder.of(EntityFeralZombie::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":feralzombie"));
    public static final RegistryObject<EntityType<EntityFeralSpider>> FERAL_SPIDER = ENTITIES.register("feralspider", () -> EntityType.Builder.of(EntityFeralSpider::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":feralspider"));
    public static final RegistryObject<EntityType<EntityFeralEnderman>> FERAL_ENDERMAN = ENTITIES.register("feralenderman", () -> EntityType.Builder.of(EntityFeralEnderman::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":feralenderman"));
    public static final RegistryObject<EntityType<EntityFeralWolf>> FERAL_WOLF = ENTITIES.register("feralwolf", () -> EntityType.Builder.of(EntityFeralWolf::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":feralwolf"));
    public static final RegistryObject<EntityType<EntityFeralIronGolem>> FERAL_IRON_GOLEM = ENTITIES.register("feralirongolem", () -> EntityType.Builder.of(EntityFeralIronGolem::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":feralirongolem"));

    // ================================================================
    // HIJACKED ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityHijackedCreeper>> HIJACKED_CREEPER = ENTITIES.register("hijackedcreeper", () -> EntityType.Builder.of(EntityHijackedCreeper::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":hijackedcreeper"));
    public static final RegistryObject<EntityType<EntityHijackedSkeleton>> HIJACKED_SKELETON = ENTITIES.register("hijackedskeleton", () -> EntityType.Builder.of(EntityHijackedSkeleton::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":hijackedskeleton"));
    public static final RegistryObject<EntityType<EntityHijackedZombie>> HIJACKED_ZOMBIE = ENTITIES.register("hijackedzombie", () -> EntityType.Builder.of(EntityHijackedZombie::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":hijackedzombie"));
    public static final RegistryObject<EntityType<EntityHijackedSpider>> HIJACKED_SPIDER = ENTITIES.register("hijackedspider", () -> EntityType.Builder.of(EntityHijackedSpider::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":hijackedspider"));
    public static final RegistryObject<EntityType<EntityHijackedEnderman>> HIJACKED_ENDERMAN = ENTITIES.register("hijackedenderman", () -> EntityType.Builder.of(EntityHijackedEnderman::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":hijackedenderman"));
    public static final RegistryObject<EntityType<EntityHijackedWitch>> HIJACKED_WITCH = ENTITIES.register("hijackedwitch", () -> EntityType.Builder.of(EntityHijackedWitch::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":hijackedwitch"));
    public static final RegistryObject<EntityType<EntityHijackedPillager>> HIJACKED_PILLAGER = ENTITIES.register("hijackedpillager", () -> EntityType.Builder.of(EntityHijackedPillager::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":hijackedpillager"));
    public static final RegistryObject<EntityType<EntityHijackedEvoker>> HIJACKED_EVOKER = ENTITIES.register("hijackedevoker", () -> EntityType.Builder.of(EntityHijackedEvoker::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":hijackedevoker"));
    public static final RegistryObject<EntityType<EntityHijackedRavager>> HIJACKED_RAVAGER = ENTITIES.register("hijackedravager", () -> EntityType.Builder.of(EntityHijackedRavager::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":hijackedravager"));

    // ================================================================
    // INBORN ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityAlafin>> INBORN_ALAFIN = ENTITIES.register("inbornalafin", () -> EntityType.Builder.of(EntityAlafin::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":inbornalafin"));
    public static final RegistryObject<EntityType<EntityObus>> INBORN_OBUS = ENTITIES.register("inbornobus", () -> EntityType.Builder.of(EntityObus::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":inbornobus"));
    public static final RegistryObject<EntityType<EntityNormas>> INBORN_NORMAS = ENTITIES.register("inbornnormas", () -> EntityType.Builder.of(EntityNormas::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":inbornnormas"));
    public static final RegistryObject<EntityType<EntityCanal>> INBORN_CANAL = ENTITIES.register("inborncanal", () -> EntityType.Builder.of(EntityCanal::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":inborncanal"));
    
    // Inborn entities - Implemented
    public static final RegistryObject<EntityType<EntityGothol>> INBORN_GOTHOL = ENTITIES.register("inborn_gothol",
        () -> EntityType.Builder.of(EntityGothol::new, MobCategory.MONSTER).sized(1.2F, 2.2F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":inborn_gothol"));
    public static final RegistryObject<EntityType<EntityKol>> INBORN_KOL = ENTITIES.register("inborn_kol",
        () -> EntityType.Builder.of(EntityKol::new, MobCategory.MONSTER).sized(1.0F, 1.9F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":inborn_kol"));
    public static final RegistryObject<EntityType<EntityLesh>> INBORN_LESH = ENTITIES.register("inborn_lesh",
        () -> EntityType.Builder.of(EntityLesh::new, MobCategory.MONSTER).sized(0.9F, 1.65F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":inborn_lesh"));
    public static final RegistryObject<EntityType<EntityLodo>> INBORN_LODO = ENTITIES.register("inborn_lodo",
        () -> EntityType.Builder.of(EntityLodo::new, MobCategory.MONSTER).sized(0.9F, 1.6F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":inborn_lodo"));
    public static final RegistryObject<EntityType<EntityMor>> INBORN_MOR = ENTITIES.register("inborn_mor",
        () -> EntityType.Builder.of(EntityMor::new, MobCategory.MONSTER).sized(1.3F, 2.4F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":inborn_mor"));
    public static final RegistryObject<EntityType<EntityMudo>> INBORN_MUDO = ENTITIES.register("inborn_mudo",
        () -> EntityType.Builder.of(EntityMudo::new, MobCategory.MONSTER).sized(1.0F, 1.7F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":inborn_mudo"));
    public static final RegistryObject<EntityType<EntityNuuh>> INBORN_NUUH = ENTITIES.register("inborn_nuuh",
        () -> EntityType.Builder.of(EntityNuuh::new, MobCategory.MONSTER).sized(1.5F, 2.8F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":inborn_nuuh"));
    public static final RegistryObject<EntityType<EntityRathol>> INBORN_RATHOL = ENTITIES.register("inborn_rathol",
        () -> EntityType.Builder.of(EntityRathol::new, MobCategory.MONSTER).sized(1.0F, 1.8F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":inborn_rathol"));
    public static final RegistryObject<EntityType<EntityViin>> INBORN_VIIN = ENTITIES.register("inborn_viin",
        () -> EntityType.Builder.of(EntityViin::new, MobCategory.MONSTER).sized(1.0F, 1.85F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":inborn_viin"));
    public static final RegistryObject<EntityType<EntityAta>> INBORN_ATA = ENTITIES.register("inborn_ata",
        () -> EntityType.Builder.of(EntityAta::new, MobCategory.MONSTER).sized(0.8F, 1.5F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":inborn_ata"));
    public static final RegistryObject<EntityType<EntityButhol>> INBORN_BUTHOL = ENTITIES.register("inborn_buthol",
        () -> EntityType.Builder.of(EntityButhol::new, MobCategory.MONSTER).sized(1.1F, 2.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":inborn_buthol"));

    // ================================================================
    // CRUDE ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityMovingFlesh>> CRUDE_MOVING_FLESH = ENTITIES.register("crude_moving_flesh", 
        () -> EntityType.Builder.of(EntityMovingFlesh::new, MobCategory.MONSTER).sized(1.2F, 1.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":crude_moving_flesh"));
    public static final RegistryObject<EntityType<EntityWorker>> CRUDE_WORKER = ENTITIES.register("crude_worker", 
        () -> EntityType.Builder.of(EntityWorker::new, MobCategory.MONSTER).sized(1.0F, 1.5F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":crude_worker"));
    public static final RegistryObject<EntityType<EntityCruxA>> CRUDE_CRUX_A = ENTITIES.register("crude_crux_a",
        () -> EntityType.Builder.of(EntityCruxA::new, MobCategory.MONSTER).sized(1.2F, 1.8F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":crude_crux_a"));
    public static final RegistryObject<EntityType<EntityCruxB>> CRUDE_CRUX_B = ENTITIES.register("crude_crux_b",
        () -> EntityType.Builder.of(EntityCruxB::new, MobCategory.MONSTER).sized(1.2F, 1.8F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":crude_crux_b"));
    public static final RegistryObject<EntityType<EntityDone>> CRUDE_DONE = ENTITIES.register("crude_done",
        () -> EntityType.Builder.of(EntityDone::new, MobCategory.MONSTER).sized(1.0F, 1.2F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":crude_done"));
    public static final RegistryObject<EntityType<EntityHeed>> CRUDE_HEED = ENTITIES.register("crude_heed",
        () -> EntityType.Builder.of(EntityHeed::new, MobCategory.MONSTER).sized(0.8F, 1.5F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":crude_heed"));
    public static final RegistryObject<EntityType<EntityHost>> CRUDE_HOST = ENTITIES.register("crude_host",
        () -> EntityType.Builder.of(EntityHost::new, MobCategory.MONSTER).sized(1.5F, 2.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":crude_host"));
    public static final RegistryObject<EntityType<EntityInhooM>> CRUDE_INHOO_M = ENTITIES.register("crude_inhoo_m",
        () -> EntityType.Builder.of(EntityInhooM::new, MobCategory.MONSTER).sized(1.0F, 1.5F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":crude_inhoo_m"));
    public static final RegistryObject<EntityType<EntityInhooS>> CRUDE_INHOO_S = ENTITIES.register("crude_inhoo_s",
        () -> EntityType.Builder.of(EntityInhooS::new, MobCategory.MONSTER).sized(1.0F, 1.5F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":crude_inhoo_s"));
    public static final RegistryObject<EntityType<EntityLeer>> CRUDE_LEER = ENTITIES.register("crude_leer",
        () -> EntityType.Builder.of(EntityLeer::new, MobCategory.MONSTER).sized(1.2F, 2.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":crude_leer"));
    public static final RegistryObject<EntityType<com.subspaceparasite.common.entity.monster.crude.EntityLesh>> CRUDE_LESH = ENTITIES.register("crude_lesh",
        () -> EntityType.Builder.of(com.subspaceparasite.common.entity.monster.crude.EntityLesh::new, MobCategory.MONSTER).sized(1.0F, 1.2F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":crude_lesh"));
    public static final RegistryObject<EntityType<EntityMes>> CRUDE_MES = ENTITIES.register("crude_mes",
        () -> EntityType.Builder.of(EntityMes::new, MobCategory.MONSTER).sized(0.8F, 1.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":crude_mes"));
    public static final RegistryObject<EntityType<EntityQuac>> CRUDE_QUAC = ENTITIES.register("crude_quac",
        () -> EntityType.Builder.of(EntityQuac::new, MobCategory.MONSTER).sized(0.8F, 1.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":crude_quac"));
    public static final RegistryObject<EntityType<EntityScorcher>> CRUDE_SCORCHER = ENTITIES.register("crudescorcher", () -> EntityType.Builder.of(EntityScorcher::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":crudescorcher"));
    public static final RegistryObject<EntityType<EntityMindim>> CRUDE_MINDIM = ENTITIES.register("crudemindim", () -> EntityType.Builder.of(EntityMindim::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":crudemindim"));
    public static final RegistryObject<EntityType<EntityEgas>> CRUDE_EGAS = ENTITIES.register("crudeegas", () -> EntityType.Builder.of(EntityEgas::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":crudeegas"));

    // ================================================================
    // PRIMITIVE ENTITIES (12 core + 12 variant)
    // ================================================================
    public static final RegistryObject<EntityType<EntityBano>> PRIMITIVE_BANO = ENTITIES.register("primitive_bano", 
        () -> EntityType.Builder.of(EntityBano::new, MobCategory.MONSTER).sized(1.0F, 2.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitive_bano"));
    public static final RegistryObject<EntityType<EntityCanra>> PRIMITIVE_CANRA = ENTITIES.register("primitive_canra",
        () -> EntityType.Builder.of(EntityCanra::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitive_canra"));
    public static final RegistryObject<EntityType<EntityEmana>> PRIMITIVE_EMANA = ENTITIES.register("primitive_emana",
        () -> EntityType.Builder.of(EntityEmana::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitive_emana"));
    public static final RegistryObject<EntityType<EntityGim>> PRIMITIVE_GIM = ENTITIES.register("primitive_gim",
        () -> EntityType.Builder.of(EntityGim::new, MobCategory.MONSTER).sized(0.6F, 0.6F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitive_gim"));
    public static final RegistryObject<EntityType<EntityHull>> PRIMITIVE_HULL = ENTITIES.register("primitive_hull",
        () -> EntityType.Builder.of(EntityHull::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitive_hull"));
    public static final RegistryObject<EntityType<EntityIki>> PRIMITIVE_IKI = ENTITIES.register("primitive_iki",
        () -> EntityType.Builder.of(EntityIki::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitive_iki"));
    public static final RegistryObject<EntityType<EntityLum>> PRIMITIVE_LUM = ENTITIES.register("primitive_lum",
        () -> EntityType.Builder.of(EntityLum::new, MobCategory.MONSTER).sized(0.6F, 0.6F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitive_lum"));
    public static final RegistryObject<EntityType<EntityNogla>> PRIMITIVE_NOGLA = ENTITIES.register("primitive_nogla",
        () -> EntityType.Builder.of(EntityNogla::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitive_nogla"));
    public static final RegistryObject<EntityType<EntityRanrac>> PRIMITIVE_RANRAC = ENTITIES.register("primitive_ranrac",
        () -> EntityType.Builder.of(EntityRanrac::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitive_ranrac"));
    public static final RegistryObject<EntityType<EntityShyco>> PRIMITIVE_SHYCO = ENTITIES.register("primitive_shyco",
        () -> EntityType.Builder.of(EntityShyco::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitive_shyco"));
    public static final RegistryObject<EntityType<EntityWymo>> PRIMITIVE_WYMO = ENTITIES.register("primitive_wymo",
        () -> EntityType.Builder.of(EntityWymo::new, MobCategory.MONSTER).sized(0.6F, 0.6F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitive_wymo"));
    public static final RegistryObject<EntityType<EntityZaa>> PRIMITIVE_ZAA = ENTITIES.register("primitive_zaa",
        () -> EntityType.Builder.of(EntityZaa::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitive_zaa"));
    public static final RegistryObject<EntityType<EntityLongarms>> PRIMITIVE_LONGARMS = ENTITIES.register("primitivelongarms", () -> EntityType.Builder.of(EntityLongarms::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitivelongarms"));
    public static final RegistryObject<EntityType<EntityManducater>> PRIMITIVE_MANDUCATER = ENTITIES.register("primitivemanducater", () -> EntityType.Builder.of(EntityManducater::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitivemanducater"));
    public static final RegistryObject<EntityType<EntityReeker>> PRIMITIVE_REEKER = ENTITIES.register("primitivereeker", () -> EntityType.Builder.of(EntityReeker::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitivereeker"));
    public static final RegistryObject<EntityType<EntityYelloweye>> PRIMITIVE_YELLOWEYE = ENTITIES.register("primitiveyelloweye", () -> EntityType.Builder.of(EntityYelloweye::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitiveyelloweye"));
    public static final RegistryObject<EntityType<EntitySummoner>> PRIMITIVE_SUMMONER = ENTITIES.register("primitivesummoner", () -> EntityType.Builder.of(EntitySummoner::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitivesummoner"));
    public static final RegistryObject<EntityType<EntityBolster>> PRIMITIVE_BOLSTER = ENTITIES.register("primitivebolster", () -> EntityType.Builder.of(EntityBolster::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitivebolster"));
    public static final RegistryObject<EntityType<EntityTozon>> PRIMITIVE_TOZON = ENTITIES.register("primitivetozon", () -> EntityType.Builder.of(EntityTozon::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitivetozon"));
    public static final RegistryObject<EntityType<EntityArachnida>> PRIMITIVE_ARACHNIDA = ENTITIES.register("primitivearachnida", () -> EntityType.Builder.of(EntityArachnida::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitivearachnida"));
    public static final RegistryObject<EntityType<EntityDevourer>> PRIMITIVE_DEVOURER = ENTITIES.register("primitivedevourer", () -> EntityType.Builder.of(EntityDevourer::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitivedevourer"));
    public static final RegistryObject<EntityType<EntityVermin>> PRIMITIVE_VERMIN = ENTITIES.register("primitivevermin", () -> EntityType.Builder.of(EntityVermin::new, MobCategory.MONSTER).sized(0.6F, 0.6F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitivevermin"));
    public static final RegistryObject<EntityType<EntityViscera>> PRIMITIVE_VISCERA = ENTITIES.register("primitiveviscera", () -> EntityType.Builder.of(EntityViscera::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitiveviscera"));
    public static final RegistryObject<EntityType<EntityBurrower>> PRIMITIVE_BURROWER = ENTITIES.register("primitiveburrower", () -> EntityType.Builder.of(EntityBurrower::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitiveburrower"));
    public static final RegistryObject<EntityType<EntityBomph>> PRIMITIVE_BOMPH = ENTITIES.register("primitivebomph", () -> EntityType.Builder.of(EntityBomph::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitivebomph"));
    public static final RegistryObject<EntityType<EntityPrimitiveWolf>> PRIMITIVE_WOLF = ENTITIES.register("primitivewolf", () -> EntityType.Builder.of(EntityPrimitiveWolf::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitivewolf"));

    // ================================================================
    // ADAPTED ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityColonyAdapted>> ADAPTED_COLONY = ENTITIES.register("adaptedcolony", () -> EntityType.Builder.of(EntityColonyAdapted::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":adaptedcolony"));
    public static final RegistryObject<EntityType<EntityCreeperAdapted>> ADAPTED_CREEPER = ENTITIES.register("adaptedcreeper", () -> EntityType.Builder.of(EntityCreeperAdapted::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":adaptedcreeper"));
    public static final RegistryObject<EntityType<EntitySkeletonAdapted>> ADAPTED_SKELETON = ENTITIES.register("adaptedskeleton", () -> EntityType.Builder.of(EntitySkeletonAdapted::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":adaptedskeleton"));
    public static final RegistryObject<EntityType<EntitySpiderAdapted>> ADAPTED_SPIDER = ENTITIES.register("adaptedspider", () -> EntityType.Builder.of(EntitySpiderAdapted::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":adaptedspider"));
    public static final RegistryObject<EntityType<EntityZombieAdapted>> ADAPTED_ZOMBIE = ENTITIES.register("adaptedzombie", () -> EntityType.Builder.of(EntityZombieAdapted::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":adaptedzombie"));
    public static final RegistryObject<EntityType<EntityEndermanAdapted>> ADAPTED_ENDERMAN = ENTITIES.register("adaptedenderman", () -> EntityType.Builder.of(EntityEndermanAdapted::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":adaptedenderman"));

    // Adapted entities - Implemented
    public static final RegistryObject<EntityType<EntityBanoAdapted>> ADAPTED_BANO = ENTITIES.register("adapted_bano",
        () -> EntityType.Builder.of(EntityBanoAdapted::new, MobCategory.MONSTER).sized(1.0F, 2.8F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":adapted_bano"));
    public static final RegistryObject<EntityType<EntityCanraAdapted>> ADAPTED_CANRA = ENTITIES.register("adapted_canra",
        () -> EntityType.Builder.of(EntityCanraAdapted::new, MobCategory.MONSTER).sized(1.0F, 1.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":adapted_canra"));
    public static final RegistryObject<EntityType<EntityEmanaAdapted>> ADAPTED_EMANA = ENTITIES.register("adapted_emana",
        () -> EntityType.Builder.of(EntityEmanaAdapted::new, MobCategory.MONSTER).sized(1.0F, 1.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":adapted_emana"));
    public static final RegistryObject<EntityType<EntityGimAdapted>> ADAPTED_GIM = ENTITIES.register("adapted_gim",
        () -> EntityType.Builder.of(EntityGimAdapted::new, MobCategory.MONSTER).sized(0.6F, 0.6F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":adapted_gim"));
    public static final RegistryObject<EntityType<EntityHullAdapted>> ADAPTED_HULL = ENTITIES.register("adapted_hull",
        () -> EntityType.Builder.of(EntityHullAdapted::new, MobCategory.MONSTER).sized(1.5F, 2.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":adapted_hull"));
    public static final RegistryObject<EntityType<EntityIkiAdapted>> ADAPTED_IKI = ENTITIES.register("adapted_iki",
        () -> EntityType.Builder.of(EntityIkiAdapted::new, MobCategory.MONSTER).sized(1.0F, 1.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":adapted_iki"));
    public static final RegistryObject<EntityType<EntityLumAdapted>> ADAPTED_LUM = ENTITIES.register("adapted_lum",
        () -> EntityType.Builder.of(EntityLumAdapted::new, MobCategory.MONSTER).sized(0.6F, 0.6F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":adapted_lum"));
    public static final RegistryObject<EntityType<EntityNoglaAdapted>> ADAPTED_NOGLA = ENTITIES.register("adapted_nogla",
        () -> EntityType.Builder.of(EntityNoglaAdapted::new, MobCategory.MONSTER).sized(1.0F, 1.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":adapted_nogla"));
    public static final RegistryObject<EntityType<EntityRanracAdapted>> ADAPTED_RANRAC = ENTITIES.register("adapted_ranrac",
        () -> EntityType.Builder.of(EntityRanracAdapted::new, MobCategory.MONSTER).sized(1.5F, 2.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":adapted_ranrac"));
    public static final RegistryObject<EntityType<EntityShycoAdapted>> ADAPTED_SHYCO = ENTITIES.register("adapted_shyco",
        () -> EntityType.Builder.of(EntityShycoAdapted::new, MobCategory.MONSTER).sized(1.0F, 1.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":adapted_shyco"));
    public static final RegistryObject<EntityType<EntityWymoAdapted>> ADAPTED_WYMO = ENTITIES.register("adapted_wymo",
        () -> EntityType.Builder.of(EntityWymoAdapted::new, MobCategory.MONSTER).sized(0.6F, 0.6F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":adapted_wymo"));
    public static final RegistryObject<EntityType<EntityZaaAdapted>> ADAPTED_ZAA = ENTITIES.register("adapted_zaa",
        () -> EntityType.Builder.of(EntityZaaAdapted::new, MobCategory.MONSTER).sized(1.5F, 2.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":adapted_zaa"));

    // ================================================================
    // NEXUS / BECKON ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityBeckonCommon>> BECKON_COMMON = ENTITIES.register("beckoncommon", () -> EntityType.Builder.of(EntityBeckonCommon::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":beckoncommon"));
    public static final RegistryObject<EntityType<EntityBeckonUncommon>> BECKON_UNCOMMON = ENTITIES.register("beckonuncommon", () -> EntityType.Builder.of(EntityBeckonUncommon::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":beckonuncommon"));
    public static final RegistryObject<EntityType<EntityBeckonRare>> BECKON_RARE = ENTITIES.register("beckonrare", () -> EntityType.Builder.of(EntityBeckonRare::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":beckonrare"));
    public static final RegistryObject<EntityType<EntityBeckonEpic>> BECKON_EPIC = ENTITIES.register("beckonepic", () -> EntityType.Builder.of(EntityBeckonEpic::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":beckonepic"));
    public static final RegistryObject<EntityType<EntityBeckonLegendary>> BECKON_LEGENDARY = ENTITIES.register("beckonlegendary", () -> EntityType.Builder.of(EntityBeckonLegendary::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":beckonlegendary"));

    // ================================================================
    // NEXUS / DISPATCHER ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityDispatcherCommon>> DISPATCHER_COMMON = ENTITIES.register("dispatchercommon", () -> EntityType.Builder.of(EntityDispatcherCommon::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":dispatchercommon"));
    public static final RegistryObject<EntityType<EntityDispatcherUncommon>> DISPATCHER_UNCOMMON = ENTITIES.register("dispatcheruncommon", () -> EntityType.Builder.of(EntityDispatcherUncommon::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":dispatcheruncommon"));
    public static final RegistryObject<EntityType<EntityDispatcherRare>> DISPATCHER_RARE = ENTITIES.register("dispatcherrare", () -> EntityType.Builder.of(EntityDispatcherRare::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":dispatcherrare"));
    public static final RegistryObject<EntityType<EntityDispatcherEpic>> DISPATCHER_EPIC = ENTITIES.register("dispatcherepic", () -> EntityType.Builder.of(EntityDispatcherEpic::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":dispatcherepic"));
    public static final RegistryObject<EntityType<EntityDodT>> DISPATCHER_LEGENDARY = ENTITIES.register("dispatcherlegendary", () -> EntityType.Builder.of(EntityDodT::new, MobCategory.MONSTER).sized(1.0F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":dispatcherlegendary"));

    // ================================================================
    // NEXUS / ROOTER ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityRooterCommon>> ROOTER_COMMON = ENTITIES.register("rootercommon", () -> EntityType.Builder.of(EntityRooterCommon::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":rootercommon"));
    public static final RegistryObject<EntityType<EntityRooterUncommon>> ROOTER_UNCOMMON = ENTITIES.register("rooteruncommon", () -> EntityType.Builder.of(EntityRooterUncommon::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":rooteruncommon"));
    public static final RegistryObject<EntityType<EntityRooterRare>> ROOTER_RARE = ENTITIES.register("rooterrare", () -> EntityType.Builder.of(EntityRooterRare::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":rooterrare"));
    public static final RegistryObject<EntityType<EntityRooterEpic>> ROOTER_EPIC = ENTITIES.register("rooterepic", () -> EntityType.Builder.of(EntityRooterEpic::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":rooterepic"));
    public static final RegistryObject<EntityType<EntityLeemB>> ROOTER_LEGENDARY = ENTITIES.register("rooterlegendary", () -> EntityType.Builder.of(EntityLeemB::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":rooterlegendary"));

    // ================================================================
    // OTHER NEXUS ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityNak>> NEXUS_GUARD = ENTITIES.register("nexusguard", () -> EntityType.Builder.of(EntityNak::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":nexusguard"));
    public static final RegistryObject<EntityType<EntityUnvo>> NEXUS_OVERSEER = ENTITIES.register("nexusoverseer", () -> EntityType.Builder.of(EntityUnvo::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":nexusoverseer"));
    public static final RegistryObject<EntityType<EntityTonro>> NEXUS_CONSTRUCT = ENTITIES.register("nexusconstruct", () -> EntityType.Builder.of(EntityTonro::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":nexusconstruct"));

    // ================================================================
    // DETERRENT ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityDeterrentSentry>> DETERRENT_SENTRY = ENTITIES.register("deterrentsentry", () -> EntityType.Builder.of(EntityDeterrentSentry::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":deterrentsentry"));
    public static final RegistryObject<EntityType<EntityDeterrentOutpost>> DETERRENT_OUTPOST = ENTITIES.register("deterrentoutpost", () -> EntityType.Builder.of(EntityDeterrentOutpost::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":deterrentoutpost"));
    public static final RegistryObject<EntityType<EntityDeterrentBastion>> DETERRENT_BASTION = ENTITIES.register("deterrentbastion", () -> EntityType.Builder.of(EntityDeterrentBastion::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":deterrentbastion"));

    // ================================================================
    // PURE ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityCreeperPure>> PURE_CREEPER = ENTITIES.register("purecreeper", () -> EntityType.Builder.of(EntityCreeperPure::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":purecreeper"));
    public static final RegistryObject<EntityType<EntitySkeletonPure>> PURE_SKELETON = ENTITIES.register("pureskeleton", () -> EntityType.Builder.of(EntitySkeletonPure::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":pureskeleton"));
    public static final RegistryObject<EntityType<EntityZombiePure>> PURE_ZOMBIE = ENTITIES.register("purezombie", () -> EntityType.Builder.of(EntityZombiePure::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":purezombie"));
    public static final RegistryObject<EntityType<EntitySpiderPure>> PURE_SPIDER = ENTITIES.register("purespider", () -> EntityType.Builder.of(EntitySpiderPure::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":purespider"));
    public static final RegistryObject<EntityType<EntityEndermanPure>> PURE_ENDERMAN = ENTITIES.register("pureenderman", () -> EntityType.Builder.of(EntityEndermanPure::new, MobCategory.MONSTER).sized(0.6F, 1.8F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":pureenderman"));
    public static final RegistryObject<EntityType<EntityWolfPure>> PURE_WOLF = ENTITIES.register("purewolf", () -> EntityType.Builder.of(EntityWolfPure::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":purewolf"));

    // Pure entities - Implemented
    public static final RegistryObject<EntityType<EntityFlam>> PURE_FLAM = ENTITIES.register("pure_flam",
        () -> EntityType.Builder.of(EntityFlam::new, MobCategory.MONSTER).sized(0.6F, 1.8F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":pure_flam"));
    public static final RegistryObject<EntityType<EntityFlog>> PURE_FLOG = ENTITIES.register("pure_flog",
        () -> EntityType.Builder.of(EntityFlog::new, MobCategory.MONSTER).sized(1.0F, 1.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":pure_flog"));
    public static final RegistryObject<EntityType<EntityOmboo>> PURE_OMBOO = ENTITIES.register("pure_omboo",
        () -> EntityType.Builder.of(EntityOmboo::new, MobCategory.MONSTER).sized(1.5F, 2.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":pure_omboo"));
    public static final RegistryObject<EntityType<EntityAlafha>> PURE_ALAFHA = ENTITIES.register("pure_alafha",
        () -> EntityType.Builder.of(EntityAlafha::new, MobCategory.MONSTER).sized(1.0F, 1.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":pure_alafha"));
    public static final RegistryObject<EntityType<EntityGanro>> PURE_GANRO = ENTITIES.register("pure_ganro",
        () -> EntityType.Builder.of(EntityGanro::new, MobCategory.MONSTER).sized(1.5F, 2.5F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":pure_ganro"));
    public static final RegistryObject<EntityType<EntityEsor>> PURE_ESOR = ENTITIES.register("pure_esor",
        () -> EntityType.Builder.of(EntityEsor::new, MobCategory.MONSTER).sized(1.2F, 2.0F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":pure_esor"));
    public static final RegistryObject<EntityType<EntityElvia>> PURE_ELVIA = ENTITIES.register("pure_elvia",
        () -> EntityType.Builder.of(EntityElvia::new, MobCategory.MONSTER).sized(0.8F, 1.5F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":pure_elvia"));
    public static final RegistryObject<EntityType<EntityAnged>> PURE_ANGED = ENTITIES.register("pure_anged",
        () -> EntityType.Builder.of(EntityAnged::new, MobCategory.MONSTER).sized(1.0F, 1.8F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":pure_anged"));

    // ================================================================
    // PREEMINENT ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityPreeminentMarauder>> PREEMINENT_MARAUDER = ENTITIES.register("preeminentmarauder", () -> EntityType.Builder.of(EntityPreeminentMarauder::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":preeminentmarauder"));
    public static final RegistryObject<EntityType<EntityPreeminentWarden>> PREEMINENT_WARDEN = ENTITIES.register("preeminentwarden", () -> EntityType.Builder.of(EntityPreeminentWarden::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":preeminentwarden"));
    public static final RegistryObject<EntityType<EntityPreeminentSovereign>> PREEMINENT_SOVEREIGN = ENTITIES.register("preeminentsovereign", () -> EntityType.Builder.of(EntityPreeminentSovereign::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":preeminentsovereign"));

    // ================================================================
    // ANCIENT ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityAncientDreadnought>> ANCIENT_DREADNOUGHT = ENTITIES.register("ancientdreadnought", () -> EntityType.Builder.of(EntityAncientDreadnought::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":ancientdreadnought"));
    public static final RegistryObject<EntityType<EntityAncientLeviathan>> ANCIENT_LEVIATHAN = ENTITIES.register("ancientleviathan", () -> EntityType.Builder.of(EntityAncientLeviathan::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":ancientleviathan"));
    public static final RegistryObject<EntityType<EntityAncientColossus>> ANCIENT_COLOSSUS = ENTITIES.register("ancientcolossus", () -> EntityType.Builder.of(EntityAncientColossus::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":ancientcolossus"));

    // ================================================================
    // DERIVED ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityDerivedFly>> DERIVED_FLY = ENTITIES.register("derivedfly", () -> EntityType.Builder.of(EntityDerivedFly::new, MobCategory.MONSTER).sized(0.3F, 0.3F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":derivedfly"));
    public static final RegistryObject<EntityType<EntityDerivedSwarm>> DERIVED_SWARM = ENTITIES.register("derivedswarm", () -> EntityType.Builder.of(EntityDerivedSwarm::new, MobCategory.MONSTER).sized(0.3F, 0.3F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":derivedswarm"));
    public static final RegistryObject<EntityType<EntityDerivedCrawler>> DERIVED_CRAWLER = ENTITIES.register("derivedcrawler", () -> EntityType.Builder.of(EntityDerivedCrawler::new, MobCategory.MONSTER).sized(0.6F, 0.6F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":derivedcrawler"));
    public static final RegistryObject<EntityType<EntityDerivedLeaper>> DERIVED_LEAPER = ENTITIES.register("derivedleaper", () -> EntityType.Builder.of(EntityDerivedLeaper::new, MobCategory.MONSTER).sized(0.6F, 0.6F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":derivedleaper"));
    public static final RegistryObject<EntityType<EntityDerivedStalker>> DERIVED_STALKER = ENTITIES.register("derivedstalker", () -> EntityType.Builder.of(EntityDerivedStalker::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":derivedstalker"));

    // ================================================================
    // ABOMINATION ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityAbominationAmalgam>> ABOMINATION_AMALGAM = ENTITIES.register("abominationamalgam", () -> EntityType.Builder.of(EntityAbominationAmalgam::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":abominationamalgam"));
    public static final RegistryObject<EntityType<EntityAbominationChimera>> ABOMINATION_CHIMERA = ENTITIES.register("abominationchimera", () -> EntityType.Builder.of(EntityAbominationChimera::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":abominationchimera"));
    public static final RegistryObject<EntityType<EntityAbominationHydra>> ABOMINATION_HYDRA = ENTITIES.register("abominationhydra", () -> EntityType.Builder.of(EntityAbominationHydra::new, MobCategory.MONSTER).sized(1.5F, 2.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":abominationhydra"));

    // ================================================================
    // PROJECTILE ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityAcidSpit>> ACID_SPIT = ENTITIES.register("acidspit", () -> EntityType.Builder.of(EntityAcidSpit::new, MobCategory.MISC).sized(0.25F, 0.25F).setTrackingRange(128).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":acidspit"));
    public static final RegistryObject<EntityType<EntityBileBomb>> BILE_BOMB = ENTITIES.register("bilebomb", () -> EntityType.Builder.of(EntityBileBomb::new, MobCategory.MISC).sized(0.25F, 0.25F).setTrackingRange(128).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":bilebomb"));
    public static final RegistryObject<EntityType<EntitySporeCloud>> SPORE_CLOUD = ENTITIES.register("sporecloud", () -> EntityType.Builder.of(EntitySporeCloud::new, MobCategory.MISC).sized(0.25F, 0.25F).setTrackingRange(128).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":sporecloud"));
    public static final RegistryObject<EntityType<EntityVirulentShot>> VIRULENT_SHOT = ENTITIES.register("virulentshot", () -> EntityType.Builder.of(EntityVirulentShot::new, MobCategory.MISC).sized(0.25F, 0.25F).setTrackingRange(128).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":virulentshot"));
    public static final RegistryObject<EntityType<EntityParasiteWeb>> PARASITE_WEB = ENTITIES.register("parasiteweb", () -> EntityType.Builder.of(EntityParasiteWeb::new, MobCategory.MISC).sized(0.25F, 0.25F).setTrackingRange(128).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":parasiteweb"));
    public static final RegistryObject<EntityType<EntityBeckonBlast>> BECKON_BLAST = ENTITIES.register("beckonblast", () -> EntityType.Builder.of(EntityBeckonBlast::new, MobCategory.MISC).sized(0.25F, 0.25F).setTrackingRange(128).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":beckonblast"));
    public static final RegistryObject<EntityType<EntityNexusBeam>> NEXUS_BEAM_ENTITY = ENTITIES.register("nexusbeam", () -> EntityType.Builder.of(EntityNexusBeam::new, MobCategory.MISC).sized(0.25F, 0.25F).setTrackingRange(128).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":nexusbeam"));
    
    // Orb Projectiles (for Heblu, Kirin, etc.)
    public static final RegistryObject<EntityType<EntityOrbScary>> PROJECTILE_ORB_SCARY = ENTITIES.register("projectile_orb_scary",
            () -> EntityType.Builder.<EntityOrbScary>of(EntityOrbScary::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .setTrackingRange(128)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(SubspaceParasite.MOD_ID + ":projectile_orb_scary"));
    
    public static final RegistryObject<EntityType<EntityOrbVoid>> PROJECTILE_ORB_VOID = ENTITIES.register("projectile_orb_void",
            () -> EntityType.Builder.<EntityOrbVoid>of(EntityOrbVoid::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F)
                    .setTrackingRange(128)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(SubspaceParasite.MOD_ID + ":projectile_orb_void"));

    // Meteor Projectile (celestial events)
    public static final RegistryObject<EntityType<EntityMeteor>> PROJECTILE_METEOR = ENTITIES.register("projectile_meteor",
            () -> EntityType.Builder.<EntityMeteor>of(EntityMeteor::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .setTrackingRange(128)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(SubspaceParasite.MOD_ID + ":projectile_meteor"));

    // ================================================================
    // DERIVED STAGE ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<com.subspaceparasite.common.entity.monster.derived.EntityHeblu>> DERIVED_HEBLU = ENTITIES.register("derived_heblu",
            () -> EntityType.Builder.of(com.subspaceparasite.common.entity.monster.derived.EntityHeblu::new, MobCategory.MONSTER)
                    .sized(1.2F, 2.5F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(SubspaceParasite.MOD_ID + ":derived_heblu"));
    
    public static final RegistryObject<EntityType<com.subspaceparasite.common.entity.monster.derived.EntityKirin>> DERIVED_KIRIN = ENTITIES.register("derived_kirin",
            () -> EntityType.Builder.of(com.subspaceparasite.common.entity.monster.derived.EntityKirin::new, MobCategory.MONSTER)
                    .sized(1.4F, 3.0F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(SubspaceParasite.MOD_ID + ":derived_kirin"));

    // ================================================================
    // DETERRENT STAGE ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityVenkrolSIV>> DETERRENT_VENKROL_SIV = ENTITIES.register("deterrent_venkrol_siv",
            () -> EntityType.Builder.of(EntityVenkrolSIV::new, MobCategory.MONSTER)
                    .sized(1.0F, 2.2F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(SubspaceParasite.MOD_ID + ":deterrent_venkrol_siv"));

    // ================================================================
    // CONNECTIVE STAGE ENTITIES
    // ================================================================
    
    // ================================================================
    // MISC ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityBuglin>> BUGLIN_ENTITY = ENTITIES.register("buglin", () -> EntityType.Builder.of(EntityBuglin::new, MobCategory.MONSTER).sized(0.3F, 0.3F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":buglin"));
    public static final RegistryObject<EntityType<EntityAlveoliWorm>> ALVEOLI_WORM = ENTITIES.register("alveoliworm", () -> EntityType.Builder.of(EntityAlveoliWorm::new, MobCategory.MONSTER).sized(0.6F, 0.6F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":alveoliworm"));
    public static final RegistryObject<EntityType<EntityParasiteLarva>> PARASITE_LARVA = ENTITIES.register("parasitelarva", () -> EntityType.Builder.of(EntityParasiteLarva::new, MobCategory.MONSTER).sized(0.3F, 0.3F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":parasitelarva"));
    public static final RegistryObject<EntityType<EntityVoidOrb>> VOID_ORB = ENTITIES.register("voidorb", () -> EntityType.Builder.of(EntityVoidOrb::new, MobCategory.MISC).sized(0.25F, 0.25F).setTrackingRange(128).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":voidorb"));
    public static final RegistryObject<EntityType<EntityBoomOrb>> BOOM_ORB = ENTITIES.register("boomb", () -> EntityType.Builder.of(EntityBoomOrb::new, MobCategory.MISC).sized(0.25F, 0.25F).setTrackingRange(128).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":boomb"));

    // ================================================================
    // BLOCK ENTITIES
    // ================================================================
    public static final RegistryObject<BlockEntityType<BiomeHeartBlockEntity>> BIOME_HEART_BE = typedBlockEntity("biomeheart_be", BiomeHeartBlockEntity::new, ModBlocks.BIOME_HEART);
    public static final RegistryObject<BlockEntityType<ColonyHeartBlockEntity>> COLONY_HEART_BE = typedBlockEntity("colonyheart_be", ColonyHeartBlockEntity::new, ModBlocks.COLONY_HEART);
    public static final RegistryObject<BlockEntityType<ColonyOutpostBlockEntity>> COLONY_OUTPOST_BE = typedBlockEntity("colonyoutpost_be", ColonyOutpostBlockEntity::new, ModBlocks.COLONY_OUTPOST);
    public static final RegistryObject<BlockEntityType<RelayControllerBlockEntity>> RELAY_CONTROLLER_BE = typedBlockEntity("relaycontroller_be", RelayControllerBlockEntity::new, ModBlocks.RELAY_CONTROLLER);
    public static final RegistryObject<BlockEntityType<EvolutionLureBlockEntity>> EVOLUTION_LURE_BE = typedBlockEntity("evolutionlure_be", EvolutionLureBlockEntity::new, ModBlocks.EVOLUTION_LURE);
    public static final RegistryObject<BlockEntityType<InfestedFurnaceBlockEntity>> INFESTED_FURNACE_BE = typedBlockEntity("infestedfurnace_be", InfestedFurnaceBlockEntity::new, ModBlocks.INFESTED_FURNACE);
    public static final RegistryObject<BlockEntityType<InfuserFurnaceBlockEntity>> INFUSER_FURNACE_BE = typedBlockEntity("infuserfurnace_be", InfuserFurnaceBlockEntity::new, ModBlocks.INFUSER_FURNACE);
    public static final RegistryObject<BlockEntityType<ParasiteCanisterBlockEntity>> PARASITE_CANISTER_BE = typedBlockEntity("parasitecanister_be", ParasiteCanisterBlockEntity::new, ModBlocks.PARASITE_CANISTER, ModBlocks.PARASITE_CANISTER_ACTIVE);
    public static final RegistryObject<BlockEntityType<NodeLampBlockEntity>> NODE_LAMP_BE = typedBlockEntity("nodelamp_be", NodeLampBlockEntity::new, ModBlocks.NODE_LAMP, ModBlocks.NODE_REDSTONE_LAMP);
    public static final RegistryObject<BlockEntityType<NodeRelayBlockEntity>> NODE_RELAY_BE = typedBlockEntity("noderelay_be", NodeRelayBlockEntity::new, ModBlocks.NODE_RELAY);
    public static final RegistryObject<BlockEntityType<ParasiteLootBlockEntity>> PARASITE_LOOT_BE = typedBlockEntity("parasiteloot_be", ParasiteLootBlockEntity::new, ModBlocks.PARASITE_LOOT);

    private ModEntities() {
        // Utility class - prevent instantiation
    }
}
