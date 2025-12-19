package org.team27.stocksim.model.users.bot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.team27.stocksim.dto.InstrumentDTO;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.users.Bot;

public class RandomStrategy extends AbstractBotStrategy {

    private enum Action {
        BUY, SELL, NONE
    }

    private final double doSomethingProbability;
    private final double buyProbability = 0.51;

    public RandomStrategy() {
        this(new Random(), 0.01, 1, 10);
    }

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
