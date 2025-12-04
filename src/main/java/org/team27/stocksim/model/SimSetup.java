package org.team27.stocksim.model;

import java.math.BigDecimal;

import org.team27.stocksim.model.market.Order;

public class SimSetup {
    private final StockSim model;

    public SimSetup(StockSim model) {
        this.model = model;
    }

    public void start() {
        // Create some default stocks
        createDefaultStocks();
        createBots(10000);
        // Create user
        model.createUser("user1", "Default User");
        createSellOrders();
        model.startMarketSimulation(); // Start the simulation if needed

    }

    private void createSellOrders() {
        // Example: Create some sell orders for the default stocks
        for (var i = 0; i < 50; i++) {
            Order sellOrder = new Order(Order.Side.SELL, "AAPL", 100, BigDecimal.valueOf(100.00), 10, "bot" + i);
            model.placeOrder(sellOrder);
        }
    }

    private void createDefaultStocks() {
        model.createStock("AAPL", "Apple Inc.", "0.01", "100");
        model.createStock("GOOGL", "Alphabet Inc.", "0.01", "100");
        model.createStock("MSFT", "Microsoft Corp.", "0.01", "100");
    }

    private void createBots(int numberOfBots) {
        for (int i = 1; i <= numberOfBots; i++) {
            String botId = "bot" + i;
            String botName = "Bot " + i;
            model.createBot(botId, botName);
        }
    }
}
