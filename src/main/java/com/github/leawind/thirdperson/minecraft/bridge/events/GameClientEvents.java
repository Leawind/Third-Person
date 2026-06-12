package com.github.leawind.thirdperson.minecraft.bridge.events;

import com.github.leawind.thirdperson.minecraft.bridge.events.context.CameraSetupContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.EventContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.ExtractVisibleEntitiesContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.ModifyArmorRenderTypeContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.ModifyEntityHitContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.ModifyModelPartOpacityContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.MouseTurnPlayerStartContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.MoveImpulseContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.PickBlockContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.TurnPlayerContext;
import io.github.leawind.inventory.event.EventEmitter;
import io.github.leawind.inventory.event.SimpleEventEmitter;

public final class GameClientEvents {
  private GameClientEvents() {}

  public static final SimpleEventEmitter<Float> RENDER_TICK_START = new SimpleEventEmitter<>();

  /// 修改视野大小 (Field of View)
  public static final EventEmitter<EventContext<Float>> MODIFY_FOV = new EventEmitter<>();

  public static final EventEmitter<CameraSetupContext> SETUP_CAMERA = new EventEmitter<>();

  /// 启用第三人称视角摇晃
  ///
  /// Vanilla: enabled
  public static final SimpleEventEmitter<EventContext<Boolean>> ENABLE_BOB_VIEW =
      new SimpleEventEmitter<>();

  public static final SimpleEventEmitter<PickBlockContext> PICK_BLOCK = new SimpleEventEmitter<>();

  /// 修改实体选取（pick）的参数
  ///
  /// 当玩家进行实体选取时触发，可以修改探测的起点、终点、碰撞箱和距离
  public static final SimpleEventEmitter<ModifyEntityHitContext> PICK_ENTITY =
      new SimpleEventEmitter<>();

  public static final SimpleEventEmitter<TurnPlayerContext> TURN_PLAYER =
      new SimpleEventEmitter<>();

  public static final SimpleEventEmitter<EventContext<Boolean>> ENABLE_THIRD_PERSON_CROSSHAIR =
      new SimpleEventEmitter<>();

  /// 修改移动冲量，移动冲量由向前和向左的冲量组成
  ///
  /// 当玩家按下移动键（WASD）时，会触发此事件，并传递一个包含冲量值的上下文对象
  public static final SimpleEventEmitter<MoveImpulseContext> MODIFY_MOVE_IMPULSE =
      new SimpleEventEmitter<>();

  /// 提取可见实体
  ///
  /// 可以在这里过滤掉某些不该渲染的实体
  public static final SimpleEventEmitter<ExtractVisibleEntitiesContext> EXTRACT_VISIBLE_ENTITIES =
      new SimpleEventEmitter<>();

  /// 是否禁用双击冲刺
  public static final SimpleEventEmitter<EventContext<Boolean>> DISABLE_DOUBLE_TAP_SPRINT =
      new SimpleEventEmitter<>();

  /// 在此事件中可以处理自定义按键绑定
  public static final SimpleEventEmitter<Void> HANDLE_KEYBINDS_START = new SimpleEventEmitter<>();

  /// 鼠标移动转动玩家前触发
  ///
  /// 如果在事件处理函数中设置 cancelDefault = true，则后续的玩家旋转处理将会被取消，
  /// 鼠标累积位移量也会被重置
  public static final SimpleEventEmitter<MouseTurnPlayerStartContext> MOUSE_TURN_PLAYER_START =
      new SimpleEventEmitter<>();

  /// 修改模型部件渲染时的透明度
  ///
  /// 当渲染模型部件的 Cube 时触发，可以修改 ARGB 颜色值
  public static final SimpleEventEmitter<ModifyModelPartOpacityContext> MODIFY_MODEL_PART_OPACITY =
      new SimpleEventEmitter<>();

  /// 修改盔甲的渲染类型
  ///
  /// 当渲染盔甲时触发，可以替换默认的 RenderType（例如替换为半透明版本）
  public static final SimpleEventEmitter<ModifyArmorRenderTypeContext> MODIFY_ARMOR_RENDER_TYPE =
      new SimpleEventEmitter<>();
}
