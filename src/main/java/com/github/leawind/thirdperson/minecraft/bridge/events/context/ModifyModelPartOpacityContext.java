package com.github.leawind.thirdperson.minecraft.bridge.events.context;

/// 用于修改模型部件渲染时的透明度
///
/// <p>当渲染模型部件的 Cube 时，此事件允许修改 ARGB 颜色值中的 Alpha 通道
public class ModifyModelPartOpacityContext {
  /// ARGB 颜色值（可修改）
  public int argb;

  /// 部分刻（可用来计算平滑的透明度变化）
  public final float partialTick;

  public ModifyModelPartOpacityContext(int argb, float partialTick) {
    this.argb = argb;
    this.partialTick = partialTick;
  }
}
