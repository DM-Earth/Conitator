package com.dm.earth.conitator.api.builder.core;

import com.dm.earth.conitator.api.Conitator;

import net.minecraft.util.Identifier;

/**
 * Registration builder for a registry. (eg. items, blocks)
 */
public abstract class RegistrationBuilder<T> {

	protected T instance = null;
	protected final Conitator conitator;
	protected final Identifier id;

	/**
	 * @param conitator The conitator of this builder
	 * @param id        The registration {@code Identifier} of this builder
	 */
	protected RegistrationBuilder(Conitator conitator, Identifier id) {
		this.conitator = conitator;
		this.id = id;
	}

	/**
	 * Get or build the registration.
	 *
	 * @return The object of registration
	 */
	public final T get() {
		if (instance == null)
			this.instance = this.build();
		return this.instance;
	}

	/**
	 * @return If the builder has been built before
	 */
	public final boolean isBuilt() {
		return this.instance != null;
	}

	/**
	 * Build this builder. You need to register it to registry if it's required, it
	 * will be executed in the first time when getting the instance.
	 *
	 * @return The object that has been built
	 */
	protected abstract T build();

}
