package net.leawind.mc.thirdpersonperspective.core;


import net.minecraft.world.phys.Vec2;

/**
 * 第三人称相机的偏移模式
 * <p>
 * 第三人称下，相机会根据其当前所处的模式来确定相机的行为。例如如何跟随玩家、如何旋转、与玩家的相对位置如何确定等。
 */
public class CameraOffsetProfile {
	/**
	 * 到平滑眼睛的最大距离
	 */
	public double     maxDistanceToSmoothEye;
	/**
	 * 该模式下相机的偏移量
	 */
	public Vec2       offset;
	public OffsetType offsetType = OffsetType.SCREEN_RELATIVE;

	public CameraOffsetProfile () {
	}

	/**
	 * 偏移类型
	 * <p>
	 * 在不同的偏移类型下，位置的计算方式不同
	 * <p>
	 * 1. SCREEN_RELATIVE 将平滑眼睛保持在屏幕中的特定位置
	 * <p>
	 * 2. STATIC_DISTANCE 保持平滑眼睛与相机视线间的距离不变
	 */
	public enum OffsetType {
		SCREEN_RELATIVE(),
		STATIC_DISTANCE();

		OffsetType () {
		}
	}

	// TODO
	public static final CameraOffsetProfile DEFAULT_CLOSER  = new CameraOffsetProfile();
	public static final CameraOffsetProfile DEFAULT_FARTHER = new CameraOffsetProfile();
}


