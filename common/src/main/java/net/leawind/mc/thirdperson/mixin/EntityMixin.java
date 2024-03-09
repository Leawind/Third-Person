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
@Mixin(value=net.minecraft.world.entity.Entity.class, priority=2000)
public class EntityMixin {
	/**
	 * 第三人称下重新计算选取的方块
	 */
	@Inject(method="pick", at=@At("HEAD"), cancellable=true)
	public void pick_head (double pickRange, float partialTick, boolean includeFluid, CallbackInfoReturnable<HitResult> ci) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			Entity         cameraEntity    = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			BlockHitResult cameraHitResult = ThirdPerson.CAMERA_AGENT.pickBlock(ThirdPerson.getConfig().camera_ray_trace_length);
			Vec3           pickStart, pickEnd;
			BlockHitResult result;
			if (ThirdPersonStatus.shouldPickFromCamera()) {
				result = cameraHitResult;
			} else {
				pickStart = cameraEntity.getEyePosition(partialTick);
				pickEnd   = cameraHitResult.getLocation();
				result    = cameraEntity.level().clip(new ClipContext(pickStart, pickEnd, ClipContext.Block.OUTLINE, includeFluid ? net.minecraft.world.level.ClipContext.Fluid.ANY: net.minecraft.world.level.ClipContext.Fluid.NONE, cameraEntity));
			}
			if (result.getType() != HitResult.Type.MISS) {
				Vec3 centerOfBlockPos = Vec3.atCenterOf(result.getBlockPos());
				if (cameraEntity.getEyePosition(partialTick).distanceTo(centerOfBlockPos) > pickRange) {
					result = BlockHitResult.miss(result.getLocation(), result.getDirection(), result.getBlockPos());
				}
			}
			ci.setReturnValue(result);
			ci.cancel();
		}
	}
}
