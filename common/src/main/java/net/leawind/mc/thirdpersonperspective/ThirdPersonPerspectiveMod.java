package net.leawind.mc.thirdpersonperspective;


import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import net.leawind.mc.thirdpersonperspective.client.ModKeys;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class ThirdPersonPerspectiveMod {
	public static final String                     MOD_ID     = "leawind_third_person_perspective";
	public static final Logger                     LOGGER     = LogUtils.getLogger();
	public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

	public static void init () {
		// run/config
		System.out.println(ExampleExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
		ModKeys.register();
		ModEvents.register();
		LOGGER.info("Mod Third Person Perspective initialized!");
	}
}
