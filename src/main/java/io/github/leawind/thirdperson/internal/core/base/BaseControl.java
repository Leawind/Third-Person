package io.github.leawind.thirdperson.internal.core.base;

import io.github.leawind.thirdperson.internal.core.base.rotation.LookRotation;

/// Commands through which the scheduling layer updates the base layer.
public interface BaseControl {
  void applyParameters(BaseParameters parameters);

  /// Commits an interaction-facing rotation locally and to the server before vanilla interacts.
  void commitInteractionRotation(LookRotation rotation);
}
