package com.dm.earth.conitator.api.builder;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

import com.dm.earth.conitator.api.Conitator;
import com.dm.earth.conitator.api.DefaultEntryKeys;
import com.dm.earth.conitator.api.builder.core.RegistrationBuilder;
import com.dm.earth.conitator.impl.client.events.ClientInitCallback;
import com.dm.earth.conitator.impl.datagen.entry_keys.BlockLootTableEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.ModelEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.TranslationEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.tags.BlockTagEntryKey;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ConitatorBlockBuilder<T extends Block> extends RegistrationBuilder<T> {

	@FunctionalInterface
	public static interface BlockItemModelBuildConsumer {

		void accept(BlockStateModelGenerator generator, Item item, Block block);

	}

	@FunctionalInterface
	public static interface BlockItemLootTableBuildConsumer {

		void accept(BiConsumer<Identifier, LootTable.Builder> consumer, Item item, Block block);

	}

	public static <T extends Block> ConitatorBlockBuilder<T> create(Conitator instance, Identifier id,
			Function<QuiltBlockSettings, T> factory) {
		ConitatorBlockBuilder<T> obj = new ConitatorBlockBuilder<T>(instance, id);
		obj.factory = factory;
		return obj;
	}

	private QuiltBlockSettings settings = QuiltBlockSettings.copyOf(Blocks.AIR);
	private Function<QuiltBlockSettings, T> factory;
	private Supplier<RenderLayer> renderLayer = null;

	private ConitatorItemBuilder<? extends BlockItem> item = null;

	protected ConitatorBlockBuilder(Conitator conitator, Identifier id) {
		super(conitator, id);
	}

	public ConitatorBlockBuilder<T> settings(QuiltBlockSettings settings) {
		this.settings = settings;
		return this;
	}

	public ConitatorBlockBuilder<T> renderLayer(Supplier<RenderLayer> layer) {
		this.renderLayer = layer;
		return this;
	}

	public ConitatorBlockBuilder<T> translation(String language, String name) {
		this.conitator.getEntry(DefaultEntryKeys.translationId(language))
				.ifPresent(entry -> ((TranslationEntryKey) entry.getKey())
						.registerCallback(builder -> builder.add(this.get(), name)));
		return this;
	}

	public ConitatorBlockBuilder<T> tag(Collection<TagKey<Block>> tags) {
		this.conitator.getEntry(DefaultEntryKeys.BLOCK_TAG).ifPresent(
				entry -> tags.forEach(t -> ((BlockTagEntryKey) entry.getKey()).register(t, e -> e.add(this.get()))));
		return this;
	}

	public ConitatorBlockBuilder<T> tag(Identifier... tags) {
		return this.tag(Arrays.stream(tags).map(t -> TagKey.of(RegistryKeys.BLOCK, t)).toList());
	}

	public ConitatorBlockBuilder<T> tagBoth(Collection<Identifier> tags) {
		this.item.tag(tags.toArray(new Identifier[0]));
		return this.tag(tags.toArray(new Identifier[0]));
	}

	public ConitatorBlockBuilder<T> tagBoth(Identifier... tags) {
		this.item.tag(tags);
		return this.tag(tags);
	}

	public ConitatorBlockBuilder<T> model(BiConsumer<BlockStateModelGenerator, Block> consumer) {
		this.conitator.getEntry(DefaultEntryKeys.MODEL)
				.ifPresent(entry -> ((ModelEntryKey) entry)
						.registerBlockStateCallback(g -> consumer.accept(g, this.get())));
		return this;
	}

	public ConitatorBlockBuilder<T> model(BlockItemModelBuildConsumer consumer) {
		this.conitator.getEntry(DefaultEntryKeys.MODEL)
				.ifPresent(entry -> ((ModelEntryKey) entry)
						.registerBlockStateCallback(g -> consumer.accept(g, this.item.get(), this.get())));
		return this;
	}

	public ConitatorBlockBuilder<T> loot(BiConsumer<BiConsumer<Identifier, LootTable.Builder>, Block> consumer) {
		this.conitator.getEntry(DefaultEntryKeys.BLOCK_LOOT_TABLE)
				.ifPresent(entry -> ((BlockLootTableEntryKey) entry)
						.registerCallback(g -> consumer.accept(g, this.get())));
		return this;
	}

	public ConitatorBlockBuilder<T> loot(BlockItemLootTableBuildConsumer consumer) {
		this.conitator.getEntry(DefaultEntryKeys.BLOCK_LOOT_TABLE)
				.ifPresent(entry -> ((BlockLootTableEntryKey) entry)
						.registerCallback(g -> consumer.accept(g, this.item.get(), this.get())));
		return this;
	}

	public <V extends BlockItem> ConitatorBlockBuilder<T> item(ConitatorItemBuilder<V> item) {
		this.item = item;
		return this;
	}

	public ConitatorBlockBuilder<T> item(@Nullable Consumer<ConitatorItemBuilder<BlockItem>> consumer) {
		ConitatorItemBuilder<BlockItem> builder = ConitatorItemBuilder.create(this.conitator, this.id,
				bs -> new BlockItem(this.get(), bs));
		if (consumer != null)
			consumer.accept(builder);
		this.item(builder);
		return this;
	}

	@Override
	protected T build() {
		T block = factory.apply(settings);
		Registry.register(Registries.BLOCK, this.id, block);
		this.item.get();

		this.conitator.getEntry(DefaultEntryKeys.BLOCK)
				.ifPresent(entry -> entry.getValue().add(this.id));
		ClientInitCallback.registerSafe(() -> BlockRenderLayerMap.put(this.renderLayer.get(), block));
		return block;
	}

}
