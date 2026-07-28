package io.github.leawind.thirdperson.internal.core.camera;

/// Persistable camera placement selected independently for normal and aiming modes.
public record CameraProfile(
    double distance,
    double offsetX,
    double offsetY,
    double centeredOffsetY,
    double fovMultiplier,
    boolean centered) {
  public CameraProfile {
    if (!Double.isFinite(distance)
        || !Double.isFinite(offsetX)
        || !Double.isFinite(offsetY)
        || !Double.isFinite(centeredOffsetY)
        || !Double.isFinite(fovMultiplier)
        || distance < 0.0
        || distance > 16.0
        || Math.abs(offsetX) > 1.0
        || Math.abs(offsetY) > 1.0
        || Math.abs(centeredOffsetY) > 1.0
        || fovMultiplier < 0.25
        || fovMultiplier > 2.0) {
      throw new IllegalArgumentException("Invalid camera profile");
    }
  }

  public CameraParameters cameraParameters() {
    return centered
        ? new CameraParameters(distance, 0.0, centeredOffsetY)
        : new CameraParameters(distance, offsetX, offsetY);
  }

  public CameraProfile withDistance(double value) {
    return new CameraProfile(
        value, offsetX, offsetY, centeredOffsetY, fovMultiplier, centered);
  }

  public CameraProfile withOffsetX(double value) {
    return new CameraProfile(
        distance, value, offsetY, centeredOffsetY, fovMultiplier, centered);
  }

  public CameraProfile withOffsetY(double value) {
    return new CameraProfile(
        distance, offsetX, value, centeredOffsetY, fovMultiplier, centered);
  }

  public CameraProfile withCenteredOffsetY(double value) {
    return new CameraProfile(distance, offsetX, offsetY, value, fovMultiplier, centered);
  }

  public CameraProfile withFovMultiplier(double value) {
    return new CameraProfile(distance, offsetX, offsetY, centeredOffsetY, value, centered);
  }

  public CameraProfile withCentered(boolean value) {
    return centered == value
        ? this
        : new CameraProfile(distance, offsetX, offsetY, centeredOffsetY, fovMultiplier, value);
  }
}
