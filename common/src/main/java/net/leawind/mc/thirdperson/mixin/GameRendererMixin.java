package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonEvents;
import net.leawind.mc.util.math.LMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@link GameRenderer#pick}会先调用{@link Entity#pick}探测方块，再通过{@link ProjectileUtil#getEntityHitResult}探测实体，然后计算最终探测结果
 */
@Mixin(value=net.minecraft.client.renderer.GameRenderer.class, priority=2000)
public class GameRendererMixin {
	@Inject(method="render", at=@At("HEAD"))
	public void pre_render (float particalTicks, long l, boolean bl, CallbackInfo ci) {
		ThirdPersonEvents.onPreRender(particalTicks);
	}

	/**
	 * @param pickStartFake 探测起点，默认为玩家眼睛坐标
	 * @see EntityMixin#pick_storePickStart(Vec3)
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
	 * 在 viewVector 赋值时截获，将其修改为朝向相机视线落点的方向。
	 * <p>
	 * 这样后面就可以计算出正确的 pickEnd 和 aabb
	 *
	 * @param viewVectorFake 探测向量
	 * @see EntityMixin#pick_storeViewVector(Vec3)
	 */
	@ModifyVariable(method="pick", at=@At("STORE"), ordinal=1)
	public Vec3 pick_storeViewVector (Vec3 viewVectorFake) {
		if (ThirdPerson.isAvailable() && ThirdPerson.isThirdPerson()) {
			if (ThirdPerson.shouldPickFromCamera()) {
				return new Vec3(ThirdPerson.CAMERA_AGENT.getRawCamera().getLookVector());
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

	/**
	 * 延长 pickRange
	 */
	@ModifyVariable(method="pick", at=@At("STORE"), ordinal=0)
	public double pick_storePickRange (double pickRange) {
		if (ThirdPerson.isAvailable() && ThirdPerson.isThirdPerson() && ThirdPerson.shouldPickFromCamera()) {
			pickRange += Math.max(0, ThirdPerson.ENTITY_AGENT.getRawEyePosition(1).distance(LMath.toVector3d(ThirdPerson.CAMERA_AGENT.getRawCamera().getPosition())));
		}
		return pickRange;
	}

	/**
	 * pick 实体时，将AABB移动到以相机为起点处
	 */
	@ModifyVariable(method="pick", at=@At("STORE"), ordinal=0)
	public AABB pick_storeAabb (AABB aabb) {
		if (ThirdPerson.isAvailable() && ThirdPerson.isThirdPerson() && ThirdPerson.shouldPickFromCamera()) {
			aabb.move(ThirdPerson.CAMERA_AGENT.getRawCamera().getPosition().subtract(ThirdPerson.ENTITY_AGENT.getRawCameraEntity().getEyePosition()));
		}
		return aabb;
	}
}
