package io.github.leawind.thirdperson.internal.logic.base.camera;

/// Camera placement requested by the scheduling layer for the current frame.
public record CameraProfile(
    double distanceFactor,
    double offsetX,
    double offsetY,
    double centeredOffsetY,
    double fovMultiplier,
    boolean centered) {
  public CameraProfile {
    if (!Double.isFinite(distanceFactor)
        || !Double.isFinite(offsetX)
        || !Double.isFinite(offsetY)
        || !Double.isFinite(centeredOffsetY)
        || !Double.isFinite(fovMultiplier)
        || distanceFactor < 0.0
        || distanceFactor > 16.0
        || Math.abs(offsetX) > 1.0
        || Math.abs(offsetY) > 1.0
        || Math.abs(centeredOffsetY) > 1.0
        || fovMultiplier < 0.25
        || fovMultiplier > 2.0) {
      throw new IllegalArgumentException("Invalid camera profile");
    }
  }

  public CameraProfile withDistanceFactor(double value) {
    return new CameraProfile(value, offsetX, offsetY, centeredOffsetY, fovMultiplier, centered);
  }

  public CameraProfile withOffsetX(double value) {
    return new CameraProfile(
        distanceFactor, value, offsetY, centeredOffsetY, fovMultiplier, centered);
  }

  public CameraProfile withOffsetY(double value) {
    return new CameraProfile(
        distanceFactor, offsetX, value, centeredOffsetY, fovMultiplier, centered);
  }

  public CameraProfile withCenteredOffsetY(double value) {
    return new CameraProfile(distanceFactor, offsetX, offsetY, value, fovMultiplier, centered);
  }

  public CameraProfile withFovMultiplier(double value) {
    return new CameraProfile(distanceFactor, offsetX, offsetY, centeredOffsetY, value, centered);
  }

  public CameraProfile withCentered(boolean value) {
    return centered == value
        ? this
        : new CameraProfile(
            distanceFactor, offsetX, offsetY, centeredOffsetY, fovMultiplier, value);
  }
}
