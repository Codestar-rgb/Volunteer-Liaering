package com.subspaceparasite.common.network;

import com.subspaceparasite.SubspaceParasite;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Server-to-Client packet for syncing evolution phase data.
 * Updates the client-side phase cache used for fog rendering and overlay effects.
 */
public class S2CPhaseSyncPacket implements SPacket {

    private final int phase;
    private final float phaseProgress;

    public S2CPhaseSyncPacket(int phase, float phaseProgress) {
        this.phase = phase;
        this.phaseProgress = phaseProgress;
    }

    public S2CPhaseSyncPacket(FriendlyByteBuf buf) {
        this.phase = buf.readVarInt();
        this.phaseProgress = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.phase);
        buf.writeFloat(this.phaseProgress);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                // Update the client-side phase cache
                com.subspaceparasite.client.fog.FogHandler.ClientPhaseCache.setPhase(this.phase);

                // Phase progress can be used for interpolation in future features
                // For now, store it for potential overlay effects
            });
        });
        context.get().setPacketHandled(true);
    }

    public int getPhase() {
        return this.phase;
    }

    public float getPhaseProgress() {
        return this.phaseProgress;
    }
}
