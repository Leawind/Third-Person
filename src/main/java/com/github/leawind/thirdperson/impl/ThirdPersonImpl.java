package com.github.leawind.thirdperson.impl;

import com.github.leawind.thirdperson.api.ThirdPerson;
import com.github.leawind.thirdperson.api.model.Actor;
import com.github.leawind.thirdperson.api.model.Viewpoint;
import com.github.leawind.thirdperson.impl.model.ActorImpl;
import com.github.leawind.thirdperson.impl.model.ViewpointImpl;
import com.github.leawind.thirdperson.utils.Suppressor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class ThirdPersonImpl implements ThirdPerson {
  private final Minecraft minecraft;

  private @Nullable ViewpointImpl viewpoint = null;
  private @Nullable ActorImpl actor = null;

  private final Map<Class<? extends States>, States> statesMap = new HashMap<>();

  /// @throws NullPointerException if `minecraft` is null
  private ThirdPersonImpl(@NonNull Minecraft minecraft) throws NullPointerException {
    this.minecraft = Objects.requireNonNull(minecraft);

    updateViewpoint();
    updateActor();
  }

  @Override
  public @NonNull Minecraft getMinecraft() {
    return minecraft;
  }

  @Override
  public @Nullable Viewpoint getViewpointOrNull() {
    return viewpoint;
  }

  @Override
  public @Nullable Actor getActorOrNull() {
    return actor;
  }

  @Override
  public boolean isAvailable() {
    return Suppressor.alwaysTrue()
        && ThirdPerson.getConfigManager().getConfig().is_mod_enabled
        && viewpoint != null
        && actor != null;
  }

  @Override
  public void reset() {
    updateViewpoint();
    updateActor();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <S extends States> S getStates(Class<S> clazz) {
    States states = statesMap.get(clazz);

    if (states == null) {
      try {
        states = clazz.getConstructor(ThirdPerson.class).newInstance(this);
      } catch (InstantiationException
          | IllegalAccessException
          | InvocationTargetException
          | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
      statesMap.put(clazz, states);
    }

    return (S) states;
  }

  private void updateViewpoint() {
    var gameRenderer = minecraft.gameRenderer;
    if (Suppressor.notNull(gameRenderer)) {
      var camera = gameRenderer.getMainCamera();

      if (viewpoint != null && viewpoint.camera() == camera) {
        return;
      }

      this.viewpoint = new ViewpointImpl(camera);
    }
  }

  private void updateActor() {
    var cameraEntity = minecraft.getCameraEntity();
    if (cameraEntity != null) {
      if (actor != null && actor.entity() == cameraEntity) {
        return;
      }

      actor = new ActorImpl(cameraEntity);
    }
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
