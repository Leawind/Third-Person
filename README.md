[English](./README.en.md) | [简体中文](./README.md) | [Pусский](./README.ru.md)

[CurseForge](https://legacy.curseforge.com/minecraft/mc-mods/leawinds-third-person-perspective)

# LEAWIND 的第三人称视角

好得多的第三人称视角MOD。

适用于 Minecraft Java Edition

![Switch camera](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/switch_camera.gif?raw=true)
![Move](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/move.gif?raw=true)
![Shoot](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/shoot.gif?raw=true)
![Fight](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/fight.gif?raw=true)
![](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/fly.gif?raw=true)
![Destroy and create](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/destroy-create.gif?raw=true)
![Swim](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/swim.gif?raw=true)

## 特性

### 瞄准状态

当玩家拉弓、拿着三叉戟蓄力、或手持上了弦的弩时，会自动进入瞄准状态。

但玩家按下特定的按键时，也会进入瞄准状态。

瞄准状态下，摄像机会更加靠近玩家的脑袋，并且玩家会实时瞄准摄像机视线的落点。

### 正常状态

正常状态下，

* 在陆地上`爬|走|跑|跳`时，玩家不会实时跟随相机转动，除非使用、摧毁、选取方块或实体。
* 当游泳或使用鞘翅飞行时，玩家的朝向会始终与摄像机视线平行。

### 摄像机位

共有 3 种摄像机位

* 右后方
* 左后方
* 头顶后方

按`Z`键可以在左右之间切换，按住不放可以切换到头顶。

### 自动切换摄像机位

当玩家靠近障碍物时，摄像机位会自动切换以避免视线被遮挡。

### 按键

| 译名ID              | 默认  | 按键名称     | 功能                           |
| ------------------- |-----| ------------ | ------------------------------ |
| key.tpv_hold_aim    |     | 按住瞄准     | 按住后可以强制保持瞄准状态     |
| key.tpv_toggle_aim  |     | 切换瞄准     | 短按可以在瞄准和普通模式间切换 |
| key.tpv_toggle_side | `Z` | 切换摄像机位 | 短按左右切换，长按切换至头顶   |

### 其他

懒得写了，其他细节自己进游戏体验吧

## 注意

如果你安装了 Optifine，请关闭以下选项，否则第三人称时转动视角可能会不流畅。

`选项 -> 视频设置 -> 性能 -> 快速运算`
