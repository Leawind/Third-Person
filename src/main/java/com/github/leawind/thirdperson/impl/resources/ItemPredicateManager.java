package com.github.leawind.thirdperson.impl.resources;

import com.github.leawind.thirdperson.api.ThirdPerson;
import com.github.leawind.thirdperson.impl.config.Config;
import com.github.leawind.thirdperson.utils.ItemPredicateUtil;
import com.github.leawind.thirdperson.utils.annotation.VersionSensitive;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 物品谓词资源管理器
 *
 * <ul>
 *   <li>物品谓词（ItemPredicate）是指 {@link Predicate<ItemStack>}对象，它代表一种规则，可以判断一个物品栈（{@link
 *       ItemStack}）是否符合其规则
 *   <li>物品模式（ItemPattern）是字符串，可以用{@link ItemPredicateUtil#parse(String)}将其解析为物品谓词
 * </ul>
 *
 * 资源包中的物品谓词集合采用json格式存储
 *
 * <p>重载资源包时，mc会调用{@link ItemPredicateManager#apply}方法处理读取到的json数据
 *
 * @see ItemPredicateUtil
 * @see SplashManager
 */
@VersionSensitive("SimpleJsonResourceReloadListener may not exist in other mc version")
public class ItemPredicateManager extends SimpleJsonResourceReloadListener<JsonElement> {
  public static final String ID = "item_patterns";

  private static final String SET_HOLD_TO_AIM = "hold_to_aim";
  private static final String SET_USE_TO_AIM = "use_to_aim";
  private static final String SET_USE_TO_FIRST_PERSON = "use_to_first_person";

  public final Set<String> holdToAimItemPatterns = new HashSet<>();
  public final Set<String> useToAimItemPatterns = new HashSet<>();
  public final Set<String> useToFirstPersonItemPatterns = new HashSet<>();

  /** 玩家手持（主手或副手）这些物品时，进入瞄准模式 */
  public final Set<Predicate<ItemStack>> holdToAimItemPredicates = new HashSet<>();

  /** 玩家使用这些物品时，进入瞄准模式 */
  public final Set<Predicate<ItemStack>> useToAimItemPredicates = new HashSet<>();

  /** 玩家使用这些物品时，会暂时进入第一人称 */
  public final Set<Predicate<ItemStack>> useToFirstPersonItemPredicates = new HashSet<>();

  public ItemPredicateManager() {
    super(ExtraCodecs.JSON, FileToIdConverter.json(ID));
  }

  /**
   * 重载资源包时会调用此方法，处理资源包中的json数据
   *
   * @param map 资源地址与json数据的映射表
   * @param resourceManager {@link MultiPackResourceManager}的实例
   * @see Config#reparseItemPredicates()
   */
  @Override
  public void apply(
      @NotNull Map<Identifier, JsonElement> map,
      ResourceManager resourceManager,
      ProfilerFiller profile) {
    holdToAimItemPatterns.clear();
    useToAimItemPatterns.clear();
    useToFirstPersonItemPatterns.clear();

    map.forEach(
        (resourceLocation, jsonElement) -> {
          var obj = jsonElement.getAsJsonArray();
          var resourcePath = resourceLocation.getPath().split("/");

          if (resourcePath.length >= 2) {
            var resourceSetName = resourcePath[0];
            switch (resourceSetName) {
              case SET_HOLD_TO_AIM -> append(holdToAimItemPatterns, obj);
              case SET_USE_TO_AIM -> append(useToAimItemPatterns, obj);
              case SET_USE_TO_FIRST_PERSON -> append(useToFirstPersonItemPatterns, obj);
            }
          }
        });
    reparse();
  }

  private void append(@NotNull Set<String> patterns, @NotNull JsonArray arr) {
    arr.forEach(
        ele -> {
          try {
            var pattern = ele.getAsString();
            patterns.add(pattern);
          } catch (Throwable e) {
            ThirdPerson.LOGGER.warn(e.getMessage());
          }
        });
  }

  /**
   * Parse patterns in {@link #holdToAimItemPatterns} and update {@link #holdToAimItemPredicates}
   *
   * <p>If {@link ItemPredicateUtil} is not initialized, it will run after initialization
   */
  public void reparse() {
    if (ItemPredicateUtil.isInitialized()) {
      reparseImmediately();
    } else {
      ItemPredicateUtil.ON_INITIALIZED.once(
          "Item predicates in resource", e -> reparseImmediately());
    }
  }

  private void reparseImmediately() {
    holdToAimItemPredicates.clear();
    useToAimItemPredicates.clear();
    useToFirstPersonItemPredicates.clear();

    holdToAimItemPredicates.addAll(ItemPredicateUtil.parseAll(holdToAimItemPatterns));
    useToAimItemPredicates.addAll(ItemPredicateUtil.parseAll(useToAimItemPatterns));
    useToFirstPersonItemPredicates.addAll(ItemPredicateUtil.parseAll(useToFirstPersonItemPatterns));
  }
}
