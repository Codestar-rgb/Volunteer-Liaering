package com.subspaceparasite.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * Root command class for the /srp command tree.
 * <p>
 * Provides the top-level "/srp" literal and delegates to subcommand
 * classes for each feature area. All subcommands require OP level 2.
 */
public class SRPCommand {

    /**
     * Registers the full /srp command tree on the given dispatcher.
     *
     * @param dispatcher the command dispatcher
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(build());
    }

    /**
     * Builds the /srp command tree as a literal builder.
     * This can also be used for redirect targets.
     *
     * @return the root literal builder for /srp
     */
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("srp")
                .requires(src -> src.hasPermission(2))
                .then(PhaseCommand.register())
                .then(EvolveCommand.register())
                .then(ColonyCommand.register())
                .then(DifficultyCommand.register())
                .then(InfectionCommand.register())
                .then(CelestialCommand.register())
                .then(ParasiteCommand.registerSpawn())
                .then(ParasiteCommand.registerKillall())
                .then(ConfigCommand.register())
                .then(MergeCommand.register())
                .then(GeneCommand.register())
                .then(DebugCommand.register());
    }
}
