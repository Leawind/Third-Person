package net.leawind.mc.thirdperson.event;


import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.leawind.mc.thirdperson.core.ModConstants;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

/**
 * 这是自定义的按键绑定处理器
 */
@SuppressWarnings("unused")
public final class ThirdPersonKeyMapping extends KeyMapping {
	private static final String                                 CATEGORY_KEY = "key.categories." + ModConstants.MOD_ID;
	private static final HashMap<String, ThirdPersonKeyMapping> mappings     = new HashMap<>();
	/**
	 * 按下后经过 holdLength 则触发 hold 事件
	 */
	private              long                                   holdLength   = 300;
	/**
	 * 按下->抬起 经过时长小于 pressLength 将触发 press 事件
	 */
	private              long                                   pressLength  = 300;
	private              long                                   keyDownTime  = 0;
	private              Timer                                  timer        = null;
	@Nullable private    Supplier<Boolean>                      ondown       = null;
	@Nullable private    Supplier<Boolean>                      onup         = null;
	@Nullable private    Supplier<Boolean>                      onhold       = null;
	@Nullable private    Supplier<Boolean>                      onpress      = null;

	public ThirdPersonKeyMapping (String name) {
		this(name, InputConstants.UNKNOWN.getValue());
	}

	public ThirdPersonKeyMapping (String name, int defaultValue) {
		this(name, defaultValue, CATEGORY_KEY);
	}

	public ThirdPersonKeyMapping (String name, int defaultValue, String categoryKey) {
		super(getId(name), defaultValue, categoryKey);
		mappings.put(getId(name), this);
	}

	private static String getId (String name) {
		return "key." + ModConstants.MOD_ID + "." + name;
	}

	public static void registerAll () {
		mappings.values().forEach(KeyMappingRegistry::register);
	}

	public ThirdPersonKeyMapping holdLength (long holdLength) {
		this.holdLength = holdLength;
		return this;
	}

	public ThirdPersonKeyMapping pressLength (long pressLength) {
		this.pressLength = pressLength;
		return this;
	}

	public ThirdPersonKeyMapping onDown (Runnable handler) {
		return onDown(() -> {
			handler.run();
			return false;
		});
	}

	public ThirdPersonKeyMapping onDown (Supplier<Boolean> handler) {
		ondown = handler;
		return this;
	}

	public ThirdPersonKeyMapping onUp (Runnable handler) {
		return onUp(() -> {
			handler.run();
			return false;
		});
	}

	public ThirdPersonKeyMapping onUp (Supplier<Boolean> handler) {
		onup = handler;
		return this;
	}

	public ThirdPersonKeyMapping onPress (Runnable handler) {
		return onPress(() -> {
			handler.run();
			return false;
		});
	}

	public ThirdPersonKeyMapping onPress (Supplier<Boolean> handler) {
		onpress = handler;
		return this;
	}

	public ThirdPersonKeyMapping onHold (Runnable handler) {
		return onHold(() -> {
			handler.run();
			return false;
		});
	}

	public ThirdPersonKeyMapping onHold (Supplier<Boolean> handler) {
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
