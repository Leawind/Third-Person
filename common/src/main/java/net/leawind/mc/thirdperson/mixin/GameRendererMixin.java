package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.thirdperson.event.ThirdPersonEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 方法 GameRenderer#pick 用于计算玩家pick的实体
 * <p>
 * 为了在第三人称视角中能够随时选取准星所指的实体，将获取到的玩家 viewVector 修改为朝向相机视线落点的方向
 */
@Mixin(value=net.minecraft.client.renderer.GameRenderer.class, priority=2000)
public class GameRendererMixin {
	@Inject(method="render", at=@At("HEAD"))
	private void pre_render (float particalTicks, long l, boolean bl, CallbackInfo ci) {
		ThirdPersonEvents.onPreRender(particalTicks);
	}

	/**
	 * 在 viewVector 赋值时截获，重新计算 viewVector 的值。这样就可以计算出正确的 pickEnd 和 aabb
	 */
	@ModifyVariable(method="pick", at=@At("STORE"), ordinal=1)
	private Vec3 storeViewVector (Vec3 viewVectorFake) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) {
			return viewVectorFake;
		}
		if (CameraAgent.isAvailable() && ModReferee.isThirdPerson()) {
			Vec3      eyePosition    = mc.player.getEyePosition(1);
			HitResult hr             = CameraAgent.pick();
			Vec3      eyeToHitResult = eyePosition.vectorTo(hr.getLocation());
			return eyeToHitResult.normalize();
		}
		return viewVectorFake;
	}
}
