# Leawind's Third Person Documentation

## Build scripts

| name                | script                                  |     |
| ------------------- | --------------------------------------- | --- |
| `gen-mc-vers-table` | `node scripts/gen_mc_version_table.mjs` |     |
| `docs:clean`        | `rimraf docs/.vitepress/dist`           |     |
| `docs:build`        | `vitepress build docs`                  |     |
| ``                  | ``                                      |     |
| `docs:preview`      | `vitepress preview docs`                |     |
| `docs:dev`          | `vitepress dev docs`                    |     |
| `pretty`            | `prettier -w "./docs/**/*.md"`          |     |

| name            | script                             |     |
| --------------- | ---------------------------------- | --- |
| `build-all`     | `run-s docs:prebuild docs:rebuild` |     |
| `docs:prebuild` | `run-s gen-mc-vers-table`          |     |
| `docs:rebuild`  | `run-s docs:clean docs:build`          |     |

1. `build-all`
    1. `docs:prebuild`
        1. `gen-mc-vers-table`
    2. `docs:rebuild`
        1. `docs:clean`
        2. `docs:build`

## Other scripts

-   `docs:preview`
-   `docs:dev`
-   `pretty`
