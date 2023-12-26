package net.leawind.mc.thirdperson.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.client.Camera.class)
@Environment(EnvType.CLIENT)
public interface CameraInvoker {
	@Invoker("setPosition")
	void invokeSetPosition (Vec3 pos);

	/**
	 * 单位：角度制
	 *
	 * @param yRot 偏航角，z轴正向是0，顺时针为正向
	 * @param xRot 俯仰角，俯正仰负 [-90,90]
	 */
	@Invoker("setRotation")
	void invokeSetRotation (float yRot, float xRot);

	/**
	 * 相对于当前位置移动相机
	 */
	@Invoker("move")
	void invokeMove (double forward, double up, double left);
}
