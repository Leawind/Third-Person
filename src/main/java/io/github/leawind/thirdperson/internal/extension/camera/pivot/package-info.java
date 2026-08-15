/// Internal lifecycle and registration boundary for camera-pivot position strategies.
///
/// Providers may consume other internal bridge facades, including entity reference poses and
/// spatial queries. Registration is project-internal; dependency cycles are prevented by design
/// and package direction rather than a runtime recursion detector.
package io.github.leawind.thirdperson.internal.extension.camera.pivot;
