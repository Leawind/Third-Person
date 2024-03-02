# 资源包

## 内置资源包

本模组内置资源包中仅包含适用于原版 Minecraft 的相关[物品模式](./ItemPattern)。

* 持有这些物品时进入瞄准模式
:::details `assets/minecraft/item_patterns/hold_to_aim/vanilla.json`
```json
[
	"minecraft:crossbow{Charged:1b}",
	"minecraft:ender_pearl",
	"minecraft:snowball",
	"minecraft:egg",
	"minecraft:splash_potion",
	"minecraft:lingering_potion",
	"minecraft:experience_bottle"
]
```
:::

* 使用这些物品时进入瞄准模式
:::details `assets/minecraft/item_patterns/use_to_aim/vanilla.json`
```json
[
	"minecraft:bow",
	"minecraft:trident"
]
```
:::

* 使用这些物品时暂时进入第一人称视角
:::details `assets/minecraft/item_patterns/use_to_first_person/vanilla.json`
```json
[
	"minecraft:spyglass"
]
```
:::

## 物品模式集合

目前已支持的集合有 3 种：
* `hold_to_aim` **手持即瞄准的 _物品模式_ 集合** 当玩家手持的任意物品符合集合中任意模式时，进入瞄准模式。
* `use_to_aim` **使用即瞄准的 _物品模式_ 集合** 当玩家正在使用的物品符合集合中任意模式时，进入瞄准模式。
* `use_to_first_person` **使用时暂时切换到第一人称的 _物品模式_ 集合** 当玩家正在使用的物品符合集合中任意模式时，暂时入第一人称模式。

每个集合都对应着资源包中的一个目录。例如集合 `hold_to_aim` 对应的资源包目录是 `assets/<命名空间>/item_patterns/hold_to_aim/`。

该目录中可以包含多个任意名称的json文件，每个文件中可以包含若干 _物品模式_ 表达式（见下方的内置资源包示例）。

该目录中的所有文件中的所有 _物品模式_ 都将会合并到这个目录所代表的集合中并在游戏中生效。

## 添加额外资源包

通过添加额外的资源包，可以使其他物品也在第三人称下自动进入瞄准模式。

`assets/<命名空间>/item_patterns/<hold_to_aim|use_to_aim|use_to_first_person>/<任意名称>.json`

:::warning
命名空间和名称可以任取，但是不同资源包中拥有相同路径的 JSON 文件会互相冲突，最终生效的文件将是所在资源包优先级最高的那个。
:::

