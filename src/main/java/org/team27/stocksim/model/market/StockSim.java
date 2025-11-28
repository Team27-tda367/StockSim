package org.team27.stocksim.model.market;

import java.util.ArrayList;
import java.util.List;

import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.BotFactory;
import org.team27.stocksim.model.users.Trader;
import org.team27.stocksim.model.users.TraderFactory;
import org.team27.stocksim.model.users.UserFactory;

import java.util.HashMap;

public class StockSim {
    MarketState state;
    HashMap<String, Instrument> stocks;
    InstrumentFactory stockFactory;
    HashMap<String, Trader> traders;
    TraderFactory userFactory;
    TraderFactory botFactory;

    private String createdStockMsg = "Initial message";

    public String messageCreatedStock() {
        return createdStockMsg;
    }

    public StockSim() {
        // Stock related inits
        stocks = new HashMap<>();
        stockFactory = new StockFactory();

        // Trader related inits
        traders = new HashMap<>();
        userFactory = new UserFactory();
        botFactory = new BotFactory();

        System.out.println("Succesfully created Sim-model");
    }

    /* Listeners */
    private final List<StockSimListener> listeners = new ArrayList<>();

    public void addListener(StockSimListener l) {
        listeners.add(l);
    }

    public void removeListener(StockSimListener l) {
        listeners.remove(l);
    }

    private void notifyListeners(String newMessage) {
        for (StockSimListener l : List.copyOf(listeners)) {
            l.messageChanged(newMessage);
        }
    }

    public void createStock(String symbol, String stockName, String tickSize, String lotSize) {
        // checking if symbol already exists (if yes -> error)
        String highSymbol = symbol.toUpperCase();
        if (stocks.containsKey(highSymbol)) {
            createdStockMsg = "Symbol already exists!";
        } else {
            Instrument stock = stockFactory.createInstrument(highSymbol, stockName, Double.parseDouble(tickSize),
                    Integer.parseInt(lotSize));
            stocks.put(highSymbol, stock);
            String createdStock = highSymbol + " " + stockName + " " + tickSize + " " + lotSize;
            createdStockMsg = createdStock;
        }

        notifyListeners(createdStockMsg);
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
        double startingBalance = 10000;
        return new Portfolio(startingBalance);
    }

}