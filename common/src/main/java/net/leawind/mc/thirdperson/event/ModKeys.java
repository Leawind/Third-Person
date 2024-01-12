package net.leawind.mc.thirdperson.event;


import com.mojang.blaze3d.platform.InputConstants;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.config.ConfigManager;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.thirdperson.core.PlayerAgent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public class ModKeys {
	/**
	 * 按住后进入调整相机位置的模式
	 * <p>
	 * 移动鼠标调整相机位置
	 * <p>
	 * 鼠标滚轮调整相机到玩家的距离（调整幅度随距离指数增长）
	 */
	public static final  KeyMapping ADJUST_POSITION   = new ThirdPersonKeyMapping("adjust_position", InputConstants.KEY_Z).onDown(ModEvents::onStartAdjustingCameraOffset).onUp(ModEvents::onStopAdjustingCameraOffset);
	/**
	 * 按住强制瞄准
	 */
	public static final  KeyMapping FORCE_AIMING      = new ThirdPersonKeyMapping("force_aiming", InputConstants.UNKNOWN.getValue());
	private static final KeyMapping TOOGLE_MOD_ENABLE = new ThirdPersonKeyMapping("toggle_mod_enable").onDown(() -> {
		Config config = Config.get();
		if (CameraAgent.isThirdPerson()) {
			if (config.is_mod_enable) {
				PlayerAgent.turnToCameraRotation(true);
			} else {
				CameraAgent.onEnterThirdPerson();
			}
			config.is_mod_enable = !config.is_mod_enable;
		}
	});
	/**
	 * 按下打开配置菜单
	 */
	private static final KeyMapping OPEN_CONFIG_MENU  = new ThirdPersonKeyMapping("open_config_menu").onDown(() -> {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen == null) {
			mc.setScreen(ConfigManager.get().getConfigScreen(null));
		}
	});
	/**
	 * 切换左右
	 */
	private static final KeyMapping TOGGLE_SIDE       = new ThirdPersonKeyMapping("toggle_side", InputConstants.KEY_CAPSLOCK).onDown(() -> {
		if (Config.get().cameraOffsetScheme.isCentered()) {
			Config.get().cameraOffsetScheme.toNextSide();
			return true;
		} else {
			return false;
		}
	}).onHold(() -> {
		Config.get().cameraOffsetScheme.setCentered(true);
	}).onPress(() -> {
		Config.get().cameraOffsetScheme.toNextSide();
	});
	/**
	 * 切换瞄准状态
	 */
	private static final KeyMapping TOGGLE_AIMING     = new ThirdPersonKeyMapping("toggle_aiming").onDown(() -> {
		if (CameraAgent.isAvailable() && CameraAgent.isThirdPerson()) {
			ModReferee.isToggleToAiming = !ModReferee.isToggleToAiming;
		}
	});

	public static void register () {
		ThirdPersonKeyMapping.registerAll();
	}
}
