# ItemPattern

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
| ------------------------ | ------------------------------------------------- |
| `item.minecraft.egg`     | Egg                                               |
| `minecraft.egg`          | Egg                                               |
| `minecraft:egg`          | Egg                                               |
| `egg`                    | Egg                                               |
| `crossbow`               | Crossbow (whether charged or not)                 |
| `crossbow{Charged:1b}`   | Charged crossbow                                  |
| `{Charged:1b}`           | Any item with an NBT tag `Charged` and value `1b` |

## Reference

[Item patterns in build-in resource pack](./ResourcePack#built-in-resource-pack)
