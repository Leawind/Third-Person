package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * 方法{@link Entity#pick}用于计算玩家视线落点
 * <p>
 * 为了在第三人称视角中能够随时选取准星所指的方块，将获取到的玩家 viewVector 修改为朝向相机视线落点的方向
 */
@Mixin(value=net.minecraft.world.entity.Entity.class, priority=2000)
public class EntityMixin {
	/**
	 * 将探测起点更改为相机处
	 *
	 * @param pickStartFake 探测起点，默认为玩家眼睛坐标
	 * @see GameRendererMixin#pick_storePickStart(Vec3)
	 */
	@ModifyVariable(method="pick", at=@At("STORE"), ordinal=0)
	public Vec3 pick_storePickStart (Vec3 pickStartFake) {
		if (ThirdPerson.isAvailable() && ThirdPerson.isThirdPerson() && ThirdPerson.shouldPickFromCamera()) {
			return ThirdPerson.CAMERA_AGENT.getRawCamera().getPosition();
		} else {
			return pickStartFake;
		}
	}

	/**
	 * {@link Entity#pick}方法在{@link GameRenderer#pick}中被用于探测方块，
	 * <p>
	 * 原本 viewVector 是通过{@link Entity#getViewVector(float)}取得的，之后要用于计算 pickEnd
	 * <p>
	 * 咱得在 viewVector 被赋值时拦截，重新计算 viewVector 的值。
	 *
	 * @param viewVectorFake 探测向量
	 * @see GameRendererMixin#pick_storeViewVector(Vec3)
	 * @see ThirdPerson#shouldPickFromCamera()
	 */
	@ModifyVariable(method="pick", at=@At("STORE"), ordinal=1)
	public Vec3 pick_storeViewVector (Vec3 viewVectorFake) {
		if (ThirdPerson.isAvailable() && ThirdPerson.isThirdPerson()) {
			if (ThirdPerson.shouldPickFromCamera()) {
				Vector3d viewVector = LMath.toVector3d(ThirdPerson.CAMERA_AGENT.getRawCamera().getLookVector()).normalize();
				return LMath.toVec3(viewVector);
			} else {
				Vec3      eyePosition     = LMath.toVec3(ThirdPerson.ENTITY_AGENT.getRawEyePosition(1));
				HitResult cameraHitResult = ThirdPerson.CAMERA_AGENT.pick();
				Vec3      eyeToHitResult  = eyePosition.vectorTo(cameraHitResult.getLocation());
				return eyeToHitResult.normalize();
			}
		} else {
			return viewVectorFake;
		}
	}
}
