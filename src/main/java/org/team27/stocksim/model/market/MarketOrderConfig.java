package org.team27.stocksim.model.market;

import java.math.BigDecimal;

/**
 * Configuration for market order behavior and constraints.
 *
 * <p>MarketOrderConfig defines the rules and limits for market order execution,
 * particularly the maximum price deviation allowed from the last traded price.
 * This prevents market orders from executing at unreasonable prices during
 * volatile or illiquid market conditions.</p>
 *
 * <p><strong>Design Pattern:</strong> Configuration Object + Immutable Value Object</p>
 * <ul>
 *   <li>Immutable configuration prevents mid-execution changes</li>
 *   <li>Validates parameters at construction</li>
 *   <li>Provides sensible defaults via factory method</li>
 *   <li>Controls market order execution limits</li>
 * </ul>
 *
 * <h2>Configuration Parameters:</h2>
 * <ul>
 *   <li><strong>Max Price Deviation:</strong> Maximum % price can deviate from last trade (default: 10%)</li>
 *   <li><strong>Allow Partial Fills:</strong> Whether market orders can partially fill (default: true)</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Use default configuration (10% max deviation, partial fills allowed)
 * MarketOrderConfig defaultConfig = MarketOrderConfig.createDefault();
 *
 * // Custom configuration with tighter controls
 * MarketOrderConfig strictConfig = new MarketOrderConfig(
 *     new BigDecimal("0.05"),  // 5% max deviation
 *     false                     // No partial fills
 * );
 *
 * // Use in matching engine
 * MatchingEngine engine = new MatchingEngine(strictConfig);
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see MatchingEngine
 * @see Order
 */
public class MarketOrderConfig {
    /**
     * Maximum price deviation from last trade as decimal (e.g., 0.10 = 10%).
     */
    private final BigDecimal maxPriceDeviation;

    /**
     * Whether market orders can be partially filled.
     */
    private final boolean allowPartialFills;

    /**
     * Constructs a MarketOrderConfig with specified parameters.
     *
     * @param maxPriceDeviation Maximum price deviation (must be non-negative)
     * @param allowPartialFills Whether to allow partial order fills
     * @throws IllegalArgumentException if maxPriceDeviation is negative
     */
    public MarketOrderConfig(BigDecimal maxPriceDeviation, boolean allowPartialFills) {
        if (maxPriceDeviation == null || maxPriceDeviation.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Max price deviation must be non-negative");
        }
        this.maxPriceDeviation = maxPriceDeviation;
        this.allowPartialFills = allowPartialFills;
    }

    /**
     * Creates a default configuration with standard market parameters.
     *
     * <ul>
     *   <li>Max price deviation: 10%</li>
     *   <li>Partial fills: Allowed</li>
     * </ul>
     *
     * @return Default MarketOrderConfig
     */
    public static MarketOrderConfig createDefault() {
        return new MarketOrderConfig(new BigDecimal("0.10"), true);
    }

    /**
     * Gets the maximum price deviation allowed for market orders.
     *
     * @return Max deviation as decimal (e.g., 0.10 = 10%)
     */
    public BigDecimal getMaxPriceDeviation() {
        return maxPriceDeviation;
    }

    /**
     * Checks if partial fills are allowed for market orders.
     *
     * @return true if partial fills allowed, false otherwise
     */
    public boolean isAllowPartialFills() {
        return allowPartialFills;
    }

    @Override
    public String toString() {
        return "MarketOrderConfig{maxDeviation=" + maxPriceDeviation +
               ", allowPartialFills=" + allowPartialFills + "}";
    }
}

