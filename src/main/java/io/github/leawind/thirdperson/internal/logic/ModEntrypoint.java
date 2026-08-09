package io.github.leawind.thirdperson.internal.logic;

import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.logic.base.BaseRuntime;
import io.github.leawind.thirdperson.internal.core.schedule.SchedulerRuntime;

/// Outermost composition root installed by loader entrypoints.
public final class ModEntrypoint {
  private ModEntrypoint() {}

  public static void initialize() {
    BaseRuntime base = BaseRuntime.getInstance();
    if (base.initialize()) {
      ThirdPerson.LOGGER.info("{} initialized", ThirdPerson.MOD_NAME);
    }
    SchedulerRuntime.getInstance().initialize(base);
    ModEvents.register();
  }
}
