package com.subspaceparasite.handler;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.capability.ParasiteCapability;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.entity.base.EvolutionDispatcher;
import com.subspaceparasite.common.world.DifficultySystem;
import com.subspaceparasite.common.world.ModWorldData;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Forge event handler registered to MinecraftForge.EVENT_BUS.
 * Handles global parasite-related events.
 */
@Mod.EventBusSubscriber(modid = SubspaceParasite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    private static AABB getWorldBorderAABB(Level level) {
        net.minecraft.world.level.border.WorldBorder border = level.getWorldBorder();
        return new AABB(border.getMinX(), level.getMinBuildHeight(), border.getMinZ(),
                border.getMaxX(), level.getMaxBuildHeight(), border.getMaxZ());
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof EntityParasiteBase parasite)) return;

        Level level = event.getLevel();

        // Phase check using the API's ParasiteType spawn phase range
        EvoPhase currentPhase = EntityParasiteBase.getCurrentPhase(level);
        ParasiteType pt = parasite.getParasiteType();
        if (pt != null && !pt.canSpawnInPhase(currentPhase)) {
            event.setCanceled(true);
            if (ModConfigSystems.shouldLogPhaseChanges()) {
                LOGGER.debug("Cancelled spawn of {} in phase {}", pt, currentPhase);
            }
            return;
        }

        if (level.getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL
                && !ModConfigSystems.canSpawnInPeaceful()) {
            event.setCanceled(true);
            return;
        }

        if (ModConfigSystems.isDimensionBlacklisted(level)) {
            event.setCanceled(true);
            return;
        }

        // Mob cap - check type-specific cap
        if (pt != null) {
            int typeCap = ModConfigSystems.getTypeMobCap(pt);
            if (typeCap > 0) {
                long typeCount = level.getEntitiesOfClass(EntityParasiteBase.class,
                        getWorldBorderAABB(level),
                        p -> p.getParasiteType() == pt).size();
                if (typeCount >= typeCap) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        if (ModConfigSystems.isEmergencyCullEnabled()) {
            checkEmergencyCull(level);
        }
    }

    private static void checkEmergencyCull(Level level) {
        int globalCap = ModConfigSystems.getMobCap();
        if (globalCap <= 0) return;

        long totalParasites = level.getEntitiesOfClass(EntityParasiteBase.class,
                getWorldBorderAABB(level)).size();

        double threshold = globalCap * ModConfigSystems.getEmergencyCullThreshold();
        if (totalParasites <= threshold) return;

        int excess = (int) (totalParasites - globalCap);
        int toCull = (int) (excess * ModConfigSystems.getEmergencyCullPercent());

        List<EntityParasiteBase> candidates = level.getEntitiesOfClass(EntityParasiteBase.class,
                getWorldBorderAABB(level), EntityParasiteBase::canDespawn);

        candidates.sort((a, b) -> {
            int ea = a.getEvolutionComponent() != null ? a.getEvolutionComponent().getEvolutionLevel() : 0;
            int eb = b.getEvolutionComponent() != null ? b.getEvolutionComponent().getEvolutionLevel() : 0;
            return ea - eb;
        });

        int culled = 0;
        for (EntityParasiteBase p : candidates) {
            if (culled >= toCull) break;
            p.discard();
            culled++;
        }

        if (culled > 0 && ModConfigSystems.shouldLogCombat()) {
            LOGGER.info("Emergency cull: removed {} parasites (total was {})", culled, totalParasites);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (ServerLevel serverLevel : event.getServer().getAllLevels()) {
            ModWorldData worldData = ModWorldData.get(serverLevel);
            worldData.tick();

            int interval = ModConfigSystems.getPhaseProgressionInterval();
            if (worldData.getTickCounter() % interval == 0) {
                checkPhaseProgression(serverLevel, worldData);
            }
        }
    }

    private static void checkPhaseProgression(ServerLevel level, ModWorldData worldData) {
        EvoPhase currentPhase = worldData.getCurrentPhase();
        if (currentPhase == EvoPhase.PHASE_4) return;

        double totalKills = worldData.getKillStat("total");
        EvoPhase nextPhase = currentPhase.next();
        if (nextPhase == currentPhase) return;

        int threshold = ModConfigSystems.getPhaseKillThreshold(nextPhase);
        if (totalKills >= threshold) {
            worldData.setCurrentPhase(level, nextPhase);
            if (ModConfigSystems.shouldLogPhaseChanges()) {
                LOGGER.info("Phase progression: {} -> {} (kills: {})",
                        currentPhase, nextPhase, totalKills);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof EntityParasiteBase parasite)) return;

        if (parasite.isMultipartEntity()) {
            for (PartEntity<?> part : parasite.getParts()) {
                if (part != null) part.discard();
            }
        }

        if (parasite.getColonyComponent() != null && parasite.getColonyComponent().isColonyLeader()) {
            parasite.getColonyComponent().onDeath();
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        LivingEntity target = event.getEntity();
        if (target instanceof EntityParasiteBase) return;

        DamageSource source = event.getSource();
        if (source.getEntity() instanceof EntityParasiteBase parasite) {
            if (parasite.getInfectionComponent() != null) {
                parasite.getInfectionComponent().spreadCOTH(target);
            }
        }

        if (ModConfigSystems.isInfectionAuraEnabled()) {
            var nearby = target.level().getEntitiesOfClass(
                    EntityParasiteBase.class,
                    target.getBoundingBox().inflate(8.0),
                    p -> p.getInfectionComponent() != null && p.getInfectionComponent().isAuraEnabled());
            for (EntityParasiteBase p : nearby) {
                p.getInfectionComponent().spreadCOTH(target);
            }
        }

        if (target.hasEffect(net.minecraft.world.effect.MobEffects.WITHER)) {
            var nearby = target.level().getEntitiesOfClass(
                    EntityParasiteBase.class, target.getBoundingBox().inflate(16.0));
            for (EntityParasiteBase p : nearby) {
                if (p.getInfectionComponent() != null) {
                    if (p.getInfectionComponent().checkConversion(target)) break;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        LivingEntity deadEntity = event.getEntity();
        DamageSource source = event.getSource();

        if (deadEntity.level() instanceof ServerLevel serverLevel) {
            ModWorldData worldData = ModWorldData.get(serverLevel);

            if (source.getEntity() instanceof EntityParasiteBase parasite) {
                String victimType = deadEntity.getType().getDescriptionId();
                worldData.addKillStat(victimType, 1);
                worldData.addKillStat("total", 1);
                parasite.addKillCount(1.0);

                if (parasite.getColonyComponent() != null) {
                    parasite.getColonyComponent().onKill();
                }

                // Notify the EvolutionDispatcher of the kill
                EvolutionDispatcher.getInstance().registerKill(parasite, deadEntity);
            } else if (source.getEntity() instanceof Player player) {
                if (deadEntity instanceof EntityParasiteBase) {
                    worldData.addKillStat("parasites_killed_by_players", 1);
                    // Player is fighting back — reduce player threat slightly
                    DifficultySystem.get(serverLevel).adjustPlayerThreat(-0.05f);
                }
            }

            // If a player is killed by a parasite, increase threat
            if (deadEntity instanceof Player && source.getEntity() instanceof EntityParasiteBase) {
                DifficultySystem.get(serverLevel).adjustPlayerThreat(0.5f);
            }
        }
    }

    /**
     * Ticks infection effects on all living entities.
     * Applies Nausea, Weakness, Slowness, and Wither based on infection level.
     */
    @SubscribeEvent
    public static void onLivingTick(LivingTickEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        LivingEntity entity = event.getEntity();

        // Tick the capability and apply infection effects
        entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(cap -> {
            cap.tick();
            cap.tickEffects(entity);
        });
    }
}
