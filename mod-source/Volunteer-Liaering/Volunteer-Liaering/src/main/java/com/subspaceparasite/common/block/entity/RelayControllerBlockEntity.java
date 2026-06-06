package com.subspaceparasite.common.block.entity;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.common.item.ItemModule;
import com.subspaceparasite.core.ModBlocks;
import com.subspaceparasite.core.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Relay Controller block entity - the central hub for the parasite scanner
 * relay system. Accepts scanner modules, manages child NodeRelay positions,
 * and performs area scans to generate reports.
 * Ported from TileEntityRelayController (1.12) to 1.20.1.
 */
public class RelayControllerBlockEntity extends BlockEntity {

    // Formation state
    public boolean formed = false;

    // Scan state
    private long nextScanTick = 0L;
    private static final String NBT_NEXT_SCAN = "NextScanTick";
    private boolean scanPending = false;
    private int scanTicks = 0;
    private UUID scanPlayer = null;
    private ItemModule.ModuleKind scanKind = null;

    // Child positions (NodeRelay blocks linked to this controller)
    private final List<BlockPos> childPositions = new ArrayList<>();

    // Dismantle guard
    private boolean dismantling = false;

    // Item handler for the module slot
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return !stack.isEmpty() && stack.getItem() instanceof ItemModule;
        }

        @Override
        protected void onContentsChanged(int slot) {
            RelayControllerBlockEntity.this.setChanged();
        }
    };

    private final LazyOptional<IItemHandler> handlerLazy = LazyOptional.of(() -> itemHandler);

    public RelayControllerBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
    }

    public RelayControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ==================== Child Management ====================

    public void setChildPositions(List<BlockPos> positions) {
        this.childPositions.clear();
        if (positions != null) {
            this.childPositions.addAll(positions);
        }
        this.setChanged();
    }

    public void addChild(BlockPos p) {
        if (p != null && !this.childPositions.contains(p)) {
            this.childPositions.add(p);
            this.setChanged();
        }
    }

    public List<BlockPos> getChildPositions() {
        return this.childPositions;
    }

    // ==================== Formation ====================

    public void setFormed(boolean val) {
        if (this.formed == val) return;
        this.formed = val;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            BlockState s = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, s, s, 3);
            this.level.getChunkAt(this.worldPosition).setUnsaved(true);
        }
    }

    public boolean isFormed() {
        return this.formed;
    }

    // ==================== Scan Cooldown ====================

    private int scannerCooldownTicks() {
        // Default 6000 ticks (5 minutes). Config integration when available.
        return 6000;
    }

    public boolean canScan() {
        return this.level == null || this.level.getGameTime() >= this.nextScanTick;
    }

    public int getCooldownRemainingTicks() {
        if (this.level == null) return 0;
        long t = this.nextScanTick - this.level.getGameTime();
        return t > 0L ? (int) t : 0;
    }

    public int getCooldownTotalTicks() {
        return this.scannerCooldownTicks();
    }

    public void startCooldown() {
        if (this.level == null) return;
        this.nextScanTick = this.level.getGameTime() + this.scannerCooldownTicks();
        this.setChanged();
        if (!this.level.isClientSide) {
            BlockState s = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, s, s, 3);
        }
    }

    // ==================== Item Handler ====================

    public ItemStackHandler getHandler() {
        return this.itemHandler;
    }

    public void dropContents() {
        if (this.level == null || this.level.isClientSide) return;
        ItemStack stack = this.itemHandler.getStackInSlot(0);
        if (!stack.isEmpty()) {
            Containers.dropItemStack(this.level,
                    this.worldPosition.getX() + 0.5,
                    this.worldPosition.getY() + 0.5,
                    this.worldPosition.getZ() + 0.5,
                    stack.copy());
            this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
            this.setChanged();
        }
    }

    // ==================== Tick ====================

    public static void serverTick(Level level, BlockPos pos, BlockState state, RelayControllerBlockEntity blockEntity) {
        if (level.isClientSide) return;

        if (!blockEntity.scanPending) return;

        if (--blockEntity.scanTicks > 0) return;

        blockEntity.scanPending = false;

        ServerPlayer player = ((ServerLevel) level).getServer().getPlayerList().getPlayer(blockEntity.scanPlayer);
        if (player == null) {
            blockEntity.setChanged();
            return;
        }

        // Give basic scan report
        giveScanPaper(player, blockEntity);

        level.playSound(null, pos, ModSounds.RELAY_PAPER.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        blockEntity.setChanged();
    }

    /**
     * Give a basic scan report paper to the player.
     */
    private static void giveScanPaper(ServerPlayer player, RelayControllerBlockEntity be) {
        if (be.level == null || be.level.isClientSide) return;

        // Count parasites and mobs in range
        int parasiteCount = 0;
        int totalMobCount = 0;
        BlockPos origin = be.worldPosition;
        int range = 64;

        for (Entity entity : be.level.getEntitiesOfClass(LivingEntity.class,
                new net.minecraft.world.phys.AABB(
                        origin.getX() - range, origin.getY() - range, origin.getZ() - range,
                        origin.getX() + range, origin.getY() + range, origin.getZ() + range))) {
            // Filter to range
            if (entity.blockPosition().distManhattan(origin) > range) continue;
            if (entity instanceof Player) continue;

            totalMobCount++;

            // Check if this is a parasite entity
            ResourceLocation entityKey = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
            if (entityKey != null && entityKey.getNamespace().equals(SubspaceParasite.MOD_ID)) {
                parasiteCount++;
            }
        }

        // Create a written paper item with scan results
        ItemStack paper = new ItemStack(net.minecraft.world.item.Items.PAPER);
        CompoundTag tag = paper.getOrCreateTag();
        CompoundTag display = tag.getCompound("display");
        ListTag loreTag = new ListTag();
        loreTag.add(net.minecraft.nbt.StringTag.valueOf(net.minecraft.network.chat.Component.Serializer.toJson(
                Component.translatable("item.subspaceparasite.scan_report.header"))));
        loreTag.add(net.minecraft.nbt.StringTag.valueOf(net.minecraft.network.chat.Component.Serializer.toJson(
                Component.translatable("item.subspaceparasite.scan_report.total_mobs", totalMobCount))));
        loreTag.add(net.minecraft.nbt.StringTag.valueOf(net.minecraft.network.chat.Component.Serializer.toJson(
                Component.translatable("item.subspaceparasite.scan_report.total_parasites", parasiteCount))));
        display.put("Lore", loreTag);
        tag.put("display", display);
        paper.setTag(tag);
        paper.setHoverName(Component.translatable("item.subspaceparasite.scan_report.name"));

        if (!player.getInventory().add(paper)) {
            player.drop(paper, false);
        }
    }

    // ==================== Scan Trigger ====================

    public boolean performScan(ServerPlayer player) {
        if (this.level == null || this.level.isClientSide) return false;

        if (!this.formed) {
            player.sendSystemMessage(Component.translatable("chat.subspaceparasite.relay.not_formed"));
            return false;
        }

        ItemStack stack = this.itemHandler.getStackInSlot(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemModule)) {
            player.sendSystemMessage(Component.translatable("chat.subspaceparasite.relay.insert_module"));
            return false;
        }

        ItemModule.ModuleKind kind = ((ItemModule) stack.getItem()).getModuleKind(stack);

        if (this.scanPending) {
            player.sendSystemMessage(Component.translatable("chat.subspaceparasite.relay.scan_busy"));
            return false;
        }

        this.scanPending = true;
        this.scanTicks = 110; // ~5.5 seconds
        this.scanPlayer = player.getUUID();
        this.scanKind = kind;
        this.level.playSound(null, this.worldPosition, ModSounds.RELAY_ACTIVATE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        player.sendSystemMessage(Component.translatable("chat.subspaceparasite.relay.scan_started"));
        this.startCooldown();
        this.setChanged();
        return true;
    }

    // ==================== Dismantle ====================

    public void dismantle() {
        if (this.level == null || this.level.isClientSide) return;
        if (this.dismantling) return;
        this.dismantling = true;

        try {
            List<BlockPos> targets = new ArrayList<>(this.childPositions);
            if (targets.isEmpty()) {
                targets = this.findChildrenByScan();
            }

            this.formed = false;
            this.dropContents();

            for (BlockPos p : targets) {
                if (p == null || !this.level.isLoaded(p)) continue;
                if (this.level.getBlockState(p).getBlock() == ModBlocks.NODE_RELAY.get()) {
                    this.level.destroyBlock(p, false);
                }
            }

            this.childPositions.clear();
            this.setChanged();

            // Remove self
            if (this.level.isLoaded(this.worldPosition) &&
                    this.level.getBlockState(this.worldPosition).getBlock() == ModBlocks.RELAY_CONTROLLER.get()) {
                this.level.destroyBlock(this.worldPosition, true);
            }
        } finally {
            this.dismantling = false;
        }
    }

    private List<BlockPos> findChildrenByScan() {
        List<BlockPos> found = new ArrayList<>();
        if (this.level == null) return found;

        BlockPos origin = this.worldPosition;
        for (int dx = -8; dx <= 8; dx++) {
            for (int dy = -8; dy <= 8; dy++) {
                for (int dz = -8; dz <= 8; dz++) {
                    BlockPos p = origin.offset(dx, dy, dz);
                    if (p.equals(origin)) continue;
                    if (!this.level.isLoaded(p)) continue;
                    if (this.level.getBlockState(p).getBlock() == ModBlocks.NODE_RELAY.get()) {
                        found.add(p);
                    }
                }
            }
        }
        return found;
    }

    /**
     * Re-link all child NodeRelay entities to this controller.
     * Called on load.
     */
    private void relinkChildrenToMe() {
        if (this.level == null || this.level.isClientSide) return;
        for (BlockPos p : this.childPositions) {
            if (p == null || !this.level.isLoaded(p)) continue;
            BlockEntity be = this.level.getBlockEntity(p);
            if (be instanceof NodeRelayBlockEntity relay) {
                relay.setControllerPos(this.worldPosition);
            }
        }
    }

    // ==================== Capabilities ====================

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.handlerLazy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.handlerLazy.invalidate();
    }

    // ==================== NBT ====================

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.nextScanTick = tag.getLong(NBT_NEXT_SCAN);
        this.formed = tag.getBoolean("Formed");

        this.childPositions.clear();
        if (tag.contains("Children", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Children", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                this.childPositions.add(NbtUtils.readBlockPos(list.getCompound(i)));
            }
        }

        if (tag.contains("Inv", Tag.TAG_COMPOUND)) {
            this.itemHandler.deserializeNBT(tag.getCompound("Inv"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("Formed", this.formed);
        tag.putLong(NBT_NEXT_SCAN, this.nextScanTick);

        ListTag list = new ListTag();
        for (BlockPos p : this.childPositions) {
            if (p != null) {
                list.add(NbtUtils.writeBlockPos(p));
            }
        }
        tag.put("Children", list);
        tag.put("Inv", this.itemHandler.serializeNBT());
    }

    // ==================== Sync ====================

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            BlockState s = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, s, s, 3);
        }
    }

    // ==================== onLoad ====================

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.handlerLazy.invalidate();
    }

    /**
     * Called when the chunk is loaded. Re-links children.
     */
    public void onLoad() {
        if (this.level == null) return;
        if (!this.level.isClientSide) {
            this.relinkChildrenToMe();
            if (this.nextScanTick < 0L) {
                this.nextScanTick = 0L;
                this.setChanged();
            }
        }
    }
}
