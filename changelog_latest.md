### Features

* Do not rotate when eating.
	* add config: do_not_rotate_when_eating.
* Render in vanilla way when camera entity opacity is close to 1.0
* Determine aiming mode by animation (bow or spear) #69
	* Add config: `determine_aim_mode_by_animation`.
* Add category camera_offset in yacl config screen.
* Remove config `turn_with_camera_when_enter_first_person`.
* When switching to third person, camera start from eye position.
* No transition when enter temporary first person.

### Bug fix

* Numbers are not allowed in item patterns. #67
* The default namespace of item patterns is always `minecraft`. #68

### Other

* Check transitioning in render tick pre. e45c556
* Reformat code, suppress some warnings.
* Update .editorconfig.
* Update build scripts.
* Log error when entity rotation is NaN. This can help debugging.
* Optimize item pattern matching.
