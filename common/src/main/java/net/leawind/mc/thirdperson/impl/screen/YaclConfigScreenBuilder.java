package net.leawind.mc.thirdperson.impl.screen;


import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.thirdperson.api.screen.ConfigScreenBuilder;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class YaclConfigScreenBuilder implements ConfigScreenBuilder {
	@Override
	public @NotNull Screen build (@NotNull Config config, @Nullable Screen parent) {
		throw new RuntimeException("Not implemented yet");
	}

	@Override
	public boolean isAvailable () {
		return false;
	}

	@Override
	public boolean isImplemented () {
		return false;
	}
}
