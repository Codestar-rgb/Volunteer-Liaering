package com.subspaceparasite.common.network;

import com.subspaceparasite.client.ClientData;
import com.subspaceparasite.client.ClientData.ColonyData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/**
 * Server-to-Client packet for syncing colony heart status.
 * <p>
 * Sent when a colony heart block entity updates its state on the server.
 * The client stores this data in {@link ClientData#CLIENT_COLONY_DATA}
 * for GUI display (e.g., colony overview screens, beacon-like rendering).
 * <p>
 * Colony data includes the colony's evolution level and the number of
 * parasites belonging to the colony.
 */
public class ColonyHeartSyncPacket {

    public int blockEntityId;
    public int colonyLevel;
    public int parasiteCount;

    public ColonyHeartSyncPacket(int blockEntityId, int colonyLevel, int parasiteCount) {
        this.blockEntityId = blockEntityId;
        this.colonyLevel = colonyLevel;
        this.parasiteCount = parasiteCount;
    }

    public static void encode(ColonyHeartSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.blockEntityId);
        buf.writeInt(msg.colonyLevel);
        buf.writeInt(msg.parasiteCount);
    }

    public static ColonyHeartSyncPacket decode(FriendlyByteBuf buf) {
        return new ColonyHeartSyncPacket(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void handle(ColonyHeartSyncPacket msg, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                // Store or update the colony data on the client
                ClientData.CLIENT_COLONY_DATA.put(
                        msg.blockEntityId,
                        new ColonyData(msg.colonyLevel, msg.parasiteCount)
                );
            });
        });
    }
}
