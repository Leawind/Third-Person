<div align=center>

| English | [中文](./README-ZH.md) |
|---------|----------------------|

[![CurseForge downloads](https://img.shields.io/curseforge/dt/930880?style=flat&logo=curseforge&color=F1643%5E&cacheSeconds=3600&label=Downloads)](https://www.curseforge.com/minecraft/mc-mods/leawind-third-person)
[![Modrinth downloads](https://img.shields.io/modrinth/dt/S3D3QF0M?style=flat&logo=modrinth&color=17B85A&cacheSeconds=3600&label=Downloads)](https://modrinth.com/mod/leawind-third-person)

[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/LEAWIND/Third-Person?tab=MIT-1-ov-file)
[![Stars](https://img.shields.io/github/stars/LEAWIND/Third-Person?style=flat&logo=github&color=daaa3f)](https://github.com/LEAWIND/Third-Person)

# Leawind's Third Person

A practical, smooth, feature-rich third person mod for all Minecraft players.

</div>

- **Client-side only.** Servers do not need to install the mod.
- **Free camera rotation.** Looking around does not immediately rotate the player's body; movement remains relative to
  the camera.
- **Adjustable shoulder camera.** Hold `Z`, move the mouse to adjust the screen-space offset, and use the wheel to scale
  distance by 1.25 per step. Tap `Caps Lock` to switch shoulders or hold it to center. Both keys can be rebound.
- **Normal and aiming compositions.** Bows, crossbows, spears, a manually bound aim key, and resource-pack rules can
  activate the closer aiming camera.
- **Third-person interaction alignment.** Attacks, item use, and block picking align the player's authoritative look ray
  with the rendered camera intent, then reuse vanilla picking without increasing reach.
- **Smooth perspective switching.** Perspective API owns perspective selection, transitions, and cooperation with other
  API-based camera mods.
- **Focused configuration.** A small validated JSON config is always available. Install YACL for the optional in-game
  screen; Fabric users can open it through Mod Menu.

## Requirements and supported versions

[Perspective API](https://modrinth.com/mod/perspective-api) is required. Fabric builds also require
[Fabric API](https://modrinth.com/mod/fabric-api). YACL and Mod Menu are optional.

- Minecraft 1.20.1: Fabric and Forge
- Minecraft 1.20.4: Fabric and NeoForge
- Minecraft 1.21–1.21.1: Fabric and NeoForge
- Minecraft 1.21.11: Fabric and NeoForge
- Minecraft 26.1–26.1.2: Fabric and NeoForge
- Minecraft 26.2: Fabric and NeoForge

## Resource-pack aiming rules

Automatic aiming is entirely driven by item predicates. Each JSON file is an array of expressions using the same
syntax as the item argument accepted by `/clear`. Put files in one of these resource-pack directories:

- `assets/<namespace>/item_patterns/hold_to_aim/`
- `assets/<namespace>/item_patterns/use_to_aim/`

```json
["example:rifle", "#example:ranged[example:mode=aim]"]
```

The mod's vanilla behavior is supplied through the same resource mechanism. Extra predicates can also be edited in
the YACL config screen. The `smartAiming` setting controls all automatic predicate behavior; the manual aim key remains
independent.

## Contributors

[ArctynFox](https://github.com/ArctynFox),
[Flonja](https://github.com/Flonja),
[Serrenos](https://github.com/Serrenos),
[avpbynf](https://github.com/avpbynf)

## Thanks

[Nakou](https://github.com/Nakou),
[CodeZhangBorui](https://github.com/CodeZhangBorui)

<div align=center>
<details>
<summary>Donate to author</summary>

<img alt=ΨQ src="https://github.com/Leawind/Third-Person/raw/gh-pages/docs/public/donate/IHY-216.jpg" width=648/>

> <details>
> <summary>Donate using Wechat</summary>
> <img alt=wechat src="https://github.com/Leawind/Third-Person/raw/gh-pages/docs/public/donate/wechat.jpg" width=320 />
> </details>
> <details>
> <summary>Other ways</summary>
>
> [Buy Me a Coffee](https://www.buymeacoffee.com/leawind)  
> [Afdian (爱发电)](https://afdian.com/a/Leawind)
>
> </details>

</details>
</div>
