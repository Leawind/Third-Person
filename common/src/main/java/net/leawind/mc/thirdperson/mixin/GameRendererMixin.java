package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonEvents;
import net.leawind.mc.thirdperson.ThirdPersonStatus;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@link GameRenderer#pick}会先调用{@link EntityMixin#pick_head}探测方块，再通过{@link ProjectileUtil#getEntityHitResult}探测实体，然后计算最终探测结果
 * <p>
 * 此外，当没有探测结果时，它会通过 {@link BlockHitResult#miss} 创建一个表示结果为空的 BlockHitResult 对象，此时会根据玩家的朝向计算 Direction 参数。
 * <p>
 * 对方块的探测在{@link EntityMixin#pick_head}中修改
 * <p>
 * 对实体的探测在{@link GameRendererMixin#pick_storeEntityPickResult(EntityHitResult)}中修改
 */
@Mixin(value=net.minecraft.client.renderer.GameRenderer.class, priority=2000)
public class GameRendererMixin {
	@Inject(method="render", at=@At("HEAD"))
	public void pre_render (float particalTicks, long l, boolean bl, CallbackInfo ci) {
		if (ThirdPerson.getConfig().is_mod_enable) {
			ThirdPersonEvents.onPreRender(particalTicks);
		}
	}

	/**
	 * 原版中 viewVector 有两个作用：
	 * <li>计算pick射线方向</li>
	 * <li>当 pick 结果为 miss 时，用于计算 miss 类型 pick 结果的方向</li>
	 * <p>
	 * 由于 {@link GameRendererMixin#pick_storeEntityPickResult} 重新计算了 pick 结果，所以它将不会发挥第一个作用。
	 * <p>
	 * 第二个作用也应当修改一下。
	 * <p>
	 * 这里将方向修改为相机的朝向。这样在放置方块时更符合直觉。
	 */
	@ModifyVariable(method="pick", at=@At("STORE"), ordinal=1)
	public Vec3 pick_storeViewVector (Vec3 pickFromFake) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			return new Vec3(ThirdPerson.CAMERA_AGENT.getRawCamera().getLookVector());
		} else {
			return pickFromFake;
		}
	}

	/**
	 * 重新计算 pick entity 结果
	 */
	@ModifyVariable(method="pick", at=@At("STORE"), ordinal=0)
	public EntityHitResult pick_storeEntityPickResult (EntityHitResult hitResult) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && ThirdPersonStatus.shouldPickFromCamera()) {
			return ThirdPerson.CAMERA_AGENT.pickEntity().orElse(null);
		} else {
			return hitResult;
		}
	}
}
