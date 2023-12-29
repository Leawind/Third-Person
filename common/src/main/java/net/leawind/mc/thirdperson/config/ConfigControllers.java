package net.leawind.mc.thirdperson.config;


import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import net.minecraft.network.chat.Component;

/**
 * Config 控制器
 */
class ConfigControllers {
	/**
	 * 平滑系数控制器
	 */
	public static ControllerBuilder<Double> SMOOTH_FACTOR (Option<Double> opt) {
		return DoubleSliderControllerBuilder.create(opt)
											.valueFormatter(v -> Component.literal(String.format("1E-%2.2f", v)))
											.range(0D, 20D)
											.step(0.25d);
	}

	/**
	 * 相机偏移量，范围 [-1, +1]
	 */
	public static ControllerBuilder<Double> OFFSET (Option<Double> opt) {
		return DoubleSliderControllerBuilder.create(opt)
											.valueFormatter(v -> Component.literal(String.format("%+1.3f", v)))
											.range(-1D, 1D)
											.step(0.001d);
	}
}
