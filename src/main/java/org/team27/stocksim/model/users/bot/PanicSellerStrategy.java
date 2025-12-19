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
 * Panic Seller Strategy - Loss-averse behavior with quick exits.
 *
 * <p>The PanicSellerStrategy implements emotional, fear-driven trading behavior.
 * This bot is hypersensitive to losses and price drops, selling at the first
 * sign of trouble. It represents retail investors with low risk tolerance who
 * react emotionally to market volatility, often selling at the worst times.</p>
 *
 * <p><strong>Design Pattern:</strong> Strategy (concrete implementation)</p>
 * <ul>
 *   <li>Monitors holdings constantly for losses or price drops</li>
 *   <li>Sells immediately when panic threshold is triggered</li>
 *   <li>Very rarely buys (prefers holding cash)</li>
 *   <li>Low risk tolerance with quick loss realization</li>
 *   <li>Contributes to market volatility during downturns</li>
 * </ul>
 *
 * <h2>Panic Triggers:</h2>
 * <ul>
 *   <li>Price drops exceeding panic threshold (default: 2%)</li>
 *   <li>Any position showing unrealized losses</li>
 *   <li>Recent downward price trends</li>
 *   <li>Sells entire position when panicking</li>
 * </ul>
 *
 * <h2>Buying Behavior:</h2>
 * <ul>
 *   <li>Very cautious buying (0.5% probability)</li>
 *   <li>Small position sizes to minimize risk</li>
 *   <li>Only buys during calm markets</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Use default panic seller strategy
 * IBotStrategy strategy = new PanicSellerStrategy();
 *
 * // Custom panic seller with different thresholds
 * IBotStrategy strategy = new PanicSellerStrategy(
 *     new Random(),
 *     0.25,   // 25% check probability
 *     0.01,   // 1% panic threshold (even more nervous)
 *     3,      // 3-tick lookback
 *     0.01,   // 1% buy probability
 *     1,      // min quantity
 *     5       // max quantity
 * );
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see AbstractBotStrategy
 * @see IBotStrategy
 */
public class PanicSellerStrategy extends AbstractBotStrategy {

    /**
     * Probability of checking holdings for panic conditions each tick.
     */
    private final double checkProbability;

    /**
     * Price drop percentage that triggers panic selling (e.g., 0.02 = 2%).
     */
    private final double panicThreshold;

    /**
     * Number of historical ticks to analyze for price drops.
     */
    private final int lookbackPeriod;

    /**
     * Very low probability of buying (panic sellers prefer cash).
     */
    private final double buyProbability;

    /**
     * Constructs a PanicSellerStrategy with default parameters.
     * <ul>
     *   <li>Check probability: 15%</li>
     *   <li>Panic threshold: 2% price drop</li>
     *   <li>Lookback period: 5 ticks</li>
     *   <li>Buy probability: 0.5%</li>
     *   <li>Quantity range: 1-3 (small positions)</li>
     * </ul>
     */
    public PanicSellerStrategy() {
        this(new Random(), 0.15, 0.02, 5, 0.005, 1, 3);
    }

    /**
     * Constructs a PanicSellerStrategy with custom parameters.
     *
     * @param random Random number generator
     * @param checkProbability Probability of checking holdings per tick
     * @param panicThreshold Minimum price drop to trigger panic (e.g., 0.02 = 2%)
     * @param lookbackPeriod Number of ticks to analyze
     * @param buyProbability Probability of buying per tick
     * @param minQuantity Minimum order quantity
     * @param maxQuantity Maximum order quantity
     */
    public PanicSellerStrategy(Random random, double checkProbability, double panicThreshold,
            int lookbackPeriod, double buyProbability, int minQuantity, int maxQuantity) {
        super(random, minQuantity, maxQuantity);
        this.checkProbability = checkProbability;
        this.panicThreshold = panicThreshold;
        this.lookbackPeriod = lookbackPeriod;
        this.buyProbability = buyProbability;
    }

    @Override
    public List<Order> decide(StockSim model, Bot bot) {
        List<Order> orders = new ArrayList<>();
        if (random.nextDouble() > checkProbability) {
            return orders;
        }

        // Check for positions that are losing money or dropping
        Order order = panicSell(model, bot);
        if (order != null) {
            orders.add(order);
            return orders;
        }

        // Very rarely buy (panic sellers are cautious)
        if (random.nextDouble() < buyProbability) {
            order = cautiousBuy(model, bot);
            if (order != null) {
                orders.add(order);
            }
        }

        return orders;
    }

    private Order panicSell(StockSim model, Bot bot) {
        HashMap<String, Integer> holdings = new HashMap<>(bot.getPortfolio().getStockHoldings());
        if (holdings.isEmpty()) {
            return null;
        }

        List<String> panicSymbols = new ArrayList<>();
        for (String symbol : holdings.keySet()) {
            InstrumentDTO stock = model.getStocks().get(symbol);
            if (stock == null)
                continue;

            if (hasPriceDropped(stock, lookbackPeriod, panicThreshold) || isLosingMoney(bot, symbol)) {
                panicSymbols.add(symbol);
            }
        }

        if (panicSymbols.isEmpty()) {
            return null;
        }

        String symbol = panicSymbols.get(random.nextInt(panicSymbols.size()));
        InstrumentDTO stock = model.getStocks().get(symbol);
        if (stock == null)
            return null;

        int quantity = holdings.get(symbol); // Sell ALL shares
        BigDecimal price = stock.getPrice()
                .multiply(BigDecimal.valueOf(0.96 + random.nextDouble() * 0.02)) // 2-4% discount
                .setScale(2, java.math.RoundingMode.HALF_UP);

        return createSellOrder(model, bot, symbol, quantity, price);
    }

    private boolean isLosingMoney(Bot bot, String symbol) {
        // This is a simplified check - we could enhance with current price from model
        // For now, we'll rely on hasPriceDropped in the calling code
        return false;
    }

    private Order cautiousBuy(StockSim model, Bot bot) {
        HashMap<String, InstrumentDTO> stocks = model.getStocks();
        if (stocks == null || stocks.isEmpty()) {
            return null;
        }

        List<InstrumentDTO> safeStocks = new ArrayList<>();
        for (InstrumentDTO stock : stocks.values()) {
            if (!hasPriceDropped(stock, lookbackPeriod, panicThreshold)) {
                safeStocks.add(stock);
            }
        }

        if (safeStocks.isEmpty()) {
            return null;
        }

        InstrumentDTO stock = safeStocks.get(random.nextInt(safeStocks.size()));
        int quantity = randomQuantity();
        BigDecimal price = calculatePriceWithVariation(stock.getPrice(), 0.005);

        return createBuyOrder(model, bot, stock, quantity, price);
    }
}
