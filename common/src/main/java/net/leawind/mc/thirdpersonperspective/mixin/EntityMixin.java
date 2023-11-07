package net.leawind.mc.thirdpersonperspective.mixin;


import net.leawind.mc.thirdpersonperspective.core.CameraAgent;
import net.leawind.mc.thirdpersonperspective.core.PlayerAgent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Entity#pick 方法用于获取玩家视线的落点的坐标
 * <p>
 * 其中会创建一个 ClipContext 对象，用于描述视线的起点、终点等信息
 * <p>
 * 为了在第三人称视角中能够随时选取准星所指的方块， 在这里直接将终点信息更改为准星落点。
 */
@Mixin(net.minecraft.world.entity.Entity.class)
public class EntityMixin {
	@ModifyArg(method="pick", at=@At(value="INVOKE",
									 target="Lnet/minecraft/world/level/ClipContext;<init>(Lnet/minecraft/world/phys/Vec3;" +
											"Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/ClipContext$Block;" +
											"Lnet/minecraft/world/level/ClipContext$Fluid;" +
											"Lnet/minecraft/world/entity/Entity;" + ")" + "V"), index=1)
	private Vec3 clipContextConstructor (Vec3 viewEndFake) {
		if (CameraAgent.isAvailable() && CameraAgent.isThirdPerson) {
			Vec3   eye               = PlayerAgent.player.getEyePosition();
			double pickRange         = eye.distanceTo(viewEndFake);
			Vec3   cameraHitPosition = CameraAgent.getPickPosition();
			if (cameraHitPosition != null) {
				Vec3 viewVectorToCameraHit = eye.vectorTo(cameraHitPosition);
				return eye.add(viewVectorToCameraHit.normalize().scale(pickRange));
			}
		}
		return viewEndFake;
	}
}
