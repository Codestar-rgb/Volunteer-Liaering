package com.subspaceparasite.core;

import com.subspaceparasite.common.entity.monster.deterrent.EntityVenkrolSIV;
import com.subspaceparasite.common.entity.PlaceholderBlockEntity;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.entity.base.EntityParasitePlaceholder;
import com.subspaceparasite.common.entity.monster.assimilated.EntityAssimilatedHuman;
import com.subspaceparasite.common.entity.monster.assimilated.EntityAssimilatedCow;
import com.subspaceparasite.common.entity.monster.assimilated.EntityAssimilatedSheep;
import com.subspaceparasite.common.entity.monster.crude.EntityMovingFlesh;
import com.subspaceparasite.common.entity.monster.crude.EntityWorker;
import com.subspaceparasite.common.entity.monster.feral.EntityFeralHuman;
import com.subspaceparasite.common.entity.monster.infected.EntityInfectedHuman;
import com.subspaceparasite.common.entity.monster.inborn.EntityGothol;
import com.subspaceparasite.common.entity.monster.inborn.EntityKol;
import com.subspaceparasite.common.entity.monster.inborn.EntityLesh;
import com.subspaceparasite.common.entity.monster.inborn.EntityLodo;
import com.subspaceparasite.common.entity.monster.inborn.EntityMor;
import com.subspaceparasite.common.entity.monster.inborn.EntityMudo;
import com.subspaceparasite.common.entity.monster.inborn.EntityNuuh;
import com.subspaceparasite.common.entity.monster.inborn.EntityRathol;
import com.subspaceparasite.common.entity.monster.inborn.EntityViin;
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
import com.subspaceparasite.common.entity.projectile.EntityOrbBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.subspaceparasite.SubspaceParasite;
import net.minecraftforge.registries.RegistryObject;

/**
 * Entity and BlockEntity registry for the SubspaceParasite mod.
 * Contains ALL entities (~140+) from the original Scape and Run: Parasites mod.
 * Uses EntityParasiteBase as placeholder for all parasite entities until
 * specific entity classes are implemented.
 */
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SubspaceParasite.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SubspaceParasite.MOD_ID);

    // Helper: standard parasite entity builder
    private static RegistryObject<EntityType<EntityParasitePlaceholder>> parasite(String name, float width, float height) {
        return ENTITIES.register(name, () -> EntityType.Builder.<EntityParasitePlaceholder>of(EntityParasitePlaceholder::new, MobCategory.MONSTER)
                .sized(width, height)
                .setTrackingRange(64)
                .setUpdateInterval(3)
                .setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":" + name));
    }

    // Helper: humanoid parasite entity (zombies, skeletons, etc.)
    private static RegistryObject<EntityType<EntityParasitePlaceholder>> humanoidParasite(String name) {
        return parasite(name, 0.6F, 1.8F);
    }

    // Helper: large parasite entity (beasts, golems, bosses)
    private static RegistryObject<EntityType<EntityParasitePlaceholder>> largeParasite(String name) {
        return parasite(name, 1.5F, 2.0F);
    }

    // Helper: medium parasite entity (spiders, wolves, etc.)
    private static RegistryObject<EntityType<EntityParasitePlaceholder>> mediumParasite(String name) {
        return parasite(name, 1.0F, 1.0F);
    }

    // Helper: small parasite entity (chickens, foxes, etc.)
    private static RegistryObject<EntityType<EntityParasitePlaceholder>> smallParasite(String name) {
        return parasite(name, 0.6F, 0.6F);
    }

    // Helper: tiny parasite entity (bats, bees, bugs)
    private static RegistryObject<EntityType<EntityParasitePlaceholder>> tinyParasite(String name) {
        return parasite(name, 0.3F, 0.3F);
    }

    // Helper: projectile entity
    private static RegistryObject<EntityType<EntityParasitePlaceholder>> projectile(String name) {
        return ENTITIES.register(name, () -> EntityType.Builder.<EntityParasitePlaceholder>of(EntityParasitePlaceholder::new, MobCategory.MISC)
                .sized(0.25F, 0.25F)
                .setTrackingRange(128)
                .setUpdateInterval(1)
                .setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":" + name));
    }

    // Helper: block entity with placeholder
    @SafeVarargs
    private static RegistryObject<BlockEntityType<PlaceholderBlockEntity>> blockEntity(String name, RegistryObject<Block>... blocks) {
        return BLOCK_ENTITIES.register(name, () -> {
            Block[] blockArray = new Block[blocks.length];
            for (int i = 0; i < blocks.length; i++) {
                blockArray[i] = blocks[i].get();
            }
            // Use a type-reference array so the BlockEntityType can be passed to the
            // PlaceholderBlockEntity constructor. The array is populated immediately after
            // build() returns, before any BE instance is actually created at runtime.
            BlockEntityType<PlaceholderBlockEntity>[] typeRef = new BlockEntityType[1];
            BlockEntityType<PlaceholderBlockEntity> result = BlockEntityType.Builder.of(
                    (pos, state) -> new PlaceholderBlockEntity(typeRef[0], pos, state),
                    blockArray
            ).build(null);
            typeRef[0] = result;
            return result;
        });
    }

    // ================================================================
    // INFECTED ENTITIES (~20)
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_CREEPER = mediumParasite("infectedcreeper");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_SKELETON = humanoidParasite("infectedskeleton");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_ZOMBIE = humanoidParasite("infectedzombie");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_SPIDER = mediumParasite("infectedspider");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_ENDERMAN = humanoidParasite("infectedenderman");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_PIG = mediumParasite("infectedpig");
    public static final RegistryObject<EntityType<EntityInfectedHuman>> INFECTED_HUMAN = ENTITIES.register("infectedhuman", 
        () -> EntityType.Builder.of(EntityInfectedHuman::new, MobCategory.MONSTER).sized(0.6F, 1.8F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":infectedhuman"));
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_COW = mediumParasite("infectedcow");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_SHEEP = mediumParasite("infectedsheep");
    
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
    public static final RegistryObject<EntityType<EntityFeralHuman>> FERAL_HUMAN = ENTITIES.register("feralhuman",
        () -> EntityType.Builder.of(EntityFeralHuman::new, MobCategory.MONSTER).sized(0.6F, 1.8F)
                .setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true)
                .build(SubspaceParasite.MOD_ID + ":feralhuman"));
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_CHICKEN = smallParasite("infectedchicken");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_VILLAGER = humanoidParasite("infectedvillager");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_WOLF = mediumParasite("infectedwolf");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_HORSE = largeParasite("infectedhorse");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_IRON_GOLEM = largeParasite("infectedirongolem");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_SNOW_GOLEM = mediumParasite("infectedsnowgolem");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_BAT = tinyParasite("infectedbat");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_BLAZE = mediumParasite("infectedblaze");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_WITCH = humanoidParasite("infectedwitch");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_RAVAGER = largeParasite("infectedravager");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_PILLAGER = humanoidParasite("infectedpillager");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_EVOKER = humanoidParasite("infectedevoker");

    // ================================================================
    // SPECIAL INFECTED
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_GHAST = largeParasite("infectedghast");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_PHANTOM = largeParasite("infectedphantom");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_WARDEN = largeParasite("infectedwarden");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_WITHER_SKELETON = humanoidParasite("infectedwitherskeleton");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_STRAY = humanoidParasite("infectedstray");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_HUSK = humanoidParasite("infectedhusk");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_DROWNED = humanoidParasite("infecteddrowned");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_CAVE_SPIDER = smallParasite("infectedcavespider");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_MOOSHROOM = humanoidParasite("infectedmooshroom");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_LLAMA = largeParasite("infectedllama");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_POLAR_BEAR = largeParasite("infectedpolarbear");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_PANDA = mediumParasite("infectedpanda");  // quadruped
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_FOX = smallParasite("infectedfox");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INFECTED_BEE = tinyParasite("infectedbee");

    // ================================================================
    // FERAL ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> FERAL_CREEPER = mediumParasite("feralcreeper");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> FERAL_SKELETON = humanoidParasite("feralskeleton");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> FERAL_ZOMBIE = humanoidParasite("feralzombie");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> FERAL_SPIDER = mediumParasite("feralspider");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> FERAL_ENDERMAN = humanoidParasite("feralenderman");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> FERAL_WOLF = mediumParasite("feralwolf");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> FERAL_IRON_GOLEM = largeParasite("feralirongolem");

    // ================================================================
    // HIJACKED ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> HIJACKED_CREEPER = mediumParasite("hijackedcreeper");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> HIJACKED_SKELETON = humanoidParasite("hijackedskeleton");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> HIJACKED_ZOMBIE = humanoidParasite("hijackedzombie");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> HIJACKED_SPIDER = mediumParasite("hijackedspider");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> HIJACKED_ENDERMAN = humanoidParasite("hijackedenderman");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> HIJACKED_WITCH = humanoidParasite("hijackedwitch");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> HIJACKED_PILLAGER = humanoidParasite("hijackedpillager");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> HIJACKED_EVOKER = humanoidParasite("hijackedevoker");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> HIJACKED_RAVAGER = largeParasite("hijackedravager");

    // ================================================================
    // INBORN ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INBORN_ALAFIN = mediumParasite("inbornalafin");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INBORN_OBUS = mediumParasite("inbornobus");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INBORN_NORMAS = mediumParasite("inbornnormas");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> INBORN_CANAL = mediumParasite("inborncanal");
    
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
    public static final RegistryObject<EntityType<EntityLesh>> CRUDE_LESH = ENTITIES.register("crude_lesh",
        () -> EntityType.Builder.of(EntityLesh::new, MobCategory.MONSTER).sized(1.0F, 1.2F)
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
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> CRUDE_SCORCHER = mediumParasite("crudescorcher");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> CRUDE_MINDIM = mediumParasite("crudemindim");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> CRUDE_EGAS = mediumParasite("crudeegas");

    // ================================================================
    // PRIMITIVE ENTITIES (12 core + 12 variant)
    // ================================================================
    public static final RegistryObject<EntityType<EntityBano>> PRIMITIVE_BANO = ENTITIES.register("primitive_bano", 
        () -> EntityType.Builder.of(EntityBano::new, MobCategory.MONSTER).sized(1.0F, 2.8F).build(SubspaceParasite.MOD_ID + ":primitive_bano"));
    public static final RegistryObject<EntityType<EntityCanra>> PRIMITIVE_CANRA = ENTITIES.register("primitive_canra",
        () -> EntityType.Builder.of(EntityCanra::new, MobCategory.MONSTER).sized(1.0F, 1.0F).build(SubspaceParasite.MOD_ID + ":primitive_canra"));
    public static final RegistryObject<EntityType<EntityEmana>> PRIMITIVE_EMANA = ENTITIES.register("primitive_emana",
        () -> EntityType.Builder.of(EntityEmana::new, MobCategory.MONSTER).sized(1.0F, 1.0F).build(SubspaceParasite.MOD_ID + ":primitive_emana"));
    public static final RegistryObject<EntityType<EntityGim>> PRIMITIVE_GIM = ENTITIES.register("primitive_gim",
        () -> EntityType.Builder.of(EntityGim::new, MobCategory.MONSTER).sized(0.6F, 0.6F).build(SubspaceParasite.MOD_ID + ":primitive_gim"));
    public static final RegistryObject<EntityType<EntityHull>> PRIMITIVE_HULL = ENTITIES.register("primitive_hull",
        () -> EntityType.Builder.of(EntityHull::new, MobCategory.MONSTER).sized(1.5F, 2.0F).build(SubspaceParasite.MOD_ID + ":primitive_hull"));
    public static final RegistryObject<EntityType<EntityIki>> PRIMITIVE_IKI = ENTITIES.register("primitive_iki",
        () -> EntityType.Builder.of(EntityIki::new, MobCategory.MONSTER).sized(1.0F, 1.0F).build(SubspaceParasite.MOD_ID + ":primitive_iki"));
    public static final RegistryObject<EntityType<EntityLum>> PRIMITIVE_LUM = ENTITIES.register("primitive_lum",
        () -> EntityType.Builder.of(EntityLum::new, MobCategory.MONSTER).sized(0.6F, 0.6F).build(SubspaceParasite.MOD_ID + ":primitive_lum"));
    public static final RegistryObject<EntityType<EntityNogla>> PRIMITIVE_NOGLA = ENTITIES.register("primitive_nogla",
        () -> EntityType.Builder.of(EntityNogla::new, MobCategory.MONSTER).sized(1.0F, 1.0F).setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).build(SubspaceParasite.MOD_ID + ":primitive_nogla"));
    public static final RegistryObject<EntityType<EntityRanrac>> PRIMITIVE_RANRAC = ENTITIES.register("primitive_ranrac",
        () -> EntityType.Builder.of(EntityRanrac::new, MobCategory.MONSTER).sized(1.5F, 2.0F).build(SubspaceParasite.MOD_ID + ":primitive_ranrac"));
    public static final RegistryObject<EntityType<EntityShyco>> PRIMITIVE_SHYCO = ENTITIES.register("primitive_shyco",
        () -> EntityType.Builder.of(EntityShyco::new, MobCategory.MONSTER).sized(1.0F, 1.0F).build(SubspaceParasite.MOD_ID + ":primitive_shyco"));
    public static final RegistryObject<EntityType<EntityWymo>> PRIMITIVE_WYMO = ENTITIES.register("primitive_wymo",
        () -> EntityType.Builder.of(EntityWymo::new, MobCategory.MONSTER).sized(0.6F, 0.6F).build(SubspaceParasite.MOD_ID + ":primitive_wymo"));
    public static final RegistryObject<EntityType<EntityZaa>> PRIMITIVE_ZAA = ENTITIES.register("primitive_zaa",
        () -> EntityType.Builder.of(EntityZaa::new, MobCategory.MONSTER).sized(1.5F, 2.0F).build(SubspaceParasite.MOD_ID + ":primitive_zaa"));
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PRIMITIVE_LONGARMS = humanoidParasite("primitivelongarms");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PRIMITIVE_MANDUCATER = humanoidParasite("primitivemanducater");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PRIMITIVE_REEKER = mediumParasite("primitivereeker");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PRIMITIVE_YELLOWEYE = mediumParasite("primitiveyelloweye");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PRIMITIVE_SUMMONER = humanoidParasite("primitivesummoner");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PRIMITIVE_BOLSTER = mediumParasite("primitivebolster");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PRIMITIVE_TOZON = largeParasite("primitivetozon");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PRIMITIVE_ARACHNIDA = mediumParasite("primitivearachnida");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PRIMITIVE_DEVOURER = largeParasite("primitivedevourer");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PRIMITIVE_VERMIN = smallParasite("primitivevermin");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PRIMITIVE_VISCERA = mediumParasite("primitiveviscera");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PRIMITIVE_BURROWER = mediumParasite("primitiveburrower");

    // ================================================================
    // ADAPTED ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ADAPTED_COLONY = mediumParasite("adaptedcolony");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ADAPTED_CREEPER = mediumParasite("adaptedcreeper");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ADAPTED_SKELETON = humanoidParasite("adaptedskeleton");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ADAPTED_SPIDER = mediumParasite("adaptedspider");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ADAPTED_ZOMBIE = humanoidParasite("adaptedzombie");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ADAPTED_ENDERMAN = humanoidParasite("adaptedenderman");

    // ================================================================
    // NEXUS / BECKON ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> BECKON_COMMON = largeParasite("beckoncommon");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> BECKON_UNCOMMON = largeParasite("beckonuncommon");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> BECKON_RARE = largeParasite("beckonrare");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> BECKON_EPIC = largeParasite("beckonepic");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> BECKON_LEGENDARY = largeParasite("beckonlegendary");

    // ================================================================
    // NEXUS / DISPATCHER ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> DISPATCHER_COMMON = mediumParasite("dispatchercommon");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> DISPATCHER_UNCOMMON = mediumParasite("dispatcheruncommon");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> DISPATCHER_RARE = mediumParasite("dispatcherrare");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> DISPATCHER_EPIC = mediumParasite("dispatcherepic");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> DISPATCHER_LEGENDARY = mediumParasite("dispatcherlegendary");

    // ================================================================
    // NEXUS / ROOTER ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ROOTER_COMMON = largeParasite("rootercommon");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ROOTER_UNCOMMON = largeParasite("rooteruncommon");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ROOTER_RARE = largeParasite("rooterrare");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ROOTER_EPIC = largeParasite("rooterepic");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ROOTER_LEGENDARY = largeParasite("rooterlegendary");

    // ================================================================
    // OTHER NEXUS ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> NEXUS_GUARD = largeParasite("nexusguard");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> NEXUS_OVERSEER = largeParasite("nexusoverseer");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> NEXUS_CONSTRUCT = largeParasite("nexusconstruct");

    // ================================================================
    // DETERRENT ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> DETERRENT_SENTRY = mediumParasite("deterrentsentry");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> DETERRENT_OUTPOST = largeParasite("deterrentoutpost");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> DETERRENT_BASTION = largeParasite("deterrentbastion");

    // ================================================================
    // PURE ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PURE_CREEPER = mediumParasite("purecreeper");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PURE_SKELETON = humanoidParasite("pureskeleton");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PURE_ZOMBIE = humanoidParasite("purezombie");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PURE_SPIDER = mediumParasite("purespider");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PURE_ENDERMAN = humanoidParasite("pureenderman");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PURE_WOLF = mediumParasite("purewolf");

    // ================================================================
    // PREEMINENT ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PREEMINENT_MARAUDER = largeParasite("preeminentmarauder");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PREEMINENT_WARDEN = largeParasite("preeminentwarden");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PREEMINENT_SOVEREIGN = largeParasite("preeminentsovereign");

    // ================================================================
    // ANCIENT ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ANCIENT_DREADNOUGHT = largeParasite("ancientdreadnought");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ANCIENT_LEVIATHAN = largeParasite("ancientleviathan");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ANCIENT_COLOSSUS = largeParasite("ancientcolossus");

    // ================================================================
    // DERIVED ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> DERIVED_FLY = tinyParasite("derivedfly");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> DERIVED_SWARM = tinyParasite("derivedswarm");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> DERIVED_CRAWLER = smallParasite("derivedcrawler");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> DERIVED_LEAPER = smallParasite("derivedleaper");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> DERIVED_STALKER = mediumParasite("derivedstalker");

    // ================================================================
    // ABOMINATION ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ABOMINATION_AMALGAM = largeParasite("abominationamalgam");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ABOMINATION_CHIMERA = largeParasite("abominationchimera");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ABOMINATION_HYDRA = largeParasite("abominationhydra");

    // ================================================================
    // PROJECTILE ENTITIES
    // ================================================================
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ACID_SPIT = projectile("acidspit");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> BILE_BOMB = projectile("bilebomb");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> SPORE_CLOUD = projectile("sporecloud");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> VIRULENT_SHOT = projectile("virulentshot");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PARASITE_WEB = projectile("parasiteweb");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> BECKON_BLAST = projectile("beckonblast");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> NEXUS_BEAM_ENTITY = projectile("nexusbeam");
    
    // Orb Projectiles (for Heblu, Kirin, etc.)
    public static final RegistryObject<EntityType<EntityOrbBase>> PROJECTILE_ORB_SCARY = ENTITIES.register("projectile_orb_scary",
            () -> EntityType.Builder.<EntityOrbBase>of(EntityOrbBase::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .setTrackingRange(128)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(SubspaceParasite.MOD_ID + ":projectile_orb_scary"));
    
    public static final RegistryObject<EntityType<EntityOrbBase>> PROJECTILE_ORB_VOID = ENTITIES.register("projectile_orb_void",
            () -> EntityType.Builder.<EntityOrbBase>of(EntityOrbBase::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F)
                    .setTrackingRange(128)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(SubspaceParasite.MOD_ID + ":projectile_orb_void"));

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
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> BUGLIN_ENTITY = tinyParasite("buglin");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> ALVEOLI_WORM = smallParasite("alveoliworm");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> PARASITE_LARVA = tinyParasite("parasitelarva");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> VOID_ORB = projectile("voidorb");
    public static final RegistryObject<EntityType<EntityParasitePlaceholder>> BOOM_ORB = projectile("boomb");

    // ================================================================
    // BLOCK ENTITIES
    // ================================================================
    public static final RegistryObject<BlockEntityType<PlaceholderBlockEntity>> BIOME_HEART_BE = blockEntity("biomeheart_be", ModBlocks.BIOME_HEART);
    public static final RegistryObject<BlockEntityType<PlaceholderBlockEntity>> COLONY_HEART_BE = blockEntity("colonyheart_be", ModBlocks.COLONY_HEART);
    public static final RegistryObject<BlockEntityType<PlaceholderBlockEntity>> COLONY_OUTPOST_BE = blockEntity("colonyoutpost_be", ModBlocks.COLONY_OUTPOST);
    public static final RegistryObject<BlockEntityType<PlaceholderBlockEntity>> RELAY_CONTROLLER_BE = blockEntity("relaycontroller_be", ModBlocks.RELAY_CONTROLLER);
    public static final RegistryObject<BlockEntityType<PlaceholderBlockEntity>> EVOLUTION_LURE_BE = blockEntity("evolutionlure_be", ModBlocks.EVOLUTION_LURE);
    public static final RegistryObject<BlockEntityType<PlaceholderBlockEntity>> INFESTED_FURNACE_BE = blockEntity("infestedfurnace_be", ModBlocks.INFESTED_FURNACE);
    public static final RegistryObject<BlockEntityType<PlaceholderBlockEntity>> INFUSER_FURNACE_BE = blockEntity("infuserfurnace_be", ModBlocks.INFUSER_FURNACE);
    public static final RegistryObject<BlockEntityType<PlaceholderBlockEntity>> PARASITE_CANISTER_BE = blockEntity("parasitecanister_be", ModBlocks.PARASITE_CANISTER, ModBlocks.PARASITE_CANISTER_ACTIVE);
    public static final RegistryObject<BlockEntityType<PlaceholderBlockEntity>> NODE_LAMP_BE = blockEntity("nodelamp_be", ModBlocks.NODE_LAMP, ModBlocks.NODE_REDSTONE_LAMP);

    private ModEntities() {
        // Utility class - prevent instantiation
    }
}
