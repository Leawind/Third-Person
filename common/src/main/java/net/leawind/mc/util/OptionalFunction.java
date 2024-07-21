package net.leawind.mc.util;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface OptionalFunction<T> {
	@Contract(value="_, _ -> new", pure=true)
	static <E> @NotNull OptionalFunction<E> of (Supplier<E> supplier, Supplier<Boolean> availablePredicate) {
		return new OptionalFunction<>() {
			@Override
			public E get () {
				return supplier.get();
			}

			@Override
			public boolean isAvailable () {
				return availablePredicate.get();
			}
		};
	}

	T get ();

	boolean isAvailable ();
}
