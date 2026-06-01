package com.subspaceparasite.common.network;

import com.subspaceparasite.client.ClientData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/**
 * Server-to-Client packet for syncing biome infection state.
 * <p>
 * Sent when a chunk's infection level changes on the server. The client
 * stores this data in {@link ClientData#CLIENT_INFECTION_DATA} for use
 * by the infection overlay handler and fog renderer.
 * <p>
 * Infection levels are stored per-chunk using a long key computed from
 * the chunk coordinates, matching {@code ChunkPos.toLong()}.
 */
public class BiomeInfectionSyncPacket {

    public int chunkX;
    public int chunkZ;
    public float infectionLevel;

    public BiomeInfectionSyncPacket(int chunkX, int chunkZ, float infectionLevel) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.infectionLevel = infectionLevel;
    }

    public static void encode(BiomeInfectionSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.chunkX);
        buf.writeInt(msg.chunkZ);
        buf.writeFloat(msg.infectionLevel);
    }

    public static BiomeInfectionSyncPacket decode(FriendlyByteBuf buf) {
        return new BiomeInfectionSyncPacket(buf.readInt(), buf.readInt(), buf.readFloat());
    }

    public static void handle(BiomeInfectionSyncPacket msg, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                // Store the infection level in the client-side data map
                // Used by InfectionOverlayHandler and FogHandler for visual effects
                ClientData.setChunkInfection(msg.chunkX, msg.chunkZ, msg.infectionLevel);
            });
        });
    }
}
