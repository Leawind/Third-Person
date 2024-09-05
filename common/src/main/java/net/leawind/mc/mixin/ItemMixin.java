package net.leawind.mc.mixin;


import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value=Item.class, priority=2000)
public class ItemMixin {
}
