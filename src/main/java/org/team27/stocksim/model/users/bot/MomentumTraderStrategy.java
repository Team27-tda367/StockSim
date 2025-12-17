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
 * Momentum Trader Strategy - Follows price momentum
 * This bot buys stocks that are rising and sells stocks that are falling:
 * - Identifies upward price trends
 * - Buys rising stocks (momentum)
 * - Sells falling stocks quickly
 */
public class MomentumTraderStrategy extends AbstractBotStrategy {

    private final double actionProbability;
    private final int lookbackPeriod;
    private final double momentumThreshold;

    public MomentumTraderStrategy() {
        this(new Random(), 0.08, 10, 0.03, 1, 12);
    }

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
