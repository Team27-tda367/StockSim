package org.team27.stocksim.model;

import org.team27.stocksim.model.clock.GameClock;
import org.team27.stocksim.model.clock.GameTicker;
import org.team27.stocksim.model.market.*;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.*;
import org.team27.stocksim.observer.ModelEvent;
import org.team27.stocksim.observer.ModelObserver;
import org.team27.stocksim.observer.ModelSubject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    private String createdStockMsg;
    private MatchingEngine matchingEngine;
    private HashMap<String, OrderBook> orderBooks;
    private HashMap<Integer, String> orderIdToTraderId; // maps order ID to trader ID
    private List<Trade> completedTrades; // tracks all completed trades
    private HashMap<String, List<Map<String, Object>>> priceHistory; // tracks price history for each stock
    private GameTicker ticker; // reference to ticker for stopping simulation
    private long simulationStartTime; // tracks when simulation started

    public StockSim() {
        // Stock related inits
        stocks = new HashMap<>();
        stockFactory = new StockFactory();

        // Trader related inits
        orderBooks = new HashMap<>();
        traders = new HashMap<>();
        userFactory = new UserFactory();
        botFactory = new BotFactory();
        matchingEngine = new MatchingEngine();
        orderIdToTraderId = new HashMap<>();
        completedTrades = new ArrayList<>();
        priceHistory = new HashMap<>();

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
        processOrder(order);
    } // TODO: seperate order placing logic from processing logic

    private void processOrder(Order order) {

        List<Trade> trades = matchingEngine.match(order, getOrderBook(order.getSymbol()));

        for (Trade trade : trades) {
            settleTrade(trade);
            completedTrades.add(trade);
        }
    }

    private void settleTrade(Trade trade) {
        String buyerTraderId = orderIdToTraderId.get(trade.getBuyOrderId());
        String sellerTraderId = orderIdToTraderId.get(trade.getSellOrderId());

        if (buyerTraderId == null || sellerTraderId == null) {
            // TODO: handle error
            return;
        }

        Trader buyer = traders.get(buyerTraderId);
        Trader seller = traders.get(sellerTraderId);

        if (buyer == null || seller == null) {
            // TODO: handle error
            return;
        }

        BigDecimal tradeValue = trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity()));

        Portfolio buyerPortfolio = buyer.getPortfolio();
        Portfolio sellerPortfolio = seller.getPortfolio();

        if (buyerPortfolio.withdraw(tradeValue)) {
            sellerPortfolio.deposit(tradeValue);

            sellerPortfolio.removeStock(trade.getStockSymbol(), trade.getQuantity());
            buyerPortfolio.addStock(trade.getStockSymbol(), trade.getQuantity());

            // Set stock price to last trade price
            Instrument stock = stocks.get(trade.getStockSymbol());
            if (stock != null) {
                stock.setCurrentPrice(trade.getPrice());
            }

        } else {
            // TODO: handle error
        }
    }

    public void createStock(String symbol, String stockName, String tickSize, String lotSize) {
        // checking if symbol already exists (if yes -> error)
        String highSymbol = symbol.toUpperCase();
        if (stocks.containsKey(highSymbol)) {
            createdStockMsg = "Symbol already exists!";
        } else {
            Instrument stock = stockFactory.createInstrument(highSymbol, stockName, new BigDecimal(tickSize),
                    Integer.parseInt(lotSize));
            stocks.put(highSymbol, stock);
            String createdStock = highSymbol + " " + stockName + " " + tickSize + " " + lotSize;
            createdStockMsg = "Created stock: " + createdStock;
        }

        notifyObservers(new ModelEvent(ModelEvent.Type.STOCK_CREATED, createdStockMsg));
        notifyObservers(new ModelEvent(ModelEvent.Type.STOCKS_CHANGED, stocks));

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
    public HashMap<String, Instrument> getStocks() {
        return stocks;
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

    public Portfolio createPortfolio(String id) {
        // if id in portfolio-db, fetch balance and insert, else -> new User/Bot, insert
        // startingBalance
        BigDecimal startingBalance = money("10000");
        return new Portfolio(startingBalance);
    }

    private void notifyObservers(ModelEvent event) {
        for (ModelObserver o : observers) {
            o.modelChanged(event);
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
        simulationStartTime = System.currentTimeMillis();

        // start the clock/timer for market simulation
        GameClock clock = new GameClock(
                ZoneId.of("Europe/Stockholm"),
                Instant.now(),
                1.0);

        ticker = new GameTicker(clock, simInstant -> {
            tick();
        });
        ticker.start();

        clock.setSpeed(100000);

        // Schedule simulation stop after 10 seconds
        new Thread(() -> {
            try {
                Thread.sleep(1000); // 10 seconds
                stopMarketSimulation();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // notifyObservers(new ModelEvent(ModelEvent.Type.MARKET_STARTED, "Market
        // simulation started."));

    }

    private void tick() {
        // loop through bots and have them place orders
        for (Trader bot : getBots().values()) {
            ((Bot) bot).decide(this);
        }

        // Write stock prices to JSON file
        writeStockPricesToJson();

    }

    private void writeStockPricesToJson() {
        try {
            Map<String, Map<String, Object>> stockData = new HashMap<>();

            for (Instrument stock : stocks.values()) {
                // Record current price in history
                String symbol = stock.getSymbol();
                priceHistory.putIfAbsent(symbol, new ArrayList<>());

                Map<String, Object> priceSnapshot = new HashMap<>();
                priceSnapshot.put("timestamp", System.currentTimeMillis());
                priceSnapshot.put("price", stock.getCurrentPrice().toString());
                priceHistory.get(symbol).add(priceSnapshot);

                // Build stock info with history
                Map<String, Object> stockInfo = new HashMap<>();
                stockInfo.put("symbol", stock.getSymbol());
                stockInfo.put("name", stock.getName());
                stockInfo.put("currentPrice", stock.getCurrentPrice().toString());
                stockInfo.put("tickSize", stock.getTickSize().toString());
                stockInfo.put("lotSize", stock.getLotSize());
                stockInfo.put("priceHistory", priceHistory.get(symbol));

                stockData.put(stock.getSymbol(), stockInfo);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(stockData);

            try (FileWriter writer = new FileWriter("stock_prices.json")) {
                writer.write(json);
            }

        } catch (IOException e) {
            System.err.println("Error writing stock prices to JSON: " + e.getMessage());
        }
    }

    public void pauseMarketSimulation() {
        state = MarketState.PAUSED;
    }

    public void stopMarketSimulation() {
        state = MarketState.PAUSED;
        if (ticker != null) {
            ticker.stop();
        }
        System.out.println("\n========================================");
        System.out.println("Simulation stopped after 10 seconds");
        System.out.println("Total trades executed: " + completedTrades.size());
        System.out.println("========================================\n");
        // notifyObservers(new ModelEvent(ModelEvent.Type.MARKET_STOPPED, "Market
        // simulation stopped."));
    }

}