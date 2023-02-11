package com.dm.earth.conitator.api.builder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.FlammableBlockEntry;
import org.quiltmc.qsl.block.content.registry.api.ReversibleBlockEntry;
import org.quiltmc.qsl.block.content.registry.api.enchanting.ConstantBooster;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBooster;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

import com.dm.earth.conitator.api.Conitator;
import com.dm.earth.conitator.api.DefaultEntryKeys;
import com.dm.earth.conitator.api.builder.core.RegistrationBuilder;
import com.dm.earth.conitator.impl.client.color.SimpleColorProvider;
import com.dm.earth.conitator.impl.client.events.ClientInitCallback;
import com.dm.earth.conitator.impl.datagen.entry_keys.BlockLootTableEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.ModelEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.TranslationEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.tags.BlockTagEntryKey;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColorProvider;
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

public class ConitatorBlockBuilder<T extends Block> extends RegistrationBuilder<Block, T> {

	@FunctionalInterface
	public static interface BlockItemModelBuildConsumer {

		void accept(BlockStateModelGenerator generator, Item item, Block block);

	}

	@FunctionalInterface
	public static interface BlockItemLootTableBuildConsumer {

		void accept(BiConsumer<Identifier, LootTable.Builder> consumer, Item item, Block block);

	}

	/**
	 * Create a new block builder.
	 *
	 * @param <T>      The type of block
	 * @param instance The conitator
	 * @param id       The id of the block
	 * @param factory  The factory for creating the block instance
	 * @return The block builder
	 */
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

	/**
	 * Apply settings to the block, and it will be applied when building the block
	 * object.
	 *
	 * @param settings The block settings for the block
	 * @return this
	 */
	public ConitatorBlockBuilder<T> settings(QuiltBlockSettings settings) {
		this.settings = settings;
		return this;
	}

	/**
	 * Apply a render layer to the block. Due to server-side safety, you need to
	 * provide a supplier of the render layer.
	 *
	 * @param layer The render layer supplier
	 * @return this
	 */
	public ConitatorBlockBuilder<T> renderLayer(Supplier<RenderLayer> layer) {
		this.renderLayer = layer;
		return this;
	}

	/**
	 * Apply a translation to the block. Will be applied to the data generator.
	 *
	 * @param language The language code (ex. en_us)
	 * @param name     The translation
	 * @return this
	 */
	public ConitatorBlockBuilder<T> translation(String language, String name) {
		this.conitator.getEntry(DefaultEntryKeys.translationId(language))
				.ifPresent(entry -> ((TranslationEntryKey) entry.getKey())
						.registerCallback(builder -> builder.add(this.get(), name)));
		return this;
	}

	/**
	 * Apply tags to the block, will be applied to the data generator.
	 *
	 * @param tags The tags, as a collection
	 * @return this
	 */
	public ConitatorBlockBuilder<T> tag(Collection<TagKey<Block>> tags) {
		this.conitator.getEntry(DefaultEntryKeys.BLOCK_TAG).ifPresent(
				entry -> tags.forEach(t -> ((BlockTagEntryKey) entry.getKey()).register(t, e -> e.add(this.get()))));
		return this;
	}

	/**
	 * Apply tags to the block, will be applied to the data generator.
	 *
	 * @param tags The tags, as a id list, will be converted to tag keys
	 * @return this
	 */
	public ConitatorBlockBuilder<T> tag(Identifier... tags) {
		return this.tag(Arrays.stream(tags).map(t -> TagKey.of(RegistryKeys.BLOCK, t)).toList());
	}

	/**
	 * Apply a tag to the block, will be applied to the data generator.
	 *
	 * @param tags The tag
	 * @return this
	 */
	public ConitatorBlockBuilder<T> tag(TagKey<Block> tag) {
		return this.tag(List.of(tag));
	}

	/**
	 * Apply tags to the block and the item, will be applied to the data generator.
	 *
	 * @param tags The tags, as a collection of id, will be converted to tag keys
	 * @return this
	 */
	public ConitatorBlockBuilder<T> tagBoth(Collection<Identifier> tags) {
		safeExecuteItem(i -> i.tag(tags.toArray(new Identifier[0])));
		return this.tag(tags.toArray(new Identifier[0]));
	}

	/**
	 * Apply tags to the block and this item, will be applied to the data generator.
	 *
	 * @param tags The tags, as a id list, will be converted to tag keys
	 * @return this
	 */
	public ConitatorBlockBuilder<T> tagBoth(Identifier... tags) {
		safeExecuteItem(i -> i.tag(tags));
		return this.tag(tags);
	}

	/**
	 * Apply model to the block.
	 *
	 * @param consumer The consumer
	 * @return this
	 */
	public ConitatorBlockBuilder<T> model(BiConsumer<BlockStateModelGenerator, Block> consumer) {
		this.conitator.getEntry(DefaultEntryKeys.MODEL)
				.ifPresent(entry -> ((ModelEntryKey) entry)
						.registerBlockStateCallback(g -> consumer.accept(g, this.get())));
		return this;
	}

	/**
	 * Apply model to the block and the item.
	 *
	 * @param consumer The consumer
	 * @return this
	 */
	public ConitatorBlockBuilder<T> model(BlockItemModelBuildConsumer consumer) {
		safeExecuteItem(i -> this.conitator.getEntry(DefaultEntryKeys.MODEL)
				.ifPresent(entry -> ((ModelEntryKey) entry)
						.registerBlockStateCallback(g -> consumer.accept(g, i.get(), this.get()))));
		return this;
	}

	/**
	 * Apply loot table to the block.
	 *
	 * @param consumer The consumer
	 * @return this
	 */
	public ConitatorBlockBuilder<T> loot(BiConsumer<BiConsumer<Identifier, LootTable.Builder>, Block> consumer) {
		this.conitator.getEntry(DefaultEntryKeys.BLOCK_LOOT_TABLE)
				.ifPresent(entry -> ((BlockLootTableEntryKey) entry)
						.registerCallback(g -> consumer.accept(g, this.get())));
		return this;
	}

	/**
	 * Apply loot table to the block with the provided item.
	 *
	 * @param consumer The consumer
	 * @return this
	 */
	public ConitatorBlockBuilder<T> loot(BlockItemLootTableBuildConsumer consumer) {
		safeExecuteItem(i -> this.conitator.getEntry(DefaultEntryKeys.BLOCK_LOOT_TABLE)
				.ifPresent(entry -> ((BlockLootTableEntryKey) entry)
						.registerCallback(g -> consumer.accept(g, i.get(), this.get()))));
		return this;
	}

	/**
	 * Create a {@link BlockItem} for this block.
	 *
	 * @param <V>  The type of block item
	 * @param item The item builder, will be built when building this block
	 * @return this
	 */
	public <V extends BlockItem> ConitatorBlockBuilder<T> customItem(
			Function<ConitatorBlockBuilder<T>, ConitatorItemBuilder<V>> item) {
		this.item = item.apply(this);
		return this;
	}

	/**
	 * Create a {@link BlockItem} for this block.
	 *
	 * @param <V>      The type of block item
	 * @param consumer The item builder, will be built when building this block
	 * @return this
	 */
	public ConitatorBlockBuilder<T> item(@Nullable Consumer<ConitatorItemBuilder<BlockItem>> consumer) {
		ConitatorItemBuilder<BlockItem> builder = ConitatorItemBuilder.create(this.conitator, this.id,
				bs -> new BlockItem(this.get(), bs));
		if (consumer != null)
			consumer.accept(builder);
		this.customItem(b -> builder);
		return this;
	}

	/**
	 * Apply a {@link BlockColorProvider} for the block.
	 *
	 * @param provider The color provider
	 * @return this
	 */
	public <C extends BlockColorProvider> ConitatorBlockBuilder<T> color(Supplier<C> provider) {
		ClientInitCallback.registerSafe(() -> ColorProviderRegistry.BLOCK.register(provider.get(), this.get()));
		return this;
	}

	/**
	 * Apply a color provider for both the item and the block.
	 *
	 * @param provider The color provider
	 * @return this
	 */
	public <C extends SimpleColorProvider> ConitatorBlockBuilder<T> colorBoth(Supplier<C> provider) {
		safeExecuteItem(i -> i.color(provider));
		return this.color(provider);
	}

	/**
	 * Make the block flattenable by flattening with a shovel through a REA.
	 *
	 * @param flattened The flattened block state
	 * @return this
	 */
	public ConitatorBlockBuilder<T> flattenable(BlockState flattened) {
		this.attachREA(BlockContentRegistries.FLATTENABLE, flattened);
		return this;
	}

	/**
	 * Make the block oxidizable through a REA.
	 *
	 * @param entry The entry
	 * @return this
	 */
	public ConitatorBlockBuilder<T> oxidizable(ReversibleBlockEntry entry) {
		this.attachREA(BlockContentRegistries.OXIDIZABLE, entry);
		return this;
	}

	/**
	 * Make the block oxidizable through a REA.
	 *
	 * @param block      The oxidized block
	 * @param reversible If the process is reversible
	 * @return this
	 */
	public ConitatorBlockBuilder<T> oxidizable(Block block, boolean reversible) {
		return this.oxidizable(new ReversibleBlockEntry(block, reversible));
	}

	/**
	 * Make the block waxable through a REA.
	 *
	 * @param entry The entry
	 * @return this
	 */
	public ConitatorBlockBuilder<T> waxable(ReversibleBlockEntry entry) {
		this.attachREA(BlockContentRegistries.WAXABLE, entry);
		return this;
	}

	/**
	 * Make the block waxable through a REA.
	 *
	 * @param block      The waxed block
	 * @param reversible If the process is reversible
	 * @return this
	 */
	public ConitatorBlockBuilder<T> waxable(Block block, boolean reversible) {
		return this.waxable(new ReversibleBlockEntry(block, reversible));
	}

	/**
	 * Make the block strippable via an axe through a REA.
	 *
	 * @param block The stripped block, should contain AXIS property
	 * @return this
	 */
	public ConitatorBlockBuilder<T> strippable(Block block) {
		this.attachREA(BlockContentRegistries.STRIPPABLE, block);
		return this;
	}

	/**
	 * Make the block flammable through a REA.
	 *
	 * @param entry The entry
	 * @return this
	 */
	public ConitatorBlockBuilder<T> flammable(FlammableBlockEntry entry) {
		this.attachREA(BlockContentRegistries.FLAMMABLE, entry);
		return this;
	}

	/**
	 * Make the block flammable through a REA.
	 *
	 * @return this
	 */
	public ConitatorBlockBuilder<T> flammable(int burn, int spread) {
		return this.flammable(new FlammableBlockEntry(burn, spread));
	}

	/**
	 * Make the block able to provide enchanting levels for a enchanting table
	 * through a REA.
	 *
	 * @param booster The {@link EnchantingBooster}
	 * @return this
	 */
	public ConitatorBlockBuilder<T> enchantingBoost(EnchantingBooster booster) {
		this.attachREA(BlockContentRegistries.ENCHANTING_BOOSTERS, booster);
		return this;
	}

	/**
	 * Make the block able to provide enchanting levels for a enchanting table
	 * through a REA.
	 *
	 * @param amount The amount of levels
	 * @return this
	 */
	public ConitatorBlockBuilder<T> enchantingBoost(float amount) {
		return this.enchantingBoost(new ConstantBooster(amount));
	}

	protected void safeExecuteItem(Consumer<ConitatorItemBuilder<? extends BlockItem>> consumer) {
		if (this.item != null)
			consumer.accept(this.item);
	}

	@Override
	protected T build() {
		T block = factory.apply(settings);
		Registry.register(Registries.BLOCK, this.id, block);
		safeExecuteItem(i -> i.get());

		this.conitator.getEntry(DefaultEntryKeys.BLOCK)
				.ifPresent(entry -> entry.getValue().add(this.id));
		ClientInitCallback.registerSafe(() -> BlockRenderLayerMap.put(this.renderLayer.get(), block));
		return block;
	}

}
