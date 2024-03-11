# 测试

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
