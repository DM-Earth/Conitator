package com.dm.earth.conitator.impl.datagen.entry_keys;

import java.util.Optional;

import com.dm.earth.conitator.api.Conitator;
import com.dm.earth.conitator.impl.datagen.DatagenLoadable;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;

public abstract class DatagenEntryKey<P extends DataProvider> implements Conitator.EntryKey<String>, DatagenLoadable {

	@Override
	public Optional<String> get(Conitator instance, Identifier id) {
		// Useless method here
		return Optional.empty();
	}

	@Override
	public boolean test(Object arg0) {
		// Useless method here
		return false;
	}

	public abstract P createProvider(FabricDataOutput output);

	@Override
	public void loadDatagen(FabricDataGenerator.Pack pack) {
		pack.addProvider(this::createProvider);
	}

}
