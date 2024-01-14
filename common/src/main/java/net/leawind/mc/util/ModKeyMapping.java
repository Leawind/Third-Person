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
 * 按键绑定处理器
 * <p>
 * 会自动使用 Architectury API 注册按键绑定
 */
@SuppressWarnings("unused")
public final class ModKeyMapping extends KeyMapping {
	private static final HashMap<String, ModKeyMapping> mappings = new HashMap<>();

	public static void registerAll () {
		mappings.values().forEach(KeyMappingRegistry::register);
	}

	/**
	 * 按下后经过 holdLength 则触发 hold 事件
	 */
	private           long              holdLength  = 300;
	/**
	 * 按下->抬起 经过时长小于 pressLength 将触发 press 事件
	 */
	private           long              pressLength = 300;
	private           long              keyDownTime = 0;
	private           Timer             timer       = null;
	@Nullable private Supplier<Boolean> ondown      = null;
	@Nullable private Supplier<Boolean> onup        = null;
	@Nullable private Supplier<Boolean> onhold      = null;
	@Nullable private Supplier<Boolean> onpress     = null;

	public ModKeyMapping (String id, String categoryKey) {
		this(id, InputConstants.UNKNOWN.getValue(), categoryKey);
	}

	public ModKeyMapping (String id, int defaultValue, String categoryKey) {
		super(id, defaultValue, categoryKey);
		mappings.put(id, this);
	}

	public ModKeyMapping holdLength (long holdLength) {
		this.holdLength = holdLength;
		return this;
	}

	public ModKeyMapping pressLength (long pressLength) {
		this.pressLength = pressLength;
		return this;
	}

	public ModKeyMapping onDown (Runnable handler) {
		return onDown(() -> {
			handler.run();
			return false;
		});
	}

	public ModKeyMapping onDown (Supplier<Boolean> handler) {
		ondown = handler;
		return this;
	}

	public ModKeyMapping onUp (Runnable handler) {
		return onUp(() -> {
			handler.run();
			return false;
		});
	}

	public ModKeyMapping onUp (Supplier<Boolean> handler) {
		onup = handler;
		return this;
	}

	public ModKeyMapping onPress (Runnable handler) {
		return onPress(() -> {
			handler.run();
			return false;
		});
	}

	public ModKeyMapping onPress (Supplier<Boolean> handler) {
		onpress = handler;
		return this;
	}

	public ModKeyMapping onHold (Runnable handler) {
		return onHold(() -> {
			handler.run();
			return false;
		});
	}

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
