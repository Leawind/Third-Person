package net.leawind.mc.thirdperson;


import com.mojang.logging.LogUtils;
import net.leawind.mc.thirdperson.config.Config;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class ThirdPersonMod {
	public static final String MOD_ID          = "leawind_third_person";
	public static final String TITLE_KEY       = "text.leawind_third_person.title";
	public static final String DESCRIPTION_KEY = "text.leawind_third_person.description";
	public static final Logger LOGGER          = LogUtils.getLogger();

	public static void init () {
		LOGGER.atLevel(Level.TRACE);
		Config.init();
		ModKeys.register();
		ModEvents.register();
	}
}
