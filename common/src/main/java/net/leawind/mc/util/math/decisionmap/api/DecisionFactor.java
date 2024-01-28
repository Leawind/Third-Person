package net.leawind.mc.util.math.decisionmap.api;


import net.leawind.mc.util.math.decisionmap.impl.DecisionFactorImpl;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

/**
 * 用于标记因素
 */
public interface DecisionFactor {
	static @NotNull DecisionFactor of (BooleanSupplier getter) {
		return new DecisionFactorImpl(getter);
	}

	boolean get ();

	DecisionFactor update ();

	String getName ();

	void setName (String name);

	void setIndex (int index);

	default int mask () {
		return 1 << index();
	}

	int index ();
}
