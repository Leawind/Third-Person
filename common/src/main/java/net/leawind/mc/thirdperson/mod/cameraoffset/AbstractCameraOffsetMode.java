package net.leawind.mc.thirdperson.mod.cameraoffset;


import net.leawind.mc.thirdperson.interfaces.cameraoffset.CameraOffsetMode;
import net.leawind.mc.thirdperson.interfaces.config.Config;
import net.leawind.mc.util.math.vector.api.Vector2d;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCameraOffsetMode implements CameraOffsetMode {
	protected final @NotNull Config config;

	public AbstractCameraOffsetMode (@NotNull Config config) {
		this.config = config;
	}

	@Override
	public void setSide (boolean isCameraLeftOfPlayer) {
		if (isCameraLeftOfPlayer ^ isCameraLeftOfPlayer()) {
			toNextSide();
			setCentered(false);
		}
	}

	@Override
	public void getOffsetRatio (@NotNull Vector2d v) {
		if (isCentered()) {
			v.set(0, getCenterOffsetRatio());
		} else {
			getSideOffsetRatio(v);
		}
	}
}
