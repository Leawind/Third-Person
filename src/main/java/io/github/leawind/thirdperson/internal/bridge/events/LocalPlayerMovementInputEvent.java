package io.github.leawind.thirdperson.internal.bridge.events;

import io.github.leawind.thirdperson.internal.extension.input.MovementInput;
import java.util.Objects;
import net.minecraft.client.player.LocalPlayer;

/// Neutral bridge event emitted after vanilla updates the local player's movement input.
public final class LocalPlayerMovementInputEvent {
  private static final SingleEventHandler<Listener> HANDLER = new SingleEventHandler<>();

  private LocalPlayerMovementInputEvent() {}

  public static void register(Listener listener) {
    HANDLER.install(listener);
  }

  public static MovementInput emit(
      LocalPlayer player, float vanillaLeftImpulse, float vanillaForwardImpulse) {
    Objects.requireNonNull(player, "player");
    MovementInput input = new MovementInput(vanillaLeftImpulse, vanillaForwardImpulse);
    Listener listener = HANDLER.get();
    if (listener == null) {
      return input;
    }
    return Objects.requireNonNull(listener.modifyInput(player, input), "modifiedInput");
  }

  @FunctionalInterface
  public interface Listener {
    MovementInput modifyInput(LocalPlayer player, MovementInput vanillaInput);
  }
}
