package net.leawind.mc.thirdperson.config;


import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 用于构建 Config
 */
class ConfigBuilders {
	public static Option<Double> SMOOTH_FACTOR_OPTION (double defaultValue, String name, Supplier<Double> rawGetter, Consumer<Double> rawSetter) {
		Consumer<Double> packedSetter = v -> {
			rawSetter.accept(v);
			Config.updateCameraOffsetScheme();
		};
		return ConfigBuilders.<Double>option(name).binding(defaultValue, rawGetter, packedSetter).controller(ConfigBuilders::SMOOTH_FACTOR_CONTROLLER).build();
	}

	/**
	 * 平滑系数控制器
	 */
	public static ControllerBuilder<Double> SMOOTH_FACTOR_CONTROLLER (Option<Double> opt) {
		return DoubleSliderControllerBuilder.create(opt).valueFormatter(v -> Component.literal(String.format("%1.3f", v))).range(0D, 1D).step(0.001d);
	}

	/**
	 * 相机偏移量，范围 [-1, +1]
	 */
	public static ControllerBuilder<Double> OFFSET_CONTROLLER (Option<Double> opt) {
		return DoubleSliderControllerBuilder.create(opt).valueFormatter(v -> Component.literal(String.format("%+1.3f", v))).range(-1D, 1D).step(0.001d);
	}

	/**
	 * 获取可翻译文本
	 */
	public static Component getText (String name) {
		return Component.translatable(ThirdPersonMod.MOD_ID + "." + name);
	}

	/**
	 * 生成 option 的 name 和 description
	 */
	public static <T> Option.Builder<T> option (String name) {
		return Option.<T>createBuilder().name(getText("option." + name)).description(OptionDescription.of(getText("option." + name + ".desc")));
	}

	/**
	 * 生成 group 的 name 和 description
	 */
	public static OptionGroup.Builder group (String name) {
		return OptionGroup.createBuilder().name(getText("option_group." + name)).description(OptionDescription.of(getText("option_group." + name + ".desc")));
	}
}
