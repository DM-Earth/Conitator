package com.dm.earth.conitator.impl.datagen.entry_keys;

import java.util.function.BiConsumer;

import org.quiltmc.qsl.base.api.event.Event;

import com.dm.earth.conitator.api.DefaultEntryKeys;
import com.dm.earth.conitator.impl.datagen.entry_keys.core.DatagenEntryKey;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;

public class BlockLootTableEntryKey extends DatagenEntryKey<FabricLootTableProvider> {

	@FunctionalInterface
	public static interface GenerateBlockLootTableCallback {

		void generate(BiConsumer<Identifier, Builder> arg0);

	}

	private static class ConitatorBlockLootTableProvider extends SimpleFabricLootTableProvider {

		private final BlockLootTableEntryKey entryKey;

		public ConitatorBlockLootTableProvider(FabricDataOutput output, BlockLootTableEntryKey entryKey) {
			super(output, LootContextTypes.BLOCK);
			this.entryKey = entryKey;
		}

		@Override
		public void accept(BiConsumer<Identifier, Builder> arg0) {
			this.entryKey.event.invoker().generate(arg0);
		}

	}

	protected Event<GenerateBlockLootTableCallback> event = Event.create(
			GenerateBlockLootTableCallback.class,
			callbacks -> consumer -> {
				for (GenerateBlockLootTableCallback callback : callbacks)
					callback.generate(consumer);
			});

	@Override
	public Identifier getId() {
		return DefaultEntryKeys.BLOCK_LOOT_TABLE;
	}

	public void registerCallback(GenerateBlockLootTableCallback callback) {
		this.event.register(callback);
	}

	@Override
	public FabricLootTableProvider createProvider(FabricDataOutput output) {
		return new ConitatorBlockLootTableProvider(output, this);
	}

}
