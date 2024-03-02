# 📖 Detailed Features

## Configuration File

The configuration file for this mod is in JSON format and is located at `config/leawind_third_person.json` within the game's runtime directory.

## Aim Mode Checking Rules

In third person view, the mod determines whether to enter aiming mode based on the items the player is holding or currently using.

During gameplay, the mod maintains two sets of [_ItemPattern_](#itempattern):

* **Hold-to-Aim _ItemPattern_ Set:** Enters aiming mode when the player holds any item that matches any pattern in this set.
* **Use-to-Aim _ItemPattern_ Set:** Enters aiming mode when the player is using any item that matches any pattern in this set.

_ItemPattern_ in the sets can originate from two sources:

* **mod Configuration:** _ItemPattern_ can be defined in the mod's configuration interface.
* **Resource Packs:** This includes built-in resource packs of this mod, resource packs from other mods, and manually installed resource packs by the player.

## Temporary Switch to First Person when Using Specific Items

In third person view, when the player uses specific items, it temporarily switches to the first-person perspective. Upon stopping the use of that item, it returns to the third-person perspective.

Similar to the aim mode determination rules, _ItemPattern_ are used for determining these types of items. Customization of related _ItemPattern_ can be done through configuration or resource packs.

## ItemPattern

_ItemPattern_ are rules used to match items with certain characteristics based on NBT tags. They are used to determine if a specific item in the player's inventory conforms to a certain rule.

_ItemPattern_ can be described using _ItemPattern Expressions_. For example, to match a crossbow with the NBT tag `Charged` having a value of `1b`, the following _ItemPattern_ can be used:

> An item with the ID `minecraft:crossbow` and the `Charged` tag value set to `1b`.

The expression is `minecraft:crossbow{Charged:1b}`.

Expressions can have one of the following three structures:

| Structure            | Meaning                                           | Example                |
| -------------------- | ------------------------------------------------- | ---------------------- |
| Item ID              | Matches any item with the same ID                 | `egg`, `minecraft:egg` |
| NBT Tag              | Matches any item with the specified tag structure | `{Charged:1b}`         |
| Item ID with NBT Tag | Matches items that satisfy both conditions        | `crossbow{Charged:1b}` |

Examples:

| _ItemPattern_ Expression | Meaning                                           |
| ---------------------- | ------------------------------------------------- |
| `item.minecraft.egg`   | Egg                                               |
| `minecraft.egg`        | Egg                                               |
| `minecraft:egg`        | Egg                                               |
| `egg`                  | Egg                                               |
| `crossbow`             | Crossbow (whether charged or not)                 |
| `crossbow{Charged:1b}` | Charged crossbow                                  |
| `{Charged:1b}`         | Any item with an NBT tag `Charged` and value `1b` |

## Resource Packs

### ItemPattern Sets

Currently, three types of sets are supported:

* `hold_to_aim`: **Hold-to-Aim _ItemPattern_ Set:** Enters aiming mode when the player holds any item that matches any pattern in this set.
* `use_to_aim`: **Use-to-Aim _ItemPattern_ Set:** Enters aiming mode when the player is using any item that matches any pattern in this set.
* `use_to_first_person`: **Temporary Switch to First Person _ItemPattern_ Set:** Temporarily switches to the first-person perspective when the player is using any item that matches any pattern in this set.

Each set corresponds to a directory in the resource pack. For example, the `hold_to_aim` set corresponds to the directory `assets/<namespace>/item_patterns/hold_to_aim/`.

This directory can contain multiple JSON files with arbitrary names, each containing several _ItemPattern Expressions_ (see the built-in resource pack example below).

All _ItemPattern_ in all files in this directory will be merged into the set it represents and will take effect in the game.

### Built-in Resource Pack

The mod's built-in resource pack includes _ItemPattern_ relevant to the vanilla Minecraft.

:::details Enters aiming mode when holding these items

`assets/minecraft/item_patterns/hold_to_aim/vanilla.json`

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

:::details Enters aiming mode when using these items

`assets/minecraft/item_patterns/use_to_aim/vanilla.json`

```json
[
	"minecraft:bow",
	"minecraft:trident"
]
```
:::

:::details Temporarily switches to first-person when using these items

`assets/minecraft/item_patterns/use_to_first_person/vanilla.json`

```json
[
	"minecraft:spyglass"
]
```
:::

### Additional Resource Packs

By adding additional resource packs, other items can also automatically enter aiming mode in third person view.

`assets/<namespace>/item_patterns/<hold_to_aim|use_to_aim|use_to_first_person>/<anyname>.json`

:::warning
Namespaces and names can be freely chosen, but JSON files with the same path in different resource packs will conflict with each other. The file with the highest priority in the resource pack will take effect.
:::
