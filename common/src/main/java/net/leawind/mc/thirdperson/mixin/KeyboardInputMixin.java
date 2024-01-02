package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.MixinProxy;
import net.minecraft.client.player.KeyboardInput;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
	/**
	 * 注入到tick的末尾，重新计算 leftImpulse 和 forwardImpulse 的值
	 */
	@Inject(method="tick", at=@At(value="TAIL"))
	@PerformanceSensitive
	public void tick_inject_tail (boolean moveSlowly, float sneakingSpeedBonus, CallbackInfo ci) {
		KeyboardInput that = ((KeyboardInput)(Object)this);
		MixinProxy.tick_KeyboardInputMixin(that, moveSlowly, sneakingSpeedBonus, ci);
	}
}
