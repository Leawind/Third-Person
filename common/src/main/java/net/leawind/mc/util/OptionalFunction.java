package net.leawind.mc.util;


import java.util.function.Supplier;

public interface OptionalFunction<T> {
	static <E> OptionalFunction<E> of (Supplier<E> supplier, Supplier<Boolean> availablePredicate) {
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
