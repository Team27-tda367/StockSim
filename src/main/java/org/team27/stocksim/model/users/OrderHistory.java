package org.team27.stocksim.model.users;

import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.market.Trade;
import org.team27.stocksim.model.util.dto.OrderDTO;
import org.team27.stocksim.model.util.dto.OrderMapper;
import org.team27.stocksim.model.util.dto.TradeDTO;
import org.team27.stocksim.model.util.dto.TradeMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tracks a trader's order and trade history.
 * Maintains records of all orders placed and trades executed.
 * Provides DTOs for external access to maintain encapsulation.
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
     * Get all orders as DTOs for external consumption.
     * 
     * @return List of order DTOs
     */
    public List<OrderDTO> getAllOrdersDTO() {
        return orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all trades as DTOs for external consumption.
     * 
     * @return List of trade DTOs
     */
    public List<TradeDTO> getAllTradesDTO() {
        return trades.stream()
                .map(TradeMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get orders for a specific symbol as DTOs.
     * 
     * @param symbol The stock symbol
     * @return List of order DTOs for the symbol
     */
    public List<OrderDTO> getOrdersBySymbolDTO(String symbol) {
        return orders.stream()
                .filter(order -> order.getSymbol().equals(symbol))
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get trades for a specific symbol as DTOs.
     * 
     * @param symbol The stock symbol
     * @return List of trade DTOs for the symbol
     */
    public List<TradeDTO> getTradesBySymbolDTO(String symbol) {
        return trades.stream()
                .filter(trade -> trade.getStockSymbol().equals(symbol))
                .map(TradeMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all buy orders as DTOs.
     * 
     * @return List of buy order DTOs
     */
    public List<OrderDTO> getBuyOrdersDTO() {
        return orders.stream()
                .filter(Order::isBuyOrder)
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all sell orders as DTOs.
     * 
     * @return List of sell order DTOs
     */
    public List<OrderDTO> getSellOrdersDTO() {
        return orders.stream()
                .filter(order -> !order.isBuyOrder())
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get filled orders as DTOs.
     * 
     * @return List of filled order DTOs
     */
    public List<OrderDTO> getFilledOrdersDTO() {
        return orders.stream()
                .filter(Order::isFilled)
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get active (non-filled, non-cancelled) orders as DTOs.
     * 
     * @return List of active order DTOs
     */
    public List<OrderDTO> getActiveOrdersDTO() {
        return orders.stream()
                .filter(order -> order.getStatus() != Order.Status.FILLED
                        && order.getStatus() != Order.Status.CANCELLED)
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Internal method: Get all orders (for model layer use only).
     * External callers should use getAllOrdersDTO().
     * 
     * @return Unmodifiable list of all orders
     */
    public List<Order> getAllOrders() {
        return Collections.unmodifiableList(orders);
    }

    /**
     * Internal method: Get all trades (for model layer use only).
     * External callers should use getAllTradesDTO().
     * 
     * @return Unmodifiable list of all trades
     */
    public List<Trade> getAllTrades() {
        return Collections.unmodifiableList(trades);
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
