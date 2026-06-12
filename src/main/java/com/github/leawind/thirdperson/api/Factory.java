package com.github.leawind.thirdperson.api;

import com.github.leawind.thirdperson.api.config.ConfigManager;
import com.github.leawind.thirdperson.impl.ThirdPersonImpl;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class Factory {
  private Factory() {}

  /// @throws NullPointerException if `minecraft` is null
  public static ThirdPerson getOrCreateThirdPerson(Minecraft minecraft)
      throws NullPointerException {
    return ThirdPersonImpl.getOrCreate(minecraft);
  }

  public static ConfigManager getConfigManager() {
    return ConfigManager.INSTANCE;
  }
}
