package io.github.leawind.thirdperson.internal.core.schedule.camera;

import io.github.leawind.thirdperson.internal.core.base.camera.CameraProfile;
import java.util.Objects;
import java.util.function.UnaryOperator;

/// Owns the camera values that survive client restarts.
public final class CameraSettings {
  private static final CameraProfile DEFAULT_NORMAL_PROFILE =
      new CameraProfile(1.1, -0.2, -0.25, -0.254, 1.0, false);
  private static final CameraProfile DEFAULT_AIMING_PROFILE =
      new CameraProfile(0.448, -0.3, -0.41, -0.43, 0.9, false);
  private static final CameraSmoothing DEFAULT_SMOOTHING =
      new CameraSmoothing(
          0.0,
          0.07,
          0.05,
          0.05,
          new ModeSmoothing(0.064, 0.04, 0.08, 0.08),
          new ModeSmoothing(0.02, 0.025, 0.025, 0.036));

  private CameraProfile normalProfile = DEFAULT_NORMAL_PROFILE;
  private CameraProfile aimingProfile = DEFAULT_AIMING_PROFILE;
  private CameraSmoothing smoothing = DEFAULT_SMOOTHING;

  public CameraProfile normalProfile() {
    return normalProfile;
  }

  public CameraProfile aimingProfile() {
    return aimingProfile;
  }

  public CameraProfile profile(CameraProfileSlot slot) {
    return switch (Objects.requireNonNull(slot, "slot")) {
      case NORMAL -> normalProfile;
      case AIMING -> aimingProfile;
    };
  }

  public void setProfile(CameraProfileSlot slot, CameraProfile profile) {
    Objects.requireNonNull(slot, "slot");
    Objects.requireNonNull(profile, "profile");
    switch (slot) {
      case NORMAL -> normalProfile = profile;
      case AIMING -> aimingProfile = profile;
    }
  }

  public void updateProfile(CameraProfileSlot slot, UnaryOperator<CameraProfile> update) {
    Objects.requireNonNull(update, "update");
    setProfile(slot, Objects.requireNonNull(update.apply(profile(slot)), "updated profile"));
  }

  public CameraSmoothing smoothing() {
    return smoothing;
  }

  public ModeSmoothing smoothingFor(CameraProfileSlot slot) {
    return switch (Objects.requireNonNull(slot, "slot")) {
      case NORMAL -> smoothing.normal();
      case AIMING -> smoothing.aiming();
    };
  }

  public void setSmoothing(CameraSmoothing smoothing) {
    this.smoothing = Objects.requireNonNull(smoothing, "smoothing");
  }

  public void updateSmoothing(UnaryOperator<CameraSmoothing> update) {
    Objects.requireNonNull(update, "update");
    setSmoothing(Objects.requireNonNull(update.apply(smoothing), "updated smoothing"));
  }

  public void updateModeSmoothing(CameraProfileSlot slot, UnaryOperator<ModeSmoothing> update) {
    Objects.requireNonNull(slot, "slot");
    Objects.requireNonNull(update, "update");
    updateSmoothing(
        current ->
            new CameraSmoothing(
                current.rotationHalfLife(),
                current.flyingPivotPositionHalfLife(),
                current.adjustingOffsetHalfLife(),
                current.adjustingDistanceHalfLife(),
                slot == CameraProfileSlot.NORMAL
                    ? Objects.requireNonNull(update.apply(current.normal()), "updated smoothing")
                    : current.normal(),
                slot == CameraProfileSlot.AIMING
                    ? Objects.requireNonNull(update.apply(current.aiming()), "updated smoothing")
                    : current.aiming()));
  }

  public void restore(
      CameraProfile normalProfile, CameraProfile aimingProfile, CameraSmoothing smoothing) {
    this.normalProfile = Objects.requireNonNull(normalProfile, "normalProfile");
    this.aimingProfile = Objects.requireNonNull(aimingProfile, "aimingProfile");
    this.smoothing = Objects.requireNonNull(smoothing, "smoothing");
  }

  public static CameraProfile defaultNormalProfile() {
    return DEFAULT_NORMAL_PROFILE;
  }

  public static CameraProfile defaultAimingProfile() {
    return DEFAULT_AIMING_PROFILE;
  }

  public static CameraSmoothing defaultSmoothing() {
    return DEFAULT_SMOOTHING;
  }
}
