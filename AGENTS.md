# Third-Person 开发指南

## 最高约束

最高约束由用户（人类开发者）亲自编写，Agent 不得擅动。

- 本项目是一个 Minecraft: Java Edition 模组，实现更好的第三人称视角
- 仓库中的文档代表目标，可超前于代码实现，不可滞后
- 本分支中的文档仅用于当前分支

项目版本和维护现状：

- 3.x.x：持续维护，目前仅发布 beta 版本，可进行破坏性更改；使用 stonecutter + modstitch 构建，全部mc版本和加载器逻辑都位于 `main` 分支。
- 2.x.x：持续维护。每个 Minecraft 版本使用独立的分支，分支用对应的 Minecraft 版本命名。
- 1.x.x：不再维护

## 项目约束

- AGENTS-LOCAL.md 中存放本地开发环境下所用工具链相关的经验，建议阅读
- 文档用中文，代码注释用英文
- 使用 google-java-formatter 进行格式化
- 修改文件时注意单个源码文件通常不多于700行，若过长可考虑拆分、重构相关逻辑，但若理由充分也可保留较长的文件
- 执行任务时应适时提交更改，而非完成全部任务后一次性提交
- 若踩到值得记录的坑，可在任务完成后报告
- 与相机相关的功能尽量使用活动相机实体（camera entity），而非本地玩家；旁观模式下附着实体时，相机实体是被附着实体
- 尽量不要重新实现复杂的 Minecraft 原版逻辑；应在原调用路径上进行最小、可组合的注入

## 提交规范

采用 Conventional Commits，格式为 `type(scope): subject`，scope 可省略。

- type 取值：`feat`、`fix`、`docs`、`refactor`、`chore`、`build`、`ci`、`test`、`style`
- scope 取值：模块名（`core`、`logic`、`bridge`、`extension`、`platform`、`mixin`）或流程/文档名（`docs`、`agents`、`build`）；多模块联动用逗号并列，如 `refactor(core,logic):`
- 破坏性变更：在 `:` 前加 `!`，并在 body 末尾附 `BREAKING CHANGE:` 脚注说明破坏内容
- subject 用英文，小写开头（专有名词、模块名除外），祈使或一般现在时，结尾不加句号；多个要点可用分号或破折线串联
- body 约 72 字符折行，先动机后行为，可用 `-` 列表
- 设计语义与代码强耦合的变更应放在同一个提交中，使每个提交点上的仓库自洽（文档不滞后于已入库代码）

## 内部扩展

- 扩展机制位于 `io.github.leawind.thirdperson.internal.extension`，仅用于项目内部组合，不属于公共 API。
- 扩展点按功能领域放在 `internal.extension` 的子包中；扩展点定义、上下文和调用入口不得包含具体 Minecraft 或兼容实现的注册。
- Minecraft 基线实现位于 `internal.extension.minecraft`，Sable 兼容实现位于与其平行的 `internal.extension.sable`。每组实现只在本组的注册入口中注册。
- 组合根先安装 Minecraft 基线扩展，再安装可用的 Sable 扩展。Sable 实现以更高优先级覆盖适用的扩展点；除组合根外，其他代码不得直接依赖这两个具体实现包。
- 扩展点定义不得依赖 bridge、logic 或 platform 实现；Minecraft 与 Sable 扩展实现不得依赖 bridge 或 logic。

## Mixin 兼容性

- 本模组只维护 `leawind_third_person.mixins.json` 一份 Mixin 元数据；各加载器共用该配置。
- 所有 Mixin 类位于 `io.github.leawind.thirdperson.internal.bridge.mixin` 包树，并直接按 `hud`、`input`、`interaction`、`lifecycle`、`render`、`sound` 用途分类。一个类同时涉及多个用途时，应按职责拆分。
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

## 踩坑记录

实现中踩过并确认的坑，供后续任务避让；条目应写清现象与结论，不罗列排查过程。

- stonecutter 的字符串替换是双向的：共享源码以最新 Minecraft 版本的类名形态存储（如 `ClientInput`、`Identifier`），构建旧版本变体时由 `build.gradle.kts` 的 `replacements` 反向替换回旧名（`Input`、`ResourceLocation`）。现象：`<=1.21` 等旧版本条件分支中的类引用看似未导入或不存在，像编译错误。结论：不要“修复”这些看似错误的分支；判定旧变体能否编译必须实际构建对应变体。
- 直接调用 `:<version>:compileJava` 可能因 `versions/*/build/stonecutter-cache` 陈旧而被判定 up-to-date，跳过实际编译，得到假通过。结论：验证共享源码在旧版本的行为前，先删除该变体 `build/` 下的 `stonecutter-cache`、`generated`、`classes`，强制重新生成与编译。
