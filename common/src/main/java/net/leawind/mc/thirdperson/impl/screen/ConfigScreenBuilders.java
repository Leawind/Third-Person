package net.leawind.mc.thirdperson.impl.screen;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.api.screen.ConfigScreenBuilder;
import net.leawind.mc.util.OptionalFunction;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

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
		builders.put("Cloth Config", OptionalFunction.of(() -> new ClothConfigScreenBuilder(), packageExists("me.shedaniel.clothconfig2.api.ConfigBuilder")));
		builders.put("Carbon Config", OptionalFunction.of(() -> new CarbonConfigScreenBuilder(), () -> false));
		builders.put("YACL", OptionalFunction.of(() -> new YaclConfigScreenBuilder(), () -> false));
		if (getAvailableBuidlers().isEmpty()) {
			ThirdPerson.LOGGER.warn("No config screen API available.");
		}
	}

	public static @NotNull Optional<ConfigScreenBuilder> getBuilder () {
		final Map<String, OptionalFunction<ConfigScreenBuilder>> availables = getAvailableBuidlers();
		final String                                             expected   = ThirdPerson.getConfig().config_screen_api;
		if (availables.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(availables.getOrDefault(expected, availables.values().iterator().next()).get());
		}
	}

	public static @NotNull Map<String, OptionalFunction<ConfigScreenBuilder>> getAvailableBuidlers () {
		final Map<String, OptionalFunction<ConfigScreenBuilder>> availableBuilders = new HashMap<>();
		builders.forEach((name, builder) -> {
			if (builder.isAvailable()) {
				availableBuilders.put(name, builder);
			}
		});
		return availableBuilders;
	}

	private static Supplier<Boolean> packageExists (String packageName) {
		return () -> {
			try {
				Class.forName(packageName);
				return true;
			} catch (Exception e) {
				return false;
			}
		};
	}
}
