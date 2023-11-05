package net.leawind.mc.thirdpersonperspective.mixin;


import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.client.Camera.class)
public interface CameraInvoker {
	@Invoker("setPosition")
	void invokeSetPosition (Vec3 pos);

	/**
	 * 单位：角度制
	 *
	 * @param yRot 水平方向旋转角度，z轴正向是0，顺时针为正向
	 * @param xRot 俯仰角，俯正仰负 [-90,90]
	 */
	@Invoker("setRotation")
	void invokeSetRotation (float yRot, float xRot);
}