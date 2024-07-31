package net.leawind.mc.thirdperson.resources;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.util.annotations.VersionSensitive;
import net.leawind.mc.util.ItemPattern;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
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
 * @see SplashManager
 */
@VersionSensitive("SimpleJsonResourceReloadListener may not exist in other mc version")
public class ItemPatternManager extends SimpleJsonResourceReloadListener {
	private static final Gson             GSON                         = new GsonBuilder().create();
	public static final  String           ID                           = "item_patterns";
	private static final String           SET_HOLD_TO_AIM              = "hold_to_aim";
	private static final String           SET_USE_TO_AIM               = "use_to_aim";
	private static final String           SET_USE_TO_FIRST_PERSON      = "use_to_first_person";
	public final         Set<ItemPattern> holdToAimItemPatterns        = new HashSet<>();
	public final         Set<ItemPattern> useToAimItemPatterns         = new HashSet<>();
	public final         Set<ItemPattern> useToFirstPersonItemPatterns = new HashSet<>();

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
		useToFirstPersonItemPatterns.clear();
		map.forEach((resourceLocation, jsonElement) -> {
			JsonArray aimingCheckObj = jsonElement.getAsJsonArray();
			String[]  resourcePath   = resourceLocation.getPath().split("/");
			String    namespace      = resourceLocation.getNamespace();
			if (resourcePath.length >= 2) {
				String resourceSetName = resourcePath[0];
				switch (resourceSetName) {
					case SET_HOLD_TO_AIM -> {
						int count = addToSet(namespace, holdToAimItemPatterns, aimingCheckObj);
						ThirdPerson.LOGGER.info("Loaded {} hold_to_aim item patterns from {}", count, resourceLocation);
					}
					case SET_USE_TO_AIM -> {
						int count = addToSet(namespace, useToAimItemPatterns, aimingCheckObj);
						ThirdPerson.LOGGER.info("Loaded {}  use_to_aim item patterns from {}", count, resourceLocation);
					}
					case SET_USE_TO_FIRST_PERSON -> {
						int count = addToSet(namespace, useToFirstPersonItemPatterns, aimingCheckObj);
						ThirdPerson.LOGGER.info("Loaded {}  use_to_first_person item patterns from {}", count, resourceLocation);
					}
				}
			}
		});
	}

	private int addToSet (@NotNull String defaultNamespace, @NotNull Set<ItemPattern> set, @NotNull JsonArray arr) {
		arr.forEach(ele -> {
			try {
				set.add(ItemPattern.of(defaultNamespace, ele.getAsString()));
			} catch (Exception e) {
				ThirdPerson.LOGGER.warn(e.getMessage());
			}
		});
		return arr.size();
	}
}
