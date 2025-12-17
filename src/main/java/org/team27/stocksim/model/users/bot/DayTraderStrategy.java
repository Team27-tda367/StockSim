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
 * Day Trader Strategy - Active trading, frequent buys and sells
 * This bot actively trades throughout the day:
 * - Frequently buys and sells
 * - Takes quick profits
 * - Doesn't hold positions for long
 */
public class DayTraderStrategy extends AbstractBotStrategy {

    private final double tradeProbability;

    public DayTraderStrategy() {
        this(new Random(), 0.2, 0.025, 1, 10);
    }

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
