package net.leawind.mc.thirdperson.impl.screen;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.api.screen.ConfigScreenBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 配置屏幕构建器
 */
public final class ConfigScreenBuilders {
	private static final Map<String, ConfigScreenBuilder> builders = new HashMap<>();

	static {
		builders.put("Cloth Config", new ClothConfigScreenBuilder());
		builders.put("Carbon Config", new CarbonConfigScreenBuilder());
		builders.put("YACL", new YaclConfigScreenBuilder());
	}

	public static @NotNull Optional<ConfigScreenBuilder> getBuilder () {
		final Map<String, ConfigScreenBuilder> availables = getAvailableBuidlers();
		final String                           expected   = ThirdPerson.getConfig().expected_config_screen_builder;
		if (availables.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(availables.getOrDefault(expected, availables.values().iterator().next()));
		}
	}

	public static @NotNull Map<String, ConfigScreenBuilder> getAvailableBuidlers () {
		final Map<String, ConfigScreenBuilder> availableBuilders = new HashMap<>();
		builders.forEach((name, builder) -> {
			if (builder.isAvailable()) {
				availableBuilders.put(name, builder);
			}
		});
		return availableBuilders;
	}
}
