package net.leawind.mc.util.math;


import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;
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
		return (Vec3d)v;
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

	public static Vec3d atBottomCenterOf (Vec3i vec3i) {
		return Vec3d.atLowerCornerWithOffset(vec3i, 0.5, 0.0, 0.5);
	}

	public static Vec3d upFromBottomCenterOf (Vec3i vec3i, double d) {
		return Vec3d.atLowerCornerWithOffset(vec3i, 0.5, d, 0.5);
	}

	public static Vec3d directionFromRotation (Vec2 vec2) {
		return Vec3d.directionFromRotation(vec2.x, vec2.y);
	}

	public static Vec3d directionFromRotation (float f, float g) {
		float h = Mth.cos(-g * ((float)Math.PI / 180) - (float)Math.PI);
		float i = Mth.sin(-g * ((float)Math.PI / 180) - (float)Math.PI);
		float j = -Mth.cos(-f * ((float)Math.PI / 180));
		float k = Mth.sin(-f * ((float)Math.PI / 180));
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

	public double dot (Vec3d vec3D) {
		return this.x * vec3D.x + this.y * vec3D.y + this.z * vec3D.z;
	}

	public Vec3d cross (Vec3d vec3D) {
		return new Vec3d(this.y * vec3D.z - this.z * vec3D.y,
						 this.z * vec3D.x - this.x * vec3D.z,
						 this.x * vec3D.y - this.y * vec3D.x);
	}

	public Vec3d negated () {
		return new Vec3d(-x, -y, -z);
	}

	public Vec3d subtract (Vec3d vec3D) {
		return this.subtract(vec3D.x, vec3D.y, vec3D.z);
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

	public Vec3d add (Vec3d vec3D) {
		return this.add(vec3D.x, vec3D.y, vec3D.z);
	}

	public double distanceTo (Vec3d vec3D) {
		double d = vec3D.x - this.x;
		double e = vec3D.y - this.y;
		double f = vec3D.z - this.z;
		return Math.sqrt(d * d + e * e + f * f);
	}

	public double distanceToSqr (Vec3d vec3D) {
		double d = vec3D.x - this.x;
		double e = vec3D.y - this.y;
		double f = vec3D.z - this.z;
		return d * d + e * e + f * f;
	}

	public @NotNull Vec3d reverse () {
		return this.scale(-1.0);
	}

	public @NotNull Vec3d scale (double d) {
		return this.multiply(d, d, d);
	}

	public @NotNull Vec3d multiply (double d, double e, double f) {
		return new Vec3d(this.x * d, this.y * e, this.z * f);
	}

	public Vec3d multiply (Vec3d vec3D) {
		return this.multiply(vec3D.x, vec3D.y, vec3D.z);
	}

	public @NotNull Vec3d offsetRandom (RandomSource randomSource, float f) {
		return this.add((randomSource.nextFloat() - 0.5f) * f,
						(randomSource.nextFloat() - 0.5f) * f,
						(randomSource.nextFloat() - 0.5f) * f);
	}

	public boolean equals (Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Vec3d vec3D)) {
			return false;
		}
		if (Double.compare(vec3D.x, this.x) != 0) {
			return false;
		}
		if (Double.compare(vec3D.y, this.y) != 0) {
			return false;
		}
		return Double.compare(vec3D.z, this.z) == 0;
	}

	public Vec3d lerp (Vec3d vec3D, double d) {
		return new Vec3d(Mth.lerp(d, this.x, vec3D.x), Mth.lerp(d, this.y, vec3D.y), Mth.lerp(d, this.z, vec3D.z));
	}

	public @NotNull Vec3d xRot (float f) {
		float  g = Mth.cos(f);
		float  h = Mth.sin(f);
		double e = this.y * (double)g + this.z * (double)h;
		double i = this.z * (double)g - this.y * (double)h;
		return new Vec3d(this.x, e, i);
	}

	public @NotNull Vec3d yRot (float f) {
		float  g = Mth.cos(f);
		float  h = Mth.sin(f);
		double d = this.x * (double)g + this.z * (double)h;
		double i = this.z * (double)g - this.x * (double)h;
		return new Vec3d(d, this.y, i);
	}

	public @NotNull Vec3d zRot (float f) {
		float  g = Mth.cos(f);
		float  h = Mth.sin(f);
		double d = this.x * (double)g + this.y * (double)h;
		double e = this.y * (double)g - this.x * (double)h;
		return new Vec3d(d, e, this.z);
	}

	public @NotNull Vec3d align (EnumSet<Direction.Axis> enumSet) {
		double d = enumSet.contains(Direction.Axis.X) ? (double)Mth.floor(this.x): this.x;
		double e = enumSet.contains(Direction.Axis.Y) ? (double)Mth.floor(this.y): this.y;
		double f = enumSet.contains(Direction.Axis.Z) ? (double)Mth.floor(this.z): this.z;
		return new Vec3d(d, e, f);
	}

	public @NotNull Vec3d with (Direction.Axis axis, double d) {
		double e = axis == Direction.Axis.X ? d: this.x;
		double f = axis == Direction.Axis.Y ? d: this.y;
		double g = axis == Direction.Axis.Z ? d: this.z;
		return new Vec3d(e, f, g);
	}

	public @NotNull Vec3d relative (Direction direction, double d) {
		Vec3i vec3i = direction.getNormal();
		return new Vec3d(this.x + d * (double)vec3i.getX(),
						 this.y + d * (double)vec3i.getY(),
						 this.z + d * (double)vec3i.getZ());
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
