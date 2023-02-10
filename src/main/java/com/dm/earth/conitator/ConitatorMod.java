package com.dm.earth.conitator;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.util.Identifier;

@Internal
public class ConitatorMod {

	public static final String MOD_ID = "conitator";

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}

}
