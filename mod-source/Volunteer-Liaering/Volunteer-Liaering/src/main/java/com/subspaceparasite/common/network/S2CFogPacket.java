package com.subspaceparasite.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Server-to-Client packet for controlling fog effects.
 * Sends fog density and color parameters to the client for
 * phase-appropriate fog rendering.
 * Ported from SPPacketFog (1.12) to 1.20.1.
 */
public class S2CFogPacket implements SPacket {

    private final float fogDensity;
    private final float fogRed;
    private final float fogGreen;
    private final float fogBlue;

    public S2CFogPacket(float fogDensity, float fogRed, float fogGreen, float fogBlue) {
        this.fogDensity = fogDensity;
        this.fogRed = fogRed;
        this.fogGreen = fogGreen;
        this.fogBlue = fogBlue;
    }

    public S2CFogPacket(FriendlyByteBuf buf) {
        this.fogDensity = buf.readFloat();
        this.fogRed = buf.readFloat();
        this.fogGreen = buf.readFloat();
        this.fogBlue = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(this.fogDensity);
        buf.writeFloat(this.fogRed);
        buf.writeFloat(this.fogGreen);
        buf.writeFloat(this.fogBlue);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                // Store fog parameters for the FogHandler to use
                ClientFogState.setFog(this.fogDensity, this.fogRed, this.fogGreen, this.fogBlue);
            });
        });
        context.get().setPacketHandled(true);
    }

    public float getFogDensity() { return this.fogDensity; }
    public float getFogRed() { return this.fogRed; }
    public float getFogGreen() { return this.fogGreen; }
    public float getFogBlue() { return this.fogBlue; }

    /**
     * Client-side fog state storage. The FogHandler reads from this
     * when S2CFogPacket overrides the phase-based fog calculation.
     */
    public static class ClientFogState {
        private static float fogDensity = 0.0f;
        private static float fogRed = 1.0f;
        private static float fogGreen = 1.0f;
        private static float fogBlue = 1.0f;
        private static boolean hasCustomFog = false;

        public static void setFog(float density, float red, float green, float blue) {
            fogDensity = density;
            fogRed = red;
            fogGreen = green;
            fogBlue = blue;
            hasCustomFog = density > 0.001f;
        }

        public static float getFogDensity() { return fogDensity; }
        public static float getFogRed() { return fogRed; }
        public static float getFogGreen() { return fogGreen; }
        public static float getFogBlue() { return fogBlue; }
        public static boolean hasCustomFog() { return hasCustomFog; }

        public static void reset() {
            fogDensity = 0.0f;
            fogRed = 1.0f;
            fogGreen = 1.0f;
            fogBlue = 1.0f;
            hasCustomFog = false;
        }
    }
}
