package org.team27.stocksim.model.users;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.market.Order;

import java.util.List;

/**
 * Encapsulates a bot's trading action for asynchronous execution.
 *
 * <p>BotAction represents a unit of work containing the bot's trading decisions
 * (orders to place) that can be executed asynchronously by the BotActionExecutor.
 * It implements the Command pattern, encapsulating the action's execution logic
 * and ensuring proper state transitions even when exceptions occur.</p>
 *
 * <p><strong>Design Pattern:</strong> Command + Error Handling</p>
 * <ul>
 *   <li>Encapsulates bot trading decisions as executable commands</li>
 *   <li>Executes orders sequentially in a single transaction</li>
 *   <li>Ensures bot returns to IDLE state in finally block</li>
 *   <li>Handles exceptions gracefully without crashing simulation</li>
 *   <li>Enables asynchronous bot action execution</li>
 * </ul>
 *
 * <h2>Execution Flow:</h2>
 * <ol>
 *   <li>Bot strategy generates list of orders</li>
 *   <li>BotAction created with bot, orders, and model reference</li>
 *   <li>Action submitted to BotActionExecutor thread pool</li>
 *   <li>Execute method places each order sequentially</li>
 *   <li>Bot state returned to IDLE (guaranteed via finally)</li>
 * </ol>
 *
 * <h2>Error Handling:</h2>
 * <ul>
 *   <li>Catches all exceptions during order placement</li>
 *   <li>Logs errors with bot ID for debugging</li>
 *   <li>Continues simulation despite individual bot failures</li>
 *   <li>Always returns bot to IDLE state</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // In Bot.tick() method
 * List<Order> orders = strategy.decide(model, this);
 *
 * if (!orders.isEmpty()) {
 *     BotAction action = new BotAction(this, orders, model);
 *     executor.submit(action);  // Asynchronous execution
 * } else {
 *     returnToIdle();  // No orders, immediate return to idle
 * }
 *
 * // Later, in thread pool:
 * action.execute();  // Places orders and returns bot to idle
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see Bot
 * @see BotActionExecutor
 * @see IBotStrategy
 * @see BotState
 */
public class BotAction {
    /**
     * The bot whose action this represents.
     */
    private final Bot bot;

    /**
     * List of orders to place as part of this action.
     */
    private final List<Order> orders;

    /**
     * Reference to the simulation model for placing orders.
     */
    private final StockSim model;

    /**
     * Constructs a BotAction with the bot's trading decisions.
     *
     * @param bot The bot executing this action
     * @param orders List of orders to place
     * @param model The simulation model for order placement
     */
    public BotAction(Bot bot, List<Order> orders, StockSim model) {
        this.bot = bot;
        this.orders = orders;
        this.model = model;
    }

    /**
     * Executes the bot's trading action by placing all orders.
     *
     * <p>This method is called asynchronously by the BotActionExecutor. It
     * iterates through all orders and places them sequentially. Any exceptions
     * are caught and logged without interrupting the simulation. The bot is
     * guaranteed to return to IDLE state via the finally block.</p>
     *
     * <p><strong>Thread Safety:</strong> This method is executed in a thread pool
     * but the bot's state machine ensures only one action executes at a time.</p>
     */
    public void execute() {
        try {
            for (Order order : orders) {
                model.placeOrder(order);
            }
        } catch (Exception e) {
            System.err.println("Error executing bot action for " + bot.getId() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            bot.returnToIdle();
        }
    }
}

