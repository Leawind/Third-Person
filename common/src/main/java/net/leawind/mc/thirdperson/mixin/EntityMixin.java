package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.core.CameraAgent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * 方法 Entity#pick 用于计算玩家视线落点
 * <p>
 * 计算过程中会创建一个 ClipContext 对象，用于描述视线的起点、终点等信息
 * <p>
 * 为了在第三人称视角中能够随时选取准星所指的方块，在这里直接将终点信息更改为相机准星落点。
 */
@Mixin(net.minecraft.world.entity.Entity.class)
public class EntityMixin {
	/**
	 * 更改 ClipContext 构造函数的第二个实参
	 *
	 * @param viewEndFake 原本的视线终点，可用于计算pickRange
	 * @return 修改后的视线终点
	 */
	@ModifyArg(method="pick", at=@At(value="INVOKE",
									 target="Lnet/minecraft/world/level/ClipContext;<init>(Lnet/minecraft/world/phys/Vec3;" +
											"Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/ClipContext$Block;" +
											"Lnet/minecraft/world/level/ClipContext$Fluid;" +
											"Lnet/minecraft/world/entity/Entity;" + ")" + "V"), index=1)
	private Vec3 clipContextConstructor (Vec3 viewEndFake) {
		if (CameraAgent.isAvailable() && CameraAgent.isThirdPerson) {
			Vec3   eye                   = CameraAgent.attachedEntity.getEyePosition(Minecraft.getInstance().getFrameTime());
			double pickRange             = eye.distanceTo(viewEndFake);
			Vec3   viewVectorToCameraHit = eye.vectorTo(CameraAgent.pick().getLocation());
			return eye.add(viewVectorToCameraHit.normalize().scale(pickRange));
		}
		return viewEndFake;
	}
}
