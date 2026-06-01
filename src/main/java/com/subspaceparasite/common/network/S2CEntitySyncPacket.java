package com.subspaceparasite.common.network;

import com.subspaceparasite.SubspaceParasite;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Server-to-Client packet for syncing entity data.
 * Used to synchronize parasite-specific entity data that isn't
 * handled by the vanilla entity tracker.
 */
public class S2CEntitySyncPacket implements SPacket {

    private final int entityId;
    private final byte syncFlags;
    private final int data1;
    private final int data2;
    private final float data3;

    // Sync flags for which data to update
    public static final byte SYNC_TIER = 0x01;
    public static final byte SYNC_GENE = 0x02;
    public static final byte SYNC_STATE = 0x04;
    public static final byte SYNC_CLOAK = 0x08;
    public static final byte SYNC_LEADER = 0x10;
    public static final byte SYNC_SELF_DESTRUCT = 0x20;
    public static final byte SYNC_ALL = 0x3F;

    public S2CEntitySyncPacket(int entityId, byte syncFlags, int data1, int data2, float data3) {
        this.entityId = entityId;
        this.syncFlags = syncFlags;
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
    }

    public S2CEntitySyncPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.syncFlags = buf.readByte();
        this.data1 = buf.readVarInt();
        this.data2 = buf.readVarInt();
        this.data3 = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeByte(this.syncFlags);
        buf.writeVarInt(this.data1);
        buf.writeVarInt(this.data2);
        buf.writeFloat(this.data3);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.level == null) return;

                Entity entity = mc.level.getEntity(this.entityId);
                if (entity == null) return;

                // Apply sync data on client
                handleClientSync(entity);
            });
        });
        context.get().setPacketHandled(true);
    }

    /**
     * Apply the synced data to the entity on the client side.
     */
    private void handleClientSync(Entity entity) {
        // This would cast to EntityParasiteBase and apply specific data
        // based on syncFlags. Implemented when entity base class is available.

        if ((this.syncFlags & SYNC_TIER) != 0) {
            // entity.setParasiteTier(data1);
        }
        if ((this.syncFlags & SYNC_GENE) != 0) {
            // entity.setGene(data2);
        }
        if ((this.syncFlags & SYNC_STATE) != 0) {
            // entity.setWorkingState(data1);
        }
        if ((this.syncFlags & SYNC_CLOAK) != 0) {
            // entity.setCloakingLevel(data3);
        }
        if ((this.syncFlags & SYNC_LEADER) != 0) {
            // entity.setLeaderId(data1);
        }
        if ((this.syncFlags & SYNC_SELF_DESTRUCT) != 0) {
            // entity.setSelfDestructState(data1);
        }
    }

    // ==================== Builder helpers ====================

    public static S2CEntitySyncPacket syncTier(int entityId, int tier) {
        return new S2CEntitySyncPacket(entityId, SYNC_TIER, tier, 0, 0.0f);
    }

    public static S2CEntitySyncPacket syncGene(int entityId, int gene) {
        return new S2CEntitySyncPacket(entityId, SYNC_GENE, 0, gene, 0.0f);
    }

    public static S2CEntitySyncPacket syncCloak(int entityId, float cloakLevel) {
        return new S2CEntitySyncPacket(entityId, SYNC_CLOAK, 0, 0, cloakLevel);
    }

    public static S2CEntitySyncPacket syncLeader(int entityId, int leaderId) {
        return new S2CEntitySyncPacket(entityId, SYNC_LEADER, leaderId, 0, 0.0f);
    }
}
