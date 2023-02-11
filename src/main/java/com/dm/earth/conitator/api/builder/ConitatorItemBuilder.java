package com.dm.earth.conitator.api.builder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.quiltmc.qsl.item.content.registry.api.ItemContentRegistries;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import com.dm.earth.conitator.api.Conitator;
import com.dm.earth.conitator.api.DefaultEntryKeys;
import com.dm.earth.conitator.api.builder.core.RegistrationBuilder;
import com.dm.earth.conitator.impl.client.events.ClientInitCallback;
import com.dm.earth.conitator.impl.datagen.entry_keys.ModelEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.TranslationEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.tags.ItemTagEntryKey;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ConitatorItemBuilder<T extends Item> extends RegistrationBuilder<Item, T> {

	/**
	 * Create a new item builder.
	 *
	 * @param <T>      The type of the item
	 * @param instance The conitator instance
	 * @param id       The id of the item
	 * @param factory  The factory for creating the item instance
	 * @return The item builder
	 */
	public static <T extends Item> ConitatorItemBuilder<T> create(Conitator instance, Identifier id,
			Function<QuiltItemSettings, T> factory) {
		ConitatorItemBuilder<T> obj = new ConitatorItemBuilder<T>(instance, id);
		obj.factory = factory;
		return obj;
	}

	protected QuiltItemSettings settings = new QuiltItemSettings();

	protected Function<QuiltItemSettings, T> factory;

	protected ConitatorItemBuilder(Conitator conitator, Identifier id) {
		super(conitator, id);
	}

	/**
	 * Modify the item settings for the item.
	 *
	 * @param func The item settings processor
	 * @return this
	 */
	public ConitatorItemBuilder<T> settings(Consumer<QuiltItemSettings> func) {
		if (!this.isBuilt())
			func.accept(settings);
		return this;
	}

	/**
	 * Append the item to an {@link ItemGroup}.
	 *
	 * @param group    The item group
	 * @param callback The modify item group callback
	 * @return
	 */
	public ConitatorItemBuilder<T> itemGroup(ItemGroup group, ModifyEntries callback) {
		ItemGroupEvents.modifyEntriesEvent(group).register(callback);
		return this;
	}

	/**
	 * Append the item to an {@link ItemGroup}.
	 *
	 * @param group The item group
	 * @return
	 */
	public ConitatorItemBuilder<T> itemGroup(ItemGroup group) {
		return this.itemGroup(group, entries -> entries.addItem(this.get()));
	}

	/**
	 * Apply a translation to the item. Will be applied to the data generator.
	 *
	 * @param language The language code (ex. en_us)
	 * @param name     The translation
	 * @return this
	 */
	public ConitatorItemBuilder<T> translation(String language, String name) {
		this.conitator.getEntry(DefaultEntryKeys.translationId(language))
				.ifPresent(entry -> ((TranslationEntryKey) entry.getKey())
						.registerCallback(builder -> builder.add(this.get(), name)));
		return this;
	}

	/**
	 * Apply tags to the item, will be applied to the data generator.
	 *
	 * @param tags The tags, as a collection
	 * @return this
	 */
	public ConitatorItemBuilder<T> tag(Collection<TagKey<Item>> tags) {
		this.conitator.getEntry(DefaultEntryKeys.ITEM_TAG).ifPresent(
				entry -> tags.forEach(t -> ((ItemTagEntryKey) entry.getKey()).register(t, e -> e.add(this.get()))));
		return this;
	}

	/**
	 * Apply tags to the item, will be applied to the data generator.
	 *
	 * @param tags The tags, as a id list, will be converted to tag keys
	 * @return this
	 */
	public ConitatorItemBuilder<T> tag(Identifier... tags) {
		return this.tag(Arrays.stream(tags).map(t -> TagKey.of(RegistryKeys.ITEM, t)).toList());
	}

	/**
	 * Apply a tag to the item, will be applied to the data generator.
	 *
	 * @param tag The tag
	 * @return this
	 */
	public ConitatorItemBuilder<T> tag(TagKey<Item> tag) {
		return this.tag(List.of(tag));
	}

	/**
	 * Apply model to the item, will be applied to the data generator.
	 *
	 * @param consumer The consumer
	 * @return this
	 */
	public ConitatorItemBuilder<T> model(BiConsumer<ItemModelGenerator, Item> consumer) {
		this.conitator.getEntry(DefaultEntryKeys.MODEL)
				.ifPresent(entry -> ((ModelEntryKey) entry).registerItemCallback(g -> consumer.accept(g, this.get())));
		return this;
	}

	/**
	 * Apply a {@link ItemColorProvider} to the item.
	 *
	 * @param provider The provider
	 * @return this
	 */
	public <C extends ItemColorProvider> ConitatorItemBuilder<T> color(Supplier<C> provider) {
		ClientInitCallback.registerSafe(() -> ColorProviderRegistry.ITEM.register(provider.get(), this.get()));
		return this;
	}

	/**
	 * Make the item burnable in a furnace through a REA.
	 *
	 * @param time The burn time as ticks
	 * @return this
	 */
	public ConitatorItemBuilder<T> fuel(int time) {
		this.attachREA(ItemContentRegistries.FUEL_TIMES, time);
		return this;
	}

	/**
	 * Make the item compostable in a composter through a REA.
	 *
	 * @param chance The compost chance
	 * @return this
	 */
	public ConitatorItemBuilder<T> compostable(float chance) {
		this.attachREA(ItemContentRegistries.COMPOST_CHANCES, chance);
		return this;
	}

	@Override
	protected T build() {
		T item = factory.apply(this.settings);
		Registry.register(Registries.ITEM, this.id, item);

		this.conitator.getEntry(DefaultEntryKeys.ITEM)
				.ifPresent(entry -> entry.getValue().add(this.id));

		return item;
	}

}
