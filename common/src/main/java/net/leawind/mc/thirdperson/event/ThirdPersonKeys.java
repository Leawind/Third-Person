package net.leawind.mc.thirdperson.event;


import com.mojang.blaze3d.platform.InputConstants;
import net.leawind.mc.thirdperson.ModConstants;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.api.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.thirdperson.impl.core.rotation.RotateTarget;
import net.leawind.mc.util.modkeymapping.ModKeyMapping;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface ThirdPersonKeys {
	ModKeyMapping ADJUST_POSITION   = ModKeyMapping.of(getId("adjust_position"), InputConstants.KEY_Z, ModConstants.KEY_CATEGORY)    //
												   .onDown(ThirdPersonEvents::onStartAdjustingCameraOffset)    //
												   .onUp(ThirdPersonEvents::onStopAdjustingCameraOffset);
	ModKeyMapping FORCE_AIMING      = ModKeyMapping.of(getId("force_aiming"), InputConstants.UNKNOWN.getValue(), ModConstants.KEY_CATEGORY);
	ModKeyMapping TOOGLE_MOD_ENABLE = ModKeyMapping.of(getId("toggle_mod_enable"), ModConstants.KEY_CATEGORY).onDown(() -> {
		Config config = ThirdPerson.getConfig();
		if (ThirdPerson.isThirdPerson()) {
			if (config.is_mod_enable) {
				ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTarget.CAMERA_ROTATION);
			} else {
				ThirdPersonEvents.onEnterThirdPerson();
			}
			config.is_mod_enable = !config.is_mod_enable;
		}
	});
	ModKeyMapping OPEN_CONFIG_MENU  = ModKeyMapping.of(getId("open_config_menu"), ModConstants.KEY_CATEGORY).onDown(() -> {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen == null) {
			mc.setScreen(ThirdPerson.CONFIG_MANAGER.getConfigScreen(null));
		}
	});
	ModKeyMapping TOGGLE_SIDE       = ModKeyMapping.of(getId("toggle_side"), InputConstants.KEY_CAPSLOCK, ModConstants.KEY_CATEGORY).onDown(() -> {
		CameraOffsetScheme scheme = ThirdPerson.getConfig().getCameraOffsetScheme();
		if (scheme.isCentered()) {
			scheme.toNextSide();
			return true;
		} else {
			return false;
		}
	}).onHold(() -> {
		ThirdPerson.getConfig().getCameraOffsetScheme().setCentered(true);
	}).onPress(() -> {
		ThirdPerson.getConfig().getCameraOffsetScheme().toNextSide();
	});
	ModKeyMapping TOGGLE_AIMING     = ModKeyMapping.of(getId("toggle_aiming"), ModConstants.KEY_CATEGORY).onDown(() -> {
		if (ThirdPerson.isAvailable() && ThirdPerson.isThirdPerson()) {
			ThirdPerson.isToggleToAiming = !ThirdPerson.isToggleToAiming;
		}
	});
	ModKeyMapping TOGGLE_PITCH_LOCK = ModKeyMapping.of(getId("toggle_pitch_lock"), ModConstants.KEY_CATEGORY).onDown(() -> {
		Config config = ThirdPerson.getConfig();
		config.lock_camera_pitch_angle = !config.lock_camera_pitch_angle;
	});

	private static @NotNull String getId (@NotNull String name) {
		return "key." + ModConstants.MOD_ID + "." + name;
	}

	static void register () {
		ModKeyMapping.registerAll();
	}
}
