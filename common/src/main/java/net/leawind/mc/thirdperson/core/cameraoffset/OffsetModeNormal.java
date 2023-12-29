package net.leawind.mc.thirdperson.core.cameraoffset;


import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class OffsetModeNormal extends CameraOffsetMode {
	public OffsetModeNormal (CameraOffsetScheme scheme, double maxDist, float x, float y) {
		super(scheme);
		setEyeSmoothFactor(new Vec3(8e-4, 1e-2, 8e-4));
		setDistanceSmoothFactor(1e-3);
		setOffsetSmoothFactor(new Vec2(4e-5f, 4e-5f));
		setMaxDistance(maxDist);
		setOffsetRatio(new Vec2(x, y));
		setCenterOffsetRatio(0.2);
	}
}
