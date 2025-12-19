package org.team27.stocksim.model.users;

import org.team27.stocksim.dto.OrderDTO;
import org.team27.stocksim.dto.OrderMapper;
import org.team27.stocksim.dto.TradeDTO;
import org.team27.stocksim.dto.TradeMapper;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.market.Trade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tracks a trader's complete order and trade history.
 *
 * <p>OrderHistory maintains chronological records of all orders placed and trades
 * executed by a user. It provides filtered access and converts internal domain
 * objects to DTOs for safe external consumption, maintaining encapsulation.</p>
 *
 * <p><strong>Design Pattern:</strong> Repository + DTO Conversion</p>
 * <ul>
 *   <li>Maintains complete audit trail of trading activity</li>
 *   <li>Converts domain objects to DTOs for view layer</li>
 *   <li>Provides filtered access by symbol</li>
 *   <li>Ensures encapsulation by returning DTOs not domain objects</li>
 *   <li>Supports order history display and reporting</li>
 * </ul>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Chronological order recording</li>
 *   <li>Trade execution history</li>
 *   <li>Symbol-based filtering</li>
 *   <li>DTO conversion for safe external access</li>
 *   <li>Separate collections for orders and trades</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * OrderHistory history = new OrderHistory();
 *
 * // Record orders and trades
 * history.addOrder(buyOrder);
 * history.addTrade(executedTrade);
 *
 * // Retrieve for display
 * List<OrderDTO> allOrders = history.getAllOrdersDTO();
 * List<TradeDTO> allTrades = history.getAllTradesDTO();
 *
 * // Filter by symbol
 * List<OrderDTO> appleOrders = history.getOrdersBySymbolDTO("AAPL");
 * List<TradeDTO> appleTrades = history.getTradesBySymbolDTO("AAPL");
 *
 * // Access internal objects (package-private)
 * List<Order> orders = history.getAllOrders();
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see Order
 * @see Trade
 * @see OrderDTO
 * @see TradeDTO
 * @see User
 */
public class OrderHistory {

    /**
     * Chronological list of all orders placed.
     */
    private final List<Order> orders;

    /**
     * Chronological list of all trades executed.
     */
    private final List<Trade> trades;

    /**
     * Constructs an empty OrderHistory.
     */
    public OrderHistory() {
        this.orders = new ArrayList<>();
        this.trades = new ArrayList<>();
    }

    /**
     * Adds an order to the history.
     *
     * @param order The order to record
     */
    public void addOrder(Order order) {
        orders.add(order);
    }

    /**
     * Adds a trade to the history.
     *
     * @param trade The trade to record
     */
    public void addTrade(Trade trade) {
        trades.add(trade);
    }

    /**
     * Gets all orders as DTOs for external consumption.
     *
     * @return List of order DTOs
     */
    public List<OrderDTO> getAllOrdersDTO() {
        return orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets all trades as DTOs for external consumption.
     *
     * @return List of trade DTOs
     */
    public List<TradeDTO> getAllTradesDTO() {
        return trades.stream()
                .map(TradeMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets orders for a specific symbol as DTOs.
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
     * Get an order by its ID.
     * 
     * @param orderId The order ID to find
     * @return The order, or null if not found
     */
    public Order getOrderById(int orderId) {
        return orders.stream()
                .filter(order -> order.getOrderId() == orderId)
                .findFirst()
                .orElse(null);
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
