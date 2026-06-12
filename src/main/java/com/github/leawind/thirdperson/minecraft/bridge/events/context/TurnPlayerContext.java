package com.github.leawind.thirdperson.minecraft.bridge.events.context;

import net.minecraft.world.entity.Entity;

public final class TurnPlayerContext {
  public final Entity entity;

  /// 偏航角变化量
  public final double yRotDelta;

  /// 俯仰角变化量
  public final double xRotDelta;

  /// 是否取消默认操作
  ///
  /// Minecraft 默认操作是：旋转实体相应角度，并触发载具实体的“乘客旋转”事件
  public boolean cancelDefault = false;

  public TurnPlayerContext(Entity entity, double yRotDelta, double xRotDelta) {
    this.entity = entity;
    this.yRotDelta = yRotDelta;
    this.xRotDelta = xRotDelta;
  }
}
