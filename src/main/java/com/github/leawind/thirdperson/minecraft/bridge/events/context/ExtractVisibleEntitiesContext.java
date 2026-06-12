package com.github.leawind.thirdperson.minecraft.bridge.events.context;

import net.minecraft.world.entity.Entity;

public class ExtractVisibleEntitiesContext {
  public final Entity entity;
  public final float partialTicks;

  public ExtractVisibleEntitiesContext(Entity entity, float partialTicks) {
    this.entity = entity;
    this.partialTicks = partialTicks;
  }

  /// 是否取消该实体的渲染
  public boolean cancelRendering = false;
}
