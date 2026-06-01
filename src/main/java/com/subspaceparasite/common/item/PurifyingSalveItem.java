package com.subspaceparasite.common.item;

import com.subspaceparasite.common.capability.ParasiteCapability;
import com.subspaceparasite.common.effect.CleansingEffect;
import com.subspaceparasite.common.effect.PurgeEffect;
import com.subspaceparasite.core.ModEffects;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Purifying Salve — an item that reduces infection level when used.
 * <p>
 * When a player (or any LivingEntity) uses this item, it:
 * <ul>
 *   <li>Reduces the infection level in their ParasiteCapability by the configured amount</li>
 *   <li>Applies the Cleansing effect for ongoing infection reduction</li>
 *   <li>Removes the Wither effect (COTH visual indicator)</li>
 *   <li>Consumes one item from the stack</li>
 * </ul>
 */
public class PurifyingSalveItem extends Item {

    /** Amount of infection reduction per use. */
    private final int infectionReduction;

    /** Duration of the Cleansing effect granted on use (in ticks). */
    private static final int CLEANSING_DURATION = 600; // 30 seconds

    public PurifyingSalveItem(int infectionReduction, Properties properties) {
        super(properties);
        this.infectionReduction = infectionReduction;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // Reduce infection via capability
            player.getCapability(ParasiteCapability.CAPABILITY).ifPresent(cap -> {
                if (cap.getInfectionLevel() > 0 && !cap.isImmune()) {
                    cap.reduceInfection(infectionReduction);
                    cap.markDirty();

                    // Apply Cleansing effect for ongoing reduction
                    player.addEffect(new MobEffectInstance(ModEffects.CLEANSING.get(),
                            CLEANSING_DURATION, 0));

                    // Remove COTH indicator effects
                    player.removeEffect(net.minecraft.world.effect.MobEffects.WITHER);
                    player.removeEffect(net.minecraft.world.effect.MobEffects.CONFUSION);

                    // Play purification sound
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.2F);

                    // Consume one item
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
            });
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.subspaceparasite.purifyingsalve.tooltip",
                infectionReduction));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Always glows like a curative item
    }
}
