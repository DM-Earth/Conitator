package com.dm.earth.conitator.impl.datagen.entry_keys.tags.core;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.quiltmc.qsl.base.api.event.Event;

import com.dm.earth.conitator.impl.datagen.entry_keys.core.DatagenEntryKey;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.HolderLookup.Provider;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

public abstract class TagEntryKey<T> extends DatagenEntryKey<TagEntryKey.ConitatorTagProvider<T>> {

	@FunctionalInterface
	public static interface GenerateTagsCallback<T> {

		void generate(ConitatorTagProvider<T> tagProvider);

	}

	public static class SimpleGenerateTagsCallback<T> implements GenerateTagsCallback<T> {

		private final TagKey<T> tag;
		private final Consumer<ConitatorTagProvider<T>.FabricTagBuilder> consumer;

		public SimpleGenerateTagsCallback(TagKey<T> tag, Consumer<ConitatorTagProvider<T>.FabricTagBuilder> consumer) {
			this.tag = tag;
			this.consumer = consumer;
		}

		@Override
		public void generate(ConitatorTagProvider<T> tagProvider) {
			this.consumer.accept(tagProvider.getOrCreateTagBuilder(this.tag));
		}

	}

	public static class ConitatorTagProvider<T> extends FabricTagProvider<T> {

		private final TagEntryKey<T> entryKey;

		public ConitatorTagProvider(FabricDataOutput output, RegistryKey<? extends Registry<T>> registryKey,
				CompletableFuture<Provider> registriesFuture, TagEntryKey<T> entryKey) {
			super(output, registryKey, registriesFuture);
			this.entryKey = entryKey;
		}

		@Override
		public FabricTagProvider<T>.FabricTagBuilder getOrCreateTagBuilder(TagKey<T> tag) {
			return super.getOrCreateTagBuilder(tag);
		}

		@Override
		protected void configure(Provider arg) {
			this.entryKey.event.invoker().generate(this);
		}

		@Override
		protected RegistryKey<T> reverseLookup(T element) {
			var key = this.entryKey.getRegistryKey(element);
			return key == null ? super.reverseLookup(element) : key;
		}

	}

	protected Event<GenerateTagsCallback<T>> event = Event.create(GenerateTagsCallback.class,
			callbacks -> builder -> {
				for (GenerateTagsCallback<T> callback : callbacks)
					callback.generate(builder);
			});

	public void registerCallback(GenerateTagsCallback<T> callback) {
		this.event.register(callback);
	}

	public void register(TagKey<T> tag, Consumer<ConitatorTagProvider<T>.FabricTagBuilder> consumer) {
		this.registerCallback(new SimpleGenerateTagsCallback<>(tag, consumer));
	}

	@Override
	public ConitatorTagProvider<T> createProvider(FabricDataOutput output) {
		return null;
	}

	@Override
	public void loadDatagen(Pack pack) {
		pack.addProvider((o, f) -> new ConitatorTagProvider<>(o, this.getRegistryKey(), f, this));
	}

	protected abstract RegistryKey<? extends Registry<T>> getRegistryKey();

	protected abstract RegistryKey<T> getRegistryKey(T instance);

}
