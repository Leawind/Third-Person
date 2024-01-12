package net.leawind.mc.thirdperson.core.cameraoffset;


import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.util.vector.Vector2d;
import net.leawind.mc.util.vector.Vector3d;
import org.jetbrains.annotations.NotNull;

public class CameraOffsetModeAiming extends CameraOffsetMode {
	public CameraOffsetModeAiming (@NotNull Config config) {
		super(config);
	}

	@Override
	public Vector3d getEyeSmoothFactor () {
		return new Vector3d(config.aiming_smooth_factor_horizon, config.aiming_smooth_factor_vertical, config.aiming_smooth_factor_horizon);
	}

	@Override
	public CameraOffsetMode setEyeSmoothFactor (double horizon, double vertical) {
		config.aiming_smooth_factor_horizon  = horizon;
		config.aiming_smooth_factor_vertical = vertical;
		return this;
	}

	@Override
	public double getDistanceSmoothFactor () {
		return config.aiming_distance_smooth_factor;
	}

	@Override
	public CameraOffsetMode setDistanceSmoothFactor (double smoothFactor) {
		config.aiming_distance_smooth_factor = smoothFactor;
		return this;
	}

	@Override
	public Vector2d getOffsetSmoothFactor () {
		return new Vector2d(config.aiming_camera_offset_smooth_factor);
	}

	@Override
	public CameraOffsetMode setOffsetSmoothFactor (double smoothFactor) {
		config.aiming_camera_offset_smooth_factor = smoothFactor;
		return this;
	}

	@Override
	public double getMaxDistance () {
		return config.aiming_max_distance;
	}

	@Override
	public CameraOffsetMode setMaxDistance (double distance) {
		config.aiming_max_distance = distance;
		return this;
	}

	@Override
	public boolean isCentered () {
		return config.aiming_is_centered;
	}

	public CameraOffsetMode setCentered (boolean isCentered) {
		config.aiming_is_centered = isCentered;
		return this;
	}

	public boolean isCameraLeftOfPlayer () {
		return config.aiming_offset_x > 0;
	}

	@Override
	public CameraOffsetMode setSide (boolean isCameraLeftOfPlayer) {
		if (isCameraLeftOfPlayer ^ isCameraLeftOfPlayer()) {
			toNextSide();
			setCentered(false);
		}
		return this;
	}

	@Override
	public void toNextSide () {
		if (isCentered()) {
			setCentered(false);
		}
		config.aiming_offset_x = -config.aiming_offset_x;
	}

	@Override
	public Vector2d getOffsetRatio () {
		return isCentered() ? new Vector2d(0, getCenterOffsetRatio()): getSideOffsetRatio();
	}

	@Override
	public CameraOffsetMode setSideOffsetRatio (double x, double y) {
		config.aiming_offset_x = x;
		config.aiming_offset_y = y;
		return this;
	}

	@Override
	public double getCenterOffsetRatio () {
		return config.aiming_offset_center;
	}

	@Override
	public Vector2d getSideOffsetRatio () {
		return new Vector2d(config.aiming_offset_x, config.aiming_offset_y);
	}

	@Override
	public CameraOffsetMode setCenterOffsetRatio (double offset) {
		config.aiming_offset_center = offset;
		return this;
	}
}
