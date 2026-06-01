package com.subspaceparasite.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.subspaceparasite.common.world.ModWorldData;
import com.subspaceparasite.common.world.SRPDifficultySetting;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

/**
 * /srp difficulty &lt;get|set&gt; [level]
 * <p>
 * View or set the SRP difficulty setting for the current world.
 * Valid levels: EASY, NORMAL, HARD, IMPOSSIBLE.
 * <p>
 * These are the 4 SRP difficulty levels, INDEPENDENT from the Evolution Phase system:
 * <ul>
 *   <li>EASY — Halves evolution point gain. Reduces parasite knockback immunity</li>
 *   <li>NORMAL — Baseline, no modifications</li>
 *   <li>HARD — Parasites' attack power, defense, and stamina increase 4×</li>
 *   <li>IMPOSSIBLE — Parasites' health and attack power increase 11×, defense increases 6×,
 *       strong knockback immunity</li>
 * </ul>
 */
public class DifficultyCommand {

    /**
     * Registers the "difficulty" subcommand tree.
     *
     * @return the literal builder for "difficulty"
     */
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("difficulty")
                .then(Commands.literal("get")
                        .executes(DifficultyCommand::getDifficulty))
                .then(Commands.literal("set")
                        .then(Commands.literal("easy")
                                .executes(ctx -> setDifficulty(ctx, SRPDifficultySetting.EASY)))
                        .then(Commands.literal("normal")
                                .executes(ctx -> setDifficulty(ctx, SRPDifficultySetting.NORMAL)))
                        .then(Commands.literal("hard")
                                .executes(ctx -> setDifficulty(ctx, SRPDifficultySetting.HARD)))
                        .then(Commands.literal("impossible")
                                .executes(ctx -> setDifficulty(ctx, SRPDifficultySetting.IMPOSSIBLE))));
    }

    private static int getDifficulty(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        ModWorldData worldData = ModWorldData.get(level);
        SRPDifficultySetting difficulty = worldData.getSRPDifficulty();

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.difficulty.get",
                        difficulty.name(),
                        String.format("%.1f", difficulty.getEvolutionRateMultiplier()),
                        String.format("%.1f", difficulty.getInfectionRateMultiplier()),
                        String.format("%.1f", difficulty.getHealthMultiplier()),
                        String.format("%.1f", difficulty.getAttackMultiplier()),
                        String.format("%.1f", difficulty.getDefenseMultiplier()),
                        difficulty.canParasitesBreakBlocks()),
                false
        );
        return difficulty.getId();
    }

    private static int setDifficulty(CommandContext<CommandSourceStack> ctx, SRPDifficultySetting difficulty) {
        ServerLevel level = ctx.getSource().getLevel();
        ModWorldData worldData = ModWorldData.get(level);
        worldData.setSRPDifficulty(difficulty);

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.difficulty.set",
                        difficulty.name()),
                true
        );
        return difficulty.getId();
    }
}
