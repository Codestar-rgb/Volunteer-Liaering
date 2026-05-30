package com.subspaceparasite.client.overlay;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.core.ModEffects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/**
 * Overlay rendering for infection effects on the client HUD.
 * Handles COTH overlay, phase warnings, bleed/corrosion/viral effects,
 * NOVISION blackout, and VOMIT scrolling overlay.
 *
 * Registered via Forge's RegisterGuiOverlaysEvent in ClientSetup.
 */
public class InfectionOverlayHandler implements IGuiOverlay {

    public static final InfectionOverlayHandler INSTANCE = new InfectionOverlayHandler();

    // Texture locations for overlay effects
    private static final ResourceLocation COTH_OVERLAY = new ResourceLocation(SubspaceParasite.MOD_ID, "textures/gui/overlay/coth.png");
    private static final ResourceLocation PHASE_VIGNETTE = new ResourceLocation(SubspaceParasite.MOD_ID, "textures/gui/overlay/phase_vignette.png");
    private static final ResourceLocation BLEED_OVERLAY = new ResourceLocation(SubspaceParasite.MOD_ID, "textures/gui/overlay/bleed.png");
    private static final ResourceLocation CORROSION_OVERLAY = new ResourceLocation(SubspaceParasite.MOD_ID, "textures/gui/overlay/corrosion.png");
    private static final ResourceLocation VIRAL_OVERLAY = new ResourceLocation(SubspaceParasite.MOD_ID, "textures/gui/overlay/viral.png");
    private static final ResourceLocation VOMIT_OVERLAY = new ResourceLocation(SubspaceParasite.MOD_ID, "textures/gui/overlay/vomit.png");

    // COTH overlay stages (0-4 = 5 stages)
    private static final int COTH_STAGES = 5;

    // Vomit overlay scroll offset
    private float vomitScrollOffset = 0.0f;

    /**
     * Renders all infection overlays. Called from the Forge GUI overlay event.
     *
     * @param graphics    the GUI graphics context
     * @param partialTick the partial tick for interpolation
     */
    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        // Render each overlay effect based on active effects
        renderCOTHOverlay(graphics, player, width, height, partialTick);
        renderPhaseWarning(graphics, player, width, height, partialTick);
        renderBleedOverlay(graphics, player, width, height, partialTick);
        renderCorrosionOverlay(graphics, player, width, height, partialTick);
        renderViralOverlay(graphics, player, width, height, partialTick);
        renderNoVision(graphics, player, width, height);
        renderVomitOverlay(graphics, player, width, height, partialTick);
    }

    /**
     * Render the COTH (Corruption of the Host) overlay at 5 stages.
     */
    private void renderCOTHOverlay(GuiGraphics graphics, Player player, int width, int height, float partialTick) {
        MobEffectInstance cothEffect = player.getEffect(ModEffects.COTH.get());
        if (cothEffect == null) return;

        int amplifier = cothEffect.getAmplifier();
        int stage = Math.min(amplifier, COTH_STAGES - 1);

        float alpha = 0.1f + (stage * 0.18f); // 0.1 to 0.82
        alpha = Math.min(alpha, 0.85f);

        // Pulsing effect
        float pulse = (float) Math.sin(player.tickCount * 0.05f) * 0.05f;
        alpha += pulse;
        alpha = Math.max(0.0f, Math.min(1.0f, alpha));

        graphics.setColor(1.0f, 1.0f, 1.0f, alpha);
        graphics.blit(COTH_OVERLAY, 0, 0, 0, stage * height, width, height, width, height * COTH_STAGES);
        graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Render phase warning vignette effect.
     */
    private void renderPhaseWarning(GuiGraphics graphics, Player player, int width, int height, float partialTick) {
        int phase = com.subspaceparasite.client.fog.FogHandler.ClientPhaseCache.getCurrentPhase();
        if (phase <= 3) return;

        float alpha = (phase - 3) * 0.08f;
        alpha = Math.min(alpha, 0.5f);

        float pulse = (float) Math.sin(player.tickCount * 0.03f) * 0.03f;
        alpha += pulse;
        alpha = Math.max(0.0f, Math.min(1.0f, alpha));

        graphics.setColor(1.0f, 1.0f, 1.0f, alpha);
        graphics.blit(PHASE_VIGNETTE, 0, 0, 0, 0, width, height, width, height);
        graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Render bleed screen effect.
     */
    private void renderBleedOverlay(GuiGraphics graphics, Player player, int width, int height, float partialTick) {
        MobEffectInstance bleedEffect = player.getEffect(ModEffects.BLEED.get());
        if (bleedEffect == null) return;

        float alpha = 0.2f + (bleedEffect.getAmplifier() * 0.1f);
        alpha = Math.min(alpha, 0.7f);

        float pulse = (float) Math.sin(player.tickCount * 0.08f) * 0.04f;
        alpha += pulse;
        alpha = Math.max(0.0f, Math.min(1.0f, alpha));

        graphics.setColor(1.0f, 1.0f, 1.0f, alpha);
        graphics.blit(BLEED_OVERLAY, 0, 0, 0, 0, width, height, width, height);
        graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Render corrosion screen effect.
     */
    private void renderCorrosionOverlay(GuiGraphics graphics, Player player, int width, int height, float partialTick) {
        MobEffectInstance corrosionEffect = player.getEffect(ModEffects.CORROSION.get());
        if (corrosionEffect == null) return;

        float alpha = 0.15f + (corrosionEffect.getAmplifier() * 0.08f);
        alpha = Math.min(alpha, 0.6f);

        float flicker = player.tickCount % 4 < 2 ? 0.0f : 0.02f;
        alpha += flicker;

        graphics.setColor(1.0f, 1.0f, 1.0f, alpha);
        graphics.blit(CORROSION_OVERLAY, 0, 0, 0, 0, width, height, width, height);
        graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Render viral screen effect.
     */
    private void renderViralOverlay(GuiGraphics graphics, Player player, int width, int height, float partialTick) {
        MobEffectInstance viralEffect = player.getEffect(ModEffects.VIRAL.get());
        if (viralEffect == null) return;

        float alpha = 0.1f + (viralEffect.getAmplifier() * 0.06f);
        alpha = Math.min(alpha, 0.5f);

        // Erratic pulsing for viral
        float pulse = (float) (Math.sin(player.tickCount * 0.15f) * Math.cos(player.tickCount * 0.07f)) * 0.04f;
        alpha += pulse;
        alpha = Math.max(0.0f, Math.min(1.0f, alpha));

        graphics.setColor(1.0f, 1.0f, 1.0f, alpha);
        graphics.blit(VIRAL_OVERLAY, 0, 0, 0, 0, width, height, width, height);
        graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Render NOVISION blackout effect - completely blacks out the screen.
     */
    private void renderNoVision(GuiGraphics graphics, Player player, int width, int height) {
        MobEffectInstance novisionEffect = player.getEffect(ModEffects.NOVISION.get());
        if (novisionEffect == null) return;

        float alpha = 0.8f + (novisionEffect.getAmplifier() * 0.1f);
        alpha = Math.min(alpha, 1.0f);

        int color = ((int)(alpha * 255) << 24);
        graphics.fill(0, 0, width, height, color);
    }

    /**
     * Render VOMIT scrolling overlay effect.
     */
    private void renderVomitOverlay(GuiGraphics graphics, Player player, int width, int height, float partialTick) {
        MobEffectInstance vomitEffect = player.getEffect(ModEffects.VOMIT.get());
        if (vomitEffect == null) {
            vomitScrollOffset = 0.0f;
            return;
        }

        float alpha = 0.3f + (vomitEffect.getAmplifier() * 0.1f);
        alpha = Math.min(alpha, 0.7f);

        // Scroll the vomit texture downward
        vomitScrollOffset += partialTick * 2.0f;
        if (vomitScrollOffset >= height) {
            vomitScrollOffset -= height;
        }

        int offset = (int) vomitScrollOffset;

        graphics.setColor(1.0f, 1.0f, 1.0f, alpha);
        // Draw two copies for seamless scrolling
        graphics.blit(VOMIT_OVERLAY, 0, -offset, 0, 0, width, height, width, height);
        graphics.blit(VOMIT_OVERLAY, 0, height - offset, 0, 0, width, height, width, height);
        graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
