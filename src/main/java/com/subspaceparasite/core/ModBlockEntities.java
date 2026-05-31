package com.subspaceparasite.core;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.common.block.entity.TileEntityInfuserFurnace;
import com.subspaceparasite.common.block.entity.TileEntityParasiteBarrier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 方块实体注册表
 */
public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SubspaceParasite.MOD_ID);

    // 寄生虫屏障方块实体
    public static final RegistryObject<BlockEntityType<TileEntityParasiteBarrier>> PARASITE_BARRIER =
            BLOCK_ENTITIES.register("parasite_barrier", () ->
                    BlockEntityType.Builder.of(TileEntityParasiteBarrier::new, ModBlocks.PARASITE_BARRIER.get())
                            .build(null));

    // 感染熔炉方块实体
    public static final RegistryObject<BlockEntityType<TileEntityInfuserFurnace>> INFUSER_FURNACE =
            BLOCK_ENTITIES.register("infuser_furnace", () ->
                    BlockEntityType.Builder.of(TileEntityInfuserFurnace::new, ModBlocks.INFUSER_FURNACE.get())
                            .build(null));
}
