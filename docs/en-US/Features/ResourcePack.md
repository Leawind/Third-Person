
# Resource Packs

## Built-in Resource Pack

The mod's built-in resource pack includes [ItemPattern](./ItemPattern.md) relevant to the vanilla Minecraft.

* Enters aiming mode when holding these items
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

* Enters aiming mode when using these items
:::details `assets/minecraft/item_patterns/use_to_aim/vanilla.json`
```json
[
	"minecraft:bow",
	"minecraft:trident"
]
```
:::

* Temporarily switches to first-person when using these items
:::details `assets/minecraft/item_patterns/use_to_first_person/vanilla.json`

```json
[
	"minecraft:spyglass"
]
```
:::

## ItemPattern Sets

Currently, three types of sets are supported:

* `hold_to_aim`: **Hold-to-Aim _ItemPattern_ Set:** Enters aiming mode when the player holds any item that matches any pattern in this set.
* `use_to_aim`: **Use-to-Aim _ItemPattern_ Set:** Enters aiming mode when the player is using any item that matches any pattern in this set.
* `use_to_first_person`: **Temporary Switch to First Person _ItemPattern_ Set:** Temporarily switches to the first-person perspective when the player is using any item that matches any pattern in this set.

Each set corresponds to a directory in the resource pack. For example, the `hold_to_aim` set corresponds to the directory `assets/<namespace>/item_patterns/hold_to_aim/`.

This directory can contain multiple JSON files with arbitrary names, each containing several _ItemPattern Expressions_ (see the built-in resource pack example above).

All _ItemPattern_ in all files in this directory will be merged into the set it represents and will take effect in the game.

## Additional Resource Packs

By adding additional resource packs, other items can also automatically enter aiming mode in third person view.

`assets/<namespace>/item_patterns/hold_to_aim/<anyname>.json`
`assets/<namespace>/item_patterns/use_to_aim/<anyname>.json`
`assets/<namespace>/item_patterns/use_to_first_person/<anyname>.json`

:::warning
Namespaces and names can be freely chosen, but JSON files with the same path in different resource packs will conflict with each other. The file with the highest priority in the resource pack will take effect.
:::
