package net.leawind.mc.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonStatus;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 方法{@link Entity#pick}用于计算玩家视线落点
 */
@Mixin(value=Entity.class, priority=2000)
public class EntityMixin {
	/**
	 * 探测方块
	 *
	 * @param playerReach  探测距离，目标与玩家眼睛间的最大距离
	 * @param includeFluid 是否探测液体，如果是，则使用{@link ClipContext.Fluid#ANY}，否则使用{@link ClipContext.Fluid#NONE}
	 * @see GameRendererMixin
	 */
	@Inject(method="pick", at=@At("HEAD"), cancellable=true)
	public void pick_head (double playerReach, float partialTick, boolean includeFluid, CallbackInfoReturnable<HitResult> ci) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			Entity cameraEntity   = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			Vec3   cameraPosition = ThirdPerson.CAMERA_AGENT.getRawCamera().getPosition();
			Vec3   eyePosition    = cameraEntity.getEyePosition(partialTick);
			Vec3   pickFrom;
			double pickRange;
			if (ThirdPersonStatus.shouldPickFromCamera()) {
				pickFrom  = cameraPosition;
				pickRange = ThirdPerson.CAMERA_AGENT.getHitResult().getLocation().distanceTo(cameraPosition) + 1e-5;
			} else {
				pickFrom  = eyePosition;
				pickRange = playerReach;
			}
			Vec3           viewVector = ThirdPerson.CAMERA_AGENT.getHitResult().getLocation().subtract(pickFrom).normalize().scale(pickRange);
			Vec3           pickTo     = pickFrom.add(viewVector);
			BlockHitResult result     = cameraEntity.level().clip(new ClipContext(pickFrom, pickTo, ClipContext.Block.OUTLINE, includeFluid ? ClipContext.Fluid.ANY: ClipContext.Fluid.NONE, cameraEntity));
			// 如果玩家眼睛与探测结果之间的距离大于探测距离，则将结果设置为MISS
			if (result.getType() != HitResult.Type.MISS) {
				if (eyePosition.distanceTo(result.getLocation()) > playerReach) {
					result = BlockHitResult.miss(result.getLocation(), result.getDirection(), result.getBlockPos());
				}
			}
			ci.setReturnValue(result);
			ci.cancel();
		}
	}
}
