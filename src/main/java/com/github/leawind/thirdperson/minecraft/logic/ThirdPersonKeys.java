package com.github.leawind.thirdperson.minecraft.logic;

import com.github.leawind.thirdperson.api.ThirdPerson;
import com.github.leawind.thirdperson.impl.ThirdPersonStates;
import com.github.leawind.thirdperson.utils.modkeymapping.ModKeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public final class ThirdPersonKeys {
  public static final KeyMapping.Category CATEGORY =
      KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ThirdPerson.MOD_ID, "keybinds"));

  public static final ModKeyMapping TOGGLE_MOD_ENABLE =
      ModKeyMapping.of(id("toggle_mod_enable"), CATEGORY)
          .onDown(
              () -> {
                var config = ThirdPerson.getConfigManager().getConfig();
                if (config.is_mod_enabled) {
                  Minecraft.getInstance().gameRenderer.checkEntityPostEffect(null);
                }
                config.is_mod_enabled = !config.is_mod_enabled;
              });

  public static final ModKeyMapping ADJUST_POSITION =
      ModKeyMapping.of(id("adjust_position"), InputConstants.KEY_Z, CATEGORY)
          .when(() -> ThirdPerson.getOrThrow().isAvailable())
          .onDown(() -> {})
          .onUp(() -> {});

  public static final ModKeyMapping FORCE_AIMING = ModKeyMapping.of(id("force_aiming"), CATEGORY);

  public static final ModKeyMapping OPEN_CONFIG_MENU =
      ModKeyMapping.of(id("open_config_menu"), CATEGORY)
          .when(() -> ThirdPerson.getOrThrow().isAvailable())
          .onDown(
              () -> {
                var minecraft = Minecraft.getInstance();
                if (minecraft.screen == null) {
                  minecraft.setScreen(ThirdPerson.getConfigManager().getConfigScreen(null));
                }
              });

  public static final ModKeyMapping TOGGLE_SIDE =
      ModKeyMapping.of(id("toggle_side"), InputConstants.KEY_CAPSLOCK, CATEGORY)
          .when(() -> ThirdPerson.getOrThrow().isAvailable())
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
      ModKeyMapping.of(id("toggle_aiming"), CATEGORY)
          .when(() -> ThirdPerson.getOrThrow().isAvailable())
          .onDown(
              () -> {
                var states = ThirdPerson.getOrThrow().getStates(ThirdPersonStates.class);
                states.isToggleToAiming = !states.isToggleToAiming;
              });

  public static final ModKeyMapping TOGGLE_PITCH_LOCK =
      ModKeyMapping.of(id("toggle_pitch_lock"), CATEGORY)
          .when(() -> ThirdPerson.getOrThrow().isAvailable())
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

  private static String id(String name) {
    return "key." + ThirdPerson.MOD_ID + "." + name;
  }

  public static void registerKeyMappings(Consumer<KeyMapping> registrar) {
    getAll().forEach(registrar);
  }
}
