# Item Pattern

An Item-Pattern is a human-edited string that can be parsed into an `ItemPredicate` to determine if an `ItemStack` matches the specified criteria.

Format: `[#][namespace:]<id>[nbt]` or `<nbt>`

| Format                   | Meaning                                           | Examples                                                 |
| ------------------------ | ------------------------------------------------- | -------------------------------------------------------- |
| `[namespace:]<id>`       | Specifies the item ID                             | `egg`, `minecraft:egg`                                   |
| `#[namespace:]<id>`      | Refers to items with a specific tag               | `#minecraft:boat`, `#boat`                               |
| `[namespace:]<id><nbt>`  | Specifies the item ID and requires a specific NBT | `crossbow{Charged:1b}`, `minecraft:crossbow{Charged:1b}` |
| `#[namespace:]<id><nbt>` | Refers to items with a specific tag and NBT       | `#boats{Charged:1b}`, `#minecraft:boats{Charged:1b}`     |
| `<nbt>`                  | Requires a specific NBT structure                 | `{Charged:1b}`                                           |

The namespace in the item ID can be omitted. In mod configurations, the default namespace is `minecraft`. In resource packs, the default namespace is the one where the resource file is located.

## Examples

| Item Pattern           | Meaning                                        |
| ---------------------- | ---------------------------------------------- |
| `minecraft:egg`        | An egg                                         |
| `egg`                  | An egg                                         |
| `crossbow`             | A crossbow (whether charged or not)            |
| `crossbow{Charged:1b}` | A charged crossbow                             |
| `{Charged:1b}`         | Any item with an NBT tag `Charged` set to `1b` |
| `#boats`               | All boats, including rafts                     |
