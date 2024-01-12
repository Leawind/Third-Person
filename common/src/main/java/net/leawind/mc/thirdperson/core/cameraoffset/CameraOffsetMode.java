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

	/**
	 * 设置是否居中
	 */
	abstract public CameraOffsetMode setCentered (boolean isCentered);

	abstract public boolean isCameraLeftOfPlayer ();

	/**
	 * 设置相机在玩家的左边还是右边
	 */
	abstract public CameraOffsetMode setSide (boolean isCameraLeftOfPlayer);

	/**
	 * 切换到另一边，如果当前居中，则退出居中
	 */
	abstract public void toNextSide ();

	/**
	 * 根据当前是否居中自动计算偏移量
	 */
	abstract public Vector2d getOffsetRatio ();

	abstract public CameraOffsetMode setSideOffsetRatio (double x, double y);

	abstract public double getCenterOffsetRatio ();

	abstract public Vector2d getSideOffsetRatio ();

	abstract public CameraOffsetMode setCenterOffsetRatio (double offset);
}
