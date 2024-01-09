package net.leawind.mc.util.monolist;


import java.util.LinkedList;
import java.util.List;

public interface MonoList {
	static List<Double> arrange (double start, double end, double step) {
		int length = (int)((end - start) / step);
		if (length <= 0) {
			return List.of();
		} else {
			return arrange(start, end, length);
		}
	}

	static List<Double> arrange (double start, double end, int length) {
		LinkedList<Double> list = new LinkedList<>();
		double             step = (end - start) / length;
		for (int i = 0; i < length; i++) {
			list.add(start + step * i);
		}
		return list;
	}
}
