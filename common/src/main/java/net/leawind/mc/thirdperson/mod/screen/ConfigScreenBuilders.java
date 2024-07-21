package net.leawind.mc.thirdperson.mod.screen;


import dev.architectury.platform.Platform;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.interfaces.screen.ConfigScreenBuilder;
import net.leawind.mc.util.OptionalFunction;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 配置屏幕构建器
 */
@SuppressWarnings("all")
public final class ConfigScreenBuilders {
	/**
	 * 已经实现或将来可能实现的构建器们
	 */
	private static final Map<String, OptionalFunction<ConfigScreenBuilder>> builders = new HashMap<>();

	static {
		builders.put("Cloth Config", OptionalFunction.of(() -> new ClothConfigScreenBuilder(), () -> Platform.isModLoaded("cloth-config") || Platform.isModLoaded("cloth_config")));
		builders.put("YACL", OptionalFunction.of(() -> new YaclConfigScreenBuilder(), () -> Platform.isModLoaded("yet_another_config_lib_v3")));
		Set<String> availables = getAvailableBuidlers().keySet();
		availables.forEach(name -> {
			ThirdPerson.LOGGER.debug("Found available config screen builder: {}", name);
		});
		if (availables.isEmpty()) {
			ThirdPerson.LOGGER.warn("No config screen API available.");
		}
	}

	/**
	 * 根据配置获取屏幕构建器
	 */
	public static @NotNull Optional<ConfigScreenBuilder> getBuilder () {
		final Map<String, OptionalFunction<ConfigScreenBuilder>> availables = getAvailableBuidlers();
		final String                                             expected   = ThirdPerson.getConfig().config_screen_api;
		if (availables.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(availables.getOrDefault(expected, availables.values().iterator().next()).get());
		}
	}

	/**
	 * 获取全部可用的构建器
	 */
	public static @NotNull Map<String, OptionalFunction<ConfigScreenBuilder>> getAvailableBuidlers () {
		final Map<String, OptionalFunction<ConfigScreenBuilder>> availableBuilders = new HashMap<>();
		builders.forEach((name, builder) -> {
			if (builder.isAvailable()) {
				availableBuilders.put(name, builder);
			}
		});
		return availableBuilders;
	}
}
