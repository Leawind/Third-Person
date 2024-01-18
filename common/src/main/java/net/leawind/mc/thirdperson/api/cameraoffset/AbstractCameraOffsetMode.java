package net.leawind.mc.thirdperson.api.cameraoffset;


import net.leawind.mc.util.math.vector.Vector2d;
import net.leawind.mc.thirdperson.impl.config.Config;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCameraOffsetMode implements CameraOffsetMode {
	public @NotNull Config config;

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
