package com.dm.earth.conitator.test;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

import com.dm.earth.conitator.ConitatorMod;
import com.dm.earth.conitator.api.Conitator;
import com.dm.earth.conitator.api.DefaultEntryKeys;
import com.dm.earth.conitator.api.builder.ConitatorBlockBuilder;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.tag.BlockTags;

public class ConitatorTestMod implements ModInitializer, DataGeneratorEntrypoint {

	public static final boolean DEBUG = true;
	public static Conitator conitator = Conitator.create().apply(DefaultEntryKeys.block(), DefaultEntryKeys.item(),
			DefaultEntryKeys.blockTag(), DefaultEntryKeys.blockLootTable(), DefaultEntryKeys.model(),
			DefaultEntryKeys.translation("en_us"), DefaultEntryKeys.itemTag());

	public static final Block BLOCK = ConitatorBlockBuilder
			.create(conitator, ConitatorMod.id("test_slab"), Block::new)
			.item(ib -> ib.settings(s -> s.maxCount(16)).itemGroup(ItemGroups.BUILDING_BLOCKS).fuel(20)).flammable(1, 1)
			.translation("en_us", "Based Slab").translation("zh_cn", "FROGESUCKS")
			.settings(QuiltBlockSettings.of(Material.CACTUS)).tag(BlockTags.AXE_MINEABLE)
			.model((g, i, b) -> g.registerSimpleCubeAll(b))
			.get();

	@Override
	public void onInitialize(ModContainer mod) {

	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		conitator.loadDatagen(fabricDataGenerator);
	}

}
