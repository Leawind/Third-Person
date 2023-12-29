package net.leawind.mc.thirdperson.core.cameraoffset;


import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class OffsetModeNormal extends CameraOffsetMode {
	/**
	 * (x,y) 表示玩家眼睛在屏幕上的位置
	 *
	 * @param x 范围 [-1, 1]
	 * @param y 范围 [-1, 1]
	 */
	public OffsetModeNormal (CameraOffsetScheme scheme, double maxDist, float x, float y) {
		super(scheme);
		setEyeSmoothFactor(new Vec3(8e-4, 1e-2, 8e-4));
		setDistanceSmoothFactor(1e-3);
		setOffsetSmoothFactor(new Vec2(4e-5f, 4e-5f));
		setMaxDistance(maxDist);
		setOffsetValue(new Vec2(x, y));
		setMiddleOffsetValue(0.2);
	}

	@Override
	public Vec2 getOffsetRatio (double distance) {
		return getOffsetRatio();
	}

	public Vec2 getOffsetRatio () {
		return cameraOffsetScheme.isMiddle ? new Vec2(0, (float)getMiddleOffsetValue()): getOffsetValue();
	}
}
