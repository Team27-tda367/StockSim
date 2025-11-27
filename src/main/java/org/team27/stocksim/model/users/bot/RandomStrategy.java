package org.team27.stocksim.model.users.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.team27.stocksim.model.market.Instrument;

import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.market.StockSim;
import org.team27.stocksim.model.users.Bot;

public class RandomStrategy implements BotStrategy {
    private double buyProbability = 0.1;

    public RandomStrategy(double buyProbability) {
        if (buyProbability < 0.0 || buyProbability > 1.0) {
            throw new IllegalArgumentException("buyProbability must be between 0.0 and 1.0");
        }
        this.buyProbability = buyProbability;
    }

    public double getBuyProbability() {
        return buyProbability;
    }

    @Override
    public List<Order> decide(StockSim model, Bot bot) {
        if (Math.random() >= buyProbability) {
            return Collections.emptyList(); // If not buying, return empty list
        }

        HashMap<String, Instrument> stocks = model.getStocks();

        if (stocks.isEmpty()) {
            return Collections.emptyList();
        }

        List<Instrument> instruments = new ArrayList<>(stocks.values());
        Instrument selectedStock = instruments.get((int) (Math.random() * instruments.size()));

        // Random quantity
        int quantity = 1 + (int) (Math.random() * 10); //

        // Put together order
        Order buyOrder = Order.createBuyOrder(bot, selectedStock, quantity, selectedStock.getCurrentPrice());

        return Collections.singletonList(buyOrder);

    }

}
