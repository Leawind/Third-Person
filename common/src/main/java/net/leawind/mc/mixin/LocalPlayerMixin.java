package net.leawind.mc.mixin;


import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value=LocalPlayer.class, priority=2000)
public class LocalPlayerMixin {
}
