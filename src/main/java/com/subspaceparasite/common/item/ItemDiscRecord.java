package com.subspaceparasite.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 唱片 (Disc Record)
 * 原版 SRP 机制：可播放的特殊音乐唱片
 * 有多种变体，对应不同的寄生虫主题音乐
 */
public class ItemDiscRecord extends Item {

    private final DiscType type;
    private final int duration;

    public enum DiscType {
        EVO("evo", "Biome Heart Beat", 120),
        ASSIMILATE("assimilate", "Ambient Parasite", 180),
        INFESTATION("infestation", "Infestation Theme", 200),
        COLONY("colony", "Colony Ambience", 160),
        BECKON("beckon", "Call of the Beckon", 240),
        FINAL_PHASE("final_phase", "Final Phase", 300);

        private final String name;
        private final String description;
        private final int duration;

        DiscType(String name, String description, int duration) {
            this.name = name;
            this.description = description;
            this.duration = duration;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getDuration() {
            return duration;
        }
    }

    public ItemDiscRecord(DiscType type) {
        super(new Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.RARE));
        this.type = type;
        this.duration = type.getDuration();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("§4§lMusic Disc"));
        tooltip.add(Component.literal("§7" + type.getDescription()));
        tooltip.add(Component.literal("§8Duration: " + (duration / 20) + " seconds"));
    }

    public DiscType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }
}
