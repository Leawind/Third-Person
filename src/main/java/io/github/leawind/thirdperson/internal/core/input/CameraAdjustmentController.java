package io.github.leawind.thirdperson.internal.core.input;

import io.github.leawind.thirdperson.internal.core.config.ConfigValidation;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.util.Objects;
import java.util.Optional;

/// Applies bounded mouse and wheel adjustments to one camera profile.
public final class CameraAdjustmentController {
  private static final double OFFSET_INPUT_SCALE = 0.0025;
  private static final double DISTANCE_SCROLL_FACTOR = 1.25;

  private ThirdPersonConfig.CameraProfile profile;
  private boolean adjusting;
  private boolean changed;

  public boolean isAdjusting() {
    return adjusting;
  }

  public void begin(ThirdPersonConfig.CameraProfile profile) {
    if (adjusting) {
      return;
    }
    this.profile = Objects.requireNonNull(profile, "profile");
    adjusting = true;
    changed = false;
  }

  public Optional<ThirdPersonConfig.CameraProfile> turn(double rawYaw, double rawPitch) {
    if (!adjusting || !Double.isFinite(rawYaw) || !Double.isFinite(rawPitch)) {
      return Optional.empty();
    }
    return replace(
        profile.distance(),
        profile.offsetX() - rawYaw * OFFSET_INPUT_SCALE,
        profile.offsetY() + rawPitch * OFFSET_INPUT_SCALE);
  }

  public Optional<ThirdPersonConfig.CameraProfile> scroll(double yOffset) {
    if (!adjusting || !Double.isFinite(yOffset) || yOffset == 0.0) {
      return Optional.empty();
    }
    double factor = Math.pow(DISTANCE_SCROLL_FACTOR, Math.abs(yOffset));
    double distance;
    if (!Double.isFinite(factor)) {
      distance = yOffset > 0.0 ? 0.0 : 16.0;
    } else {
      distance =
          yOffset > 0.0 ? profile.distance() / factor : profile.distance() * factor;
    }
    return replace(
        distance,
        profile.offsetX(),
        profile.offsetY());
  }

  public Optional<ThirdPersonConfig.CameraProfile> finish() {
    if (!adjusting) {
      return Optional.empty();
    }
    adjusting = false;
    return changed ? Optional.of(profile) : Optional.empty();
  }

  public void reset() {
    adjusting = false;
    changed = false;
    profile = null;
  }

  private Optional<ThirdPersonConfig.CameraProfile> replace(
      double distance, double offsetX, double offsetY) {
    var next =
        new ThirdPersonConfig.CameraProfile(
            ConfigValidation.finiteClamped(distance, 0.0, 16.0, profile.distance()),
            ConfigValidation.finiteClamped(offsetX, -1.0, 1.0, profile.offsetX()),
            ConfigValidation.finiteClamped(offsetY, -1.0, 1.0, profile.offsetY()),
            profile.fovMultiplier());
    if (next.equals(profile)) {
      return Optional.empty();
    }
    profile = next;
    changed = true;
    return Optional.of(profile);
  }
}
