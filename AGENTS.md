# Third-Person 开发指南

## 项目约束

- 与相机相关的功能尽量使用活动相机实体（camera entity），而非本地玩家；旁观模式下附着实体时，相机实体是被附着实体。
- 尽量不要重新实现复杂的 Minecraft 原版逻辑；应在原调用路径上进行最小、可组合的注入。
- 不要使用 google-java-formatter 执行格式化。

## Mixin 兼容性

- 不要使用 `@Redirect`。它会排他地占用调用点，容易与其他模组冲突；优先使用可组合的 Mixin 或 MixinExtras 注入器。
- 需要支持 Forge 1.20.1 的共享 Mixin 不要使用 `@ModifyArgs`。Mixin 0.8.5 会在 `org.spongepowered.asm.synthetic.args` 动态生成 `Args$N`，而 Forge 1.20.1 的 ModLauncher 10 无法加载该包，造成目标类链接时的 `NoClassDefFoundError`。
- 出现 `org.spongepowered.asm.synthetic.args.Args$N` 缺失时，优先排查新引入的 `@ModifyArgs`；不要把它当作普通依赖或打包问题，也不要尝试将动态生成类放入模组 JAR。
- 需要改写多个参数时，选择不依赖动态 `Args` 的可组合注入方式，如多个 `@ModifyArg` 或合适的 MixinExtras 注入器。
- 包裹构造器时，使用 `@WrapOperation` 与 `@At(value = "NEW", target = "L目标类;")`，并在所有分支调用 `original`；不要把构造器作为普通 `INVOKE` 目标。

## 多版本与验证

- 不要编辑 `versions/*/build/generated/stonecutter/` 下的生成源码；修改共享源后生成并检查各版本产物。
- 构建成功不代表 Mixin 能在生产环境加载。涉及 Forge Mixin、类加载或 ModLauncher 行为时，必须使用对应版本的打包产物，在真实 Forge 客户端中至少验证进入世界；功能默认关闭也不能避免目标类链接时崩溃。
- 修改共享逻辑时，运行覆盖全部版本变体的构建与测试；对客户端行为另行进行针对性的游戏内验证。

## 仓库卫生

- 保留无关的工作区改动；只暂存和提交本任务涉及的文件。
- 提交前运行 `git diff --check`，并确认代码、测试、文档和提交信息一致。
- 未经明确要求不要推送远端。
