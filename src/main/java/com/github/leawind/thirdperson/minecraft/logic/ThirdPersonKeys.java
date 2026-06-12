package com.github.leawind.thirdperson.minecraft.logic;

import com.github.leawind.thirdperson.api.ThirdPerson;
import com.github.leawind.thirdperson.utils.modkeymapping.ModKeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class ThirdPersonKeys {
  public static final KeyMapping.Category CATEGORY =
      KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ThirdPerson.MOD_ID, "keybinds"));

  public static final ModKeyMapping ADJUST_POSITION =
      ModKeyMapping.of(getId("adjust_position"), InputConstants.KEY_Z, CATEGORY)
          .onDown(
              () -> {
                // TODO
              })
          .onUp(
              () -> {
                // TODO
              });

  public static final ModKeyMapping FORCE_AIMING =
      ModKeyMapping.of(getId("force_aiming"), CATEGORY);

  public static final ModKeyMapping TOGGLE_MOD_ENABLE =
      ModKeyMapping.of(getId("toggle_mod_enable"), CATEGORY)
          .onDown(
              () -> {
                var config = ThirdPerson.getConfigManager().getConfig();
                config.is_mod_enabled = !config.is_mod_enabled;
                // TODO
              });

  public static final ModKeyMapping OPEN_CONFIG_MENU =
      ModKeyMapping.of(getId("open_config_menu"), CATEGORY)
          .onDown(
              () -> {
                var mc = Minecraft.getInstance();
                if (mc.screen == null) {
                  mc.setScreen(ThirdPerson.getConfigManager().getConfigScreen(null));
                }
              });

  public static final ModKeyMapping TOGGLE_SIDE =
      ModKeyMapping.of(getId("toggle_side"), InputConstants.KEY_CAPSLOCK, CATEGORY)
          .onDown(
              () -> {
                var scheme = ThirdPerson.getConfigManager().getConfig().getCameraOffsetScheme();
                boolean wasCentered = scheme.isCentered();
                if (wasCentered) {
                  scheme.toNextSide();
                }
                return wasCentered;
              }) //
          .onHold(
              () ->
                  ThirdPerson.getConfigManager()
                      .getConfig()
                      .getCameraOffsetScheme()
                      .setCentered(true))
          .onPress(
              () ->
                  ThirdPerson.getConfigManager().getConfig().getCameraOffsetScheme().toNextSide());

  public static final ModKeyMapping TOGGLE_AIMING =
      ModKeyMapping.of(getId("toggle_aiming"), CATEGORY)
          .onDown(
              () -> {
                var tp = ThirdPerson.get(Minecraft.getInstance());
                if (tp.isAvailable()) { // TODO and is in third person perspective
                  // TODO
                  // ThirdPersonStatus.isToggleToAiming = !ThirdPersonStatus.isToggleToAiming;
                }
              });

  public static final ModKeyMapping TOGGLE_PITCH_LOCK =
      ModKeyMapping.of(getId("toggle_pitch_lock"), CATEGORY)
          .onDown(
              () -> {
                var config = ThirdPerson.getConfigManager().getConfig();
                config.lock_camera_pitch_angle = !config.lock_camera_pitch_angle;
              });

  private static Collection<KeyMapping> getAll() {
    List<KeyMapping> list = new ArrayList<>();
    for (Field field : ThirdPersonKeys.class.getDeclaredFields()) {
      var modifiers = field.getModifiers();
      if (!Modifier.isStatic(modifiers)) {
        continue;
      }
      if (!field.canAccess(null)) {
        continue;
      }
      try {
        if (field.get(null) instanceof KeyMapping keyMapping) {
          list.add(keyMapping);
        }
      } catch (IllegalAccessException ignored) {
      }
    }
    return list;
  }

  private static @NotNull String getId(@NotNull String name) {
    return "key." + ThirdPerson.MOD_ID + "." + name;
  }

  public static void register() {
    register(KeyMappingRegistry::register);
  }

  public static void register(Consumer<KeyMapping> registrar) {
    getAll().forEach(registrar);
  }
}
