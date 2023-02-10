package com.dm.earth.conitator.impl.client.events;

import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;
import org.quiltmc.qsl.base.api.event.Event;

import net.fabricmc.api.EnvType;

@FunctionalInterface
public interface ClientInitCallback {

	void load();

	@ClientOnly
	Event<ClientInitCallback> EVENT = Event.create(ClientInitCallback.class, callbacks -> () -> {
		for (ClientInitCallback callback : callbacks)
			callback.load();
	});

	static void registerSafe(ClientInitCallback callback) {
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT)
			EVENT.register(callback);
	}

}
