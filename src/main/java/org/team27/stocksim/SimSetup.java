package org.team27.stocksim;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.team27.stocksim.data.BotData;
import org.team27.stocksim.data.BotDataLoader;
import org.team27.stocksim.data.PositionData;
import org.team27.stocksim.data.StockData;
import org.team27.stocksim.data.StockDataLoader;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.users.bot.*;
import org.team27.stocksim.repository.BotPositionRepository;
import org.team27.stocksim.repository.StockPriceRepository;

public class SimSetup {
    private final StockSim model;

    public SimSetup(StockSim model) {
        this.model = model;
    }

    public void start() {
        start(false);
    }

    public void startWithLoadedPrices() {
        start(true);
    }

    private void start(boolean loadExistingPrices) {
        createDefaultStocks();
        createBotsFromFile(loadExistingPrices);
        model.createUser("user1", "Default User", 1000000);
        model.setCurrentUser("user1");

        if (loadExistingPrices) {
            // Load existing prices
            loadStockPrices();
        }
        model.startMarketSimulation();
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
                    stock.getCategory(),
                    stock.getInitialPrice());
        }
    }

    private void createBotsFromFile(boolean loadExistingPrices) {
        BotPositionRepository positionRepo = new BotPositionRepository();
        List<BotData> bots;

        if (loadExistingPrices) {
            bots = positionRepo.loadBotPositions();
        } else {
            bots = null;
        }

        if (bots == null || bots.isEmpty()) {
            BotDataLoader loader = new BotDataLoader();
            bots = loader.loadDefaultBots();
        }

        for (BotData botData : bots) {
            // Create strategy first
            IBotStrategy strategy = createStrategy(botData.getStrategy());

            // Create the bot with strategy via constructor (DIP)
            model.createBot(botData.getId(), botData.getName(), strategy);

            // Get the created bot and initialize its positions (use uppercase ID)
            var trader = model.getTraders().get(botData.getId().toUpperCase());
            if (trader instanceof Bot) {
                Bot bot = (Bot) trader;

                // Initialize positions
                for (PositionData position : botData.getPositions()) {
                    BigDecimal costBasis = new BigDecimal(position.getCostBasis());
                    bot.getPortfolio().addStock(
                            position.getSymbol(),
                            position.getQuantity(),
                            costBasis,
                            null);
                }
            }
        }
    }

    // TODO refactor to factory pattern if more strategies are added
    private IBotStrategy createStrategy(String strategyName) {
        if (strategyName == null || strategyName.isEmpty()) {
            return new RandomStrategy();
        }

        switch (strategyName) {
            case "RandomStrategy":
                return new RandomStrategy();
            case "HodlerStrategy":
                return new HodlerStrategy();
            case "MomentumTraderStrategy":
                return new MomentumTraderStrategy();
            case "DayTraderStrategy":
                return new DayTraderStrategy();
            case "PanicSellerStrategy":
                return new PanicSellerStrategy();
            case "FocusedTraderStrategy":
                return new FocusedTraderStrategy();
            case "InstitutionalInvestorStrategy":
                return new InstitutionalInvestorStrategy();
            default:
                return new RandomStrategy();
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
    }
}
