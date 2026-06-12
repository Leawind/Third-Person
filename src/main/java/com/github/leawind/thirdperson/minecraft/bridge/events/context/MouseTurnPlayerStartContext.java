package com.github.leawind.thirdperson.minecraft.bridge.events.context;

public class MouseTurnPlayerStartContext {
  public final double accumulatedDX;
  public final double accumulatedDY;

  /// 是否取消默认操作
  ///
  /// 如果设置为 true，则后续的玩家旋转处理将会被取消，鼠标累积位移量也会被重置
  public boolean cancelDefault = false;

  public MouseTurnPlayerStartContext(double accumulatedDX, double accumulatedDY) {
    this.accumulatedDX = accumulatedDX;
    this.accumulatedDY = accumulatedDY;
  }
}
