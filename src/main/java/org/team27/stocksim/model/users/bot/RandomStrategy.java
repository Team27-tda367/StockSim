package org.team27.stocksim.model.users.bot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.users.Bot;

public class RandomStrategy implements BotStrategy {

    private enum Action {
        BUY, SELL, NONE
    }

    private final Random random;

    // Configuration parameters
    private final double doSomethingProbability; // probability of buying or selling
    private final double buyProbability = 0.51; // Buy vs Sell probability
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
            sell(model, bot);
        } else if (action == Action.BUY) {
            buy(model, bot);
        }

    }

    private void sell(StockSim model, Bot bot) {
        // Check if the bot owns any stocks
        if (bot.getPortfolio().isEmpty()) {
            return;
        }

        // Get the stocks the bot owns
        HashMap<String, Integer> holdings = new HashMap<>(bot.getPortfolio().getStockHoldings());
        if (holdings.isEmpty()) {
            return;
        }

        // Pick a random stock from the bot's holdings
        List<String> ownedSymbols = new ArrayList<>(holdings.keySet());
        String symbol = ownedSymbols.get(random.nextInt(ownedSymbols.size()));

        // Get the instrument from the market
        Instrument stock = model.getStocks().get(symbol);
        if (stock == null) {
            return;
        }

        // Get the maximum quantity the bot can sell
        int maxAvailableQuantity = holdings.get(symbol);
        if (maxAvailableQuantity <= 0) {
            return;
        }

        // Determine quantity to sell (can't sell more than owned)
        int quantity = Math.min(randomQuantity(), maxAvailableQuantity);
        BigDecimal price = randomPrice(stock.getCurrentPrice());

        Order sellOrder = new Order(Order.Side.SELL, stock.getSymbol(), price, quantity, bot.getId());
        model.placeOrder(sellOrder);
    }

    private void buy(StockSim model, Bot bot) {
        // Choose a random instrument in market
        Instrument stock = pickRandomStock(model);
        if (stock == null) {
            return;
        }

        int quantity = randomQuantity();

        BigDecimal price = randomPrice(stock.getCurrentPrice());

        Order buyOrder = new Order(Order.Side.BUY, stock.getSymbol(), price, quantity, bot.getId());
        model.placeOrder(buyOrder);
    }

    private BigDecimal randomPrice(BigDecimal basePrice) {
        // Create a variation using normal distribution (mean=0, std dev=0.5%)
        // This gives approximately 95% of prices within ±1% of base price
        double variationPercent = random.nextGaussian() * 0.005;
        BigDecimal variation = basePrice.multiply(BigDecimal.valueOf(variationPercent));
        BigDecimal newPrice = basePrice.add(variation);

        // Ensure price is positive and round to 2 decimal places
        if (newPrice.compareTo(BigDecimal.ONE) < 0) {
            newPrice = BigDecimal.ONE;
        }
        return newPrice.setScale(2, java.math.RoundingMode.HALF_UP);
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
