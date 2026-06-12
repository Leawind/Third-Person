package com.github.leawind.thirdperson.minecraft.bridge.events.context;

public class EventContext<T> {
  private T value;

  public EventContext(T value) {
    this.set(value);
  }

  public T get() {
    return value;
  }

  public void set(T value) {
    this.value = value;
  }
}
