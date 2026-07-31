package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import net.minecraft.client.player.LocalPlayer;

/// Neutral bridge event for the directional-input condition used by vanilla sprinting.
public final class LocalPlayerSprintImpulseEvent {
  private static final SingleEventHandler<Listener> HANDLER = new SingleEventHandler<>();

  private LocalPlayerSprintImpulseEvent() {}

  public static void register(Listener listener) {
    HANDLER.install(listener);
  }

  public static boolean emit(
      LocalPlayer player,
      boolean vanillaResult,
      double leftImpulse,
      double forwardImpulse,
      double minimumMagnitude) {
    Objects.requireNonNull(player, "player");
    Listener listener = HANDLER.get();
    return listener == null
        ? vanillaResult
        : listener.modify(
            player, vanillaResult, leftImpulse, forwardImpulse, minimumMagnitude);
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
