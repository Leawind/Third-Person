package io.github.leawind.thirdperson.internal.core.camera;

/// Size inputs used to turn a persisted distance factor into a world-space camera distance.
public record CameraSubjectDimensions(double bodyRadius, double vehicleTotalSize) {
  public CameraSubjectDimensions {
    if (!Double.isFinite(bodyRadius)
        || !Double.isFinite(vehicleTotalSize)
        || bodyRadius < 0.0
        || vehicleTotalSize < 0.0) {
      throw new IllegalArgumentException("Invalid camera subject dimensions");
    }
  }

  public double resolveDistance(double distanceFactor, double fovMultiplier) {
    if (!Double.isFinite(distanceFactor)
        || !Double.isFinite(fovMultiplier)
        || distanceFactor < 0.0
        || fovMultiplier <= 0.0) {
      throw new IllegalArgumentException("Invalid camera distance inputs");
    }
    double distance = (distanceFactor * vehicleTotalSize + bodyRadius) / fovMultiplier;
    if (!Double.isFinite(distance)) {
      throw new IllegalArgumentException("Resolved camera distance is not finite");
    }
    return distance;
  }
}
