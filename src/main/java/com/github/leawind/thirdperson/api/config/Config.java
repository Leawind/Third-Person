package com.github.leawind.thirdperson.api.config;

import com.github.leawind.thirdperson.core.cameraoffset.CameraOffsetScheme;
import com.github.leawind.thirdperson.minecraft.logic.resources.ItemPredicateManager;
import com.github.leawind.thirdperson.utils.ItemPredicateUtil;
import com.github.leawind.thirdperson.utils.math.monolist.MonoList;
import com.github.leawind.thirdperson.utils.math.monolist.StaticMonoList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractConfig 中包含了用户可以直接修改的配置项及默认值。
 *
 * <p>但要在模组中使用这些配置项，还需要进行进一步的处理。
 */
public class Config extends AbstractConfig {
  public static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

  public static final @NotNull Config DEFAULTS = new Config();

  private final @NotNull CameraOffsetScheme cameraOffsetScheme = new CameraOffsetScheme(this);

  private final @NotNull Set<Predicate<ItemStack>> holdToAimItemPredicates = new HashSet<>();
  private final @NotNull Set<Predicate<ItemStack>> useToAimItemPredicates = new HashSet<>();
  private final @NotNull Set<Predicate<ItemStack>> useToFirstPersonItemPredicates = new HashSet<>();

  private @Nullable MonoList distanceMonoList;

  public Config() {
    update();
  }

  /** 在配置项发生变化时更新 */
  public void update() {
    // 确保不存在非法值
    if (normal_rotate_mode == null) {
      normal_rotate_mode = DEFAULTS.normal_rotate_mode;
    }

    updateDistancesMonoList();
    reparseItemPredicates();
  }

  /** 更新相机到玩家的距离的可调挡位们 */
  public void updateDistancesMonoList() {
    LOGGER.debug("Updating distances mono list");
    distanceMonoList =
        StaticMonoList.of(
            available_distance_count,
            camera_distance_min,
            camera_distance_max,
            i -> i * i,
            Math::sqrt);
  }

  /**
   * @see ItemPredicateManager#apply
   */
  public void reparseItemPredicates() {
    holdToAimItemPredicates.clear();
    useToAimItemPredicates.clear();
    useToFirstPersonItemPredicates.clear();

    holdToAimItemPredicates.addAll(ItemPredicateUtil.parseAll(hold_to_aim_item_patterns));
    useToAimItemPredicates.addAll(ItemPredicateUtil.parseAll(use_to_aim_item_patterns));
    useToFirstPersonItemPredicates.addAll(ItemPredicateUtil.parseAll(use_to_first_person_patterns));
  }

  public @NotNull Set<Predicate<ItemStack>> getHoldToAimItemPredicates() {
    return holdToAimItemPredicates;
  }

  public @NotNull Set<Predicate<ItemStack>> getUseToAimItemPredicates() {
    return useToAimItemPredicates;
  }

  public @NotNull Set<Predicate<ItemStack>> getUseToFirstPersonItemPredicates() {
    return useToFirstPersonItemPredicates;
  }

  public @NotNull CameraOffsetScheme getCameraOffsetScheme() {
    return cameraOffsetScheme;
  }

  public @NotNull MonoList getDistanceMonoList() {
    return Objects.requireNonNull(distanceMonoList);
  }
}
