package com.dm.earth.conitator.api.builder.core;

import com.dm.earth.conitator.api.Conitator;
import net.minecraft.util.Identifier;

public abstract class RegistrationBuilder<T> {

	protected T instance = null;
	protected final Conitator conitator;
	protected final Identifier id;

	protected RegistrationBuilder(Conitator conitator, Identifier id) {
		this.conitator = conitator;
		this.id = id;
	}

	public T get() {
		if (instance == null)
			this.instance = this.build();
		return this.instance;
	}

	public boolean isBuilded() {
		return this.instance != null;
	}

	protected abstract T build();

}
