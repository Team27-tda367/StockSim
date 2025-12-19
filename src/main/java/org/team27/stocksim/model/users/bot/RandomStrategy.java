package org.team27.stocksim.model.users.bot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.team27.stocksim.dto.InstrumentDTO;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.users.Bot;

/**
 * Trading strategy that makes random buy/sell decisions.
 *
 * <p>The RandomStrategy simulates unpredictable market participants who trade
 * without following any particular pattern. It randomly decides whether to buy,
 * sell, or do nothing on each tick, with a slight bias toward buying to
 * maintain market liquidity.</p>
 *
 * <p><strong>Design Pattern:</strong> Strategy (concrete implementation)</p>
 * <ul>
 *   <li>Configurable probability of taking action each tick</li>
 *   <li>Slight bias toward buying (51% vs 49%) for market health</li>
 *   <li>Random stock selection for both buys and sells</li>
 *   <li>Random quantities within configured range</li>
 *   <li>Price variation around current market price</li>
 * </ul>
 *
 * <h2>Behavior:</h2>
 * <ul>
 *   <li>1% chance of action per tick (default)</li>
 *   <li>If acting, 51% chance to buy, 49% to sell</li>
 *   <li>Buys random stock at slightly varied price</li>
 *   <li>Sells random holding if portfolio not empty</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Use default random strategy
 * IBotStrategy strategy = new RandomStrategy();
 *
 * // Custom random strategy with higher activity
 * IBotStrategy activeStrategy = new RandomStrategy(
 *     new Random(),
 *     0.10,  // 10% chance of action
 *     5,     // min quantity
 *     50     // max quantity
 * );
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see AbstractBotStrategy
 * @see IBotStrategy
 */
public class RandomStrategy extends AbstractBotStrategy {

    /**
     * Enum representing possible actions the bot can take.
     */
    private enum Action {
        BUY, SELL, NONE
    }

    /**
     * Probability of taking any action on a given tick.
     */
    private final double doSomethingProbability;

    /**
     * Probability of buying when action is taken (vs selling).
     */
    private final double buyProbability = 0.51;

    /**
     * Constructs a RandomStrategy with default parameters.
     * <ul>
     *   <li>Action probability: 1%</li>
     *   <li>Quantity range: 1-10</li>
     * </ul>
     */
    public RandomStrategy() {
        this(new Random(), 0.01, 1, 10);
    }

    /**
     * Constructs a RandomStrategy with custom parameters.
     *
     * @param random Random number generator
     * @param doSomethingProbability Probability of action per tick (0.0-1.0)
     * @param minQuantity Minimum order quantity
     * @param maxQuantity Maximum order quantity
     */
    public RandomStrategy(Random random, double doSomethingProbability, int minQuantity, int maxQuantity) {
        super(random, minQuantity, maxQuantity);
        this.doSomethingProbability = doSomethingProbability;
    }

    @Override
    public List<Order> decide(StockSim model, Bot bot) {
        List<Order> orders = new ArrayList<>();
        // Determine if we should buy or sell anything in this tick
        Action action = randomAction();

        if (action == Action.NONE) {
            return orders;
        }

        Order order = null;
        if (action == Action.SELL) {
            order = sell(model, bot);
        } else if (action == Action.BUY) {
            order = buy(model, bot);
        }

        if (order != null) {
            orders.add(order);
        }

        return orders;
    }

    private Order sell(StockSim model, Bot bot) {
        if (bot.getPortfolio().isEmpty()) {
            return null;
        }

        String symbol = pickRandomHolding(bot);
        if (symbol == null) {
            return null;
        }

        InstrumentDTO stock = model.getStocks().get(symbol);
        if (stock == null) {
            return null;
        }

        int maxAvailable = bot.getPortfolio().getStockQuantity(symbol);
        int quantity = Math.min(randomQuantity(), maxAvailable);
        BigDecimal price = randomPrice(stock.getPrice());

        return createSellOrder(model, bot, symbol, quantity, price);
    }

    private Order buy(StockSim model, Bot bot) {
        InstrumentDTO stock = pickRandomStock(model);
        if (stock == null) {
            return null;
        }

        int quantity = randomQuantity();
        BigDecimal price = randomPrice(stock.getPrice());

        return createBuyOrder(model, bot, stock, quantity, price);
    }

    private BigDecimal randomPrice(BigDecimal basePrice) {
        // Create a variation using normal distribution (mean=0, std dev=0.5%)
        double variationPercent = random.nextGaussian() * 0.005;
        BigDecimal variation = basePrice.multiply(BigDecimal.valueOf(variationPercent));
        BigDecimal newPrice = basePrice.add(variation);

        if (newPrice.compareTo(BigDecimal.ONE) < 0) {
            newPrice = BigDecimal.ONE;
        }
        return newPrice.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private boolean shouldDoSomething() {
        return random.nextDouble() < doSomethingProbability;
    }

    private Action randomAction() {
        double x = random.nextDouble();
        if (!shouldDoSomething()) {
            return Action.NONE;
        }
        if (x < buyProbability) {
            return Action.BUY;
        } else {
            return Action.SELL;
        }
    }

}
