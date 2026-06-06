package com.subspaceparasite.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.subspaceparasite.common.world.CelestialManager;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

/**
 * /srp celestial &lt;start|stop|status&gt;
 * <p>
 * Control celestial night events in the current dimension:
 * <ul>
 *   <li>{@code start} — immediately begin a celestial night event</li>
 *   <li>{@code stop} — immediately end the current celestial night event</li>
 *   <li>{@code status} — display whether a celestial night is active, remaining duration, and cooldown</li>
 * </ul>
 */
public class CelestialCommand {

    /**
     * Registers the "celestial" subcommand tree.
     *
     * @return the literal builder for "celestial"
     */
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("celestial")
                .then(Commands.literal("start")
                        .executes(CelestialCommand::startCelestial))
                .then(Commands.literal("stop")
                        .executes(CelestialCommand::stopCelestial))
                .then(Commands.literal("status")
                        .executes(CelestialCommand::statusCelestial));
    }

    private static int startCelestial(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        CelestialManager manager = CelestialManager.get(level);

        if (manager.isCelestialNightActive()) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.celestial.already_active")
            );
            return 0;
        }

        manager.startCelestialNight(level);

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.celestial.start"),
                true
        );
        return 1;
    }

    private static int stopCelestial(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        CelestialManager manager = CelestialManager.get(level);

        if (!manager.isCelestialNightActive()) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.celestial.not_active")
            );
            return 0;
        }

        manager.endCelestialNight(level);

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.celestial.stop"),
                true
        );
        return 1;
    }

    private static int statusCelestial(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        CelestialManager manager = CelestialManager.get(level);

        boolean active = manager.isCelestialNightActive();
        int duration = manager.getCelestialNightDuration();
        int cooldown = manager.getCelestialNightCooldown();
        float spawnMult = manager.getSpawnRateMultiplier();
        float evoMult = manager.getEvolutionMultiplier();

        if (active) {
            ctx.getSource().sendSuccess(
                    () -> Component.translatable("commands.subspaceparasite.celestial.status_active",
                            duration, String.format("%.1f", spawnMult), String.format("%.1f", evoMult)),
                    false
            );
        } else {
            ctx.getSource().sendSuccess(
                    () -> Component.translatable("commands.subspaceparasite.celestial.status_inactive",
                            cooldown),
                    false
            );
        }

        return active ? 1 : 0;
    }
}
