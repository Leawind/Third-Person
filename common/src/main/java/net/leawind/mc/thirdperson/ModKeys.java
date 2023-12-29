package net.leawind.mc.thirdperson;


import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.Options;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class ModKeys {
	public static final Logger     LOGGER           = LogUtils.getLogger();
	public static final String     CATEGORY_KEY     = "key.categories.leawind_third_person";
	/**
	 * 按下打开配置菜单
	 */
	public static final KeyMapping OPEN_CONFIG_MENU = new KeyMapping("key.leawind_third_person.open_config_menu",
																	 InputConstants.UNKNOWN.getValue(),
																	 CATEGORY_KEY) {
		@Override
		public void setDown (boolean down) {
			boolean wasDown = isDown();
			super.setDown(down);
			if (CameraAgent.isAvailable()) {
				if (!wasDown && down) {
					// key down
					LOGGER.debug("Open config menu");//TODO
				}
			}
		}
	};
	/**
	 * 切换左右
	 */
	public static final KeyMapping TOGGLE_SIDE      = new KeyMapping("key.leawind_third_person.toggle_side",
																	 InputConstants.KEY_CAPSLOCK,
																	 CATEGORY_KEY) {
		private Timer timer = null;
		private double keyDownTime = 0;

		@Override
		public void setDown (boolean down) {
			final boolean wasDown = isDown();
			super.setDown(down);
			double now = Blaze3D.getTime();
			if (CameraAgent.isAvailable() && CameraAgent.isThirdPerson) {
				CameraOffsetScheme scheme = Config.cameraOffsetScheme;
				if (!wasDown && down) {   // on key down
					if (scheme.isMiddle()) {
						scheme.nextSide();
					} else {
						keyDownTime = now;
						timer       = new Timer();
						timer.schedule(new TimerTask() {
							public void run () {
								// 长按
								scheme.setToMiddle();
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
	 * 按住后进入调整相机位置的模式
	 * <p>
	 * 移动鼠标调整相机位置
	 * <p>
	 * 鼠标滚轮调整相机到玩家的距离（调整幅度随距离指数增长）
	 */
	public static final KeyMapping ADJUST_POSITION  = new KeyMapping("key.leawind_third_person.adjust_position",
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
	 * 切换瞄准状态
	 */
	public static final KeyMapping TOGGLE_AIMING    = new KeyMapping("key.leawind_third_person.toggle_aiming",
																	 InputConstants.UNKNOWN.getValue(),
																	 CATEGORY_KEY) {
		@Override
		public void setDown (boolean down) {
			final boolean wasDown = isDown();
			super.setDown(down);
			if (CameraAgent.isAvailable() && !wasDown && down) {// on key down
				Options.isToggleToAiming = !Options.isToggleToAiming;
			}
		}
	};
	/**
	 * 按住强制瞄准
	 */
	public static final KeyMapping FORCE_AIMING     = new KeyMapping("key.leawind_third_person.force_aiming",
																	 InputConstants.UNKNOWN.getValue(),
																	 CATEGORY_KEY);

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
