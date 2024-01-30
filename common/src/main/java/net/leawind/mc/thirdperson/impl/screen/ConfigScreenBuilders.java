package net.leawind.mc.thirdperson.impl.screen;


import net.leawind.mc.thirdperson.api.screen.ConfigScreenBuilder;

public final class ConfigScreenBuilders {
	public static final ConfigScreenBuilder YACL          = null;
	public static final ConfigScreenBuilder CARBON_CONFIG = null;
	public static final ConfigScreenBuilder CLOTH_CONFIG  = new ClothConfigScreenBuilder();
}
