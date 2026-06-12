package com.github.leawind.thirdperson.utils;

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
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ItemPredicateUtil {
  public static final Logger LOGGER = LoggerFactory.getLogger(ItemPredicateUtil.class);

  /** 用来解析物品谓词，语法和命令中的物品谓词参数相同（例如 {@code /clear } 命令） */
  private static final ItemPredicateArgument ITEM_PREDICATE_ARGUMENT =
      ItemPredicateArgument.itemPredicate(
          Commands.createValidationContext(VanillaRegistries.createLookup()));

  public static final EventEmitter<Void> ON_INITIALIZED = new EventEmitter<>();

  /**
   * 在 Commands 构造函数中Mixin，取得context，并调用此方法，得到 ItemPredicateArgument，可以用它来解析物品谓词。
   *
   * @param context {@link Commands} 构造函数中的 context 对象
   */
  public static void init(CommandBuildContext context) {
    ON_INITIALIZED.emit();
  }

  public static boolean isInitialized() {
    return true;
  }

  /// 使用 {@link ItemPredicateArgument} 解析物品谓词
  ///
  /// @param pattern 物品谓词表达式，语法和命令中的物品谓词参数相同（例如 {@code /clear } 命令）
  /// @return 物品谓词
  /// @throws CommandSyntaxException 语法错误
  public static Predicate<ItemStack> parse(String pattern)
      throws IllegalStateException, CommandSyntaxException {
    return ITEM_PREDICATE_ARGUMENT.parse(new StringReader(pattern));
  }

  /// 解析所有物品谓词表达式，跳过语法错误的，返回物品谓词集合
  public static Collection<Predicate<ItemStack>> parseAll(Iterable<String> patterns)
      throws IllegalStateException {
    var set = new HashSet<Predicate<ItemStack>>();
    for (var pattern : patterns) {
      try {
        set.add(parse(pattern));
      } catch (CommandSyntaxException e) {
        LOGGER.error("Skip invalid item pattern: {}, because {}", pattern, e.getMessage());
      }
    }
    return set;
  }

  public static Optional<Component> supplyError(String pattern) {
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
      ItemStack itemStack, Iterable<Predicate<ItemStack>>... predicatesList) {
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
