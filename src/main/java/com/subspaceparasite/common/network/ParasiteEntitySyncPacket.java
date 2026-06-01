package com.subspaceparasite.common.network;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/**
 * Server-to-Client packet for syncing parasite entity variant and sentient data.
 * <p>
 * Sent when a parasite entity's visual variant or sentient status changes
 * on the server. The client handler locates the entity by its network ID
 * and updates its skin variant and other sync data for correct rendering.
 * <p>
 * The variant value corresponds to the entity's skin variant byte,
 * which affects texture selection in the renderer. The sentient flag
 * indicates whether the parasite has achieved sentient status, which
 * may trigger special visual effects (glow, aura, etc.).
 */
public class ParasiteEntitySyncPacket {

    public int entityId;
    public int variant;
    public boolean sentient;

    public ParasiteEntitySyncPacket(int entityId, int variant, boolean sentient) {
        this.entityId = entityId;
        this.variant = variant;
        this.sentient = sentient;
    }

    public static void encode(ParasiteEntitySyncPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeInt(msg.variant);
        buf.writeBoolean(msg.sentient);
    }

    public static ParasiteEntitySyncPacket decode(FriendlyByteBuf buf) {
        return new ParasiteEntitySyncPacket(buf.readInt(), buf.readInt(), buf.readBoolean());
    }

    public static void handle(ParasiteEntitySyncPacket msg, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null) return;

                Entity entity = mc.level.getEntity(msg.entityId);
                if (entity == null) return;

                if (entity instanceof EntityParasiteBase parasite) {
                    // Update the skin variant for renderer texture selection
                    parasite.setSkinVariant((byte) msg.variant);

                    // Sentient entities get a glow flag for rendering
                    if (msg.sentient) {
                        parasite.setHasGlow(true);
                    }

                    // Update infection overlay level for sentient parasites
                    // (sentient parasites have a visible aura/overlay)
                    if (msg.sentient && parasite.getInfectionOverlayLevel() < 0.3f) {
                        parasite.setInfectionOverlayLevel(0.3f);
                    }
                }
            });
        });
    }
}
