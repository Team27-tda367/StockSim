package org.team27.stocksim;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.team27.stocksim.data.BotData;
import org.team27.stocksim.data.BotDataLoader;
import org.team27.stocksim.data.StockData;
import org.team27.stocksim.data.StockDataLoader;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.StockPriceRepository;
import org.team27.stocksim.model.BotPositionRepository;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.users.bot.*;

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
        createBotsFromFile();
        model.createUser("user1", "Default User");
        model.setCurrentUser("user1");

        if (loadExistingPrices) {
            // Load existing prices
            loadStockPrices();
        }
        // Start the market simulation
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
                    stock.getCategory());
        }
    }

    private void createBotsFromFile() {
        BotPositionRepository positionRepo = new BotPositionRepository();
        List<BotData> bots;
        
        // Try to load from saved positions first, fallback to defaults
        bots = positionRepo.loadBotPositions();
        
        if (bots == null || bots.isEmpty()) {
            System.out.println("Loading bots from default-bots.json");
            BotDataLoader loader = new BotDataLoader();
            bots = loader.loadDefaultBots();
        } else {
            System.out.println("Loading bots from bot-positions.json");
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
                for (BotData.PositionData position : botData.getInitialPositions()) {
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
            case "WSBstrategy":
                return new WSBstrategy();
            default:
                System.out.println("Unknown strategy: " + strategyName + ", using RandomStrategy");
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
