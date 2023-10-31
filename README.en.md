[English](./README.en.md) | [简体中文](./README.md) | [Pусский](./README.ru.md)

[CurseForge](https://legacy.curseforge.com/minecraft/mc-mods/leawinds-third-person-perspective)


# LEAWIND's Third-Person Perspective

A much-improved third-person perspective mod.

Compatible with Minecraft Java Edition.

![Switch camera](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/switch_camera.gif?raw=true)
![Move](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/move.gif?raw=true)
![Shoot](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/shoot.gif?raw=true)
![Fight](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/fight.gif?raw=true)
![](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/fly.gif?raw=true)
![Destroy and create](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/destroy-create.gif?raw=true)
![Swim](https://github.com/LEAWIND/Images/blob/main/repository/Third-Person-Perspective/swim.gif?raw=true)

## Features

### Aiming Mode

When a player draws a bow, charges a trident, or loads a crossbow, they will automatically enter aiming mode.

However, players can also enter aiming mode by pressing specific keys.

In aiming mode, the camera gets closer to the player's head, and the player continuously aims at where the camera is pointing.

### Normal Mode

In the normal mode,

* While walking, running, or jumping on land, the player won't automatically follow the camera's rotation unless they use,
  break, or select blocks or entities.
* When swimming or flying with an elytra, the player's orientation will always be parallel to the camera's line of sight.

### Camera Positions

There are three camera positions:

* Right Rear
* Left Rear
* Head Rear

Press `Z` to switch between right and left positions, and hold it to switch to the head position.

### Auto-Switch Camera Position

The camera position will automatically switch when the player gets close to obstacles to prevent obstructed vision.

### Key Bindings

| Translation ID      | Key Name          | Function                             |
| ------------------- | ----------------- | ------------------------------------ |
| key.tpv_hold_aim    | Hold to Aim       | Allows forcing the aiming mode while holding the key |
| key.tpv_toggle_aim  | Toggle Aim        | Short press toggles between aiming and normal modes |
| key.tpv_toggle_side | Toggle Camera Side | Short press switches between left and right positions, long press switches to the head position |

### Miscellaneous

Too lazy to write more; experience other details in the game.

## Note

If you have Optifine installed, please disable the following option, as it may cause choppy camera movement in third-person
mode.

`Options -> Video Settings -> Performance -> Fast Math`
