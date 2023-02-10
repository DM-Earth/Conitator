package com.dm.earth.conitator.impl.datagen.entry_keys.tags;

import com.dm.earth.conitator.api.DefaultEntryKeys;
import com.dm.earth.conitator.impl.datagen.entry_keys.tags.core.TagEntryKey;

import net.minecraft.block.Block;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class BlockTagEntryKey extends TagEntryKey<Block> {

	@Override
	public Identifier getId() {
		return DefaultEntryKeys.BLOCK_TAG;
	}

	@Override
	protected RegistryKey<? extends Registry<Block>> getRegistryKey() {
		return RegistryKeys.BLOCK;
	}

	@Override
	@SuppressWarnings("deprecation")
	protected RegistryKey<Block> getRegistryKey(Block instance) {
		return instance.getBuiltInRegistryHolder().getRegistryKey();
	}

}
