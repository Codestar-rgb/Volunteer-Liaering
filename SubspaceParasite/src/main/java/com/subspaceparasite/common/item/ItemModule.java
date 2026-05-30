package com.subspaceparasite.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Module item for parasite enhancement.
 * Different module types provide different bonuses when installed in compatible entities.
 */
public class ItemModule extends Item {
    
    private final String moduleType;
    private final ChatFormatting color;
    
    public ItemModule(String moduleType, ChatFormatting color) {
        super(new Properties().stacksTo(16));
        this.moduleType = moduleType;
        this.color = color;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        String displayName = moduleType.substring(0, 1).toUpperCase() + moduleType.substring(1).replace("_", " ");
        tooltip.add(Component.literal("§o" + displayName + " Module").withStyle(color));
        tooltip.add(Component.literal("Install in compatible parasites for bonuses").withStyle(ChatFormatting.GRAY));
        
        // Add type-specific description
        switch (moduleType.toLowerCase()) {
            case "adapter":
                tooltip.add(Component.literal("Adapts to environmental conditions").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "barricade":
                tooltip.add(Component.literal("Increases defense and resistance").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "dynamo":
                tooltip.add(Component.literal("Generates energy over time").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "exothermic":
                tooltip.add(Component.literal("Deals fire damage on attack").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "ferromagnetic":
                tooltip.add(Component.literal("Attracts metallic projectiles").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "gravitational":
                tooltip.add(Component.literal("Manipulates gravity field").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "hyperthreat":
                tooltip.add(Component.literal("Greatly increases aggression").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "insulating":
                tooltip.add(Component.literal("Provides elemental resistance").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "kinetic":
                tooltip.add(Component.literal("Stores momentum for powerful strikes").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "luminous":
                tooltip.add(Component.literal("Emits blinding light").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "motile":
                tooltip.add(Component.literal("Enhances movement speed").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "nutrient":
                tooltip.add(Component.literal("Improves regeneration rate").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "outreach":
                tooltip.add(Component.literal("Extends interaction range").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "pheromone":
                tooltip.add(Component.literal("Attracts nearby parasites").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "quantum":
                tooltip.add(Component.literal("Enables phase-shifting abilities").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "resilient":
                tooltip.add(Component.literal("Increases maximum health").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "siege":
                tooltip.add(Component.literal("Bonus damage against structures").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "thornian":
                tooltip.add(Component.literal("Reflects melee damage").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "umbrella":
                tooltip.add(Component.literal("Creates protective barrier").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "venomous":
                tooltip.add(Component.literal("Applies poison on hit").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "wanderer":
                tooltip.add(Component.literal("Increases patrol range").withStyle(ChatFormatting.DARK_GRAY));
                break;
            case "xenolithic":
                tooltip.add(Component.literal("Assimilates foreign biology").withStyle(ChatFormatting.DARK_GRAY));
                break;
        }
    }
}
