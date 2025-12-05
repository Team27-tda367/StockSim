package org.team27.stocksim.model;

import java.math.BigDecimal;
import java.util.Random;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.users.Bot;

public class SimSetup {
    private final Random random = new Random();
    private final StockSim model;

    public SimSetup(StockSim model) {
        this.model = model;
    }

    public void start() {
        // Create some default stocks
        createDefaultStocks();
        createBots(100);
        // Create user
        model.createUser("user1", "Default User");
        model.setCurrentUser("user1");
        // createSellOrders(100, BigDecimal.valueOf(100.00));
        initializeBotPositions();

        model.startMarketSimulation(); // Start the simulation if needed

    }

    private void initializeBotPositions() {
        for (var trader : model.getTraders().values()) {
            if (trader instanceof Bot) {
                var bot = (Bot) trader;
                for (Instrument stock : model.getStocks().values()) {
                    int quantity = random.nextInt(151) + 50; // Random quantity between 50 and 200
                    // Initialize with a random cost basis between 95 and 105
                    BigDecimal initialCost = BigDecimal.valueOf(95 + random.nextInt(11));
                    bot.getPortfolio().addStock(stock.getSymbol(), quantity, initialCost, null);
                }
            }
        }
    }

    /*
     * private void createSellOrders(int numberOfOrders, BigDecimal startingPrice) {
     * 
     * for (Instrument stock : model.getStocks().values()) {
     * String symbol = stock.getSymbol();
     * // You can customize the starting price based on the stock if needed
     * for (var i = 0; i < numberOfOrders / 10; i++) {
     * for (var quantity = 1; quantity <= 10; quantity++) {
     * 
     * BigDecimal randomPrice = startingPrice.add(BigDecimal.valueOf(i));
     * Order sellOrder = new Order(Order.Side.SELL, symbol, quantity, randomPrice,
     * 10, "bot" + i);
     * model.placeOrder(sellOrder);
     * }
     * }
     * }
     * }
     */

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
