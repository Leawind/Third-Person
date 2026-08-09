package io.github.leawind.thirdperson.internal.core.base;

import io.github.leawind.thirdperson.internal.core.base.math.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Vector3dc;

/// Pure range calculations for camera-directed interaction raycasts.
public final class InteractionRaycastGeometry {
  static final double MAX_CAMERA_ORIGIN_EXTENSION = 256.0;

  private InteractionRaycastGeometry() {}

  /// Chooses the final interaction ray without changing the camera's intent point.
  ///
  /// The configured origin affects only this final ray: camera-origin interaction keeps the
  /// camera ray, while player-eye interaction converges from the eyes onto the camera intent.
  public static Optional<WorldRay> selectInteractionRay(
      RaycastOrigin origin, WorldRay cameraRay, Vector3dc playerEye, Vector3dc cameraIntentPoint) {
    Objects.requireNonNull(origin, "origin");
    Objects.requireNonNull(cameraRay, "cameraRay");
    Objects.requireNonNull(playerEye, "playerEye");
    Objects.requireNonNull(cameraIntentPoint, "cameraIntentPoint");
    if (!FiniteMath.isFinite(playerEye) || !FiniteMath.isFinite(cameraIntentPoint)) {
      return Optional.empty();
    }
    return origin == RaycastOrigin.CAMERA
        ? Optional.of(cameraRay)
        : WorldRay.toward(playerEye, cameraIntentPoint);
  }

  /// Extends only the candidate search ray when its chosen origin is displaced from the player
  /// eyes.
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

  /// Bounds the camera-origin compensation used while constructing world-space ray queries.
  ///
  /// A smoothed camera can temporarily remain far from its subject after a teleport. That
  /// distance must not become an unbounded entity-search ray.
  public static double capCameraOriginExtension(double originExtension) {
    if (!Double.isFinite(originExtension) || originExtension < 0.0) {
      return Double.NaN;
    }
    return Math.min(originExtension, MAX_CAMERA_ORIGIN_EXTENSION);
  }

  /// Matches vanilla's strict `closerThan` range check without taking a square root.
  public static boolean isWithinRange(double distanceSquared, double interactionRange) {
    return Double.isFinite(distanceSquared)
        && Double.isFinite(interactionRange)
        && distanceSquared >= 0.0
        && interactionRange >= 0.0
        && distanceSquared < interactionRange * interactionRange;
  }

  /// Extends item attack candidate discovery for forward movement and a displaced ray origin.
  /// Neither extension changes the item's configured reach eligibility.
  public static double attackCandidateRange(
      double maximumRange, double forwardMovement, double originExtension) {
    if (!Double.isFinite(maximumRange)
        || !Double.isFinite(forwardMovement)
        || !Double.isFinite(originExtension)
        || maximumRange < 0.0
        || forwardMovement < 0.0
        || originExtension < 0.0) {
      return Double.NaN;
    }
    return maximumRange + forwardMovement + originExtension;
  }

  /// Preserves vanilla item attack eligibility while testing a world-space candidate location.
  /// The hitbox margin expands both boundaries; forward movement expands only the far boundary.
  public static boolean isWithinAttackRange(
      double distanceSquared,
      double minimumRange,
      double maximumRange,
      double hitboxMargin,
      double forwardMovement) {
    if (!Double.isFinite(distanceSquared)
        || !Double.isFinite(minimumRange)
        || !Double.isFinite(maximumRange)
        || !Double.isFinite(hitboxMargin)
        || !Double.isFinite(forwardMovement)
        || distanceSquared < 0.0
        || minimumRange < 0.0
        || maximumRange < minimumRange
        || hitboxMargin < 0.0
        || forwardMovement < 0.0) {
      return false;
    }
    double minimumDistance = Math.max(0.0, minimumRange - hitboxMargin);
    double maximumDistance = maximumRange + hitboxMargin + forwardMovement;
    return distanceSquared >= minimumDistance * minimumDistance
        && distanceSquared <= maximumDistance * maximumDistance;
  }
}
