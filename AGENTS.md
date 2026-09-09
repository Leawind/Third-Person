# Third-Person 2.x Agent 指南

## 最高约束

最高约束由用户（人类开发者）亲自编写，Agent 不得擅动。

本项目是一个 Minecraft: Java Edition 的模组，实现更好的第三人称视角。

### 项目版本和维护现状

- v3 (3.x.x)：持续维护，目前仅发布 beta 版本，可进行破坏性更改；使用 stonecutter + modstitch 构建，全部mc版本和加载器逻辑都位于 `main` 分支。
- v2 (2.x.x)：持续维护。每个 Minecraft 版本使用独立的分支，分支用对应的 Minecraft 版本命名，例如 `1.20.1`, `26.2` 等。
- v1：不再维护

v3 已经引入 Perspective API 依赖，v2 未曾引入，将来也不该引入或关心该依赖。

本文件所在分支应当是 `v2`，`v2` 分支专门用于管理 v2 的 Agent 指南、内部设计文档，不包含模组代码，在本地开发环境中本分支对应的 worktree 可以作为 v2 版本唯一的 Vibe Coding 工作区。

### 文档管理

- Agent 指南
  - AGENTS.md（本文件）包含 Agent 应当遵循的规则和应知晓的经验
  - AGENTS-*.md 不纳入版本管理
    - AGENTS-LOCAL.md：因与本地开发环境相关而不适合放在 AGENTS.md 的内容，应当阅读
    - AGENTS-NOTES.md：有时效性的调查快照，通常应忽略，仅当用户明确要求时才访问
- 设计文档
  - 设计文档位于 docs/design.md 单个文件中，它应包含 v2 版本的功能、架构设计细节
  - 文档用简体中文编写，标点符号使用中国大陆规范
  - 文档代表目标，可超前于代码实现，不可滞后
  - 如果执行任务时发现现有文档存在错误、矛盾、过时、缺失等问题而应当修改，但用户没预料到，应向用户请示，不能编写超前于文档的实现或擅自修改文档

## 其他约束

### 任务执行

- 执行任务时应适时提交更改，而非完成全部任务后一次性提交
- 若踩到值得记录的坑，可在任务完成后报告

### 提交规范

- 提交信息遵循 Conventional Commits，用英语编写。
- 未经用户明确允许不要推送远端
- 未经用户明确允许不得修改已推送到远端的历史提交
- 意义不大的本地提交可以 squash

### 项目约束

- 尽量不要重新实现复杂的 Minecraft 原版逻辑；应在原调用路径上进行最小、可组合的注入。
- 2.x 是多加载器项目：公共代码在 `common/`，加载器专属代码在 `fabric/`, `forge/`, `neoforge/`（旧分支以实际模块为准），包根为 `com.github.leawind.thirdperson`。
- 2.x 主开发线是最新的版本分支；新功能与修复先落在主开发线，当用户明确要求时再按需下放到旧分支。

### Mixin 兼容性

- 不要使用 `@Redirect`。它会排他地占用调用点，容易与其他模组冲突；优先使用可组合的 Mixin 或 MixinExtras 注入器。
- 需要支持 Forge 1.20.1 的共享 Mixin 不要使用 `@ModifyArgs`。Mixin 0.8.5 会在 `org.spongepowered.asm.synthetic.args` 动态生成 `Args$N`，而 Forge 1.20.1 的 ModLauncher 10 无法加载该包，造成目标类链接时的 `NoClassDefFoundError`。
- 出现 `org.spongepowered.asm.synthetic.args.Args$N` 缺失时，优先排查新引入的 `@ModifyArgs`；不要把它当作普通依赖或打包问题，也不要尝试将动态生成类放入模组 JAR。
- 需要改写多个参数时，选择不依赖动态 `Args` 的可组合注入方式，如多个 `@ModifyArg` 或合适的 MixinExtras 注入器。
- 包裹构造器时，使用 `@WrapOperation` 与 `@At(value = "NEW", target = "L目标类;")`，并在所有分支调用 `original`；不要把构造器作为普通 `INVOKE` 目标。

## 踩坑记录

实现中踩过并确认的坑，供后续任务避让；条目应写清现象与结论，不罗列排查过程。

（暂无）
