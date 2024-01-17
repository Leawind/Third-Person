package net.leawind.mc.util;


import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

/**
 * 按键映射处理器
 * <p>
 * 会自动使用 Architectury API 注册按键绑定
 */
@SuppressWarnings("unused")
public final class ModKeyMapping extends KeyMapping {
	private static final HashMap<String, ModKeyMapping> mappings = new HashMap<>();

	/**
	 * 使用 Architectury API 注册所有已实例化的按键映射
	 */
	public static void registerAll () {
		mappings.values().forEach(KeyMappingRegistry::register);
	}

	private           long              holdLength  = 300;
	private           long              pressLength = 300;
	private           long              keyDownTime = 0;
	private @Nullable Timer             timer       = null;
	private @Nullable Supplier<Boolean> ondown      = null;
	private @Nullable Supplier<Boolean> onup        = null;
	private @Nullable Supplier<Boolean> onhold      = null;
	private @Nullable Supplier<Boolean> onpress     = null;

	/**
	 * 不设置默认按键
	 * <p>
	 * 需要用户自行设置按键
	 *
	 * @param id          按键映射的标识符，用于可翻译文本
	 * @param categoryKey 类别标识符，用于可翻译文本
	 */
	public ModKeyMapping (String id, String categoryKey) {
		this(id, InputConstants.UNKNOWN.getValue(), categoryKey);
	}

	/**
	 * @param id           按键映射的标识符，用于可翻译文本
	 * @param defaultValue 默认按键
	 * @param categoryKey  类别标识符，用于可翻译文本
	 */
	public ModKeyMapping (String id, int defaultValue, String categoryKey) {
		super(id, defaultValue, categoryKey);
		mappings.put(id, this);
	}

	/**
	 * 长按时长
	 * <p>
	 * 按住一个按键足够长时间后触发长按事件
	 *
	 * @param holdLength 长按时长，单位是 ms
	 */
	public ModKeyMapping holdLength (long holdLength) {
		this.holdLength = holdLength;
		return this;
	}

	/**
	 * 短按时长
	 * <p>
	 * 按下一个按键，并在足够短的时间内松开，就会触发短按事件。
	 *
	 * @param pressLength 短按时长，单位是 ms
	 */
	public ModKeyMapping pressLength (long pressLength) {
		this.pressLength = pressLength;
		return this;
	}

	/**
	 * 当按键被按下时立即触发
	 */
	public ModKeyMapping onDown (Runnable handler) {
		return onDown(() -> {
			handler.run();
			return false;
		});
	}

	/**
	 * 当按键被按下时立即触发
	 *
	 * @param handler 事件处理函数。若其返回true，则不会触发后续的 onPress 或 onHold 事件
	 */
	public ModKeyMapping onDown (Supplier<Boolean> handler) {
		ondown = handler;
		return this;
	}

	/**
	 * 当按键松开时立即触发，位于 onPress 之前
	 */
	public ModKeyMapping onUp (Runnable handler) {
		return onUp(() -> {
			handler.run();
			return false;
		});
	}

	/**
	 * 当按键松开时立即触发，位于 onPress 之前
	 *
	 * @param handler 事件处理函数。若其返回true，则不会触发后续的 onPress 事件
	 */
	public ModKeyMapping onUp (Supplier<Boolean> handler) {
		onup = handler;
		return this;
	}

	/**
	 * 按下一个按键后经过足够短的时间后抬起时触发
	 */
	public ModKeyMapping onPress (Runnable handler) {
		return onPress(() -> {
			handler.run();
			return false;
		});
	}

	/**
	 * 按下一个按键后经过足够短的时间后抬起时触发
	 */
	public ModKeyMapping onPress (Supplier<Boolean> handler) {
		onpress = handler;
		return this;
	}

	/**
	 * 当按住一个按键时间足够长时触发
	 */
	public ModKeyMapping onHold (Runnable handler) {
		return onHold(() -> {
			handler.run();
			return false;
		});
	}

	/**
	 * 当按住一个按键时间足够长时触发
	 */
	public ModKeyMapping onHold (Supplier<Boolean> handler) {
		onhold = handler;
		return this;
	}

	@Override
	public void setDown (boolean down) {
		boolean wasDown = isDown();
		super.setDown(down);
		long now = (long)(1e3 * Blaze3D.getTime());
		if (!wasDown && down) {
			// key down
			if (handle(ondown)) {
				return;
			}
			keyDownTime = now;
			if (onhold != null) {
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run () {
						handle(onhold);
						timer = null;
					}
				}, holdLength);
			}
		} else if (wasDown && !down) {
			// key up
			long sinceKeydown = now - keyDownTime;
			if (handle(onup)) {
				return;
			}
			if (sinceKeydown < pressLength) {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				handle(onpress);
			}
		}
	}

	private boolean handle (Supplier<Boolean> handler) {
		return handler != null && handler.get();
	}
}
