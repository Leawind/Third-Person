package net.leawind.mc.thirdperson.core.cameraoffset;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class OffsetModeAiming extends CameraOffsetMode {
	public OffsetModeAiming (CameraOffsetProfile profile, double maxDist, float x, float y) {
		this(profile, maxDist, new Vec2(x, y));
	}

	public OffsetModeAiming (CameraOffsetProfile profile, double maxDist, Vec2 offset) {
		super(profile);
		setEyeSmoothFactor(new Vec3(1e-11, 1e-8, 1e-11));
		setDistanceSmoothFactor(1e-5);
		setOffsetSmoothFactor(new Vec2(2e-8f, 2e-8f));
		setMaxDistance(maxDist);
		setOffsetValue(offset);
		setTopOffsetValue(0.6);
	}

	@Override
	public Vec2 getOffsetRatio (double distance) {
		return cameraOffsetProfile.isTop ? new Vec2(0, (float)Math.atan2(topOffsetValue, distance)):
			   new Vec2((float)Math.atan2(
			offsetValue.x,
			distance), (float)Math.atan2(offsetValue.y, distance));
	}
}
