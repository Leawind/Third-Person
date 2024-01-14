package net.leawind.mc.thirdperson.core.cameraoffset;


import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.math.vector.Vector2d;
import net.leawind.mc.math.vector.Vector3d;
import org.jetbrains.annotations.NotNull;

public abstract class CameraOffsetMode {
	public @NotNull Config config;

	public CameraOffsetMode (@NotNull Config config) {
		this.config = config;
	}

	abstract public void getEyeSmoothFactor (@NotNull Vector3d v);

	abstract public double getDistanceSmoothFactor ();

	abstract public void getOffsetSmoothFactor (@NotNull Vector2d v);

	abstract public double getMaxDistance ();

	abstract public void setMaxDistance (double distance);

	abstract public boolean isCentered ();

	/**
	 * 设置是否居中
	 */
	abstract public void setCentered (boolean isCentered);

	abstract public boolean isCameraLeftOfPlayer ();

	/**
	 * 设置相机在玩家的左边还是右边
	 */
	abstract public void setSide (boolean isCameraLeftOfPlayer);

	/**
	 * 切换到另一边，如果当前居中，则退出居中
	 */
	abstract public void toNextSide ();

	abstract public void getOffsetRatio (@NotNull Vector2d v);

	abstract public void setSideOffsetRatio (Vector2d v);

	abstract public double getCenterOffsetRatio ();

	abstract public Vector2d getSideOffsetRatio ();

	abstract public void getSideOffsetRatio (@NotNull Vector2d v);

	abstract public void setCenterOffsetRatio (double offset);
}
