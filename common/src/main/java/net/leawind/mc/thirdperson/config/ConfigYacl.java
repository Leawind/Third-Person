package net.leawind.mc.thirdperson.config;


import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;

public class ConfigYacl {
	// @formatter:off
	private static final ConfigClassHandler<ConfigYacl> HANDLER = ConfigClassHandler.createBuilder(ConfigYacl.class)
//		.id(new ResourceLocation(ThirdPersonMod.MOD_ID))
		.serializer(config -> GsonConfigSerializerBuilder
			.create(config)
			.setPath(Config.CONFIG_FILE_PATH)
			.setJson5(true)
			.build())
		.build();
	// @formatter:on
	@SerialEntry
	public static        boolean                        is_mod_enable = true;
	@SerialEntry
	public static        int                            available_distance_count = 16;
	@SerialEntry
	public static        double                         camera_distance_min = 0.1;

	public static void init () {
		// @formatter:off
//		YetAnotherConfigLib.createBuilder()
//			.title(Component.literal("Used for narration. Could be used to render a title in the future."))
//			.category(ConfigCategory.createBuilder()
//				.name(Component.literal("Name of the category"))
//					.tooltip(Component.literal("This text will appear as a tooltip when you hover or focus the button with Tab. There is no need to add \n to wrap as YACL will do it for you."))
//					.group(OptionGroup.createBuilder()
//						.name(Component.literal("Name of the group"))
//						.description(OptionDescription.of(Component.literal("This text will appear when you hover over the name or focus on the collapse button with Tab.")))
//						.build())
//					.build())
//			.build();
		// @formatter:on
	}
}
