package com.dm.earth.conitator.api;

import java.util.function.Supplier;
import com.dm.earth.conitator.ConitatorMod;
import com.dm.earth.conitator.api.Conitator.EntryKey;
import com.dm.earth.conitator.impl.entry_key.RenderLayerEntryKey;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class DefaultEntryKeys {
	private DefaultEntryKeys() {}

	public static final Identifier ITEM = ConitatorMod.id("items");
	public static final Identifier BLOCK = ConitatorMod.id("blocks");
	public static final Identifier RENDER_LAYER = ConitatorMod.id("render_layers");

	public static EntryKey<Item> item() {
		return EntryKey.ofRegistry(ITEM, obj -> obj != null && obj instanceof Item,
				Registries.ITEM);
	}

	public static EntryKey<Block> block() {
		return EntryKey.ofRegistry(BLOCK, obj -> obj != null && obj instanceof Block,
				Registries.BLOCK);
	}

	public static EntryKey<Supplier<RenderLayer>> renderLayer() {
		return new RenderLayerEntryKey();
	}

}
