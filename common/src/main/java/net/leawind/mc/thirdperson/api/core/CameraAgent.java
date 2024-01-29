package net.leawind.mc.thirdperson.api.core;


import net.leawind.mc.thirdperson.impl.core.CameraAgentImpl;
import net.leawind.mc.util.api.math.vector.Vector2d;
import net.leawind.mc.util.api.math.vector.Vector3d;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CameraAgent {
	static CameraAgent create (Minecraft mc) {
		return new CameraAgentImpl(mc);
	}

	/**
	 * 重置各种属性
	 */
	void reset ();

	/**
	 * 渲染前
	 */
	void onPreRender (double period, float partialTick);

	/**
	 * 渲染时放置相机
	 */
	void onCameraSetup (double period);

	/**
	 * client tick 前
	 * <p>
	 * 通常频率固定为 20Hz
	 */
	void onClientTickPre ();

	@NotNull Camera getRawCamera ();

	void setLevel (BlockGetter level);

	boolean wasCameraCloseToEntity ();

	@NotNull Vector2d calculateRotation ();

	Vector3d getPickPosition ();

	HitResult pick ();

	Camera getFakeCamera ();

	void onCameraTurn (double dy, double dx);

	@NotNull Vector2d getRelativeRotation ();

	/**
	 * 获取相机视线落点坐标
	 *
	 * @param pickRange 最大探测距离
	 */
	@Nullable Vector3d getPickPosition (double pickRange);

	@NotNull HitResult pick (double pickRange);

	@Nullable EntityHitResult pickEntity (double pickRange);

	/**
	 * pick 方块
	 * <p>
	 * 瞄准时忽略草
	 */
	@NotNull BlockHitResult pickBlock (double pickRange);
}
