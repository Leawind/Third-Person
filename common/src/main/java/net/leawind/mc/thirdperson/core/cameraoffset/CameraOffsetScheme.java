package net.leawind.mc.thirdperson.core.cameraoffset;


import net.leawind.mc.thirdperson.config.Config;
import org.jetbrains.annotations.NotNull;

/**
 * 第三人称相机的偏移方案
 * <p>
 * 第三人称下，相机会根据其当前所处的模式来确定相机的行为。例如如何跟随玩家、如何旋转、与玩家的相对位置如何确定等。
 * <p>
 * 默认有两种模式，按F5在第一人称和两种模式间切换
 */
public class CameraOffsetScheme {
	private @NotNull Config           config;
	public           boolean          isAiming = false;
	public           CameraOffsetMode normalMode;
	public           CameraOffsetMode aimingMode;

	public CameraOffsetScheme (@NotNull Config config) {
		this.config = config;
		normalMode  = new CameraOffsetModeNormal(config);
		aimingMode  = new CameraOffsetModeAiming(config);
	}

	/**
	 * 获取当前模式
	 */
	public CameraOffsetMode getMode () {
		return isAiming ? aimingMode: normalMode;
	}

	/**
	 * 获取当前未启用的模式
	 */
	public CameraOffsetMode getAnotherMode () {
		return isAiming ? normalMode: aimingMode;
	}

	/**
	 * 设置相机相对于玩家的方向
	 * <p>
	 *
	 * @param side 大于0表示相机在玩家左侧
	 */
	public void setSide (double side) {
		setSide(side > 0);
	}

	/**
	 * 设置相机相对于玩家的方向
	 *
	 * @param isCameraLeftOfPlayer 相机是否在玩家左侧
	 */
	public void setSide (boolean isCameraLeftOfPlayer) {
		aimingMode.setSide(isCameraLeftOfPlayer);
		normalMode.setSide(isCameraLeftOfPlayer);
	}

	/**
	 * 切换到另一边
	 */
	public void toNextSide () {
		aimingMode.toNextSide();
		normalMode.toNextSide();
	}

	public boolean isCentered () {
		return getMode().isCentered();
	}

	public void setCentered (boolean isCentered) {
		getMode().setCentered(isCentered);
		getAnotherMode().setCentered(isCentered);//TODO add config: is seperated centered or not
	}

	public void setIsAiming (boolean isAiming) {
		this.isAiming = isAiming;
	}
}
