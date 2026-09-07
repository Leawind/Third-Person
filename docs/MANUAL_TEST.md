# 手动测试

## 命令

以下命令适用于 1.20.5 及以上版本（物品组件语法）；更早版本的 NBT 语法不同。

### 召唤一匹马

```mcfunction
/summon minecraft:horse ~ ~ ~
{
	Bred:0b,
	Tame:1b,
	Invulnerable:1b,
	PersistenceRequired:1b,
	Health:30.0f,
	equipment:{saddle:{count:1,id:"minecraft:saddle"}},
	attributes:[
		{id:jump_strength, base:1d},
		{id:movement_speed, base:0.3375d},
		{id:max_health, base:30d}
	]
}
```

### 给予一把弩

```mcfunction
/give @s minecraft:crossbow[minecraft:enchantments={quick_charge:8}] 1
```

### 给予一把斧

```mcfunction
/give @s minecraft:netherite_axe[minecraft:enchantments={sharpness:5, mending:1, unbreaking:3}] 1
```
