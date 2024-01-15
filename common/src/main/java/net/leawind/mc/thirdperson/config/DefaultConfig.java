package net.leawind.mc.thirdperson.config;


public class DefaultConfig extends AbstractConfig {
	protected static final DefaultConfig instance = new DefaultConfig();

	public static DefaultConfig get () {
		return instance;
	}
}
