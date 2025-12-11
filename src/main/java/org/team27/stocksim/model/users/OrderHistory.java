package org.team27.stocksim.model.users;

import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.market.Trade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tracks a trader's order and trade history.
 * Maintains records of all orders placed and trades executed.
 */
public class OrderHistory {

    private final List<Order> orders;
    private final List<Trade> trades;

    public OrderHistory() {
        this.orders = new ArrayList<>();
        this.trades = new ArrayList<>();
    }

    /**
     * Add an order to the history.
     * 
     * @param order The order to record
     */
    public void addOrder(Order order) {
        orders.add(order);
    }

    /**
     * Add a trade to the history.
     * 
     * @param trade The trade to record
     */
    public void addTrade(Trade trade) {
        trades.add(trade);
    }

    /**
     * Get all orders.
     * 
     * @return Unmodifiable list of all orders
     */
    public List<Order> getAllOrders() {
        return Collections.unmodifiableList(orders);
    }

    /**
     * Get all trades.
     * 
     * @return Unmodifiable list of all trades
     */
    public List<Trade> getAllTrades() {
        return Collections.unmodifiableList(trades);
    }

    /**
     * Get orders for a specific symbol.
     * 
     * @param symbol The stock symbol
     * @return List of orders for the symbol
     */
    public List<Order> getOrdersBySymbol(String symbol) {
        return orders.stream()
                .filter(order -> order.getSymbol().equals(symbol))
                .collect(Collectors.toList());
    }

    /**
     * Get trades for a specific symbol.
     * 
     * @param symbol The stock symbol
     * @return List of trades for the symbol
     */
    public List<Trade> getTradesBySymbol(String symbol) {
        return trades.stream()
                .filter(trade -> trade.getStockSymbol().equals(symbol))
                .collect(Collectors.toList());
    }

    /**
     * Get all buy orders.
     * 
     * @return List of buy orders
     */
    public List<Order> getBuyOrders() {
        return orders.stream()
                .filter(Order::isBuyOrder)
                .collect(Collectors.toList());
    }

    /**
     * Get all sell orders.
     * 
     * @return List of sell orders
     */
    public List<Order> getSellOrders() {
        return orders.stream()
                .filter(order -> !order.isBuyOrder())
                .collect(Collectors.toList());
    }

    /**
     * Get filled orders.
     * 
     * @return List of filled orders
     */
    public List<Order> getFilledOrders() {
        return orders.stream()
                .filter(Order::isFilled)
                .collect(Collectors.toList());
    }

    /**
     * Get active (non-filled, non-cancelled) orders.
     * 
     * @return List of active orders
     */
    public List<Order> getActiveOrders() {
        return orders.stream()
                .filter(order -> order.getStatus() != Order.Status.FILLED
                        && order.getStatus() != Order.Status.CANCELLED)
                .collect(Collectors.toList());
    }

    /**
     * Get the total number of orders.
     * 
     * @return Number of orders
     */
    public int getOrderCount() {
        return orders.size();
    }

    /**
     * Get the total number of trades.
     * 
     * @return Number of trades
     */
    public int getTradeCount() {
        return trades.size();
    }

    /**
     * Clear all history.
     */
    public void clear() {
        orders.clear();
        trades.clear();
    }
}
