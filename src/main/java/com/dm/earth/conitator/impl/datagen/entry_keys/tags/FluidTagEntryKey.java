package com.dm.earth.conitator.impl.datagen.entry_keys.tags;

import com.dm.earth.conitator.api.DefaultEntryKeys;
import com.dm.earth.conitator.impl.datagen.entry_keys.tags.core.TagEntryKey;

import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class FluidTagEntryKey extends TagEntryKey<Fluid> {

	@Override
	public Identifier getId() {
		return DefaultEntryKeys.FLUID_TAG;
	}

	@Override
	protected RegistryKey<? extends Registry<Fluid>> getRegistryKey() {
		return RegistryKeys.FLUID;
	}

	@Override
	@SuppressWarnings("deprecation")
	protected RegistryKey<Fluid> getRegistryKey(Fluid instance) {
		return instance.getBuiltInRegistryHolder().getRegistryKey();
	}

}
