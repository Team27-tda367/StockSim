package org.team27.stocksim.model.market;

import java.math.BigDecimal;


public class MarketOrderConfig {
    private final BigDecimal maxPriceDeviation;
    private final boolean allowPartialFills;


    public MarketOrderConfig(BigDecimal maxPriceDeviation, boolean allowPartialFills) {
        if (maxPriceDeviation == null || maxPriceDeviation.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Max price deviation must be non-negative");
        }
        this.maxPriceDeviation = maxPriceDeviation;
        this.allowPartialFills = allowPartialFills;
    }
    
    public static MarketOrderConfig createDefault() {
        return new MarketOrderConfig(new BigDecimal("0.10"), true);
    }

    public BigDecimal getMaxPriceDeviation() {
        return maxPriceDeviation;
    }

    public boolean isAllowPartialFills() {
        return allowPartialFills;
    }

    @Override
    public String toString() {
        return "MarketOrderConfig{maxDeviation=" + maxPriceDeviation +
               ", allowPartialFills=" + allowPartialFills + "}";
    }
}

