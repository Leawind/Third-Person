package io.github.leawind.thirdperson.internal.logic.scheduler;

import io.github.leawind.thirdperson.internal.logic.base.CameraProfile;
import java.util.Objects;
import java.util.Optional;

/// Applies bounded mouse and wheel adjustments to one camera profile.
public final class CameraAdjustmentController {
  private static final double OFFSET_INPUT_SCALE = 0.0025;
  private static final double DISTANCE_SCROLL_FACTOR = 1.25;

  private CameraProfile profile;
  private boolean adjusting;
  private boolean changed;

  public boolean isAdjusting() {
    return adjusting;
  }

  public void begin(CameraProfile profile) {
    if (adjusting) {
      return;
    }
    this.profile = Objects.requireNonNull(profile, "profile");
    adjusting = true;
    changed = false;
  }

  public Optional<CameraProfile> turn(double rawYaw, double rawPitch) {
    if (!adjusting || !Double.isFinite(rawYaw) || !Double.isFinite(rawPitch)) {
      return Optional.empty();
    }
    if (profile.centered()) {
      return replace(
          profile.distanceFactor(),
          profile.offsetX(),
          profile.offsetY(),
          profile.centeredOffsetY() + rawPitch * OFFSET_INPUT_SCALE);
    }
    return replace(
        profile.distanceFactor(),
        profile.offsetX() - rawYaw * OFFSET_INPUT_SCALE,
        profile.offsetY() + rawPitch * OFFSET_INPUT_SCALE,
        profile.centeredOffsetY());
  }

  public Optional<CameraProfile> scroll(double yOffset) {
    if (!adjusting || !Double.isFinite(yOffset) || yOffset == 0.0) {
      return Optional.empty();
    }
    double factor = Math.pow(DISTANCE_SCROLL_FACTOR, Math.abs(yOffset));
    double distanceFactor;
    if (!Double.isFinite(factor)) {
      distanceFactor = yOffset > 0.0 ? 0.0 : 16.0;
    } else {
      distanceFactor =
          yOffset > 0.0
              ? profile.distanceFactor() / factor
              : profile.distanceFactor() * factor;
    }
    return replace(
        distanceFactor,
        profile.offsetX(),
        profile.offsetY(),
        profile.centeredOffsetY());
  }

  public Optional<CameraProfile> finish() {
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

  private Optional<CameraProfile> replace(
      double distanceFactor, double offsetX, double offsetY, double centeredOffsetY) {
    var next =
        new CameraProfile(
            finiteClamped(distanceFactor, 0.0, 16.0, profile.distanceFactor()),
            finiteClamped(offsetX, -1.0, 1.0, profile.offsetX()),
            finiteClamped(offsetY, -1.0, 1.0, profile.offsetY()),
            finiteClamped(
                centeredOffsetY, -1.0, 1.0, profile.centeredOffsetY()),
            profile.fovMultiplier(),
            profile.centered());
    if (next.equals(profile)) {
      return Optional.empty();
    }
    profile = next;
    changed = true;
    return Optional.of(profile);
  }

  private static double finiteClamped(
      double value, double minimum, double maximum, double fallback) {
    if (!Double.isFinite(value)) {
      return fallback;
    }
    return Math.max(minimum, Math.min(maximum, value));
  }
}
