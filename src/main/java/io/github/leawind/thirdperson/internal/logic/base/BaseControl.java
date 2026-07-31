package io.github.leawind.thirdperson.internal.logic.base;

/// Commands through which the scheduling layer updates the base layer.
public interface BaseControl {
  void applyParameters(BaseParameters parameters);
}
