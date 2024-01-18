package net.leawind.mc.util.api;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.util.impl.ItemPatternImpl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
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
 * 使用 {@link ItemPattern#of(String)} 或 {@link ItemPattern#of(String, String)} 构建实例
 * <p>
 * 示例：
 * <pre>
 * ItemPattern ip          = ItemPattern.of("crossbow{Charged:1b}");
 * LocalPlayer player      = Minecraft.getInstance().player;
 * boolean     matchResult = ip.match(player.getMainHandItem());
 * </pre>
 */
@SuppressWarnings("unused")
public interface ItemPattern {
	ItemPattern ANY              = of(null, null);
	Pattern     RGX_REGULAR_ID   = Pattern.compile("^item\\.[a-z_]+\\.[a-z_]+$");
	Pattern     RGX_PURE_ID      = Pattern.compile("^[a-z_]+$");
	Pattern     RGX_NAMESPACE_ID = Pattern.compile("^[a-z_]+[.:][a-z_]+$");
	Pattern     RGX_ID_NBT       = Pattern.compile("^[a-z.:_]+\\{.*}$");
	Pattern     RGX_ID           = Pattern.compile("^[a-z.:_]+$");
	Pattern     RGX_NBT          = Pattern.compile("^\\{.*}$");

	static boolean anyMatch (Iterable<ItemPattern> itemPatterns, ItemStack itemStack) {
		for (ItemPattern ip: itemPatterns) {
			if (ip.match(itemStack)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 允许的格式：
	 * <p>
	 * 完整格式：s -> s
	 * <p>
	 * item.minecraft.snowball
	 * <p>
	 * 简写格式：s -> "item.minecraft." + s
	 * <p>
	 * snowbow
	 * <p>
	 * 命名空间+物品id "item." + s.replace(':', '.')
	 * <p>
	 * minecraft.snowball
	 * <p>
	 * minecraft:snowball
	 */
	static @Nullable String parseDescriptionId (@Nullable String idExp) throws IllegalArgumentException {
		if (idExp == null || idExp.isEmpty()) {
			return null;
		} else if (RGX_REGULAR_ID.matcher(idExp).matches()) {
			return idExp;
		} else if (RGX_PURE_ID.matcher(idExp).matches()) {
			return "item.minecraft." + idExp;
		} else if (RGX_NAMESPACE_ID.matcher(idExp).matches()) {
			return "item." + idExp.replace(':', '.');
		} else {
			throw new IllegalArgumentException("Invalid item description id: " + idExp);
		}
	}

	/**
	 * 提供错误信息
	 *
	 * @param ruleExpression 表达式
	 * @return 错误信息，null 表示没有错误
	 */
	static @NotNull Optional<Component> supplyError (@Nullable String ruleExpression) {
		try {
			of(ruleExpression);
			return Optional.empty();
		} catch (IllegalArgumentException e) {
			return Optional.of(Component.literal(e.getMessage()));
		}
	}

	/**
	 * 示例：
	 * <p>
	 * snowball
	 * <p>
	 * crossbow{Charged:1b}
	 * <p>
	 * minecraft:crossbow{Charged:1b}
	 * <p>
	 * item.minecraft.crossbow{Charged:1b}
	 */
	static @NotNull ItemPattern of (@Nullable String ruleExpression) throws IllegalArgumentException {
		if (ruleExpression == null) {
			return ANY;
		} else if (RGX_ID.matcher(ruleExpression).matches()) {
			return of(ruleExpression, null);
		} else if (RGX_ID_NBT.matcher(ruleExpression).matches()) {
			int i = ruleExpression.indexOf('{');
			return of(ruleExpression.substring(0, i), ruleExpression.substring(i));
		} else if (RGX_NBT.matcher(ruleExpression).matches()) {
			return of(null, ruleExpression);
		} else {
			throw new IllegalArgumentException(String.format("Invalid item pattern expression: %s", ruleExpression));
		}
	}

	/**
	 * @param idExp  宽松规则的 descriptionId
	 * @param tagExp NBT复合标签表达式
	 */
	static @NotNull ItemPattern of (@Nullable String idExp, @Nullable String tagExp) throws IllegalArgumentException {
		return new ItemPatternImpl(parseDescriptionId(idExp), parsePatternTag(tagExp));
	}

	static @Nullable CompoundTag parsePatternTag (@Nullable String tagExp) throws IllegalArgumentException {
		if (tagExp == null) {
			return null;
		}
		try {
			return TagParser.parseTag(tagExp);
		} catch (CommandSyntaxException e) {
			throw new IllegalArgumentException(String.format("Invalid NBT expression: %s\n%s", tagExp, e.getMessage()));
		}
	}

	static void addToSet (@NotNull Set<ItemPattern> set, @Nullable Iterable<String> ruleExpressions) {
		if (ruleExpressions != null) {
			for (String nbtSrc: ruleExpressions) {
				try {
					set.add(of(nbtSrc));
				} catch (IllegalArgumentException e) {
					ThirdPerson.LOGGER.error("Skip invalid id-nbt expression: {}", nbtSrc);
				}
			}
		}
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

	/**
	 * 匹配物品
	 * <p>
	 * 仅当 id 和 nbt 都匹配成功时，才匹配成功
	 *
	 * @param itemStack 物品槽
	 */
	boolean match (@Nullable ItemStack itemStack);
}
