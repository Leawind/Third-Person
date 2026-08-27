package io.github.leawind.thirdperson.internal.core.base;

import io.github.leawind.thirdperson.internal.core.base.rotation.LookRotation;
import java.util.Optional;

/// Complete internal boundary exposed by the base layer to the scheduling layer.
public interface ThirdPersonBase {
  /// Commands through which the scheduling layer updates the base layer.
  void applyParameters(BaseParameters parameters);

  /// Commits an interaction-facing rotation locally and to the server before vanilla interacts.
  void commitInteractionRotation(LookRotation rotation);

  /// Read-only base capabilities used while the scheduling layer selects parameters.
  boolean isCameraControlEnabled();

  boolean isControllingLocalPlayer();

  boolean hasDirectionalMovementIntent(double minimumMagnitude);

  Optional<LookRotation> resolveInterestPointRotation();

  Optional<LookRotation> resolvePredictedCameraTargetRotation();
}
