package com.github.leawind.thirdperson.api;

import com.github.leawind.thirdperson.api.model.Actor;
import com.github.leawind.thirdperson.api.model.Viewpoint;
import com.github.leawind.thirdperson.core.config.ConfigManager;
import com.github.leawind.thirdperson.utils.Suppressor;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ThirdPerson {
  String MOD_ID = "leawind_third_person";
  String MOD_NAME = "Leawind's Third Person";

  static @Nullable ThirdPerson getOrNull() {
    Minecraft minecraft = Minecraft.getInstance();
    if (Suppressor.isNull(minecraft)) {
      return null;
    }
    return get(minecraft);
  }

  /// @throws NullPointerException if `Minecraft.getInstance()` is null
  static @NonNull ThirdPerson getOrThrow() throws NullPointerException {
    return Factory.getOrCreateThirdPerson(Minecraft.getInstance());
  }

  static @NonNull ThirdPerson get(Minecraft minecraft) throws NullPointerException {
    return Factory.getOrCreateThirdPerson(minecraft);
  }

  static @NonNull ConfigManager getConfigManager() {
    return ConfigManager.INSTANCE;
  }

  @NonNull Minecraft getMinecraft();

  /// 未进入游戏时，返回 null
  @Nullable Viewpoint getViewpointOrNull();

  default @NonNull Viewpoint getViewpoint() {
    return Objects.requireNonNull(getViewpointOrNull());
  }

  /// 未加入世界时，返回 null
  @Nullable Actor getActorOrNull();

  default @NonNull Actor getActor() {
    return Objects.requireNonNull(getActorOrNull());
  }

  /// 满足以下条件：
  ///
  /// - 在配置中启用了本模组
  /// - 相机和相机实体都存在（玩家已加入世界）
  /// - 相机初始化完毕
  boolean isAvailable();

  /// 重新设置 actor 和 viewpoint
  ///
  /// 应当在玩家加入世界时，或玩家重生时调用
  void reset();

  <S extends States> S getStates(Class<S> clazz);

  /// ### Api Note
  ///
  /// - 必须拥有一个参数为 `(ThirdPerson)` 的构造函数
  abstract class States {
    protected ThirdPerson thirdPerson;

    protected States(ThirdPerson thirdPerson) {
      this.thirdPerson = thirdPerson;
    }
  }
}
