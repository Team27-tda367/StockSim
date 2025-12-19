package org.team27.stocksim.model.market;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.users.Trader;
import org.team27.stocksim.model.users.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;


public class Market implements IMarket {

    private final ConcurrentHashMap<String, OrderBook> orderBooks;
    private final MatchingEngine matchingEngine;
    private final SettlementEngine settlementEngine;
    private final List<Trade> completedTrades;
    private final ConcurrentHashMap<Integer, String> orderIdToTraderId;

    private Consumer<Set<String>> onPriceUpdate;
    private Consumer<Trade> onTradeSettled;

    public Market() {
        this.orderBooks = new ConcurrentHashMap<>();
        this.matchingEngine = new MatchingEngine();
        this.completedTrades = new CopyOnWriteArrayList<>();
        this.orderIdToTraderId = new ConcurrentHashMap<>();
        this.settlementEngine = new SettlementEngine(orderIdToTraderId, this::handleTradeSettled);
    }

    @Override
    public void placeOrder(Order order, HashMap<String, Trader> traders, HashMap<String, Instrument> stocks) {

        settlementEngine.trackOrder(order.getOrderId(), order.getTraderId());


        recordOrderInHistory(order, traders);


        processOrder(order, traders, stocks);
    }

    @Override
    public void cancelOrder(int orderId, HashMap<String, Trader> traders) {
        String traderId = orderIdToTraderId.get(orderId);
        if (traderId == null) {
            return;
        }

        Trader trader = traders.get(traderId);
        if (!(trader instanceof User user)) {
            return;
        }

        Order order = user.getOrderHistory().getOrderById(orderId);
        if (order == null) {
            return;
        }

        // Only cancel if the order is active (not filled or already cancelled)
        if (order.getStatus() != Order.Status.FILLED && order.getStatus() != Order.Status.CANCELLED) {
            // Remove from order book
            OrderBook orderBook = getOrderBook(order.getSymbol());
            if (orderBook != null) {
                orderBook.remove(order);
            }

            // Mark as cancelled
            order.cancel();
        }
    }

    private void recordOrderInHistory(Order order, HashMap<String, Trader> traders) {
        Trader trader = traders.get(order.getTraderId());
        if (trader instanceof User) {
            ((User) trader).getOrderHistory().addOrder(order);
        }
    }

    private void processOrder(Order order, HashMap<String, Trader> traders, HashMap<String, Instrument> stocks) {
        OrderBook orderBook = getOrderBook(order.getSymbol());

        // Synchronize at the orderBook level to ensure atomic matching and settlement
        synchronized (orderBook) {
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
