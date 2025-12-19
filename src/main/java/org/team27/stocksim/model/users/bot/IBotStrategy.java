package org.team27.stocksim.model.users.bot;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.users.Bot;

import java.util.List;

/**
 * Strategy interface for bot trading behavior.
 *
 * <p>This interface defines the contract for all bot trading strategies,
 * following the Strategy pattern. Each implementation provides different
 * trading logic while maintaining a consistent interface for the Bot class.</p>
 *
 * <p><strong>Design Pattern:</strong> Strategy</p>
 * <ul>
 *   <li>Enables pluggable trading algorithms</li>
 *   <li>Decouples bot behavior from bot identity</li>
 *   <li>Allows runtime strategy selection</li>
 *   <li>Facilitates testing and comparison of strategies</li>
 * </ul>
 *
 * <h2>Implementing a Custom Strategy:</h2>
 * <pre>{@code
 * public class ConservativeStrategy implements IBotStrategy {
 *     @Override
 *     public List<Order> decide(StockSim model, Bot bot) {
 *         List<Order> orders = new ArrayList<>();
 *         // Implement conservative trading logic
 *         // Only buy established stocks with low volatility
 *         // Sell on small gains
 *         return orders;
 *     }
 * }
 *
 * // Register and use
 * registry.register("ConservativeStrategy", ConservativeStrategy::new);
 * Bot bot = botFactory.create("bot1", "Safe Bot", "ConservativeStrategy");
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see AbstractBotStrategy
 * @see BotStrategyRegistry
 * @see Bot
 */
public interface IBotStrategy {
    /**
     * Decides what trading actions the bot should take.
     *
     * <p>This method is called on each simulation tick. It should analyze
     * the current market state and bot's portfolio to determine appropriate
     * trading actions. Return an empty list if no action is needed.</p>
     *
     * @param model The simulation model providing market and instrument data
     * @param bot The bot making the decision
     * @return List of orders to place (may be empty, should not be null)
     */
    List<Order> decide(StockSim model, Bot bot);
}
