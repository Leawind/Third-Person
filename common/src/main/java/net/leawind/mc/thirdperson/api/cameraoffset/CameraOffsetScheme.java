package net.leawind.mc.thirdperson.api.cameraoffset;


import org.jetbrains.annotations.NotNull;

public interface CameraOffsetScheme {
	/**
	 * 获取当前模式
	 */
	@NotNull CameraOffsetMode getMode ();

	/**
	 * 获取当前未启用的模式
	 */
	@NotNull CameraOffsetMode getAnotherMode ();

	/**
	 * 设置相机相对于玩家的方向
	 * <p>
	 *
	 * @param side 大于0表示相机在玩家左侧
	 */
	void setSide (double side);

	/**
	 * 设置相机相对于玩家的方向
	 *
	 * @param isCameraLeftOfPlayer 相机是否在玩家左侧
	 */
	void setSide (boolean isCameraLeftOfPlayer);

	/**
	 * 切换到另一边
	 */
	void toNextSide ();

	/**
	 * 当前是否居中
	 */
	boolean isCentered ();

	/**
	 * 设置当前是否居中
	 */
	void setCentered (boolean isCentered);

	/**
	 * 设置当前是否处于瞄准模式
	 */
	boolean isAiming ();

	/**
	 * 当前是否处于瞄准模式
	 */
	void setAiming (boolean aiming);
}
