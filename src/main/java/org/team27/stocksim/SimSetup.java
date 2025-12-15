package org.team27.stocksim;

import java.math.BigDecimal;
import java.util.Random;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.util.dto.InstrumentDTO;

public class SimSetup {
    private final Random random = new Random();
    private final StockSim model;

    public SimSetup(StockSim model) {
        this.model = model;
    }

    public void start() {
        createDefaultStocks();
        createBots(1000);
        model.createUser("user1", "Default User");
        model.setCurrentUser("user1");
        initializeBotPositions();

        model.startMarketSimulation();

    }

    private void initializeBotPositions() {
        for (var trader : model.getTraders().values()) {
            if (trader instanceof Bot) {
                var bot = (Bot) trader;
                for (InstrumentDTO stock : model.getStocks().values()) {
                    int quantity = random.nextInt(151) + 50; // Random quantity between 50 and 200
                    // Initialize with a random cost basis between 95 and 105
                    BigDecimal initialCost = BigDecimal.valueOf(95 + random.nextInt(11));
                    bot.getPortfolio().addStock(stock.getSymbol(), quantity, initialCost, null);
                }
            }
        }
    }

    private void createDefaultStocks() {
        // 20 random stocks
        model.createStock("AAPL", "Apple Inc.", "0.01", "100", "Technology");
        model.createStock("GOOGL", "Alphabet Inc.", "0.01", "100", "Technology");
        model.createStock("MSFT", "Microsoft Corp.", "0.01", "100", "Technology");
        model.createStock("TSLA", "Tesla Inc.", "0.01", "100", "Consumer");
        model.createStock("AMZN", "Amazon.com Inc.", "0.01", "100", "Consumer");
        model.createStock("META", "Meta Platforms Inc.", "0.01", "100", "Entertainment");
        model.createStock("NVDA", "NVIDIA Corporation", "0.01", "100", "Semiconductors");
        model.createStock("JPM", "JPMorgan Chase & Co.", "0.01", "100", "Finance");
        model.createStock("V", "Visa Inc.", "0.01", "100", "Finance");
        model.createStock("WMT", "Walmart Inc.", "0.01", "100", "Consumer");
        model.createStock("DIS", "The Walt Disney Company", "0.01", "100", "Entertainment");
        model.createStock("NFLX", "Netflix Inc.", "0.01", "100", "Entertainment");
        model.createStock("PYPL", "PayPal Holdings Inc.", "0.01", "100", "Finance");
        model.createStock("INTC", "Intel Corporation", "0.01", "100", "Semiconductors");
        model.createStock("CSCO", "Cisco Systems Inc.", "0.01", "100", "Technology");
        model.createStock("PEP", "PepsiCo Inc.", "0.01", "100", "Consumer");
        model.createStock("KO", "The Coca-Cola Company", "0.01", "100", "Consumer");
        model.createStock("NKE", "Nike Inc.", "0.01", "100", "Consumer");
        model.createStock("BA", "The Boeing Company", "0.01", "100", "Aviation");
        model.createStock("AMD", "Advanced Micro Devices Inc.", "0.01", "100", "Semiconductors");
    }

    private void createBots(int numberOfBots) {
        for (int i = 1; i <= numberOfBots; i++) {
            String botId = "bot" + i;
            String botName = "Bot " + i;
            model.createBot(botId, botName);
        }
    }
}
