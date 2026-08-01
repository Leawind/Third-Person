package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/// Neutral bridge event for adjusting the position of a sound with a known entity source.
public final class SoundSourcePositionEvent {
  private static final SingleEventHandler<Listener> HANDLER = new SingleEventHandler<>();

  private SoundSourcePositionEvent() {}

  public static void register(Listener listener) {
    HANDLER.install(listener);
  }

  public static Vec3 emit(Entity source, Vec3 vanillaPosition) {
    Objects.requireNonNull(source, "source");
    Objects.requireNonNull(vanillaPosition, "vanillaPosition");
    Listener listener = HANDLER.get();
    return listener == null
        ? vanillaPosition
        : Objects.requireNonNull(
            listener.adjustPosition(source, vanillaPosition), "adjustedPosition");
  }

  @FunctionalInterface
  public interface Listener {
    Vec3 adjustPosition(Entity source, Vec3 vanillaPosition);
  }
}
