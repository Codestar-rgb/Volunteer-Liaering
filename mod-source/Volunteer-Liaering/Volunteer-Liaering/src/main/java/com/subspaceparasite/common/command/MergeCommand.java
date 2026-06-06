package com.subspaceparasite.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.subspaceparasite.api.IEvolvable;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.world.ModWorldData;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * /srp merge
 * <p>
 * Force-merges nearby compatible parasites around the executor.
 * Merging is the process by which two parasites of the same or compatible
 * types combine their evolution points, potentially triggering an evolution
 * to a higher tier.
 * <p>
 * This command finds all parasites within 16 blocks of the executor and
 * consolidates their evolution points into the nearest parasite, then
 * removes the others.
 */
public class MergeCommand {

    private static final double MERGE_RANGE = 16.0;

    /**
     * Registers the "merge" subcommand.
     *
     * @return the literal builder for "merge"
     */
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("merge")
                .executes(MergeCommand::merge);
    }

    private static int merge(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        var source = ctx.getSource().getEntity();

        if (source == null) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.merge.no_source")
            );
            return 0;
        }

        List<EntityParasiteBase> parasites = level.getEntitiesOfClass(
                EntityParasiteBase.class,
                source.getBoundingBox().inflate(MERGE_RANGE)
        );

        if (parasites.size() < 2) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.merge.not_enough")
            );
            return 0;
        }

        // Consolidate evolution points into the first parasite
        EntityParasiteBase target = parasites.get(0);
        int totalPoints = target.getEvolutionPoints();
        int merged = 0;

        for (int i = 1; i < parasites.size(); i++) {
            EntityParasiteBase other = parasites.get(i);
            // Only merge parasites of the same or compatible evolution path
            if (areCompatible(target, other)) {
                totalPoints += other.getEvolutionPoints();
                other.discard();
                merged++;
            }
        }

        target.addEvolutionPoints(totalPoints - target.getEvolutionPoints());

        final int mergedCount = merged;
        final int finalPoints = totalPoints;
        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.merge.success",
                        mergedCount, target.getName(), finalPoints),
                true
        );
        return merged;
    }

    /**
     * Checks if two parasites are compatible for merging.
     * Parasites of the same evolution path can merge.
     */
    private static boolean areCompatible(EntityParasiteBase a, EntityParasiteBase b) {
        ParasiteType typeA = a.getParasiteType();
        ParasiteType typeB = b.getParasiteType();
        if (typeA == null || typeB == null) return true; // Allow merge if type is unknown
        return typeA.getEvolutionTier() == typeB.getEvolutionTier();
    }
}
