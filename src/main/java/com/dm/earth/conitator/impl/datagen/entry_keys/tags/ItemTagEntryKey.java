package com.dm.earth.conitator.impl.datagen.entry_keys.tags;

import com.dm.earth.conitator.api.DefaultEntryKeys;
import com.dm.earth.conitator.impl.datagen.entry_keys.tags.core.TagEntryKey;

import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ItemTagEntryKey extends TagEntryKey<Item> {

	@Override
	public Identifier getId() {
		return DefaultEntryKeys.ITEM_TAG;
	}

	@Override
	protected RegistryKey<? extends Registry<Item>> getRegistryKey() {
		return RegistryKeys.ITEM;
	}

	@Override
	@SuppressWarnings("deprecation")
	protected RegistryKey<Item> getRegistryKey(Item instance) {
		return instance.getBuiltInRegistryHolder().getRegistryKey();
	}

}
