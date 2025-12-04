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
            notifyObservers(new ModelEvent(ModelEvent.Type.PRICE_UPDATE, stocks));
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

            sellerPortfolio.removeStock(trade.getStockSymbol(), trade.getQuantity());
            buyerPortfolio.addStock(trade.getStockSymbol(), trade.getQuantity());

            // Set stock price to last trade price
            Instrument stock = stocks.get(trade.getStockSymbol());
            if (stock != null) {
                stock.setCurrentPrice(trade.getPrice());
            }

            return true; // Price changed

        } else {
            // TODO: handle error
            return false;
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

        /*
         * notifyObservers(new ModelEvent(ModelEvent.Type.STOCK_CREATED,
         * createdStockMsg));
         * notifyObservers(new ModelEvent(ModelEvent.Type.STOCKS_CHANGED, stocks));
         */

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
                10000);

        ticker = new GameTicker(clock, simInstant -> {
            tick();
        });
        ticker.start();

        new Thread(() -> {
            try {
                Thread.sleep(1000); // 1 second
                clock.setSpeed(100);
                Thread.sleep(1000); // 1 seconds

                ticker.setCallback(simInstant -> {
                    tick();
                    // Price updates are now notified from processOrder only when trades occur
                });

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