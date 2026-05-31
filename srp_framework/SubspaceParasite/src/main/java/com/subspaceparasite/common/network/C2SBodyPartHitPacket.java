package com.subspaceparasite.common.network;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.common.entity.ai.misc.EntityBodyParts;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Client-to-Server packet for handling body part hit detection.
 * Sent when a player hits a specific body part of a parasite entity.
 * Server validates and processes damage accordingly.
 */
public class C2SBodyPartHitPacket implements SPacket {

    private final int entityId;
    private final int partIndex;
    private final float damage;
    private final double hitX;
    private final double hitY;
    private final double hitZ;

    /**
     * Create a body part hit packet with all parameters.
     * 
     * @param entityId The ID of the entity being hit
     * @param partIndex The index of the body part hit (0 = main body, 1+ = specific parts)
     * @param damage The damage dealt
     * @param hitX Hit position X coordinate
     * @param hitY Hit position Y coordinate
     * @param hitZ Hit position Z coordinate
     */
    public C2SBodyPartHitPacket(int entityId, int partIndex, float damage, 
                                 double hitX, double hitY, double hitZ) {
        this.entityId = entityId;
        this.partIndex = partIndex;
        this.damage = damage;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
    }

    /**
     * Decode from network buffer.
     */
    public C2SBodyPartHitPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.partIndex = buf.readVarInt();
        this.damage = buf.readFloat();
        this.hitX = buf.readDouble();
        this.hitY = buf.readDouble();
        this.hitZ = buf.readDouble();
    }

    /**
     * Encode to network buffer.
     */
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.partIndex);
        buf.writeFloat(this.damage);
        buf.writeDouble(this.hitX);
        buf.writeDouble(this.hitY);
        buf.writeDouble(this.hitZ);
    }

    /**
     * Handle this packet on the server side.
     */
    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) return;

            // Validate packet data
            if (this.entityId <= 0 || this.partIndex < 0) {
                SubspaceParasite.LOGGER.warn("Invalid body part hit packet: entityId={}, partIndex={}", 
                    this.entityId, this.partIndex);
                return;
            }

            // Validate damage range
            if (this.damage < 0.0F || this.damage > 1000.0F) {
                SubspaceParasite.LOGGER.warn("Invalid damage value in body part hit packet: {}", this.damage);
                return;
            }

            // Get the target entity
            Entity target = player.level().getEntity(this.entityId);
            if (target == null || !target.isAlive()) {
                SubspaceParasite.LOGGER.debug("Target entity {} not found or dead", this.entityId);
                return;
            }

            // Check if entity supports body parts
            if (target instanceof EntityBodyParts bodyPartsEntity) {
                // Validate part index
                int maxParts = bodyPartsEntity.getBodyPartsCount();
                if (this.partIndex >= maxParts) {
                    SubspaceParasite.LOGGER.warn("Invalid part index {} for entity with {} parts", 
                        this.partIndex, maxParts);
                    return;
                }

                // Validate hit distance (anti-cheat)
                double distSq = player.distanceToSqr(this.hitX, this.hitY, this.hitZ);
                if (distSq > 64.0 * 64.0) {
                    SubspaceParasite.LOGGER.warn("Hit too far away: distance={}", Math.sqrt(distSq));
                    return;
                }

                // Process the body part hit
                bodyPartsEntity.onBodyPartHit(this.partIndex, player, this.damage, 
                    this.hitX, this.hitY, this.hitZ);
                
                SubspaceParasite.LOGGER.debug("Body part hit processed: entity={}, part={}, damage={}", 
                    this.entityId, this.partIndex, this.damage);
            } else {
                SubspaceParasite.LOGGER.debug("Entity {} does not support body parts", this.entityId);
            }
        });
        context.get().setPacketHandled(true);
    }

    /**
     * Get the target entity ID.
     */
    public int getEntityId() {
        return this.entityId;
    }

    /**
     * Get the body part index that was hit.
     */
    public int getPartIndex() {
        return this.partIndex;
    }

    /**
     * Get the damage dealt.
     */
    public float getDamage() {
        return this.damage;
    }

    /**
     * Get the hit position X coordinate.
     */
    public double getHitX() {
        return this.hitX;
    }

    /**
     * Get the hit position Y coordinate.
     */
    public double getHitY() {
        return this.hitY;
    }

    /**
     * Get the hit position Z coordinate.
     */
    public double getHitZ() {
        return this.hitZ;
    }
}
