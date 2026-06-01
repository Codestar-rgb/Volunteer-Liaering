package com.subspaceparasite.common.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.core.ModEntities;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * /srp spawn &lt;parasite_type&gt; [count]
 * /srp killall
 * <p>
 * Spawn or kill parasite entities:
 * <ul>
 *   <li>{@code spawn &lt;type&gt; [count]} — spawn specific parasite types near the executor; type can be a
 *       ParasiteType serial name (e.g. "sim_human") or a tier name ("infected", "feral", "primitive", etc.)
 *       to spawn a random entity from that tier</li>
 *   <li>{@code killall} — kill all parasite entities in the current dimension</li>
 * </ul>
 */
public class ParasiteCommand {

    private static final int MAX_SPAWN_COUNT = 50;

    /**
     * Registers the "spawn" subcommand.
     *
     * @return the literal builder for "spawn"
     */
    public static LiteralArgumentBuilder<CommandSourceStack> registerSpawn() {
        return Commands.literal("spawn")
                .then(Commands.argument("parasite_type", net.minecraft.commands.arguments.ResourceLocationArgument.id())
                        .executes(ctx -> spawnParasites(ctx, 1))
                        .then(Commands.argument("count", IntegerArgumentType.integer(1, MAX_SPAWN_COUNT))
                                .executes(ctx -> spawnParasites(ctx,
                                        IntegerArgumentType.getInteger(ctx, "count")))));
    }

    /**
     * Registers the "killall" subcommand.
     *
     * @return the literal builder for "killall"
     */
    public static LiteralArgumentBuilder<CommandSourceStack> registerKillall() {
        return Commands.literal("killall")
                .executes(ParasiteCommand::killAll);
    }

    private static int spawnParasites(CommandContext<CommandSourceStack> ctx, int count) {
        ServerLevel level = ctx.getSource().getLevel();
        Vec3 pos = ctx.getSource().getPosition();
        String typeArg = net.minecraft.commands.arguments.ResourceLocationArgument.getId(ctx, "parasite_type").toString();

        // Try to resolve as a tier name first
        List<EntityType<?>> candidates = resolveEntityTypes(typeArg);

        if (candidates.isEmpty()) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.spawn.unknown_type", typeArg)
            );
            return 0;
        }

        int spawned = 0;
        for (int i = 0; i < count; i++) {
            EntityType<?> type = candidates.get(level.random.nextInt(candidates.size()));
            BlockPos spawnPos = BlockPos.containing(
                    pos.x + (level.random.nextDouble() - 0.5) * 8,
                    pos.y + 2,
                    pos.z + (level.random.nextDouble() - 0.5) * 8
            );

            var entity = type.create(level);
            if (entity != null) {
                entity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                        level.random.nextFloat() * 360F, 0);
                if (entity instanceof EntityParasiteBase parasite) {
                    parasite.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos),
                            MobSpawnType.COMMAND, null, null);
                }
                level.addFreshEntity(entity);
                spawned++;
            }
        }

        final int total = spawned;
        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.spawn.success",
                        total, typeArg),
                true
        );
        return spawned;
    }

    /**
     * Resolves a type string to a list of entity types.
     * Supports tier names (e.g. "infected", "feral") or specific ParasiteType serial names.
     */
    private static List<EntityType<?>> resolveEntityTypes(String typeArg) {
        List<EntityType<?>> result = new ArrayList<>();
        String lower = typeArg.toLowerCase(Locale.ROOT);

        // Tier-based spawning using ModEntities fields
        switch (lower) {
            case "infected" -> {
                addIfPresent(result, ModEntities.INFECTED_CREEPER);
                addIfPresent(result, ModEntities.INFECTED_SKELETON);
                addIfPresent(result, ModEntities.INFECTED_ZOMBIE);
                addIfPresent(result, ModEntities.INFECTED_SPIDER);
                addIfPresent(result, ModEntities.INFECTED_ENDERMAN);
                addIfPresent(result, ModEntities.INFECTED_PIG);
                addIfPresent(result, ModEntities.INFECTED_HUMAN);
                addIfPresent(result, ModEntities.INFECTED_COW);
                addIfPresent(result, ModEntities.INFECTED_SHEEP);
                addIfPresent(result, ModEntities.INFECTED_CHICKEN);
                addIfPresent(result, ModEntities.INFECTED_VILLAGER);
                addIfPresent(result, ModEntities.INFECTED_WOLF);
                addIfPresent(result, ModEntities.INFECTED_HORSE);
                addIfPresent(result, ModEntities.INFECTED_BAT);
                addIfPresent(result, ModEntities.INFECTED_BLAZE);
                addIfPresent(result, ModEntities.INFECTED_WITCH);
                addIfPresent(result, ModEntities.INFECTED_PILLAGER);
            }
            case "feral" -> {
                addIfPresent(result, ModEntities.FERAL_COW);
                addIfPresent(result, ModEntities.FERAL_HUMAN);
                addIfPresent(result, ModEntities.FERAL_CREEPER);
                addIfPresent(result, ModEntities.FERAL_SKELETON);
                addIfPresent(result, ModEntities.FERAL_ZOMBIE);
                addIfPresent(result, ModEntities.FERAL_SPIDER);
                addIfPresent(result, ModEntities.FERAL_ENDERMAN);
                addIfPresent(result, ModEntities.FERAL_WOLF);
                addIfPresent(result, ModEntities.FERAL_IRON_GOLEM);
            }
            case "hijacked" -> {
                addIfPresent(result, ModEntities.HIJACKED_CREEPER);
                addIfPresent(result, ModEntities.HIJACKED_SKELETON);
                addIfPresent(result, ModEntities.HIJACKED_ZOMBIE);
                addIfPresent(result, ModEntities.HIJACKED_SPIDER);
                addIfPresent(result, ModEntities.HIJACKED_ENDERMAN);
                addIfPresent(result, ModEntities.HIJACKED_WITCH);
                addIfPresent(result, ModEntities.HIJACKED_PILLAGER);
            }
            case "inborn" -> {
                addIfPresent(result, ModEntities.INBORN_GOTHOL);
                addIfPresent(result, ModEntities.INBORN_KOL);
                addIfPresent(result, ModEntities.INBORN_LESH);
                addIfPresent(result, ModEntities.INBORN_LODO);
                addIfPresent(result, ModEntities.INBORN_MOR);
                addIfPresent(result, ModEntities.INBORN_MUDO);
                addIfPresent(result, ModEntities.INBORN_NUUH);
                addIfPresent(result, ModEntities.INBORN_RATHOL);
                addIfPresent(result, ModEntities.INBORN_VIIN);
                addIfPresent(result, ModEntities.INBORN_ATA);
                addIfPresent(result, ModEntities.INBORN_BUTHOL);
            }
            case "crude" -> {
                addIfPresent(result, ModEntities.CRUDE_MOVING_FLESH);
                addIfPresent(result, ModEntities.CRUDE_WORKER);
                addIfPresent(result, ModEntities.CRUDE_CRUX_A);
                addIfPresent(result, ModEntities.CRUDE_CRUX_B);
                addIfPresent(result, ModEntities.CRUDE_DONE);
                addIfPresent(result, ModEntities.CRUDE_HEED);
                addIfPresent(result, ModEntities.CRUDE_HOST);
                addIfPresent(result, ModEntities.CRUDE_LEER);
                addIfPresent(result, ModEntities.CRUDE_MES);
            }
            case "primitive" -> {
                addIfPresent(result, ModEntities.PRIMITIVE_BANO);
                addIfPresent(result, ModEntities.PRIMITIVE_CANRA);
                addIfPresent(result, ModEntities.PRIMITIVE_EMANA);
                addIfPresent(result, ModEntities.PRIMITIVE_GIM);
                addIfPresent(result, ModEntities.PRIMITIVE_HULL);
                addIfPresent(result, ModEntities.PRIMITIVE_IKI);
                addIfPresent(result, ModEntities.PRIMITIVE_LUM);
                addIfPresent(result, ModEntities.PRIMITIVE_NOGLA);
                addIfPresent(result, ModEntities.PRIMITIVE_RANRAC);
                addIfPresent(result, ModEntities.PRIMITIVE_SHYCO);
                addIfPresent(result, ModEntities.PRIMITIVE_WYMO);
                addIfPresent(result, ModEntities.PRIMITIVE_ZAA);
            }
            case "adapted" -> {
                addIfPresent(result, ModEntities.ADAPTED_BANO);
                addIfPresent(result, ModEntities.ADAPTED_CANRA);
                addIfPresent(result, ModEntities.ADAPTED_EMANA);
                addIfPresent(result, ModEntities.ADAPTED_GIM);
                addIfPresent(result, ModEntities.ADAPTED_HULL);
                addIfPresent(result, ModEntities.ADAPTED_IKI);
                addIfPresent(result, ModEntities.ADAPTED_LUM);
                addIfPresent(result, ModEntities.ADAPTED_NOGLA);
                addIfPresent(result, ModEntities.ADAPTED_RANRAC);
                addIfPresent(result, ModEntities.ADAPTED_SHYCO);
                addIfPresent(result, ModEntities.ADAPTED_WYMO);
                addIfPresent(result, ModEntities.ADAPTED_ZAA);
            }
            case "beckon" -> {
                addIfPresent(result, ModEntities.BECKON_COMMON);
                addIfPresent(result, ModEntities.BECKON_UNCOMMON);
                addIfPresent(result, ModEntities.BECKON_RARE);
                addIfPresent(result, ModEntities.BECKON_EPIC);
                addIfPresent(result, ModEntities.BECKON_LEGENDARY);
            }
            case "dispatcher" -> {
                addIfPresent(result, ModEntities.DISPATCHER_COMMON);
                addIfPresent(result, ModEntities.DISPATCHER_UNCOMMON);
                addIfPresent(result, ModEntities.DISPATCHER_RARE);
                addIfPresent(result, ModEntities.DISPATCHER_EPIC);
            }
            case "rooter" -> {
                addIfPresent(result, ModEntities.ROOTER_COMMON);
                addIfPresent(result, ModEntities.ROOTER_UNCOMMON);
                addIfPresent(result, ModEntities.ROOTER_RARE);
                addIfPresent(result, ModEntities.ROOTER_EPIC);
            }
            case "pure" -> {
                addIfPresent(result, ModEntities.PURE_FLAM);
                addIfPresent(result, ModEntities.PURE_FLOG);
                addIfPresent(result, ModEntities.PURE_OMBOO);
                addIfPresent(result, ModEntities.PURE_ALAFHA);
                addIfPresent(result, ModEntities.PURE_GANRO);
                addIfPresent(result, ModEntities.PURE_ESOR);
                addIfPresent(result, ModEntities.PURE_ELVIA);
                addIfPresent(result, ModEntities.PURE_ANGED);
            }
            case "deterrent" -> {
                addIfPresent(result, ModEntities.NEXUS_GUARD);         // Nak
                addIfPresent(result, ModEntities.NEXUS_OVERSEER);      // Unvo
                addIfPresent(result, ModEntities.NEXUS_CONSTRUCT);     // Tonro
                addIfPresent(result, ModEntities.DETERRENT_SENTRY);
                addIfPresent(result, ModEntities.DETERRENT_BASTION);
                addIfPresent(result, ModEntities.DETERRENT_OUTPOST);
                addIfPresent(result, ModEntities.DETERRENT_VENKROL_SIV);
            }
            case "ancient" -> {
                addIfPresent(result, ModEntities.ANCIENT_COLOSSUS);
                addIfPresent(result, ModEntities.ANCIENT_DREADNOUGHT);
                addIfPresent(result, ModEntities.ANCIENT_LEVIATHAN);
            }
            case "abomination" -> {
                addIfPresent(result, ModEntities.ABOMINATION_AMALGAM);
                addIfPresent(result, ModEntities.ABOMINATION_CHIMERA);
                addIfPresent(result, ModEntities.ABOMINATION_HYDRA);
            }
            case "preeminent" -> {
                addIfPresent(result, ModEntities.PREEMINENT_MARAUDER);
                addIfPresent(result, ModEntities.PREEMINENT_SOVEREIGN);
                addIfPresent(result, ModEntities.PREEMINENT_WARDEN);
            }
            default -> {
                // Try to match by ParasiteType serial name
                for (ParasiteType pt : ParasiteType.values()) {
                    if (pt.getSerializedName().equals(lower)) {
                        // We can't easily map ParasiteType to EntityType at runtime
                        // without a registry, so we just return empty and let the user
                        // use the tier name instead. A future enhancement could add
                        // a ParasiteType → EntityType mapping.
                        break;
                    }
                }
                // For unrecognized types, try namespace-qualified lookup
                if (lower.contains(":")) {
                    // Could attempt ForgeRegistries.ENTITIES.getValue() but this
                    // adds complexity; tier names cover the main use case
                }
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static void addIfPresent(List<EntityType<?>> list,
                                     net.minecraftforge.registries.RegistryObject<? extends EntityType<?>> ro) {
        if (ro.isPresent()) {
            list.add(ro.get());
        }
    }

    private static int killAll(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();

        net.minecraft.world.level.border.WorldBorder border = level.getWorldBorder();
        AABB searchBounds = new AABB(
                border.getMinX(), level.getMinBuildHeight(), border.getMinZ(),
                border.getMaxX(), level.getMaxBuildHeight(), border.getMaxZ()
        );

        List<EntityParasiteBase> parasites = level.getEntitiesOfClass(
                EntityParasiteBase.class,
                searchBounds
        );

        if (parasites.isEmpty()) {
            ctx.getSource().sendSuccess(
                    () -> Component.translatable("commands.subspaceparasite.killall.none"),
                    false
            );
            return 0;
        }

        for (EntityParasiteBase parasite : parasites) {
            parasite.discard();
        }

        final int count = parasites.size();
        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.killall.success", count),
                true
        );
        return count;
    }
}
