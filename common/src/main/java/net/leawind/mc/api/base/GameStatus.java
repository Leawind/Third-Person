package net.leawind.mc.api.base;


public final class GameStatus {
	/**
	 * 第三人称下是否强制显示准星
	 * <p>
	 * false 表示按照原版（不显示）
	 * <p>
	 * true 表示显示准星
	 */
	public static boolean forceThirdPersonCrosshair = false;
	public static boolean isPerspectiveInverted     = false;
	public static double  sprintImpulseThreshold    = -1;
}
