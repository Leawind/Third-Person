package net.leawind.mc.thirdperson.api.core;


import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

public record AimingTargetComparator(Vec3 pos, Vector3d viewVector) implements Comparator<Entity> {
	@Override
	public int compare (Entity e1, Entity e2) {
		return (int)Math.signum(getValue(e1) - getValue(e2));
	}

	/**
	 * TODO 判断目标优先级
	 * <p>
	 * /give @s crossbow{Enchantments:[{id:quick_charge,lvl:5}],Charged:1b}
	 */
	public double getValue (Entity entity) {
		Vec3     entityPos      = entity.getPosition(1);
		Vector3d vectorToTarget = LMath.toVector3d(entityPos.subtract(pos)).normalize();
		double   dist           = pos.distanceTo(entityPos);
		double   angleRadian    = Math.acos(viewVector.dot(vectorToTarget));
		double   angleDegrees   = Math.toDegrees(angleRadian);
		return Math.pow(dist, 2) * Math.pow(angleDegrees, 2.5);
		//		return dist * 2 + angleDegrees * 5;
	}
}
