package org.team27.stocksim.model.portfolio;

import org.team27.stocksim.model.market.Trade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages a trader's cash balance and stock positions.
 *
 * <p>The Portfolio class provides thread-safe operations for managing cash
 * and stock holdings. It tracks initial balance for profit/loss calculations
 * and maintains individual positions for each stock symbol.</p>
 *
 * <p><strong>Design Patterns:</strong> Aggregate Root + Repository Pattern</p>
 * <ul>
 *   <li>Thread-safe operations using synchronized methods</li>
 *   <li>Aggregates multiple Position objects</li>
 *   <li>Tracks trade history through Position objects</li>
 *   <li>Validates withdrawals to prevent negative balance</li>
 *   <li>Automatic position cleanup when holdings reach zero</li>
 * </ul>
 *
 * <h2>Key Operations:</h2>
 * <ul>
 *   <li>Cash management: deposit, withdraw with validation</li>
 *   <li>Stock operations: add/remove with quantity validation</li>
 *   <li>Position tracking: average cost, unrealized P&L</li>
 *   <li>Portfolio valuation: total equity calculation</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * Portfolio portfolio = new Portfolio(new BigDecimal("10000"));
 *
 * // Buy stock
 * boolean withdrawn = portfolio.withdraw(new BigDecimal("1500"));
 * portfolio.addStock("AAPL", 10, new BigDecimal("150.00"), trade);
 *
 * // Sell stock
 * boolean sold = portfolio.removeStock("AAPL", 5, trade);
 * portfolio.deposit(new BigDecimal("775"));
 *
 * // Check holdings
 * int quantity = portfolio.getStockQuantity("AAPL");
 * BigDecimal balance = portfolio.getBalance();
 * BigDecimal netWorth = portfolio.getNetWorth(currentPrices);
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see Position
 * @see Trader
 * @see Trade
 */
public class Portfolio {

    /**
     * Starting balance when portfolio was created.
     */
    private final BigDecimal initialBalance;

    /**
     * Current cash balance available for trading.
     */
    private BigDecimal balance;

    /**
     * Map of stock positions by symbol.
     */
    private Map<String, Position> positions; // symbol -> Position

    /**
     * Constructs a new Portfolio with the specified initial balance.
     *
     * @param traderBalance Starting cash balance
     */
    public Portfolio(BigDecimal traderBalance) {
        this.balance = traderBalance;
        this.initialBalance = traderBalance;
        this.positions = new HashMap<>();
    }

    public synchronized BigDecimal getBalance() {
        return balance;
    }

    public synchronized void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public synchronized boolean withdraw(BigDecimal amount) {
        BigDecimal newBalance = this.balance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
            balance = newBalance;
            return true; // succesfull withdrawal
        }
        return false; // unsuccessfull withdrawal (not enough balance)
    }

    public synchronized void addStock(String symbol, int quantity, BigDecimal price, Trade trade) {
        Position position = positions.computeIfAbsent(symbol, Position::new);
        position.addShares(quantity, price, trade);
    }

    public synchronized void addStock(String symbol, int quantity) {
        addStock(symbol, quantity, BigDecimal.ZERO, null);
    }

    public synchronized boolean removeStock(String symbol, int quantity, Trade trade) {
        Position position = positions.get(symbol);
        if (position == null) {
            return false;
        }

        boolean success = position.removeShares(quantity, trade);
        if (success && position.isEmpty()) {
            positions.remove(symbol);
        }
        return success;
    }

    public synchronized boolean removeStock(String symbol, int quantity) {
        return removeStock(symbol, quantity, null);
    }

    public synchronized int getStockQuantity(String symbol) {
        Position position = positions.get(symbol);
        return position != null ? position.getQuantity() : 0;
    }

    public Position getPosition(String symbol) {
        return positions.get(symbol);
    }

    public Map<String, Position> getPositions() {
        return new HashMap<>(positions);
    }

    public Map<String, Integer> getStockHoldings() {
        return positions.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getQuantity()));
    }

    public boolean isEmpty() {
        return positions.isEmpty();
    }

    public BigDecimal getTotalCost() {
        return positions.values().stream().map(Position::getTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPositionsValue(Map<String, BigDecimal> currentPrices) {
        BigDecimal totalValue = BigDecimal.ZERO;

        for (Map.Entry<String, Position> entry : positions.entrySet()) {
            String symbol = entry.getKey();
            Position position = entry.getValue();
            BigDecimal currentPrice = currentPrices.get(symbol);

            if (currentPrice != null) {
                BigDecimal positionValue = currentPrice.multiply(BigDecimal.valueOf(position.getQuantity()));
                totalValue = totalValue.add(positionValue);
            }
        }

        return totalValue;
    }

    public BigDecimal getTotalValue(Map<String, BigDecimal> currentPrices) {
        BigDecimal positionsValue = getPositionsValue(currentPrices);
        return positionsValue.add(balance);
    }

    public BigDecimal getTotalGainLoss(Map<String, BigDecimal> currentPrices) {
        if (positions.isEmpty()) {
            // No positions: compare current balance to initial balance
            return balance.subtract(initialBalance);
        }
        // With positions: compare current position value to cost basis
        BigDecimal positionsValue = getPositionsValue(currentPrices);
        BigDecimal totalCost = getTotalCost();
        return positionsValue.subtract(totalCost);
    }

    public BigDecimal getGainLossPercentage(Map<String, BigDecimal> currentPrices) {
        BigDecimal costBasis;
        if (positions.isEmpty()) {
            // No positions: use initial balance as cost basis
            costBasis = initialBalance;
        } else {
            // With positions: use total cost of positions
            costBasis = getTotalCost();
        }

        if (costBasis.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalGainLoss = getTotalGainLoss(currentPrices);
        return totalGainLoss.divide(costBasis, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    public synchronized boolean canBuy(String symbol, int quantity, BigDecimal price) {
        BigDecimal totalCost = price.multiply(BigDecimal.valueOf(quantity));
        return balance.compareTo(totalCost) >= 0;
    }

    public synchronized boolean canSell(String symbol, int quantity) {
        return getStockQuantity(symbol) >= quantity;
    }

}
