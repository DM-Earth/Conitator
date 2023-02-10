package com.dm.earth.conitator.impl.datagen.entry_keys;

import org.quiltmc.qsl.base.api.event.Event;

import com.dm.earth.conitator.api.DefaultEntryKeys;
import com.dm.earth.conitator.impl.datagen.entry_keys.core.DatagenEntryKey;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.util.Identifier;

public class ModelEntryKey extends DatagenEntryKey<FabricModelProvider> {

	@FunctionalInterface
	public static interface GenerateBlockStateModelCallback {

		void generate(BlockStateModelGenerator generator);

	}

	@FunctionalInterface
	public static interface GenerateItemModelCallback {

		void generate(ItemModelGenerator generator);

	}

	private static class ConitatorModelProvider extends FabricModelProvider {

		private final ModelEntryKey entryKey;

		public ConitatorModelProvider(FabricDataOutput output, ModelEntryKey entryKey) {
			super(output);
			this.entryKey = entryKey;
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
			this.entryKey.eventBlockState.invoker().generate(blockStateModelGenerator);
		}

		@Override
		public void generateItemModels(ItemModelGenerator itemModelGenerator) {
			this.entryKey.eventItem.invoker().generate(itemModelGenerator);
		}

	}

	protected Event<GenerateBlockStateModelCallback> eventBlockState = Event.create(
			GenerateBlockStateModelCallback.class,
			callbacks -> builder -> {
				for (GenerateBlockStateModelCallback callback : callbacks)
					callback.generate(builder);
			});

	protected Event<GenerateItemModelCallback> eventItem = Event.create(
			GenerateItemModelCallback.class,
			callbacks -> builder -> {
				for (GenerateItemModelCallback callback : callbacks)
					callback.generate(builder);
			});

	@Override
	public Identifier getId() {
		return DefaultEntryKeys.MODEL;
	}

	@Override
	public FabricModelProvider createProvider(FabricDataOutput output) {
		return new ConitatorModelProvider(output, this);
	}

	public void registerBlockStateCallback(GenerateBlockStateModelCallback callback) {
		this.eventBlockState.register(callback);
	}

	public void registerItemCallback(GenerateItemModelCallback callback) {
		this.eventItem.register(callback);
	}

}
