# 手动测试

## 命令

以下命令适用于 1.21 及以上版本；1.20.5/1.20.6 的附魔组件需要 `levels` 包装
（如 `minecraft:enchantments={levels:{quick_charge:8}}`），更早版本使用 NBT 语法。

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
