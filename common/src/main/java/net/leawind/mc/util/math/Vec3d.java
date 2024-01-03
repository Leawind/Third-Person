package net.leawind.mc.util.math;


import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.EnumSet;
import java.util.List;

@SuppressWarnings("unused")
public class Vec3d extends Vec3 {
	public static final Codec<Vec3d> CODEC      = Codec.DOUBLE.listOf().comapFlatMap(list2 -> Util.fixedSize(list2, 3)
																								  .map(list -> new Vec3d(list.get(
																									  0),
																														 list.get(
																															 1),
																														 list.get(
																															 2))),
																					 vec3 -> List.of(vec3.x(),
																									 vec3.y(),
																									 vec3.z()));
	public static final Vec3d        ZERO       = new Vec3d(0);
	public static final Vec3d        ONE        = new Vec3d(1);
	public static final Vec3d        UNIT_X     = new Vec3d(1, 0, 0);
	public static final Vec3d        UNIT_Y     = new Vec3d(0, 1, 0);
	public static final Vec3d        UNIT_Z     = new Vec3d(0, 0, 1);
	public static final Vec3d        NEG_UNIT_X = new Vec3d(-1, 0, 0);
	public static final Vec3d        NEG_UNIT_Y = new Vec3d(0, -1, 0);
	public static final Vec3d        NEG_UNIT_Z = new Vec3d(0, 0, -1);
	public static final Vec3d        MAX        = new Vec3d(Double.MAX_VALUE);
	public static final Vec3d        MIN        = new Vec3d(Double.MIN_VALUE);

	public static Vec3d of (Vec3 v) {
		return new Vec3d(v.x, v.y, v.z);
	}

	public Vec3d (double d) {
		this(d, d, d);
	}

	public Vec3d (Vector3f v) {
		this(v.x, v.y, v.z);
	}

	public Vec3d (Vector3d v) {
		this(v.x, v.y, v.z);
	}

	public Vec3d (double x, double y, double z) {
		super(x, y, z);
	}

	public static Vec3d fromRGB24 (int i) {
		double d = (double)(i >> 16 & 0xFF) / 255.0;
		double e = (double)(i >> 8 & 0xFF) / 255.0;
		double f = (double)(i & 0xFF) / 255.0;
		return new Vec3d(d, e, f);
	}

	public static Vec3d atLowerCornerOf (Vec3i vec3i) {
		return new Vec3d(vec3i.getX(), vec3i.getY(), vec3i.getZ());
	}

	public static Vec3d atCenterOf (Vec3i vec3i) {
		return Vec3d.atLowerCornerWithOffset(vec3i, 0.5, 0.5, 0.5);
	}

	public static Vec3d atLowerCornerWithOffset (Vec3i vec3i, double d, double e, double f) {
		return new Vec3d((double)vec3i.getX() + d, (double)vec3i.getY() + e, (double)vec3i.getZ() + f);
	}

	public static Vec3d atBottomCenterOf (Vec3i v) {
		return Vec3d.atLowerCornerWithOffset(v, 0.5, 0.0, 0.5);
	}

	public static Vec3d upFromBottomCenterOf (Vec3i v, double d) {
		return Vec3d.atLowerCornerWithOffset(v, 0.5, d, 0.5);
	}

	public static Vec3d directionFromRotation (Vec2d v) {
		return Vec3d.directionFromRotation(v.x, v.y);
	}

	public static Vec3d directionFromRotation (double f, double g) {
		double h = Math.cos(-g * (Math.PI / 180) - Math.PI);
		double i = Math.sin(-g * (Math.PI / 180) - Math.PI);
		double j = -Math.cos(-f * (Math.PI / 180));
		double k = Math.sin(-f * (Math.PI / 180));
		return new Vec3d(i * j, k, h * j);
	}

	public Vec3d vectorTo (Vec3d vec3D) {
		return new Vec3d(vec3D.x - this.x, vec3D.y - this.y, vec3D.z - this.z);
	}

	public Vec3d normalized () {
		return normalize();
	}

	public @NotNull Vec3d normalize () {
		double d = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		if (d < 1.0E-4) {
			return ZERO;
		}
		return new Vec3d(this.x / d, this.y / d, this.z / d);
	}

	public double dot (Vec3d v) {
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}

	public Vec3d cross (Vec3d v) {
		return new Vec3d(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
	}

	public Vec3d negated () {
		return new Vec3d(-x, -y, -z);
	}

	public Vec3d subtract (Vec3d v) {
		return this.subtract(v.x, v.y, v.z);
	}

	public @NotNull Vec3d subtract (double d, double e, double f) {
		return this.add(-d, -e, -f);
	}

	public Vec3d add (double d) {
		return add(d, d, d);
	}

	public @NotNull Vec3d add (double d, double e, double f) {
		return new Vec3d(this.x + d, this.y + e, this.z + f);
	}

	public Vec3d add (Vec3d v) {
		return this.add(v.x, v.y, v.z);
	}

	public double distanceTo (Vec3d v) {
		double d = v.x - x;
		double e = v.y - y;
		double f = v.z - z;
		return Math.sqrt(d * d + e * e + f * f);
	}

	public double distanceToSqr (Vec3d v) {
		double d = v.x - x;
		double e = v.y - y;
		double f = v.z - z;
		return d * d + e * e + f * f;
	}

	public @NotNull Vec3d reverse () {
		return this.scale(-1.0);
	}

	public @NotNull Vec3d scale (double d) {
		return this.multiply(d, d, d);
	}

	public @NotNull Vec3d multiply (double d) {
		return multiply(d, d, d);
	}

	public @NotNull Vec3d multiply (double d, double e, double f) {
		return new Vec3d(this.x * d, this.y * e, this.z * f);
	}

	public Vec3d multiply (Vec3d v) {
		return this.multiply(v.x, v.y, v.z);
	}

	public @NotNull Vec3d offsetRandom (RandomSource randomSource, double f) {
		return this.add((randomSource.nextDouble() - 0.5D) * f,
						(randomSource.nextDouble() - 0.5D) * f,
						(randomSource.nextDouble() - 0.5D) * f);
	}

	public boolean equals (Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Vec3d v)) {
			return false;
		}
		if (Double.compare(v.x, x) != 0) {
			return false;
		}
		if (Double.compare(v.y, y) != 0) {
			return false;
		}
		return Double.compare(v.z, z) == 0;
	}

	public Vec3d lerp (Vec3d v, double d) {
		return new Vec3d(Mth.lerp(d, this.x, v.x), Mth.lerp(d, this.y, v.y), Mth.lerp(d, this.z, v.z));
	}

	public @NotNull Vec3d xRot (double f) {
		double g = Math.cos(f);
		double h = Math.sin(f);
		double e = y * g + z * h;
		double i = z * g - y * h;
		return new Vec3d(x, e, i);
	}

	public @NotNull Vec3d yRot (double f) {
		double g = Math.cos(f);
		double h = Math.sin(f);
		double d = x * g + z * h;
		double i = z * g - x * h;
		return new Vec3d(d, this.y, i);
	}

	public @NotNull Vec3d zRot (double f) {
		double g = Math.cos(f);
		double h = Math.sin(f);
		double d = x * g + y * h;
		double e = y * g - x * h;
		return new Vec3d(d, e, this.z);
	}

	public @NotNull Vec3d align (EnumSet<Direction.Axis> enumSet) {
		double d = enumSet.contains(Direction.Axis.X) ? Math.floor(this.x): this.x;
		double e = enumSet.contains(Direction.Axis.Y) ? Math.floor(this.y): this.y;
		double f = enumSet.contains(Direction.Axis.Z) ? Math.floor(this.z): this.z;
		return new Vec3d(d, e, f);
	}

	public @NotNull Vec3d with (Direction.Axis axis, double d) {
		double e = axis == Direction.Axis.X ? d: x;
		double f = axis == Direction.Axis.Y ? d: y;
		double g = axis == Direction.Axis.Z ? d: z;
		return new Vec3d(e, f, g);
	}

	public @NotNull Vec3d relative (Direction direction, double d) {
		Vec3i vec3i = direction.getNormal();
		return new Vec3d(x + d * vec3i.getX(), y + d * vec3i.getY(), z + d * vec3i.getZ());
	}

	public Vec2d xy () {
		return new Vec2d(x, y);
	}

	public Vec2d xz () {
		return new Vec2d(x, z);
	}

	public Vec2d yz () {
		return new Vec2d(y, z);
	}

	public Vec2d yx () {
		return new Vec2d(y, x);
	}

	public Vec2d zx () {
		return new Vec2d(z, x);
	}

	public Vec2d zy () {
		return new Vec2d(z, y);
	}

	public Vector3d toVector3d () {
		return new Vector3d(x, y, z);
	}
}
