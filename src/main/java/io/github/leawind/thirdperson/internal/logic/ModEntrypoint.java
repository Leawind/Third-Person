package io.github.leawind.thirdperson.internal.logic;

import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.core.schedule.SchedulerRuntime;
import io.github.leawind.thirdperson.internal.extension.minecraft.MinecraftExtensions;
import io.github.leawind.thirdperson.internal.extension.sable.SableExtensions;
import io.github.leawind.thirdperson.internal.logic.base.BaseRuntime;

/// Outermost composition root installed by loader entrypoints.
public final class ModEntrypoint {
  private ModEntrypoint() {}

  public static void initialize() {
    MinecraftExtensions.register();
    SableExtensions.register();
    BaseRuntime base = BaseRuntime.getInstance();
    if (base.initialize()) {
      ThirdPerson.LOGGER.info("{} initialized", ThirdPerson.MOD_NAME);
    }
    SchedulerRuntime.getInstance().initialize(base);
    ModEvents.register();
  }
}
