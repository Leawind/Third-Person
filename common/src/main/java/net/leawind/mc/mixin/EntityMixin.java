package net.leawind.mc.mixin;


import net.leawind.mc.api.base.GameEvents;
import net.leawind.mc.api.client.events.MinecraftPickEvent;
import net.leawind.mc.api.client.events.PreEntityTurnEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 *
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
	private void pick (double playerReach, float partialTick, boolean includeFluid, CallbackInfoReturnable<HitResult> ci) {
		if (GameEvents.minecraftPick != null) {
			Entity             entity = (Entity)(Object)this;
			MinecraftPickEvent event  = new MinecraftPickEvent(partialTick, playerReach);
			GameEvents.minecraftPick.accept(event);
			if (event.set()) {
				BlockHitResult result = entity.level().clip(new ClipContext(event.pickFrom(), event.pickTo(), ClipContext.Block.OUTLINE, includeFluid ? ClipContext.Fluid.ANY: ClipContext.Fluid.NONE, entity));
				if (result.getType() != HitResult.Type.MISS) {
					if (result.getLocation().distanceTo(entity.getEyePosition(partialTick)) > playerReach) {
						result = BlockHitResult.miss(result.getLocation(), result.getDirection(), result.getBlockPos());
					}
				}
				ci.setReturnValue(result);
				ci.cancel();
			}
		}
	}

	@Inject(method="turn", at=@At("HEAD"), cancellable=true)
	private void turn (double yRot, double xRot, @NotNull CallbackInfo ci) {
		if (GameEvents.preEntityTurn != null) {
			Entity             entity = (Entity)(Object)this;
			PreEntityTurnEvent event  = new PreEntityTurnEvent(entity, yRot * 0.15, xRot * 0.15);
			GameEvents.preEntityTurn.accept(event);
			if (event.isDefaultCancelled()) {
				ci.cancel();
			}
		}
	}
}
