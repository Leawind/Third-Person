# Beta channel

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

