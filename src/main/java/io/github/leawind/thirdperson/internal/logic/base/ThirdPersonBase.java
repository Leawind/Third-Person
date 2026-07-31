package io.github.leawind.thirdperson.internal.logic.base;

import java.util.Optional;

/// Public boundary through which the scheduling layer controls the base layer.
public interface ThirdPersonBase {
  void applyParameters(BaseParameters parameters);

  BaseParameters parameters();

  boolean isCameraControlEnabled();

  boolean isControllingLocalPlayer();

  Optional<LookRotation> resolveInterestPointRotation();

  Optional<LookRotation> resolvePredictedCameraTargetRotation();
}
