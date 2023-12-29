package net.leawind.mc.thirdperson.config;
//import com.google.gson.*;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.ConfigInstance;
import dev.isxander.yacl3.config.GsonConfigInstance;
import net.leawind.mc.thirdperson.ExpectPlatform;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.util.monolist.MonoList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.nio.file.Path;

/**
 * 模组配置项
 * <p>
 * 普通配置项可由用户在配置屏幕中调整，程序可以直接使用其值。
 * <p>
 * 也有一些配置项比较特殊。以相机离玩家的距离的调整范围为例，以下3个选项可以由玩家直接调整：
 * <p>
 * available_distance_count, camera_distance_min, camera_distance_max
 * <p>
 * 但让程序直接使用这些值就有点不方便了。需要先根据这些值生成一个 MonoList 对象，再让程序直接访问这个 MonoList对象。
 * <p>
 * 如果每次使用时都实例化一个新的MonoList对象就太浪费了，所以最好在修改配置后立即生成它并记录在Config的某一字段中（distanceMonoList）
 * <p>
 * 所以原本的3个选项不必被外界访问，可以使用private。
 */
public class Config {
	public static final Logger                 LOGGER                             = LoggerFactory.getLogger(ThirdPersonMod.MOD_ID);//TODO loggers
	// 配置文件路径
	public static final Path                   CONFIG_FILE_PATH                   = ExpectPlatform.getConfigDirectory()
		.resolve(ThirdPersonMod.MOD_ID + ".json");
	// ============================================================//
	@ConfigEntry
	public static       boolean                is_mod_enable                      = true;
	@ConfigEntry
	public static       double                 camera_ray_trace_length            = 256;
	@ConfigEntry
	public static       double                 aiming_offset_max                  = 2.0;
	// Camera distance -------------------------------------------//
	@ConfigEntry
	private static      int                    available_distance_count           = 16;
	@ConfigEntry
	private static      double                 camera_distance_min                = 0.1;
	@ConfigEntry
	private static      double                 camera_distance_max                = 8;
	// Smooth factors --------------------------------------------//
	@ConfigEntry
	public static       double                 flying_smooth_factor               = 0D;//TODO
	@ConfigEntry
	private static      double                 normal_smooth_factor_horizon       = 0D;
	@ConfigEntry
	private static      double                 normal_smooth_factor_vertical      = 0D;
	@ConfigEntry
	private static      double                 normal_camera_offset_smooth_factor = 0D;
	@ConfigEntry
	private static      double                 normal_distance_smooth_factor      = 0D;
	@ConfigEntry
	private static      double                 aiming_smooth_factor_horizon       = 0D;
	@ConfigEntry
	private static      double                 aiming_smooth_factor_vertical      = 0D;
	@ConfigEntry
	private static      double                 aiming_camera_offset_smooth_factor = 0D;
	@ConfigEntry
	private static      double                 aiming_distance_smooth_factor      = 0D;
	// Camera offset scheme --------------------------------------//
	@ConfigEntry
	private static      double                 normal_max_distance                = 1.6D;
	@ConfigEntry
	private static      double                 normal_offset_x                    = -0.372D;
	@ConfigEntry
	private static      double                 normal_offset_y                    = 0.2D;
	@ConfigEntry
	private static      double                 normal_offset_middle               = 0D;
	@ConfigEntry
	private static      double                 aiming_max_distance                = 1.6D;
	@ConfigEntry
	private static      double                 aiming_offset_x                    = -0.372D;
	@ConfigEntry
	private static      double                 aiming_offset_y                    = 0.2D;
	@ConfigEntry
	private static      double                 aiming_offset_middle               = 0D;
	// ============================================================//
	// 根据上面的配置选项生成的配置
	public static       MonoList               distanceMonoList;
	public static       CameraOffsetScheme     cameraOffsetScheme                 = CameraOffsetScheme.DEFAULT_CLOSER;
	// ============================================================//
	public static final ConfigInstance<Config> GSON                               = GsonConfigInstance.createBuilder(Config.class)
		.setPath(CONFIG_FILE_PATH)
		.overrideGsonBuilder(new GsonBuilder().setPrettyPrinting()
			.serializeNulls()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.registerTypeHierarchyAdapter(Component.class, new Component.Serializer())
			.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
			.registerTypeHierarchyAdapter(Color.class, new GsonConfigInstance.ColorTypeAdapter())
			.registerTypeHierarchyAdapter(Config.class, new ConfigAdapter()))
		.build();

	/**
	 * 生成 option 的 name 和 description
	 */
	private static <T> Option.Builder<T> option (String name) {
		return Option.<T>createBuilder()
			.name(getText("option." + name))
			.description(OptionDescription.of(getText("option." + name + ".desc")));
	}

	/**
	 * //TODO simplify
	 * <p>
	 * 获取配置屏幕
	 * <p>
	 * 提供给 ModMenu
	 */
	public static Screen getConfigScreen (Screen parent) {
		YetAnotherConfigLib config = YetAnotherConfigLib.createBuilder()
			.title(getText("text.title"))
			.save(Config::save)
			.category(ConfigCategory.createBuilder()
				.name(getText("option_category.common"))
				.tooltip(getText("option_category.common.desc"))
				.option(Config.<Boolean>option("is_mod_enable")
					.binding(true, () -> is_mod_enable, v -> is_mod_enable = v)
					.controller(TickBoxControllerBuilder::create)
					.build())
				.group(OptionGroup.createBuilder()
					.name(getText("option_group.camera_distance_adjustment"))
					.description(OptionDescription.of(getText("option_group.camera_distance_adjustment.desc")))
					.option(Config.<Integer>option("available_distance_count")
						.binding(16, () -> available_distance_count, v -> {available_distance_count = v; updateCameraDistances();})
						.controller(opt -> IntegerSliderControllerBuilder.create(opt)
							.range(2, 64)
							.step(1))
						.build())
					.option(Config.<Double>option("camera_distance_min")
						.binding(0.1d, () -> camera_distance_min, v -> {camera_distance_min = v; updateCameraDistances();})
						.controller(opt -> DoubleSliderControllerBuilder.create(opt)
							.range(0d, 2d)
							.step(0.1d))
						.build())
					.option(Config.<Double>option("camera_distance_max")
						.binding(8d, () -> camera_distance_max, v -> {camera_distance_max = v; updateCameraDistances();})
						.controller(opt -> DoubleSliderControllerBuilder.create(opt)
							.range(2d, 16d)
							.step(0.1d))
						.build())
					.build())
				.build())
			.category(ConfigCategory.createBuilder()
				.name(getText("option_category.smooth_factors"))
				.tooltip(getText("option_category.smooth_factors.desc"))
				.option(Config.<Double>option("flying_smooth_factor")
					.binding(10D, () -> -Math.log10(flying_smooth_factor), v -> flying_smooth_factor = Math.pow(10, -v))
					.controller(ConfigControllers::SMOOTH_FACTOR)
					.build())
				.group(OptionGroup.createBuilder()    // 普通模式
					.name(getText("option_group.normal_mode"))
					.description(OptionDescription.of(getText("option_group.normal_mode.desc")))
					.option(Config.<Double>option("smooth_factor_horizon")
						.binding(10D, () -> -Math.log10(normal_smooth_factor_horizon), v -> {normal_smooth_factor_horizon = Math.pow(10, -v); updateCameraOffsetScheme();})
						.controller(ConfigControllers::SMOOTH_FACTOR)
						.build())
					.option(Config.<Double>option("smooth_factor_vertical")
						.binding(10D, () -> -Math.log10(normal_smooth_factor_vertical), v -> {normal_smooth_factor_vertical = Math.pow(10, -v); updateCameraOffsetScheme();})
						.controller(ConfigControllers::SMOOTH_FACTOR)
						.build())
					.option(Config.<Double>option("camera_offset_smooth_factor")
						.binding(10D, () -> -Math.log10(normal_camera_offset_smooth_factor), v -> {normal_camera_offset_smooth_factor = Math.pow(10, -v); updateCameraOffsetScheme();})
						.controller(ConfigControllers::SMOOTH_FACTOR)
						.build())
					.option(Config.<Double>option("distance_smooth_factor")
						.binding(10D, () -> -Math.log10(normal_distance_smooth_factor), v -> {normal_distance_smooth_factor = Math.pow(10, -v); updateCameraOffsetScheme();})
						.controller(ConfigControllers::SMOOTH_FACTOR)
						.build())
					.build())
				.group(OptionGroup.createBuilder()    // 瞄准模式
					.name(getText("option_group.aiming_mode"))
					.description(OptionDescription.of(getText("option_group.aiming_mode.desc")))
					.option(Config.<Double>option("smooth_factor_horizon")
						.binding(10D, () -> -Math.log10(aiming_smooth_factor_horizon), v -> {aiming_smooth_factor_horizon = Math.pow(10, -v); updateCameraOffsetScheme();})
						.controller(ConfigControllers::SMOOTH_FACTOR)
						.build())
					.option(Config.<Double>option("smooth_factor_vertical")
						.binding(10D, () -> -Math.log10(aiming_smooth_factor_vertical), v -> {aiming_smooth_factor_vertical = Math.pow(10, -v); updateCameraOffsetScheme();})
						.controller(ConfigControllers::SMOOTH_FACTOR)
						.build())
					.option(Config.<Double>option("camera_offset_smooth_factor")
						.binding(10D, () -> -Math.log10(aiming_camera_offset_smooth_factor), v -> {aiming_camera_offset_smooth_factor = Math.pow(10, -v); updateCameraOffsetScheme();})
						.controller(ConfigControllers::SMOOTH_FACTOR)
						.build())
					.option(Config.<Double>option("distance_smooth_factor")
						.binding(10D, () -> -Math.log10(aiming_distance_smooth_factor), v -> {aiming_distance_smooth_factor = Math.pow(10, -v); updateCameraOffsetScheme();})
						.controller(ConfigControllers::SMOOTH_FACTOR)
						.build())
					.build())
				.build())
			.category(ConfigCategory.createBuilder()
				.name(getText("option_category.misc"))
				.tooltip(getText("option_category.misc.desc"))
				.option(Config.<Double>option("camera_ray_trace_length")
					.binding(256d, () -> camera_ray_trace_length, v -> camera_ray_trace_length = v)
					.controller(opt -> DoubleSliderControllerBuilder.create(opt)
						.range(10d, 1024d)
						.step(1d))
					.build())
				.build())
			.category(ConfigCategory.createBuilder()
				.name(getText("option_category.camera_offset"))
				.tooltip(getText("option_category.camera_offset.desc"))
				.option(Config.<Double>option("aiming_offset_max")//TODO deprecated
					.binding(2d, () -> aiming_offset_max, v -> aiming_offset_max = v)
					.controller(opt -> DoubleSliderControllerBuilder.create(opt)
						.range(0.5d, 5d)
						.step(0.1d))
					.build())
				.group(OptionGroup.createBuilder()    // 普通模式
					.name(getText("option_group.normal_mode"))
					.description(OptionDescription.of(getText("option_group.normal_mode.desc")))
					.option(Config.<Double>option("max_distance")
						.binding(3.6D, () -> normal_max_distance, v -> normal_max_distance = v)
						.controller(opt -> DoubleSliderControllerBuilder.create(opt)
							.range(1d, 32d)
							.step(0.25d))
						.build())
					.option(Config.<Double>option("offset_x")
						.binding(0D, () -> normal_offset_x, v -> normal_offset_x = v)
						.controller(ConfigControllers::OFFSET)
						.build())
					.option(Config.<Double>option("offset_y")
						.binding(0D, () -> normal_offset_y, v -> normal_offset_y = v)
						.controller(ConfigControllers::OFFSET)
						.build())
					.option(Config.<Double>option("offset_middle")
						.binding(0D, () -> normal_offset_middle, v -> {normal_offset_middle = v; updateCameraOffsetScheme();})
						.controller(ConfigControllers::OFFSET)
						.build())
					.build())
				.group(OptionGroup.createBuilder()    // 瞄准模式
					.name(getText("option_group.aiming_mode"))
					.description(OptionDescription.of(getText("option_group.aiming_mode.desc")))
					.option(Config.<Double>option("max_distance")
						.binding(3.6D, () -> aiming_max_distance, v -> {aiming_max_distance = v; updateCameraOffsetScheme();})
						.controller(opt -> DoubleSliderControllerBuilder.create(opt)
							.range(1d, 32d)
							.step(0.25d))
						.build())
					.option(Config.<Double>option("offset_x")
						.binding(0D, () -> aiming_offset_x, v -> {aiming_offset_x = v; updateCameraOffsetScheme();})
						.controller(ConfigControllers::OFFSET)
						.build())
					.option(Config.<Double>option("offset_y")
						.binding(0D, () -> aiming_offset_y, v -> {aiming_offset_y = v; updateCameraOffsetScheme();})
						.controller(ConfigControllers::OFFSET)
						.build())
					.option(Config.<Double>option("offset_middle")
						.binding(0D, () -> aiming_offset_middle, v -> {aiming_offset_middle = v; updateCameraOffsetScheme();})
						.controller(ConfigControllers::OFFSET)
						.build())
					.build())
				.build())
			.build(); return config.generateScreen(parent);
	}

	private static Component getText (String name) {
		return Component.translatable(ThirdPersonMod.MOD_ID + "." + name);
	}

	/**
	 * 加载模组时初始化
	 * <p>
	 * 尝试加载配置文件，如果出错则记录错误信息
	 */
	public static void init () {
		try {
			load();
		} catch (Exception e) {
			LOGGER.error("Error loading config", e);
			// 保存配置
			save();
		}
	}

	public static void load () {
		GSON.load(); onLoad();
	}

	public static void save () {
		GSON.save();
	}

	public static void onLoad () {
		updateCameraDistances(); updateCameraOffsetScheme();
	}

	/**
	 * 更新相机到玩家的距离的可调挡位们
	 */
	public static void updateCameraDistances () {
		distanceMonoList = MonoList.of(available_distance_count, camera_distance_min, camera_distance_max, i -> i * i, Math::sqrt);
	}

	/**
	 * 更新相机偏移方案
	 */
	@Deprecated
	public static void updateCameraOffsetScheme () {
		// maxDist, offsetValue
		CameraOffsetScheme scheme = CameraOffsetScheme.create(normal_max_distance, normal_offset_x, normal_offset_y, aiming_max_distance, aiming_offset_x, aiming_offset_y);
		// Normal mode //
		scheme.normalMode.setDistanceSmoothFactor(normal_distance_smooth_factor)
			.setOffsetSmoothFactor(new Vec2((float)normal_camera_offset_smooth_factor, (float)normal_camera_offset_smooth_factor))
			.setEyeSmoothFactor(new Vec3(normal_smooth_factor_horizon, normal_smooth_factor_vertical, normal_smooth_factor_horizon))
			.setMiddleOffsetValue(normal_offset_middle);
		// Aiming mode //
		scheme.aimingMode.setDistanceSmoothFactor(aiming_distance_smooth_factor)
			.setOffsetSmoothFactor(new Vec2((float)aiming_camera_offset_smooth_factor, (float)aiming_camera_offset_smooth_factor))
			.setEyeSmoothFactor(new Vec3(aiming_smooth_factor_horizon, aiming_smooth_factor_vertical, aiming_smooth_factor_horizon))
			.setMiddleOffsetValue(aiming_offset_middle);
		// apply
		cameraOffsetScheme = scheme;
	}

	@Deprecated
	public static void loadFromCameraOffsetScheme () {
		// Normal mode //
		normal_max_distance                = cameraOffsetScheme.normalMode.getMaxDistance(); normal_offset_x = cameraOffsetScheme.normalMode.getOffsetValue().x; normal_offset_y = cameraOffsetScheme.normalMode.getOffsetValue().y;
		normal_smooth_factor_horizon       = cameraOffsetScheme.normalMode.getEyeSmoothFactor().x; normal_smooth_factor_vertical = cameraOffsetScheme.normalMode.getEyeSmoothFactor().y;
		normal_camera_offset_smooth_factor = cameraOffsetScheme.normalMode.getOffsetSmoothFactor().x; normal_distance_smooth_factor = cameraOffsetScheme.normalMode.getDistanceSmoothFactor();
		normal_offset_middle               = cameraOffsetScheme.normalMode.getMiddleOffsetValue();
		// Aiming mode //
		aiming_max_distance                = cameraOffsetScheme.aimingMode.getMaxDistance(); aiming_offset_x = cameraOffsetScheme.aimingMode.getOffsetValue().x; aiming_offset_y = cameraOffsetScheme.aimingMode.getOffsetValue().y;
		aiming_smooth_factor_horizon       = cameraOffsetScheme.aimingMode.getEyeSmoothFactor().x; aiming_smooth_factor_vertical = cameraOffsetScheme.aimingMode.getEyeSmoothFactor().y;
		aiming_camera_offset_smooth_factor = cameraOffsetScheme.aimingMode.getOffsetSmoothFactor().x; aiming_distance_smooth_factor = cameraOffsetScheme.aimingMode.getDistanceSmoothFactor();
		aiming_offset_middle               = cameraOffsetScheme.aimingMode.getMiddleOffsetValue();
	}

	@SuppressWarnings("unused")
	public static Option.Builder<Boolean> HIDDEN_OPTION = Option.<Boolean>createBuilder()
		.name(getText("option.projectile_auto_aim"))
		.description(OptionDescription.of(getText("option.projectile_auto_aim.desc")))
		.binding(true, () -> false, v -> {})
		.controller(TickBoxControllerBuilder::create);
}
