package com.github.leawind.thirdperson.util;

import com.github.leawind.thirdperson.ThirdPerson;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.leawind.inventory.event.EventEmitter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemPredicateUtil {

  /** 用来解析物品谓词，语法和命令中的物品谓词参数相同（例如 {@code /clear } 命令） */
  private static @Nullable ItemPredicateArgument ITEM_PREDICATE_ARGUMENT = null;

  public static final EventEmitter<Void> ON_INITIALIZED = new EventEmitter<>();

  /**
   * 在 Commands 构造函数中Mixin，取得context，并调用此方法，得到 ItemPredicateArgument，可以用它来解析物品谓词。
   *
   * @param context {@link Commands} 构造函数中的 context 对象
   */
  public static void init(CommandBuildContext context) {
    ITEM_PREDICATE_ARGUMENT = ItemPredicateArgument.itemPredicate(context);
    ON_INITIALIZED.emit();
  }

  public static boolean isInitialized() {
    return ITEM_PREDICATE_ARGUMENT != null;
  }

  /**
   * 使用 {@link ItemPredicateArgument} 解析物品谓词
   *
   * @param pattern 物品谓词表达式，语法和命令中的物品谓词参数相同（例如 {@code /clear } 命令）
   * @return 物品谓词
   * @throws IllegalStateException {@link #ITEM_PREDICATE_ARGUMENT} 还未初始化
   * @throws CommandSyntaxException 语法错误
   */
  public static Predicate<ItemStack> parse(String pattern)
      throws IllegalStateException, CommandSyntaxException {
    if (ITEM_PREDICATE_ARGUMENT == null) {
      throw new IllegalStateException("ItemPredicateArgument has not been initialized yet.");
    }
    return ITEM_PREDICATE_ARGUMENT.parse(new StringReader(pattern));
  }

  /**
   * 解析所有物品谓词表达式，跳过语法错误的，返回物品谓词集合
   *
   * @throws IllegalStateException {@link #ITEM_PREDICATE_ARGUMENT} 还未初始化
   */
  public static Collection<Predicate<ItemStack>> parseAll(@NotNull Iterable<String> patterns)
      throws IllegalStateException {
    var set = new HashSet<Predicate<ItemStack>>();
    for (var pattern : patterns) {
      try {
        set.add(parse(pattern));
      } catch (CommandSyntaxException e) {
        ThirdPerson.LOGGER.error(
            "Skip invalid item pattern: {}, because {}", pattern, e.getMessage());
      }
    }
    return set;
  }

  public static @NotNull Optional<Component> supplyError(String pattern) {
    try {
      parse(pattern);
      return Optional.empty();
    } catch (IllegalStateException e) {
      return Optional.empty();
    } catch (CommandSyntaxException e) {
      return Optional.of(Component.literal(e.getMessage()));
    }
  }

  @SafeVarargs
  public static boolean anyMatches(
      @NotNull ItemStack itemStack, Iterable<Predicate<ItemStack>> @NotNull ... predicatesList) {
    if (itemStack.isEmpty()) {
      return false;
    }
    for (var predicates : predicatesList) {
      for (var predicate : predicates) {
        if (predicate.test(itemStack)) {
          return true;
        }
      }
    }
    return false;
  }
}