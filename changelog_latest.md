### Add

- Add config: `hide_crosshair_when_flying`
- Let player rotate to interest point, keep body not move
	- Add config: `player_rotate_to_intrest_point`
- Add config: `sprint_impulse_threshold`

### Changes

- Update translation of config option
- Remove YACL dependency declaration in forge `mods.toml`. But still support YACL 3.2.2 or below

### Fixed

- Sneak speed insanely slow in first person #133
- Player blinking when switch to first-person while moving
- Can't rotate camera with _Controllable_ #34
- Can't toggle perspective with _Controllable_ #34
- _AutoThirdPerson_ can't toggle perspective
- Unexpected sprinting when walking
- Mouse lag when adjusting camera offset
