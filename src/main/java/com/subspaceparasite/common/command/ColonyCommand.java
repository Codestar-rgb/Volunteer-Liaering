package com.subspaceparasite.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.subspaceparasite.common.world.ModWorldData;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import java.util.List;
import java.util.UUID;

/**
 * /srp colony &lt;list|remove|spawn&gt;
 * <p>
 * Manage parasite colonies in the current dimension:
 * <ul>
 *   <li>{@code list} — display all colony centers and unit counts</li>
 *   <li>{@code remove} — remove all colonies from the current dimension</li>
 *   <li>{@code spawn} — manually create a colony at the executor's position</li>
 * </ul>
 */
public class ColonyCommand {

    /**
     * Registers the "colony" subcommand tree.
     *
     * @return the literal builder for "colony"
     */
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("colony")
                .then(Commands.literal("list")
                        .executes(ColonyCommand::listColonies))
                .then(Commands.literal("remove")
                        .executes(ColonyCommand::removeColonies))
                .then(Commands.literal("spawn")
                        .executes(ColonyCommand::spawnColony));
    }

    private static int listColonies(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        ModWorldData data = ModWorldData.get(level);

        int count = data.getColonyCount();
        if (count == 0) {
            ctx.getSource().sendSuccess(
                    () -> Component.translatable("commands.subspaceparasite.colony.list_none"),
                    false
            );
            return 0;
        }

        List<BlockPos> centers = data.getColonyCenters();
        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.colony.list_header", count),
                false
        );

        for (int i = 0; i < centers.size(); i++) {
            BlockPos pos = centers.get(i);
            final int idx = i;
            ctx.getSource().sendSuccess(
                    () -> Component.translatable("commands.subspaceparasite.colony.list_entry",
                            idx + 1, pos.getX(), pos.getY(), pos.getZ()),
                    false
            );
        }

        return count;
    }

    private static int removeColonies(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        ModWorldData data = ModWorldData.get(level);

        int count = data.getColonyCount();
        if (count == 0) {
            ctx.getSource().sendFailure(
                    Component.translatable("commands.subspaceparasite.colony.list_none")
            );
            return 0;
        }

        // Reset world data which clears colonies
        data.reset();

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.colony.remove", count),
                true
        );
        return count;
    }

    private static int spawnColony(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        ModWorldData data = ModWorldData.get(level);

        BlockPos pos = BlockPos.containing(ctx.getSource().getPosition());
        UUID leaderUUID = UUID.randomUUID();

        data.addColony(pos, leaderUUID);

        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.subspaceparasite.colony.spawn",
                        pos.getX(), pos.getY(), pos.getZ()),
                true
        );
        return 1;
    }
}
