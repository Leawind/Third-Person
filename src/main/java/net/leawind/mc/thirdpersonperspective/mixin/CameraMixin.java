package net.leawind.mc.thirdpersonperspective.mixin;


import net.leawind.mc.thirdpersonperspective.agent.CameraAgent;
import net.leawind.mc.thirdpersonperspective.mixininterface.CameraMixinInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修改 setup 方法，
 * <p>
 * 用于设置相机的朝向和位置
 */
@Mixin(net.minecraft.client.Camera.class)
public abstract class CameraMixin implements CameraMixinInterface {
	@Shadow
	protected void setPosition (Vec3 pos) {
	}

	/**
	 * 单位：角度制
	 *
	 * @param yRot 水平方向旋转角度，z轴正向是0，顺时针为正向
	 * @param xRot 俯仰角，俯正仰负 [-90,90]
	 */
	@Shadow
	protected void setRotation (float yRot, float xRot) {
	}

	/**
	 * 设置相机真实位置 不允许出现无穷大或NaN
	 */
	@Override
	public void third_Person_View$setRealPosition (
		@NotNull
		Vec3 pos) {
		setPosition(pos);
	}

	/**
	 * 设置相机真实位置 不允许出现无穷大或NaN
	 */
	@Override
	public void third_Person_View$setRealRotation (float yRot, float xRot) {
		setRotation(yRot, xRot);
	}

	@Unique
	private boolean third_Person_View$wasFirstPerson = true;
	@Unique
	private boolean third_Person_View$isFarther      = false;

	/**
	 * 记录实参
	 *
	 * @param isFartherForReal 原本表示“相机是否在玩家正前方”，现在我将它解释为 “是否采用更远的相机距离”
	 * @return 必须返回 false，这样可以阻止其调用 setRotation 来设置相机朝向
	 */
	@ModifyVariable(method="setup", at=@At("HEAD"), ordinal=1, argsOnly=true)
	private boolean injected (boolean isFartherForReal) {
		third_Person_View$isFarther = isFartherForReal;
		return false;
	}

	/**
	 * 如果刚进入第三人称视角，则调用相应的事件处理函数
	 *
	 * @param blockGetter   用于获取维度中的方块状态
	 * @param entity        相机所属的实体
	 * @param detached      是否是第三人称视角
	 * @param isFartherFake 已经被我改成了false
	 */
	@Inject(method="setup", at=@At(value="HEAD"))
	public void setup_head (BlockGetter blockGetter,
							Entity entity,
							boolean detached,
							boolean isFartherFake,
							float lerpK,
							CallbackInfo ci) {
		boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
		if (CameraAgent.isAvailable()) {
			CameraAgent.getInstance().isFreeTpv = false;
			if (third_Person_View$wasFirstPerson && !isFirstPerson) {
				CameraAgent.getInstance().onEnterThirdPerson(lerpK);
			}
		}
		third_Person_View$wasFirstPerson = isFirstPerson;
	}

	/**
	 * 调用咱自己的 tick 方法，更新相机的朝向、位置等信息
	 */
	@Inject(method="setup", at=@At(value="INVOKE", target="Lnet/minecraft/client/Camera;move(DDD)V", shift=At.Shift.BEFORE),
			cancellable=true)
	public void setup_inject (BlockGetter level,
							  Entity entity,
							  boolean detached,
							  boolean isFartherFake,
							  float lerpK,
							  CallbackInfo ci) {
		if (CameraAgent.isAvailable()) {
			CameraAgent cameraAgent = CameraAgent.getInstance();
			cameraAgent.isFreeTpv = true;
			cameraAgent.onRenderTick(level, entity, third_Person_View$isFarther, lerpK);
			ci.cancel();
		}
	}
}
