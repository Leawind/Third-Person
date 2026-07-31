package io.github.leawind.thirdperson.internal.logic.base;

import java.util.Optional;

/// Read-only base capabilities used while the scheduling layer selects parameters.
public interface BaseView {
  boolean isCameraControlEnabled();

  boolean isControllingLocalPlayer();

  Optional<LookRotation> resolveInterestPointRotation();

  Optional<LookRotation> resolvePredictedCameraTargetRotation();
}
