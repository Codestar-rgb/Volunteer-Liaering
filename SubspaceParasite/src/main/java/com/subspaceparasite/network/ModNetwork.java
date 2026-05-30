package com.subspaceparasite.network;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.common.network.C2SRequestScanPacket;
import com.subspaceparasite.common.network.S2CEntitySyncPacket;
import com.subspaceparasite.common.network.S2CPhaseSyncPacket;
import com.subspaceparasite.common.world.CelestialManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Network channel setup for the SubspaceParasite mod.
 * Handles all client-server communication for parasite evolution,
 * biome infection, entity sync, and other systems.
 */
public class ModNetwork {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SubspaceParasite.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    /**
     * Register all packet types. Called during mod construction.
     */
    public static void register() {
        // === Evolution Packets ===
        registerPacket(EvolutionSyncPacket.class,
                EvolutionSyncPacket::encode,
                EvolutionSyncPacket::decode,
                EvolutionSyncPacket::handle,
                NetworkDirection.PLAY_TO_CLIENT
        );

        // === Biome Infection Packets ===
        registerPacket(BiomeInfectionSyncPacket.class,
                BiomeInfectionSyncPacket::encode,
                BiomeInfectionSyncPacket::decode,
                BiomeInfectionSyncPacket::handle,
                NetworkDirection.PLAY_TO_CLIENT
        );

        // === Colony Heart Packets ===
        registerPacket(ColonyHeartSyncPacket.class,
                ColonyHeartSyncPacket::encode,
                ColonyHeartSyncPacket::decode,
                ColonyHeartSyncPacket::handle,
                NetworkDirection.PLAY_TO_CLIENT
        );

        // === Entity Sync Packets ===
        registerPacket(ParasiteEntitySyncPacket.class,
                ParasiteEntitySyncPacket::encode,
                ParasiteEntitySyncPacket::decode,
                ParasiteEntitySyncPacket::handle,
                NetworkDirection.PLAY_TO_CLIENT
        );

        // === Player Infection Packet ===
        registerPacket(PlayerInfectionSyncPacket.class,
                PlayerInfectionSyncPacket::encode,
                PlayerInfectionSyncPacket::decode,
                PlayerInfectionSyncPacket::handle,
                NetworkDirection.PLAY_TO_CLIENT
        );

        // === Particle Packets ===
        registerPacket(ParasiteParticlePacket.class,
                ParasiteParticlePacket::encode,
                ParasiteParticlePacket::decode,
                ParasiteParticlePacket::handle,
                NetworkDirection.PLAY_TO_CLIENT
        );

        // === Server-bound Action Packets ===
        registerPacket(EvolutionActionPacket.class,
                EvolutionActionPacket::encode,
                EvolutionActionPacket::decode,
                EvolutionActionPacket::handle,
                NetworkDirection.PLAY_TO_SERVER
        );

        // === Celestial Sync Packets ===
        registerPacket(CelestialManager.CelestialSyncPacket.class,
                CelestialManager.CelestialSyncPacket::encode,
                CelestialManager.CelestialSyncPacket::decode,
                CelestialManager.CelestialSyncPacket::handle,
                NetworkDirection.PLAY_TO_CLIENT
        );

        // === C2S Request Scan Packet ===
        registerPacket(C2SRequestScanPacket.class,
                C2SRequestScanPacket::encode,
                C2SRequestScanPacket::new,
                (msg, ctx) -> msg.handle(() -> ctx),
                NetworkDirection.PLAY_TO_SERVER
        );

        // === S2C Entity Sync Packet ===
        registerPacket(S2CEntitySyncPacket.class,
                S2CEntitySyncPacket::encode,
                S2CEntitySyncPacket::new,
                (msg, ctx) -> msg.handle(() -> ctx),
                NetworkDirection.PLAY_TO_CLIENT
        );

        // === S2C Phase Sync Packet ===
        registerPacket(S2CPhaseSyncPacket.class,
                S2CPhaseSyncPacket::encode,
                S2CPhaseSyncPacket::new,
                (msg, ctx) -> msg.handle(() -> ctx),
                NetworkDirection.PLAY_TO_CLIENT
        );
    }

    private static <MSG> void registerPacket(Class<MSG> messageType,
                                              BiConsumer<MSG, FriendlyByteBuf> encoder,
                                              Function<FriendlyByteBuf, MSG> decoder,
                                              BiConsumer<MSG, net.minecraftforge.network.NetworkEvent.Context> consumer,
                                              NetworkDirection direction) {
        CHANNEL.registerMessage(packetId++, messageType, encoder, decoder,
                (msg, ctxSupplier) -> {
                    net.minecraftforge.network.NetworkEvent.Context ctx = ctxSupplier.get();
                    consumer.accept(msg, ctx);
                    ctx.setPacketHandled(true);
                },
                java.util.Optional.of(direction));
    }

    // ========================
    // Packet placeholder classes
    // ========================

    /**
     * Syncs evolution level data to clients.
     */
    public static class EvolutionSyncPacket {
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

        public static void handle(EvolutionSyncPacket msg, net.minecraftforge.network.NetworkEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                // Client-side handling: update entity evolution data
            });
        }
    }

    /**
     * Syncs biome infection state to clients.
     */
    public static class BiomeInfectionSyncPacket {
        public int chunkX;
        public int chunkZ;
        public float infectionLevel;

        public BiomeInfectionSyncPacket(int chunkX, int chunkZ, float infectionLevel) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.infectionLevel = infectionLevel;
        }

        public static void encode(BiomeInfectionSyncPacket msg, FriendlyByteBuf buf) {
            buf.writeInt(msg.chunkX);
            buf.writeInt(msg.chunkZ);
            buf.writeFloat(msg.infectionLevel);
        }

        public static BiomeInfectionSyncPacket decode(FriendlyByteBuf buf) {
            return new BiomeInfectionSyncPacket(buf.readInt(), buf.readInt(), buf.readFloat());
        }

        public static void handle(BiomeInfectionSyncPacket msg, net.minecraftforge.network.NetworkEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                // Client-side handling: update biome infection overlay
            });
        }
    }

    /**
     * Syncs colony heart status to clients.
     */
    public static class ColonyHeartSyncPacket {
        public int blockEntityId;
        public int colonyLevel;
        public int parasiteCount;

        public ColonyHeartSyncPacket(int blockEntityId, int colonyLevel, int parasiteCount) {
            this.blockEntityId = blockEntityId;
            this.colonyLevel = colonyLevel;
            this.parasiteCount = parasiteCount;
        }

        public static void encode(ColonyHeartSyncPacket msg, FriendlyByteBuf buf) {
            buf.writeInt(msg.blockEntityId);
            buf.writeInt(msg.colonyLevel);
            buf.writeInt(msg.parasiteCount);
        }

        public static ColonyHeartSyncPacket decode(FriendlyByteBuf buf) {
            return new ColonyHeartSyncPacket(buf.readInt(), buf.readInt(), buf.readInt());
        }

        public static void handle(ColonyHeartSyncPacket msg, net.minecraftforge.network.NetworkEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                // Client-side handling: update colony heart display
            });
        }
    }

    /**
     * Syncs parasite entity data to clients.
     */
    public static class ParasiteEntitySyncPacket {
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

        public static void handle(ParasiteEntitySyncPacket msg, net.minecraftforge.network.NetworkEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                // Client-side handling: update parasite entity renderer
            });
        }
    }

    /**
     * Syncs player infection data to clients.
     */
    public static class PlayerInfectionSyncPacket {
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

        public static void handle(PlayerInfectionSyncPacket msg, net.minecraftforge.network.NetworkEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                // Client-side handling: update infection overlay
            });
        }
    }

    /**
     * Spawns particles on the client.
     */
    public static class ParasiteParticlePacket {
        public double x, y, z;
        public int particleType;
        public int count;

        public ParasiteParticlePacket(double x, double y, double z, int particleType, int count) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.particleType = particleType;
            this.count = count;
        }

        public static void encode(ParasiteParticlePacket msg, FriendlyByteBuf buf) {
            buf.writeDouble(msg.x);
            buf.writeDouble(msg.y);
            buf.writeDouble(msg.z);
            buf.writeInt(msg.particleType);
            buf.writeInt(msg.count);
        }

        public static ParasiteParticlePacket decode(FriendlyByteBuf buf) {
            return new ParasiteParticlePacket(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readInt(), buf.readInt());
        }

        public static void handle(ParasiteParticlePacket msg, net.minecraftforge.network.NetworkEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                // Client-side handling: spawn particles
            });
        }
    }

    /**
     * Server-bound evolution action packet.
     */
    public static class EvolutionActionPacket {
        public int actionType;
        public int entityId;

        public EvolutionActionPacket(int actionType, int entityId) {
            this.actionType = actionType;
            this.entityId = entityId;
        }

        public static void encode(EvolutionActionPacket msg, FriendlyByteBuf buf) {
            buf.writeInt(msg.actionType);
            buf.writeInt(msg.entityId);
        }

        public static EvolutionActionPacket decode(FriendlyByteBuf buf) {
            return new EvolutionActionPacket(buf.readInt(), buf.readInt());
        }

        public static void handle(EvolutionActionPacket msg, net.minecraftforge.network.NetworkEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                // Server-side handling: process evolution action
            });
        }
    }

    private ModNetwork() {
        // Utility class - prevent instantiation
    }
}
