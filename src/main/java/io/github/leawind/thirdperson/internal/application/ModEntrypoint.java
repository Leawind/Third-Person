package io.github.leawind.thirdperson.internal.application;

public final class ModEntrypoint {
  private ModEntrypoint() {}

  public static void initialize() {
    ThirdPersonRuntime.getInstance().initialize();
  }
}
