---
title: 🏠首页

layout: home
hero:
  name: Leawind的第三人称
  text:
  tagline: 一个实用、丝滑、功能丰富的第三人称模组
  image:
  actions:
    - theme: alt
      text: 🕗更新日志
      link: ./changelog
    - theme: alt
      text: ⚔️兼容性
      link: ./compatibility
    - theme: alt
      text: 💬疑问
      link: https://github.com/Leawind/Third-Person/discussions/categories/q-a
    - theme: alt
      text: 💡报告Bug
      link: https://github.com/LEAWIND/Third-Person/issues/new/choose
    - theme: brand
      text: 📥下载
      link: https://www.curseforge.com/minecraft/mc-mods/leawind-third-person/files/all
---

| ![MIT License](https://img.shields.io/badge/license-MIT-blue.svg) |
| ----------------------------------------------------------------- |

| [![](https://img.shields.io/curseforge/dt/930880?style=flat&logo=curseforge&color=F1643%5E&cacheSeconds=3600&label=下载量)](https://www.curseforge.com/minecraft/mc-mods/leawind-third-person) | [![](https://img.shields.io/modrinth/dt/S3D3QF0M?style=flat&logo=modrinth&color=17B85A&cacheSeconds=3600&label=下载量)](https://modrinth.com/mod/leawind-third-person) |
| ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |

## 特性

- **自由转动视角** 视角可以自由转动，同时保持玩家身体不动。
- **自由调整相机位置**
  - 按住 `Z` 时，移动鼠标可以调整相机偏移量（玩家在屏幕上的位置），鼠标滚轮可以调整相机到玩家的距离
  - **快速切换相机偏移位置（左|中|右）** 短按 `CapsLock` 可切换左右，按住 `CapsLock` 可以切换到居中
- **智能瞄准模式** 根据玩家手持物品和使用状态自动切换到瞄准模式。规则可自定义以兼容其他模组的物品，这是相关[文档](https://leawind.github.io/Third-Person/zh_cn/Details/ItemPredicate)。
- **类似第一人称的射击** 第三人称下瞄准远处的敌人时，模组会自动预测你想要射击的目标实体，你只需要像第一人称那样将准星放在敌人上方即可
- **玩家半透明** 当玩家实体阻挡视线时，会变得半透明（可能不兼容 Sodium，默认禁用此功能）
- **平滑切换视角** 在第一/第三人称视角间平滑过渡
- **随时禁用** 如果此模组引发了故障，你随时可以在游戏中通过配置界面或快捷键来禁用此模组，恢复成原版第三人称视角
- **纯客户端** 不需要在服务端安装此模组，因此可以在服务器中使用

其他特性：

- 飞行时隐藏准星
- 鞘翅飞行时自动居中，且使用独立的平滑系数
- 当相机离玩家足够近时，玩家变得不可见
- 跳过原版的镜像第三人称（俗称第二人称，即相机总是在玩家面前）
- 使用特定物品时，可以暂时进入第一人称视角，比如望远镜。可通过配置自定义相关物品。
- 玩家转向兴趣点。当相机位于后方时，玩家实体看向准星所指处。当相机位于面前时，玩家实体看向相机。

:::tip
本文档仅适用于模组的最新版本。
:::

## 相关链接

- [Github 仓库](https://github.com/Leawind/Third-Person)
- [Gitee 镜像仓库](https://gitee.com/leawind/Third-Person)
- [Modrinth](https://modrinth.com/mod/leawind-third-person)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/leawind-third-person)
- [MC百科](https://www.mcmod.cn/class/12699.html)

## 下载

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/leawind-third-person/files/all)
- [Modrinth](https://modrinth.com/mod/leawind-third-person)
- [Github releases](https://github.com/LEAWIND/Third-Person/releases)

## 疑问

如果你对于此模组有任何疑问，欢迎在 [Github 仓库的讨论区](https://github.com/Leawind/Third-Person/discussions/categories/q-a) 提问。
