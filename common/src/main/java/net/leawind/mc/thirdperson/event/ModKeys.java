package net.leawind.mc.thirdperson.event;


import com.mojang.blaze3d.platform.InputConstants;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.thirdperson.core.PlayerAgent;
import net.leawind.mc.thirdperson.util.ModConstants;
import net.leawind.mc.util.ModKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public class ModKeys {
	private static String getId (String name) {
		return "key." + ModConstants.MOD_ID + "." + name;
	}

	/**
	 * 按住后进入调整相机位置的模式
	 * <p>
	 * 移动鼠标调整相机位置
	 * <p>
	 * 鼠标滚轮调整相机到玩家的距离（调整幅度随距离指数增长）
	 */
	public static final  KeyMapping ADJUST_POSITION   = new ModKeyMapping(getId("adjust_position"), InputConstants.KEY_Z, ModConstants.KEY_CATEGORY).onDown(ModEvents::onStartAdjustingCameraOffset).onUp(ModEvents::onStopAdjustingCameraOffset);
	/**
	 * 按住强制瞄准
	 */
	public static final  KeyMapping FORCE_AIMING      = new ModKeyMapping(getId("force_aiming"), InputConstants.UNKNOWN.getValue(), ModConstants.KEY_CATEGORY);
	private static final KeyMapping TOOGLE_MOD_ENABLE = new ModKeyMapping(getId("toggle_mod_enable"), ModConstants.KEY_CATEGORY).onDown(() -> {
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
	/**
	 * 按下打开配置菜单
	 */
	private static final KeyMapping OPEN_CONFIG_MENU  = new ModKeyMapping(getId("open_config_menu"), ModConstants.KEY_CATEGORY).onDown(() -> {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen == null) {
			mc.setScreen(ThirdPersonMod.getConfigManager().getConfigScreen(null));
		}
	});
	/**
	 * 切换左右
	 */
	private static final KeyMapping TOGGLE_SIDE       = new ModKeyMapping(getId("toggle_side"), InputConstants.KEY_CAPSLOCK, ModConstants.KEY_CATEGORY).onDown(() -> {
		Config config = ThirdPersonMod.getConfig();
		if (config.cameraOffsetScheme.isCentered()) {
			config.cameraOffsetScheme.toNextSide();
			return true;
		} else {
			return false;
		}
	}).onHold(() -> {
		ThirdPersonMod.getConfig().cameraOffsetScheme.setCentered(true);
	}).onPress(() -> {
		ThirdPersonMod.getConfig().cameraOffsetScheme.toNextSide();
	});
	/**
	 * 切换瞄准状态
	 */
	private static final KeyMapping TOGGLE_AIMING     = new ModKeyMapping(getId("toggle_aiming"), ModConstants.KEY_CATEGORY).onDown(() -> {
		if (CameraAgent.isAvailable() && ModReferee.isThirdPerson()) {
			ModReferee.isToggleToAiming = !ModReferee.isToggleToAiming;
		}
	});
	/**
	 * 切换俯仰角锁定
	 */
	private static final KeyMapping TOGGLE_PITCH_LOCK = new ModKeyMapping(getId("toggle_pitch_lock"), ModConstants.KEY_CATEGORY).onDown(() -> {
		Config config = ThirdPersonMod.getConfig();
		config.lock_camera_pitch_angle = !config.lock_camera_pitch_angle;
	});

	public static void register () {
		ModKeyMapping.registerAll();
	}
}
