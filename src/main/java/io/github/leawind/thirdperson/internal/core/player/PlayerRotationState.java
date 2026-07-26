package io.github.leawind.thirdperson.internal.core.player;

/// Minecraft-independent state considered when selecting how the local player should rotate.
public record PlayerRotationState(
    boolean aiming,
    boolean swimming,
    boolean sprinting,
    boolean fallFlying,
    boolean interacting,
    boolean passenger,
    boolean vehicleLivingEntity) {}
