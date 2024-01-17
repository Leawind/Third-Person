package net.leawind.mc.thirdperson.event;


import com.mojang.blaze3d.platform.InputConstants;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.api.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.thirdperson.core.PlayerAgent;
import net.leawind.mc.thirdperson.api.ModConstants;
import net.leawind.mc.util.api.ModKeyMapping;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public interface ModKeys {
	private static String getId (String name) {
		return "key." + ModConstants.MOD_ID + "." + name;
	}

	ModKeyMapping ADJUST_POSITION   = ModKeyMapping.of(getId("adjust_position"), InputConstants.KEY_Z, ModConstants.KEY_CATEGORY)    //
												   .onDown(ModEvents::onStartAdjustingCameraOffset)    //
												   .onUp(ModEvents::onStopAdjustingCameraOffset);
	ModKeyMapping FORCE_AIMING      = ModKeyMapping.of(getId("force_aiming"), InputConstants.UNKNOWN.getValue(), ModConstants.KEY_CATEGORY);
	ModKeyMapping TOOGLE_MOD_ENABLE = ModKeyMapping.of(getId("toggle_mod_enable"), ModConstants.KEY_CATEGORY).onDown(() -> {
		Config config = ThirdPersonMod.getConfig();
		if (ModReferee.isThirdPerson()) {
			if (config.is_mod_enable) {
				PlayerAgent.turnToCameraRotation(true);
			} else {
				ModEvents.onEnterThirdPerson();
			}
			config.is_mod_enable = !config.is_mod_enable;
		}
	});
	ModKeyMapping OPEN_CONFIG_MENU  = ModKeyMapping.of(getId("open_config_menu"), ModConstants.KEY_CATEGORY).onDown(() -> {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen == null) {
			mc.setScreen(ThirdPersonMod.getConfigManager().getConfigScreen(null));
		}
	});
	ModKeyMapping TOGGLE_SIDE       = ModKeyMapping.of(getId("toggle_side"), InputConstants.KEY_CAPSLOCK, ModConstants.KEY_CATEGORY).onDown(() -> {
		CameraOffsetScheme scheme = ThirdPersonMod.getConfig().cameraOffsetScheme;
		if (scheme.isCentered()) {
			scheme.toNextSide();
			return true;
		} else {
			return false;
		}
	}).onHold(() -> {
		ThirdPersonMod.getConfig().cameraOffsetScheme.setCentered(true);
	}).onPress(() -> {
		ThirdPersonMod.getConfig().cameraOffsetScheme.toNextSide();
	});
	ModKeyMapping TOGGLE_AIMING     = ModKeyMapping.of(getId("toggle_aiming"), ModConstants.KEY_CATEGORY).onDown(() -> {
		if (CameraAgent.isAvailable() && ModReferee.isThirdPerson()) {
			ModReferee.isToggleToAiming = !ModReferee.isToggleToAiming;
		}
	});
	ModKeyMapping TOGGLE_PITCH_LOCK = ModKeyMapping.of(getId("toggle_pitch_lock"), ModConstants.KEY_CATEGORY).onDown(() -> {
		Config config = ThirdPersonMod.getConfig();
		config.lock_camera_pitch_angle = !config.lock_camera_pitch_angle;
	});

	static void register () {
		ModKeyMapping.registerAll();
	}
}
