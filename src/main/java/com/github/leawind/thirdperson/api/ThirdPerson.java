package com.github.leawind.thirdperson.api;

import com.github.leawind.thirdperson.api.config.ConfigManager;
import com.github.leawind.thirdperson.api.model.Actor;
import com.github.leawind.thirdperson.api.model.Viewpoint;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ThirdPerson {
  String MOD_ID = "leawind_third_person";
  String MOD_NAME = "Leawind's Third Person";

  static @Nullable ThirdPerson getOrNull() {
    Minecraft minecraft = Minecraft.getInstance();
    // In some situations, Minecraft.getInstance() may return null.
    if (minecraft == null) {
      return null;
    }
    return get(minecraft);
  }

  /// @throws NullPointerException if `minecraft` is null
  static ThirdPerson get(Minecraft minecraft) throws NullPointerException {
    return Factory.getOrCreateThirdPerson(minecraft);
  }

  static ConfigManager getConfigManager() {
    return Factory.getConfigManager();
  }

  @NonNull Minecraft getMinecraft();

  @NonNull Viewpoint getViewpoint();

  /// 未加入世界时，返回 null
  @Nullable Actor getActor();

  /// 满足以下条件：
  ///
  /// - 在配置中启用了本模组
  /// - 相机和相机实体都存在（玩家已加入世界）
  /// - 相机初始化完毕
  boolean isAvailable();
}
