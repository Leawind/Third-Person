package io.github.leawind.thirdperson.internal.base.core.camera;

/// Validated parameters for one camera composition profile.
public record CameraParameters(
    double distance, double anchorNdcX, double anchorNdcY) {
  public CameraParameters {
    if (!Double.isFinite(distance)
        || !Double.isFinite(anchorNdcX)
        || !Double.isFinite(anchorNdcY)
        || distance < 0.0
        || Math.abs(anchorNdcX) > 1.0
        || Math.abs(anchorNdcY) > 1.0) {
      throw new IllegalArgumentException("Invalid camera parameters");
    }
  }
}
