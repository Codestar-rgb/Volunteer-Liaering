package com.subspaceparasite.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.List;

/**
 * /srp evolve &lt;player|entity&gt;
 * <p>
 * Force-evolves a nearby parasite. If "player" is specified, all parasites
 * within 16 blocks of the player are force-evolved. If "entity" is specified,
 * the targeted entity (if it is a parasite) is evolved.
 * <p>
 * Force evolution grants a burst of evolution points, potentially triggering
 * a tier advancement.
 */
public class EvolveCommand {

    private static final int EVOLVE_POINT_BURST = 50;

    /**
     * Registers the "evolve" subcommand tree.
     *
     * @return the literal builder for "evolve"
     */
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("evolve")
                .then(Commands.literal("player")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(EvolveCommand::evolveNearPlayer)))
                .then(Commands.literal("entity")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(EvolveCommand::evolveEntity)));
    }

    private static int evolveNearPlayer(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = ctx.getSource().getLevel();
        var player = EntityArgument.getPlayer(ctx, "target");

        List<EntityParasiteBase> parasites = level.getEntitiesOfClass(
                EntityParasiteBase.class,
                player.getBoundingBox().inflate(16.0)
        );

        if (parasites.isEmpty()) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.evolve.no_parasites")
            );
            return 0;
        }

        int evolved = 0;
        for (EntityParasiteBase parasite : parasites) {
            parasite.addEvolutionPoints(EVOLVE_POINT_BURST);
            evolved++;
        }

        final int count = evolved;
        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.evolve.success_player",
                        count, EVOLVE_POINT_BURST),
                true
        );
        return evolved;
    }

    private static int evolveEntity(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, "target");

        if (!(target instanceof EntityParasiteBase parasite)) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.evolve.not_parasite")
            );
            return 0;
        }

        parasite.addEvolutionPoints(EVOLVE_POINT_BURST);

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.evolve.success_entity",
                        parasite.getName(), EVOLVE_POINT_BURST),
                true
        );
        return 1;
    }
}
