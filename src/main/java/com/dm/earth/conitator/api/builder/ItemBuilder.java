package com.dm.earth.conitator.api.builder;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import com.dm.earth.conitator.api.Conitator;
import com.dm.earth.conitator.api.DefaultEntryKeys;
import com.dm.earth.conitator.api.builder.core.RegistrationBuilder;
import com.dm.earth.conitator.impl.datagen.entry_keys.TranslationEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.tags.ItemTagEntryKey;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ItemBuilder<T extends Item> extends RegistrationBuilder<T> {

	public static <T extends Item> ItemBuilder<T> create(Conitator instance, Identifier id,
			Function<QuiltItemSettings, T> factory) {
		ItemBuilder<T> obj = new ItemBuilder<T>(instance, id);
		obj.factory = factory;
		return obj;
	}

	protected QuiltItemSettings settings = new QuiltItemSettings();

	protected Function<QuiltItemSettings, T> factory;

	protected ItemBuilder(Conitator conitator, Identifier id) {
		super(conitator, id);
	}

	public ItemBuilder<T> settings(Consumer<QuiltItemSettings> func) {
		if (!this.isBuilt())
			func.accept(settings);
		return this;
	}

	public ItemBuilder<T> itemGroup(ItemGroup group, ModifyEntries callback) {
		ItemGroupEvents.modifyEntriesEvent(group).register(callback);
		return this;
	}

	public ItemBuilder<T> itemGroup(ItemGroup group) {
		return this.itemGroup(group, entries -> entries.addItem(this.get()));
	}

	public ItemBuilder<T> translation(String language, String name) {
		this.conitator.getEntry(DefaultEntryKeys.translationId(language))
				.ifPresent(entry -> ((TranslationEntryKey) entry.getKey())
						.registerCallback(builder -> builder.add(this.get(), name)));
		return this;
	}

	public ItemBuilder<T> tag(Collection<TagKey<Item>> tags) {
		this.conitator.getEntry(DefaultEntryKeys.ITEM_TAG).ifPresent(
				entry -> tags.forEach(t -> ((ItemTagEntryKey) entry.getKey()).register(t, e -> e.add(this.get()))));
		return this;
	}

	public ItemBuilder<T> tag(Identifier... tags) {
		return this.tag(Arrays.stream(tags).map(t -> TagKey.of(RegistryKeys.ITEM, t)).toList());
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
