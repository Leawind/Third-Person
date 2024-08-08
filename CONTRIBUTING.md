# Contributing

## Basic

* Use UTF-8 encoding.
* Use LF line ending.
* Use .editorconfig to format code.

## Commit message format

These are some requirements of commit message:

* use lowercase
* no . at the end
* fit the format below

```
<type>: <title> [#issueId]

[description]
```

Example:

```
fix: too many downloads #999
```

### Type (required)

- feat: feature change
- fix: fix bug
- style: code format or comments change
- chore: build script change
- refactor: refactor code
- perf: improve performance or experience
- revert: revert commit

## How to add a new config item

### Define config item and set default value

Edit `net.leawind.mc.thirdperson.config.AbstractConfig`

```
@Expose public double my_option = 0.5;
```

### Add translations

Edit `resources/assets/minecraft/lang/*.json`

### Use

Use the config item somewhere.

### Edit config screen builder

Find all subclass of abstract class `net.leawind.mc.thirdperson.screen.ConfigScreenBuilder`

For example

* `net.leawind.mc.thirdperson.screen.YaclConfigScreenBuilder`
  ```
		.option(option("my_option", defaults.my_option, 0D, 1D, 0.05D, () -> config.my_option, v -> config.my_option = v).build()) //
  ```
* `net.leawind.mc.thirdperson.screen.ClothConfigScreenBuilder`
  ```
		CATEGORY_OTHER.addEntry(buildDoubleEntry("my_option", 0D, 1D, defaults.my_option, config.my_option, v -> config.my_option = v, entryBuilder));
  ```

Don't forget to debug and determine the default value and adjustable range for the new config item.
