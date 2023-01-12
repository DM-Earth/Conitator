package com.dm.earth.conitator.builder;

import java.util.function.Function;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import com.dm.earth.conitator.api.Conitator;
import com.dm.earth.conitator.api.DefaultEntryKeys;
import com.dm.earth.conitator.builder.core.RegistrationBuilder;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemBuilder<T extends Item> extends RegistrationBuilder<T> {

	private QuiltItemSettings settings = new QuiltItemSettings();
	private Function<QuiltItemSettings, T> factory;

	protected ItemBuilder(Conitator conitator, Identifier id) {
		super(conitator, id);
	}

	public static <T extends Item> ItemBuilder<T> create(Conitator instance, Identifier id,
			Function<QuiltItemSettings, T> factory) {
		ItemBuilder<T> obj = new ItemBuilder<T>(instance, id);
		obj.factory = factory;
		return obj;
	}

	@Override
	protected T build() {
		T item = factory.apply(this.settings);
		Registry.register(Registries.ITEM, this.id, item);

		this.conitator.getEntry(DefaultEntryKeys.ITEM)
				.ifPresent(entry -> entry.getValue().add(this.id));

		return item;
	}

	public ItemBuilder<T> settings(Function<QuiltItemSettings, QuiltItemSettings> func) {
		if (!this.isBuilded())
			this.settings = func.apply(settings);
		return this;
	}

}
