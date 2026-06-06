package com.subspaceparasite.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Server-to-Client packet notifying the client that a multi-part entity
 * was hit. Used for visual feedback (hit particles, damage numbers, etc.)
 * on the client when a body part of a multi-part parasite is damaged.
 * Ported from SPPacketEntityBodyHit (1.12) to 1.20.1.
 */
public class S2CEntityBodyHitPacket implements SPacket {

    private final int targetId;
    private final int partId;

    public S2CEntityBodyHitPacket(int targetId, int partId) {
        this.targetId = targetId;
        this.partId = partId;
    }

    public S2CEntityBodyHitPacket(FriendlyByteBuf buf) {
        this.targetId = buf.readVarInt();
        this.partId = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.targetId);
        buf.writeVarInt(this.partId);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientLevel level = Minecraft.getInstance().level;
                if (level == null) return;

                Entity target = level.getEntity(this.targetId);
                if (target == null) return;

                // Visual feedback for body part hit - spawn hit particles
                double px = target.getX();
                double py = target.getY() + target.getBbHeight() * 0.5;
                double pz = target.getZ();

                level.addParticle(net.minecraft.core.particles.ParticleTypes.DAMAGE_INDICATOR,
                        px, py, pz,
                        (level.random.nextFloat() - 0.5) * 0.3,
                        0.1 + level.random.nextFloat() * 0.2,
                        (level.random.nextFloat() - 0.5) * 0.3);
            });
        });
        context.get().setPacketHandled(true);
    }

    public int getTargetId() { return this.targetId; }
    public int getPartId() { return this.partId; }
}
