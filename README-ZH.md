<div align=center>

| [English](./README.md) | 中文 |
|------------------------|----|

[![CurseForge下载量](https://img.shields.io/curseforge/dt/930880?style=flat&logo=curseforge&color=F1643%5E&cacheSeconds=3600&label=下载量)](https://www.curseforge.com/minecraft/mc-mods/leawind-third-person)
[![Modrinth下载量](https://img.shields.io/modrinth/dt/S3D3QF0M?style=flat&logo=modrinth&color=17B85A&cacheSeconds=3600&label=下载量)](https://modrinth.com/mod/leawind-third-person)

[![Codacy Badge](https://img.shields.io/codacy/grade/41e70a17218c4773aefb62382b9547a6?logo=codacy&label=代码质量)](https://app.codacy.com/gh/Leawind/Third-Person/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Stars](https://img.shields.io/github/stars/LEAWIND/Third-Person?style=flat&logo=github&color=daaa3f&label=星标)](https://github.com/LEAWIND/Third-Person)

[![上次提交](https://img.shields.io/github/last-commit/LEAWIND/Third-Person?logo=github&label=上次提交)](https://github.com/LEAWIND/Third-Person)
[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg?label=开源协议)](https://github.com/LEAWIND/Third-Person?tab=MIT-1-ov-file)
[![文档](https://img.shields.io/github/deployments/LEAWIND/Third-Person/github-pages?style=flat&logo=github&label=文档&cacheSeconds=900)](https://leawind.github.io/Third-Person/en-US/?autolang)

# Leawind 的第三人称

一个实用、丝滑、功能丰富的第三人称模组。

</div>

- **纯客户端。** 服务端不需要安装本模组。
- **自由转动相机。** 单纯观察不会立即带动玩家身体转向，移动方向仍然相对于相机。
- **可调整的越肩相机。** 按住 `Z` 后移动鼠标可调整屏幕空间偏移，滚轮每格以 1.25 倍调整距离；
  短按 `Caps Lock` 可切换左右肩位，长按则居中。两个按键均可重新绑定。
- **普通与瞄准构图。** 弓、弩、长矛、手动绑定的瞄准键以及资源包规则都可以启用距离更近的瞄准相机。
- **第三人称交互对齐。** 攻击、使用物品和选取方块前，会将玩家的权威视线与最终相机意图对齐，再调用原版选取，
  不会扩大交互距离。
- **平滑切换视角。** Perspective API 统一负责视角选择、过渡以及与其他 API 视角模组的协作。
- **精简配置。** 始终可使用经过校验的 JSON 配置；安装 YACL 后可使用游戏内配置界面，Fabric 用户可从 Mod Menu 打开。

## 依赖与支持版本

必须安装 [Perspective API](https://modrinth.com/mod/perspective-api)；Fabric 版本还需要
[Fabric API](https://modrinth.com/mod/fabric-api)。YACL 和 Mod Menu 是可选依赖。

- Minecraft 1.20.1：Fabric、Forge
- Minecraft 1.20.4：Fabric、NeoForge
- Minecraft 1.21–1.21.1：Fabric、NeoForge
- Minecraft 1.21.11：Fabric、NeoForge
- Minecraft 26.1–26.1.2：Fabric、NeoForge
- Minecraft 26.2：Fabric、NeoForge

## 资源包瞄准规则

其他模组的物品可以通过资源包启用瞄准。每个 JSON 文件包含一组物品谓词表达式，语法与
`/clear` 接受的物品参数相同。文件可分别放在以下目录：

- `assets/<namespace>/item_patterns/hold_to_aim/`
- `assets/<namespace>/item_patterns/use_to_aim/`

```json
["example:rifle", "#example:ranged[example:mode=aim]"]
```

模组的原版物品规则也通过同一资源机制提供。YACL 配置界面可以添加额外谓词；`smartAiming`
配置控制所有自动物品谓词行为，手动瞄准按键不受影响。

## 贡献者

[ArctynFox](https://github.com/ArctynFox),
[Flonja](https://github.com/Flonja),
[Serrenos](https://github.com/Serrenos),
[avpbynf](https://github.com/avpbynf)

## 感谢

[Nakou](https://github.com/Nakou),
[CodeZhangBorui](https://github.com/CodeZhangBorui)

<div align=center>
<details>
<summary>捐赠作者</summary>

<img alt=ΨQ src="https://github.com/Leawind/Third-Person/raw/gh-pages/docs/public/donate/IHY-216.jpg" width=648/>

> <details>
> <summary>通过微信捐赠</summary>
> <img alt=wechat src="https://github.com/Leawind/Third-Person/raw/gh-pages/docs/public/donate/wechat.jpg" width=320 />
> </details>
> <details>
> <summary>其他方式</summary>
>
> [Buy Me a Coffee](https://www.buymeacoffee.com/leawind)  
> [爱发电](https://afdian.com/a/Leawind)
>
> </details>

</details>
</div>
