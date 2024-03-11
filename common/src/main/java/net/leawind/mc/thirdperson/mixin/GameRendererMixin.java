package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonEvents;
import net.leawind.mc.thirdperson.ThirdPersonStatus;
import net.leawind.mc.thirdperson.api.core.CameraAgent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@link GameRenderer#pick(float)} 方法在{@link Minecraft#tick()}开头，tick 完 chatListener 和 gui 之后调用
 * <p>
 * 在 {@link GameRenderer#renderLevel} 中也会在开头更新完相机实体后调用
 * <p>
 * {@link GameRenderer#pick}会先调用{@link Entity#pick}探测方块，再通过{@link ProjectileUtil#getEntityHitResult}探测实体，然后计算最终探测结果
 * <p>
 * 当探测结果为空时，它会通过 {@link BlockHitResult#miss(Vec3, Direction, BlockPos)} 创建一个表示结果为空的 BlockHitResult 对象，此时会根据玩家的朝向计算 Direction 参数。
 * <p>
 * {@link EntityMixin#pick_head}修改了探测方块的逻辑
 * <p>
 * {@link GameRendererMixin#pick_storeEntityPickResult(EntityHitResult)}修改了探测实体的逻辑
 */
@Mixin(value=GameRenderer.class, priority=2000)
public class GameRendererMixin {
	/**
	 * on pre render
	 */
	@Inject(method="render", at=@At("HEAD"))
	public void pre_render (float partialTick, long nanoTime, boolean doRenderLevel, CallbackInfo ci) {
		if (doRenderLevel && ThirdPerson.getConfig().is_mod_enable) {
			ThirdPersonEvents.onPreRender(partialTick);
		}
	}

	/**
	 * 原版中 viewVector 有两个作用：
	 * <li>用于计算探测终点。应将它改为从实体眼睛到相机探测落点{@link CameraAgent#getHitResult()}的方向</li>
	 * <li>当 pick 结果为 miss 时，用于计算 miss 类型 pick 结果的方向，在{@link GameRendererMixin#pick_modifyDirection} 中修改</li>
	 */
	@ModifyVariable(method="pick", at=@At("STORE"), ordinal=1)
	public Vec3 pick_storeViewVector (Vec3 viewVector) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			Entity cameraEntity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			return ThirdPerson.CAMERA_AGENT.getHitResult().getLocation().subtract(cameraEntity.getEyePosition(1)).normalize();
		} else {
			return viewVector;
		}
	}

	/**
	 * 在探测完实体后截获返回值，
	 * <p>
	 * 如果要从相机开始探测则重新计算探测结果。
	 * <p>
	 * 否则直接返回原本的结果。
	 *
	 * @param hitResult 原本的探测结果，
	 */
	@ModifyVariable(method="pick", at=@At("STORE"), ordinal=0)
	public EntityHitResult pick_storeEntityPickResult (EntityHitResult hitResult) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && ThirdPersonStatus.shouldPickFromCamera()) {
			return ThirdPerson.CAMERA_AGENT.pickEntity().orElse(null);
		} else {
			return hitResult;
		}
	}

	/**
	 * 当 pick 结果为 miss 时，重新计算 Direction 参数（不知道有什么用，但这样似乎更合理，且应该不太会影响兼容性）
	 */
	@ModifyArg(method="pick", at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/BlockHitResult;miss(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/BlockHitResult;"), index=1)
	private Direction pick_modifyDirection (Direction direction) {
		Vector3f viewVector = ThirdPerson.CAMERA_AGENT.getRawCamera().getLookVector();
		return Direction.getNearest(viewVector.x, viewVector.y, viewVector.z);
	}
}
