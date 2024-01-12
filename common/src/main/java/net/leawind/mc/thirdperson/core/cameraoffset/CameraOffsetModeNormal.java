package net.leawind.mc.thirdperson.core.cameraoffset;


import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.util.vector.Vector2d;
import net.leawind.mc.util.vector.Vector3d;
import org.jetbrains.annotations.NotNull;

public class CameraOffsetModeNormal extends CameraOffsetMode {
	public CameraOffsetModeNormal (@NotNull Config config) {
		super(config);
	}

	@Override
	public Vector3d getEyeSmoothFactor () {
		return new Vector3d(config.normal_smooth_factor_horizon, config.normal_smooth_factor_vertical, config.normal_smooth_factor_horizon);
	}

	@Override
	public CameraOffsetMode setEyeSmoothFactor (double horizon, double vertical) {
		config.normal_smooth_factor_horizon  = horizon;
		config.normal_smooth_factor_vertical = vertical;
		return this;
	}

	@Override
	public double getDistanceSmoothFactor () {
		return config.normal_distance_smooth_factor;
	}

	@Override
	public CameraOffsetMode setDistanceSmoothFactor (double smoothFactor) {
		config.normal_distance_smooth_factor = smoothFactor;
		return this;
	}

	@Override
	public Vector2d getOffsetSmoothFactor () {
		return new Vector2d(config.normal_camera_offset_smooth_factor);
	}

	@Override
	public CameraOffsetMode setOffsetSmoothFactor (double smoothFactor) {
		config.normal_camera_offset_smooth_factor = smoothFactor;
		return this;
	}

	@Override
	public double getMaxDistance () {
		return config.normal_max_distance;
	}

	@Override
	public CameraOffsetMode setMaxDistance (double distance) {
		config.normal_max_distance = distance;
		return this;
	}

	@Override
	public boolean isCentered () {
		return config.normal_is_centered;
	}

	public CameraOffsetMode setCentered (boolean isCentered) {
		config.normal_is_centered = isCentered;
		return this;
	}

	public boolean isCameraLeftOfPlayer () {
		return config.normal_offset_x > 0;
	}

	@Override
	public CameraOffsetMode setSide (boolean isLeft) {
		if (isLeft ^ isCameraLeftOfPlayer()) {
			toNextSide();
			setCentered(false);
		}
		return this;
	}

	@Override
	public void toNextSide () {
		config.normal_offset_x = -config.normal_offset_x;
	}

	@Override
	public Vector2d getOffsetRatio () {
		return isCentered() ? new Vector2d(0, getCenterOffsetRatio()): getSideOffsetRatio();
	}

	@Override
	public CameraOffsetMode setSideOffsetRatio (double x, double y) {
		config.normal_offset_x = x;
		config.normal_offset_y = y;
		return this;
	}

	@Override
	public double getCenterOffsetRatio () {
		return config.normal_offset_center;
	}

	@Override
	public Vector2d getSideOffsetRatio () {
		return new Vector2d(config.normal_offset_x, config.normal_offset_y);
	}

	@Override
	public CameraOffsetMode setCenterOffsetRatio (double offset) {
		config.normal_offset_center = offset;
		return this;
	}
}
