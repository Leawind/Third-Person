package net.leawind.mc.thirdperson;


import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.event.ModEvents;
import net.leawind.mc.thirdperson.event.ModKeys;
import net.minecraft.client.main.GameConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThirdPersonMod {
	public static final String MOD_ID = "leawind_third_person";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void init () {
		Config.init();
		ModKeys.register();
		ModEvents.register();
	}
}
