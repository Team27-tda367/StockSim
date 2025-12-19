package org.team27.stocksim.model.users.bot;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Registry for bot trading strategies.
 * 
 * <p>This registry implements the Registry pattern combined with the Factory pattern
 * to manage bot strategy creation. It follows the Open/Closed Principle by allowing
 * new strategies to be registered without modifying existing code.</p>
 * 
 * <p><strong>Design Pattern:</strong> Registry + Factory</p>
 * <ul>
 *   <li>Eliminates switch statements for strategy creation</li>
 *   <li>Enables runtime registration of new strategies</li>
 *   <li>Provides type-safe strategy instantiation</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * BotStrategyRegistry registry = new BotStrategyRegistry();
 * 
 * // Create a strategy by name
 * IBotStrategy strategy = registry.create("RandomStrategy");
 * 
 * // Register a custom strategy
 * registry.register("CustomStrategy", CustomStrategy::new);
 * 
 * // Check available strategies
 * Set<String> available = registry.getAvailableStrategies();
 * }</pre>
 * 
 * @author Team 27
 * @version 2.0
 * @see IBotStrategy
 * @see AbstractBotStrategy
 */
public class BotStrategyRegistry {
    
    /**
     * Internal registry mapping strategy names to their factory functions.
     * Uses Supplier functional interface for lazy instantiation.
     */
    private final Map<String, Supplier<IBotStrategy>> strategies = new HashMap<>();
    
    /**
     * Constructs a new BotStrategyRegistry with all default strategies pre-registered.
     * 
     * <p>Default strategies include:</p>
     * <ul>
     *   <li>RandomStrategy - Random buy/sell decisions</li>
     *   <li>HodlerStrategy - Buy and hold behavior</li>
     *   <li>MomentumTraderStrategy - Trend-following strategy</li>
     *   <li>DayTraderStrategy - High-frequency trading</li>
     *   <li>PanicSellerStrategy - Loss-averse behavior</li>
     *   <li>FocusedTraderStrategy - Watchlist-based trading</li>
     *   <li>InstitutionalInvestorStrategy - Large periodic investments</li>
     * </ul>
     */
    public BotStrategyRegistry() {
        registerDefaultStrategies();
    }
    

    private void registerDefaultStrategies() {
        register("RandomStrategy", RandomStrategy::new);
        register("HodlerStrategy", HodlerStrategy::new);
        register("MomentumTraderStrategy", MomentumTraderStrategy::new);
        register("DayTraderStrategy", DayTraderStrategy::new);
        register("PanicSellerStrategy", PanicSellerStrategy::new);
        register("FocusedTraderStrategy", FocusedTraderStrategy::new);
        register("InstitutionalInvestorStrategy", InstitutionalInvestorStrategy::new);
    }
    
    /**
     * Registers a new strategy type with the registry.
     * 
     * <p>This method enables extension of the strategy system at runtime
     * without modifying the registry class itself (Open/Closed Principle).</p>
     * 
     * @param name The unique identifier for the strategy (case-sensitive)
     * @param supplier Factory function that creates instances of the strategy
     * @throws NullPointerException if name or supplier is null
     * 
     * @example
     * <pre>{@code
     * registry.register("AggressiveTrader", AggressiveTraderStrategy::new);
     * }</pre>
     */
    public void register(String name, Supplier<IBotStrategy> supplier) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Strategy name cannot be null or empty");
        }
        if (supplier == null) {
            throw new IllegalArgumentException("Strategy supplier cannot be null");
        }
        strategies.put(name, supplier);
    }
    
    /**
     * Creates a new instance of the specified strategy.
     * 
     * <p>If the requested strategy is not found, this method falls back to
     * creating a RandomStrategy instance to ensure graceful degradation.</p>
     * 
     * @param name The name of the strategy to create
     * @return A new instance of the requested strategy, or RandomStrategy if not found
     * @throws NullPointerException if name is null
     * 
     * @example
     * <pre>{@code
     * IBotStrategy strategy = registry.create("HodlerStrategy");
     * Order order = strategy.decide(bot, market, instruments, gameClock);
     * }</pre>
     */
    public IBotStrategy create(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Strategy name cannot be null");
        }
        
        return Optional.ofNullable(strategies.get(name))
                .map(Supplier::get)
                .orElseGet(() -> {
                    System.err.println("Warning: Strategy '" + name + "' not found. Falling back to RandomStrategy.");
                    return new RandomStrategy();
                });
    }
    
    /**
     * Checks if a strategy with the given name is registered.
     * 
     * @param name The strategy name to check
     * @return true if the strategy exists in the registry, false otherwise
     */
    public boolean hasStrategy(String name) {
        return strategies.containsKey(name);
    }
    
    /**
     * Returns an immutable set of all registered strategy names.
     * 
     * <p>This method is useful for:</p>
     * <ul>
     *   <li>Populating UI dropdowns with available strategies</li>
     *   <li>Validating user input</li>
     *   <li>Testing and debugging</li>
     * </ul>
     * 
     * @return An unmodifiable set containing all registered strategy names
     */
    public Set<String> getAvailableStrategies() {
        return Set.copyOf(strategies.keySet());
    }
    
    /**
     * Returns the total number of registered strategies.
     * 
     * @return The count of registered strategies
     */
    public int getStrategyCount() {
        return strategies.size();
    }
    
    /**
     * Removes a strategy from the registry.
     * 
     * <p><strong>Note:</strong> Use with caution. Removing strategies that are
     * referenced elsewhere may cause runtime errors.</p>
     * 
     * @param name The name of the strategy to remove
     * @return true if the strategy was removed, false if it didn't exist
     */
    public boolean unregister(String name) {
        return strategies.remove(name) != null;
    }
    
    /**
     * Clears all registered strategies from the registry.
     * 
     * <p><strong>Warning:</strong> This will remove all strategies including
     * default ones. Use {@link #registerDefaultStrategies()} to restore defaults.</p>
     */
    public void clear() {
        strategies.clear();
    }
}
