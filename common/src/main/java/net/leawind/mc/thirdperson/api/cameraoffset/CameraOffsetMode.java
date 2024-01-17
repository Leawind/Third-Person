package net.leawind.mc.thirdperson.api.cameraoffset;


import net.leawind.mc.math.vector.Vector2d;
import net.leawind.mc.math.vector.Vector3d;
import org.jetbrains.annotations.NotNull;

public interface CameraOffsetMode {
	void getEyeSmoothFactor (@NotNull Vector3d v);

	double getDistanceSmoothFactor ();

	void getOffsetSmoothFactor (@NotNull Vector2d v);

	double getMaxDistance ();

	void setMaxDistance (double distance);

	boolean isCentered ();

	/**
	 * 设置是否居中
	 */
	void setCentered (boolean isCentered);

	boolean isCameraLeftOfPlayer ();

	/**
	 * 设置相机在玩家的左边还是右边
	 */
	void setSide (boolean isCameraLeftOfPlayer);

	/**
	 * 切换到另一边，如果当前居中，则退出居中
	 */
	void toNextSide ();

	void getOffsetRatio (@NotNull Vector2d v);

	void setSideOffsetRatio (@NotNull Vector2d v);

	double getCenterOffsetRatio ();

	@NotNull Vector2d getSideOffsetRatio ();

	void getSideOffsetRatio (@NotNull Vector2d v);

	void setCenterOffsetRatio (double offset);
}
