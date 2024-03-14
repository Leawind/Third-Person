:::warning
This doc needs to be updated
:::

## Configuration File

The configuration file for this mod is in JSON format and is located at `config/leawind_third_person.json` within the game's runtime directory.

| ID                                          | Type      | Description                                                                                         | Default |
| ------------------------------------------- | --------- | --------------------------------------------------------------------------------------------------- | ------- |
| `is_mod_enable`                             | `boolean` | Is mod enabled                                                                                      | `true`  |
| `is_third_person_mode`                      | `boolean` | Is now third person mode                                                                            | `true`  |
| `lock_camera_pitch_angle`                   | `boolean` | Lock camera pitch angle                                                                             | `false` |
| `player_rotate_with_camera_when_not_aiming` | `boolean` | Force the player's line of sight to parallel the camera's line of sight when not aiming.            | `false` |
| `rotate_to_moving_direction`                | `boolean` | Automatically Rotate Player to Moving Direction                                                     | `true`  |
| `auto_rotate_interacting`                   | `boolean` | When interacting, should the player turn towards the target?                                        | `true`  |
| `rotate_interacting_type`                   | `boolean` | Type of rotation during interaction                                                                 |         |
| `auto_turn_body_drawing_a_bow`              | `boolean` | Automatically rotate the player's body when drawing a bow                                           | `false` |
| `available_distance_count`                  | `int`     | Number of available values when adjusting camera distance (default: Z key + scroll wheel)           | `16`    |
| `camera_distance_min`                       | `double`  | Minimum camera distance when adjusting with scroll wheel (default: Z key + scroll wheel)            | `0.5`   |
| `camera_distance_max`                       | `double`  | Maximum camera distance when adjusting with scroll wheel (default: Z key + scroll wheel)            | `8.0`   |
| `center_offset_when_flying`                 | `boolean` | Should the player be centered when flying (disables camera offset)                                  | `true`  |
| `use_camera_pick_in_creative`               | `boolean` | Allow direct block selection in creative mode (otherwise, player's line of sight may be obstructed) | `true`  |
| `turn_with_camera_when_enter_first_person`  | `boolean` | When switching from third person to first person, should the player turn towards the camera         | `true`  |
| `camera_ray_trace_length`                   | `double`  | Camera line of sight detection distance                                                             | `256.0` |
| `player_fade_out_enabled`                   | `boolean` | Should the player blur at close range                                                               | `true`  |
| `render_crosshair_when_not_aiming`          | `boolean` | Should the crosshair be displayed when not aiming                                                   | `true`  |
| `render_crosshair_when_aiming`              | `boolean` | Should the crosshair be displayed when aiming                                                       | `true`  |

## Camera Offset

Setting the smoothing factor to 0 results in no smoothing effect.

| ID                                      | Type     | Description                                                                     | Default |
| --------------------------------------- | -------- | ------------------------------------------------------------------------------- | ------- |
| `flying_smooth_factor`                  | `double` | Smoothing factor during flying                                                  | `0.5`   |
| `adjusting_camera_offset_smooth_factor` | `double` | Smoothing factor when adjusting camera offset (default: Z key)                  | `0.1`   |
| `adjusting_distance_smooth_factor`      | `double` | Smoothing factor when adjusting camera distance (default: Z key + scroll wheel) | `0.1`   |
| `normal_smooth_factor_horizon`          | `double` | Horizontal smoothing factor in non-aiming mode                                  | `0.5`   |
| `normal_smooth_factor_vertical`         | `double` | Vertical smoothing factor in non-aiming mode                                    | `0.5`   |
| `normal_camera_offset_smooth_factor`    | `double` | Smoothing factor for camera offset in non-aiming mode                           | `0.5`   |
| `normal_distance_smooth_factor`         | `double` | Smoothing factor for camera distance in non-aiming mode                         | `0.64`  |
| `aiming_smooth_factor_horizon`          | `double` | Horizontal smoothing factor in aiming mode                                      | `0.002` |
| `aiming_smooth_factor_vertical`         | `double` | Vertical smoothing factor in aiming mode                                        | `0.002` |
| `aiming_camera_offset_smooth_factor`    | `double` | Smoothing factor for camera offset in aiming mode                               | `0.1`   |
| `aiming_distance_smooth_factor`         | `double` | Smoothing factor for camera distance in aiming mode                             | `0.11`  |

### Offsets

These configuration items do not need manual modification; adjust them in-game using the Z key and mouse scroll wheel.

| ID                     | Type      | Description                                      | Default |
| ---------------------- | --------- | ------------------------------------------------ | ------- |
| `normal_is_centered`   | `boolean` | Should the camera be centered in non-aiming mode | `false` |
| `normal_max_distance`  | `double`  | Camera distance to the player in non-aiming mode | `2.5`   |
| `normal_offset_x`      | `double`  | Horizontal offset in non-aiming mode             | `-0.28` |
| `normal_offset_y`      | `double`  | Vertical offset in non-aiming mode               | `0.31`  |
| `normal_offset_center` | `double`  | Vertical offset when centered in non-aiming mode | `0.24`  |
| `aiming_is_centered`   | `boolean` | Should the camera be centered in aiming mode     | `false` |
| `aiming_max_distance`  | `double`  | Camera distance to the player in aiming mode     | `0.89`  |
| `aiming_offset_x`      | `double`  | Horizontal offset in aiming mode                 | `-0.47` |
| `aiming_offset_y`      | `double`  | Vertical offset in aiming mode                   | `-0.09` |
| `aiming_offset_center` | `double`  | Vertical offset when centered in aiming mode     | `0.48`  |

## Item Mode

| ID                                        | Type       | Description                                                                           | Default |
| ----------------------------------------- | ---------- | ------------------------------------------------------------------------------------- | ------- |
| `hold_to_aim_item_pattern_expressions`    | `String[]` | List of [Item Pattern](./ItemPattern) expressions for holding to aim                  | `[]`    |
| `use_to_aim_item_pattern_expressions`     | `String[]` | List of [Item Pattern](./ItemPattern) expressions for using to aim                    | `[]`    |
| `use_to_first_person_pattern_expressions` | `String[]` | List of [Item Pattern](./ItemPattern) expressions for using to switch to first person | `[]`    |
