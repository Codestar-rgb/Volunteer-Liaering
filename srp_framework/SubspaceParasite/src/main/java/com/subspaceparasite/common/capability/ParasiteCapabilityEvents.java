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
 * Event handler for the ParasiteCapability system.
 * <p>
 * Registered on the FORGE event bus to handle:
 * <ul>
 *   <li>{@link AttachCapabilitiesEvent} — attaches ParasiteCapability to all LivingEntity instances</li>
 *   <li>{@link PlayerEvent.Clone} — persists capability data across death/respawn</li>
 * </ul>
 * Also registers the capability class via {@link RegisterCapabilitiesEvent} on the mod event bus.
 */
@Mod.EventBusSubscriber(modid = SubspaceParasite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ParasiteCapabilityEvents {

    /** Unique resource location for the parasite capability. */
    private static final ResourceLocation CAPABILITY_ID =
            new ResourceLocation(SubspaceParasite.MOD_ID, "parasite_infection");

    /**
     * Registers the ParasiteCapability class with Forge.
     * Called on the MOD event bus during capability registration.
     *
     * @param event the register capabilities event
     */
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ParasiteCapability.class);
    }

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
}
