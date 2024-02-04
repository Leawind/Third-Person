package net.leawind.mc.thirdperson.api.cameraoffset;


import net.leawind.mc.util.math.vector.api.Vector2d;
import net.leawind.mc.util.math.vector.api.Vector3d;
import org.jetbrains.annotations.NotNull;

/**
 * 相机偏移模式
 * <p>
 * 描述相机应如何偏移
 * <p>
 * 相机偏移相关数据直接存储在配置对象中
 */
public interface CameraOffsetMode {
	/**
	 * 眼睛平滑系数
	 */
	void getEyeSmoothFactor (@NotNull Vector3d v);

	/**
	 * 距离平滑系数
	 */
	double getDistanceSmoothFactor ();

	/**
	 * 相机偏移平滑系数
	 */
	void getOffsetSmoothFactor (@NotNull Vector2d v);

	/**
	 * 相机到玩家的最大距离
	 */
	double getMaxDistance ();

	/**
	 * 设置相机到玩家的最大距离
	 */
	void setMaxDistance (double distance);

	/**
	 * 当前是否居中
	 */
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

	/**
	 * 获取偏移量
	 * <p>
	 * 根据当前是居中还是在两侧自动计算偏移量
	 */
	void getOffsetRatio (@NotNull Vector2d v);

	/**
	 * 设置当相机位于两侧，而非居中时的偏移量。
	 */
	void setSideOffsetRatio (@NotNull Vector2d v);

	/**
	 * 获取当相机居中时的，垂直偏移量
	 */
	double getCenterOffsetRatio ();

	/**
	 * 设置当相机居中时的，垂直偏移量
	 */
	void setCenterOffsetRatio (double offset);

	/**
	 * 获取当相机位于两侧，而非居中时的偏移量。
	 *
	 * @param v 将取得的数据存入该向量
	 * @return 与传入参数是同一个对象
	 */
	@NotNull Vector2d getSideOffsetRatio (@NotNull Vector2d v);
}
