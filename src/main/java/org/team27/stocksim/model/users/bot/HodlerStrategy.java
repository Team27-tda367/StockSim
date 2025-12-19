package org.team27.stocksim.model.users.bot;

import org.team27.stocksim.dto.InstrumentDTO;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.users.Bot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.team27.stocksim.model.market.Order;

/**
 * HODL Strategy - Buy and hold for long-term gains.
 *
 * <p>The HodlerStrategy implements a classic "buy and hold" investment approach,
 * inspired by long-term cryptocurrency investors. This bot believes in long-term
 * growth and rarely sells, only liquidating positions after massive gains
 * (default: 50% profit threshold).</p>
 *
 * <p><strong>Design Pattern:</strong> Strategy (concrete implementation)</p>
 * <ul>
 *   <li>Accumulates positions gradually over time</li>
 *   <li>Extremely rare selling (0.1% probability per tick)</li>
 *   <li>Only sells after configurable profit threshold</li>
 *   <li>Partial position exits to maintain holdings</li>
 *   <li>Diamond hands mentality - holds through volatility</li>
 * </ul>
 *
 * <h2>Behavior Characteristics:</h2>
 * <ul>
 *   <li>Buy probability: 1% per tick (default)</li>
 *   <li>Sell consideration: 0.1% per tick</li>
 *   <li>Sell threshold: 50% profit minimum (default)</li>
 *   <li>When selling, only liquidates half the position</li>
 *   <li>Never panic sells on losses</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Use default hodler strategy
 * IBotStrategy strategy = new HodlerStrategy();
 *
 * // Custom hodler with higher activity and lower sell threshold
 * IBotStrategy strategy = new HodlerStrategy(
 *     new Random(),
 *     0.05,  // 5% buy probability
 *     0.30,  // Sell after 30% gains
 *     10,    // min quantity
 *     100    // max quantity
 * );
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see AbstractBotStrategy
 * @see IBotStrategy
 */
public class HodlerStrategy extends AbstractBotStrategy {

    /**
     * Probability of attempting to buy on each tick.
     */
    private final double buyProbability;

    /**
     * Minimum profit percentage required before considering a sell (e.g., 0.5 = 50%).
     */
    private final double sellThreshold;

    /**
     * Constructs a HodlerStrategy with default parameters.
     * <ul>
     *   <li>Buy probability: 1%</li>
     *   <li>Sell threshold: 50% gains</li>
     *   <li>Quantity range: 5-20</li>
     * </ul>
     */
    public HodlerStrategy() {
        this(new Random(), 0.01, 0.5, 5, 20);
    }

    /**
     * Constructs a HodlerStrategy with custom parameters.
     *
     * @param random Random number generator
     * @param buyProbability Probability of buying per tick (0.0-1.0)
     * @param sellThreshold Minimum profit threshold to sell (e.g., 0.5 = 50%)
     * @param minQuantity Minimum order quantity
     * @param maxQuantity Maximum order quantity
     */
    public HodlerStrategy(Random random, double buyProbability, double sellThreshold,
            int minQuantity, int maxQuantity) {
        super(random, minQuantity, maxQuantity);
        this.buyProbability = buyProbability;
        this.sellThreshold = sellThreshold;
    }

    @Override
    public List<Order> decide(StockSim model, Bot bot) {
        List<Order> orders = new ArrayList<>();
        // Hodlers mostly buy and rarely sell
        Order order = null;
        if (random.nextDouble() < buyProbability) {
            order = buy(model, bot);
        } else if (random.nextDouble() < 0.001) { // Very rarely check to sell
            order = sellIfMassiveGains(model, bot);
        }

        if (order != null) {
            orders.add(order);
        }

        return orders;
    }

    private Order buy(StockSim model, Bot bot) {
        InstrumentDTO stock = pickRandomStock(model);
        if (stock == null) {
            return null;
        }

        int quantity = randomQuantity();
        BigDecimal price = calculatePriceWithVariation(stock.getPrice(), 0.01);

        return createBuyOrder(model, bot, stock, quantity, price);
    }

    private Order sellIfMassiveGains(StockSim model, Bot bot) {
        List<String> massiveGainers = findProfitableHoldings(model, bot, sellThreshold);

        if (massiveGainers.isEmpty()) {
            return null;
        }

        String symbol = massiveGainers.get(random.nextInt(massiveGainers.size()));
        InstrumentDTO stock = model.getStocks().get(symbol);
        if (stock == null)
            return null;

        int totalHolding = bot.getPortfolio().getStockQuantity(symbol);
        int quantity = Math.max(1, Math.min(randomQuantity(), totalHolding / 2));
        BigDecimal price = calculatePriceWithVariation(stock.getPrice(), 0.01);

        return createSellOrder(model, bot, symbol, quantity, price);
    }
}
