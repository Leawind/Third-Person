package net.leawind.mc.thirdperson.config;


import com.mojang.logging.LogUtils;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.ConfigInstance;
import dev.isxander.yacl3.config.GsonConfigInstance;
import net.leawind.mc.thirdperson.ExpectPlatform;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.util.monolist.MonoList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

import java.nio.file.Path;

public class Config {
	//============================================================//
	@ConfigEntry
	public static       boolean                is_mod_enable            = true;
	@ConfigEntry
	private static      int                    available_distance_count = 16;
	@ConfigEntry
	public static       double                 camera_distance_min      = 0.1;
	@ConfigEntry
	public static       double                 camera_distance_max      = 8;
	@ConfigEntry
	public static       double                 camera_ray_trace_length  = 256;
	@ConfigEntry
	public static       double                 aiming_offset_max        = 2.0;
	//============================================================//
	// 根据上面的配置选项生成的配置（在onLoad中更新）
	public static       MonoList               distanceMonoList;
	//============================================================//
	public static final Logger                 LOGGER                   = LogUtils.getLogger();
	//	配置文件路径
	public static final Path                   CONFIG_FILE_PATH         = ExpectPlatform.getConfigDirectory().resolve(
		ThirdPersonMod.MOD_ID + ".json5");
	public static final ConfigInstance<Config> GSON                     = GsonConfigInstance.createBuilder(Config.class)
																							.setPath(CONFIG_FILE_PATH)
																							.build();

	/**
	 * 获取配置屏幕
	 * <p>
	 * 提供给 ModMenu
	 */
	public static Screen getConfigScreen (Screen parent) {
		// @formatter:off
		YetAnotherConfigLib config = YetAnotherConfigLib.createBuilder()
			.title(Component.translatable(ThirdPersonMod.TITLE_KEY))
			.save(Config::save)
			.category(ConfigCategory.createBuilder()
				.name(Component.translatable("l3p.options.category.common"))
				.tooltip(Component.translatable("l3p.options.category.common.desc"))
					.option(Option.<Boolean>createBuilder()
						.name(Component.translatable(getKey("is_mod_enable")))
						.description(OptionDescription.of(Component.translatable(getKey("is_mod_enable.desc"))))
						.binding(true, () -> is_mod_enable, v -> is_mod_enable = v)
						.controller(TickBoxControllerBuilder::create)
						.build())
					.option(Option.<Integer>createBuilder()
						.name(Component.translatable(getKey("available_distance_count")))
						.description(OptionDescription.of(Component.translatable(getKey("available_distance_count.desc"))))
						.binding(16, ()->available_distance_count, v->{available_distance_count=v;	updateCameraDistances();})
						.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(2, 64).step(1))
						.build())
					.option(Option.<Double>createBuilder()
						.name(Component.translatable(getKey("camera_distance_min")))
						.description(OptionDescription.of(Component.translatable(getKey("camera_distance_min.desc"))))
						.binding(0.1d, ()->camera_distance_min, v->{camera_distance_min=v;	updateCameraDistances();})
						.controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0d, 2d).step(0.1d))
						.build())
					.option(Option.<Double>createBuilder()
						.name(Component.translatable(getKey("camera_distance_max")))
						.description(OptionDescription.of(Component.translatable(getKey("camera_distance_max.desc"))))
						.binding(8d, ()->camera_distance_max, v->{camera_distance_max=v;	updateCameraDistances();})
						.controller(opt -> DoubleSliderControllerBuilder.create(opt).range(2d, 16d).step(0.1d))
						.build())
					.option(Option.<Double>createBuilder()
						.name(Component.translatable(getKey("camera_ray_trace_length")))
						.description(OptionDescription.of(Component.translatable(getKey("camera_ray_trace_length.desc"))))
						.binding(256d, ()->camera_ray_trace_length, v->camera_ray_trace_length=v)
						.controller(opt -> DoubleSliderControllerBuilder.create(opt).range(10d, 1024d).step(1d))
						.build())
					.option(Option.<Double>createBuilder()
						.name(Component.translatable(getKey("aiming_offset_max")))
						.description(OptionDescription.of(Component.translatable(getKey("aiming_offset_max.desc"))))
						.binding(2d, ()->aiming_offset_max, v->aiming_offset_max=v)
						.controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.5d, 5d).step(0.1d))
						.build())
				.build())
			.build();
		// @formatter:on
		return config.generateScreen(parent);
	}

	/**
	 * 加载模组时初始化
	 */
	public static void init () {
		load();
	}

	/**
	 * 保存配置文件
	 */
	public static void save () {
		GSON.save();
	}

	/**
	 * 加载配置文件
	 */
	public static void load () {
		GSON.load();
		updateCameraDistances();
	}

	/**
	 * 加载配置完成时调用
	 */
	public static void updateCameraDistances () {
		distanceMonoList = MonoList.of(available_distance_count,
									   camera_distance_min,
									   camera_distance_max,
									   i -> i * i,
									   Math::sqrt);
	}

	public static void setAvailableDistanceCount (int v) {
		available_distance_count = v;
		updateCameraDistances();
	}

	/**
	 * 根据配置项的名称获取配置键
	 */
	private static String getKey (String name) {
		//		return "options." + ThirdPersonMod.MOD_ID + "." + name;
		return "options.l3p." + name;
	}
}
