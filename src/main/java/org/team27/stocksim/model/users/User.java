package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;

/**
 * Represents a human-controlled trader in the simulation.
 *
 * <p>A User extends Trader and adds order history tracking, allowing the
 * simulation to maintain a complete record of all orders placed by human
 * players. Unlike bots, users do not have automated trading behavior.</p>
 *
 * <p><strong>Design Pattern:</strong> Inheritance (Trader hierarchy)</p>
 * <ul>
 *   <li>Maintains order history for audit and display purposes</li>
 *   <li>Represents human participants in the simulation</li>
 *   <li>No automated trading behavior (unlike Bot)</li>
 *   <li>Portfolio management inherited from Trader</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Users are typically created through TraderRegistry
 * TraderRegistry registry = new TraderRegistry(userFactory, botFactory);
 * registry.createUser("user1", "John Doe", 10000);
 * User user = registry.getUsers().get("user1");
 *
 * // Place order and track in history
 * Order order = new Order(Order.Side.BUY, "AAPL", new BigDecimal("150.00"), 10, user.getId());
 * market.placeOrder(order, traders, instruments);
 *
 * // Review order history
 * OrderHistory history = user.getOrderHistory();
 * List<Order> allOrders = history.getAllOrders();
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see Trader
 * @see Bot
 * @see OrderHistory
 * @see UserFactory
 */
public class User extends Trader {
    /**
     * Complete history of all orders placed by this user.
     */
    private OrderHistory orderHistory;

    /**
     * Package-private constructor for use by UserFactory.
     *
     * @param id Unique identifier for the user
     * @param name Display name of the user
     * @param portfolio User's portfolio containing cash and positions
     */
    User(String id, String name, Portfolio portfolio) {
        super(id, name, portfolio);
        this.orderHistory = new OrderHistory();
    }

    public OrderHistory getOrderHistory() {
        return orderHistory;
    }
}
