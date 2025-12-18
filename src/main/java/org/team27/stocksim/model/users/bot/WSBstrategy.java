package org.team27.stocksim.model.users.bot;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.util.dto.InstrumentDTO;
import org.team27.stocksim.model.market.Order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * WSB (WallStreetBets) Strategy - "Buy low, sell high" momentum trading
 * This bot analyzes price history to identify opportunities:
 * - Buys stocks that have dropped significantly (potential bargain)
 * - Sells stocks that have risen significantly (take profits)
 */
public class WSBstrategy extends AbstractBotStrategy {

    private final double actionProbability;
    private final double buyThreshold;
    private final double sellThreshold;
    private final int lookbackPeriod;

    public WSBstrategy() {
        this(new Random(), 0.05, 0.08, 0.15, 20, 1, 15);
    }

    public WSBstrategy(Random random, double actionProbability, double buyThreshold,
            double sellThreshold, int lookbackPeriod, int minQuantity, int maxQuantity) {
        super(random, minQuantity, maxQuantity);
        this.actionProbability = actionProbability;
        this.buyThreshold = buyThreshold;
        this.sellThreshold = sellThreshold;
        this.lookbackPeriod = lookbackPeriod;
    }

    @Override
    public List<Order> decide(StockSim model, Bot bot) {
        List<Order> orders = new ArrayList<>();
        if (random.nextDouble() > actionProbability) {
            return orders;
        }

        // Try to sell profitable positions first
        Order order = tryToSellProfitable(model, bot);
        if (order != null) {
            orders.add(order);
            return orders;
        }

        // Then look for buying opportunities
        order = tryToBuyLow(model, bot);
        if (order != null) {
            orders.add(order);
        }

        return orders;
    }

    private Order tryToSellProfitable(StockSim model, Bot bot) {
        List<String> profitableStocks = findProfitableHoldings(model, bot, sellThreshold * 100);

        if (profitableStocks.isEmpty()) {
            return null;
        }

        String symbol = profitableStocks.get(random.nextInt(profitableStocks.size()));
        InstrumentDTO stock = model.getStocks().get(symbol);
        if (stock == null) {
            return null;
        }

        int maxAvailable = bot.getPortfolio().getStockHoldings().get(symbol);
        int quantity = Math.min(randomQuantity(), maxAvailable);
        BigDecimal price = calculatePriceWithVariation(stock.getPrice(), 1.0);

        return createSellOrder(model, bot, symbol, quantity, price);
    }

    private Order tryToBuyLow(StockSim model, Bot bot) {
        HashMap<String, InstrumentDTO> stocks = model.getStocks();
        if (stocks == null || stocks.isEmpty()) {
            return null;
        }

        List<InstrumentDTO> opportunities = new ArrayList<>();
        for (InstrumentDTO stock : stocks.values()) {
            if (hasPriceDropped(stock, lookbackPeriod, buyThreshold)) {
                opportunities.add(stock);
            }
        }

        if (opportunities.isEmpty()) {
            return null;
        }

        InstrumentDTO stock = opportunities.get(random.nextInt(opportunities.size()));
        int quantity = randomQuantity();
        BigDecimal price = calculatePriceWithVariation(stock.getPrice(), -1.0);

        return createBuyOrder(model, bot, stock, quantity, price);
    }
}
