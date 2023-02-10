package com.dm.earth.conitator.api;

import com.dm.earth.conitator.ConitatorMod;
import com.dm.earth.conitator.api.Conitator.EntryKey;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class DefaultEntryKeys {

	private DefaultEntryKeys() {
	}

	public static final Identifier ITEM = ConitatorMod.id("/common/registry/items");
	public static final Identifier BLOCK = ConitatorMod.id("/common/registry/blocks");
	public static final Identifier FLUID = ConitatorMod.id("/common/registry/fluids");

	public static final Identifier ITEM_TAG = ConitatorMod.id("/datagen/tags/items");
	public static final Identifier BLOCK_TAG = ConitatorMod.id("/datagen/tags/blocks");
	public static final Identifier FLUID_TAG = ConitatorMod.id("/datagen/tags/fluids");

	public static Identifier translationId(String language) {
		return ConitatorMod.id("/datagen/translation/" + language);
	}

	public static EntryKey<Item> item() {
		return EntryKey.ofRegistry(ITEM, obj -> obj instanceof Item, Registries.ITEM);
	}

	public static EntryKey<Block> block() {
		return EntryKey.ofRegistry(BLOCK, obj -> obj instanceof Block, Registries.BLOCK);
	}

}
