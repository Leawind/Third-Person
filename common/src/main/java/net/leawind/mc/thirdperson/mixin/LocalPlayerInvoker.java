package net.leawind.mc.thirdperson.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.client.player.LocalPlayer.class)
public interface LocalPlayerInvoker {
	@Invoker("isControlledCamera")
	boolean invokeIsControlledCamera ();
}
