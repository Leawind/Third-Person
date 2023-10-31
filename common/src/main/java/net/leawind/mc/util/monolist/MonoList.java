package net.leawind.mc.util.monolist;


import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.function.Function;

/**
 * TODO
 * <p>
 * 单调递增列表
 * <p>
 * 数列中每一项的值会在实例化时被创建。
 */
public class MonoList {
	protected double[] list;
	public    boolean  isReversed = false;

	/**
	 * @param length 列表长度
	 * @param getter 值与下标的对应关系
	 */
	private MonoList (int length, Function<Integer, Double> getter, boolean needToSort) {
		this.list = new double[length];
		for (int i = 0; i < length; i++) {
			list[i] = getter.apply(i);
		}
		if (needToSort) {
			sort();
		}
	}

	private MonoList (double[] list, boolean needToSort) {
		this.list = list;
		if (needToSort) {
			sort();
		}
	}

	private MonoList () {
	}

	public static MonoList exp (int length) {
		return new MonoList(length, Math::exp, true);
	}

	public static MonoList squared (int length) {
		return new MonoList(length, i -> (double)(i * i), true);
	}

	public static MonoList of (int length, double min, double max, Function<Double, Double> f, Function<Double, Double> fInv) {
		double xmin   = fInv.apply(min);
		double xrange = fInv.apply(max) - xmin;
		return new MonoList(length, i -> f.apply(i * xrange / length + xmin), true);
	}

	public static MonoList of (double[] list) {//TODO
		return new MonoList(list, true);
	}

	public boolean isSorted () {
		double lastValue = list[0];
		for (double value: list) {
			if (value < lastValue) {
				return false;
			}
		}
		return true;
	}

	public MonoList sort () {
		Arrays.sort(list);
		return this;
	}

	protected int sgn () {
		return isReversed ? -1: 1;
	}

	/**
	 * 插入一个值到列表中
	 */
	public MonoList insert (double v) {
		double[] newList = Arrays.copyOf(list, list.length + 1);
		newList[list.length] = v;
							   list = newList;
		sort();
		return this;
	}

	public int length () {
		return list.length;
	}

	public double get (int i) {
		return list[i];
	}

	public double getNext (double value) {
		return offset(value, 1);
	}

	public double getLast (double value) {
		return offset(value, -1);
	}

	public double offset (double value, int offset) {
		int i = iadsorption(value) + offset * sgn();
		i = Mth.clamp(i, 0, length() - 1);
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
		int iright  = length() - 1;
		int icenter = length() / 2;
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
