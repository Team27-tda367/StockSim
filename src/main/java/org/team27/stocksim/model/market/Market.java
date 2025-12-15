package org.team27.stocksim.model.market;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.users.Trader;
import org.team27.stocksim.model.users.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;


public class Market implements IMarket {

    private final HashMap<String, OrderBook> orderBooks;
    private final MatchingEngine matchingEngine;
    private final SettlementEngine settlementEngine;
    private final List<Trade> completedTrades;
    private final HashMap<Integer, String> orderIdToTraderId;

    private Consumer<Set<String>> onPriceUpdate;
    private Consumer<Trade> onTradeSettled;

    public Market() {
        this.orderBooks = new HashMap<>();
        this.matchingEngine = new MatchingEngine();
        this.completedTrades = new ArrayList<>();
        this.orderIdToTraderId = new HashMap<>();
        this.settlementEngine = new SettlementEngine(orderIdToTraderId, this::handleTradeSettled);
    }

    @Override
    public void placeOrder(Order order, HashMap<String, Trader> traders, HashMap<String, Instrument> stocks) {

        settlementEngine.trackOrder(order.getOrderId(), order.getTraderId());


        recordOrderInHistory(order, traders);


        processOrder(order, traders, stocks);
    }

    private void recordOrderInHistory(Order order, HashMap<String, Trader> traders) {
        Trader trader = traders.get(order.getTraderId());
        if (trader instanceof User) {
            ((User) trader).getOrderHistory().addOrder(order);
            System.out.println(((User) trader).getOrderHistory().getAllOrders().size() + " orders in history");
        }
    }

    private void processOrder(Order order, HashMap<String, Trader> traders, HashMap<String, Instrument> stocks) {
        OrderBook orderBook = getOrderBook(order.getSymbol());
        List<Trade> trades = matchingEngine.match(order, orderBook);

        Set<String> affectedSymbols = new HashSet<>();
        for (Trade trade : trades) {
            completedTrades.add(trade);
            boolean settled = settlementEngine.settleTrade(trade, traders, stocks);
            if (settled) {
                affectedSymbols.add(trade.getStockSymbol());
            }
        }

        if (!affectedSymbols.isEmpty() && onPriceUpdate != null) {
            onPriceUpdate.accept(affectedSymbols);
        }
    }

    private void handleTradeSettled(Trade trade) {
        if (onTradeSettled != null) {
            onTradeSettled.accept(trade);
        }
    }

    @Override
    public void addOrderBook(String symbol, OrderBook orderBook) {
        orderBooks.put(symbol, orderBook);
    }

    @Override
    public void removeOrderBook(String symbol) {
        orderBooks.remove(symbol);
    }

    @Override
    public OrderBook getOrderBook(String symbol) {
        return orderBooks.computeIfAbsent(symbol, OrderBook::new);
    }

    @Override
    public List<Trade> getCompletedTrades() {
        return new ArrayList<>(completedTrades);
    }

    @Override
    public void setOnPriceUpdate(Consumer<Set<String>> callback) {
        this.onPriceUpdate = callback;
    }

    @Override
    public void setOnTradeSettled(Consumer<Trade> callback) {
        this.onTradeSettled = callback;
    }
}
