package com.subspaceparasite.common.network;

import com.subspaceparasite.client.ClientData;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.entity.base.EvolutionComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/**
 * Server-to-Client packet for syncing entity evolution level data.
 * <p>
 * When a parasite entity's evolution state changes on the server,
 * this packet is sent to all tracking clients so they can update
 * the entity's evolution component data for rendering and UI purposes.
 * <p>
 * The client-side handler finds the entity by its network ID and, if it
 * is an {@link EntityParasiteBase}, updates its evolution component's
 * level and points.
 */
public class EvolutionSyncPacket {

    public int entityId;
    public int evolutionLevel;
    public float evolutionPoints;

    public EvolutionSyncPacket(int entityId, int evolutionLevel, float evolutionPoints) {
        this.entityId = entityId;
        this.evolutionLevel = evolutionLevel;
        this.evolutionPoints = evolutionPoints;
    }

    public static void encode(EvolutionSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeInt(msg.evolutionLevel);
        buf.writeFloat(msg.evolutionPoints);
    }

    public static EvolutionSyncPacket decode(FriendlyByteBuf buf) {
        return new EvolutionSyncPacket(buf.readInt(), buf.readInt(), buf.readFloat());
    }

    public static void handle(EvolutionSyncPacket msg, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null) return;

                Entity entity = mc.level.getEntity(msg.entityId);
                if (entity == null) return;

                if (entity instanceof EntityParasiteBase parasite) {
                    EvolutionComponent evoComp = parasite.getEvolutionComponent();
                    if (evoComp != null) {
                        // Update the client-side evolution component data
                        // These directly set the component's internal fields
                        // for client-side rendering and UI purposes
                        parasite.setEvolutionPoints((int) msg.evolutionPoints);
                        evoComp.setEvolutionLevel(msg.evolutionLevel);
                    }
                }
            });
        });
    }
}
