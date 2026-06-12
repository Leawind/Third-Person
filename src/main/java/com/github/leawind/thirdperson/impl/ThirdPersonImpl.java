package com.github.leawind.thirdperson.impl;

import com.github.leawind.thirdperson.api.ThirdPerson;
import com.github.leawind.thirdperson.api.model.Actor;
import com.github.leawind.thirdperson.api.model.Viewpoint;
import com.github.leawind.thirdperson.impl.model.ActorImpl;
import com.github.leawind.thirdperson.impl.model.ViewpointImpl;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ThirdPersonImpl implements ThirdPerson {
  private final Minecraft minecraft;

  private final ViewpointImpl viewpoint;
  private @Nullable ActorImpl actor = null;

  /// @throws NullPointerException if `minecraft` is null
  private ThirdPersonImpl(@NonNull Minecraft minecraft) throws NullPointerException {
    this.minecraft = Objects.requireNonNull(minecraft);

    this.viewpoint = new ViewpointImpl(minecraft.gameRenderer.getMainCamera());

    var cameraEntity = minecraft.getCameraEntity();
    if (cameraEntity != null) {
      actor = new ActorImpl(cameraEntity);
    }
  }

  @Override
  public @NonNull Minecraft getMinecraft() {
    return minecraft;
  }

  @Override
  public @NonNull Viewpoint getViewpoint() {
    return viewpoint;
  }

  @Override
  public @Nullable Actor getActor() {
    return actor;
  }

  @Override
  public boolean isAvailable() {
    return minecraft.getCameraEntity() != null
        && ThirdPerson.getConfigManager().getConfig().is_mod_enabled
        && viewpoint.camera().isInitialized();
  }

  private static volatile @Nullable ThirdPersonImpl instance = null;
  private static final Object lock = new Object();

  /// @throws NullPointerException if `minecraft` is null
  public static ThirdPerson getOrCreate(@NonNull Minecraft minecraft) throws NullPointerException {
    ThirdPersonImpl inst;

    inst = instance;
    if (inst != null && inst.minecraft == minecraft) {
      return inst;
    }

    synchronized (lock) {
      inst = instance;
      if (inst != null && inst.minecraft == minecraft) {
        return inst;
      }

      instance = new ThirdPersonImpl(minecraft);
      return instance;
    }
  }
}
