package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import net.minecraft.client.player.LocalPlayer;

/// Neutral bridge event emitted before the local player's vanilla turn handling.
public final class LocalPlayerTurnEvent {
  private static final SingleEventHandler<Listener> HANDLER = new SingleEventHandler<>();

  private LocalPlayerTurnEvent() {}

  public static void register(Listener listener) {
    HANDLER.install(listener);
  }

  public static boolean emit(LocalPlayer player, double rawYaw, double rawPitch) {
    Objects.requireNonNull(player, "player");
    Listener listener = HANDLER.get();
    return listener != null && listener.onTurn(player, rawYaw, rawPitch);
  }

  @FunctionalInterface
  public interface Listener {
    boolean onTurn(LocalPlayer player, double rawYaw, double rawPitch);
  }
}
