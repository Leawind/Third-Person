package io.github.leawind.thirdperson.internal.logic.base.rotation;

public record LookRotation(float yawDegrees, float pitchDegrees) {
  public LookRotation {
    if (!Float.isFinite(yawDegrees) || !Float.isFinite(pitchDegrees)) {
      throw new IllegalArgumentException("Look rotation must be finite");
    }
  }
}
