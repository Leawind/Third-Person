package net.leawind.mc.util.impl.math.monolist;


import net.leawind.mc.util.api.math.MonoList;
import net.leawind.mc.util.math.LMath;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * 延迟计算的单调列表
 * <p>
 * 列表项是下标的单调函数
 * <p>
 * 数列中每一项的值只有在被访问时才计算
 */
@SuppressWarnings("unused")
public class DeferedMonoList implements MonoList {
	private final          int                       length;
	private final @NotNull Function<Integer, Double> getter;
	private final          int                       sgn;

	/**
	 * @param length 列表长度
	 * @param getter 值与下标的对应关系
	 */
	protected DeferedMonoList (int length, @NotNull Function<Integer, Double> getter) {
		this.length = length;
		this.getter = getter;
		this.sgn    = getter.apply(1) > getter.apply(0) ? 1: -1;
	}

	public static @NotNull DeferedMonoList exp (int length) {
		return new DeferedMonoList(length, Math::exp);
	}

	public static @NotNull DeferedMonoList squared (int length) {
		return new DeferedMonoList(length, i -> (double)(i * i));
	}

	public static @NotNull DeferedMonoList of (int length, @NotNull Function<Integer, Double> getter) {
		return new DeferedMonoList(length, getter);
	}

	public double offset (double value, int offset) {
		int i = iadsorption(value) + offset;
		i = LMath.clamp(i, 0, length() - 1);
		return get(i);
	}

	public int iadsorption (double value) {
		int ileft   = 0;
		int iright  = length() - 1;
		int icenter = length() / 2;
		while (true) {
			double vi = get(icenter);
			if (vi < value) {
				ileft = icenter;
			} else if (vi > value) {
				iright = icenter;
			} else {
				return icenter;
			}
			if (iright - ileft == 1) {
				break;
			}
			icenter = (ileft + iright) / 2;
		}
		double emin = value - get(ileft);
		double emax = get(iright) - value;
		if (emin <= emax) {
			return ileft;
		} else {
			return iright;
		}
	}

	public double get (int i) {
		return getter.apply(i);
	}

	public double adsorption (double value) {
		return get(iadsorption(value));
	}

	@Override
	public int sgn () {
		return sgn;
	}

	public int length () {
		return length;
	}

	@Override
	public double getNext (double value) {
		return offset(value, 1);
	}

	@Override
	public double getLast (double value) {
		return offset(value, -1);
	}
}
