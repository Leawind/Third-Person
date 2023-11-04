package net.leawind.mc.thirdpersonperspective;


import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.leawind.mc.thirdpersonperspective.core.Options;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

public class ModKeys {
	public static final Logger     LOGGER          = LogUtils.getLogger();
	public static final String     CATEGORY_KEY    = "key.categories.third_person_perspective";
	/**
	 * 切换左右
	 */
	public static final KeyMapping TOGGLE_SIDE     = new KeyMapping("key.ltpv.toggle_side",
																	InputConstants.KEY_CAPSLOCK,
																	CATEGORY_KEY) {
		@Override
		public void setDown (boolean down) {
			final boolean wasDown = isDown();
			super.setDown(down);
			if (!wasDown && down) {// on key down
				LOGGER.error("Toggle side key down");
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
	public static final KeyMapping ADJUST_POSITION = new KeyMapping("key.ltpv.adjust_position",
																	InputConstants.KEY_Z,
																	CATEGORY_KEY);
	/**
	 * 切换瞄准状态
	 */
	public static final KeyMapping TOGGLE_AIMING   = new KeyMapping("key.ltpv.force_aiming",
																	InputConstants.UNKNOWN.getValue(),
																	CATEGORY_KEY) {
		@Override
		public void setDown (boolean down) {
			final boolean wasDown = isDown();
			super.setDown(down);
			if (!wasDown && down) {// on key down
				Options.isToggleToAiming = !Options.isToggleToAiming;
			}
		}
	};
	/**
	 * 按住强制瞄准
	 */
	public static final KeyMapping FORCE_AIMING    = new KeyMapping("key.ltpv.toggle_aiming",
																	InputConstants.UNKNOWN.getValue(),
																	CATEGORY_KEY);

	public static void register () {
		KeyMappingRegistry.register(TOGGLE_SIDE);
		KeyMappingRegistry.register(ADJUST_POSITION);
		KeyMappingRegistry.register(TOGGLE_AIMING);
		KeyMappingRegistry.register(FORCE_AIMING);
	}

	public static void handleThrowExpey (Minecraft mc) {
	}
}
