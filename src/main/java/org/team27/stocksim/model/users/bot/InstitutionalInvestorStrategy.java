package org.team27.stocksim.model.users.bot;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.util.dto.InstrumentDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.team27.stocksim.model.market.Order;

/**
 * Institutional Investor Strategy - Represents large funds/institutions
 * This bot simulates institutional money flowing into the market:
 * - Receives periodic capital injections (like pension funds, 401k
 * contributions)
 * - Makes large, strategic investments
 * - Focuses on diversification and long-term holdings
 * - Generally bullish (represents net market inflow over time)
 */
public class InstitutionalInvestorStrategy extends AbstractBotStrategy {

    private final double investmentProbability;
    private int ticksSinceLastInvestment = 0;
    private final int capitalInjectionInterval;

    public InstitutionalInvestorStrategy() {
        this(new Random(), 0.15, 10, 50, 20);
    }

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
