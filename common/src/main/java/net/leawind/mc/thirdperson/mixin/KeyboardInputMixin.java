package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
	@Inject(method="tick", at=@At(value="TAIL"))
	public void tick_inject_tail (boolean flag, float amplifier, CallbackInfo ci) {
		KeyboardInput that = ((KeyboardInput)(Object)this);
		ThirdPersonMod.temp_KeyboardInputMixin_tick(that, flag, amplifier);
	}
}
