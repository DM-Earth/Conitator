package com.dm.earth.conitator.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import com.dm.earth.conitator.api.Conitator;
import net.minecraft.util.Identifier;

@Internal
public class ConitatorImpl implements Conitator {

	protected HashMap<EntryKey<?>, ArrayList<Identifier>> entries;
	protected ArrayList<Actor<?>> actors;

	public ConitatorImpl() {
		this.entries = new HashMap<>();
		this.actors = new ArrayList<>();
	}

	@Override
	public Map<EntryKey<?>, ArrayList<Identifier>> getEntries() {
		return this.entries;
	}

	@Override
	public List<Actor<?>> getActors() {
		return this.actors;
	}

	@Override
	public Conitator apply(EntryKey<?>... entryKeys) {
		Arrays.stream(entryKeys).forEach(e -> this.entries.put(e, new ArrayList<>()));
		return this;
	}

	@Override
	public Conitator apply(Actor<?>... actors) {
		Arrays.stream(actors).forEach(this.actors::add);
		return this;
	}

}
