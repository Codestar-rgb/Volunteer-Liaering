package com.subspaceparasite.common.network;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.entity.base.EvolutionComponent;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

/**
 * Client-to-Server packet for requesting evolution actions on parasite entities.
 * <p>
 * Actions:
 * <ul>
 *   <li>0 = Force Evolve - triggers an immediate evolution on the target entity</li>
 *   <li>1 = Reset Evolution - resets all evolution data to initial state</li>
 *   <li>2 = Grant Gene - grants a random gene to the target entity</li>
 *   <li>3 = Set Phase - sets the entity's phase-created value</li>
 * </ul>
 * <p>
 * Only players in creative mode or with admin (permission level 2+) can execute
 * these actions. This is a debugging/administration tool, not a gameplay mechanic.
 */
public class EvolutionActionPacket {

    /** Action type constants */
    public static final int ACTION_FORCE_EVOLVE = 0;
    public static final int ACTION_RESET_EVOLUTION = 1;
    public static final int ACTION_GRANT_GENE = 2;
    public static final int ACTION_SET_PHASE = 3;

    public int actionType;
    public int entityId;

    public EvolutionActionPacket(int actionType, int entityId) {
        this.actionType = actionType;
        this.entityId = entityId;
    }

    public static void encode(EvolutionActionPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.actionType);
        buf.writeInt(msg.entityId);
    }

    public static EvolutionActionPacket decode(FriendlyByteBuf buf) {
        return new EvolutionActionPacket(buf.readInt(), buf.readInt());
    }

    public static void handle(EvolutionActionPacket msg, NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            // Permission check: creative mode or admin (permission level >= 2)
            if (!isAuthorized(player)) {
                player.sendSystemMessage(Component.translatable(
                        "message.subspaceparasite.evolution_action.denied"));
                return;
            }

            Entity target = player.level().getEntity(msg.entityId);
            if (target == null) {
                player.sendSystemMessage(Component.translatable(
                        "message.subspaceparasite.evolution_action.no_target"));
                return;
            }

            if (!(target instanceof EntityParasiteBase parasite)) {
                player.sendSystemMessage(Component.translatable(
                        "message.subspaceparasite.evolution_action.not_parasite"));
                return;
            }

            // Execute the requested action
            switch (msg.actionType) {
                case ACTION_FORCE_EVOLVE -> handleForceEvolve(player, parasite);
                case ACTION_RESET_EVOLUTION -> handleResetEvolution(player, parasite);
                case ACTION_GRANT_GENE -> handleGrantGene(player, parasite);
                case ACTION_SET_PHASE -> handleSetPhase(player, parasite);
                default -> player.sendSystemMessage(Component.translatable(
                        "message.subspaceparasite.evolution_action.unknown_action"));
            }
        });
    }

    /**
     * Checks whether the player is authorized to perform evolution actions.
     * Requires creative mode or permission level 2+ (admin).
     *
     * @param player the server player making the request
     * @return true if authorized
     */
    private static boolean isAuthorized(ServerPlayer player) {
        return player.isCreative() || player.hasPermissions(2);
    }

    /**
     * Action 0: Force-evolves the target parasite entity.
     * Triggers an immediate evolution cycle, advancing the entity's
     * evolution level and applying evolution bonuses.
     *
     * @param player   the admin player
     * @param parasite the target parasite entity
     */
    private static void handleForceEvolve(ServerPlayer player, EntityParasiteBase parasite) {
        EvolutionComponent evoComp = parasite.getEvolutionComponent();
        if (evoComp == null) {
            player.sendSystemMessage(Component.translatable(
                    "message.subspaceparasite.evolution_action.no_component"));
            return;
        }

        // Grant enough points to trigger an evolution, then let the component handle it
        evoComp.addEvolutionPoints(evoComp.getEvolutionThreshold());
        parasite.setEvolutionPoints((int) evoComp.getEvolutionPointsInternal());

        // Force the component to attempt evolution on next tick
        // by ensuring points meet threshold
        if (evoComp.getEvolutionPointsInternal() >= evoComp.getEvolutionThreshold()) {
            // Directly call attemptEvolution (it's protected, so we use addEvolutionPoints
            // which triggers the check on next tick via tick())
            // For immediate effect, we manually evolve:
            evoComp.addEvolutionPoints(0); // Triggers threshold check
        }

        player.sendSystemMessage(Component.translatable(
                "message.subspaceparasite.evolution_action.force_evolve",
                parasite.getName()));

        SubspaceParasite.LOGGER.debug("Player {} force-evolved entity {} (ID: {})",
                player.getName().getString(), parasite.getName().getString(), parasite.getId());
    }

    /**
     * Action 1: Resets the target parasite's evolution to initial state.
     * Clears all evolution points, level, and gene data.
     *
     * @param player   the admin player
     * @param parasite the target parasite entity
     */
    private static void handleResetEvolution(ServerPlayer player, EntityParasiteBase parasite) {
        EvolutionComponent evoComp = parasite.getEvolutionComponent();
        if (evoComp != null) {
            // Reset by loading a fresh tag
            net.minecraft.nbt.CompoundTag emptyTag = new net.minecraft.nbt.CompoundTag();
            emptyTag.putDouble("EvolutionPoints", 0.0);
            emptyTag.putDouble("EvolutionThreshold", 100.0);
            emptyTag.putInt("AccumulationTimer", 0);
            emptyTag.putInt("AccumulationInterval", 600);
            emptyTag.putInt("MutationTimer", 0);
            emptyTag.putInt("MutationCheckInterval", 2400);
            emptyTag.putInt("EvolutionLevel", 0);
            emptyTag.putBoolean("CanEvolve", true);
            emptyTag.putBoolean("HasMutated", false);
            evoComp.load(emptyTag);
        }

        // Reset entity-level evolution points
        parasite.setEvolutionPoints(0);

        // Reset all gene data
        for (int i = 0; i < GeneType.booleanGeneCount(); i++) {
            parasite.setGeneBoolean(i, false);
        }
        for (int i = 0; i < GeneType.floatGeneCount(); i++) {
            parasite.setGeneFloat(i, 0.0f);
        }

        parasite.applyGeneModifications();
        parasite.applyBonuses();

        player.sendSystemMessage(Component.translatable(
                "message.subspaceparasite.evolution_action.reset_evolution",
                parasite.getName()));

        SubspaceParasite.LOGGER.debug("Player {} reset evolution for entity {} (ID: {})",
                player.getName().getString(), parasite.getName().getString(), parasite.getId());
    }

    /**
     * Action 2: Grants a random gene to the target parasite entity.
     * Activates a random unactivated boolean gene, or boosts a random
     * float gene if all booleans are already active.
     *
     * @param player   the admin player
     * @param parasite the target parasite entity
     */
    private static void handleGrantGene(ServerPlayer player, EntityParasiteBase parasite) {
        // Try to activate a random unactivated boolean gene
        GeneType chosen = null;
        GeneType[] genes = GeneType.values();
        java.util.List<GeneType> inactiveGenes = new java.util.ArrayList<>();

        for (GeneType gene : genes) {
            if (gene.isBoolean() && !parasite.hasGene(gene)) {
                inactiveGenes.add(gene);
            }
        }

        if (!inactiveGenes.isEmpty()) {
            chosen = inactiveGenes.get(parasite.getRandom().nextInt(inactiveGenes.size()));
            parasite.activateGene(chosen);
        } else {
            // All boolean genes active; boost a random float gene
            GeneType[] floatGenes = { GeneType.POISON_HEALING, GeneType.MOB_HEALING,
                    GeneType.ATTACK_SPEED, GeneType.REGEN_RATE, GeneType.ANTI_KNOCKBACK,
                    GeneType.LEAP_POWER, GeneType.PROJECTILE_SPEED, GeneType.INFECTIOUSNESS };
            chosen = floatGenes[parasite.getRandom().nextInt(floatGenes.length)];
            float current = parasite.getGeneFloat(chosen.getIndex());
            parasite.setGeneFloat(chosen.getIndex(), current + parasite.getRandom().nextFloat() * 0.3f);
        }

        parasite.applyGeneModifications();

        String geneName = chosen != null ? chosen.name() : "UNKNOWN";
        player.sendSystemMessage(Component.translatable(
                "message.subspaceparasite.evolution_action.grant_gene",
                parasite.getName(), geneName));

        SubspaceParasite.LOGGER.debug("Player {} granted gene {} to entity {} (ID: {})",
                player.getName().getString(), geneName,
                parasite.getName().getString(), parasite.getId());
    }

    /**
     * Action 3: Sets the target parasite's phase-created value.
     * This changes which evolution phase the entity was created in,
     * affecting its stat bonuses and behavior.
     *
     * @param player   the admin player
     * @param parasite the target parasite entity
     */
    private static void handleSetPhase(ServerPlayer player, EntityParasiteBase parasite) {
        // Cycle through phases: PHASE_0 -> PHASE_1 -> PHASE_2 -> PHASE_3 -> PHASE_4 -> PHASE_0
        EvoPhase currentPhase = parasite.getPhaseCreated();
        int currentNum = currentPhase.getPhaseNumber();
        int nextNum = (currentNum + 1) % (EvoPhase.getMaxPhaseNumber() + 1); // Phases 0-4
        EvoPhase newPhase = EvoPhase.getByPhaseNumber(nextNum);

        parasite.setPhaseCreated(newPhase);
        parasite.applyBonuses();

        player.sendSystemMessage(Component.translatable(
                "message.subspaceparasite.evolution_action.set_phase",
                parasite.getName(), newPhase.name()));

        SubspaceParasite.LOGGER.debug("Player {} set phase of entity {} (ID: {}) to {}",
                player.getName().getString(), parasite.getName().getString(),
                parasite.getId(), newPhase.name());
    }
}
