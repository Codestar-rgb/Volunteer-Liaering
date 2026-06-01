package com.subspaceparasite.common.network;

import com.subspaceparasite.common.entity.ai.misc.EntityBodyParts;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Client-to-Server packet reporting that a client hit a body part of
 * a multi-part parasite entity. The server validates the hit and
 * applies damage to the correct body part.
 * Ported from SPPacketEntityBodyHit (1.12) to 1.20.1.
 */
public class C2SBodyPartHitPacket implements SPacket {

    private final int targetId;
    private final int partId;

    public C2SBodyPartHitPacket(int targetId, int partId) {
        this.targetId = targetId;
        this.partId = partId;
    }

    public C2SBodyPartHitPacket(FriendlyByteBuf buf) {
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
            ServerPlayer player = context.get().getSender();
            if (player == null) return;

            Entity target = player.level().getEntity(this.targetId);
            if (target == null) return;

            if (target instanceof EntityBodyParts bodyParts) {
                // Calculate damage from player's attack attribute
                float dmg = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float attackStrength = player.getAttackStrengthScale(0.0f);
                float finalDamage = attackStrength * dmg;

                // Apply damage to the specific body part
                bodyParts.attackEntityBodyFrom(
                        player.level().damageSources().playerAttack(player),
                        finalDamage,
                        this.partId,
                        true
                );
            }
        });
        context.get().setPacketHandled(true);
    }

    public int getTargetId() {
        return this.targetId;
    }

    public int getPartId() {
        return this.partId;
    }
}
