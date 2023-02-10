package com.dm.earth.conitator.api;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.dm.earth.conitator.impl.ConitatorImpl;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * A conitator is a place to store resources for a mod's initialization.
 */
public interface Conitator {

	/**
	 * Create a new instance.
	 *
	 * @return The new instance
	 */
	static Conitator create() {
		return new ConitatorImpl();
	}

	/**
	 * An EntryKey is a key of the entry map in the {@code Conitator} and it will be
	 * mapped into a list of {@code Identifier}.
	 */
	interface EntryKey<T> extends Predicate<Object> {

		/**
		 * Create an EntryKey based on a {@code Registry}.
		 *
		 * @param <T>       The type of the registration
		 * @param id        The id of this EntryKey
		 * @param predicate Checker of this EntryKey if the given object is type of the
		 *                  registration
		 * @param registry  The {@code Registry}
		 * @return The created EntryKey
		 */
		static <T> EntryKey<T> ofRegistry(Identifier id, Predicate<Object> predicate,
				Registry<T> registry) {
			return create(id, predicate, registry::get);
		}

		/**
		 * Create a simple EntryKey.
		 *
		 * @param <T>       The type of the entry
		 * @param id        The id of this EntryKey
		 * @param predicate Checker of this EntryKey if the given object is type of the
		 *                  entry
		 * @param getter    The entry getter of this EntryKey
		 * @return The created EntryKey
		 */
		static <T> EntryKey<T> create(Identifier id, Predicate<Object> predicate,
				Function<Identifier, T> getter) {
			return new EntryKey<T>() {

				@Override
				public boolean test(Object arg0) {
					return predicate.test(arg0);
				}

				@Override
				@Nullable
				public Optional<T> get(Conitator instance, Identifier arg0) {
					return Optional.ofNullable(getter.apply(arg0));
				}

				@Override
				public Identifier getId() {
					return id;
				}

			};
		}

		/**
		 * Get the id of this EntryKey.
		 *
		 * @return The id
		 */
		Identifier getId();

		/**
		 * Get an entry from the given conitator instance and id.
		 *
		 * @param instance The conitator instance
		 * @param id       The entry id
		 * @return The optional entry
		 */
		Optional<T> get(Conitator instance, Identifier id);

	}

	Map<EntryKey<?>, ArrayList<Identifier>> getEntries();

	Conitator apply(EntryKey<?>... entryKeys);

	void loadDatagen(FabricDataGenerator dataGenerator);

	default Optional<Map.Entry<EntryKey<?>, ArrayList<Identifier>>> getEntry(Identifier id) {
		return this.getEntries().entrySet().stream().filter(entry -> entry.getKey().getId().equals(id)).findFirst();
	}

	default Optional<Map.Entry<EntryKey<?>, ArrayList<Identifier>>> findFitEntry(Object obj) {
		return this.getEntries().entrySet().stream().filter(entry -> entry.getKey().test(obj)).findFirst();
	}

}
