package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=Item.class, priority=2000)
public class ItemMixin {
	@Inject(method="getPlayerPOVHitResult", at=@At("HEAD"), cancellable=true)
	private static void getPlayerPOVHitResult (Level level, Player player, ClipContext.Fluid fluidShape, CallbackInfoReturnable<BlockHitResult> ci) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			ci.setReturnValue(ThirdPerson.CAMERA_AGENT.pickBlock(ClipContext.Block.OUTLINE, fluidShape));
			ci.cancel();
		}
	}
}
