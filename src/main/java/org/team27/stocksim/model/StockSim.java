package org.team27.stocksim.model;

import org.team27.stocksim.model.market.*;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.*;
import org.team27.stocksim.observer.ModelObserver;
import org.team27.stocksim.observer.ModelSubject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private String createdStockMsg = "Initial message";
    private MatchingEngine matchingEngine;
    private HashMap<String, OrderBook> orderBooks;
    private HashMap<Integer, String> orderIdToTraderId; // maps order ID to trader ID

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
            createdStockMsg = createdStock;
        }

        notifyObservers();
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

    private void notifyObservers() {
        for (ModelObserver obs : observers) {
            obs.newStockCreated(stocks);
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
}