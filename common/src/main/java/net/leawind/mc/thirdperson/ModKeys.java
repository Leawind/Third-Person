package net.leawind.mc.thirdperson;


import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModOptions;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class ModKeys {
	public static final  Logger     LOGGER           = LoggerFactory.getLogger(ThirdPersonMod.MOD_ID);
	private static final String     CATEGORY_KEY     = "key.categories." + ThirdPersonMod.MOD_ID;
	/**
	 * 按住后进入调整相机位置的模式
	 * <p>
	 * 移动鼠标调整相机位置
	 * <p>
	 * 鼠标滚轮调整相机到玩家的距离（调整幅度随距离指数增长）
	 */
	public static final  KeyMapping ADJUST_POSITION  = new KeyMapping(getText("adjust_position"),
																	  InputConstants.KEY_Z,
																	  CATEGORY_KEY) {
		@Override
		public void setDown (boolean down) {
			boolean wasDown = isDown();
			super.setDown(down);
			if (CameraAgent.isAvailable()) {
				if (!wasDown && down) {
					// key down
					ModEvents.onStartAdjustingCameraOffset();
				} else if (wasDown && !down) {
					// key up
					ModEvents.onStopAdjustingCameraOffset();
				}
			}
		}
	};
	/**
	 * 按住强制瞄准
	 */
	public static final  KeyMapping FORCE_AIMING     = new KeyMapping(getText("force_aiming"),
																	  InputConstants.UNKNOWN.getValue(),
																	  CATEGORY_KEY);
	/**
	 * 按下打开配置菜单
	 */
	private static final KeyMapping OPEN_CONFIG_MENU = new KeyMapping(getText("open_config_menu"),
																	  InputConstants.UNKNOWN.getValue(),
																	  CATEGORY_KEY) {
		@Override
		public void setDown (boolean down) {
			boolean wasDown = isDown();
			super.setDown(down);
			if (!wasDown && down) {
				// key down
				Minecraft mc = Minecraft.getInstance();
				if (mc.screen == null) {
					mc.setScreen(Config.getConfigScreen(null));
				}
			}
		}
	};
	/**
	 * 切换左右
	 */
	private static final KeyMapping TOGGLE_SIDE      = new KeyMapping(getText("toggle_side"),
																	  InputConstants.KEY_CAPSLOCK,
																	  CATEGORY_KEY) {
		private Timer timer = null;
		private double keyDownTime = 0;

		@Override
		public void setDown (boolean down) {
			final boolean wasDown = isDown();
			super.setDown(down);
			if (CameraAgent.isAvailable() && CameraAgent.isThirdPerson()) {
				double             now    = Blaze3D.getTime();
				CameraOffsetScheme scheme = Config.cameraOffsetScheme;
				if (!wasDown && down) {   // on key down
					if (scheme.isCenter()) {
						scheme.nextSide();
					} else {
						keyDownTime = now;
						timer       = new Timer();
						timer.schedule(new TimerTask() {
							public void run () {
								// 长按
								scheme.setToCenter();
								timer = null;
							}
						}, 300);
					}
				} else if (wasDown && !down) {    // on key up
					double sinceKeydown = now - keyDownTime;
					if (sinceKeydown < 0.3) {
						// 单击
						if (timer != null) {
							timer.cancel();
							timer = null;
						}
						scheme.nextSide();
					}
				}
			}
		}
	};
	/**
	 * 切换瞄准状态
	 */
	private static final KeyMapping TOGGLE_AIMING    = new KeyMapping(getText("toggle_aiming"),
																	  InputConstants.UNKNOWN.getValue(),
																	  CATEGORY_KEY) {
		@Override
		public void setDown (boolean down) {
			final boolean wasDown = isDown();
			super.setDown(down);
			if (CameraAgent.isAvailable() && !wasDown && down) {// on key down
				ModOptions.isToggleToAiming = !ModOptions.isToggleToAiming;
			}
		}
	};

	private static String getText (String name) {
		return "key." + ThirdPersonMod.MOD_ID + "." + name;
	}

	public static void register () {
		KeyMappingRegistry.register(OPEN_CONFIG_MENU);
		KeyMappingRegistry.register(TOGGLE_SIDE);
		KeyMappingRegistry.register(ADJUST_POSITION);
		KeyMappingRegistry.register(TOGGLE_AIMING);
		KeyMappingRegistry.register(FORCE_AIMING);
	}

	public static void handleThrowExpey (Minecraft mc) {
	}
}
