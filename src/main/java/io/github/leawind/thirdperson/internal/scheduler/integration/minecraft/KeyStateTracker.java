package io.github.leawind.thirdperson.internal.scheduler.integration.minecraft;

import net.minecraft.client.KeyMapping;
import org.jspecify.annotations.Nullable;

/// Tracks key state transitions across ticks for one vanilla key mapping.
///
/// This is intentionally maintained locally instead of depending on Perspective API internals.
public final class KeyStateTracker {
  public static Builder builder(KeyMapping keyMapping) {
    var tracker = new KeyStateTracker(keyMapping);
    return tracker.new Builder();
  }

  private final KeyMapping keyMapping;
  private int holdTicks = 4;

  private boolean wasDown;
  private int heldTicks;
  private boolean holdTriggered;
  private boolean pressTriggered;

  private @Nullable Runnable onDownHandler;
  private @Nullable Runnable onUpHandler;
  private @Nullable Runnable onPressHandler;
  private @Nullable Runnable onHoldHandler;
  private @Nullable Runnable onHoldStopHandler;

  private KeyStateTracker(KeyMapping keyMapping) {
    this.keyMapping = keyMapping;
  }

  /// Updates the tracker. Call exactly once per client tick.
  public KeyStateTracker tick() {
    if (keyMapping.isDown()) {
      if (!wasDown) {
        wasDown = true;
        heldTicks = 0;
        holdTriggered = false;
        pressTriggered = false;
        trigger(onDownHandler);
      }
      heldTicks++;
      if (!holdTriggered && heldTicks >= holdTicks) {
        holdTriggered = true;
        trigger(onHoldHandler);
      }
    } else {
      if (wasDown) {
        if (!holdTriggered && !pressTriggered) {
          pressTriggered = true;
          trigger(onPressHandler);
        } else if (holdTriggered) {
          trigger(onHoldStopHandler);
        }
        trigger(onUpHandler);
      }
      wasDown = false;
      heldTicks = 0;
    }
    return this;
  }

  public int getHoldTicks() {
    return holdTicks;
  }

  public KeyStateTracker setHoldTicks(int ticks) {
    if (ticks < 1) {
      throw new IllegalArgumentException("Hold threshold must be positive");
    }
    holdTicks = ticks;
    return this;
  }

  public void drain() {
    while (keyMapping.consumeClick()) {}
  }

  public KeyMapping key() {
    return keyMapping;
  }

  public boolean isDown() {
    return wasDown;
  }

  public boolean isHoldTriggered() {
    return holdTriggered;
  }

  private static void trigger(@Nullable Runnable handler) {
    if (handler != null) {
      handler.run();
    }
  }

  public final class Builder {
    private Builder() {}

    public Builder setHoldTicks(int ticks) {
      KeyStateTracker.this.setHoldTicks(ticks);
      return this;
    }

    public Builder onDown(Runnable handler) {
      KeyStateTracker.this.onDownHandler = handler;
      return this;
    }

    public Builder onUp(Runnable handler) {
      KeyStateTracker.this.onUpHandler = handler;
      return this;
    }

    public Builder onPress(Runnable handler) {
      KeyStateTracker.this.onPressHandler = handler;
      return this;
    }

    public Builder onHoldStart(Runnable handler) {
      KeyStateTracker.this.onHoldHandler = handler;
      return this;
    }

    public Builder onHoldStop(Runnable handler) {
      KeyStateTracker.this.onHoldStopHandler = handler;
      return this;
    }

    public KeyStateTracker build() {
      return KeyStateTracker.this;
    }
  }
}
