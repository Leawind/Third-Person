# Leawind's Third Person 设计

本文描述当前 `main` 分支的实现。它不是功能规划；代码与本文冲突时，以代码为准，并应同时更新本文。

注意除非用户明确要求，否则不要删除docs/中的文档，也不要在其他位置创建新文档。

## 概览

Leawind's Third Person 是纯客户端第三人称模组。它向 Perspective API 注册一个
`leawind_third_person.third_person` 行为，基础类型为 `THIRD_PERSON_BACK`。普通构图和瞄准构图
是该行为的内部模式，不是额外的 Perspective 行为。

相机的位置、旋转和 FOV 由本模组计算为理想姿态后写入 Perspective API 的可变状态；相机穿墙检测
通过 bridge 调用原版方块裁剪完成，视角切换由 Perspective API 负责。本模组不会自行修改实体渲染、
模型、透明度或发光轮廓。

只有同时满足以下条件时，视角专属逻辑才会生效：Perspective API 已启用、当前行为是本模组行为，
且需要本地玩家的逻辑还要求相机实体就是本地玩家。`PerspectiveGuard` 是这一判断的唯一入口。

## 分层

```text
platform → logic.ModEntrypoint / ModEvents
                         |
                         v
              logic.scheduler → logic.base
                         |           |
                         +-----+-----+
                               v
                             bridge

logic.base / logic.scheduler / bridge → core（纯逻辑层，无 Minecraft 依赖）
logic.base / logic.scheduler / bridge → extension（内部扩展点，实现仅组合根安装）
```

- `core`：纯逻辑层，不依赖 Minecraft；`core.base` 是与模式无关的相机、射线、旋转纯实现，`core.schedule` 是动态状态与设置，`core.api` 是扩展点基础设施。
- `extension`：内部扩展点及其上下文，按功能领域分子包；Minecraft 与 Sable 实现分别位于 `extension.minecraft`、`extension.sable`，仅由组合根安装。
- `logic.base`：基础层唯一根包树，包含与普通/瞄准模式无关的相机、射线、移动和玩家旋转逻辑。
- `logic.scheduler`：调度层唯一根包树，包含动态状态、配置、持久化、UI、HUD 与参数调度逻辑。
- `logic` 根包：初始化两个逻辑层与内部扩展，每种 bridge 事件只注册一个监听器，并显式编排执行顺序。
- `bridge`：Mixin、中立事件、Minecraft 原语和版本差异，不包含业务策略，也不引用 logic。
- `platform`：Fabric、Forge、NeoForge 的入口、按键与可选配置界面注册。

logic 的基础逻辑和调度逻辑各自只能存在于自己的唯一根包树中，不能重新分散到已废弃的
`internal.base`、`internal.scheduler`、`internal.application`、`internal.integration` 等旧业务
包树。层根包只保存门面、运行时、会话和跨功能协调；其余代码按高内聚职责放入受控子包。core
与 logic 两层共用同一子包类别白名单：基础层为 `camera`、`math`、`pivot`、`rotation`，调度层为
`aiming`、`camera`、`config`、`hud`、`input`、`rotation`、`sound`、`state`。同一职责不能再横跨
多个子包。

调度层只能使用基础层公开的边界类型；基础层整个包树绝对不能引用调度层。Stonecutter 宏只能
出现在 bridge 和 platform，不能进入 api、core、logic 或扩展点定义。共享源码以最高支持版本的
类名形态存储，构建旧版本变体时由 `build.gradle.kts` 的 stonecutter 替换规则双向重写（见
docs/ARCHITECTURE.md 的多版本源码机制一节）。

`api` 包目前没有稳定的公共扩展点；`internal` 下的类型不构成兼容承诺。

## 生命周期与状态

`BaseRuntime` 是基础层单例，拥有 `BaseSession`、观察旋转、相机平滑器、玩家旋转控制器和最终相机
姿态。它只保存当前 `BaseParameters`，不保存普通/瞄准模式、配置或持久化状态。

`SchedulerRuntime` 是调度层单例，拥有普通/瞄准状态、相机调整状态以及
`CameraSettings`、`AimingSettings`、`PlayerSettings`、`HudSettings` 等可持久化设置。调度层保存
自己最后提交的参数快照，不从基础层读回参数再修改。

Perspective 行为激活时基础会话开始控制相机；停用、世界变更或本地玩家身份变更时基础会话重置。
相机实体发生变化时只重置观察旋转、相机平滑器和最终姿态，以免把上一实体的平滑状态带入下一实体。

`ModEvents` 对每种 bridge 事件只注册一个监听器。客户端 tick 固定按以下顺序执行：加载或观察持久化
状态、刷新物品谓词、处理按键与瞄准状态、调度并提交 `BaseParameters`、执行基础玩家旋转。渲染前先
刷新调度层逐帧目标，再由基础层应用玩家旋转。

## 相机

每帧 `ThirdPersonPerspective.applyCameraState` 采样相机实体的眼部位置、实体朝向、原版 FOV、
窗口宽高比和实体尺寸。调度层在客户端 tick 根据游泳、鞘翅等状态选择当前参数。
`LookController` 保存独立于玩家身体的观察 yaw/pitch；鼠标转动先更新它，再由它生成
Perspective API 约定的四元数。

普通与瞄准各有一个 `CameraProfile`：

- `distanceFactor`、水平/垂直屏幕锚点和居中时的垂直锚点；
- `fovMultiplier`；
- `centered` 标记。

实际距离按下式计算，其中 `vehicleTotalSize` 是根坐骑及其全部乘客包围盒的对角线长度，
`bodyRadius` 来自当前相机实体的宽度：

```text
distance = (distanceFactor × vehicleTotalSize + bodyRadius) / fovMultiplier
```

`CameraSmoother` 分别对 pivot、旋转、屏幕锚点、距离和 FOV 做指数半衰期平滑。基础层随后根据平滑后
的相机旋转对本帧屏幕锚点做极端俯仰修正。静态常量分别指定开始压缩的俯仰角、完全居中的俯仰角和
归一化 `TRANSITION_FUNCTION`；更换这些常量无需修改相机管线。过渡函数返回值会被限制在
`[0, 1]`，到达配置的结束角度后水平和垂直锚点直接归零。该修正不写回 profile 或
`CameraSmoother` 的 current，因此恢复普通俯仰角时仍以原来的平滑偏移为基础。最后 `CameraRig` 将
修正后的屏幕锚点、FOV 和宽高比转换成世界空间相机位置；碰撞处理也使用这一位置。到达配置的结束
角度时，相机中心射线由此经过平滑后的实体眼睛 pivot。游泳或鞘翅飞行时，pivot 使用专用半衰期；
按住调整键时，偏移与距离使用专用半衰期。

默认普通构图为距离系数 `1.1`、左肩偏移 `-0.2`、垂直偏移 `-0.25`；默认瞄准构图为距离系数
`0.448`、偏移 `-0.3/-0.41`、FOV 倍率 `0.9`。

## 输入、构图与瞄准

- 按 `Caps Lock` 切换左右肩；按住至少 4 tick 后改为居中。再次短按会退出居中，或在左右肩之间切换。
- 按住 `Z` 调整当前模式的构图：鼠标修改偏移，滚轮以 `1.25` 倍步进修改距离系数。调整期间会截获
  对应的鼠标输入；松开后立即请求保存。
- 瞄准模式由瞄准按键或智能瞄准触发。智能瞄准启用时，只要自动规则命中，即进入瞄准模式。
- 移动向量按观察 yaw 计算；冲刺的“向前”条件改为任意达到原版阈值的方向输入。两者只对本地、
  非乘坐状态且当前为本模组视角的玩家生效。

资源包可在每个命名空间的以下目录提供 JSON 字符串数组：

```text
assets/<namespace>/item_patterns/hold_to_aim/*.json
assets/<namespace>/item_patterns/use_to_aim/*.json
```

这些字符串使用 Minecraft 的物品谓词语法。资源包规则与配置中的附加规则会去重、编译，并在每个
客户端 tick 评估：`hold_to_aim` 匹配主手或副手；`use_to_aim` 仅在正在使用物品时匹配使用中的物品。
无效规则会记录错误并跳过，不会使整个规则集失效。

## 玩家旋转、交互与 HUD

普通状态的玩家旋转由 `NormalPlayerRotationMode` 选择，默认 `INTEREST_POINT`。瞄准时立即面向预测目标；
`CAMERA_CROSSHAIR`、`PARALLEL_WITH_CAMERA` 和 `NONE` 分别以相机命中点、相机旋转和当前旋转为目标。
在其他普通模式下，飞行时立即面向相机；若开启交互自动旋转（进食时默认不旋转），交互、游泳、疾跑、
乘坐和移动由 `PlayerRotationStrategy` 选择相机命中点、移动方向或兴趣点，并使用即时、tick 插值或逐帧
指数平滑。

射线起点可以配置为相机或玩家眼睛。无论选择哪一种，射线方向始终是最终相机的前方，目标选择不会
修改玩家实体朝向，也不会请求第二次原版 pick：

- `PLAYER_EYE` 从玩家眼睛开始探测，候选射线使用原版方块/实体交互距离中的较大值。
- `CAMERA` 从最终相机位置开始探测。候选射线额外延长“相机到玩家眼睛”的距离，以覆盖位于玩家正常
  交互范围内、但相机位于其身后的目标。
- 无论候选射线起点为何，最终方块或实体命中都会按玩家眼睛到命中点的距离，使用对应的原版方块或实体
  交互距离再次校验；超出范围的结果会变为 miss。因此相机起点不会扩大实际可交互范围。

每次原版更新命中结果后，基础层会以该射线结果覆盖 `Minecraft.hitResult` 与当前实体命中；交互执行前也
会刷新一次。这样原版交互、准星目标和方块的黑色线条边框都使用同一个目标。不同 Minecraft 版本中相机
更新与原版 pick 的先后不同，bridge 会在最终相机姿态可用的正确时机完成该刷新。

模组不改写 `Entity.pick`，也不因目标选择修改玩家实体旋转。

准星策略 `CrosshairMode` 有四种值：`ALWAYS` 恒显示、`AIMING` 仅瞄准时显示、`NOT_AIMING` 仅
非瞄准时显示、`OFF` 恒隐藏。当前为本模组视角且相机控制启用时按上述策略决定；开启对应设置时，
鞘翅飞行且未瞄准会额外隐藏准星；其他视角保留原版判断。

## 配置与持久化

状态文件为 `config/leawind_third_person.json`，当前 schema 版本为 2。首次客户端 tick 加载或创建
该文件；后续设置变更在 20 tick 防抖后保存，失败会重试。

YACL 存在时可提供配置界面；没有 YACL 时，普通运行路径不会加载其类型。

## 当前未实现的内容

当前代码没有目标锁定；没有临时第一人称覆盖；没有针对其他相机模组的专用兼容层；也没有对玩家实体、盔甲、模型部件或世界实体渲染的改动。
