:::warning
这篇文档需要更新
:::

# 配置文件

此模组的配置文件是 json 格式。位于游戏运行目录下 `config/leawind_third_person.json`

| ID                                          | 类型      | 描述                                                       | 默认值  |
| ------------------------------------------- | --------- | ---------------------------------------------------------- | ------- |
| `is_mod_enable`                             | `boolean` | 模组是否启用                                               | `true`  |
| `is_third_person_mode`                      | `boolean` | 是否切换为第三人称                                         | `true`  |
| `lock_camera_pitch_angle`                   | `boolean` | 锁定相机俯仰角                                             | `false` |
| `player_rotate_with_camera_when_not_aiming` | `boolean` | 非瞄准状态下，玩家是否跟随相机旋转                         | `false` |
| `rotate_to_moving_direction`                | `boolean` | 玩家移动时是否转向前进方向                                 | `true`  |
| `auto_rotate_interacting`                   | `boolean` | 交互时玩家是否转向目标                                     | `true`  |
| `rotate_interacting_type`                   | `boolean` | 交互时转动类型                                             |         |
| `auto_turn_body_drawing_a_bow`              | `boolean` | 拉弓时自动旋转身体                                         | `false` |
| `available_distance_count`                  | `int`     | 使用滚轮调节相机距离（默认Z键+滚轮）时，可用的值的数量     | `16`    |
| `camera_distance_min`                       | `double`  | 使用滚轮调节相机距离（默认Z键+滚轮）时，相机最小距离       | `0.5`   |
| `camera_distance_max`                       | `double`  | 使用滚轮调节相机距离（默认Z键+滚轮）时，相机最大距离       | `8.0`   |
| `center_offset_when_flying`                 | `boolean` | 飞行时是否将玩家居中（禁用相机偏移量）                     | `true`  |
| `use_camera_pick_in_creative`               | `boolean` | 创造模式下允许直接从准星选取方块（否则玩家视线可能被遮挡） | `true`  |
| `turn_with_camera_when_enter_first_person`  | `boolean` | 从第三人称进入第一人称时，玩家转向相机                     | `true`  |
| `camera_ray_trace_length`                   | `double`  | 相机视线探测距离                                           | `256.0` |
| `player_fade_out_enabled`                   | `boolean` | 近距离玩家是否虚化                                         | `true`  |
| `render_crosshair_when_not_aiming`          | `boolean` | 非瞄准状态下是否显示准星                                   | `true`  |
| `render_crosshair_when_aiming`              | `boolean` | 瞄准状态下是否显示准星                                     | `true`  |

## 相机偏移

将平滑系数设置为0就完全没有平滑效果了。

| ID                                      | 类型     | 描述                                     | 默认值  |
| --------------------------------------- | -------- | ---------------------------------------- | ------- |
| `flying_smooth_factor`                  | `double` | 飞行时的平滑系数                         | `0.5`   |
| `adjusting_camera_offset_smooth_factor` | `double` | 调整相机偏移量（默认Z键）时的平滑系数    | `0.1`   |
| `adjusting_distance_smooth_factor`      | `double` | 调整相机距离（默认Z键+滚轮）时的平滑系数 | `0.1`   |
| `normal_smooth_factor_horizon`          | `double` | 非瞄准状态下水平平滑系数                 | `0.5`   |
| `normal_smooth_factor_vertical`         | `double` | 非瞄准状态下垂直平滑系数                 | `0.5`   |
| `normal_camera_offset_smooth_factor`    | `double` | 非瞄准状态下相机偏移量的平滑系数         | `0.5`   |
| `normal_distance_smooth_factor`         | `double` | 非瞄准状态下相机距离的平滑系数           | `0.64`  |
| `aiming_smooth_factor_horizon`          | `double` | 瞄准状态下水平平滑系数                   | `0.002` |
| `aiming_smooth_factor_vertical`         | `double` | 瞄准状态下垂直平滑系数                   | `0.002` |
| `aiming_camera_offset_smooth_factor`    | `double` | 瞄准状态下相机偏移量的平滑系数           | `0.1`   |
| `aiming_distance_smooth_factor`         | `double` | 瞄准状态下相机距离的平滑系数             | `0.11`  |

### 偏移量

这些配置项不需要手动修改，在游戏中使用Z键和鼠标滚轮即可调整。

| ID                     | 类型      | 描述                           | 默认值  |
| ---------------------- | --------- | ------------------------------ | ------- |
| `normal_is_centered`   | `boolean` | 非瞄准模式下是否将相机居中     | `false` |
| `normal_max_distance`  | `double`  | 非瞄准模式下相机到玩家的距离   | `2.5`   |
| `normal_offset_x`      | `double`  | 非瞄准模式下水平偏移量         | `-0.28` |
| `normal_offset_y`      | `double`  | 非瞄准模式下垂直偏移量         | `0.31`  |
| `normal_offset_center` | `double`  | 非瞄准模式下居中时的垂直偏移量 | `0.24`  |
| `aiming_is_centered`   | `boolean` | 瞄准模式下是否将相机居中       | `false` |
| `aiming_max_distance`  | `double`  | 瞄准模式下相机到玩家的距离     | `0.89`  |
| `aiming_offset_x`      | `double`  | 瞄准模式下水平偏移量           | `-0.47` |
| `aiming_offset_y`      | `double`  | 瞄准模式下垂直偏移量           | `-0.09` |
| `aiming_offset_center` | `double`  | 瞄准模式下居中时的垂直偏移量   | `0.48`  |

## 物品模式

| ID                                        | 类型       | 描述                                | 默认值 |
| ----------------------------------------- | ---------- | ----------------------------------- | ------ |
| `hold_to_aim_item_pattern_expressions`    | `String[]` | [物品模式](./ItemPattern)表达式列表 | `[]`   |
| `use_to_aim_item_pattern_expressions`     | `String[]` | [物品模式](./ItemPattern)表达式列表 | `[]`   |
| `use_to_first_person_pattern_expressions` | `String[]` | [物品模式](./ItemPattern)表达式列表 | `[]`   |
