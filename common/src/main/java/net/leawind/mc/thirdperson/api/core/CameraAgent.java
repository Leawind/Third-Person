package net.leawind.mc.thirdperson.api.core;


import net.leawind.mc.thirdperson.impl.core.CameraAgentImpl;
import net.leawind.mc.thirdperson.mixin.CameraMixin;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface CameraAgent {
	@Contract("_ -> new")
	static @NotNull CameraAgent create (@NotNull Minecraft mc) {
		return new CameraAgentImpl(mc);
	}

	/**
	 * 重置各种属性
	 */
	void reset ();

	void setLevel (@NotNull BlockGetter level);

	/**
	 * 渲染前
	 */
	void onRenderTickPre (double period, float partialTick);

	/**
	 * 渲染过程中放置相机
	 * <p>
	 * 在原版的渲染方法中，会调用{@link Camera#setup}来设置相机的位置和朝向。
	 * <p>
	 * 在第三人称下，咱需要覆盖该方法的行为，重新设置相机的位置和朝向。
	 * <p>
	 * {@link CameraMixin#setup_invoke}
	 */
	void onCameraSetup (double period);

	/**
	 * client tick 前
	 * <p>
	 * 通常频率固定为 20Hz
	 */
	void onClientTickPre ();

	/**
	 * 获取原版相机对象
	 */
	@NotNull Camera getRawCamera ();

	boolean wasCameraCloseToEntity ();

	/**
	 * 第三人称相机朝向
	 */
	@NotNull Vector2d getRotation ();

	/**
	 * 假相机
	 */
	@NotNull Camera getFakeCamera ();

	/**
	 * 玩家控制的相机旋转
	 *
	 * @param dy 方向角变化量
	 * @param dx 俯仰角变化量
	 */
	void onCameraTurn (double dy, double dx);

	/**
	 * 获取相对旋转角度
	 */
	@NotNull Vector2d getRelativeRotation ();

	/**
	 * 从相机pick，使用默认距离
	 * <p>
	 * 默认距离是相机到玩家的距离加上配置中相机的pick距离
	 * <p>
	 * 结果可能是实体或方块
	 */
	@NotNull HitResult pick ();

	/**
	 * 获取pick结果坐标
	 * <p>
	 * 使用默认距离
	 */
	@NotNull Optional<Vector3d> getPickPosition ();

	/**
	 * 获取相机视线落点坐标
	 *
	 * @param pickRange 最大探测距离
	 */
	@NotNull Optional<Vector3d> getPickPosition (double pickRange);

	/**
	 * 根据实体视线探测所选方块或实体
	 * <p>
	 * 当探测不到时，返回的是{@link HitResult.Type#MISS}类型。坐标将为探测终点
	 *
	 * @param pickRange 探测距离
	 */
	@NotNull HitResult pick (double pickRange);

	/**
	 * 根据实体的视线确定所选实体
	 * <p>
	 * 如果探测不到就返回空值
	 *
	 * @param pickRange 探测距离
	 */
	@NotNull Optional<EntityHitResult> pickEntity (double pickRange);

	/**
	 * 根据实体的视线确定所选方块。
	 * <p>
	 * 瞄准时会忽略草，因为使用的过滤器是 {@link ClipContext.Block#COLLIDER}
	 * <p>
	 * 非瞄准时会包括所有方块，因为过滤器是 {@link ClipContext.Block#OUTLINE}
	 * <p>
	 * 当目标无限远时，返回的是{@link HitResult.Type#MISS}类型，坐标将为探测终点，即 探测起点 + 视线向量.normalize(探测距离)
	 *
	 * @param pickRange 探测距离
	 */
	@NotNull BlockHitResult pickBlock (double pickRange);
}
