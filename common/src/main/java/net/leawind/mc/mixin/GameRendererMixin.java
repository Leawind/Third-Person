package net.leawind.mc.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonEvents;
import net.leawind.mc.thirdperson.ThirdPersonStatus;
import net.leawind.mc.util.annotations.VersionSensitive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@link GameRenderer#pick(float)} 方法在{@link Minecraft#tick()}开头，tick 完 chatListener 和 gui 之后调用
 * <p>
 * 在 {@link GameRenderer#renderLevel} 中也会在开头更新完相机实体后调用
 * <p>
 * {@link GameRenderer#pick}会先调用{@link Entity#pick}探测方块，再通过{@link ProjectileUtil#getEntityHitResult}探测实体，然后计算最终探测结果
 * <p>
 * 当探测结果为空时，它会通过 {@link BlockHitResult#miss(Vec3, Direction, BlockPos)} 创建一个表示结果为空的 BlockHitResult 对象，此时会根据玩家的朝向计算 Direction 参数。
 * <p>
 * {@link EntityMixin#pick_head}修改了探测方块的逻辑
 */
@Mixin(value=GameRenderer.class, priority=2000)
public class GameRendererMixin {
	/**
	 * on pre render
	 */
	@Inject(method="render", at=@At("HEAD"))
	public void pre_render (float partialTick, long nanoTime, boolean doRenderLevel, CallbackInfo ci) {
		if (doRenderLevel && ThirdPerson.getConfig().is_mod_enable) {
			ThirdPersonEvents.onPreRender(partialTick);
		}
	}

	/**
	 * @see Minecraft#hitResult
	 * @see Minecraft#crosshairPickEntity
	 */
	@VersionSensitive("Entity predicate")
	@Inject(method="pick", at=@At("HEAD"), cancellable=true)
	public void pick_storeViewVector (float partialTick, CallbackInfo ci) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			ThirdPerson.mc.getProfiler().push("pick");
			BlockHitResult  blockHitResult;
			EntityHitResult entityHitResult;
			ThirdPerson.mc.crosshairPickEntity = null;
			assert ThirdPerson.mc.gameMode != null;
			double playerReach         = ThirdPerson.mc.gameMode.getPickRange();
			Entity cameraEntity        = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			Vec3   eyePosition         = cameraEntity.getEyePosition(partialTick);
			Vec3   cameraPosition      = ThirdPerson.CAMERA_AGENT.getRawCamera().getPosition();
			Vec3   cameraToEye         = eyePosition.subtract(cameraPosition);
			double cameraToEyeDistance = cameraToEye.length();
			double cameraReachMax      = cameraToEyeDistance + playerReach;
			// 选取方块
			{
				blockHitResult = (BlockHitResult)cameraEntity.pick(playerReach, partialTick, false);
			}
			// 选取实体
			{
				Vec3 cameraLookVector = new Vec3(ThirdPerson.mc.gameRenderer.getMainCamera().getLookVector());
				Vec3 pickFrom;
				Vec3 pickTo;
				if (ThirdPersonStatus.shouldPickFromCamera()) {
					pickFrom = cameraPosition;
					pickTo   = pickFrom.add(cameraLookVector.scale(cameraReachMax));
				} else {
					pickFrom = eyePosition;
					pickTo   = blockHitResult.getLocation();
				}
				AABB aabb = new AABB(pickFrom, pickTo);
				entityHitResult = ProjectileUtil.getEntityHitResult(cameraEntity, pickFrom, pickTo, aabb, (entity) -> !entity.isSpectator() && entity.isPickable(), cameraReachMax * cameraReachMax);
				if (entityHitResult != null && entityHitResult.getLocation().subtract(eyePosition).length() > playerReach) {
					entityHitResult = null;
				}
			}
			if (entityHitResult == null) {
				ThirdPerson.mc.hitResult = blockHitResult;
			} else if (blockHitResult.getType() == HitResult.Type.MISS) {
				ThirdPerson.mc.hitResult = entityHitResult;
			} else {
				Vec3   pickFrom         = ThirdPersonStatus.shouldPickFromCamera() ? cameraPosition: eyePosition;
				double blockResultDist  = blockHitResult.getLocation().subtract(pickFrom).length();
				double entityResultDist = entityHitResult.getLocation().subtract(pickFrom).length();
				if (blockResultDist < entityResultDist) {
					ThirdPerson.mc.hitResult = blockHitResult;
				} else {
					ThirdPerson.mc.hitResult = entityHitResult;
				}
			}
			if (ThirdPerson.mc.hitResult.getType() == HitResult.Type.ENTITY) {
				assert entityHitResult != null;
				Entity targetEntity = entityHitResult.getEntity();
				if (targetEntity instanceof LivingEntity || targetEntity instanceof ItemFrame) {
					ThirdPerson.mc.crosshairPickEntity = targetEntity;
				}
			}
			ci.cancel();
			ThirdPerson.mc.getProfiler().pop();
		}
	}
}
