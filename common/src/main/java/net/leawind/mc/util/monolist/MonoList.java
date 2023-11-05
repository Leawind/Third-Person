package net.leawind.mc.util.monolist;


import net.minecraft.util.Mth;

import java.util.function.Function;

/**
 * 单调列表
 * <p>
 * 列表项是下标的单调函数
 * <p>
 * 数列中每一项的值会在实例化时被创建。
 */
public class MonoList {
	public    int      length;
	protected double[] list;

	/**
	 * @param length 列表长度
	 * @param getter 值与下标的对应关系
	 */
	public MonoList (int length, Function<Integer, Double> getter) {
		this.length = length;
		this.list   = new double[length];
		for (int i = 0; i < length; i++) {
			list[i] = getter.apply(i);
		}
	}

	public static MonoList exp (int length) {
		return new MonoList(length, Math::exp);
	}

	public static MonoList squared (int length) {
		return new MonoList(length, i -> (double)(i * i));
	}

	public double get (int i) {
		return list[i];
	}

	public double offset (double value, int offset) {
		int i = iadsorption(value) + offset;
		i = Mth.clamp(i, 0, length - 1);
		return list[i];
	}

	/**
	 * 找最近的一个值
	 */
	public double adsorption (double value) {
		return list[iadsorption(value)];
	}

	/**
	 * 取最接近的一个值的下标
	 */
	public int iadsorption (double value) {
		int ileft   = 0;
		int iright  = length - 1;
		int icenter = length / 2;
		while (true) {
			if (list[icenter] < value) {
				ileft = icenter;
			} else if (list[icenter] > value) {
				iright = icenter;
			} else {
				return icenter;
			}
			if (iright - ileft == 1) {
				break;
			}
			icenter = (ileft + iright) / 2;
		}
		double emin = value - list[ileft], emax = list[iright] - value;
		if (emin <= emax) {
			return ileft;
		} else {
			return iright;
		}
	}
}
