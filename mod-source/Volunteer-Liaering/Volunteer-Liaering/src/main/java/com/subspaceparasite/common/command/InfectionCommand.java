package com.subspaceparasite.common.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.subspaceparasite.common.capability.ParasiteCapability;
import com.subspaceparasite.core.ModEffects;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * /srp infection &lt;get|set|cure&gt; [player]
 * <p>
 * Manage COTH infection levels on players:
 * <ul>
 *   <li>{@code get [player]} — display the target player's COTH infection level</li>
 *   <li>{@code set &lt;level&gt; [player]} — set the target player's infection level (0–5)</li>
 *   <li>{@code cure [player]} — fully cure the target player's COTH infection</li>
 * </ul>
 * If no player is specified, targets the command executor.
 */
public class InfectionCommand {

    /**
     * Registers the "infection" subcommand tree.
     *
     * @return the literal builder for "infection"
     */
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("infection")
                .then(Commands.literal("get")
                        .executes(ctx -> getInfection(ctx, ctx.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> getInfection(ctx, EntityArgument.getPlayer(ctx, "player")))))
                .then(Commands.literal("set")
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 5))
                                .executes(ctx -> setInfection(ctx,
                                        ctx.getSource().getPlayerOrException(),
                                        IntegerArgumentType.getInteger(ctx, "level")))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> setInfection(ctx,
                                                EntityArgument.getPlayer(ctx, "player"),
                                                IntegerArgumentType.getInteger(ctx, "level"))))))
                .then(Commands.literal("cure")
                        .executes(ctx -> cureInfection(ctx, ctx.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> cureInfection(ctx, EntityArgument.getPlayer(ctx, "player")))));
    }

    private static int getInfection(CommandContext<CommandSourceStack> ctx, Player player) {
        Optional<ParasiteCapability> cap = player.getCapability(ParasiteCapability.CAPABILITY).resolve();
        int level = cap.map(ParasiteCapability::getInfectionLevel).orElse(0);

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.infection.get",
                        player.getName(), level),
                false
        );
        return level;
    }

    private static int setInfection(CommandContext<CommandSourceStack> ctx, Player player, int level) {
        player.getCapability(ParasiteCapability.CAPABILITY).ifPresent(cap -> {
            cap.setInfectionLevel(level);
        });

        // Also apply or remove the COTH effect visually
        if (level > 0) {
            player.addEffect(new MobEffectInstance(
                    ModEffects.COTH.get(), Integer.MAX_VALUE, level - 1, false, true, true));
        } else {
            player.removeEffect(ModEffects.COTH.get());
        }

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.infection.set",
                        player.getName(), level),
                true
        );
        return level;
    }

    private static int cureInfection(CommandContext<CommandSourceStack> ctx, Player player) {
        player.getCapability(ParasiteCapability.CAPABILITY).ifPresent(cap -> {
            cap.setInfectionLevel(0);
        });

        // Remove COTH effect
        player.removeEffect(ModEffects.COTH.get());

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.infection.cure",
                        player.getName()),
                true
        );
        return 1;
    }
}
