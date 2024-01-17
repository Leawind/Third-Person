package net.leawind.mc.thirdperson.config;


import org.jetbrains.annotations.NotNull;

public class DefaultConfig extends AbstractConfig {
	protected static final DefaultConfig instance = new DefaultConfig();

	public static @NotNull DefaultConfig get () {
		return instance;
	}
}
