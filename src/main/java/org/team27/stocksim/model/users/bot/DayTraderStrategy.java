package org.team27.stocksim.model.users.bot;

import org.team27.stocksim.dto.InstrumentDTO;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.users.Bot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.team27.stocksim.model.market.Order;

/**
 * Day Trader Strategy - Active high-frequency trading approach.
 *
 * <p>The DayTraderStrategy implements an active trading style that frequently
 * opens and closes positions throughout the simulation. Day traders aim to
 * profit from small price movements and don't hold positions long-term. This
 * strategy provides high liquidity to the market.</p>
 *
 * <p><strong>Design Pattern:</strong> Strategy (concrete implementation)</p>
 * <ul>
 *   <li>Very high trading frequency (20% action probability)</li>
 *   <li>Doesn't hold positions long-term</li>
 *   <li>Takes quick profits on any holdings</li>
 *   <li>60/40 split favoring buys for market liquidity</li>
 *   <li>Smaller position sizes for quick turnover</li>
 * </ul>
 *
 * <h2>Trading Behavior:</h2>
 * <ul>
 *   <li>Trade probability: 20% per tick (default)</li>
 *   <li>60% chance to buy when acting</li>
 *   <li>40% chance to sell holdings for quick profit</li>
 *   <li>No long-term profit analysis needed</li>
 *   <li>Provides consistent market activity</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Use default day trader strategy
 * IBotStrategy strategy = new DayTraderStrategy();
 *
 * // Custom day trader with higher frequency
 * IBotStrategy strategy = new DayTraderStrategy(
 *     new Random(),
 *     0.40,   // 40% trade probability (very active)
 *     0.01,   // 1% quick profit threshold
 *     1,      // min quantity
 *     5       // max quantity (smaller positions)
 * );
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see AbstractBotStrategy
 * @see IBotStrategy
 */
public class DayTraderStrategy extends AbstractBotStrategy {

    /**
     * Probability of making a trade on each tick (high for day traders).
     */
    private final double tradeProbability;

    /**
     * Constructs a DayTraderStrategy with default parameters.
     * <ul>
     *   <li>Trade probability: 20%</li>
     *   <li>Quick profit threshold: 2.5%</li>
     *   <li>Quantity range: 1-10</li>
     * </ul>
     */
    public DayTraderStrategy() {
        this(new Random(), 0.2, 0.025, 1, 10);
    }

    /**
     * Constructs a DayTraderStrategy with custom parameters.
     *
     * @param random Random number generator
     * @param tradeProbability Probability of trading per tick (0.0-1.0)
     * @param quickProfitThreshold Minimum profit to trigger sell (currently unused)
     * @param minQuantity Minimum order quantity
     * @param maxQuantity Maximum order quantity
     */
    public DayTraderStrategy(Random random, double tradeProbability, double quickProfitThreshold,
            int minQuantity, int maxQuantity) {
        super(random, minQuantity, maxQuantity);
        this.tradeProbability = tradeProbability;
    }

    @Override
    public List<Order> decide(StockSim model, Bot bot) {
        List<Order> orders = new ArrayList<>();
        if (random.nextDouble() > tradeProbability) {
            return orders;
        }

        Order order = null;
        // Randomly decide to buy or sell
        if (random.nextDouble() < 0.4 && !bot.getPortfolio().isEmpty()) {
            // 40% chance to sell if we have holdings
            order = sellForQuickProfit(model, bot);
        } else {
            // 60% chance to buy
            order = buy(model, bot);
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

    private Order sellForQuickProfit(StockSim model, Bot bot) {
        HashMap<String, Integer> holdings = new HashMap<>(bot.getPortfolio().getStockHoldings());
        if (holdings.isEmpty()) {
            return null;
        }

        // Sell any holding (day traders are active)
        String symbol = pickRandomHolding(bot);
        if (symbol == null)
            return null;

        InstrumentDTO stock = model.getStocks().get(symbol);
        if (stock == null)
            return null;

        int maxAvailable = bot.getPortfolio().getStockQuantity(symbol);
        int quantity = Math.min(randomQuantity(), maxAvailable);
        BigDecimal price = calculatePriceWithVariation(stock.getPrice(), 0.01);

        return createSellOrder(model, bot, symbol, quantity, price);
    }
}
