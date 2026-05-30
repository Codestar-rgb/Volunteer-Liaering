package com.subspaceparasite.common.network;

import java.util.function.Supplier;

import net.minecraftforge.network.NetworkEvent;

/**
 * Base packet interface for all SubspaceParasite network packets.
 */
public interface SPacket {

    /**
     * Handle this packet on the receiving side.
     *
     * @param context The network event context providing side information
     *                and allowing enqueue of thread-safe work
     */
    void handle(Supplier<NetworkEvent.Context> context);
}
