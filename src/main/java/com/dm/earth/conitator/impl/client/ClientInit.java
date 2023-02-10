package com.dm.earth.conitator.impl.client;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import com.dm.earth.conitator.impl.client.events.ClientInitCallback;

@ClientOnly
public class ClientInit implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		ClientInitCallback.EVENT.invoker().load();
	}

}
