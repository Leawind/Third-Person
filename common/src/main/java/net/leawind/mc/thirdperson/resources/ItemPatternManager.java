package net.leawind.mc.thirdperson.resources;


import com.google.gson.*;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.util.annotations.VersionSensitive;
import net.leawind.mc.util.itempattern.ItemPattern;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootTables;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 物品模式管理器
 * <p>
 * 物品模式采用json格式存储。
 * <p>
 * 重载资源包时，mc会调用{@link ItemPatternManager#apply}方法处理读取到的json数据。
 *
 * @see ItemPattern
 * @see LootTables
 * @see SplashManager
 */
@VersionSensitive("SimpleJsonResourceReloadListener may not exist in other mc version")
public class ItemPatternManager extends SimpleJsonResourceReloadListener {
	public static final  String           ID                       = "item_patterns";
	private static final Gson             GSON                     = new GsonBuilder().create();
	private static final String           AIMING_CHECK_DIRECTORY   = "aiming_check";
	private static final String           AIMING_CHECK_HOLD_TO_AIM = "hold_to_aim";
	private static final String           AIMING_CHECK_USE_TO_AIM  = "use_to_aim";
	public final         Set<ItemPattern> holdToAimItemPatterns    = new HashSet<>();
	public final         Set<ItemPattern> useToAimItemPatterns     = new HashSet<>();

	public ItemPatternManager () {
		super(GSON, ID);
	}

	/**
	 * 重载资源包时会调用此方法，处理资源包中的json数据
	 *
	 * @param map             资源地址与json数据的映射表
	 * @param resourceManager {@link MultiPackResourceManager}的实例
	 * @see Config#updateItemPatterns()
	 */
	@Override
	public void apply (@NotNull Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profile) {
		holdToAimItemPatterns.clear();
		useToAimItemPatterns.clear();
		map.forEach((resourceLocation, jsonElement) -> {
			JsonObject aimingCheckObj = jsonElement.getAsJsonObject();
			if (resourceLocation.getPath().startsWith(AIMING_CHECK_DIRECTORY + "/")) {
				int countHold = aimingCheckObj.has(AIMING_CHECK_HOLD_TO_AIM) ? addToSet(holdToAimItemPatterns, aimingCheckObj.getAsJsonArray(AIMING_CHECK_HOLD_TO_AIM)): 0;
				int countUse  = aimingCheckObj.has(AIMING_CHECK_USE_TO_AIM) ? addToSet(useToAimItemPatterns, aimingCheckObj.getAsJsonArray(AIMING_CHECK_USE_TO_AIM)): 0;
				ThirdPerson.LOGGER.info("Loaded {} hold_to_aim item patterns from {}", countHold, resourceLocation);
				ThirdPerson.LOGGER.info("Loaded {}  use_to_aim item patterns from {}", countUse, resourceLocation);
			}
		});
	}

	private int addToSet (@NotNull Set<ItemPattern> set, @NotNull JsonArray arr) {
		arr.forEach(ele -> {
			try {
				set.add(ItemPattern.of(ele.getAsString()));
			} catch (Exception e) {
				ThirdPerson.LOGGER.warn(e.getMessage());
			}
		});
		return arr.size();
	}
}
