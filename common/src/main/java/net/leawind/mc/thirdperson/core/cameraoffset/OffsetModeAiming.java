package net.leawind.mc.thirdperson.core.cameraoffset;


import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class OffsetModeAiming extends CameraOffsetMode {
	public OffsetModeAiming (CameraOffsetScheme scheme, double maxDist, float x, float y) {
		this(scheme, maxDist, new Vec2(x, y));
	}

	public OffsetModeAiming (CameraOffsetScheme scheme, double maxDist, Vec2 offset) {
		super(scheme);
		setEyeSmoothFactor(new Vec3(1e-11, 1e-8, 1e-11));
		setDistanceSmoothFactor(1e-5);
		setOffsetSmoothFactor(new Vec2(2e-8f, 2e-8f));
		setMaxDistance(maxDist);
		setOffsetValue(offset);
		setMiddleOffsetValue(0.6);
	}

	@Override
	public Vec2 getOffsetRatio (double distance) {
		return cameraOffsetScheme.isMiddle
			   ? new Vec2(0, (float)Math.atan2(getMaxDistance(), distance))
			   : new Vec2((float)Math.atan2(getOffsetValue().x, distance), (float)Math.atan2(getOffsetValue().y, distance));
	}
}
