package com.github.leawind.thirdperson.minecraft.bridge.events.context;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/// 用于修改实体探测（pick）的参数
///
/// <p>当玩家进行实体选取时，此事件允许修改探测的起点、终点、碰撞箱和距离
public class ModifyEntityHitContext {
  public final Entity entity;

  /// 探测起点，原本是玩家眼睛位置
  public Vec3 pickFrom;

  /// 探测终点，原本是玩家眼睛前方距离为 pickRange 的位置
  public Vec3 pickTo;

  /// 表示探测范围的碰撞箱，只有与它相交的实体才会被考虑
  public AABB aabb;

  /// 探测距离上限的平方
  public double pickRangeSqr;

  /// 目标实体谓词
  ///
  /// 返回 false 的实体将被忽略
  public Predicate<Entity> predicate;

  public ModifyEntityHitContext(
      Entity entity,
      Vec3 pickFrom,
      Vec3 pickTo,
      AABB aabb,
      Predicate<Entity> predicate,
      double pickRangeSqr) {
    this.entity = entity;
    this.pickFrom = pickFrom;
    this.pickTo = pickTo;
    this.aabb = aabb;
    this.predicate = predicate;
    this.pickRangeSqr = pickRangeSqr;
  }
}
