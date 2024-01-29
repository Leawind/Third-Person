package net.leawind.mc.util.math.monolist;


/**
 * 单调列表
 * <p>
 * 列表中的数据是单调递增或单调递减的
 */
@SuppressWarnings("unused")
public interface MonoList {
	/**
	 * 获取下标对应的值
	 */
	double get (int i);

	double offset (double value, int offset);

	/**
	 * 取最接近的一个值的下标
	 */
	int iadsorption (double value);

	/**
	 * 找最近的一个值
	 */
	double adsorption (double value);

	/**
	 * 获取指定值的下一个值
	 * <p>
	 * 如果该值已经位于最后一个区间，那么直接返回最后一个值
	 */
	double getNext (double value);

	/**
	 * 获取指定值的上一个值
	 * <p>
	 * 如果该值已经位于第一个区间，那么直接返回第一个值
	 */
	double getLast (double value);

	/**
	 * @return 如果单调递增则为1，单调递减则为-1
	 */
	int sgn ();

	/**
	 * @return 列表长度
	 */
	int length ();
}
