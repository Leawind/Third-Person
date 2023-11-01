package net.leawind.mc.thirdpersonperspective;


import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.leawind.mc.thirdpersonperspective.agent.CameraAgent;
import net.leawind.mc.thirdpersonperspective.mixin.GuiAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
@Mod(ThirdPersonPerspective.MODID)
public class ThirdPersonPerspective {
	public static final String MODID  = "leawind_third_person_perspective";
	public static final Logger LOGGER = LogUtils.getLogger();

	public ThirdPersonPerspective () {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static void renderCrosshair (@NotNull GuiGraphics guiGraphics) {
		final int crosshairSize = 15;
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
									   GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
									   GlStateManager.SourceFactor.ONE,
									   GlStateManager.DestFactor.ZERO);
		guiGraphics.blitSprite(GuiAccessor.getGuiIconLocation(),
							   (guiGraphics.guiWidth() - crosshairSize) / 2,
							   (guiGraphics.guiHeight() - crosshairSize) / 2,
							   15,
							   15);
		RenderSystem.defaultBlendFunc();
	}

	public static class Options {
		public static boolean isForceKeepAiming = false;
		public static boolean isToggleToAiming  = false;
	}

	@SubscribeEvent
	public void onClientTick (TickEvent.ClientTickEvent event) {
	}

	@Mod.EventBusSubscriber(modid=MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
	public static class ClientModEvents {
		/**
		 * 第三人称按住瞄准
		 */
		public static final KeyMapping keyKeepAim    = new KeyMapping("key.tpv_hold_aim",
																	  InputConstants.UNKNOWN.getValue(),
																	  "key.categories.misc") {
			@Override
			public void setDown (boolean down) {
				final boolean wasDown = isDown();
				super.setDown(down);
				if (!wasDown && down) {   // on key down
					Options.isForceKeepAiming = true;
				} else if (wasDown && !down) { // on key up
					Options.isForceKeepAiming = false;
				}
			}
		};
		/**
		 * 第三人称切换瞄准
		 */
		public static final KeyMapping keyToggleAim  = new KeyMapping("key.tpv_toggle_aim",
																	  InputConstants.UNKNOWN.getValue(),
																	  "key.categories.misc") {
			@Override
			public void setDown (boolean down) {
				final boolean wasDown = isDown();
				super.setDown(down);
				if (!wasDown && down) {   // on key down
					Options.isToggleToAiming = !Options.isToggleToAiming;
				}
			}
		};
		/**
		 * 第三人称切换机位
		 * <p>
		 * 单击在左右之间切换
		 * <p>
		 * 长按切换到头顶
		 */
		public static final KeyMapping keyToggleSide = new KeyMapping("key.tpv_toggle_side",
																	  InputConstants.KEY_Z,
																	  "key.categories.misc") {
			private double keyDownTimestamp = 0;
			private Timer timer = null;

			@Override
			public void setDown (boolean down) {
				final boolean wasDown = isDown();
				super.setDown(down);
				double now = Blaze3D.getTime();
				if (CameraAgent.isAvailable() && CameraAgent.getInstance().isFreeTpv) {
					CameraAgent cameraAgent = CameraAgent.getInstance();
					if (!wasDown && down) {   // on key down
						if (cameraAgent.cameraOffsetType.isTop()) {
							cameraAgent.nextCameraOffsetType();
						} else {
							keyDownTimestamp = now;
							timer            = new Timer();
							timer.schedule(new TimerTask() {
								public void run () {
									cameraAgent.setCameraOffsetTypeToTop();
									timer = null;
								}
							}, 300);    // 长按
						}
					} else if (wasDown && !down) {    // on key up
						double sinceKeydown = now - keyDownTimestamp;
						if (sinceKeydown < 0.3) {    // 单击
							if (timer != null) {
								timer.cancel();
								timer = null;
							}
							cameraAgent.nextCameraOffsetType();
						}
					}
				}
			}
		};

		static {
			Minecraft mc = Minecraft.getInstance();
			mc.options.keyMappings = ArrayUtils.addAll(mc.options.keyMappings, keyKeepAim, keyToggleAim, keyToggleSide);
		}
	}
}
