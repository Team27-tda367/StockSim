package org.team27.stocksim.model.users.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.team27.stocksim.model.market.Instrument;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.market.StockSim;
import org.team27.stocksim.model.users.Bot;

public class RandomStrategy implements BotStrategy {

    private final Random random;

    // Configuration parameters
    private final double buyProbability; // probability of placing a buy order (0–1)
    private final int minQuantity; // minimum quantity to buy
    private final int maxQuantity; // maximum quantity to buy

    public RandomStrategy() {
        this(new Random(), 0.01, 1, 10);
    }

    public RandomStrategy(Random random, double buyProbability, int minQuantity, int maxQuantity) {
        this.random = random;
        this.buyProbability = buyProbability;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
    }

    @Override
    public void decide(StockSim model, Bot bot) {
        // 1. Determine if we should buy anything in this tick
        if (!shouldBuy()) {
            return;
        }

        // 2. Choose a random instrument
        Instrument stock = pickRandomStock(model);
        if (stock == null) {
            return;
        }

        // 3. Determine a random quantity
        int quantity = randomQuantity();

        // 4. Determine price (here: buy at current price – but you can randomize around
        // it later)
        double price = stock.getCurrentPrice();

        // 5. Create the order
        // model.placeOrder(Order.createBuyOrder(bot, stock, quantity, price));

    }

    private boolean shouldBuy() {
        // Simple probability check
        return random.nextDouble() < buyProbability;
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
}
