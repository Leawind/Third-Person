package net.leawind.mc.thirdperson.mixin;


import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LocalPlayer.class)
public interface LocalPlayerInvoker {
	@Invoker("isControlledCamera")
	boolean invokeIsControlledCamera ();
}
