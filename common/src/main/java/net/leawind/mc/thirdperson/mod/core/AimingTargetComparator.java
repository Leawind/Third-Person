package net.leawind.mc.thirdperson.mod.core;


import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

/**
 * 在预测目标实体时，判断两个实体的优先级
 *
 * @see CameraAgent#predictTargetEntity()
 */
public record AimingTargetComparator(Vec3 pos, Vector3d viewVector) implements Comparator<Entity> {
	@Override
	public int compare (Entity e1, Entity e2) {
		return (int)Math.signum(getCost(e1) - getCost(e2));
	}

	/**
	 * 计算一个目标实体的代价，值越低越优先
	 */
	public double getCost (Entity entity) {
		Vec3     entityPos      = entity.getPosition(1);
		Vector3d vectorToTarget = LMath.toVector3d(entityPos.subtract(pos)).normalize();
		double   dist           = pos.distanceTo(entityPos);
		double   angleRadian    = Math.acos(viewVector.dot(vectorToTarget));
		double   angleDegrees   = Math.toDegrees(angleRadian);
		return Math.pow(dist, 2) * Math.pow(angleDegrees, 2.5);
		//		return dist * 2 + angleDegrees * 5;
	}
}
