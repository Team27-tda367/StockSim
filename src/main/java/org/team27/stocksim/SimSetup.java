package org.team27.stocksim;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.team27.stocksim.data.StockData;
import org.team27.stocksim.data.StockDataLoader;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.StockPriceRepository;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.util.dto.InstrumentDTO;

public class SimSetup {
    private final Random random = new Random();
    private final StockSim model;
    private final int initialBotCount;

    public SimSetup(StockSim model, int initialBotCount) {
        this.model = model;
        this.initialBotCount = initialBotCount;
    }

    public void start() {
        start(false);
    }

    public void startWithLoadedPrices() {
        start(true);
    }

    private void start(boolean loadExistingPrices) {
        createDefaultStocks();
        createBots(initialBotCount);
        model.createUser("user1", "Default User");
        model.setCurrentUser("user1");
        initializeBotPositions();

        if (loadExistingPrices) {
            // Load existing prices
            loadStockPrices();
        }
        // Start the market simulation
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
        StockDataLoader loader = new StockDataLoader();
        List<StockData> stocks = loader.loadDefaultStocks();

        for (StockData stock : stocks) {
            model.createStock(
                    stock.getSymbol(),
                    stock.getName(),
                    stock.getTickSize(),
                    stock.getLotSize(),
                    stock.getCategory());
        }
    }

    private void createBots(int numberOfBots) {
        for (int i = 1; i <= numberOfBots; i++) {
            String botId = "bot" + i;
            String botName = "Bot " + i;
            model.createBot(botId, botName);
        }
    }

    /**
     * Load stock price histories from JSON database.
     * Call this instead of starting market simulation to use pre-generated price
     * data.
     */
    public void loadStockPrices() {
        StockPriceRepository repository = new StockPriceRepository();
        Map<String, StockPriceRepository.StockPriceData> priceData = repository.loadStockPrices();

        if (priceData.isEmpty()) {
            System.out.println("No price data found to load");
            return;
        }

        // Apply loaded prices to instruments
        for (Map.Entry<String, StockPriceRepository.StockPriceData> entry : priceData.entrySet()) {
            String symbol = entry.getKey();
            StockPriceRepository.StockPriceData data = entry.getValue();

            Instrument instrument = model.getInstrument(symbol);
            if (instrument != null && data.priceHistory != null) {
                // Set current price
                if (data.currentPrice != null) {
                    instrument.setCurrentPrice(data.currentPrice, 0);
                }

                // Load price history

                for (var point : data.priceHistory) {
                    instrument.getPriceHistory().addPrice(point.getPrice(), point.getTimestamp());
                }
            }
        }

        System.out.println("Loaded price data for " + priceData.size() + " stocks");
    }
}
