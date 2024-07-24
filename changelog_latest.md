### Features

* Update fabric loader version: `0.15.7` --> `0.15.11`.
* Update fabric api version: `0.92.0` --> `0.92.2`.
* Update forge version: `47.2.20` --> `47.3.0`.
* Add config: `gaze_opacity`.
* Change config screen categories.

### Bug fix

* Crash when config file is broken. #128
* Player head rotate unexpectedly with mouse. #106
* Camera toggle to another side when leaving center position. #120

### Others

* Use Forge API to set camera position and rotation.
* Use architectury api to register config screen.
* Add optional dependency YACL to mods.toml (forge). Supported version is `(,3.2.2+1.20]`.
* Use architectury api to check if mod exist.
* Ignore dir `.vs/`.
