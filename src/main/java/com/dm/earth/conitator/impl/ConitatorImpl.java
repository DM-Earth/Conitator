package com.dm.earth.conitator.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.dm.earth.conitator.api.Conitator;
import com.dm.earth.conitator.impl.datagen.DatagenLoadable;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.util.Identifier;

@Internal
public class ConitatorImpl implements Conitator {

	protected HashMap<EntryKey<?>, ArrayList<Identifier>> entries;

	public ConitatorImpl() {
		this.entries = new HashMap<>();
	}

	@Override
	public Map<EntryKey<?>, ArrayList<Identifier>> getEntries() {
		return this.entries;
	}

	@Override
	public Conitator apply(EntryKey<?>... entryKeys) {
		Arrays.stream(entryKeys).forEach(e -> this.entries.put(e, new ArrayList<>()));
		return this;
	}

	@Override
	public void loadDatagen(FabricDataGenerator dataGenerator) {
		var pack = dataGenerator.createPack();
		this.getEntries().keySet().stream().filter(key -> key instanceof DatagenLoadable)
				.forEach(g -> ((DatagenLoadable) g).loadDatagen(pack));
	}

}
