package net.leawind.mc.thirdperson.core.cameraoffset;


import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.util.vector.Vector2d;
import net.leawind.mc.util.vector.Vector3d;
import org.jetbrains.annotations.NotNull;

public abstract class CameraOffsetMode {
	public @NotNull Config config;

	public CameraOffsetMode (@NotNull Config config) {
		this.config = config;
	}

	abstract public Vector3d getEyeSmoothFactor ();

	abstract public CameraOffsetMode setEyeSmoothFactor (double horizon, double vertical);

	abstract public double getDistanceSmoothFactor ();

	abstract public CameraOffsetMode setDistanceSmoothFactor (double smoothFactor);

	abstract public Vector2d getOffsetSmoothFactor ();

	abstract public CameraOffsetMode setOffsetSmoothFactor (double d);

	abstract public double getMaxDistance ();

	abstract public CameraOffsetMode setMaxDistance (double distance);

	abstract public boolean isCentered ();

	abstract public CameraOffsetMode setCentered (boolean isCentered);

	abstract public boolean isCameraLeftOfPlayer ();

	abstract public CameraOffsetMode setSide (boolean isLeft);

	abstract public void toNextSide ();

	abstract public Vector2d getOffsetRatio ();

	abstract public CameraOffsetMode setSideOffsetRatio (double x, double y);

	abstract public double getCenterOffsetRatio ();

	abstract public Vector2d getSideOffsetRatio ();

	abstract public CameraOffsetMode setCenterOffsetRatio (double offset);
}
