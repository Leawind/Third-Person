# 物品模式

物品模式是人工编辑的字符串，可以被解析为物品谓词，用来判断一个物品栈（ItemStack）是否符合规则

格式：`[#][namespace:]<id>[nbt]` 或 `<nbt>`

| 格式                     | 含义                              | 示例                                                    |
| ------------------------ | --------------------------------- | ------------------------------------------------------- |
| `[namespace:]<id>`       | 指定物品 ID                       | `egg`, `minecraft:egg`                                  |
| `#[namespace:]<id>`      | 拥有特定标签                      | `#minecraft:boat`, `#boat`                              |
| `[namespace:]<id><nbt>`  | 指定物品ID，且 NBT 符合特定结构   | `crossbow{Charged:1b}` `minecraft:crossbow{Charged:1b}` |
| `#[namespace:]<id><nbt>` | 拥有特定标签，且 NBT 符合特定结构 | `#boats{Charged:1b}` `#minecraft:boats{Charged:1b}`     |
| `<nbt>`                  | NBT 符合指定结构                  | `{Charged:1b}`                                          |

物品 ID 中的命名空间可以省略，在模组配置中，命名空间的缺省值是 `minecraft`，在资源包中，默认命名空间就是资源文件所在的命名空间。

## 示例

| 物品模式               | 含义                                            |
| ---------------------- | ----------------------------------------------- |
| `minecraft:egg`        | 鸡蛋                                            |
| `egg`                  | 鸡蛋                                            |
| `crossbow`             | 弩（无论是否已装填）                            |
| `crossbow{Charged:1b}` | 已装填的弩                                      |
| `{Charged:1b}`         | NBT 标签里有 Charged 属性，且值为 1b 的任意物品 |
| `#boats`               | 所有船，包括竹筏                                |
