package io.github.leawind.thirdperson.internal.logic.scheduler;

import java.util.Objects;

/// Minecraft-independent state considered when selecting how the local player should rotate.
public record PlayerRotationState(
    NormalPlayerRotationMode normalMode,
    boolean aiming,
    boolean swimming,
    boolean sprinting,
    boolean fallFlying,
    boolean interacting,
    boolean passenger,
    boolean vehicleLivingEntity,
    boolean moving) {
  public PlayerRotationState {
    Objects.requireNonNull(normalMode, "normalMode");
  }
}
