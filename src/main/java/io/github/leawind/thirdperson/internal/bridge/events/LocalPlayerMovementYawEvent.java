package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.player.LocalPlayer;

/// Neutral bridge event for the yaw used to transform local-player movement input.
public final class LocalPlayerMovementYawEvent {
  private static final CopyOnWriteArrayList<Listener> LISTENERS = new CopyOnWriteArrayList<>();

  private LocalPlayerMovementYawEvent() {}

  public static void register(Listener listener) {
    LISTENERS.add(Objects.requireNonNull(listener, "listener"));
  }

  public static float emit(LocalPlayer player, float vanillaYaw) {
    Objects.requireNonNull(player, "player");
    float yaw = vanillaYaw;
    for (Listener listener : LISTENERS) {
      yaw = listener.modifyYaw(player, yaw);
    }
    return yaw;
  }

  @FunctionalInterface
  public interface Listener {
    float modifyYaw(LocalPlayer player, float vanillaYaw);
  }
}
