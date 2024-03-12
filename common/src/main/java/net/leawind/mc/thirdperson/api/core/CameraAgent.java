package net.leawind.mc.thirdperson.api.core;


import net.leawind.mc.thirdperson.impl.core.CameraAgentImpl;
import net.leawind.mc.thirdperson.mixin.CameraMixin;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
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

	/**
	 * 设置维度
	 */
	void setBlockGetter (@NotNull BlockGetter blockGetter);

	/**
	 * 渲染前
	 */
	void onRenderTickPre (double now, double period, float partialTick);

	/**
	 * 渲染过程中放置相机
	 * <p>
	 * 在原版的渲染方法中，会调用{@link Camera#setup}来设置相机的位置和朝向。
	 * <p>
	 * 在第三人称下，咱需要覆盖该方法的行为，重新设置相机的位置和朝向。
	 * <p>
	 * {@link CameraMixin#setup_invoke}
	 */
	void onCameraSetup ();

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

	/**
	 * 获取原始相机位置
	 */
	@NotNull Vector3d getRawCameraPosition ();

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
	 * render tick 开始时，相机的 hitResult
	 */
	@NotNull HitResult getHitResult ();

	double getPickRange ();

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
	 * 从相机出发探测所选方块或实体。
	 * <p>
	 * 当探测不到时，返回的是{@link HitResult.Type#MISS}类型。坐标将为探测终点
	 *
	 * @param pickRange 探测距离限制
	 */
	@NotNull HitResult pick (double pickRange);

	/**
	 * 根据相机的视线确定所选实体
	 * <p>
	 * 如果探测不到就返回空值
	 *
	 * @param pickRange 探测距离
	 */
	@NotNull Optional<EntityHitResult> pickEntity (double pickRange);

	/**
	 * 同 {@link CameraAgent#pickEntity(double)}，使用默认距离
	 */
	@NotNull Optional<EntityHitResult> pickEntity ();

	/**
	 * 根据相机的视线探测方块
	 *
	 * @param pickRange  从相机出发的探测距离
	 * @param blockShape 方块形状获取器
	 * @param fluidShape 液体形状获取器
	 */
	@NotNull BlockHitResult pickBlock (double pickRange, @NotNull ClipContext.Block blockShape, @NotNull ClipContext.Fluid fluidShape);

	/**
	 * 同 {@link CameraAgent#pickBlock(double, ClipContext.Block, ClipContext.Fluid)}，使用默认距离
	 *
	 * @param blockShape 方块形状获取器
	 * @param fluidShape 液体形状获取器
	 */
	@NotNull BlockHitResult pickBlock (@NotNull ClipContext.Block blockShape, @NotNull ClipContext.Fluid fluidShape);

	/**
	 * 同 {@link CameraAgent#pickBlock(ClipContext.Block, ClipContext.Fluid)}，但是
	 * <p>
	 * 瞄准时使用的方块形状获取器是 {@link ClipContext.Block#COLLIDER}，不包含草
	 * <p>
	 * 非瞄准时使用的方块形状获取器是 {@link ClipContext.Block#OUTLINE}
	 * <p>
	 * 当探测不到方块时，返回的是 {@link HitResult.Type#MISS} 类型，坐标将为探测终点，即 相机位置 + 视线向量.normalize(探测距离)
	 *
	 * @param pickRange 从相机出发的探测距离
	 */
	@NotNull BlockHitResult pickBlock (double pickRange);

	/**
	 * 相机是否正在注视某个实体（无视其他实体或方块）
	 */
	boolean isLookingAt (@NotNull Entity entity);

	/**
	 * 预测玩家可能想要射击的目标实体
	 * <p>
	 * TODO 预测不够准确
	 */
	@NotNull Optional<Entity> predictTargetEntity ();
}
