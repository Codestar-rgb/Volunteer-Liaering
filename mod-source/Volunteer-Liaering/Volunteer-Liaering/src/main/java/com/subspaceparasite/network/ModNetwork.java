package com.subspaceparasite.network;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.common.network.BiomeInfectionSyncPacket;
import com.subspaceparasite.common.network.C2SBodyPartHitPacket;
import com.subspaceparasite.common.network.C2SRequestScanPacket;
import com.subspaceparasite.common.network.ColonyHeartSyncPacket;
import com.subspaceparasite.common.network.EvolutionActionPacket;
import com.subspaceparasite.common.network.EvolutionSyncPacket;
import com.subspaceparasite.common.network.ParasiteEntitySyncPacket;
import com.subspaceparasite.common.network.ParasiteParticlePacket;
import com.subspaceparasite.common.network.PlayerInfectionSyncPacket;
import com.subspaceparasite.common.network.S2CEntityBodyHitPacket;
import com.subspaceparasite.common.network.S2CEntitySyncPacket;
import com.subspaceparasite.common.network.S2CFogPacket;
import com.subspaceparasite.common.network.S2CParticlePacket;
import com.subspaceparasite.common.network.S2CPhaseSyncPacket;
import com.subspaceparasite.common.network.S2CUpdateEvoPhasePacket;
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
 * <p>
 * All packet classes are in {@link com.subspaceparasite.common.network}
 * and implement their own encode/decode/handle logic.
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

        // === S2C Update Evo Phase Packet (from original SPPacketUpdateEvoPhaseClient) ===
        registerPacket(S2CUpdateEvoPhasePacket.class,
                S2CUpdateEvoPhasePacket::encode,
                S2CUpdateEvoPhasePacket::new,
                (msg, ctx) -> msg.handle(() -> ctx),
                NetworkDirection.PLAY_TO_CLIENT
        );

        // === S2C Entity Body Hit Packet (from original SPPacketEntityBodyHit S2C variant) ===
        registerPacket(S2CEntityBodyHitPacket.class,
                S2CEntityBodyHitPacket::encode,
                S2CEntityBodyHitPacket::new,
                (msg, ctx) -> msg.handle(() -> ctx),
                NetworkDirection.PLAY_TO_CLIENT
        );

        // === S2C Particle Packet (from original SPPacketParticle) ===
        registerPacket(S2CParticlePacket.class,
                S2CParticlePacket::encode,
                S2CParticlePacket::new,
                (msg, ctx) -> msg.handle(() -> ctx),
                NetworkDirection.PLAY_TO_CLIENT
        );

        // === S2C Fog Packet (from original SPPacketFog) ===
        registerPacket(S2CFogPacket.class,
                S2CFogPacket::encode,
                S2CFogPacket::new,
                (msg, ctx) -> msg.handle(() -> ctx),
                NetworkDirection.PLAY_TO_CLIENT
        );

        // === C2S Body Part Hit Packet (from original SPPacketEntityBodyHit C2S variant) ===
        registerPacket(C2SBodyPartHitPacket.class,
                C2SBodyPartHitPacket::encode,
                C2SBodyPartHitPacket::new,
                (msg, ctx) -> msg.handle(() -> ctx),
                NetworkDirection.PLAY_TO_SERVER
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

    private ModNetwork() {
        // Utility class - prevent instantiation
    }
}
