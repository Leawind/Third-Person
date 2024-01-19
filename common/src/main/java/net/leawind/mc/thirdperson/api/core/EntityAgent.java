package net.leawind.mc.thirdperson.api.core;


import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.thirdperson.impl.core.EntityAgentImpl;
import net.leawind.mc.util.api.math.vector.Vector2d;
import net.leawind.mc.util.api.math.vector.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * 相机附着的实体
 * <p>
 * 当操控玩家时，玩家的旋转由本类接管。
 */
public interface EntityAgent {
	static EntityAgent create (Minecraft mc) {
		return new EntityAgentImpl(mc);
	}

	/**
	 * 相机实体 {@link Minecraft#cameraEntity} 是否已经存在
	 */
	boolean isCameraEntityExist ();

	/**
	 * 重置
	 */
	void reset ();

	/**
	 * @param period 相邻两次 render tick 的时间差，单位：s
	 */
	void onPreRender (double period, float partialTick);

	void onClientTickPre ();

	/**
	 * 玩家当前是否在操控这个实体
	 */
	boolean isControlled ();

	/**
	 * 获取相机附着的实体
	 */
	@NotNull Entity getRawCameraEntity () throws NullPointerException;

	/**
	 * 获取玩家实体
	 */
	@NotNull LocalPlayer getRawPlayerEntity () throws NullPointerException;

	/**
	 * 直接从实体获取眼睛坐标
	 */
	@NotNull Vector3d getRawEyePosition (float partialTick) throws NullPointerException;

	/**
	 * 直接从实体获取坐标
	 */
	@NotNull Vector3d getRawPosition (float partialTick) throws NullPointerException;

	/**
	 * 直接从实体获取朝向
	 */
	@NotNull Vector2d getRawRotation (float partialTick) throws NullPointerException;

	/**
	 * 获取平滑的眼睛坐标
	 */
	@NotNull Vector3d getSmoothEyePosition (float partialTick);

	/**
	 * 获取实体朝向
	 */
	@NotNull Vector2d getRotation (float partialTick);

	/**
	 * 实体是否在交互
	 * <p>
	 * 当控制玩家时，相当于是否按下了 使用|攻击|选取 键
	 * <p>
	 * 当附身其他实体时，另做判断
	 * <p>
	 * {@link ModReferee#isInterecting}
	 */
	boolean isInterecting ();

	/**
	 * 实体是否在飞行
	 * <p>
	 * {@link ModReferee#isAttachedEntityFallFlying}
	 */
	boolean isFallFlying ();

	/**
	 * 根据实体的手持物品和使用状态判断是否在瞄准
	 * <p>
	 * 不考虑玩家按键
	 * <p>
	 * {@link ModReferee#isCameraEntityAiming}
	 */
	boolean isAiming ();

	/**
	 * 在上一个 clientTick 中是否在瞄准
	 */
	boolean wasAiming ();
}
