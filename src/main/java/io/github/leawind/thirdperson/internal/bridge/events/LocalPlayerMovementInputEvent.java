package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.player.LocalPlayer;

/// Neutral bridge event emitted after vanilla updates the local player's movement input.
public final class LocalPlayerMovementInputEvent {
  private static final CopyOnWriteArrayList<Listener> LISTENERS = new CopyOnWriteArrayList<>();

  private LocalPlayerMovementInputEvent() {}

  public static void register(Listener listener) {
    LISTENERS.add(Objects.requireNonNull(listener, "listener"));
  }

  public static MovementInput emit(
      LocalPlayer player, float vanillaLeftImpulse, float vanillaForwardImpulse) {
    Objects.requireNonNull(player, "player");
    MovementInput input = new MovementInput(vanillaLeftImpulse, vanillaForwardImpulse);
    for (Listener listener : LISTENERS) {
      input = Objects.requireNonNull(listener.modifyInput(player, input), "modifiedInput");
    }
    return input;
  }

  public record MovementInput(float leftImpulse, float forwardImpulse) {}

  @FunctionalInterface
  public interface Listener {
    MovementInput modifyInput(LocalPlayer player, MovementInput vanillaInput);
  }
}
