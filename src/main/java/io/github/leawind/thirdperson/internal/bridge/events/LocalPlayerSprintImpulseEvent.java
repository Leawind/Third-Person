package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.player.LocalPlayer;

/// Neutral bridge event for the directional-input condition used by vanilla sprinting.
public final class LocalPlayerSprintImpulseEvent {
  private static final CopyOnWriteArrayList<Listener> LISTENERS = new CopyOnWriteArrayList<>();

  private LocalPlayerSprintImpulseEvent() {}

  public static void register(Listener listener) {
    LISTENERS.add(Objects.requireNonNull(listener, "listener"));
  }

  public static boolean emit(
      LocalPlayer player,
      boolean vanillaResult,
      double leftImpulse,
      double forwardImpulse,
      double minimumMagnitude) {
    Objects.requireNonNull(player, "player");
    boolean result = vanillaResult;
    for (Listener listener : LISTENERS) {
      result = listener.modify(player, result, leftImpulse, forwardImpulse, minimumMagnitude);
    }
    return result;
  }

  @FunctionalInterface
  public interface Listener {
    boolean modify(
        LocalPlayer player,
        boolean vanillaResult,
        double leftImpulse,
        double forwardImpulse,
        double minimumMagnitude);
  }
}
