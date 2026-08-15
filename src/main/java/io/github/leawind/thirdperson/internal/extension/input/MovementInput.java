package io.github.leawind.thirdperson.internal.extension.input;

/// Local movement input before or after mapping into Minecraft's active input basis.
public record MovementInput(float leftImpulse, float forwardImpulse) {}
