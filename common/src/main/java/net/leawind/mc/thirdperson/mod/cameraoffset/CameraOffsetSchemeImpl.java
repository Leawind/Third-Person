package net.leawind.mc.thirdperson.mod.cameraoffset;


import net.leawind.mc.thirdperson.interfaces.cameraoffset.CameraOffsetMode;
import net.leawind.mc.thirdperson.interfaces.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.interfaces.config.Config;
import org.jetbrains.annotations.NotNull;

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
