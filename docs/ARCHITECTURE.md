# 架构

包树按技术分层组织，业务逻辑不在平级的层包树之间重复。Perspective API 负责视角切换；
本模组向其注册行为并写入相机姿态。

```text
platform
   |
   v
logic.ModEntrypoint / ModEvents
   |
   +------> logic.scheduler ------> logic.base
   |                |                  |
   |                +--------+---------+
   |                         v
   +----------------------> bridge

logic.base / logic.scheduler ----> core（纯逻辑层，无 Minecraft 依赖）
logic.base / logic.scheduler / bridge ----> extension（内部扩展点）
                                                  ^
                       extension.minecraft / extension.sable（实现，仅组合根安装）

extension 依赖 core.api 的注册表；bridge 不直接依赖 core。
```

## core

`internal.core` 是纯逻辑层，不依赖 Minecraft、bridge、extension、logic 或 platform。

```text
internal.core
├── api       扩展点基础设施：ExtensionPoint、优先级解析/提供者注册表、有序修改器注册表
├── base      与普通/瞄准模式无关的相机放置、平滑、碰撞策略、射线几何、输入投影、玩家旋转
│   ├── camera
│   ├── math
│   ├── pivot
│   └── rotation
└── schedule  动态状态检测、普通/瞄准决策、设置快照、调度会话
    ├── aiming
    ├── camera
    ├── hud
    ├── rotation
    ├── sound
    └── state
```

`core.base` 的完整包树拥有与模式无关的相机位置计算、平滑、碰撞策略、射线策略、输入
投影和玩家旋转，不含普通/瞄准状态、配置界面或持久化。`core.schedule` 的完整包树拥有
动态状态、普通/瞄准策略、设置与调度会话，同样不含 Minecraft 绑定。

## logic

`internal.logic` 是 core 契约的 Minecraft 绑定，分为两棵子包树：

```text
internal.logic
├── base       BaseRuntime、相机与射线 Minecraft 集成、玩家旋转目标、PerspectiveGuard
│   ├── camera
│   └── pivot
└── scheduler  调度层 Minecraft 集成：按键、物品谓词、相机调整、HUD、持久化、配置界面
    ├── aiming
    ├── config
    ├── input
    └── state
```

每棵子树各拥有一个根包和一组受控子包；层根包只保存门面、运行时、会话和跨功能协调，
其余代码按高内聚职责放入子包。允许的子包类别与 `InternalArchitectureTest` 的白名单一致
（基础层：`camera`、`math`、`pivot`、`rotation`；调度层：`aiming`、`camera`、`config`、
`hud`、`input`、`rotation`、`sound`、`state`；实际只存在其中用到的类别）。

`ModEvents` 为每种 bridge 事件只注册一个监听器，并显式编排持久化、物品检测、按键处理、
调度与基础层执行的顺序。

## extension

`internal.extension` 是内部扩展机制，仅用于项目内部组合，不属于公共 API。扩展点按功能
领域放在子包中（`camera`、`entity`、`input`、`spatial` 等），扩展点定义、上下文和调用
入口不得包含具体实现的注册。扩展点定义依赖 `core.api` 的注册表并直接使用 Minecraft
类型，但不得依赖 bridge、logic 或 platform。

Minecraft 基线实现位于 `internal.extension.minecraft`，Sable 兼容实现位于平行的
`internal.extension.sable`；每组实现只在本组的注册入口中注册，且不得依赖 bridge 或
logic。组合根（`ModEntrypoint`）先安装 Minecraft 基线扩展，再安装可用的 Sable 扩展，
Sable 以更高优先级覆盖适用的扩展点；除组合根外，其他代码不得依赖这两个具体实现包。
Sable API 类型只允许出现在 sable 包内。

## bridge 与 platform

`internal.bridge` 拥有 Mixin、中立事件、Minecraft 原语和全部 Stonecutter 版本差异，不
包含业务策略，也不引用 logic 或公共 API。`platform` 只包含加载器入口和加载器专属的
按键、配置界面与资源重载注册。

公共 `api` 包保留但暂无稳定扩展点；internal 下的类型不构成兼容承诺。仅当出现需要稳定
公共 API 的实现时才引入 `internal.impl` 包。

## 多版本源码机制

共享源码只维护一份，位于 `src/main/java`，以最高支持版本（当前 26.2）的类名形态存储。
构建旧版本变体时，`build.gradle.kts` 的 stonecutter `replacements` 会把新类名双向替换回
旧名（如 `ClientInput` ↔ `Input`、`ResourceLocation` ↔ `Identifier`），再配合 `/*? */`
条件注释切换版本差异。因此旧版本条件分支中的类引用可能看似未导入或不存在——这不是
错误，详见 AGENTS.md 踩坑记录。Stonecutter 宏只允许出现在 bridge 与 platform。

## 基础层边界

`ThirdPersonBase` 是基础层对调度层暴露的单一边界：接受不可变 `BaseParameters` 快照，
并暴露调度所需的少量查询。调度层保存自己最后提交的快照，不从基础层读回参数再修改；
基础层也不暴露内部参数存储。

基础层射线规则独立于玩家旋转：以最终相机方向的射线替换客户端当前命中结果。
`PLAYER_EYE` 从玩家眼睛起探测，`CAMERA` 从最终相机位置起探测；相机起点的候选射线延长
相机到眼睛的距离，随后每个选中的方块或实体都按从玩家眼睛测量的原版交互距离校验。
同一结果供给原版交互与方块描边目标；射线选择不转动玩家，也不请求第二次原版 pick。

基础层相机管线先平滑配置的屏幕空间锚点，再施加瞬态垂直俯仰修正。`CameraOffsetSqueeze`
从平滑后的相机旋转导出绝对俯仰；静态常量定义起始俯仰、完全居中俯仰和归一化的
`TRANSITION_FUNCTION`，更换这些常量无需修改相机管线。过渡结果钳制到 `[0, 1]`；
`CameraRig` 与碰撞因此收到同一有效组合，而配置 profile 与 `CameraSmoother` 的当前值
保持不变。到达配置的结束俯仰时，相机中心射线经过平滑后的眼睛 pivot。

## 强制规则

`InternalArchitectureTest`（ArchUnit）与根构建的 `checkArchitecture` 任务共同强制以下
规则，违反即构建失败：

- core 不依赖 Minecraft、bridge、extension、logic 或 platform；
- logic.base 不引用 logic.scheduler；调度层只使用白名单内的 core.base 边界类型；
  pivot 策略除 `MinecraftCameraCollision` 外不依赖相机实现；
- bridge 不引用公共 API 或 logic，也不引用 platform 实现（`platform.api` 除外）；
- 公共 API 不引用 internal；
- 扩展点定义不依赖 bridge、logic 或 platform；具体扩展实现不依赖 bridge 或 logic；
  组合根之外不得依赖具体实现包；Sable API 只在 sable 包内使用；
- `internal.utils`（如引入）保持业务中立，不依赖项目内其他代码；
- Stonecutter 宏不出现在 API、core、logic 或扩展点定义中；
- 层根包下只允许白名单子包类别，不允许任意深层嵌套；
- 禁止重新引入旧的业务包树：`internal.base`、`internal.scheduler`、
  `internal.application`、`internal.integration`、`internal.persistence`、
  `internal.configscreen`、`internal.bootstrap`（`internal.core` 是合法的纯逻辑层，不在
  禁止之列）；
- 只维护一份 `leawind_third_person.mixins.json`；禁止 `@Redirect`；共享 Mixin 禁止
  `@ModifyArgs`（Forge 1.20.1 兼容，详见 AGENTS.md 的 Mixin 兼容性一节）；
- 相机意图与旋转瞄准不得读取 `RaycastOrigin`（`MinecraftCameraRaycasting`、
  `MinecraftPlayerRotationTargeting`）。
