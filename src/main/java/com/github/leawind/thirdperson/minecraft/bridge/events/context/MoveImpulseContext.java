package com.github.leawind.thirdperson.minecraft.bridge.events.context;

public class MoveImpulseContext {
  public float forwardImpulse;
  public float leftImpulse;

  public MoveImpulseContext(float forwardImpulse, float leftImpulse) {
    this.forwardImpulse = forwardImpulse;
    this.leftImpulse = leftImpulse;
  }
}
