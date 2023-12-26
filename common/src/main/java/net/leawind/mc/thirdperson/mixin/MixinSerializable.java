package net.leawind.mc.thirdperson.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

import java.io.Serializable;

/**
 * 让一些类能被序列化
 */
@Environment(EnvType.CLIENT)
public class MixinSerializable {
	@Mixin(Vec2.class)
	interface Vec2Serializable extends Serializable {
	}

	@Mixin(Vec3.class)
	interface Vec3Serializable extends Serializable {
	}
}
