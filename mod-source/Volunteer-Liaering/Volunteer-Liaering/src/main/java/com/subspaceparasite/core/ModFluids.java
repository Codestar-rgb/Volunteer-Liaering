package com.subspaceparasite.core;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.subspaceparasite.SubspaceParasite;
import net.minecraftforge.registries.RegistryObject;

/**
 * Fluid registry for the SubspaceParasite mod.
 * Contains the DeadBlood fluid (still + flowing), fluid block, and bucket.
 */
public class ModFluids {

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, SubspaceParasite.MOD_ID);

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(net.minecraftforge.registries.ForgeRegistries.Keys.FLUID_TYPES, SubspaceParasite.MOD_ID);

    // === DeadBlood Fluid Type ===
    public static final RegistryObject<FluidType> DEAD_BLOOD_TYPE = FLUID_TYPES.register("deadblood",
            () -> new FluidType(FluidType.Properties.create()
                    .density(1500)   // Denser than water (heavier, thicker blood)
                    .viscosity(2000) // More viscous than water
                    .temperature(310) // Body temperature
                    .lightLevel(0)
            ));

    // === DeadBlood Still Fluid ===
    public static final RegistryObject<FlowingFluid> DEAD_BLOOD_STILL = FLUIDS.register("deadblood",
            () -> new ForgeFlowingFluid.Source(ModFluids.DEAD_BLOOD_PROPERTIES));

    // === DeadBlood Flowing Fluid ===
    public static final RegistryObject<FlowingFluid> DEAD_BLOOD_FLOWING = FLUIDS.register("deadblood_flowing",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.DEAD_BLOOD_PROPERTIES));

    // === Fluid Properties (lazily initialized to resolve circular dependencies) ===
    public static final ForgeFlowingFluid.Properties DEAD_BLOOD_PROPERTIES =
            new ForgeFlowingFluid.Properties(
                    DEAD_BLOOD_TYPE,
                    DEAD_BLOOD_STILL,
                    DEAD_BLOOD_FLOWING
            )
                    .bucket(ModItems.DEAD_BLOOD_BUCKET)
                    .block(ModBlocks.DEAD_BLOOD);

    private ModFluids() {
        // Utility class - prevent instantiation
    }
}
