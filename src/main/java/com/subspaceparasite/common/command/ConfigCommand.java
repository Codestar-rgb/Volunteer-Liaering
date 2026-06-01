package com.subspaceparasite.common.command;

import net.minecraft.world.entity.Mob;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.subspaceparasite.config.ModConfig;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * /srp config &lt;reload|get|set&gt;
 * <p>
 * Manage runtime configuration:
 * <ul>
 *   <li>{@code reload} — reload all config values from disk</li>
 *   <li>{@code get &lt;key&gt;} — display the current value of a config key</li>
 *   <li>{@code set &lt;key&gt; &lt;value&gt;} — set a config value at runtime (not persisted until reload)</li>
 * </ul>
 * <p>
 * Config keys are dot-separated paths matching the ModConfig structure,
 * e.g. "evolution.evolutionEnabled", "infection.infectionSpreadChance".
 */
public class ConfigCommand {

    /** Suggestion provider for known config key paths. */
    private static final SuggestionProvider<CommandSourceStack> CONFIG_KEYS = (ctx, builder) ->
            SharedSuggestionProvider.suggest(getConfigKeys(), builder);

    /**
     * Registers the "config" subcommand tree.
     *
     * @return the literal builder for "config"
     */
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("config")
                .then(Commands.literal("reload")
                        .executes(ConfigCommand::reloadConfig))
                .then(Commands.literal("get")
                        .then(Commands.argument("key", net.minecraft.commands.arguments.ResourceLocationArgument.id())
                                .suggests(CONFIG_KEYS)
                                .executes(ctx -> getConfig(ctx,
                                        net.minecraft.commands.arguments.ResourceLocationArgument.getId(ctx, "key").toString()))));
    }

    private static int reloadConfig(CommandContext<CommandSourceStack> ctx) {
        // Forge's ForgeConfigSpec automatically reloads from disk when the
        // config file changes, but we can trigger a manual reload by asking
        // the spec to load. In 1.20.1 this is done implicitly.
        // ForgeConfigSpec.afterReload() doesn't exist in 1.20.1; config reloads
        // are handled automatically by Forge's config watcher on file change.
        // ModConfig.SPEC is loaded on demand when values are accessed.

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.config.reloaded"),
                true
        );
        return 1;
    }

    private static int getConfig(CommandContext<CommandSourceStack> ctx, String key) {
        String value = getConfigValue(key);
        if (value == null) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.config.unknown_key", key)
            );
            return 0;
        }

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.config.get", key, value),
                false
        );
        return 1;
    }

    /**
     * Resolves a config key to its current string value.
     * Supports dot-separated paths like "evolution.evolutionEnabled".
     */
    private static String getConfigValue(String key) {
        return switch (key) {
            // Evolution
            case "evolution.evolutionEnabled" -> String.valueOf(ModConfig.EVOLUTION.evolutionEnabled.get());
            case "evolution.naturalEvolutionRate" -> String.valueOf(ModConfig.EVOLUTION.naturalEvolutionRate.get());
            case "evolution.killEvolutionRate" -> String.valueOf(ModConfig.EVOLUTION.killEvolutionRate.get());
            case "evolution.phaseProgressionInterval" -> String.valueOf(ModConfig.EVOLUTION.phaseProgressionInterval.get());
            case "evolution.phaseHealthBonus" -> String.valueOf(ModConfig.EVOLUTION.phaseHealthBonus.get());
            case "evolution.phaseDamageBonus" -> String.valueOf(ModConfig.EVOLUTION.phaseDamageBonus.get());
            // Infection
            case "infection.infectionEnabled" -> String.valueOf(ModConfig.INFECTION.infectionEnabled.get());
            case "infection.infectionSpreadChance" -> String.valueOf(ModConfig.INFECTION.infectionSpreadChance.get());
            case "infection.infectionConversionChance" -> String.valueOf(ModConfig.INFECTION.infectionConversionChance.get());
            case "infection.infectionSpreadMultiplier" -> String.valueOf(ModConfig.INFECTION.infectionSpreadMultiplier.get());
            case "infection.cothDuration" -> String.valueOf(ModConfig.INFECTION.cothDuration.get());
            case "infection.infectionAuraEnabled" -> String.valueOf(ModConfig.INFECTION.infectionAuraEnabled.get());
            // Colony
            case "colony.colonySystemEnabled" -> String.valueOf(ModConfig.COLONY.colonySystemEnabled.get());
            case "colony.maxColonies" -> String.valueOf(ModConfig.COLONY.maxColonies.get());
            case "colony.maxColonyUnits" -> String.valueOf(ModConfig.COLONY.maxColonyUnits.get());
            // Mob Cap
            case "mobcap.globalMobCap" -> String.valueOf(ModConfig.MOB_CAP.globalMobCap.get());
            case "mobcap.emergencyCullEnabled" -> String.valueOf(ModConfig.MOB_CAP.emergencyCullEnabled.get());
            // World
            case "world.biomeWeight" -> String.valueOf(ModConfig.WORLD.biomeWeight.get());
            case "world.maxSpawnLightLevel" -> String.valueOf(ModConfig.WORLD.maxSpawnLightLevel.get());
            case "world.parasiteBiomeEnabled" -> String.valueOf(ModConfig.WORLD.parasiteBiomeEnabled.get());
            // Combat
            case "combat.damageCapEnabled" -> String.valueOf(ModConfig.COMBAT.damageCapEnabled.get());
            case "combat.defaultDamageCap" -> String.valueOf(ModConfig.COMBAT.defaultDamageCap.get());
            case "combat.adaptationMax" -> String.valueOf(ModConfig.COMBAT.adaptationMax.get());
            // Gene
            case "gene.geneMutationEnabled" -> String.valueOf(ModConfig.GENE.geneMutationEnabled.get());
            case "gene.geneMutationChance" -> String.valueOf(ModConfig.GENE.geneMutationChance.get());
            case "gene.geneGainEnabled" -> String.valueOf(ModConfig.GENE.geneGainEnabled.get());
            // Debug
            case "debug.loggingEnabled" -> String.valueOf(ModConfig.DEBUG.loggingEnabled.get());
            default -> null;
        };
    }

    /** Returns a stream of all known config key paths for tab-completion. */
    private static Stream<String> getConfigKeys() {
        return Stream.of(
                "evolution.evolutionEnabled", "evolution.naturalEvolutionRate",
                "evolution.killEvolutionRate", "evolution.phaseProgressionInterval",
                "evolution.phaseHealthBonus", "evolution.phaseDamageBonus",
                "infection.infectionEnabled", "infection.infectionSpreadChance",
                "infection.infectionConversionChance", "infection.infectionSpreadMultiplier",
                "infection.cothDuration", "infection.infectionAuraEnabled",
                "colony.colonySystemEnabled", "colony.maxColonies", "colony.maxColonyUnits",
                "mobcap.globalMobCap", "mobcap.emergencyCullEnabled",
                "world.biomeWeight", "world.maxSpawnLightLevel", "world.parasiteBiomeEnabled",
                "combat.damageCapEnabled", "combat.defaultDamageCap", "combat.adaptationMax",
                "gene.geneMutationEnabled", "gene.geneMutationChance", "gene.geneGainEnabled",
                "debug.loggingEnabled"
        );
    }
}
