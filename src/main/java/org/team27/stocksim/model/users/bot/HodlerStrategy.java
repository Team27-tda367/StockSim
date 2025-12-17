package org.team27.stocksim.model.users.bot;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.util.dto.InstrumentDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.team27.stocksim.model.market.Order;

/**
 * HODL Strategy - Buy and hold for the long term
 * This bot believes in long-term growth:
 * - Buys occasionally and holds
 * - Very rarely sells (diamond hands)
 * - Only sells after massive gains
 */
public class HodlerStrategy extends AbstractBotStrategy {

    private final double buyProbability;
    private final double sellThreshold;

    public HodlerStrategy() {
        this(new Random(), 0.01, 0.5, 5, 20);
    }

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
