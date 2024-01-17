package net.leawind.mc.math.monolist;


import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public interface MonoList {
	static @NotNull List<Double> arrange (double start, double end, double step) {
		int length = (int)((end - start) / step);
		if (length <= 0) {
			return List.of();
		} else {
			return arrange(start, end, length);
		}
	}

	static @NotNull List<Double> arrange (double start, double end, int length) {
		LinkedList<Double> list = new LinkedList<>();
		double             step = (end - start) / length;
		for (int i = 0; i < length; i++) {
			list.add(start + step * i);
		}
		return list;
	}
}
