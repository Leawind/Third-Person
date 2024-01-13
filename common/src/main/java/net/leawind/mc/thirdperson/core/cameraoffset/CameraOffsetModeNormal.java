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
	public void getEyeSmoothFactor (@NotNull Vector3d v) {
		v.set(config.normal_smooth_factor_horizon, config.normal_smooth_factor_vertical, config.normal_smooth_factor_horizon);
	}

	@Override
	public double getDistanceSmoothFactor () {
		return config.normal_distance_smooth_factor;
	}

	@Override
	public void getOffsetSmoothFactor (@NotNull Vector2d v) {
		v.set(config.normal_camera_offset_smooth_factor);
	}

	@Override
	public double getMaxDistance () {
		return config.normal_max_distance;
	}

	@Override
	public void setMaxDistance (double distance) {
		config.normal_max_distance = distance;
	}

	@Override
	public boolean isCentered () {
		return config.normal_is_centered;
	}

	public void setCentered (boolean isCentered) {
		config.normal_is_centered = isCentered;
	}

	public boolean isCameraLeftOfPlayer () {
		return config.normal_offset_x > 0;
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
		}
		config.normal_offset_x = -config.normal_offset_x;
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
	public void setSideOffsetRatio (Vector2d v) {
		config.normal_offset_x = v.x;
		config.normal_offset_y = v.y;
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
	public void getSideOffsetRatio (@NotNull Vector2d v) {
		v.set(config.normal_offset_x, config.normal_offset_y);
	}

	@Override
	public void setCenterOffsetRatio (double offset) {
		config.normal_offset_center = offset;
	}
}
