package net.leawind.mc.thirdperson.mod.cameraoffset;


import net.leawind.mc.thirdperson.mod.config.Config;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.leawind.mc.util.math.vector.api.Vector3d;
import org.jetbrains.annotations.NotNull;

public class CameraOffsetModeNormal extends AbstractCameraOffsetMode {
	public CameraOffsetModeNormal (@NotNull Config config) {
		super(config);
	}

	@Override
	public @NotNull Vector3d getEyeSmoothHalflife () {
		return Vector3d.of(config.normal_smooth_halflife_horizon, config.normal_smooth_halflife_vertical, config.normal_smooth_halflife_horizon);
	}

	@Override
	public double getDistanceSmoothHalflife () {
		return config.normal_distance_smooth_halflife;
	}

	@Override
	public @NotNull Vector2d getOffsetSmoothHalflife () {
		return Vector2d.of(config.normal_camera_offset_smooth_halflife);
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

	@Override
	public void setCentered (boolean isCentered) {
		config.normal_is_centered = isCentered;
	}

	@Override
	public boolean isCameraLeftOfPlayer () {
		return config.normal_offset_x > 0;
	}

	@Override
	public void toNextSide () {
		if (isCentered()) {
			setCentered(false);
		} else {
			config.normal_offset_x = -config.normal_offset_x;
		}
	}

	@Override
	public void setSideOffsetRatio (@NotNull Vector2d v) {
		config.normal_offset_x = v.x();
		config.normal_offset_y = v.y();
	}

	@Override
	public double getCenterOffsetRatio () {
		return config.normal_offset_center;
	}

	@Override
	public void setCenterOffsetRatio (double offset) {
		config.normal_offset_center = offset;
	}

	@Override
	public @NotNull Vector2d getSideOffsetRatio (@NotNull Vector2d v) {
		return v.set(config.normal_offset_x, config.normal_offset_y);
	}
}
