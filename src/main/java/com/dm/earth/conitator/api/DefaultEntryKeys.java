package com.dm.earth.conitator.api;

import com.dm.earth.conitator.ConitatorMod;
import com.dm.earth.conitator.api.Conitator.EntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.BlockLootTableEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.ModelEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.TranslationEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.tags.BlockTagEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.tags.FluidTagEntryKey;
import com.dm.earth.conitator.impl.datagen.entry_keys.tags.ItemTagEntryKey;

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

	// Datagen
	public static final Identifier ITEM_TAG = ConitatorMod.id("/datagen/tags/items");
	public static final Identifier BLOCK_TAG = ConitatorMod.id("/datagen/tags/blocks");
	public static final Identifier FLUID_TAG = ConitatorMod.id("/datagen/tags/fluids");

	public static final Identifier MODEL = ConitatorMod.id("/datagen/models");
	public static final Identifier BLOCK_LOOT_TABLE = ConitatorMod.id("/datagen/loot_tables");

	public static Identifier translationId(String language) {
		return ConitatorMod.id("/datagen/translation/" + language);
	}

	public static EntryKey<Item> item() {
		return EntryKey.ofRegistry(ITEM, obj -> obj instanceof Item, Registries.ITEM);
	}

	public static EntryKey<Block> block() {
		return EntryKey.ofRegistry(BLOCK, obj -> obj instanceof Block, Registries.BLOCK);
	}

	public static ItemTagEntryKey itemTag() {
		return new ItemTagEntryKey();
	}

	public static BlockTagEntryKey blockTag() {
		return new BlockTagEntryKey();
	}

	public static FluidTagEntryKey fluidTag() {
		return new FluidTagEntryKey();
	}

	public static TranslationEntryKey translation(String language) {
		return TranslationEntryKey.create(language);
	}

	public static ModelEntryKey model() {
		return new ModelEntryKey();
	}

	public static BlockLootTableEntryKey blockLootTable() {
		return new BlockLootTableEntryKey();
	}

}
