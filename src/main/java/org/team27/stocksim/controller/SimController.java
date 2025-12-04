package org.team27.stocksim.controller;

import java.math.BigDecimal;
import java.util.HashMap;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.market.Instrument;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.observer.ModelObserver;

public class SimController implements ISimController {

    private StockSim model;

    public SimController(StockSim model) {
        this.model = model;

    }

    public void setUpSimulation() {
        init();
    }

    private void init() {
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

    public void createStock(String symbol, String stockName, String tickSize, String lotSize) {
        model.createStock(symbol, stockName, tickSize, lotSize);
    }

    public void addObserver(ModelObserver obs) {
        model.addObserver(obs);
    }

    public void removeObserver(ModelObserver obs) {
        model.removeObserver(obs);
    }

    @Override
    public HashMap<String, Instrument> getAllStocks() {
        return model.getStocks();
    }

}