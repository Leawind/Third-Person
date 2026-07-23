package io.github.leawind.thirdperson.internal.logic;

public final class ModEntrypoint {
  private ModEntrypoint() {}

  public static void initialize() {
    ModEvents.register();
  }
}
