package net.leawind.mc.thirdperson.impl.cameraoffset;


import net.leawind.mc.thirdperson.api.cameraoffset.CameraOffsetMode;
import net.leawind.mc.thirdperson.api.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.impl.config.Config;
import org.jetbrains.annotations.NotNull;

/**
 * 第三人称相机的偏移方案
 * <p>
 * 第三人称下，相机会根据其当前所处的模式来确定相机的行为。例如如何跟随玩家、如何旋转、与玩家的相对位置如何确定等。
 * <p>
 * 默认有两种模式，按F5在第一人称和两种模式间切换
 */
public class CameraOffsetSchemeImpl implements CameraOffsetScheme {
	private final @NotNull CameraOffsetMode normalMode;
	private final @NotNull CameraOffsetMode aimingMode;
	private                boolean          isAiming = false;

	public CameraOffsetSchemeImpl (@NotNull Config config) {
		normalMode = new CameraOffsetModeNormal(config);
		aimingMode = new CameraOffsetModeAiming(config);
	}

	@Override
	public @NotNull CameraOffsetMode getMode () {
		return isAiming() ? aimingMode: normalMode;
	}

	@Override
	public @NotNull CameraOffsetMode getAnotherMode () {
		return isAiming() ? normalMode: aimingMode;
	}

	@Override
	public void setSide (double side) {
		setSide(side > 0);
	}

	@Override
	public void setSide (boolean isCameraLeftOfPlayer) {
		aimingMode.setSide(isCameraLeftOfPlayer);
		normalMode.setSide(isCameraLeftOfPlayer);
	}

	@Override
	public void toNextSide () {
		aimingMode.toNextSide();
		normalMode.toNextSide();
	}

	@Override
	public boolean isCentered () {
		return getMode().isCentered();
	}

	@Override
	public void setCentered (boolean isCentered) {
		getMode().setCentered(isCentered);
		getAnotherMode().setCentered(isCentered);
	}

	@Override
	public boolean isAiming () {
		return isAiming;
	}

	@Override
	public void setAiming (boolean aiming) {
		isAiming = aiming;
	}
}
