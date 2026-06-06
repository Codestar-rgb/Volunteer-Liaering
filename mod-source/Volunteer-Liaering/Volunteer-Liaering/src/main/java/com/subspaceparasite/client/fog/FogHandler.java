package com.subspaceparasite.client.fog;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Fog rendering handler for the SubspaceParasite mod.
 * Applies phase-aware fog with smooth interpolation.
 */
@Mod.EventBusSubscriber(modid = SubspaceParasite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class FogHandler {

    // 5 density levels for phases 0-4 (matching original SRP)
    private static final float[] FOG_DENSITY_BY_PHASE = {
            0.0f,    // Phase 0: Pre-assimilation — No fog
            0.01f,   // Phase 1: Primitive — Slight haze
            0.03f,   // Phase 2: Adapted — Light fog
            0.07f,   // Phase 3: Evolved — Heavy fog
            0.15f    // Phase 4: Apex — Maximum fog
    };

    // Fog color tints by phase (RGB multipliers)
    private static final float[][] FOG_COLOR_TINT_BY_PHASE = {
            {1.0f, 1.0f, 1.0f},     // Phase 0: Normal
            {0.95f, 0.95f, 0.97f},   // Phase 1: Barely tinted
            {0.85f, 0.83f, 0.88f},   // Phase 2: Cool purple hint
            {0.62f, 0.55f, 0.72f},   // Phase 3: Deep purple
            {0.30f, 0.22f, 0.45f}    // Phase 4: Dark purple
    };

    // Smooth interpolation state
    private static float currentDensity = 0.0f;
    private static float targetDensity = 0.0f;
    private static float currentColorR = 1.0f;
    private static float currentColorG = 1.0f;
    private static float currentColorB = 1.0f;
    private static float targetColorR = 1.0f;
    private static float targetColorG = 1.0f;
    private static float targetColorB = 1.0f;

    private static final float INTERPOLATION_SPEED = 0.02f;

    /**
     * Handle fog rendering based on evolution phase.
     */
    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        int phase = ClientPhaseCache.getCurrentPhase();
        if (phase <= 0) return;

        // Check if player is in a parasite biome
        if (!isInParasiteBiome(mc)) return;

        // Get target density for current phase
        int clampedPhase = Math.min(phase, FOG_DENSITY_BY_PHASE.length - 1);
        targetDensity = FOG_DENSITY_BY_PHASE[clampedPhase];

        // Smooth interpolation
        currentDensity = lerp(currentDensity, targetDensity, INTERPOLATION_SPEED);

        if (currentDensity > 0.001f) {
            float farPlaneDistance = event.getFarPlaneDistance();
            float nearPlaneDistance = event.getNearPlaneDistance();

            // Apply LINEAR fog
            event.setNearPlaneDistance(nearPlaneDistance);
            event.setFarPlaneDistance(farPlaneDistance * (1.0f - currentDensity));
            event.setCanceled(true);
        }
    }

    /**
     * Handle fog color desaturation in parasite biomes.
     */
    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        int phase = ClientPhaseCache.getCurrentPhase();
        if (phase <= 0) return;

        if (!isInParasiteBiome(mc)) return;

        int clampedPhase = Math.min(phase, FOG_COLOR_TINT_BY_PHASE.length - 1);
        float[] tint = FOG_COLOR_TINT_BY_PHASE[clampedPhase];

        targetColorR = tint[0];
        targetColorG = tint[1];
        targetColorB = tint[2];

        // Smooth interpolation
        currentColorR = lerp(currentColorR, targetColorR, INTERPOLATION_SPEED);
        currentColorG = lerp(currentColorG, targetColorG, INTERPOLATION_SPEED);
        currentColorB = lerp(currentColorB, targetColorB, INTERPOLATION_SPEED);

        event.setRed(event.getRed() * currentColorR);
        event.setGreen(event.getGreen() * currentColorG);
        event.setBlue(event.getBlue() * currentColorB);
    }

    /**
     * Check if the player is currently in a parasite biome.
     */
    private static boolean isInParasiteBiome(Minecraft mc) {
        if (mc.player == null) return false;
        ClientLevel level = mc.level;
        BlockPos pos = mc.player.blockPosition();
        Biome biome = level.getBiome(pos).value();
        ResourceLocation biomeId = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME).getKey(biome);
        return biomeId != null && biomeId.getNamespace().equals(SubspaceParasite.MOD_ID);
    }

    /**
     * Linear interpolation helper.
     */
    private static float lerp(float current, float target, float speed) {
        if (Math.abs(current - target) < 0.001f) return target;
        return current + (target - current) * speed;
    }

    /**
     * Client-side cache for phase data synced from server.
     */
    public static class ClientPhaseCache {
        private static int currentPhase = 0;

        public static int getCurrentPhase() {
            return currentPhase;
        }

        public static void setPhase(int phase) {
            currentPhase = phase;
        }

        public static void reset() {
            currentPhase = 0;
            currentDensity = 0.0f;
            targetDensity = 0.0f;
            currentColorR = 1.0f;
            currentColorG = 1.0f;
            currentColorB = 1.0f;
            targetColorR = 1.0f;
            targetColorG = 1.0f;
            targetColorB = 1.0f;
        }
    }
}
