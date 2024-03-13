package net.leawind.mc.util.itempattern;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 物品模式
 * <p>
 * 可用于根据物品 id 和 nbt 标签匹配物品
 * <p>
 * 使用 {@link ItemPattern#of(String, String)} 或 {@link ItemPattern#of(String, String, String)} 构建实例
 * <p>
 * 示例：
 * <pre>
 * // 获取玩家手持物品
 * ItemStack   item        = Minecraft.getInstance().player.getMainHandItem();
 * // 创建物品模式实例
 * ItemPattern pattern     = ItemPattern.of("crossbow{Charged:1b}");	// 已装填的弩
 * // 测试物品是否匹配该模式
 * boolean     matchResult = pattern.match(item);
 * </pre>
 */
@SuppressWarnings("unused")
public interface ItemPattern {
	/**
	 * 正规的物品描述符
	 */
	Pattern     RGX_REGULAR_ID   = Pattern.compile("^item\\.[a-z0-9_]+\\.[a-z0-9_]+$");
	/**
	 * 纯物品名，没有命名空间。
	 * <p>
	 * 例如 bread
	 */
	Pattern     RGX_PURE_ID      = Pattern.compile("^[a-z0-9_]+$");
	/**
	 * <命名空间>:<物品ID>
	 * <p>
	 * 例如 minecraft:apple
	 */
	Pattern     RGX_NAMESPACE_ID = Pattern.compile("^[a-z0-9_]+[.:][a-z0-9_]+$");
	/**
	 * 宽松规则的物品ID加上NBT标签
	 * <p>
	 * 例如 crossbow{Charged:1b}
	 */
	Pattern     RGX_ID_NBT       = Pattern.compile("^[a-z0-9.:_]+\\{.*}$");
	/**
	 * 宽松规则的物品ID
	 */
	Pattern     RGX_ID           = Pattern.compile("^[a-z0-9.:_]+$");
	/**
	 * NBT标签表达式
	 */
	Pattern     RGX_NBT          = Pattern.compile("^\\{.*}$");
	/**
	 * 匹配一切物品
	 */
	ItemPattern ANY              = of("minecraft", null, null);

	@SafeVarargs
	static boolean anyMatch (@NotNull ItemStack itemStack, Iterable<ItemPattern> @NotNull ... itemPatternsList) {
		if (itemStack.isEmpty()) {
			return false;
		}
		for (Iterable<ItemPattern> patterns: itemPatternsList) {
			for (ItemPattern ip: patterns) {
				if (ip.match(itemStack)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 如果规则表达式不合语法，则提供相关错误信息
	 *
	 * @param expression 表达式
	 * @return 错误信息，空值表示没有错误
	 */
	static @NotNull Optional<Component> supplyError (@Nullable String expression) {
		try {
			of("minecraft", expression);
			return Optional.empty();
		} catch (IllegalArgumentException e) {
			return Optional.of(Component.literal(e.getMessage()));
		}
	}

	/**
	 * 由宽松规则的表达式创建物品模式对象。
	 * <p>
	 * 表达式示例：
	 * <p>
	 * snowball
	 * <p>
	 * crossbow{Charged:1b}
	 * <p>
	 * minecraft:crossbow{Charged:1b}
	 * <p>
	 * item.minecraft.crossbow{Charged:1b}
	 */
	@Contract("_,_ -> new")
	static @NotNull ItemPattern of (@NotNull String defaultNamespace, @Nullable String expression) {
		if (expression == null) {
			return ANY;
		} else if (RGX_ID.matcher(expression).matches()) {
			return of(defaultNamespace, expression, null);
		} else if (RGX_ID_NBT.matcher(expression).matches()) {
			int i = expression.indexOf('{');
			return of(defaultNamespace, expression.substring(0, i), expression.substring(i));
		} else if (RGX_NBT.matcher(expression).matches()) {
			return of(defaultNamespace, null, expression);
		} else {
			throw new IllegalArgumentException(String.format("Invalid item pattern expression: %s", expression));
		}
	}

	/**
	 * @param idExp  宽松规则的物品ID
	 * @param tagExp NBT复合标签表达式
	 */
	@Contract("_,_,_ -> new")
	@Deprecated
	static @NotNull ItemPattern of (@NotNull String defaultNamespace, @Nullable String idExp, @Nullable String tagExp) {
		return new ItemPatternImpl(parseDescriptionId(defaultNamespace, idExp), parsePatternTag(tagExp));
	}

	/**
	 * 将宽松规则的物品ID解析为严格的descriptionId。
	 * <p>
	 * 允许 4 种输入格式：
	 * <li>null，解析结果也将是null</li>
	 * <li>"ID"，例如 snowball</li>
	 * <li>"命名空间:ID"，例如 minecraft:snowball</li>
	 * <li>"descriptionId"，例如 item.minecraft.snowball</li>
	 *
	 * @param defaultNamespace 命名空间缺省值
	 */
	@Contract("_,null->null")
	static @Nullable String parseDescriptionId (@NotNull String defaultNamespace, @Nullable String idExp) {
		if (idExp == null || idExp.isEmpty()) {
			return null;
		} else if (RGX_REGULAR_ID.matcher(idExp).matches()) {
			return idExp;
		} else if (RGX_PURE_ID.matcher(idExp).matches()) {
			return "item." + defaultNamespace + "." + idExp;
		} else if (RGX_NAMESPACE_ID.matcher(idExp).matches()) {
			return "item." + idExp.replace(':', '.');
		} else {
			throw new IllegalArgumentException("Invalid item description id: " + idExp);
		}
	}

	/**
	 * 解析给定的标签表达式，并在成功时返回一个CompoundTag。
	 *
	 * @param tagExp 要解析的标签表达式
	 * @return 解析后的CompoundTag，如果tagExp为null则返回null
	 */
	@Contract("null->null; !null ->new")
	static @Nullable CompoundTag parsePatternTag (@Nullable String tagExp) {
		if (tagExp == null) {
			return null;
		}
		try {
			return TagParser.parseTag(tagExp);
		} catch (CommandSyntaxException exception) {
			throw new IllegalArgumentException(String.format("Invalid NBT expression: %s\n%s", tagExp, exception.getMessage()));
		}
	}

	/**
	 * 迭代解析表达式，并将解析的物品模式添加到指定集合，
	 *
	 * @param itemPatterns 要添加到的物品模式集合
	 * @param expressions  包含表达式的可迭代对象
	 */
	static int addToSet (@NotNull String defaultNamespace, @NotNull Set<ItemPattern> itemPatterns, @Nullable Iterable<String> expressions) {
		int count = 0;
		if (expressions != null) {
			for (String expression: expressions) {
				try {
					itemPatterns.add(of(defaultNamespace, expression));
					count++;
				} catch (IllegalArgumentException e) {
					ThirdPerson.LOGGER.error("Skip invalid item pattern expression: {}", expression);
				}
			}
		}
		return count;
	}

	/**
	 * 匹配物品
	 * <p>
	 * 仅当 id 和 nbt 都匹配成功时，才匹配成功
	 *
	 * @param itemStack 物品槽
	 */
	default boolean match (@Nullable ItemStack itemStack) {
		return matchId(itemStack) && matchNbt(itemStack);
	}

	/**
	 * 根据id匹配物品。
	 * <p>
	 * 当 id 为 null 时总是匹配成功
	 * <p>
	 * 否则只有当id相同时才匹配成功
	 *
	 * @param itemStack 物品槽
	 */
	boolean matchId (@Nullable ItemStack itemStack);

	/**
	 * 根据 nbt 标签匹配物品
	 * <p>
	 * 使用 {@link NbtUtils#compareNbt(Tag, Tag, boolean)} 进行匹配
	 *
	 * @param itemStack 物品槽
	 */
	boolean matchNbt (@Nullable ItemStack itemStack);
}
