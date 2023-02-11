package com.dm.earth.conitator;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import com.dm.earth.conitator.test.ConitatorTestMod;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.util.Identifier;

@Internal
public class ConitatorMod implements ModInitializer, DataGeneratorEntrypoint {

	public static final String MOD_ID = "conitator";
	public static final boolean DEBUG = true;

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		if (DEBUG)
			ConitatorTestMod.onInitializeDataGenerator(fabricDataGenerator);
	}

	@Override
	public void onInitialize(ModContainer mod) {
		if (DEBUG)
			ConitatorTestMod.onInitialize(mod);
	}

}
