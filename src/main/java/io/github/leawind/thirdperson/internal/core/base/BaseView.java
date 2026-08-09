package io.github.leawind.thirdperson.internal.core.base;

import io.github.leawind.thirdperson.internal.core.base.rotation.LookRotation;
import java.util.Optional;

/// Read-only base capabilities used while the scheduling layer selects parameters.
public interface BaseView {
  boolean isCameraControlEnabled();

  boolean isControllingLocalPlayer();

  boolean hasDirectionalMovementIntent(double minimumMagnitude);

  Optional<LookRotation> resolveInterestPointRotation();

  Optional<LookRotation> resolvePredictedCameraTargetRotation();
}
