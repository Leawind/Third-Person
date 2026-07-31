package io.github.leawind.thirdperson.internal.logic.base;

/// Commands through which the scheduling layer updates the base layer.
public interface BaseControl {
  void applyParameters(BaseParameters parameters);

  /// Selects the target opacity of the entity to which the active camera is attached.
  void setCameraEntityOpacityTarget(double opacity);
}
