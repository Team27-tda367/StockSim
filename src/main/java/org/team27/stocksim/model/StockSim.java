package org.team27.stocksim.model;

import org.team27.stocksim.model.clock.GameClock;
import org.team27.stocksim.model.clock.GameTicker;
import org.team27.stocksim.model.instruments.*;
import org.team27.stocksim.model.market.*;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.*;
import org.team27.stocksim.observer.ModelObserver;
import org.team27.stocksim.observer.ModelSubject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.stmt.query.In;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.team27.stocksim.model.util.MoneyUtils.money;

public class StockSim implements ModelSubject {
    /* Listeners */
    private final List<ModelObserver> observers = new ArrayList<>();
    MarketState state;
    HashMap<String, Instrument> stocks;
    InstrumentFactory stockFactory;
    HashMap<String, Trader> traders;
    TraderFactory userFactory;
    TraderFactory botFactory;
    private MatchingEngine matchingEngine;
    private HashMap<String, OrderBook> orderBooks;
    private HashMap<Integer, String> orderIdToTraderId; // maps order ID to trader ID
    private List<Trade> completedTrades; // tracks all completed trades
    private GameTicker ticker; // reference to ticker for stopping simulation
    private GameClock clock; // game clock for simulation time
    private static User currentUser;
    private SelectionManager selectionManager; // manages currently selected stock

    public StockSim() {
        // Stock related inits
        stocks = new HashMap<>();
        stockFactory = new StockFactory();
        selectionManager = new SelectionManager();

        // Trader related inits
        orderBooks = new HashMap<>();
        traders = new HashMap<>();
        userFactory = new UserFactory();
        botFactory = new BotFactory();
        matchingEngine = new MatchingEngine();
        orderIdToTraderId = new HashMap<>();
        completedTrades = new ArrayList<>();

        System.out.println("Succesfully created Sim-model");
    }

    // OrderBook logic and Matching Engine
    public void addOrderBook(String symbol, OrderBook orderBook) {
        orderBooks.put(symbol, orderBook);
    }

    public void removeOrderBook(String symbol) {
        orderBooks.remove(symbol);
    }

    public OrderBook getOrderBook(String symbol) {
        OrderBook book = orderBooks.computeIfAbsent(
                symbol,
                OrderBook::new);
        return orderBooks.get(symbol);
    }

    public void placeOrder(Order order) {
        // TODO: validation of order (enough balance, enough stocks in portfolio, lot
        // size, tick size, etc)
        // Track the order ID to trader ID mapping
        orderIdToTraderId.put(order.getOrderId(), order.getTraderId());

        // Record order in user's history (only for Users, not Bots)
        Trader trader = traders.get(order.getTraderId());
        if (trader instanceof User) {
            ((User) trader).getOrderHistory().addOrder(order);
            System.out.println(((User) trader).getOrderHistory().getAllOrders().size() + " orders in history");
        }

        processOrder(order);
    } // TODO: seperate order placing logic from processing logic

    private void processOrder(Order order) {

        List<Trade> trades = matchingEngine.match(order, getOrderBook(order.getSymbol()));

        boolean priceChanged = false;
        for (Trade trade : trades) {
            completedTrades.add(trade);
            boolean tradeSettled = settleTrade(trade);
            if (tradeSettled) {
                priceChanged = true;
            }
        }

        // Only notify observers if a price actually changed
        if (priceChanged) {
            notifyPriceUpdate(stocks);
        }
    }

    private boolean settleTrade(Trade trade) {
        String buyerTraderId = orderIdToTraderId.get(trade.getBuyOrderId());
        String sellerTraderId = orderIdToTraderId.get(trade.getSellOrderId());

        if (buyerTraderId == null || sellerTraderId == null) {
            // TODO: handle error
            return false;
        }

        Trader buyer = traders.get(buyerTraderId);
        Trader seller = traders.get(sellerTraderId);

        if (buyer == null || seller == null) {
            // TODO: handle error
            return false;
        }

        BigDecimal tradeValue = trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity()));

        Portfolio buyerPortfolio = buyer.getPortfolio();
        Portfolio sellerPortfolio = seller.getPortfolio();

        if (buyerPortfolio.withdraw(tradeValue)) {
            sellerPortfolio.deposit(tradeValue);

            sellerPortfolio.removeStock(trade.getStockSymbol(), trade.getQuantity(), trade);
            buyerPortfolio.addStock(trade.getStockSymbol(), trade.getQuantity(), trade.getPrice(), trade);

            // Record trade in user histories (only for Users, not Bots)
            if (buyer instanceof User) {
                ((User) buyer).getOrderHistory().addTrade(trade);
                System.out.println(((User) buyer).getOrderHistory().getAllTrades().size() + " trades in history");
            }
            if (seller instanceof User) {
                ((User) seller).getOrderHistory().addTrade(trade);
                System.out.println(
                        ((User) seller).getOrderHistory().getAllTrades().size() + "(seller) trades in history");
            }

            notifyTradeSettled();

            // if users are involved, notify portfolio changed
            if (buyer instanceof User || seller instanceof User) {
                notifyPortfolioChanged(); // Notify that portfolios have changed

            }
            // Set stock price to last trade price
            Instrument stock = stocks.get(trade.getStockSymbol());
            if (stock != null) {
                long timestamp = (clock != null) ? clock.instant().toEpochMilli() : System.currentTimeMillis();
                stock.setCurrentPrice(trade.getPrice(), timestamp);
            }

            return true; // Price changed

        } else {
            // TODO: handle error
            return false;
        }
    }

    public void createStock(String symbol, String stockName, String tickSize, String lotSize, String category) {
        // checking if symbol already exists (if yes -> error)
        String highSymbol = symbol.toUpperCase();
        if (stocks.containsKey(highSymbol)) {
            System.out.println("Stock symbol already exists");
        } else {
            Instrument stock = stockFactory.createInstrument(highSymbol, stockName, new BigDecimal(tickSize),
                    Integer.parseInt(lotSize), category);
            stocks.put(highSymbol, stock);
        }

    }

    // Trader logic
    public void createUser(String id, String name) {
        String highId = id.toUpperCase();
        if (traders.containsKey(highId)) {
            System.out.println("userID already exists");
        } else {
            Portfolio portfolio = createPortfolio(highId);
            Trader user = userFactory.createTrader(highId, name, portfolio);
            traders.put(highId, user);
        }
    }

    public void createBot(String id, String name) {
        String highId = id.toUpperCase();
        if (traders.containsKey(highId)) {
            System.out.println("botID already exists");
        } else {
            Portfolio portfolio = createPortfolio(highId);
            Trader bot = botFactory.createTrader(highId, name, portfolio);
            traders.put(highId, bot);
        }
    }

    // Getters
    public ArrayList<String> getCategories() {
        ArrayList<String> categoryLabels = new ArrayList<>();
        for (ECategory c : ECategory.values()) {
            categoryLabels.add(c.getLabel());
        }
        return categoryLabels;
    }

    public HashMap<String, Instrument> getStocks() {
        return stocks;
    }

    public HashMap<String, Instrument> getStocks(String category) {
        if (category.equals("All")) {
            return stocks;
        } else {
            HashMap<String, Instrument> filteredStocks = new HashMap<>();
            for (Instrument stock : stocks.values()) {
                if (stock.getCategory().equals(category)) {
                    filteredStocks.put(stock.getSymbol(), stock);
                }
            }
            return filteredStocks;
        }
    }

    public HashMap<String, Trader> getTraders() {
        return traders;
    }

    public HashMap<String, Trader> getBots() {
        HashMap<String, Trader> bots = new HashMap<>();
        for (String id : traders.keySet()) {
            Trader trader = traders.get(id);
            if (trader instanceof Bot) {
                bots.put(id, trader);
            }
        }
        return bots;
    }

    public HashMap<String, User> getUsers() {
        HashMap<String, User> users = new HashMap<>();

        for (Map.Entry<String, Trader> entry : traders.entrySet()) {
            if (entry.getValue() instanceof User) {
                users.put(entry.getKey(), (User) entry.getValue());
            }
        }
        return users;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String userId) {
        currentUser = (User) getUsers().get(userId.toUpperCase());
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public GameClock getClock() {
        return clock;
    }

    public Portfolio createPortfolio(String id) {
        // if id in portfolio-db, fetch balance and insert, else -> new User/Bot, insert
        // startingBalance
        BigDecimal startingBalance = money("10000");
        return new Portfolio(startingBalance);
    }

    private void notifyPriceUpdate(HashMap<String, Instrument> stocks) {
        for (ModelObserver o : observers) {
            o.onPriceUpdate(stocks);
        }
    }

    private void notifyTradeSettled() {
        for (ModelObserver o : observers) {
            o.onTradeSettled();
        }
    }

    private void notifyStocksChanged(Object payload) {
        for (ModelObserver o : observers) {
            o.onStocksChanged(payload);
        }
    }

    private void notifyPortfolioChanged() {
        for (ModelObserver o : observers) {
            o.onPortfolioChanged();
        }
    }

    @Override
    public void addObserver(ModelObserver obs) {
        observers.add(obs);
    }

    @Override
    public void removeObserver(ModelObserver obs) {
        observers.remove(obs);
    }

    public void startMarketSimulation() {
        state = MarketState.RUNNING;

        // start the clock/timer for market simulation
        clock = new GameClock(
                ZoneId.of("Europe/Stockholm"),
                Instant.now(),
                3600); // 1 real second = 1 in-game hour

        ticker = new GameTicker(clock, simInstant -> {
            tick();
        });
        ticker.start();

        new Thread(() -> {
            try {
                Thread.sleep(10000); // let the market run for 10 seconds
                clock.setSpeed(5);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // TODO: Add market started notification if needed
        // notifyMarketStarted();

    }

    private void tick() {
        // loop through bots and have them place orders
        for (Trader bot : getBots().values()) {
            ((Bot) bot).decide(this);
        }
    }

    public void pauseMarketSimulation() {
        state = MarketState.PAUSED;
    }

    private void stopMarketSimulation() {
        if (ticker != null) {
            ticker.stop();
        }
        state = MarketState.PAUSED;

        try {
            savePriceHistoryToFile();
        } catch (IOException e) {
            System.err.println("Error saving price history: " + e.getMessage());
        }

    }

    /**
     * Writes all stocks' price history to the stock_prices.json file.
     * The JSON structure includes stock symbol, name, and price history with
     * timestamps and prices.
     * 
     * @throws IOException if there's an error writing to the file
     */
    public void savePriceHistoryToFile() throws IOException {
        // Create a list to hold all stock data
        List<Map<String, Object>> stocksData = new ArrayList<>();

        for (Instrument stock : stocks.values()) {
            Map<String, Object> stockData = new HashMap<>();
            stockData.put("symbol", stock.getSymbol());
            stockData.put("name", stock.getName());
            stockData.put("category", stock.getCategory());
            stockData.put("currentPrice", stock.getCurrentPrice());

            // Get price history
            PriceHistory priceHistory = stock.getPriceHistory();
            List<Map<String, Object>> pricePoints = new ArrayList<>();

            for (PricePoint point : priceHistory.getPoints()) {
                Map<String, Object> pointData = new HashMap<>();
                pointData.put("timestamp", point.getTimestamp());
                pointData.put("price", point.getPrice());
                pricePoints.add(pointData);
            }

            stockData.put("priceHistory", pricePoints);
            stocksData.add(stockData);
        }

        // Convert to JSON and write to file
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(stocksData);

        // Get the path to resources/db/stock_prices.json
        String filePath = "src/main/resources/db/stock_prices.json";

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(json);
            System.out.println("Successfully saved price history to " + filePath);
        }
    }
}