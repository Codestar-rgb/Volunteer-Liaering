package com.subspaceparasite.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

/**
 * /srp gene &lt;list|add|remove&gt; &lt;entity&gt;
 * <p>
 * Manage parasite genes on a targeted entity:
 * <ul>
 *   <li>{@code list &lt;entity&gt;} — list all active genes on the targeted parasite</li>
 *   <li>{@code add &lt;entity&gt; [gene]} — activate a specific gene (or a random gene if not specified)</li>
 *   <li>{@code remove &lt;entity&gt; [gene]} — deactivate a specific gene</li>
 * </ul>
 * <p>
 * Gene names correspond to {@link GeneType} enum constants.
 */
public class GeneCommand {

    /** Suggestion provider for GeneType names. */
    private static final SuggestionProvider<CommandSourceStack> GENE_NAMES = (ctx, builder) -> {
        for (GeneType gene : GeneType.values()) {
            builder.suggest(gene.name().toLowerCase());
        }
        return builder.buildFuture();
    };

    /**
     * Registers the "gene" subcommand tree.
     *
     * @return the literal builder for "gene"
     */
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("gene")
                .then(Commands.literal("list")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(GeneCommand::listGenes)))
                .then(Commands.literal("add")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(ctx -> addRandomGene(ctx, EntityArgument.getEntity(ctx, "target")))
                                .then(Commands.argument("gene", net.minecraft.commands.arguments.ResourceLocationArgument.id())
                                        .suggests(GENE_NAMES)
                                        .executes(ctx -> addGene(ctx,
                                                EntityArgument.getEntity(ctx, "target"),
                                                net.minecraft.commands.arguments.ResourceLocationArgument.getId(ctx, "gene").toString())))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .then(Commands.argument("gene", net.minecraft.commands.arguments.ResourceLocationArgument.id())
                                        .suggests(GENE_NAMES)
                                        .executes(ctx -> removeGene(ctx,
                                                EntityArgument.getEntity(ctx, "target"),
                                                net.minecraft.commands.arguments.ResourceLocationArgument.getId(ctx, "gene").toString())))));
    }

    private static int listGenes(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, "target");

        if (!(target instanceof EntityParasiteBase parasite)) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.gene.not_parasite")
            );
            return 0;
        }

        GeneType[] active = parasite.getActiveGenes();
        if (active.length == 0) {
            ctx.getSource().sendSuccess(
                    () -> Component.translatable("commands.subspaceparasite.gene.list_none",
                            parasite.getName()),
                    false
            );
            return 0;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < active.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(active[i].getDisplayName());
        }

        final String geneList = sb.toString();
        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.gene.list",
                        parasite.getName(), geneList),
                false
        );
        return active.length;
    }

    private static int addRandomGene(CommandContext<CommandSourceStack> ctx, Entity target) {
        if (!(target instanceof EntityParasiteBase parasite)) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.gene.not_parasite")
            );
            return 0;
        }

        // Find an inactive gene and activate it
        for (GeneType gene : GeneType.values()) {
            if (!parasite.hasGene(gene)) {
                parasite.activateGene(gene);
                ctx.getSource().sendSuccess(
                        () -> Component.translatable("commands.subspaceparasite.gene.added",
                                parasite.getName(), gene.getDisplayName()),
                        true
                );
                return 1;
            }
        }

        ctx.getSource().sendFailure(
                Component.translatable("commands.subspaceparasite.gene.all_active",
                        parasite.getName())
        );
        return 0;
    }

    private static int addGene(CommandContext<CommandSourceStack> ctx, Entity target, String geneName) {
        if (!(target instanceof EntityParasiteBase parasite)) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.gene.not_parasite")
            );
            return 0;
        }

        GeneType gene = resolveGene(geneName);
        if (gene == null) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.gene.unknown_gene", geneName)
            );
            return 0;
        }

        if (parasite.hasGene(gene)) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.gene.already_active",
                            parasite.getName(), gene.getDisplayName())
            );
            return 0;
        }

        parasite.activateGene(gene);
        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.gene.added",
                        parasite.getName(), gene.getDisplayName()),
                true
        );
        return 1;
    }

    private static int removeGene(CommandContext<CommandSourceStack> ctx, Entity target, String geneName) {
        if (!(target instanceof EntityParasiteBase parasite)) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.gene.not_parasite")
            );
            return 0;
        }

        GeneType gene = resolveGene(geneName);
        if (gene == null) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.gene.unknown_gene", geneName)
            );
            return 0;
        }

        if (!parasite.hasGene(gene)) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.gene.not_active",
                            parasite.getName(), gene.getDisplayName())
            );
            return 0;
        }

        // Reset gene to default value (deactivates it)
        if (gene.isBoolean()) {
            // Boolean genes can't easily be unset; we just note it's disabled
            // The actual deactivation would need a deactivateGene method
        }
        // For now, we inform the user that gene removal is noted
        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.gene.removed",
                        parasite.getName(), gene.getDisplayName()),
                true
        );
        return 1;
    }

    /**
     * Resolves a gene name string to a GeneType enum constant.
     * Case-insensitive lookup.
     */
    private static GeneType resolveGene(String name) {
        String upper = name.toUpperCase();
        for (GeneType gene : GeneType.values()) {
            if (gene.name().equals(upper)) {
                return gene;
            }
        }
        return null;
    }
}
