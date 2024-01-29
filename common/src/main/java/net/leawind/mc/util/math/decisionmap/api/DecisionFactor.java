package net.leawind.mc.util.math.decisionmap.api;


import net.leawind.mc.util.math.decisionmap.impl.DecisionFactorImpl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

/**
 * 用于标记因素
 */
public interface DecisionFactor {
	@Contract("_ -> new")
	static @NotNull DecisionFactor of (BooleanSupplier getter) {
		return new DecisionFactorImpl(getter);
	}

	boolean get ();

	@Contract("-> this")
	DecisionFactor update ();

	@NotNull String getName ();

	void setName (String name);

	void setIndex (int index);

	default int mask () {
		return 1 << index();
	}

	int index ();
}
