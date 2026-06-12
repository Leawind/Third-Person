package com.github.leawind.thirdperson.impl;

import com.github.leawind.thirdperson.api.ThirdPerson;
import com.github.leawind.thirdperson.minecraft.logic.ThirdPersonKeys;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.jspecify.annotations.NonNull;

public final class ThirdPersonStates extends ThirdPerson.States {

  public ThirdPersonStates(ThirdPerson thirdPerson) {
    super(thirdPerson);
  }

  /// 移动脉冲
  public final @NonNull Vector3d impulse = new Vector3d(0);

  /// 移动脉冲的水平分量
  public final @NonNull Vector2d impulseHorizon = new Vector2d(0);

  /// @see ThirdPersonKeys#TOGGLE_AIMING
  public boolean isToggleToAiming = false;

  public boolean isPerspectiveInverted = false;
}
