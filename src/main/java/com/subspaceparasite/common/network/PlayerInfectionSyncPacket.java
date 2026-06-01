package com.subspaceparasite.common.network;

import com.subspaceparasite.client.ClientData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/**
 * Server-to-Client packet for syncing player infection data.
 * <p>
 * Sent when the player's infection or virulence level changes on the server.
 * The client stores these values in {@link ClientData} for use by
 * {@link com.subspaceparasite.client.overlay.InfectionOverlayHandler InfectionOverlayHandler}
 * to render the HUD infection overlay.
 * <p>
 * Infection level represents how corrupted the player is by the COTH effect.
 * Virulence level represents how infectious the player is to nearby entities.
 */
public class PlayerInfectionSyncPacket {

    public float infectionLevel;
    public float virulenceLevel;

    public PlayerInfectionSyncPacket(float infectionLevel, float virulenceLevel) {
        this.infectionLevel = infectionLevel;
        this.virulenceLevel = virulenceLevel;
    }

    public static void encode(PlayerInfectionSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.infectionLevel);
        buf.writeFloat(msg.virulenceLevel);
    }

    public static PlayerInfectionSyncPacket decode(FriendlyByteBuf buf) {
        return new PlayerInfectionSyncPacket(buf.readFloat(), buf.readFloat());
    }

    public static void handle(PlayerInfectionSyncPacket msg, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                // Store player infection data for the HUD overlay
                ClientData.CLIENT_PLAYER_INFECTION = msg.infectionLevel;
                ClientData.CLIENT_PLAYER_VIRULENCE = msg.virulenceLevel;
            });
        });
    }
}
