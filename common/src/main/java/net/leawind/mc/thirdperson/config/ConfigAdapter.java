package net.leawind.mc.thirdperson.config;


import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.isxander.yacl3.config.ConfigEntry;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * 由于我的配置项使用的是静态字段，此版本的 YACL 无法正确识别，所以我得自己写 Adapter
 */
public class ConfigAdapter extends TypeAdapter<Config> {
	public static final Logger LOGGER = Config.LOGGER;

	@Override
	public void write (JsonWriter out, Config value) {
		try {
			out.beginObject();
			for (Field field: net.leawind.mc.thirdperson.config.Config.class.getDeclaredFields()) {
				if (field.isAnnotationPresent(ConfigEntry.class)) {
					Class<?> fieldType = field.getType();
					field.setAccessible(true);
					out.name(field.getName());
					switch (fieldType.getName()) {
						case "boolean" -> out.value(field.getBoolean(null));
						case "byte" -> out.value(field.getByte(null));
						case "char" -> out.value(field.getChar(null));
						case "short" -> out.value(field.getShort(null));
						case "int" -> out.value(field.getInt(null));
						case "long" -> out.value(field.getLong(null));
						case "float" -> out.value(field.getFloat(null));
						case "double" -> out.value(field.getDouble(null));
						case "string" -> out.value((String)field.get(null));
						default -> throw new RuntimeException("Unsupported class: " + fieldType.getName());
					}
				}
			}
			out.endObject();
		} catch (IOException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Config read (JsonReader in) {
		try {
			in.beginObject();
			while (in.hasNext()) {
				String key = in.nextName();
				try {
					Field field = net.leawind.mc.thirdperson.config.Config.class.getDeclaredField(key);
					if (!field.isAnnotationPresent(ConfigEntry.class)) {
						throw new NoSuchFieldException();
					}
					Class<?> clazz = field.getType();
					field.setAccessible(true);
					switch (clazz.getName()) {
						case "boolean" -> field.setBoolean(null, in.nextBoolean());
						case "byte" -> field.setByte(null, (byte)in.nextInt());
						case "char" -> field.setChar(null, in.nextString().charAt(0));
						case "short" -> field.setShort(null, (short)in.nextInt());
						case "int" -> field.setInt(null, in.nextInt());
						case "long" -> field.setLong(null, in.nextLong());
						case "float" -> field.setFloat(null, (float)in.nextDouble());
						case "double" -> field.setDouble(null, in.nextDouble());
						case "string" -> field.set(null, in.nextString());
						default -> throw new RuntimeException("Unsupported class: " + clazz.getName());
					}
				} catch (NoSuchFieldException e) {
					LOGGER.warn("Unrecognized config key: {}", key);
				}
			}
			in.endObject();
		} catch (IllegalAccessException | IOException e) {
			throw new RuntimeException(e);
		}
		return new Config();
	}
}
