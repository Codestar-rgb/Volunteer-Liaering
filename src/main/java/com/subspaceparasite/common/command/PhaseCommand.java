package com.subspaceparasite.common.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.world.ModSaveData;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

/**
 * /srp phase &lt;get|set&gt; [value]
 * <p>
 * View or set the current evolution phase for the caller's dimension.
 * Phase values range from 0–4 matching the original SRP:
 * <ul>
 *   <li>0 — Pre-assimilation</li>
 *   <li>1 — Primitive</li>
 *   <li>2 — Adapted</li>
 *   <li>3 — Evolved</li>
 *   <li>4 — Apex</li>
 * </ul>
 * Setting a phase also resets the progression timer.
 */
public class PhaseCommand {

    /**
     * Registers the "phase" subcommand tree.
     *
     * @return the literal builder for "phase"
     */
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("phase")
                .then(Commands.literal("get")
                        .executes(PhaseCommand::getPhase))
                .then(Commands.literal("set")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0, EvoPhase.getMaxPhaseNumber()))
                                .executes(ctx -> setPhase(ctx, IntegerArgumentType.getInteger(ctx, "value")))));
    }

    private static int getPhase(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        ModSaveData saveData = ModSaveData.get(level);
        EvoPhase phase = saveData.getCurrentPhase(level);

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.phase.get",
                        phase.getStageName(), phase.getPhaseNumber()),
                false
        );
        return phase.getPhaseNumber();
    }

    private static int setPhase(CommandContext<CommandSourceStack> ctx, int value) {
        ServerLevel level = ctx.getSource().getLevel();
        EvoPhase phase = EvoPhase.getByPhaseNumber(value);

        if (phase == EvoPhase.NONE) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.phase.invalid", value)
            );
            return 0;
        }

        ModSaveData saveData = ModSaveData.get(level);
        EvoPhase old = saveData.getCurrentPhase(level);
        saveData.setCurrentPhase(level, phase);

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.phase.set",
                        old.getStageName(), old.getPhaseNumber(), phase.getStageName(), phase.getPhaseNumber()),
                true
        );
        return phase.getPhaseNumber();
    }
}
