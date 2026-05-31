package com.subspaceparasite.core;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.common.menu.MenuInfuserFurnace;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 菜单类型注册表
 */
public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, SubspaceParasite.MOD_ID);

    // 感染熔炉菜单
    public static final RegistryObject<MenuType<MenuInfuserFurnace>> INFUSER_FURNACE_MENU =
            MENU_TYPES.register("infuser_furnace", () ->
                    IForgeMenuType.create((windowId, inv, data) -> 
                            new MenuInfuserFurnace(windowId, inv)));
}
