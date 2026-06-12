package com.github.leawind.thirdperson.utils.modkeymapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.KeyMapping;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModKeyMappingImpl extends KeyMapping implements ModKeyMapping {
  private static final Logger LOGGER = LoggerFactory.getLogger(ModKeyMappingImpl.class);

  private long holdMs = 300;
  private long pressMs = 300;
  private long keyDownTime = 0;
  private @Nullable Timer timer = null;
  private @Nullable Supplier<Boolean> onDown = null;
  private @Nullable Supplier<Boolean> onUp = null;
  private @Nullable Supplier<Boolean> onHold = null;
  private @Nullable Supplier<Boolean> onPress = null;
  private @Nullable Collection<BooleanSupplier> checkers = null;

  /**
   * @param id 按键映射的标识符，用于可翻译文本
   * @param defaultValue 默认按键
   * @param categoryKey 类别标识符，用于可翻译文本
   */
  public ModKeyMappingImpl(String id, int defaultValue, Category categoryKey) {
    super(id, defaultValue, categoryKey);
  }

  @Override
  public boolean isDown() {
    return super.isDown();
  }

  @Override
  public void setDown(boolean down) {
    boolean wasDown = isDown();
    super.setDown(down);
    long now = System.currentTimeMillis();
    if (!wasDown && down) {
      // key down
      if (runIfNonNull(onDown)) {
        return;
      }
      keyDownTime = now;
      if (onHold != null) {
        timer = new Timer();
        timer.schedule(
            new TimerTask() {
              @Override
              public void run() {
                runIfNonNull(onHold);
                timer = null;
              }
            },
            holdMs);
      }
    } else if (wasDown && !down) {
      // key up
      long sinceKeydown = now - keyDownTime;
      if (runIfNonNull(onUp)) {
        return;
      }
      if (sinceKeydown < pressMs) {
        if (timer != null) {
          timer.cancel();
          timer = null;
        }
        runIfNonNull(onPress);
      }
    }
  }

  @Override
  public ModKeyMappingImpl setHoldDuration(long ms) {
    this.holdMs = ms;
    return this;
  }

  @Override
  public ModKeyMappingImpl setPressDuration(long ms) {
    this.pressMs = ms;
    return this;
  }

  @Override
  public ModKeyMapping when(BooleanSupplier checker) {
    if (checkers == null) {
      checkers = new ArrayList<>();
    }
    checkers.add(checker);
    return this;
  }

  @Override
  public ModKeyMappingImpl onDown(Runnable handler) {
    return onDown(
        () -> {
          handler.run();
          return false;
        });
  }

  @Override
  public ModKeyMappingImpl onDown(Supplier<Boolean> handler) {
    onDown = handler;
    return this;
  }

  @Override
  public ModKeyMappingImpl onUp(Runnable handler) {
    return onUp(
        () -> {
          handler.run();
          return false;
        });
  }

  @Override
  public ModKeyMappingImpl onUp(Supplier<Boolean> handler) {
    onUp = handler;
    return this;
  }

  @Override
  public ModKeyMappingImpl onPress(Runnable handler) {
    return onPress(
        () -> {
          handler.run();
          return false;
        });
  }

  @Override
  public ModKeyMappingImpl onPress(Supplier<Boolean> handler) {
    onPress = handler;
    return this;
  }

  @Override
  public ModKeyMappingImpl onHold(Runnable handler) {
    return onHold(
        () -> {
          handler.run();
          return false;
        });
  }

  @Override
  public ModKeyMappingImpl onHold(Supplier<Boolean> handler) {
    onHold = handler;
    return this;
  }

  private boolean runIfNonNull(@Nullable Supplier<Boolean> handler) {
    if (handler == null) {
      return false;
    }

    if (checkers != null) {
      for (var checker : checkers) {
        boolean checkResult;

        try {
          checkResult = checker.getAsBoolean();
        } catch (RuntimeException e) {
          LOGGER.warn("RuntimeException thrown while checking key event condition", e);
          return false;
        }

        if (!checkResult) {
          return false;
        }
      }
    }

    return handler.get();
  }
}
