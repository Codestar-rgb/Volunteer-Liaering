package com.subspaceparasite.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Capability provider that attaches {@link ParasiteCapability} to LivingEntity instances.
 * <p>
 * Uses the LazyOptional pattern for capability access and implements
 * ICapabilitySerializable for automatic NBT persistence.
 */
public class ParasiteCapabilityProvider implements ICapabilitySerializable<CompoundTag> {

    private final ParasiteCapability capability = new ParasiteCapability();
    private final LazyOptional<ParasiteCapability> lazyOptional = LazyOptional.of(() -> capability);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ParasiteCapability.CAPABILITY) {
            return lazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return capability.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        capability.deserializeNBT(nbt);
    }

    /**
     * Invalidates the LazyOptional when the provider is no longer valid.
     * Called automatically when the owning entity is removed.
     */
    public void invalidate() {
        lazyOptional.invalidate();
    }
}
