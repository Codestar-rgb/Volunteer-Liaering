package com.subspaceparasite.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 诱饵组件 (Lure Component)
 * 原版 SRP 机制：用于合成进化诱饵的基础组件
 * 有多种变体：基础、原始、适应性、野性、纯净
 */
public class ItemLure extends Item {

    private final LureType type;

    public enum LureType {
        BASE("base", "§7Base Component"),
        PRIMORDIAL("primordial", "§aPrimordial Component"),
        ADAPTIVE("adaptive", "§bAdaptive Component"),
        FERAL("feral", "§cFeral Component"),
        PURE("pure", "§dPure Component");

        private final String name;
        private final String description;

        LureType(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    public ItemLure(LureType type) {
        super(new Properties().stacksTo(64));
        this.type = type;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("§4§lLure Component"));
        tooltip.add(Component.literal(type.getDescription()));
        tooltip.add(Component.literal("§8Used to craft Evolution Lures."));
    }

    public LureType getType() {
        return type;
    }
}
