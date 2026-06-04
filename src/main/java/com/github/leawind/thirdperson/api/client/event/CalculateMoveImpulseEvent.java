package com.github.leawind.thirdperson.api.client.event;

import com.github.leawind.thirdperson.api.base.ModEvent;
import net.minecraft.client.player.KeyboardInput;

public class CalculateMoveImpulseEvent implements ModEvent {
  public final KeyboardInput input;

  public float forwardImpulse = 0;
  public float leftImpulse = 0;

  public CalculateMoveImpulseEvent(KeyboardInput input) {
    this.input = input;
  }

  @Override
  public boolean set() {
    return true;
  }
}
