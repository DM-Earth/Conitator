package com.dm.earth.conitator.impl.datagen.entry_keys;

import org.quiltmc.qsl.base.api.event.Event;

import com.dm.earth.conitator.api.DefaultEntryKeys;
import com.dm.earth.conitator.impl.datagen.entry_keys.core.DatagenEntryKey;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder;
import net.minecraft.util.Identifier;

public class TranslationEntryKey extends DatagenEntryKey<FabricLanguageProvider> {

	@FunctionalInterface
	public static interface GenerateTranslationCallback {

		void generate(TranslationBuilder translationBuilder);

	}

	private static class ConitatorLanguageProvider extends FabricLanguageProvider {

		private final TranslationEntryKey entryKey;

		public ConitatorLanguageProvider(FabricDataOutput dataOutput, TranslationEntryKey entryKey) {
			super(dataOutput, entryKey.getLanguage());
			this.entryKey = entryKey;
		}

		@Override
		public void generateTranslations(TranslationBuilder translationBuilder) {
			this.entryKey.event.invoker().generate(translationBuilder);
		}

	}

	public static TranslationEntryKey create(String language) {
		return new TranslationEntryKey(language);
	}

	protected final String languageCode;

	protected Event<GenerateTranslationCallback> event = Event.create(GenerateTranslationCallback.class,
			callbacks -> builder -> {
				for (GenerateTranslationCallback callback : callbacks)
					callback.generate(builder);
			});

	protected TranslationEntryKey(String language) {
		this.languageCode = language;
	}

	@Override
	public Identifier getId() {
		return DefaultEntryKeys.translationId(this.languageCode);
	}

	public String getLanguage() {
		return this.languageCode;
	}

	@Override
	public FabricLanguageProvider createProvider(FabricDataOutput dataOutput) {
		return new ConitatorLanguageProvider(dataOutput, this);
	}

	public void registerCallback(GenerateTranslationCallback callback) {
		this.event.register(callback);
	}

}
