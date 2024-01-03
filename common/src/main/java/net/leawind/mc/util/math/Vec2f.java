package net.leawind.mc.util.math;


import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;

@SuppressWarnings("unused")
public class Vec2f extends Vec2 {
	public static final Vec2f ZERO       = new Vec2f(0.0f, 0.0f);
	public static final Vec2f ONE        = new Vec2f(1.0f, 1.0f);
	public static final Vec2f UNIT_X     = new Vec2f(1.0f, 0.0f);
	public static final Vec2f NEG_UNIT_X = new Vec2f(-1.0f, 0.0f);
	public static final Vec2f UNIT_Y     = new Vec2f(0.0f, 1.0f);
	public static final Vec2f NEG_UNIT_Y = new Vec2f(0.0f, -1.0f);
	public static final Vec2f MAX        = new Vec2f(Float.MAX_VALUE, Float.MAX_VALUE);
	public static final Vec2f MIN        = new Vec2f(Float.MIN_VALUE, Float.MIN_VALUE);

	public static Vec2f of (Vec2 v) {
		return new Vec2f(v.x, v.y);
	}

	public Vec2f (Vector2f v) {
		this(v.x, v.y);
	}

	public Vec2f (Vector2d v) {
		this(v.x, v.y);
	}

	public Vec2f (float f) {
		this(f, f);
	}

	public Vec2f (double f) {
		this(f, f);
	}

	public Vec2f (double x, double y) {
		this((float)x, (float)y);
	}

	public Vec2f (float x, float y) {
		super(x, y);
	}

	public Vec2f normalize () {
		return normalized();
	}

	@Override
	public @NotNull Vec2f normalized () {
		return (Vec2f)super.normalized();
	}

	public float cross (Vec2f v) {
		return x * v.y - v.x * y;
	}

	@Override
	public @NotNull Vec2f scale (float f) {
		return new Vec2f(x * f, y * f);
	}

	public Vec2f subtract (Vec2f v) {
		return subtract(v.x, v.y);
	}

	public Vec2f subtract (float x, float y) {
		return add(-x, -y);
	}

	public Vec2f add (float x, float y) {
		return new Vec2f(this.x + x, this.y + y);
	}

	public Vec2f add (Vec2f v) {
		return new Vec2f(x + v.x, y + v.y);
	}

	@Override
	public @NotNull Vec2f add (float f) {
		return new Vec2f(x + f, y + f);
	}

	public Vec2f reverse () {
		return negated();
	}

	@Override
	public @NotNull Vec2f negated () {
		return new Vec2f(-x, -y);
	}

	public float distanceTo (Vec2f v) {
		double ex = v.x - x;
		double ey = v.y - y;
		return (float)Math.sqrt(ex * ex + ey * ey);
	}

	public Vec2f multiply (float f) {
		return multiply(f, f);
	}

	public Vec2f multiply (Vec2f v) {
		return multiply(v.x, v.y);
	}

	public Vec2f multiply (float x, float y) {
		return new Vec2f(this.x * x, this.y * y);
	}

	public Vec2f offsetRandom (RandomSource randomSource, float limit) {
		return add((randomSource.nextFloat() - 0.5f) * limit, (randomSource.nextFloat() - 0.5f) * limit);
	}

	public float lengthSqr () {
		return x * x + y * y;
	}

	public int hashCode () {
		int ff = Float.floatToIntBits(x);
		int i  = (ff ^ ff >>> 16);
		ff = Float.floatToIntBits(y);
		i  = 15 * i + (ff ^ ff >>> 16);
		return i;
	}

	public boolean equals (Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Vec2f v)) {
			return false;
		}
		if (Float.compare(v.x, this.x) != 0) {
			return false;
		} else {
			return Float.compare(v.y, this.y) == 0;
		}
	}

	public String toString () {
		return "(" + x + ", " + y + ")";
	}

	public Vec2f lerp (Vec2f v, double d) {
		return new Vec2f(Mth.lerp(d, x, v.x), Mth.lerp(d, y, v.y));
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
		return new Vector2f(x, y);
	}

	public Vec2d toVec2d () {
		return new Vec2d(x, y);
	}
}
