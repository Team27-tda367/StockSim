package org.team27.stocksim.model;

import java.math.BigDecimal;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.market.Order;

public class SimSetup {
    private final StockSim model;

    public SimSetup(StockSim model) {
        this.model = model;
    }

    public void start() {
        // Create some default stocks
        createDefaultStocks();
        createBots(1000);
        // Create user
        model.createUser("user1", "Default User");
        model.setCurrentUser("user1");
        createSellOrders(100, BigDecimal.valueOf(100.00));
        model.startMarketSimulation(); // Start the simulation if needed

    }

    private void createSellOrders(int numberOfOrders, BigDecimal startingPrice) {

        for (Instrument stock : model.getStocks().values()) {
            String symbol = stock.getSymbol();
            // You can customize the starting price based on the stock if needed
            for (var i = 0; i < numberOfOrders / 10; i++) {
                for (var quantity = 1; quantity <= 10; quantity++) {

                    BigDecimal randomPrice = startingPrice.add(BigDecimal.valueOf(i));
                    Order sellOrder = new Order(Order.Side.SELL, symbol, quantity, randomPrice, 10, "bot" + i);
                    model.placeOrder(sellOrder);
                }
            }
        }
    }

    private void createDefaultStocks() {
        // Original stocks
        model.createStock("AAPL", "Apple Inc.", "0.01", "100");
        model.createStock("GOOGL", "Alphabet Inc.", "0.01", "100");
        model.createStock("MSFT", "Microsoft Corp.", "0.01", "100");

        // Additional 17 random stocks to make 20 total
        model.createStock("TSLA", "Tesla Inc.", "0.01", "100");
        model.createStock("AMZN", "Amazon.com Inc.", "0.01", "100");
        model.createStock("META", "Meta Platforms Inc.", "0.01", "100");
        model.createStock("NVDA", "NVIDIA Corporation", "0.01", "100");
        model.createStock("JPM", "JPMorgan Chase & Co.", "0.01", "100");
        model.createStock("V", "Visa Inc.", "0.01", "100");
        model.createStock("WMT", "Walmart Inc.", "0.01", "100");
        model.createStock("DIS", "The Walt Disney Company", "0.01", "100");
        model.createStock("NFLX", "Netflix Inc.", "0.01", "100");
        model.createStock("PYPL", "PayPal Holdings Inc.", "0.01", "100");
        model.createStock("INTC", "Intel Corporation", "0.01", "100");
        model.createStock("CSCO", "Cisco Systems Inc.", "0.01", "100");
        model.createStock("PEP", "PepsiCo Inc.", "0.01", "100");
        model.createStock("KO", "The Coca-Cola Company", "0.01", "100");
        model.createStock("NKE", "Nike Inc.", "0.01", "100");
        model.createStock("BA", "The Boeing Company", "0.01", "100");
        model.createStock("AMD", "Advanced Micro Devices Inc.", "0.01", "100");
    }

    private void createBots(int numberOfBots) {
        for (int i = 1; i <= numberOfBots; i++) {
            String botId = "bot" + i;
            String botName = "Bot " + i;
            model.createBot(botId, botName);
        }
    }
}
