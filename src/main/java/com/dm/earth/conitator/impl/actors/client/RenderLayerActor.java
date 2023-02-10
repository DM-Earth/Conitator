package com.dm.earth.conitator.impl.actors.client;

import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;
import com.dm.earth.conitator.api.Conitator;
import com.dm.earth.conitator.api.DefaultActors;
import com.dm.earth.conitator.api.DefaultEntryKeys;
import com.dm.earth.conitator.impl.entry_keys.client.RenderLayerEntryKey;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class RenderLayerActor implements Conitator.Actor<Void> {

	@Override
	public Void act(Conitator instance, Void object) {
		instance.getEntry(DefaultEntryKeys.RENDER_LAYER).ifPresent(entry -> {
			if (entry.getKey() instanceof RenderLayerEntryKey k) {
				entry.getValue().forEach(id -> {
					k.get(instance, id).ifPresent(l -> BlockRenderLayerMap.put(l.get(), Registries.BLOCK.get(id)));
				});
			}
		});
		return object;
	}

	@Override
	public Identifier getId() {
		return DefaultActors.RENDER_LAYER;
	}

}
