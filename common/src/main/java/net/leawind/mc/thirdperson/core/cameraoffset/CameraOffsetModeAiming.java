package net.leawind.mc.thirdperson.core.cameraoffset;


import net.leawind.mc.math.vector.Vector2d;
import net.leawind.mc.math.vector.Vector3d;
import net.leawind.mc.thirdperson.config.Config;
import org.jetbrains.annotations.NotNull;

public class CameraOffsetModeAiming extends CameraOffsetMode {
	public CameraOffsetModeAiming (@NotNull Config config) {
		super(config);
	}

	@Override
	public void getEyeSmoothFactor (@NotNull Vector3d v) {
		v.set(config.aiming_smooth_factor_horizon, config.aiming_smooth_factor_vertical, config.aiming_smooth_factor_horizon);
	}

	@Override
	public double getDistanceSmoothFactor () {
		return config.aiming_distance_smooth_factor;
	}

	@Override
	public void getOffsetSmoothFactor (@NotNull Vector2d v) {
		v.set(config.aiming_camera_offset_smooth_factor);
	}

	@Override
	public double getMaxDistance () {
		return config.aiming_max_distance;
	}

	@Override
	public void setMaxDistance (double distance) {
		config.aiming_max_distance = distance;
	}

	@Override
	public boolean isCentered () {
		return config.aiming_is_centered;
	}

	public void setCentered (boolean isCentered) {
		config.aiming_is_centered = isCentered;
	}

	public boolean isCameraLeftOfPlayer () {
		return config.aiming_offset_x > 0;
	}

	@Override
	public void setSide (boolean isCameraLeftOfPlayer) {
		if (isCameraLeftOfPlayer ^ isCameraLeftOfPlayer()) {
			toNextSide();
			setCentered(false);
		}
	}

	@Override
	public void toNextSide () {
		if (isCentered()) {
			setCentered(false);
		} else {
			config.aiming_offset_x = -config.aiming_offset_x;
		}
	}

	@Override
	public void getOffsetRatio (@NotNull Vector2d v) {
		if (isCentered()) {
			v.set(0, getCenterOffsetRatio());
		} else {
			getSideOffsetRatio(v);
		}
	}

	@Override
	public void setSideOffsetRatio (@NotNull Vector2d v) {
		config.aiming_offset_x = v.x;
		config.aiming_offset_y = v.y;
	}

	@Override
	public double getCenterOffsetRatio () {
		return config.aiming_offset_center;
	}

	@Override
	public @NotNull Vector2d getSideOffsetRatio () {
		return new Vector2d(config.aiming_offset_x, config.aiming_offset_y);
	}

	@Override
	public void getSideOffsetRatio (@NotNull Vector2d v) {
		v.set(config.aiming_offset_x, config.aiming_offset_y);
	}

	@Override
	public void setCenterOffsetRatio (double offset) {
		config.aiming_offset_center = offset;
	}
}
