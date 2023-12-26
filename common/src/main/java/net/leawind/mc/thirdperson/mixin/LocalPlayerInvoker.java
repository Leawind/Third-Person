package net.leawind.mc.thirdperson.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.client.player.LocalPlayer.class)
@Environment(EnvType.CLIENT)
public interface LocalPlayerInvoker {
	@Invoker("isControlledCamera")
	boolean invokeIsControlledCamera ();
}
