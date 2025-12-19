package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;

/**
 * Factory for creating Bot (automated trader) instances.
 *
 * <p>This factory implements the Factory Method pattern to encapsulate bot
 * creation logic. It ensures that Bot objects are properly initialized with
 * portfolios and default trading strategies (RandomStrategy if none specified).</p>
 *
 * <p><strong>Design Pattern:</strong> Factory Method</p>
 * <ul>
 *   <li>Encapsulates Bot construction details</li>
 *   <li>Implements ITraderFactory for polymorphic trader creation</li>
 *   <li>Works with TraderRegistry for unified trader management</li>
 *   <li>Assigns default RandomStrategy if none provided</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * ITraderFactory botFactory = new BotFactory();
 * Portfolio portfolio = new Portfolio(new BigDecimal("50000"));
 *
 * Trader trader = botFactory.createTrader("bot1", "Trading Bot", portfolio);
 * Bot bot = (Bot) trader;
 *
 * // Bot is created with RandomStrategy by default
 * IBotStrategy strategy = bot.getStrategy();
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see ITraderFactory
 * @see Bot
 * @see TraderRegistry
 * @see org.team27.stocksim.model.users.bot.RandomStrategy
 */
public class BotFactory implements ITraderFactory {
    /**
     * Creates a new Bot instance with the specified parameters.
     *
     * <p>The bot is initialized with a RandomStrategy by default.
     * Use Bot's constructor directly or TraderRegistry methods for
     * custom strategy assignment.</p>
     *
     * @param id Unique bot identifier
     * @param name Display name for the bot
     * @param portfolio Bot's portfolio containing cash and positions
     * @return A new Bot trader with RandomStrategy
     */
    @Override
    public Trader createTrader(String id, String name, Portfolio portfolio) {
        return new Bot(id, name, portfolio);
    }
}
