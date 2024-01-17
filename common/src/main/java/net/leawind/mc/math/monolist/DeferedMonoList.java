package net.leawind.mc.math.monolist;


import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * 延迟计算的单调列表
 * <p>
 * 列表项是下标的单调函数
 * <p>
 * 数列中每一项的值会在 使用时 而非 实例化时 计算
 */
@SuppressWarnings("unused")
public class DeferedMonoList {
	public             int                       length;
	protected @NotNull Function<Integer, Double> getter;

	/**
	 * @param length 列表长度
	 * @param getter 值与下标的对应关系
	 */
	private DeferedMonoList (int length, @NotNull Function<Integer, Double> getter) {
		this.length = length;
		this.getter = getter;
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
		i = Mth.clamp(i, 0, length - 1);
		return get(i);
	}

	/**
	 * 取最接近的一个值的下标
	 */
	public int iadsorption (double value) {
		int ileft   = 0;
		int iright  = length - 1;
		int icenter = length / 2;
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

	/**
	 * 找最近的一个值
	 */
	public double adsorption (double value) {
		return get(iadsorption(value));
	}
}
