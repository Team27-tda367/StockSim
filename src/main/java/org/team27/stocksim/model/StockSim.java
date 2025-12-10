package org.team27.stocksim.model;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.instruments.InstrumentRegistry;
import org.team27.stocksim.model.instruments.StockFactory;
import org.team27.stocksim.model.market.Market;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.market.OrderBook;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.simulation.MarketSimulator;
import org.team27.stocksim.model.users.*;
import org.team27.stocksim.observer.ModelObserver;
import org.team27.stocksim.observer.ModelSubject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StockSim implements ModelSubject {
    private final List<ModelObserver> observers = new ArrayList<>();

    // Specialized components
    private final Market market;
    private final InstrumentRegistry instrumentRegistry;
    private final TraderRegistry traderRegistry;
    private final MarketSimulator marketSimulator;

    public StockSim() {
        // Initialize registries
        this.instrumentRegistry = new InstrumentRegistry(new StockFactory());
        this.traderRegistry = new TraderRegistry(new UserFactory(), new BotFactory());

        // Initialize market
        this.market = new Market();

        // Set up market callbacks
        market.setOnPriceUpdate(unused -> notifyPriceUpdate(instrumentRegistry.getAllInstruments()));
        market.setOnTradeSettled(trade -> {
            notifyTradeSettled();

            String buyerTraderId = getTraderIdForOrder(trade.getBuyOrderId());
            String sellerTraderId = getTraderIdForOrder(trade.getSellOrderId());
            if (buyerTraderId != null && sellerTraderId != null) {
                Trader buyer = traderRegistry.getTrader(buyerTraderId);
                Trader seller = traderRegistry.getTrader(sellerTraderId);
                if (buyer instanceof User || seller instanceof User) {
                    notifyPortfolioChanged();
                }
            }
        });

        // Initialize simulator
        this.marketSimulator = new MarketSimulator(
                traderRegistry::getBots,
                this::onSimulationTick
        );

        System.out.println("Successfully created Sim-model");
    }

    private String getTraderIdForOrder(int orderId) {
        // This is a workaround - ideally we'd have better order tracking
        for (Trader trader : traderRegistry.getAllTraders().values()) {
            if (trader instanceof User user) {
                if (user.getOrderHistory().getAllOrders().stream()
                        .anyMatch(o -> o.getOrderId() == orderId)) {
                    return trader.getId();
                }
            }
        }
        return null;
    }

    private void onSimulationTick() {
        // Execute bot trading decisions
        for (Trader bot : traderRegistry.getBots().values()) {
            if (bot instanceof Bot) {
                ((Bot) bot).decide(this);
            }
        }
        marketSimulator.setTotalTradesExecuted(market.getCompletedTrades().size());
    }


    public void addOrderBook(String symbol, OrderBook orderBook) {
        market.addOrderBook(symbol, orderBook);
    }

    public void removeOrderBook(String symbol) {
        market.removeOrderBook(symbol);
    }

    public OrderBook getOrderBook(String symbol) {
        return market.getOrderBook(symbol);
    }

    public void placeOrder(Order order) {
        market.placeOrder(order, traderRegistry.getAllTraders(), instrumentRegistry.getAllInstruments());
    }


    public void createStock(String symbol, String stockName, String tickSize, String lotSize, String category) {
        instrumentRegistry.createInstrument(symbol, stockName, tickSize, lotSize, category);
    }

    public ArrayList<String> getCategories() {
        return instrumentRegistry.getCategories();
    }

    public HashMap<String, Instrument> getStocks() {
        return instrumentRegistry.getAllInstruments();
    }

    public HashMap<String, Instrument> getStocks(String category) {
        return instrumentRegistry.getInstrumentsByCategory(category);
    }

    public void createUser(String id, String name) {
        traderRegistry.createUser(id, name);
    }

    public void createBot(String id, String name) {
        traderRegistry.createBot(id, name);
    }

    public HashMap<String, Trader> getTraders() {
        return traderRegistry.getAllTraders();
    }

    public HashMap<String, Trader> getBots() {
        return traderRegistry.getBots();
    }

    public HashMap<String, User> getUsers() {
        return traderRegistry.getUsers();
    }

    public User getCurrentUser() {
        return traderRegistry.getCurrentUser();
    }

    public void setCurrentUser(String userId) {
        traderRegistry.setCurrentUser(userId);
    }

    public Portfolio createPortfolio(String id) {
        return traderRegistry.getTrader(id) != null ?
                traderRegistry.getTrader(id).getPortfolio() : null;
    }


    public void startMarketSimulation() {
        marketSimulator.start();
    }

    public void pauseMarketSimulation() {
        marketSimulator.pause();
    }

    public void stopMarketSimulation() {
        marketSimulator.stop();
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

}