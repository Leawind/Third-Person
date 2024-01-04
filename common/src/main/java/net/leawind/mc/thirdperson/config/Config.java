package net.leawind.mc.thirdperson.config;
//import com.google.gson.*;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
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
	public static final                       Logger                  LOGGER                                    = LoggerFactory.getLogger(ThirdPersonMod.MOD_ID);
	// 配置文件路径
	public static final                       Path                    CONFIG_FILE_PATH                          = ExpectPlatform.getConfigDirectory()
		.resolve(ThirdPersonMod.MOD_ID + ".json");
	public static final                       ConfigInstance<Config>  GSON                                      = GsonConfigInstance.createBuilder(Config.class)
		.setPath(CONFIG_FILE_PATH)
		.overrideGsonBuilder(new GsonBuilder().setPrettyPrinting()
			.serializeNulls()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.registerTypeHierarchyAdapter(Component.class, new Component.Serializer())
			.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
			.registerTypeHierarchyAdapter(Color.class, new GsonConfigInstance.ColorTypeAdapter())
			.registerTypeHierarchyAdapter(Config.class, new ConfigAdapter()))
		.build();
	// ============================================================ //
	@ConfigEntry public static                boolean                 is_mod_enable                             = true;
	@ConfigEntry public static                boolean                 player_rotate_with_camera_when_not_aiming = false;
	@ConfigEntry public static                boolean                 render_crosshair_when_not_aiming          = true;
	@ConfigEntry public static                boolean                 render_crosshair_when_aiming              = true;
	@ConfigEntry public static                boolean                 rotate_to_moving_direction                = true;
	@ConfigEntry public static                boolean                 lock_camera_pitch_angle                   = false;
	@ConfigEntry public static                boolean                 turn_with_camera_when_enter_first_person  = true;
	@ConfigEntry public static                boolean                 player_fade_out_enabled                   = true;
	@ConfigEntry public static                double                  camera_ray_trace_length                   = 256;
	@ConfigEntry public static                boolean                 auto_rotate_interacting                   = true;
	@ConfigEntry public static                boolean                 rotate_interacting_type                   = true;
	@ConfigEntry private static               int                     available_distance_count                  = 16;
	@ConfigEntry private static               double                  camera_distance_min                       = 0.5;
	@ConfigEntry private static               double                  camera_distance_max                       = 8;
	@ConfigEntry public static                double                  flying_smooth_factor                      = 0.5;
	@ConfigEntry public static                double                  adjusting_camera_offset_smooth_factor     = 0.100;
	@ConfigEntry public static                double                  adjusting_distance_smooth_factor          = 0.100;
	@ConfigEntry private static               double                  normal_smooth_factor_horizon              = 0.500;
	@ConfigEntry private static               double                  normal_smooth_factor_vertical             = 0.500;
	@ConfigEntry private static               double                  normal_camera_offset_smooth_factor        = 0.500;
	@ConfigEntry private static               double                  normal_distance_smooth_factor             = 0.640;
	@ConfigEntry private static               double                  aiming_smooth_factor_horizon              = 0.002;
	@ConfigEntry private static               double                  aiming_smooth_factor_vertical             = 0.002;
	@ConfigEntry private static               double                  aiming_camera_offset_smooth_factor        = 0.100;
	@ConfigEntry private static               double                  aiming_distance_smooth_factor             = 0.110;
	@ConfigEntry private static               double                  normal_max_distance                       = 2.5;
	@ConfigEntry private static               double                  normal_offset_x                           = -0.28;
	@ConfigEntry private static               double                  normal_offset_y                           = 0.31;
	@ConfigEntry private static               double                  normal_offset_center                      = 0.24D;
	@ConfigEntry private static               double                  aiming_max_distance                       = 0.89;
	@ConfigEntry private static               double                  aiming_offset_x                           = -0.47;
	@ConfigEntry private static               double                  aiming_offset_y                           = -0.09;
	@ConfigEntry private static               double                  aiming_offset_center                      = 0.48;
	// ============================================================ //
	public static                             MonoList                distanceMonoList;
	public static                             CameraOffsetScheme      cameraOffsetScheme                        = CameraOffsetScheme.DEFAULT;
	// ============================================================ //
	@SuppressWarnings("unused") public static Option.Builder<Boolean> HIDDEN_OPTION_496                         = Option.<Boolean>createBuilder()
		.name(ConfigBuilders.getText("option.projectile_auto_aim"))
		.description(OptionDescription.of(ConfigBuilders.getText("option.projectile_auto_aim.desc")))
		.binding(true, () -> false, v -> {})
		.controller(TickBoxControllerBuilder::create);

	/**
	 * 获取配置屏幕
	 * <p>
	 * 提供给 ModMenu
	 */
	public static Screen getConfigScreen (Screen parent) {
		return YetAnotherConfigLib.createBuilder()
			.title(ConfigBuilders.getText("text.title"))
			.save(Config::save)
			.category(ConfigCategory.createBuilder()
				.name(ConfigBuilders.getText("option_category.common"))
				.tooltip(ConfigBuilders.getText("option_category.common.desc"))
				.option(ConfigBuilders.<Boolean>option("is_mod_enable")
					.binding(true, () -> is_mod_enable, v -> is_mod_enable = v)
					.controller(TickBoxControllerBuilder::create)
					.build())
				.option(ConfigBuilders.<Boolean>option("lock_camera_pitch_angle")
					.binding(false, () -> lock_camera_pitch_angle, v -> lock_camera_pitch_angle = v)
					.controller(TickBoxControllerBuilder::create)
					.build())
				.group(ConfigBuilders.group("player_rotation")
					.option(ConfigBuilders.<Boolean>option("player_rotate_with_camera_when_not_aiming")
						.binding(false, () -> player_rotate_with_camera_when_not_aiming, v -> player_rotate_with_camera_when_not_aiming = v)
						.controller(TickBoxControllerBuilder::create)
						.build())
					.option(ConfigBuilders.<Boolean>option("rotate_to_moving_direction")
						.binding(true, () -> rotate_to_moving_direction, v -> rotate_to_moving_direction = v)
						.controller(TickBoxControllerBuilder::create)
						.build())
					.build())
				.group(ConfigBuilders.group("camera_distance_adjustment")
					.option(ConfigBuilders.<Integer>option("available_distance_count")
						.binding(16, () -> available_distance_count, v -> {
							available_distance_count = v; updateCameraDistances();
						})
						.controller(opt -> IntegerSliderControllerBuilder.create(opt)
							.range(3, 64)    // At least 2
							.step(1))
						.build())
					.option(ConfigBuilders.<Double>option("camera_distance_min")
						.binding(0.5d, () -> camera_distance_min, v -> {
							camera_distance_min = v; updateCameraDistances();
						})
						.controller(opt -> DoubleSliderControllerBuilder.create(opt)
							.range(0.3D, 16D)
							.step(0.1D))
						.build())
					.option(ConfigBuilders.<Double>option("camera_distance_max")
						.binding(8d, () -> camera_distance_max, v -> {
							camera_distance_max = v; updateCameraDistances();
						})
						.controller(opt -> DoubleSliderControllerBuilder.create(opt)
							.range(2d, 16d)
							.step(0.1d))
						.build())
					.build())
				.build())
			.category(ConfigCategory.createBuilder()
				.name(ConfigBuilders.getText("option_category.misc"))
				.tooltip(ConfigBuilders.getText("option_category.misc.desc"))
				.option(ConfigBuilders.<Double>option("camera_ray_trace_length")
					.binding(256d, () -> camera_ray_trace_length, v -> camera_ray_trace_length = v)
					.controller(opt -> DoubleSliderControllerBuilder.create(opt)
						.range(10d, 1024d)
						.step(1d))
					.build())
				.option(ConfigBuilders.<Boolean>option("turn_with_camera_when_enter_first_person")
					.binding(true, () -> turn_with_camera_when_enter_first_person, v -> turn_with_camera_when_enter_first_person = v)
					.controller(TickBoxControllerBuilder::create)
					.build())
				.group(ConfigBuilders.group("behavior_interacting")
					.option(ConfigBuilders.<Boolean>option("auto_rotate_interacting")
						.binding(true, () -> auto_rotate_interacting, v -> auto_rotate_interacting = v)
						.controller(TickBoxControllerBuilder::create)
						.build())
					.option(ConfigBuilders.<Boolean>option("rotate_interacting_type")
						.binding(true, () -> rotate_interacting_type, v -> rotate_interacting_type = v)
						.controller(opt -> BooleanControllerBuilder.create(opt)
							.valueFormatter(v -> ConfigBuilders.getText("option.rotate_interacting_type." + (v ? "turn_to_crosshair": "turn_with_camera"))))
						.build())
					.build())
				.group(ConfigBuilders.group("player_fade_out")
					.option(ConfigBuilders.<Boolean>option("player_fade_out_enabled")
						.binding(true, () -> player_fade_out_enabled, v -> player_fade_out_enabled = v)
						.controller(TickBoxControllerBuilder::create)
						.build())
					.build())
				.group(ConfigBuilders.group("crosshair")
					.option(ConfigBuilders.<Boolean>option("render_crosshair_when_not_aiming")
						.binding(true, () -> render_crosshair_when_not_aiming, v -> render_crosshair_when_not_aiming = v)
						.controller(TickBoxControllerBuilder::create)
						.build())
					.option(ConfigBuilders.<Boolean>option("render_crosshair_when_aiming")
						.binding(true, () -> render_crosshair_when_aiming, v -> render_crosshair_when_aiming = v)
						.controller(TickBoxControllerBuilder::create)
						.build())
					.build())
				.build())
			.category(ConfigCategory.createBuilder()
				.name(ConfigBuilders.getText("option_category.smooth_factors"))
				.tooltip(ConfigBuilders.getText("option_category.smooth_factors.desc"))
				.option(ConfigBuilders.SMOOTH_FACTOR_OPTION(0.5, "flying_smooth_factor", () -> flying_smooth_factor, v -> flying_smooth_factor = v))
				.group(ConfigBuilders.group("adjusting_camera")
					.option(ConfigBuilders.SMOOTH_FACTOR_OPTION(0.100, "adjusting_camera_offset_smooth_factor", () -> adjusting_camera_offset_smooth_factor, v -> adjusting_camera_offset_smooth_factor = v))
					.option(ConfigBuilders.SMOOTH_FACTOR_OPTION(0.100, "adjusting_distance_smooth_factor", () -> adjusting_distance_smooth_factor, v -> adjusting_distance_smooth_factor = v))
					.build())
				.group(ConfigBuilders.group("normal_mode")
					.option(ConfigBuilders.SMOOTH_FACTOR_OPTION(0.500, "smooth_factor_horizon", () -> normal_smooth_factor_horizon, v -> normal_smooth_factor_horizon = v))
					.option(ConfigBuilders.SMOOTH_FACTOR_OPTION(0.500, "smooth_factor_vertical", () -> normal_smooth_factor_vertical, v -> normal_smooth_factor_vertical = v))
					.option(ConfigBuilders.SMOOTH_FACTOR_OPTION(0.500, "camera_offset_smooth_factor", () -> normal_camera_offset_smooth_factor, v -> normal_camera_offset_smooth_factor = v))
					.option(ConfigBuilders.SMOOTH_FACTOR_OPTION(0.640, "distance_smooth_factor", () -> normal_distance_smooth_factor, v -> normal_distance_smooth_factor = v))
					.build())
				.group(ConfigBuilders.group("aiming_mode")
					.option(ConfigBuilders.SMOOTH_FACTOR_OPTION(0.002, "smooth_factor_horizon", () -> aiming_smooth_factor_horizon, v -> aiming_smooth_factor_horizon = v))
					.option(ConfigBuilders.SMOOTH_FACTOR_OPTION(0.002, "smooth_factor_vertical", () -> aiming_smooth_factor_vertical, v -> aiming_smooth_factor_vertical = v))
					.option(ConfigBuilders.SMOOTH_FACTOR_OPTION(0.100, "camera_offset_smooth_factor", () -> aiming_camera_offset_smooth_factor, v -> aiming_camera_offset_smooth_factor = v))
					.option(ConfigBuilders.SMOOTH_FACTOR_OPTION(0.110, "distance_smooth_factor", () -> aiming_distance_smooth_factor, v -> aiming_distance_smooth_factor = v))
					.build())
				.build())
			.category(ConfigCategory.createBuilder()
				.name(ConfigBuilders.getText("option_category.camera_offset"))
				.tooltip(ConfigBuilders.getText("option_category.camera_offset.desc"))
				.group(ConfigBuilders.group("normal_mode")
					.option(ConfigBuilders.<Double>option("max_distance")
						.binding(2.50D, () -> normal_max_distance, v -> normal_max_distance = v)
						.controller(opt -> DoubleSliderControllerBuilder.create(opt)
							.range(1d, 32d)
							.step(0.2d))
						.build())
					.option(ConfigBuilders.<Double>option("offset_x")
						.binding(-0.28d, () -> normal_offset_x, v -> normal_offset_x = v)
						.controller(ConfigBuilders::OFFSET_CONTROLLER)
						.build())
					.option(ConfigBuilders.<Double>option("offset_y")
						.binding(0.31D, () -> normal_offset_y, v -> normal_offset_y = v)
						.controller(ConfigBuilders::OFFSET_CONTROLLER)
						.build())
					.option(ConfigBuilders.<Double>option("offset_center")
						.binding(0.24D, () -> normal_offset_center, v -> {
							normal_offset_center = v; updateCameraOffsetScheme();
						})
						.controller(ConfigBuilders::OFFSET_CONTROLLER)
						.build())
					.build())
				.group(ConfigBuilders.group("aiming_mode")
					.option(ConfigBuilders.<Double>option("max_distance")
						.binding(0.89D, () -> aiming_max_distance, v -> {
							aiming_max_distance = v; updateCameraOffsetScheme();
						})
						.controller(opt -> DoubleSliderControllerBuilder.create(opt)
							.range(1d, 32d)
							.step(0.2d))
						.build())
					.option(ConfigBuilders.<Double>option("offset_x")
						.binding(-0.47, () -> aiming_offset_x, v -> {
							aiming_offset_x = v; updateCameraOffsetScheme();
						})
						.controller(ConfigBuilders::OFFSET_CONTROLLER)
						.build())
					.option(ConfigBuilders.<Double>option("offset_y")
						.binding(-0.09D, () -> aiming_offset_y, v -> {
							aiming_offset_y = v; updateCameraOffsetScheme();
						})
						.controller(ConfigBuilders::OFFSET_CONTROLLER)
						.build())
					.option(ConfigBuilders.<Double>option("offset_center")
						.binding(0.48D, () -> aiming_offset_center, v -> {
							aiming_offset_center = v; updateCameraOffsetScheme();
						})
						.controller(ConfigBuilders::OFFSET_CONTROLLER)
						.build())
					.build())
				.build())
			.build()
			.generateScreen(parent);
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
	public static void updateCameraOffsetScheme () {
		// maxDist, offsetValue
		CameraOffsetScheme scheme = CameraOffsetScheme.create(normal_max_distance, normal_offset_x, normal_offset_y, aiming_max_distance, aiming_offset_x, aiming_offset_y);
		// Normal mode //
		scheme.normalMode.setDistanceSmoothFactor(normal_distance_smooth_factor)
			.setOffsetSmoothFactor(normal_camera_offset_smooth_factor)
			.setEyeSmoothFactor(normal_smooth_factor_horizon, normal_smooth_factor_vertical)
			.setCenterOffsetRatio(normal_offset_center);
		// Aiming mode //
		scheme.aimingMode.setDistanceSmoothFactor(aiming_distance_smooth_factor)
			.setOffsetSmoothFactor(aiming_camera_offset_smooth_factor)
			.setEyeSmoothFactor(aiming_smooth_factor_horizon, aiming_smooth_factor_vertical)
			.setCenterOffsetRatio(aiming_offset_center);
		// apply
		cameraOffsetScheme = scheme;
	}

	/**
	 * 从相机偏移方案对象加载配置项
	 * <p>
	 * 当玩家调整偏移量等选项时，会直接修改 CameraOffsetScheme 对象，而不是直接修改 Config
	 * <p>
	 * 所以修改完后需要立即将改动应用到 Config
	 */
	public static void loadFromCameraOffsetScheme () {
		// Normal mode //
		normal_max_distance  = cameraOffsetScheme.normalMode.getMaxDistance();normal_offset_x = cameraOffsetScheme.normalMode.getSideOffsetRatio().x;normal_offset_y = cameraOffsetScheme.normalMode.getSideOffsetRatio().y;
		normal_offset_center = cameraOffsetScheme.normalMode.getCenterOffsetRatio();
		// Aiming mode //
		aiming_max_distance  = cameraOffsetScheme.aimingMode.getMaxDistance();aiming_offset_x = cameraOffsetScheme.aimingMode.getSideOffsetRatio().x;aiming_offset_y = cameraOffsetScheme.aimingMode.getSideOffsetRatio().y;
		aiming_offset_center = cameraOffsetScheme.aimingMode.getCenterOffsetRatio();
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
			save(); onLoad();
		}
	}

	/**
	 * 加载配置文件
	 *
	 * @throws JsonSyntaxException 如果配置文件格式不正确则抛出错误
	 */
	public static void load () throws JsonSyntaxException {
		GSON.load(); onLoad();
	}

	/**
	 * 保存配置文件
	 */
	public static void save () {
		GSON.save();
	}

	/**
	 * 加载完成时调用
	 * <p>
	 * 更新次生配置
	 */
	public static void onLoad () {
		updateCameraDistances(); updateCameraOffsetScheme();
	}

	public enum BehaviorInterecting implements NameableEnum {
		TURN_TO_HIT_RESULT("turn_to_hit_result"),
		TURN_WITH_CAMERA("turn_with_camera"),
		NO_TURN("no_turn");
		public final String name;

		BehaviorInterecting (String name) {
			this.name = name;
		}

		@Override
		public Component getDisplayName () {
			return Component.translatable(ThirdPersonMod.MOD_ID + ".behavior_interacting." + name);
		}
	}
}
