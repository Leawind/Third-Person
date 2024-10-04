package com.github.leawind.util;


import java.util.function.Consumer;

public final class FiniteChecker {
	private final Consumer<InfiniteException> printer;
	private       boolean                     failedOnce = false;

	public FiniteChecker (Consumer<InfiniteException> printer) {
		this.printer = printer;
	}

	public void reset () {
		failedOnce = false;
	}

	public boolean checkOnce (Object... objects) {
		for (int i = 0; i < objects.length; i++) {
			var obj = objects[i];
			if (notFinite(obj)) {
				if (!failedOnce) {
					var err = new InfiniteException(String.format("objects[%d] is not finite: %s", i, obj));
					printer.accept(err);
					failedOnce = true;
				}
				return true;
			}
		}
		return false;
	}

	public boolean check (Object... objects) {
		for (int i = 0; i < objects.length; i++) {
			var obj = objects[i];
			if (notFinite(obj)) {
				var err = new InfiniteException(String.format("objects[%d] is not finite: %s", i, obj));
				printer.accept(err);
				failedOnce = true;
				return true;
			}
		}
		return false;
	}

	private static boolean isFinite (Object obj) {
		return (obj instanceof Float && Float.isFinite((Float)obj)) || (obj instanceof Double && Double.isFinite((Double)obj));
	}

	private static boolean notFinite (Object obj) {
		return !isFinite(obj);
	}

	public static class InfiniteException extends RuntimeException {
		private final String msg;

		public InfiniteException (String msg) {
			this.msg = msg;
		}

		@Override
		public String toString () {
			StringBuilder s = new StringBuilder("InfiniteException:\n");
			s.append("\t").append(msg).append("\n");
			var trace = Thread.currentThread().getStackTrace();
			for (StackTraceElement traceElement: trace) {
				s.append("\tat ").append(traceElement).append("\n");
			}
			return s.toString();
		}
	}
}
