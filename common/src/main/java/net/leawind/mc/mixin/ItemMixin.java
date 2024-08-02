package net.leawind.mc.mixin;


import net.leawind.mc.api.base.GameEvents;
import net.leawind.mc.api.client.event.MinecraftPickEvent;
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
	/**
	 * 当使用船、水瓶、水桶、末影之眼、刷怪蛋、PlaceOnWaterBlockItem 时，计算视线落点。
	 */
	@Inject(method="getPlayerPOVHitResult", at=@At(value="INVOKE", target="Lnet/minecraft/world/level/ClipContext;<init>(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/ClipContext$Block;" +
																		  "Lnet/minecraft/world/level/ClipContext$Fluid;Lnet/minecraft/world/entity/Entity;)V"), cancellable=true)
	private static void a (Level level, Player player, ClipContext.Fluid fluid, CallbackInfoReturnable<BlockHitResult> ci) {
		if (GameEvents.minecraftPick != null) {
			MinecraftPickEvent event = new MinecraftPickEvent(1, 5.0);
			GameEvents.minecraftPick.accept(event);
			if (event.set()) {
				BlockHitResult result = level.clip(new ClipContext(event.pickFrom(), event.pickTo(), ClipContext.Block.OUTLINE, fluid, player));
				ci.setReturnValue(result);
				ci.cancel();
			}
		}
	}
}
