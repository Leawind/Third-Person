# Changelog

## v2.0.8-beta.3-mc1.20.1

### Features

* Use ModifyExpressionValue instead of Redirect. This will have better compatibility.

### Bug fix

* Compatible  with _Better Combat_ and _First Person Model_ #50
* Wrong translation key in YACL config screen
* Camera forcing first person in tall grass #54
* In third person, water is placed where player is looking at, rather than crosshair.

### Other

* Update translations
* build script: add yacl dependency in fabric publishing script.
* fix: Unexpected rotating when starting to use elytra in first person.
* style: add comments.
* Update README
* Optimize code

## v2.0.8-beta.2-mc1.20.1

### Features

* add config: `t2f_transition_halflife`

### Bug fix

* fix: Mouse sensitivity doubled in first person #49
* fix: MixinExtras not inited
* picking error in forge version

### Other

* optimize code

## v2.0.8-beta.1-mc1.20.1

Port from 2.0.7-mc1.19.4

### Features

* Use MixinExtras.
  * Replace `@Redirect` with `@WrapWithCondition`, this should resolve the conflict with _Do a Barrel Roll_.
* Remove YACL support for forge version due to some problems with YACL.

### Bug fix

* Key `force_aiming`, `toggle_aiming` not working

### Other

* Update `changelog_latest.txt` to `changelog_latest.md`
* Update deprecated method in YACL: `valueFormatter` -> `formatValue`
* Update build scripts
* add debug logs

## v2.0.7-mc1.19.4

Port from 2.0.7-mc1.19.3

## v2.0.7-mc1.19.3

Port from 2.0.7-mc1.19.2

## v2.0.7-mc1.19.2

### Features

* Shoot enemies like in first person
  * It can predict the target entity you want to shoot, even if the target is far away. Just aim at the target as if you were in first person.
  * Added config option: `enable_target_entity_predict`.
* Smooth transition when switching from third person to first person.
* Compatible with mod _do_a_barrel_roll_ (only Fabric version).
* Cloth Config API is now an optional dependency.
* Temporarily switch to first person when the camera entity is inside a wall.
* The smooth factors in the configuration has been changed to smoothing half-life for better intuition.
* When updating smooth eye position, multiply by the distance between it and the camera. This ensures a more consistent smoothing effect at different camera distances.

## v2.0.6-mc1.19.2

### Features

* When the camera approaches the player, make player semi-transparent.
* Save config when client stopping
* Enable third person mode by default
* Save third person mode in config
  * Add config: `is_third_person_mode`
* Temporarily switch to first person when using specific items (spyglass for vanilla)
  * Add config: `use_to_first_person_pattern_expressions`
* Change resource pack structure
  * `assets/<namespace>/item_patterns/hold_to_aim/<anyname>.json` Enable aim mode when holding these items
  * `assets/<namespace>/item_patterns/use_to_aim/<anyname>.json` Enable aim mode when using these items
  * `assets/<namespace>/item_patterns/use_to_first_person/<anyname>.json` Temporarily switch to first person when using these items

### Bug fix

* Camera through glasses
* Game crashing #44

## v2.0.5-mc1.19.2

### Features

* Remove config: `enable_buildin_item_aiming_rules`
* Load ItemPatterns from resource pack
* Rename config options
  *  `holding_item_aiming_rules` to `hold_to_aim_item_pattern_expressions`
  *  `using_item_aiming_rules` to `use_to_aim_item_pattern_expressions`
* In Creative mode, you can directly pick blocks from the crosshair, even if player entity's sight is obstructed
  * Add config: `use_camera_pick_in_creative`
* Method `Entity#pick` returns MISS when hitResult distance is too far

### Bug fix

* Unable quickly turn when sprinting
* Unable to join world in forge version #38

## v2.0.4-mc1.19.2

### Features

* Configurable aiming mode checking
  * add boolean config: Enable Build-in match rules
  * add string list config: Hand Item Match Rules
  * add string list config: Using Item Matching Rules

### Bug fix

* Player's unsmooth rotation
* In config screen, flying_smooth_factor option is in wrong tab

### Bug add

* Unable to join world in forge version

### Other

Optimizing and slight refactoring.

## v2.0.3-mc1.19.2

### Features

* Add Cloth-Config-API support for Forge version
* Auto rotate body when draw a bow
* Add config: auto_turn_body_drawing_a_bow
* Add key: toggle_pitch_lock

### Bug fix

* Can't pick entity in certain case
* Config is saved on dedicated server
* Config option lost in screen: render_crosshair_when_aiming

## v2.0.2-mc1.19.2

Port to 1.19.2

### Features

* Add config: normal/aiming_is_centered
* Now use Cloth Config API to build config screen for fabric version

### Bug adds

* No config screen for forge version by now, but you can modify config file manually. When trying to open config screen, config file will be reloaded.

## v2.0.1(1.19.4)

### Features

* Add config: Centered when flying
* Auto switch to aiming mode when holding some items.
  * ender_pearl
  * snowball
  * egg
  * splash_potion
  * lingering_potion
  * experience_bottle

### Fix bugs

* Wrong smooth factor when adjusting camera
* Can only swim forward
* Flashes when camera is close to player

## v2.0.1-beta.4-mc1.19.4

### Features

* Add config: Enable auto rotate when interecting. #4
* Add config: How to rotate when interecting. #26

### Fix bugs

* Camera is still moving when game paused

## v2.0.1-beta.3-mc1.19.4

### Features

* Add key: toggle mod enable
* Add config: Turn player with to camera when enter FP
* Add config: smooth factors about adjusting camera
* Now you can set smooth factor in range of [0, 1]

### Fix bugs

* Camera choppiness when moving (again)

### Hide bugs

* swimming to directions other than forward

## v2.0.1-beta.2-mc1.19.4

### Add features

* add config: lock pitch angle of camera
* Let player invible when camera close enough to player

### Fix bugs

* camera choppiness when moving

## v2.0.0-mc1.19.4

### Add features
* Use [Architectury](https://github.com/architectury/architectury-api)
* Use [YACL](https://github.com/isXander/YetAnotherConfigLib) to generate config screen
* Add [Modmenu](https://github.com/TerraformersMC/ModMenu) support for fabric version
* Add key binding to open config screen
* Ignore tall grass similar blocks when aiming
* Use dedicated smooth factor when flying with elytra
* Add config: disable auto rotate when moving #22
* Add config: if render crosshair when aiming
* Add config: if render crosshair when not aiming

### Fix bugs

* Can only run forward #20
* Camera go through wall
