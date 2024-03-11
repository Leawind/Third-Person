package net.leawind.mc.thirdperson.mixin;


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
	 * @param pickRange    探测距离，目标与玩家眼睛间的最大距离
	 * @param includeFluid 是否探测液体，如果是，则使用{@link ClipContext.Fluid#ANY}，否则使用{@link ClipContext.Fluid#NONE}
	 * @see GameRendererMixin
	 */
	@Inject(method="pick", at=@At("HEAD"), cancellable=true)
	public void pick_head (double pickRange, float partialTick, boolean includeFluid, CallbackInfoReturnable<HitResult> ci) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			final ClipContext.Fluid fluidShape   = includeFluid ? ClipContext.Fluid.ANY: ClipContext.Fluid.NONE;
			Entity                  cameraEntity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			BlockHitResult          result;
			if (ThirdPersonStatus.shouldPickFromCamera()) {
				result = ThirdPerson.CAMERA_AGENT.pickBlock(ClipContext.Block.OUTLINE, fluidShape);
			} else {
				Vec3 pickFrom   = cameraEntity.getEyePosition(partialTick);
				Vec3 viewVector = ThirdPerson.CAMERA_AGENT.getHitResult().getLocation().subtract(pickFrom).normalize().scale(pickRange);
				Vec3 pickTo     = pickFrom.add(viewVector);
				result = cameraEntity.level().clip(new ClipContext(pickFrom, pickTo, ClipContext.Block.OUTLINE, fluidShape, cameraEntity));
			}
			// 如果玩家眼睛与探测结果之间的距离大于探测距离，则将结果设置为MISS
			if (result.getType() != HitResult.Type.MISS) {
				if (cameraEntity.getEyePosition(partialTick).distanceTo(result.getLocation()) > pickRange) {
					result = BlockHitResult.miss(result.getLocation(), result.getDirection(), result.getBlockPos());
				}
			}
			ci.setReturnValue(result);
			ci.cancel();
		}
	}
}
