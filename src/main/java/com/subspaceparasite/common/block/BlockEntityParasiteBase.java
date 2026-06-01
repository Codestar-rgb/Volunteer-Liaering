package com.subspaceparasite.common.block;

import com.subspaceparasite.common.world.ModWorldData;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Base class for parasite blocks that have tile entities (BlockEntities).
 * <p>
 * These blocks participate in colony management, biome infection spreading,
 * and other persistent data operations. Examples include:
 * <ul>
 *   <li>BIOME_HEART — core of a parasite biome</li>
 *   <li>COLONY_HEART — colony center and management hub</li>
 *   <li>COLONY_OUTPOST — colony expansion point</li>
 *   <li>INFUSER_FURNACE — dual-purpose furnace with infusion</li>
 *   <li>INFESTED_FURNACE — smelting with parasite mechanics</li>
 *   <li>EVOLUTION_LURE — attracts and accelerates evolution</li>
 *   <li>PARASITE_CANISTER / PARASITE_CANISTER_ACTIVE — entity containment</li>
 * </ul>
 * <p>
 * Extends {@link BaseEntityBlock} which provides the block entity lifecycle
 * management required for these functional blocks.
 * <p>
 * In MC 1.20.1, the block must implement {@link #newBlockEntity(BlockPos, BlockState)}
 * to create the correct BlockEntity. This class supports lazy binding of the
 * entity type via {@link #setEntityTypeSupplier(Supplier, BlockEntityTicker)}
 * to avoid circular registration dependencies between ModBlocks and ModEntities.
 */
public class BlockEntityParasiteBase extends BaseEntityBlock {

    /** Lazily-resolved supplier for this block's BlockEntityType. */
    @Nullable
    private Supplier<BlockEntityType<?>> entityTypeSupplier;

    /** Cached ticker for this block's BlockEntity. */
    @Nullable
    private BlockEntityTicker<?> ticker;

    public BlockEntityParasiteBase(Properties properties) {
        super(properties);
    }

    /**
     * Binds this block to its BlockEntityType and ticker.
     * Must be called during commonSetup after both ModBlocks and ModEntities
     * are fully registered, to avoid circular class-loading issues.
     *
     * @param entityTypeSupplier supplier that returns the BlockEntityType (typically a RegistryObject::get)
     * @param ticker the BlockEntityTicker for this block's entity, or null if no ticking is needed
     */
    public void setEntityTypeSupplier(Supplier<BlockEntityType<?>> entityTypeSupplier, @Nullable BlockEntityTicker<?> ticker) {
        this.entityTypeSupplier = entityTypeSupplier;
        this.ticker = ticker;
    }

    /**
     * Creates a new BlockEntity for this block.
     * Uses the lazily-bound entity type supplier to create the entity.
     * If no supplier has been set (e.g., during early loading), returns null.
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (entityTypeSupplier == null) return null;
        BlockEntityType<?> type = entityTypeSupplier.get();
        if (type == null) return null;
        return type.create(pos, state);
    }

    /**
     * Returns the render shape — INVISIBLE for blocks with entity renderers,
     * MODEL for blocks with normal block models.
     * <p>
     * Override in subclasses that use BER (Block Entity Renderers).
     */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /**
     * Random tick: allows BlockEntityParasiteBase blocks to participate in
     * passive spreading if they don't have a custom ticker.
     */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Base implementation does nothing; subclasses override for block-specific logic
    }

    /**
     * Returns the BlockEntityTicker for this block's entity.
     * Uses the lazily-bound ticker set via
     * {@link #setEntityTypeSupplier(Supplier, BlockEntityTicker)}.
     */
    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return ticker != null ? (BlockEntityTicker<T>) ticker : null;
    }
}
