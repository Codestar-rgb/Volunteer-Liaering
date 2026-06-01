package com.subspaceparasite.common.network;

import com.subspaceparasite.client.fog.FogHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Server-to-Client packet for syncing evolution phase data.
 * Updates the client-side phase cache used for fog rendering and overlay effects.
 * Ported from SPPacketUpdateEvoPhaseClient (1.12) to 1.20.1.
 */
public class S2CUpdateEvoPhasePacket implements SPacket {

    private final int phase;
    private final boolean vector;

    public S2CUpdateEvoPhasePacket(int phase, boolean vector) {
        this.phase = phase;
        this.vector = vector;
    }

    public S2CUpdateEvoPhasePacket(FriendlyByteBuf buf) {
        this.phase = buf.readVarInt();
        this.vector = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.phase);
        buf.writeBoolean(this.vector);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                // Update the client-side phase cache for fog rendering
                FogHandler.ClientPhaseCache.setPhase(this.phase);

                // If vector flag is set, trigger visual/audio effect
                if (this.vector) {
                    // The original mod played a sound when vector activated
                    // This is now handled by the FogHandler's phase change detection
                }
            });
        });
        context.get().setPacketHandled(true);
    }

    public int getPhase() {
        return this.phase;
    }

    public boolean isVector() {
        return this.vector;
    }
}
