# 更新日志

## v2.0.8-beta.3-mc1.20.1

### 特性

* 使用 `ModifyExpressionValue` 替代 `Redirect`。这将有更好的兼容性。

### 问题修复

* 兼容 _Better Combat_ 和 _First Person Model_ #50
* YACL 配置屏幕中的本地化键错误
* 在高草丛中强制第一人称 #54
* 在第三人称中，水被放置在玩家所看的位置，而不是准星的位置。

### 其他

* 更新翻译
* 构建脚本：在 fabric 发布脚本中添加 YACL 依赖。
* 修复：第一人称使用鞘翅时意外旋转的问题。
* 添加注释。
* 更新 README
* 优化代码

## v2.0.8-beta.2-mc1.20.1

### 特性

* 添加配置：`t2f_transition_halflife`

### Bug修复

* 修复：第一人称中有双倍鼠标灵敏度 #49
* 修复：MixinExtras 没有初始化
* Forge 版本中选取方块错误

### 其他

* 优化代码

## v2.0.8-beta.1-mc1.20.1

移植自 2.0.7-mc1.19.4

### 特性

* 使用 MixinExtras。
  * 用`@WrapWithCondition`替换`@Redirect`，这应该能解决与_Do a Barrel Roll_的冲突。
* 由于YACL存在的一些问题，删除了Forge版本对YACL的支持。

### Bug修复

* 按键 `force_aiming`，`toggle_aiming` 失效

### 其他

* 将 `changelog_latest.txt` 更新为 `changelog_latest.md`
* 在YACL中更新了弃用的方法：`valueFormatter` -> `formatValue`
* 更新构建脚本
* 添加调试日志

## v2.0.7-mc1.19.4

移植自 2.0.7-mc1.19.3

## v2.0.7-mc1.19.3

移植自 2.0.7-mc1.19.2

## v2.0.7-mc1.19.2

### 特性

* 像第一人称一样射击敌人
  * 模组可以预测你想要射击的目标实体，即使目标距离很远，你也只需要像第一人称一样将准星放在目标上方即可。
  * 添加配置项：`enable_target_entity_predict`，启用目标实体预测。
* 从第三人称切换到第一人称时的平滑过渡。
* 兼容模组滚筒飞行do_a_barrel_roll（仅fabric版本兼容）
* 现在 Cloth Config API 是可选依赖
* 当相机实体在墙里时暂时切换到第一人称
* 配置中的平滑系数改成了平滑半衰期，这样更符合直觉
* 更新平滑眼睛坐标时，乘上其与相机的距离。这样在不同相机距离下的平滑效果更加一致。

## v2.0.6-mc1.19.2

### 特性

* 当相机靠近玩家时，玩家变得半透明
* 关闭客户端时自动保存配置文件
* 默认启用第三人称视角
* 将是否启用第三人称视角保存到配置文件中
  * 添加配置项：`is_third_person_mode`
* 使用特定物品时，暂时进入第一人称视角（原版的望远镜）
  * 添加配置项：`use_to_first_person_pattern_expressions`
* 修改资源包结构
  * `assets/<命名空间>/item_patterns/hold_to_aim/<任意名称>.json` 手持时进入瞄准状态
  * `assets/<命名空间>/item_patterns/use_to_aim/<任意名称>.json` 使用时进入瞄准状态
  * `assets/<命名空间>/item_patterns/use_to_first_person/<任意名称>.json` 使用时暂时切换成第一人称视角

### 修复问题

* 相机穿过玻璃
* 游戏崩溃 #44

## v2.0.5-mc1.19.2

### 特性

* 移除配置项： `enable_buildin_item_aiming_rules`
* 从资源包中加载物品模式
* 重命名配置项
  *  `holding_item_aiming_rules` -> `hold_to_aim_item_pattern_expressions`
  *  `using_item_aiming_rules` -> `use_to_aim_item_pattern_expressions`
* 创造模式下，即便玩家视线被遮挡，也可以直接从相机选取方块，
  * 添加配置项： `use_camera_pick_in_creative`
* 方法 `Entity#pick` 返回 MISS 当选取目标太远时

### 修复问题

* Unable quickly turn when sprinting
* Unable to join world in forge version #38

## v2.0.4-mc1.19.2

### 特性

* 可配置的瞄准规则
  * 添加布尔类型配置：启用内置匹配规则
  * 添加字符串列表类型配置：手持物品匹配规则
  * 添加字符串列表类型配置：使用物品匹配规则
  * add string list config: Using Item Matching Rules

### 修复Bug

* 玩家的不平滑旋转
* 配置屏幕中，flying_smooth_factor选项位于错误的标签页

### 添加Bug

* Forge 版本中无法进入世界

### Other

优化代码和稍稍重构

## v2.0.3-mc1.19.2

### 特性

* 为 Forge 版本添加 Cloth-Config-API 支持
* 拉弓时自动侧身
* 添加配置：拉弓时自动旋转身体
* 添加按键：切换俯仰角锁定

### 修复Bug

* 某些情况下无法pick实体
* 在专用服务器上配置文件仍会被保存
* 配置屏幕里缺少了一项：瞄准时渲染准星

## v2.0.2-mc1.19.2

移植到 1.19.2

### 特性

* 添加配置: 正常/瞄准模式下居中
* Fabric 版本使用 Cloth Config API 构建配置屏幕

### 加入Bug

* forge版本暂时没有配置屏幕，但你仍可以手动修改配置文件。当尝试打开配置屏幕时，会自动重新加载配置文件。

## v2.0.1(1.19.4)

### 特性

* 添加配置: 飞行时自动居中
* 手持以下物品时自动进入瞄准模式
  * 末影珍珠
  * 雪球
  * 鸡蛋
  * 喷溅药水
  * 滞留药水
  * 附魔之瓶

### 修复bug

* 调整相机时平滑系数错误
* 只能向前游泳
* 相机靠近玩家时画面闪烁

## v2.0.1-beta.4-mc1.19.4

### 特性

* 添加配置：交互时自动转身 #4
* 添加配置：交互时转身方式 #26

### 修复bug

* 游戏暂停时相机仍在移动

## v2.0.1-beta.3-mc1.19.4

### 特性

* 添加按键：切换模组是否启用
* 添加配置: 进入第一人称时玩家是否转向相机朝向
* 添加配置: 调节相机时的平滑系数
* 现在可以在 [0,1] 范围内调整平滑系数了

### 修复bug

* 相机颤动（再次修复）

### 藏匿bug

* 游泳时仅朝前方

## v2.0.1-beta.2-mc1.19.4

### 特性

* 添加配置: 锁定相机俯仰角
* 当相机离玩家足够近时，让玩家变得不可见

### 修复bug

* 玩家移动时相机颤动

## v2.0.0-mc1.19.4

### 特性
* 使用 [Architectury](https://github.com/architectury/architectury-api)
* 使用 [YACL](https://github.com/isXander/YetAnotherConfigLib) 生成配置屏幕
* 为 fabric 版本添加 [Modmenu](https://github.com/TerraformersMC/ModMenu) 支持
* 添加打开配置屏幕的按键
* 瞄准时忽略类似高草丛的方块
* 使用鞘翅飞行时使用单独的平滑系数
* 添加配置：移动时不自动转向 #22
* 添加配置：瞄准时是否显示准星
* 添加配置：未瞄准时是否显示准星

### 修复bug

* 只能向前方奔跑 #20
* 相机穿墙