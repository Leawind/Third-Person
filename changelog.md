### Added

* Add config: `camera_distance_mode`
* Allow item tag in item pattern
* Make it always use post effect of attached entity in spectator mode
* Disable double-tap sprinting in third person by default #153 #155
	* config: `allow_double_tap_sprint`

### Changed

* Support both 1.20 and 1.20.1
* Config `use_camera_pick_in_creative`:
	* Disabled by default
	* Moved to category _Other_

### Removed

* Remove config `sprint_impulse_threshold`

### Fixed

* Camera slightly shakes when hitting wall

### Compatibility

### Other

* Change package name to `com.github.leawind.thirdperson`
* Record stack trace when infinite value detected. Once player's rotation become NaN or infinity, it will log some information for debugging
