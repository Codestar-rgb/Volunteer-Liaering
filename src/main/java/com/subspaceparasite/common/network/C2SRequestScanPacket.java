package com.subspaceparasite.common.network;

import com.subspaceparasite.SubspaceParasite;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Client-to-Server packet for requesting a parasite scan.
 * Sent when the player presses the scan key binding.
 * Server validates and returns scan results.
 */
public class C2SRequestScanPacket implements SPacket {

    private final double range;
    private final ScanType scanType;

    /**
     * Types of scans the client can request.
     */
    public enum ScanType {
        AREA_SCAN(0),       // Scan for nearby parasites in range
        ENTITY_SCAN(1),     // Scan a specific targeted entity
        BIOME_SCAN(2);      // Scan the current biome for infestation level

        private final int id;

        ScanType(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static ScanType fromId(int id) {
            for (ScanType type : values()) {
                if (type.id == id) return type;
            }
            return AREA_SCAN;
        }
    }

    public C2SRequestScanPacket(double range, ScanType scanType) {
        this.range = range;
        this.scanType = scanType;
    }

    public C2SRequestScanPacket(FriendlyByteBuf buf) {
        this.range = buf.readDouble();
        this.scanType = ScanType.fromId(buf.readVarInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.range);
        buf.writeVarInt(this.scanType.getId());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // Server-side handling
            ServerPlayer player = context.get().getSender();
            if (player == null) return;

            // Validate scan request
            if (this.range <= 0 || this.range > 64.0) {
                // Cap scan range to prevent abuse; reject negative/zero range
                return;
            }

            // Check cooldown or item requirements
            // if (!canPlayerScan(player)) return;

            // Process scan based on type
            switch (this.scanType) {
                case AREA_SCAN:
                    handleAreaScan(player);
                    break;
                case ENTITY_SCAN:
                    handleEntityScan(player);
                    break;
                case BIOME_SCAN:
                    handleBiomeScan(player);
                    break;
            }
        });
        context.get().setPacketHandled(true);
    }

    /**
     * Handle an area scan request - find nearby parasites.
     */
    private void handleAreaScan(ServerPlayer player) {
        // Scan for parasites in range and send results back
        // This would query the server-level entity lists
        // and send a response packet with parasite counts/types
        // Implementation deferred to entity registration phase
    }

    /**
     * Handle an entity scan request - get details about targeted entity.
     */
    private void handleEntityScan(ServerPlayer player) {
        // Get the entity the player is looking at
        // and send detailed information back
        // Implementation deferred to entity registration phase
    }

    /**
     * Handle a biome scan request - check infestation level.
     */
    private void handleBiomeScan(ServerPlayer player) {
        // Check the biome's infestation level and send back
        // Implementation deferred to world generation phase
    }

    public double getRange() {
        return this.range;
    }

    public ScanType getScanType() {
        return this.scanType;
    }
}
