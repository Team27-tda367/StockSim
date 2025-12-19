package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;

/**
 * Factory for creating User (human trader) instances.
 *
 * <p>This factory implements the Factory Method pattern to encapsulate user
 * creation logic. It ensures that User objects are properly initialized with
 * portfolios and order history tracking.</p>
 *
 * <p><strong>Design Pattern:</strong> Factory Method</p>
 * <ul>
 *   <li>Encapsulates User construction details</li>
 *   <li>Implements ITraderFactory for polymorphic trader creation</li>
 *   <li>Works with TraderRegistry for unified trader management</li>
 *   <li>Ensures proper initialization of User-specific features</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * ITraderFactory userFactory = new UserFactory();
 * Portfolio portfolio = new Portfolio(new BigDecimal("10000"));
 *
 * Trader trader = userFactory.createTrader("user1", "John Doe", portfolio);
 * User user = (User) trader;
 *
 * // User-specific features are available
 * OrderHistory history = user.getOrderHistory();
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see ITraderFactory
 * @see User
 * @see TraderRegistry
 */
public class UserFactory implements ITraderFactory {
    /**
     * Creates a new User instance with the specified parameters.
     *
     * @param id Unique user identifier
     * @param name Display name for the user
     * @param portfolio User's portfolio containing cash and positions
     * @return A new User trader
     */
    @Override
    public Trader createTrader(String id, String name, Portfolio portfolio) {
        return new User(id, name, portfolio);
    }
}
