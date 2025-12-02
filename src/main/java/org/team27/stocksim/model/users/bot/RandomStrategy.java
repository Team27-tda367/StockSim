package org.team27.stocksim.model.users.bot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.team27.stocksim.model.market.Instrument;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.users.Bot;

public class RandomStrategy implements BotStrategy {

    private enum Action {
        BUY, SELL, NONE
    }

    private final Random random;

    // Configuration parameters
    private final double doSomethingProbability; // probability of buying or selling
    private final double buyProbability = 0.5; // Buy vs Sell probability
    private final int minQuantity; // minimum quantity to buy
    private final int maxQuantity; // maximum quantity to buy

    public RandomStrategy() {
        this(new Random(), 0.01, 1, 10);
    }

    public RandomStrategy(Random random, double doSomethingProbability, int minQuantity, int maxQuantity) {
        this.random = random;
        this.doSomethingProbability = doSomethingProbability;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
    }

    @Override
    public void decide(StockSim model, Bot bot) {
        // 1Determine if we should buy or sell anything in this tick
        Action action = randomAction();

        if (action == Action.NONE) {
            return;
        }
        if (action == Action.SELL) {
            sell(model);
        } else if (action == Action.BUY) {
            buy(model);
        }

    }

    private void sell(StockSim model) {
        // If portfolio is empty, cannot sell
        /*
         * if (bot.getPortfolio().isEmpty()) {
         * return;
         * }
         */
        // 1. Choose a random instrument in portfolio

        // 2. pickRandomQuantity from holding

        // 3. get current price
        // double price = stock.getCurrentPrice();

        // Create the order
        // model.placeOrder(Order.createSellOrder(bot, stock, quantity, price));

    }

    private void buy(StockSim model) {
        // Choose a random instrument in market
        Instrument stock = pickRandomStock(model);
        if (stock == null) {
            return;
        }

        int quantity = randomQuantity();

        BigDecimal price = stock.getCurrentPrice();

        // Create order
        // model.placeOrder(Order.createBuyOrder(bot, stock, quantity, price));
    }

    private boolean shouldDoSomething() {
        // Simple probability check
        return random.nextDouble() < doSomethingProbability;
    }

    private Instrument pickRandomStock(StockSim model) {
        HashMap<String, Instrument> stocks = model.getStocks();
        if (stocks == null || stocks.isEmpty()) {
            return null;
        }
        List<Instrument> instruments = new ArrayList<>(stocks.values());
        int index = random.nextInt(instruments.size());
        return instruments.get(index);
    }

    private int randomQuantity() {
        if (maxQuantity <= minQuantity) {
            return minQuantity;
        }
        int bound = maxQuantity - minQuantity + 1;
        return minQuantity + random.nextInt(bound);
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
