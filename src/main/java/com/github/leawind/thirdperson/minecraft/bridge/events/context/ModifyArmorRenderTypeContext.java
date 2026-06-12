package com.github.leawind.thirdperson.minecraft.bridge.events.context;

import net.minecraft.resources.Identifier;

/// 用于修改盔甲渲染类型
///
/// <p>当渲染盔甲时，此事件允许决定是否使用半透明渲染类型
public class ModifyArmorRenderTypeContext {
  /// 资源标识符
  public final Identifier resourceLocation;

  /// 是否使用半透明渲染类型（可修改，默认为 false 即使用原版渲染类型）
  public boolean useTranslucent = false;

  public ModifyArmorRenderTypeContext(Identifier resourceLocation) {
    this.resourceLocation = resourceLocation;
  }
}
