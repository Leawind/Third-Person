package net.leawind.mc.thirdperson;


import net.leawind.mc.thirdperson.api.ModConstants;
import net.leawind.mc.thirdperson.api.config.ConfigManager;
import net.leawind.mc.thirdperson.api.core.EntityAgent;
import net.leawind.mc.thirdperson.event.ThirdPersonEvents;
import net.leawind.mc.thirdperson.event.ThirdPersonKeys;
import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.util.api.math.vector.Vector2d;
import net.leawind.mc.util.api.math.vector.Vector3d;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThirdPerson {
	public static final          Logger        LOGGER                   = LoggerFactory.getLogger(ModConstants.MOD_ID);
	public static final          ConfigManager CONFIG_MANAGER           = ConfigManager.create();
	public static final @NotNull Vector3d      impulse                  = Vector3d.of(0);
	public static final @NotNull Vector2d      impulseHorizon           = Vector2d.of(0);
	public static                EntityAgent   ENTITY_AGENT;
	public static                float         lastPartialTick          = 1;
	public static                double        lastCameraSetupTimeStamp = 0;
	public static                double        lastRenderTickTimeStamp  = 0;
	/**
	 * 是否通过按键切换到了瞄准模式
	 */
	public static                boolean       isToggleToAiming         = false;

	public static void init () {
		ENTITY_AGENT = EntityAgent.create(Minecraft.getInstance());
		CONFIG_MANAGER.tryLoad();
		ThirdPersonKeys.register();
		ThirdPersonEvents.register();
	}

	public static @NotNull Config getConfig () {
		return CONFIG_MANAGER.getConfig();
	}
}
