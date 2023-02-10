package com.dm.earth.conitator.api.builder;

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
import com.dm.earth.conitator.impl.datagen.entry_keys.TranslationEntryKey;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockBuilder<T extends Block> extends RegistrationBuilder<T> {

	public static <T extends Block> BlockBuilder<T> create(Conitator instance, Identifier id,
			Function<QuiltBlockSettings, T> factory) {
		BlockBuilder<T> obj = new BlockBuilder<T>(instance, id);
		obj.factory = factory;
		return obj;
	}

	private QuiltBlockSettings settings = QuiltBlockSettings.copyOf(Blocks.AIR);
	private Function<QuiltBlockSettings, T> factory;
	private Supplier<RenderLayer> renderLayer = null;
	private ItemBuilder<? extends BlockItem> item = null;

	protected BlockBuilder(Conitator conitator, Identifier id) {
		super(conitator, id);
	}

	public BlockBuilder<T> settings(QuiltBlockSettings settings) {
		this.settings = settings;
		return this;
	}

	public BlockBuilder<T> renderLayer(Supplier<RenderLayer> layer) {
		this.renderLayer = layer;
		return this;
	}

	public BlockBuilder<T> translation(String language, String name) {
		this.conitator.getEntry(DefaultEntryKeys.translationId(language))
				.ifPresent(entry -> ((TranslationEntryKey) entry.getKey())
						.registerCallback(builder -> builder.add(this.get(), name)));
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

	public <V extends BlockItem> BlockBuilder<T> withItem(ItemBuilder<V> item) {
		this.item = item;
		return this;
	}

	public BlockBuilder<T> withItem(@Nullable Consumer<ItemBuilder<BlockItem>> consumer) {
		ItemBuilder<BlockItem> builder = ItemBuilder.create(this.conitator, this.id,
				bs -> new BlockItem(this.get(), bs));
		if (consumer != null)
			consumer.accept(builder);
		this.withItem(builder);
		return this;
	}

}
