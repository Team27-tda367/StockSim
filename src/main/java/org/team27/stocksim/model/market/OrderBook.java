package org.team27.stocksim.model.market;

import java.util.ArrayList;
import java.util.PriorityQueue;

import static java.util.Comparator.comparing;

/**
 * Maintains and organizes all pending orders for a specific instrument.
 *
 * <p>The order book uses priority queues to maintain buy and sell orders sorted
 * by price-time priority. Buy orders (bids) are sorted by highest price first,
 * while sell orders (asks) are sorted by lowest price first. Within the same
 * price level, earlier orders have priority (time priority).</p>
 *
 * <p><strong>Design Patterns:</strong> Repository + Priority Queue</p>
 * <ul>
 *   <li>Bids sorted by price DESC, then timestamp ASC (price-time priority)</li>
 *   <li>Asks sorted by price ASC, then timestamp ASC (price-time priority)</li>
 *   <li>Thread-safe operations using synchronized methods</li>
 *   <li>Efficient O(log n) insertion and O(1) best price retrieval</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * OrderBook orderBook = new OrderBook("AAPL");
 *
 * // Add buy order
 * Order buyOrder = new Order(Order.Side.BUY, "AAPL", new BigDecimal("150.00"), 100, "trader1");
 * orderBook.add(buyOrder);
 *
 * // Get best bid (highest buy price)
 * Order bestBid = orderBook.getBestBid();
 *
 * // Get best ask (lowest sell price)
 * Order bestAsk = orderBook.getBestAsk();
 *
 * // Fill order
 * orderBook.fillOrder(buyOrder, 50);
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see Order
 * @see Market
 * @see MatchingEngine
 */
public class OrderBook {
    /**
     * Priority queue of buy orders (bids), sorted by price DESC then time ASC.
     * Higher prices and earlier timestamps have priority.
     */
    private final PriorityQueue<Order> bids = new PriorityQueue<>(comparing(Order::getPrice).reversed().thenComparing(Order::getTimeStamp));

    /**
     * Priority queue of sell orders (asks), sorted by price ASC then time ASC.
     * Lower prices and earlier timestamps have priority.
     */
    private final PriorityQueue<Order> asks = new PriorityQueue<>(comparing(Order::getPrice).thenComparing(Order::getTimeStamp));

    /**
     * Symbol of the instrument this order book manages.
     */
    private final String symbol;

    /**
     * Constructs an order book for the specified instrument symbol.
     *
     * @param symbol The stock/instrument symbol this order book manages
     */
    public OrderBook(String symbol) {
        this.symbol = symbol;
    }

    public synchronized void add(Order order) {
        if (order.isBuyOrder()) {
            bids.add(order);
        } else {
            asks.add(order);
        }
    }

    public synchronized void remove(Order order) {
        if (order.isBuyOrder()) {
            bids.remove(order);
        } else {
            asks.remove(order);
        }
    }

    public synchronized Order getBestBid() {
        return bids.peek();
    }

    public synchronized Order getBestAsk() {
        return asks.peek();
    }

    public synchronized ArrayList<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.addAll(bids);
        orders.addAll(asks);
        return orders;
    }

    public synchronized void fillOrder(Order order, int quantity) {
        order.fill(quantity);
    }
}
