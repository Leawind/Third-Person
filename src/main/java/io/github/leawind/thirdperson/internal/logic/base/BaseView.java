package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.rotation.LookRotation;
import java.util.Optional;

/// Read-only base capabilities used while the scheduling layer selects parameters.
public interface BaseView {
  boolean isCameraControlEnabled();

  /// Samples the smoothed camera-entity opacity between the previous and current client ticks.
  float cameraEntityOpacity(float partialTick);

  boolean isControllingLocalPlayer();

  boolean hasDirectionalMovementIntent(double minimumMagnitude);

  Optional<LookRotation> resolveInterestPointRotation();

  Optional<LookRotation> resolvePredictedCameraTargetRotation();
}
