package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;

/**
 * Abstract base class for all trading entities in the simulation.
 *
 * <p>Trader provides the common interface and shared functionality for both
 * human users and automated bots. It manages the trader's identity and
 * portfolio, following the Template Method pattern where subclasses provide
 * specific trading behavior.</p>
 *
 * <p><strong>Design Pattern:</strong> Template Method + Composition</p>
 * <ul>
 *   <li>Base class for User and Bot hierarchies</li>
 *   <li>Encapsulates common trader attributes (ID, name, portfolio)</li>
 *   <li>Portfolio composition for separation of concerns</li>
 *   <li>Immutable identity (ID and display name)</li>
 * </ul>
 *
 * <h2>Subclasses:</h2>
 * <ul>
 *   <li>{@link User} - Human-controlled trader with order history</li>
 *   <li>{@link Bot} - Automated trader with pluggable strategies</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Polymorphic trader handling
 * Map<String, Trader> traders = traderRegistry.getAllTraders();
 * for (Trader trader : traders.values()) {
 *     Portfolio portfolio = trader.getPortfolio();
 *     BigDecimal netWorth = portfolio.getNetWorth(currentPrices);
 *     System.out.println(trader.getDisplayName() + ": $" + netWorth);
 * }
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see User
 * @see Bot
 * @see Portfolio
 * @see TraderRegistry
 */
public abstract class Trader {

    /**
     * Unique identifier for this trader.
     */
    private final String id;

    /**
     * Human-readable display name.
     */
    private final String displayName;

    /**
     * Portfolio managing cash balance and stock positions.
     */
    private final Portfolio portfolio;

    /**
     * Package-private constructor for use by factory classes.
     *
     * @param id Unique trader identifier
     * @param name Display name for the trader
     * @param portfolio Portfolio containing cash and positions
     */
    Trader(String id, String name, Portfolio portfolio) {
        this.id = id;
        this.displayName = name;
        this.portfolio = portfolio;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

}
