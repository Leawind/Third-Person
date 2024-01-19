package net.leawind.mc.thirdperson.api.core.rotation;


import net.leawind.mc.util.api.math.vector.Vector2d;
import org.jetbrains.annotations.Nullable;

public interface IRotateStrategy {
	/**
	 * 获取玩家当前的目标朝向
	 */
	@Nullable Vector2d getRotation (float partialTick);
}
