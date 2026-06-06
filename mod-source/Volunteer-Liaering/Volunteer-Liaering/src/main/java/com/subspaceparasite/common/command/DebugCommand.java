package com.subspaceparasite.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.world.CelestialManager;
import com.subspaceparasite.common.world.DifficultySystem;
import com.subspaceparasite.common.world.ModSaveData;
import com.subspaceparasite.common.world.ModWorldData;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * /srp debug &lt;info|stats|entities&gt;
 * <p>
 * Debug information commands for server operators:
 * <ul>
 *   <li>{@code info} — display mod version, current phase, difficulty, celestial status</li>
 *   <li>{@code stats} — display kill statistics and evolution point totals</li>
 *   <li>{@code entities} — display parasite entity counts by type in the current dimension</li>
 * </ul>
 */
public class DebugCommand {

    /**
     * Registers the "debug" subcommand tree.
     *
     * @return the literal builder for "debug"
     */
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("debug")
                .then(Commands.literal("info")
                        .executes(DebugCommand::info))
                .then(Commands.literal("stats")
                        .executes(DebugCommand::stats))
                .then(Commands.literal("entities")
                        .executes(DebugCommand::entities));
    }

    private static int info(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        ModSaveData saveData = ModSaveData.get(level);
        ModWorldData worldData = ModWorldData.get(level);
        DifficultySystem diffSystem = DifficultySystem.get(level);
        CelestialManager celestial = CelestialManager.get(level);

        EvoPhase phase = saveData.getCurrentPhase(level);
        float difficulty = diffSystem.getCurrentDifficulty();
        boolean celestialActive = celestial.isCelestialNightActive();
        int celestialDuration = celestial.getCelestialNightDuration();
        int colonyCount = worldData.getColonyCount();
        long ticks = saveData.getTotalTicks();

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.debug.info_header",
                        com.subspaceparasite.SubspaceParasite.MOD_NAME,
                        com.subspaceparasite.SubspaceParasite.VERSION),
                false
        );

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.debug.info_phase",
                        phase.name(), phase.getPhaseNumber()),
                false
        );

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.debug.info_difficulty",
                        String.format("%.2f", difficulty)),
                false
        );

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.debug.info_celestial",
                        celestialActive ? "active" : "inactive",
                        celestialDuration),
                false
        );

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.debug.info_colonies",
                        colonyCount),
                false
        );

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.debug.info_ticks",
                        ticks),
                false
        );

        return 1;
    }

    private static int stats(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        ModWorldData worldData = ModWorldData.get(level);
        ModSaveData saveData = ModSaveData.get(level);

        // Kill stats
        java.util.Set<String> killKeys = worldData.getKillStatKeys();
        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.debug.stats_header"),
                false
        );

        for (String key : killKeys) {
            double value = worldData.getKillStat(key);
            ctx.getSource().sendSuccess(
                    () -> Component.translatable("commands.subspaceparasite.debug.stats_entry",
                            key, String.format("%.1f", value)),
                    false
            );
        }

        // Evolution points per dimension
        Map<net.minecraft.resources.ResourceLocation, Integer> evoPoints = saveData.getAllEvolutionPoints();
        for (Map.Entry<net.minecraft.resources.ResourceLocation, Integer> entry : evoPoints.entrySet()) {
            ctx.getSource().sendSuccess(
                    () -> Component.translatable("commands.subspaceparasite.debug.stats_evo",
                            entry.getKey().toString(), entry.getValue()),
                    false
            );
        }

        return killKeys.size();
    }

    private static int entities(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();

        // Count all parasite entities by ParasiteType
        Map<String, Integer> typeCounts = new LinkedHashMap<>();
        int[] totalParasites = {0};

        net.minecraft.world.level.border.WorldBorder border = level.getWorldBorder();
        AABB searchBounds = new AABB(
                border.getMinX(), level.getMinBuildHeight(), border.getMinZ(),
                border.getMaxX(), level.getMaxBuildHeight(), border.getMaxZ()
        );

        for (EntityParasiteBase parasite : level.getEntitiesOfClass(
                EntityParasiteBase.class,
                searchBounds)) {
            ParasiteType type = parasite.getParasiteType();
            String key = type != null ? type.getDisplayName() : "Unknown";
            typeCounts.merge(key, 1, Integer::sum);
            totalParasites[0]++;
        }

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.debug.entities_header",
                        totalParasites[0]),
                false
        );

        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            final String entryKey = entry.getKey();
            final int entryValue = entry.getValue();
            ctx.getSource().sendSuccess(
                    () -> Component.translatable("commands.subspaceparasite.debug.entities_entry",
                            entryKey, entryValue),
                    false
            );
        }

        return totalParasites[0];
    }
}
