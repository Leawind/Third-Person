package net.leawind.mc.thirdperson.forge.config;


import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.config.ConfigManager;
import net.minecraft.client.gui.screens.Screen;

public class ConfigBuilders {
	/**
	 * DOITNOW forge build screen
	 */
	public static Screen buildConfigScreen (Config config, Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(Config.getText("text.title"))
			.setSavingRunnable(ConfigManager.get()::save);
		//
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		// Category: general
		builder.getOrCreateCategory(Config.getText("category.general"))
			.addEntry(entryBuilder.startBooleanToggle(Config.getText("option.is_mod_enable"), config.is_mod_enable)
				.setDefaultValue(true)
				.setTooltip(Config.getText("option.is_mod_enable.desc"))
				.setSaveConsumer(newValue -> config.is_mod_enable = newValue)
				.build());
		// return
		return builder.build();
	}
}
