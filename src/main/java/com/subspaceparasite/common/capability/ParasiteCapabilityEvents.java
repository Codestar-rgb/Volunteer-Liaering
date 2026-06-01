package com.subspaceparasite.common.capability;

import com.subspaceparasite.SubspaceParasite;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Event handlers for the ParasiteCapability system.
 * <p>
 * This class handles FORGE-bus events:
 * <ul>
 *   <li>{@link AttachCapabilitiesEvent} — attaches ParasiteCapability to all LivingEntity instances</li>
 *   <li>{@link PlayerEvent.Clone} — persists capability data across death/respawn</li>
 * </ul>
 * <p>
 * Capability registration is handled by the inner {@link ModBusEvents} class
 * which is registered on the MOD event bus where {@link RegisterCapabilitiesEvent} fires.
 */
@Mod.EventBusSubscriber(modid = SubspaceParasite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ParasiteCapabilityEvents {

    /** Unique resource location for the parasite capability. */
    private static final ResourceLocation CAPABILITY_ID =
            new ResourceLocation(SubspaceParasite.MOD_ID, "parasite_infection");

    /**
     * Attaches the ParasiteCapability provider to all LivingEntity instances.
     * This is the core mechanism that enables vanilla mobs and players
     * to carry infection state.
     *
     * @param event the attach capabilities event
     */
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<LivingEntity> event) {
        if (event.getObject() == null) return;

        // Only attach if not already present
        if (event.getObject().getCapability(ParasiteCapability.CAPABILITY).isPresent()) return;

        event.addCapability(CAPABILITY_ID, new ParasiteCapabilityProvider());
    }

    /**
     * Persists the ParasiteCapability data when a player respawns after death.
     * Without this handler, the capability data would be lost on death.
     *
     * @param event the player clone event
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player newPlayer = event.getEntity();

        // On 1.20.1, we need to revive caps on the original player to read them
        original.reviveCaps();

        original.getCapability(ParasiteCapability.CAPABILITY).ifPresent(oldCap -> {
            newPlayer.getCapability(ParasiteCapability.CAPABILITY).ifPresent(newCap -> {
                newCap.deserializeNBT(oldCap.serializeNBT());
            });
        });

        original.invalidateCaps();
    }

    /**
     * MOD event bus handler for capability registration.
     * <p>
     * {@link RegisterCapabilitiesEvent} fires on the MOD event bus, not the FORGE bus,
     * so this must be a separate inner class with {@code Bus.MOD}.
     * Without this registration, the capability token will not be populated and
     * {@link ParasiteCapability#CAPABILITY} will be {@code null}, breaking the entire
     * infection system.
     */
    @Mod.EventBusSubscriber(modid = SubspaceParasite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

        /**
         * Registers the ParasiteCapability class with Forge's capability system.
         * This must happen on the MOD event bus before any capabilities are accessed.
         *
         * @param event the register capabilities event
         */
        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(ParasiteCapability.class);
        }
    }
}
