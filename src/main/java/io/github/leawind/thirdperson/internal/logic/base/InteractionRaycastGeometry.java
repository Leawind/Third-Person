package io.github.leawind.thirdperson.internal.logic.base;

/// Pure range calculations for camera-origin interaction raycasts.
public final class InteractionRaycastGeometry {
  private InteractionRaycastGeometry() {}

  /// Extends only the candidate search ray so a camera behind the player can still cover every
  /// point within the player's original interaction range.
  public static double candidateRange(
      double blockInteractionRange,
      double entityInteractionRange,
      double cameraToPlayerEyeDistance) {
    if (!Double.isFinite(blockInteractionRange)
        || !Double.isFinite(entityInteractionRange)
        || !Double.isFinite(cameraToPlayerEyeDistance)
        || blockInteractionRange < 0.0
        || entityInteractionRange < 0.0
        || cameraToPlayerEyeDistance < 0.0) {
      return Double.NaN;
    }
    return Math.max(blockInteractionRange, entityInteractionRange) + cameraToPlayerEyeDistance;
  }

  /// Matches vanilla's strict `closerThan` range check without taking a square root.
  public static boolean isWithinRange(double distanceSquared, double interactionRange) {
    return Double.isFinite(distanceSquared)
        && Double.isFinite(interactionRange)
        && distanceSquared >= 0.0
        && interactionRange >= 0.0
        && distanceSquared < interactionRange * interactionRange;
  }
}
