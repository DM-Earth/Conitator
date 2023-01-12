package com.dm.earth.conitator.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;
import com.dm.earth.conitator.impl.ConitatorImpl;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public interface Conitator {

	static Conitator create() {
		return new ConitatorImpl();
	}

	interface EntryKey<T> extends Predicate<Object> {

		static <T> EntryKey<T> ofRegistry(Identifier id, Predicate<Object> predicate,
				Registry<T> registry) {
			return create(id, predicate, registry::get);
		}

		static <T> EntryKey<T> create(Identifier id, Predicate<Object> predicate,
				Function<Identifier, T> getter) {
			return new EntryKey<T>() {

				@Override
				public boolean test(Object arg0) {
					return predicate.test(arg0);
				}

				@Override
				@Nullable
				public T get(Conitator instance, Identifier arg0) {
					return getter.apply(arg0);
				}

				@Override
				public Identifier getId() {
					return id;
				}

			};
		}

		Identifier getId();

		T get(Conitator instance, Identifier id);

	}

	interface Actor<T> {

		T act(Conitator instance, T object);

		Identifier getId();

	}

	Map<EntryKey<?>, ArrayList<Identifier>> getEntries();

	List<Actor<?>> getActors();

	Conitator apply(EntryKey<?>... entryKeys);

	Conitator apply(Actor<?>... actors);

	default Optional<Map.Entry<EntryKey<?>, ArrayList<Identifier>>> getEntry(Identifier id) {
		return Optional.ofNullable(this.getEntries().entrySet().stream()
				.filter(entry -> entry.getKey().getId().equals(id)).findFirst().orElse(null));
	}

	default Optional<Map.Entry<EntryKey<?>, ArrayList<Identifier>>> findFitEntry(Object obj) {
		return Optional.ofNullable(this.getEntries().entrySet().stream()
				.filter(entry -> entry.getKey().test(obj)).findFirst().orElse(null));
	}

}
