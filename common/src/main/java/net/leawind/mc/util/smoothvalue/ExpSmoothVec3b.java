package net.leawind.mc.util.smoothvalue;


import java.math.BigDecimal;

public class ExpSmoothVec3b extends ExpSmoothValue<BigDecimal> {
	public ExpSmoothVec3b () {
		value        = BigDecimal.ZERO;
		target       = BigDecimal.ZERO;
		smoothFactor = BigDecimal.ZERO;
	}

	/**
	 * @param tickTime 经过的时间（s）
	 */
	@Override
	public ExpSmoothValue<BigDecimal> update (double tickTime) {
		return null;
	}

	@Override
	public BigDecimal get (float delta) {
		return null;
	}

	@Override
	ExpSmoothValue<BigDecimal> setSmoothFactor (double smoothFactor) {
		return null;
	}

	/**
	 * 每隔 deltaTime 秒，value 变为原来的 multiplier 倍
	 */
	@Override
	ExpSmoothValue<BigDecimal> setSmoothFactor (BigDecimal multiplier, BigDecimal deltaTime) {
		return null;
	}
}
