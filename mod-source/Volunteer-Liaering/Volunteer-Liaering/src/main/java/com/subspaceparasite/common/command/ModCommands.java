package com.subspaceparasite.common.command;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Event handler that registers the /srp command tree on the Forge event bus.
 * <p>
 * Listens for {@link RegisterCommandsEvent} and delegates to
 * {@link SRPCommand#register(CommandDispatcher)}.
 * <p>
 * Must be registered on the FORGE event bus (not the mod event bus).
 */
@Mod.EventBusSubscriber(modid = "subspaceparasite", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SRPCommand.register(event.getDispatcher());
    }
}
