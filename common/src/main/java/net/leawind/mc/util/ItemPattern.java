package net.leawind.mc.util;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 物品模式
 * <p>
 * 可用于根据物品 id 和 nbt 标签匹配物品
 */
public class ItemPattern {
	/**
	 * 物品描述标识符
	 * <p>
	 * 可通过 {@link net.minecraft.world.item.ItemStack#getDescriptionId()} 获取
	 */
	@Nullable public final String      descriptionId;
	/**
	 * 用于匹配物品的NBT标签
	 * <p>
	 * null 表示匹配任意标签
	 */
	@Nullable public final CompoundTag patternTag;
	/**
	 * 用于标识 patternTag
	 */
	public final           int         tagHashCode;

	/**
	 * @param descriptionId 正规的 descriptionId
	 * @param patternTag    NBT标签
	 */
	private ItemPattern (@Nullable String descriptionId, @Nullable CompoundTag patternTag) {
		if (descriptionId != null && !descriptionId.matches(RGX_REGULAR_ID)) {
			throw new IllegalArgumentException("Irregular item description id: " + descriptionId);
		}
		this.descriptionId = descriptionId;
		this.patternTag    = patternTag;
		this.tagHashCode   = patternTag == null ? 0: patternTag.getAsString().hashCode();
	}

	/**
	 * 根据id匹配物品。
	 * <p>
	 * 当 id 为 null 时总是匹配成功
	 * <p>
	 * 否则只有当id相同时才匹配成功
	 *
	 * @param stack 物品槽
	 */
	public boolean matchId (@Nullable ItemStack stack) {
		if (descriptionId == null) {
			return true;
		} else if (stack == null) {
			return false;
		} else {
			return descriptionId.equals(stack.getDescriptionId());
		}
	}

	/**
	 * 根据 nbt 标签匹配物品
	 * <p>
	 * 使用 {@link NbtUtils#compareNbt(Tag, Tag, boolean)} 进行匹配
	 *
	 * @param stack 物品槽
	 */
	public boolean matchNbt (@Nullable ItemStack stack) {
		CompoundTag tag = stack == null ? null: stack.getTag();
		return NbtUtils.compareNbt(patternTag, tag, true);
	}

	/**
	 * 匹配物品
	 * <p>
	 * 仅当 id 和 nbt 都匹配成功时，才匹配成功
	 *
	 * @param stack 物品槽
	 */
	public boolean match (@Nullable ItemStack stack) {
		return matchId(stack) && matchNbt(stack);
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
		return Objects.equals(descriptionId, ip.descriptionId) && tagHashCode == obj.hashCode();
	}

	@Override
	public int hashCode () {
		return Objects.hashCode(descriptionId) ^ tagHashCode;
	}

	public static boolean anyMatch (Set<ItemPattern> patterns, ItemStack stack) {
		for (ItemPattern ip: patterns) {
			if (ip.match(stack)) {
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
	public static @Nullable String parseDescriptionId (@Nullable String id) throws IllegalArgumentException {
		if (id == null) {
			return null;
		} else if (id.matches(RGX_REGULAR_ID)) {
			return id;
		} else if (id.matches(RGX_PURE_ID)) {
			return "item.minecraft." + id;
		} else if (id.matches(RGX_NAMESPACE_ID)) {
			return "item." + id.replace(':', '.');
		} else {
			throw new IllegalArgumentException("Invalid item description id: " + id);
		}
	}

	/**
	 * 将字符串数组解析为 ItemPattern 集合
	 *
	 * @param patternExpressions 包含物品模板表达式的数组
	 */
	public static @NotNull Set<ItemPattern> parseToSet (@Nullable Iterable<String> patternExpressions) {
		HashSet<ItemPattern> set = new HashSet<>();
		if (patternExpressions != null) {
			for (String nbtSrc: patternExpressions) {
				try {
					set.add(ItemPattern.of(nbtSrc));
				} catch (IllegalArgumentException e) {
					ThirdPersonMod.LOGGER.error("Skip invalid id-nbt expression: {}", nbtSrc);
				}
			}
		}
		return set;
	}

	public static void mergeToSet (@NotNull Set<ItemPattern> set, @Nullable Iterable<String> patternExpressions) {
		if (patternExpressions != null) {
			for (String nbtSrc: patternExpressions) {
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
	public static @NotNull ItemPattern of (@Nullable String idNbt) {
		if (idNbt == null) {
			return ANY;
		} else {
			if (idNbt.matches(RGX_ID_NBT)) {
				int i = idNbt.indexOf('{');
				return of(idNbt.substring(0, i), idNbt.substring(i));
			} else {
				String descriptionId = parseDescriptionId(idNbt);
				return of(descriptionId, null);
			}
		}
	}

	/**
	 * @param id     宽松规则的 descriptionId
	 * @param tagSrc Nbt 复合标签表达式
	 */
	public static @NotNull ItemPattern of (@Nullable String id, @Nullable String tagSrc) {
		try {
			String      descriptionId = parseDescriptionId(id);
			CompoundTag tag           = tagSrc == null ? null: TagParser.parseTag(tagSrc);
			return new ItemPattern(descriptionId, tag);
		} catch (CommandSyntaxException e) {
			throw new IllegalArgumentException("Invalid NBT tag: " + tagSrc);
		}
	}

	public static final    ItemPattern ANY              = new ItemPattern(null, null);
	protected static final String      RGX_REGULAR_ID   = "^item\\.[a-z_]+\\.[a-z_]+$";
	protected static final String      RGX_PURE_ID      = "^[a-z_]+$";
	protected static final String      RGX_NAMESPACE_ID = "^[a-z_]+[.:][a-z_]+$";
	protected static final String      RGX_ID_NBT       = "^[a-z.:_]+\\{.*}$";
}
