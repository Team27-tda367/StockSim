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
 * Institutional Investor Strategy - Large fund with periodic capital inflows.
 *
 * <p>The InstitutionalInvestorStrategy simulates large institutional money entering
 * the market, such as pension funds, 401(k) plans, mutual funds, or endowments.
 * These entities receive periodic capital injections (representing contributions)
 * and make large, strategic investments with a long-term, generally bullish outlook.</p>
 *
 * <p><strong>Design Pattern:</strong> Strategy (concrete implementation)</p>
 * <ul>
 *   <li>Periodic capital injections simulate new money entering market</li>
 *   <li>Large position sizes (min 10, max 50+ shares)</li>
 *   <li>Focus on diversification across multiple stocks</li>
 *   <li>Very rare selling (long-term hold mentality)</li>
 *   <li>Generally bullish stance representing net market inflow</li>
 * </ul>
 *
 * <h2>Capital Injection System:</h2>
 * <ul>
 *   <li>Periodic deposits every N ticks (default: 20)</li>
 *   <li>Injection amount: $1,000 - $5,000 per period</li>
 *   <li>Simulates 401(k) contributions, pension deposits</li>
 *   <li>Creates consistent buy pressure in market</li>
 * </ul>
 *
 * <h2>Investment Behavior:</h2>
 * <ul>
 *   <li>Investment probability: 15% per tick</li>
 *   <li>Diversification focus (buys different stocks)</li>
 *   <li>Rebalancing: 1% probability per tick</li>
 *   <li>Rarely sells (represents long-term capital)</li>
 *   <li>Larger quantities per trade</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Use default institutional investor strategy
 * IBotStrategy strategy = new InstitutionalInvestorStrategy();
 *
 * // Custom institutional investor with different parameters
 * IBotStrategy strategy = new InstitutionalInvestorStrategy(
 *     new Random(),
 *     0.20,   // 20% investment probability
 *     20,     // min quantity (larger positions)
 *     100,    // max quantity (very large positions)
 *     15      // inject capital every 15 ticks
 * );
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see AbstractBotStrategy
 * @see IBotStrategy
 */
public class InstitutionalInvestorStrategy extends AbstractBotStrategy {

    /**
     * Probability of making an investment on each tick.
     */
    private final double investmentProbability;

    /**
     * Counter tracking ticks since last capital injection.
     */
    private int ticksSinceLastInvestment = 0;

    /**
     * Number of ticks between capital injections.
     */
    private final int capitalInjectionInterval;

    /**
     * Constructs an InstitutionalInvestorStrategy with default parameters.
     * <ul>
     *   <li>Investment probability: 15%</li>
     *   <li>Capital injection: every 20 ticks</li>
     *   <li>Injection amount: $1,000-$5,000</li>
     *   <li>Quantity range: 10-50</li>
     * </ul>
     */
    public InstitutionalInvestorStrategy() {
        this(new Random(), 0.15, 10, 50, 20);
    }

    /**
     * Constructs an InstitutionalInvestorStrategy with custom parameters.
     *
     * @param random Random number generator
     * @param investmentProbability Probability of investing per tick (0.0-1.0)
     * @param minQuantity Minimum order quantity
     * @param maxQuantity Maximum order quantity
     * @param capitalInjectionInterval Ticks between capital injections
     */
    public InstitutionalInvestorStrategy(Random random, double investmentProbability,
            int minQuantity, int maxQuantity,
            int capitalInjectionInterval) {
        super(random, minQuantity, maxQuantity);
        this.investmentProbability = investmentProbability;
        this.capitalInjectionInterval = capitalInjectionInterval;
    }

    @Override
    public List<Order> decide(StockSim model, Bot bot) {
        List<Order> orders = new ArrayList<>();
        ticksSinceLastInvestment++;

        // Periodic capital injection (simulates new money entering market)
        if (ticksSinceLastInvestment >= capitalInjectionInterval) {
            injectCapital(bot);
            ticksSinceLastInvestment = 0;
        }

        Order order = null;
        // Make investment decisions
        if (random.nextDouble() < investmentProbability) {
            order = investInMarket(model, bot);
        }

        // Very rarely sell (institutional investors hold long-term)
        if (order == null && random.nextDouble() < 0.01) {
            order = rebalancePortfolio(model, bot);
        }

        if (order != null) {
            orders.add(order);
        }

        return orders;
    }

    /**
     * Simulates new money entering the market (401k contributions, pension
     * deposits, etc.)
     */
    private void injectCapital(Bot bot) {
        // Inject between 1,000 and 5,000 per interval
        BigDecimal injection = BigDecimal.valueOf(1000 + random.nextInt(4001));
        bot.getPortfolio().deposit(injection);
    }

    /**
     * Make strategic, diversified investments
     */
    private Order investInMarket(StockSim model, Bot bot) {
        InstrumentDTO stock = pickRandomStock(model);
        if (stock == null) {
            return null;
        }

        int quantity = randomQuantity();
        BigDecimal price = calculatePriceWithVariation(stock.getPrice(), 0.5);

        return createBuyOrder(model, bot, stock, quantity, price);
    }

    /**
     * Occasionally rebalance by selling overweight positions
     */
    private Order rebalancePortfolio(StockSim model, Bot bot) {
        HashMap<String, Integer> holdings = new HashMap<>(bot.getPortfolio().getStockHoldings());
        if (holdings.isEmpty()) {
            return null;
        }

        List<String> profitablePositions = findProfitableHoldings(model, bot, 20.0);
        if (profitablePositions.isEmpty()) {
            return null;
        }

        String symbol = profitablePositions.get(random.nextInt(profitablePositions.size()));
        InstrumentDTO stock = model.getStocks().get(symbol);
        if (stock == null) {
            return null;
        }

        int totalHolding = holdings.get(symbol);
        int quantity = Math.min(randomQuantity(), totalHolding / 4);
        if (quantity <= 0) {
            quantity = 1;
        }

        BigDecimal price = calculatePriceWithVariation(stock.getPrice(), 0.5);
        return createSellOrder(model, bot, symbol, quantity, price);
    }
}
