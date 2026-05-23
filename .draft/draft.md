## Dependencies

当前版本

| Name                                                          | Fabric   | Forge               |
| ------------------------------------------------------------- | -------- | ------------------- |
| [Fabric API](https://fabricmc.net)                            | Required | N/A                 |
| [Architectury-api](https://github.com/architectury)           | Required | Required            |
| [Mod Menu](https://github.com/TerraformersMC/ModMenu)         | Optional | N/A                 |
| [YACL](https://github.com/isXander/YetAnotherConfigLib)       | Optional | Unexpected Optional |
| [Cloth Config API](https://github.com/shedaniel/cloth-config) | Optional | Optional            |
|                                                               |          |                     |

| Name                                                          | Fabric   | NeoForge |
| ------------------------------------------------------------- | -------- | -------- |
| [Fabric API](https://fabricmc.net)                            | Required | N/A      |
| [Architectury-api](https://github.com/architectury)           | Required | Required |
| [Mod Menu](https://github.com/TerraformersMC/ModMenu)         | Optional | N/A      |
| [YACL](https://github.com/isXander/YetAnotherConfigLib)       | Optional | Optional |
| [Cloth Config API](https://github.com/shedaniel/cloth-config) | Optional | Optional |
|                                                               |          |          |

暂未支持

| Name                                                                 | Fabric   | Forge    |
| -------------------------------------------------------------------- | -------- | -------- |
| [Forge Config API Port](https://github.com/Fuzss/forgeconfigapiport) | Optional | Optional |
| [Carbon Config]()                                                    | Optional | Optional |
|                                                                      |          |          |

## Changelog

### Features

### Bug fix

-   Unexpected rotate when using spyglass in first person

### Other

## Versions

|        | Fabric       | Forge        | NeoForge |
| ------ | ------------ | ------------ | -------- |
| 1.21.4 |              |              |          |
| 1.21.3 |              |              |          |
| 1.21.2 |              |              |          |
| 1.21.1 |              |              |          |
| 1.21   |              |              |          |
| 1.20.1 | 2.0.8-beta.5 | 2.0.8-beta.5 |          |
| 1.19.4 | 2.0.7        | 2.0.7        |          |
| 1.19.3 | 2.0.7        | 2.0.7        |          |
| 1.19.2 | 2.0.7        | 2.0.7        |          |
|        |              |              |          |

## 发布

### Code

-   Anno
    -   `@Deprecated`
    -   `@VersionSensitive`
    -   `@NotNull`, `@Nullable`
    -   `Contract()`
-   TODO, NOW
-   检查引用方式 `(?<!(^(import)|(package)).*)([a-z]+\.){3}`
-   Inspect Code
-   配置项默认值、可调范围、调整步长
-   构建并交叉测试

### 准备

-   更新 `gradle.properties` 中的版本号，版本命名规则参考 [SemVer](https://semver.org/)
-   检查 `<platform>/build.gradle` 中的依赖项
-   运行 `./gradlew clean` 清理构建文件
-   编辑 `changelog_latest.md`
-   确保 `gradle.properties` 中 `publish_debug_mode` 为 true
-   运行 `./gradlew modrinth` 测试发布并检查
-   将 `publish_debug_mode` 改成 false，正式发布
-   将版本号更改为 alpha 版本
-   发布到 curseforge
-   清空 `changelog_latest.md`

### 测试

#### 基本

-   配置项和按键的翻译
-   每个按键是否都有效
-   每个配置项是否都有效

#### 可能出现异常的操作

-   在专用服务器上加载此模组

#### 交叉测试

-   游戏模式
    -   生存
    -   创造
    -   旁观者模式
-   模组加载器
    -   Fabric
    -   Forge
    -   Quilt
-   相邻的各个游戏版本
-   行为
    -   配置屏幕的翻译
    -   加入服务器
    -   传送
    -   进入其他维度
    -   死亡
    -   进入高草丛
    -   窒息
    -   鞘翅飞行
    -   游泳
    -   钓鱼
    -   使用水桶
    -   观察者附身
        -   末影人
        -   苦力怕
        -   蜘蛛
    -   骑乘
        -   船
        -   马
        -   矿车
        -   猪
    -   使用望远镜
    -   射击
        -   弓
        -   弩
        -   三叉戟
        -   雪球
        -   末影珍珠
    -   放置方块时的朝向
        -   熔炉
        -   床
        -   楼梯
        -   漏斗
        -   观察者
        -   比较器
        -   中继器
        -   命令方块
