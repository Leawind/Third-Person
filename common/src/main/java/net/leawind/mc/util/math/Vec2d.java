package net.leawind.mc.util.math;


import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2d;
import org.joml.Vector2f;

@SuppressWarnings("unused")
@Deprecated
public class Vec2d {
	public static Vec2d  ZERO       = new Vec2d(0);
	public static Vec2d  ONE        = new Vec2d(1);
	public static Vec2d  UNIT_X     = new Vec2d(1, 0);
	public static Vec2d  NEG_UNIT_X = new Vec2d(-1, 0);
	public static Vec2d  UNIT_Y     = new Vec2d(0, 1);
	public static Vec2d  NEG_UNIT_Y = new Vec2d(0, -1);
	public static Vec2d  MAX        = new Vec2d(Double.MAX_VALUE);
	public static Vec2d  MIN        = new Vec2d(Double.MIN_VALUE);
	public final  double x;
	public final  double y;

	public Vec2d (double v) {
		this(v, v);
	}

	public Vec2d (double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vec2d (Vec2 v) {
		this(v.x, v.y);
	}

	public Vec2d (Vector2f v) {
		this(v.x, v.y);
	}

	public Vec2d (Vector2d v) {
		this(v.x, v.y);
	}

	public Vec2d vectorTo (Vec2d v) {
		return new Vec2d(v.x - x, v.y - y);
	}

	public Vec2d normalized () {
		return normalize();
	}

	public Vec2d normalize () {
		double d = length();
		if (d < 1e-4) {
			return ZERO;
		}
		return new Vec2d(x / d, y / d);
	}

	public double length () {
		return Math.sqrt(x * x + y * y);
	}

	public double dot (Vec2d v) {
		return v.x * x + v.y * y;
	}

	public double cross (Vec2d v) {
		return x * v.y - v.x * y;
	}

	public Vec2d negated () {
		return new Vec2d(-x, -y);
	}

	public Vec2d subtract (Vec2d v) {
		return subtract(v.x, v.y);
	}

	public Vec2d subtract (double x, double y) {
		return this.add(-x, -y);
	}

	public Vec2d add (double x, double y) {
		return new Vec2d(this.x + x, this.y + y);
	}

	public Vec2d add (double d) {
		return add(d, d);
	}

	public Vec2d add (Vec2d d) {
		return add(d.x, d.y);
	}

	public double distanceTo (Vec2d v) {
		double ex = v.x - x;
		double ey = v.y - y;
		return Math.sqrt(x * x + y * y);
	}

	public double distanceToSqr (Vec2d v) {
		double ex = v.x - x;
		double ey = v.y - y;
		return x * x + y * y;
	}

	public Vec2d reverse () {
		return negated();
	}

	public Vec2d scale (double d) {
		return multiply(d, d);
	}

	public Vec2d multiply (double d) {
		return multiply(d, d);
	}

	public Vec2d multiply (double x, double y) {
		return new Vec2d(this.x * x, this.y * y);
	}

	public Vec2d multiply (Vec2d v) {
		return multiply(v.x, v.y);
	}

	public Vec2d offsetRandom (RandomSource randomSource, float limit) {
		return this.add((randomSource.nextFloat() - 0.5f) * limit, (randomSource.nextFloat() - 0.5f) * limit);
	}

	public double lengthSqr () {
		return this.x * this.x + this.y * this.y;
	}

	public int hashCode () {
		long l = Double.doubleToLongBits(this.x);
		int  i = (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.y);
		i = 31 * i + (int)(l ^ l >>> 32);
		return i;
	}

	public boolean equals (Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Vec2d v)) {
			return false;
		}
		if (Double.compare(v.x, this.x) != 0) {
			return false;
		} else {
			return Double.compare(v.y, this.y) == 0;
		}
	}

	public String toString () {
		return "(" + this.x + ", " + this.y + ")";
	}

	public Vec2d lerp (Vec2d v, double d) {
		return new Vec2d(Mth.lerp(d, this.x, v.x), Mth.lerp(d, this.y, v.y));
	}

	public double x () {
		return 0;
	}

	public double y () {
		return 0;
	}

	public Vector2d toVector2d () {
		return new Vector2d(x, y);
	}

	public Vector2f toVector2f () {
		return new Vector2f((float)x, (float)y);
	}

	public Vec2 toVec2f () {
		return toVec2();
	}

	public Vec2 toVec2 () {
		return new Vec2((float)x, (float)y);
	}
}
