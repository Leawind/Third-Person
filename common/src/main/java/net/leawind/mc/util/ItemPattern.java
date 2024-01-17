package net.leawind.mc.util;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
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
public class ItemPattern {
	/**
	 * 物品描述标识符
	 * <p>
	 * 可通过 {@link net.minecraft.world.item.ItemStack#getDescriptionId()} 获取
	 */
	@Nullable private final String      descriptionId;
	/**
	 * 用于匹配物品的NBT标签
	 * <p>
	 * null 表示匹配任意标签
	 */
	@Nullable private final CompoundTag patternTag;
	@Nullable private final String      tagExp;
	private final           int         hashCode;

	/**
	 * @param descriptionId 正规的 descriptionId
	 * @param patternTag    NBT模式标签
	 */
	private ItemPattern (@Nullable String descriptionId, @Nullable CompoundTag patternTag) throws IllegalArgumentException {
		if (!(descriptionId == null || RGX_REGULAR_ID.matcher(descriptionId).matches())) {
			throw new IllegalArgumentException(String.format("Irregular item description id: %s", descriptionId));
		}
		this.descriptionId = descriptionId;
		this.patternTag    = patternTag;
		this.tagExp        = patternTag == null ? null: patternTag.getAsString();
		this.hashCode      = Objects.hashCode(descriptionId) ^ Objects.hashCode(tagExp);
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
	public boolean matchId (@Nullable ItemStack itemStack) {
		if (descriptionId == null) {
			return true;
		} else if (itemStack == null) {
			return false;
		} else {
			return descriptionId.equals(itemStack.getDescriptionId());
		}
	}

	/**
	 * 根据 nbt 标签匹配物品
	 * <p>
	 * 使用 {@link NbtUtils#compareNbt(Tag, Tag, boolean)} 进行匹配
	 *
	 * @param itemStack 物品槽
	 */
	public boolean matchNbt (@Nullable ItemStack itemStack) {
		CompoundTag tag = itemStack == null ? null: itemStack.getTag();
		return NbtUtils.compareNbt(patternTag, tag, true);
	}

	/**
	 * 匹配物品
	 * <p>
	 * 仅当 id 和 nbt 都匹配成功时，才匹配成功
	 *
	 * @param itemStack 物品槽
	 */
	public boolean match (@Nullable ItemStack itemStack) {
		return matchId(itemStack) && matchNbt(itemStack);
	}

	@Override
	public String toString () {
		return (descriptionId == null ? "": descriptionId) + (tagExp == null ? "": tagExp);
	}

	@Override
	public boolean equals (Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() != obj.getClass()) {
			return false;
		}
		ItemPattern ip = (ItemPattern)obj;
		return Objects.equals(descriptionId, ip.descriptionId) && Objects.equals(tagExp, ip.tagExp);
	}

	@Override
	public int hashCode () {
		return hashCode;
	}

	public static boolean anyMatch (Iterable<ItemPattern> itemPatterns, ItemStack itemStack) {
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
	public static @Nullable String parseDescriptionId (@Nullable String idExp) throws IllegalArgumentException {
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

	public static @Nullable CompoundTag parsePatternTag (@Nullable String tagExp) throws IllegalArgumentException {
		if (tagExp == null) {
			return null;
		}
		try {
			return TagParser.parseTag(tagExp);
		} catch (CommandSyntaxException e) {
			throw new IllegalArgumentException(String.format("Invalid NBT expression: %s\n%s", tagExp, e.getMessage()));
		}
	}

	public static void mergeToSet (@NotNull Set<ItemPattern> set, @Nullable Iterable<String> ruleExpressions) {
		if (ruleExpressions != null) {
			for (String nbtSrc: ruleExpressions) {
				try {
					set.add(ItemPattern.of(nbtSrc));
				} catch (IllegalArgumentException e) {
					ThirdPersonMod.LOGGER.error("Skip invalid id-nbt expression: {}", nbtSrc);
				}
			}
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
	public static @NotNull ItemPattern of (@Nullable String ruleExpression) throws IllegalArgumentException {
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
	public static @NotNull ItemPattern of (@Nullable String idExp, @Nullable String tagExp) throws IllegalArgumentException {
		return new ItemPattern(parseDescriptionId(idExp), parsePatternTag(tagExp));
	}

	/**
	 * 提供错误信息
	 *
	 * @param ruleExpression 表达式
	 * @return 错误信息，null 表示没有错误
	 */
	public static @NotNull Optional<Component> supplyError (@Nullable String ruleExpression) {
		try {
			of(ruleExpression);
			return Optional.empty();
		} catch (IllegalArgumentException e) {
			return Optional.of(Component.literal(e.getMessage()));
		}
	}

	public static final    ItemPattern ANY              = new ItemPattern(null, null);
	protected static final Pattern     RGX_REGULAR_ID   = Pattern.compile("^item\\.[a-z_]+\\.[a-z_]+$");
	protected static final Pattern     RGX_PURE_ID      = Pattern.compile("^[a-z_]+$");
	protected static final Pattern     RGX_NAMESPACE_ID = Pattern.compile("^[a-z_]+[.:][a-z_]+$");
	protected static final Pattern     RGX_ID_NBT       = Pattern.compile("^[a-z.:_]+\\{.*}$");
	protected static final Pattern     RGX_ID           = Pattern.compile("^[a-z.:_]+$");
	protected static final Pattern     RGX_NBT          = Pattern.compile("^\\{.*}$");
}
