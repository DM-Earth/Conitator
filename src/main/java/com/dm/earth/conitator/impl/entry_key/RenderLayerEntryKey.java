package com.dm.earth.conitator.impl.entry_key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;
import com.dm.earth.conitator.api.Conitator;
import com.dm.earth.conitator.api.DefaultEntryKeys;
import net.fabricmc.api.EnvType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

@Internal
public class RenderLayerEntryKey implements Conitator.EntryKey<Supplier<RenderLayer>> {

	protected HashMap<Supplier<RenderLayer>, ArrayList<Identifier>> map = new HashMap<>();

	@Override
	public boolean test(Object arg0) {
		return arg0 != null && arg0 instanceof Supplier<?> supplier
				&& MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT
				&& supplier.get() instanceof RenderLayer;
	}

	@Override
	public Supplier<RenderLayer> get(Conitator instance, Identifier arg0) {
		return map.entrySet().stream().filter(entry -> entry.getValue().contains(arg0))
				.map(Map.Entry::getKey).findFirst().orElse(null);
	}

	@Override
	public Identifier getId() {
		return DefaultEntryKeys.RENDER_LAYER;
	}

	public void put(Supplier<RenderLayer> layer, Identifier id) {
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT && layer != null)
			map.entrySet().stream().filter(s -> s.getKey().get().equals(layer.get())).findFirst()
					.ifPresentOrElse(entry -> entry.getValue().add(id), () -> {
						ArrayList<Identifier> l = new ArrayList<>();
						l.add(id);
						map.put(layer, l);
					});
	}

}
