package com.subspaceparasite.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 寄生虫屏障方块 - 超高硬度、不可破坏的防御方块
 * 基于原版 SRP BlockParasiteBarrier (214 行) 完整逻辑移植到 1.20.1
 * 
 * 特性:
 * - 600 万爆炸抗性（几乎不可破坏）
 * - 免疫实体破坏和爆炸
 * - 右键可调整屏障半径（需要权限）
 * - 带有方块实体 TileEntityParasiteBarrier
 * - 创造模式专用方块
 */
public class BlockParasiteBarrier extends BaseEntityBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public BlockParasiteBarrier() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(-1.0F, 6000000.0F) // -1 表示不可破坏，600 万爆炸抗性
                .sound(SoundType.METAL)
                .noOcclusion()
                .isValidSpawn((state, world, pos, entityType) -> false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (worldIn.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // 检查玩家是否有权限配置屏障
        if (!canConfigure(player)) {
            if (!player.isCreative()) {
                player.sendSystemMessage(Component.translatable("msg.subspaceparasite.barrier.no_permission")
                        .withStyle(net.minecraft.ChatFormatting.RED));
            }
            return InteractionResult.SUCCESS;
        }

        // 获取方块实体并调整半径
        if (worldIn.getBlockEntity(pos) instanceof TileEntityParasiteBarrier barrier) {
            int radius = barrier.getRadiusChunks();
            radius = radius % 10 + 1; // 循环设置 1-10 区块半径
            barrier.setRadiusChunks(radius);
            
            player.sendSystemMessage(Component.translatable("msg.subspaceparasite.barrier.radius_set", radius)
                    .withStyle(net.minecraft.ChatFormatting.GREEN));
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * 检查玩家是否可以配置屏障
     */
    private boolean canConfigure(Player player) {
        // 创造模式玩家或拥有 srpbarrier 权限的玩家可以配置
        return player.isCreative() || player.hasPermissions(2);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        if (!worldIn.isClientSide() && worldIn.getBlockEntity(pos) instanceof TileEntityParasiteBarrier barrier) {
            // 初始化屏障中心并清理周围生物
            barrier.initCenterFromPos(pos);
            barrier.scrubMobsAroundField();
        }
    }

    @Override
    public void onBlockExploded(Level world, BlockPos pos, Explosion explosion) {
        // 完全免疫爆炸
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        // 禁止任何实体破坏
        return false;
    }

    @Override
    public float getExplosionResistance() {
        return 6000000.0F;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        // 不能被活塞推动
        return PushReaction.BLOCK;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return false;
    }

    /**
     * 物品栏中的提示文本
     */
    public static void appendHoverText(ItemStack stack, java.util.List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip.subspaceparasite.parasite_barrier.creative_only")
                .withStyle(net.minecraft.ChatFormatting.RED));
        tooltip.add(Component.translatable("tooltip.subspaceparasite.parasite_barrier.desc1")
                .withStyle(net.minecraft.ChatFormatting.WHITE));
        tooltip.add(Component.translatable("tooltip.subspaceparasite.parasite_barrier.desc2")
                .withStyle(net.minecraft.ChatFormatting.WHITE));
    }
}
