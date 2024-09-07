package com.github.leawind.thirdperson.resources;


import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.config.Config;
import com.github.leawind.util.ItemPredicateUtil;
import com.github.leawind.util.annotation.VersionSensitive;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 物品模式管理器
 * <p>
 * 物品模式采用json格式存储。
 * <p>
 * 重载资源包时，mc会调用{@link ItemPredicateManager#apply}方法处理读取到的json数据。
 *
 * @see ItemPredicateUtil
 * @see SplashManager
 */
@VersionSensitive("SimpleJsonResourceReloadListener may not exist in other mc version")
public class ItemPredicateManager extends SimpleJsonResourceReloadListener {
	private static final Gson                     GSON                           = new GsonBuilder().create();
	private static final String                   ID                             = "item_patterns";
	private static final String                   SET_HOLD_TO_AIM                = "hold_to_aim";
	private static final String                   SET_USE_TO_AIM                 = "use_to_aim";
	private static final String                   SET_USE_TO_FIRST_PERSON        = "use_to_first_person";
	public final         Map<String, Set<String>> holdToAimItemPatterns          = new HashMap<>();
	public final         Map<String, Set<String>> useToAimItemPatterns           = new HashMap<>();
	public final         Map<String, Set<String>> useToFirstPersonItemPatterns   = new HashMap<>();
	public final         Set<ItemPredicate>       holdToAimItemPredicates        = new HashSet<>();
	public final         Set<ItemPredicate>       useToAimItemPredicates         = new HashSet<>();
	public final         Set<ItemPredicate>       useToFirstPersonItemPredicates = new HashSet<>();

	public ItemPredicateManager () {
		super(GSON, ID);
	}

	/**
	 * 重载资源包时会调用此方法，处理资源包中的json数据
	 *
	 * @param map             资源地址与json数据的映射表
	 * @param resourceManager {@link MultiPackResourceManager}的实例
	 * @see Config#updateItemPredicates()
	 */
	@Override
	public void apply (@NotNull Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profile) {
		holdToAimItemPatterns.clear();
		useToAimItemPatterns.clear();
		useToFirstPersonItemPatterns.clear();
		map.forEach((resourceLocation, jsonElement) -> {
			var obj          = jsonElement.getAsJsonArray();
			var resourcePath = resourceLocation.getPath().split("/");
			var namespace    = resourceLocation.getNamespace();
			if (resourcePath.length >= 2) {
				var resourceSetName = resourcePath[0];
				switch (resourceSetName) {
					case SET_HOLD_TO_AIM -> addToSet(namespace, holdToAimItemPatterns, obj);
					case SET_USE_TO_AIM -> addToSet(namespace, useToAimItemPatterns, obj);
					case SET_USE_TO_FIRST_PERSON -> addToSet(namespace, useToFirstPersonItemPatterns, obj);
				}
			}
		});
		reparse();
	}

	private void addToSet (String defaultNs, @NotNull Map<String, Set<String>> patternMap, @NotNull JsonArray arr) {
		Set<String> patterns;
		if (patternMap.containsKey(defaultNs)) {
			patterns = patternMap.get(defaultNs);
		} else {
			patterns = new HashSet<>();
			patternMap.put(defaultNs, patterns);
		}
		arr.forEach(ele -> {
			try {
				var pattern = ele.getAsString();
				patterns.add(pattern);
			} catch (Throwable e) {
				ThirdPerson.LOGGER.warn(e.getMessage());
			}
		});
		arr.size();
	}

	public void reparse () {
		holdToAimItemPredicates.clear();
		useToAimItemPredicates.clear();
		useToFirstPersonItemPredicates.clear();
		int count;
		count = parseToSet(holdToAimItemPredicates, holdToAimItemPatterns);
		if (count > 0) {
			ThirdPerson.LOGGER.info("Loaded {} hold_to_aim item patterns from resource pack", count);
		}
		count = parseToSet(useToAimItemPredicates, useToAimItemPatterns);
		if (count > 0) {
			ThirdPerson.LOGGER.info("Loaded {} use_to_aim item patterns from resource pack", count);
		}
		count = parseToSet(useToFirstPersonItemPredicates, useToFirstPersonItemPatterns);
		if (count > 0) {
			ThirdPerson.LOGGER.info("Loaded {} use_to_first_person item patterns from resource pack", count);
		}
	}

	private int parseToSet (Set<ItemPredicate> predicates, Map<String, Set<String>> patternMap) {
		int count = 0;
		for (var defaultNs: patternMap.keySet()) {
			var patterns = patternMap.get(defaultNs);
			for (var pattern: patterns) {
				try {
					predicates.add(ItemPredicateUtil.parse(pattern));
					count++;
				} catch (IllegalArgumentException e) {
					ThirdPerson.LOGGER.warn("Skip invalid item pattern: {} Because {}", pattern, e.getMessage());
				}
			}
		}
		return count;
	}
}
