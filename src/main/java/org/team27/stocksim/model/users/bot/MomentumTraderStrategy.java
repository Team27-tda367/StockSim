package org.team27.stocksim.model.users.bot;

import org.team27.stocksim.dto.InstrumentDTO;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.market.Order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Momentum Trader Strategy - Follows price trends and momentum signals.
 *
 * <p>The MomentumTraderStrategy implements a technical analysis approach that
 * "buys high and sells higher." It identifies stocks with upward price momentum
 * and quickly exits positions that show downward trends. This strategy is based
 * on the principle that stocks in motion tend to stay in motion.</p>
 *
 * <p><strong>Design Pattern:</strong> Strategy (concrete implementation)</p>
 * <ul>
 *   <li>Analyzes price history over configurable lookback period</li>
 *   <li>Calculates momentum based on price change percentage</li>
 *   <li>Sells falling stocks immediately to cut losses</li>
 *   <li>Buys stocks showing strong upward momentum</li>
 *   <li>Prioritizes sell decisions over buy decisions</li>
 * </ul>
 *
 * <h2>Trading Logic:</h2>
 * <ol>
 *   <li>Check action probability (default: 8% per tick)</li>
 *   <li>First, scan holdings for falling stocks and sell</li>
 *   <li>If no sells, look for momentum opportunities to buy</li>
 *   <li>Calculate momentum using lookback period (default: 10 ticks)</li>
 *   <li>Only trade if momentum exceeds threshold (default: 3%)</li>
 * </ol>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Use default momentum strategy
 * IBotStrategy strategy = new MomentumTraderStrategy();
 *
 * // Custom momentum trader with tighter parameters
 * IBotStrategy strategy = new MomentumTraderStrategy(
 *     new Random(),
 *     0.15,  // 15% action probability
 *     5,     // 5-tick lookback
 *     0.02,  // 2% momentum threshold
 *     5,     // min quantity
 *     25     // max quantity
 * );
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see AbstractBotStrategy
 * @see IBotStrategy
 */
public class MomentumTraderStrategy extends AbstractBotStrategy {

    /**
     * Probability of taking action on each tick.
     */
    private final double actionProbability;

    /**
     * Number of historical price points to analyze for momentum.
     */
    private final int lookbackPeriod;

    /**
     * Minimum price change percentage to trigger action (e.g., 0.03 = 3%).
     */
    private final double momentumThreshold;

    /**
     * Constructs a MomentumTraderStrategy with default parameters.
     * <ul>
     *   <li>Action probability: 8%</li>
     *   <li>Lookback period: 10 ticks</li>
     *   <li>Momentum threshold: 3%</li>
     *   <li>Quantity range: 1-12</li>
     * </ul>
     */
    public MomentumTraderStrategy() {
        this(new Random(), 0.08, 10, 0.03, 1, 12);
    }

    /**
     * Constructs a MomentumTraderStrategy with custom parameters.
     *
     * @param random Random number generator
     * @param actionProbability Probability of action per tick (0.0-1.0)
     * @param lookbackPeriod Number of historical ticks to analyze
     * @param momentumThreshold Minimum price change to trigger (e.g., 0.03 = 3%)
     * @param minQuantity Minimum order quantity
     * @param maxQuantity Maximum order quantity
     */
    public MomentumTraderStrategy(Random random, double actionProbability, int lookbackPeriod,
            double momentumThreshold, int minQuantity, int maxQuantity) {
        super(random, minQuantity, maxQuantity);
        this.actionProbability = actionProbability;
        this.lookbackPeriod = lookbackPeriod;
        this.momentumThreshold = momentumThreshold;
    }

    @Override
    public List<Order> decide(StockSim model, Bot bot) {
        List<Order> orders = new ArrayList<>();
        if (random.nextDouble() > actionProbability) {
            return orders;
        }

        // First check if we should sell any falling positions
        Order order = sellFalling(model, bot);
        if (order != null) {
            orders.add(order);
            return orders;
        }

        // Then look for momentum to buy
        order = buyMomentum(model, bot);
        if (order != null) {
            orders.add(order);
        }

        return orders;
    }

    private Order sellFalling(StockSim model, Bot bot) {
        HashMap<String, Integer> holdings = new HashMap<>(bot.getPortfolio().getStockHoldings());
        if (holdings.isEmpty()) {
            return null;
        }

        List<String> fallingStocks = new ArrayList<>();
        for (String symbol : holdings.keySet()) {
            InstrumentDTO stock = model.getStocks().get(symbol);
            if (stock != null && hasPriceDropped(stock, lookbackPeriod, momentumThreshold)) {
                fallingStocks.add(symbol);
            }
        }

        if (fallingStocks.isEmpty()) {
            return null;
        }

        String symbol = fallingStocks.get(random.nextInt(fallingStocks.size()));
        InstrumentDTO stock = model.getStocks().get(symbol);
        if (stock == null)
            return null;

        int maxAvailable = bot.getPortfolio().getStockQuantity(symbol);
        int quantity = Math.min(randomQuantity(), maxAvailable);
        BigDecimal price = calculatePriceWithVariation(stock.getPrice(), 0.01);

        return createSellOrder(model, bot, symbol, quantity, price);
    }

    private Order buyMomentum(StockSim model, Bot bot) {
        HashMap<String, InstrumentDTO> stocks = model.getStocks();
        if (stocks == null || stocks.isEmpty()) {
            return null;
        }

        List<InstrumentDTO> momentumStocks = new ArrayList<>();
        for (InstrumentDTO stock : stocks.values()) {
            if (hasPriceRisen(stock, lookbackPeriod, momentumThreshold)) {
                momentumStocks.add(stock);
            }
        }

        if (momentumStocks.isEmpty()) {
            return null;
        }

        InstrumentDTO stock = momentumStocks.get(random.nextInt(momentumStocks.size()));
        int quantity = randomQuantity();
        BigDecimal price = calculatePriceWithVariation(stock.getPrice(), 0.015);

        return createBuyOrder(model, bot, stock, quantity, price);
    }

}
