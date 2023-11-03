package net.leawind.mc.thirdpersonperspective.client;


import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

public class ModKeys {
	public static final Logger     LOGGER          = LogUtils.getLogger();
	/**
	 * 切换左右
	 */
	public static final KeyMapping TOGGLE_SIDE     = new KeyMapping("key.ltpv.toggle_side",
																	InputConstants.KEY_Z,
																	"key.categories.third_person_perspective") {
		@Override
		public void setDown (boolean down) {
			final boolean wasDown = isDown();
			super.setDown(down);
			if (!wasDown && down) {// on key down
				LOGGER.error("Toggle side key down");
			} else if (wasDown && !down) {// on key up
				LOGGER.info("Toggle side key up");
			}
		}
	};
	/**
	 * 按住后移动鼠标调整相机位置
	 */
	public static final KeyMapping ADJUST_POSITION = new KeyMapping("key.ltpv.adjust_position",
																	InputConstants.KEY_Z,
																	"key.categories.third_person_perspective") {
		@Override
		public void setDown (boolean down) {
			final boolean wasDown = isDown();
			super.setDown(down);
			if (!wasDown && down) {// on key down
			} else if (wasDown && !down) {// on key up
			}
		}
	};
	public static final KeyMapping FORCE_AIMING    = new KeyMapping("key.ltpv.force_aiming",
																	InputConstants.UNKNOWN.getValue(),
																	"key.categories.third_person_perspective");

	public static void register () {
		KeyMappingRegistry.register(TOGGLE_SIDE);
		KeyMappingRegistry.register(ADJUST_POSITION);
	}

	public static void handleThrowExpey (Minecraft mc) {
	}
}
