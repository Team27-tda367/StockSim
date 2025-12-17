package org.team27.stocksim.model.portfolio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.market.Trade;

/**
 * Represents a trader's portfolio containing cash balance and stock holdings.
 * This is a pure domain object - StockSim handles notifications when portfolio
 * changes.
 */
public class Portfolio {

    private BigDecimal balance;
    private final BigDecimal initialBalance;
    private Map<String, Position> positions; // symbol -> Position

    public Portfolio(BigDecimal traderBalance) {
        this.balance = traderBalance;
        this.initialBalance = traderBalance;
        this.positions = new HashMap<>();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public boolean withdraw(BigDecimal amount) {
        BigDecimal newBalance = this.balance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
            balance = newBalance;
            return true; // succesfull withdrawal
        }
        return false; // unsuccessfull withdrawal (not enough balance)
    }

    /**
     * Adds stock to the portfolio with price information (for buys).
     * 
     * @param symbol   the stock symbol
     * @param quantity the number of shares
     * @param price    the price per share
     * @param trade    optional trade object for history tracking
     */
    public void addStock(String symbol, int quantity, BigDecimal price, Trade trade) {
        Position position = positions.computeIfAbsent(symbol, Position::new);
        position.addShares(quantity, price, trade);
    }

    /**
     * Adds stock to the portfolio without price information (for initial setup).
     * Uses zero as the cost basis.
     * 
     * @param symbol   the stock symbol
     * @param quantity the number of shares
     */
    public void addStock(String symbol, int quantity) {
        addStock(symbol, quantity, BigDecimal.ZERO, null);
    }

    /**
     * Removes stock from the portfolio (for sells).
     * 
     * @param symbol   the stock symbol
     * @param quantity the number of shares
     * @param trade    optional trade object for history tracking
     * @return true if successful, false if insufficient quantity
     */
    public boolean removeStock(String symbol, int quantity, Trade trade) {
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

    /**
     * Removes stock from the portfolio without trade tracking.
     * 
     * @param symbol   the stock symbol
     * @param quantity the number of shares
     * @return true if successful, false if insufficient quantity
     */
    public boolean removeStock(String symbol, int quantity) {
        return removeStock(symbol, quantity, null);
    }

    public int getStockQuantity(String symbol) {
        Position position = positions.get(symbol);
        return position != null ? position.getQuantity() : 0;
    }

    /**
     * Gets a position for a specific stock.
     * 
     * @param symbol the stock symbol
     * @return the Position object, or null if not found
     */
    public Position getPosition(String symbol) {
        return positions.get(symbol);
    }

    /**
     * Gets all positions in the portfolio.
     * 
     * @return map of symbol to Position
     */
    public Map<String, Position> getPositions() {
        return new HashMap<>(positions);
    }

    /**
     * Gets stock holdings as a map of symbol to quantity (for backward
     * compatibility).
     * 
     * @return map of symbol to quantity
     */
    public Map<String, Integer> getStockHoldings() {
        return positions.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getQuantity()));
    }

    public boolean isEmpty() {
        return positions.isEmpty();
    }

    /**
     * Calculates the total cost basis of all positions in the portfolio.
     * 
     * @return total cost of all positions
     */
    public BigDecimal getTotalCost() {
        return positions.values().stream()
                .map(Position::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the current market value of all positions based on current prices.
     * 
     * @param currentPrices map of symbol to current price
     * @return total market value of all positions (excluding cash)
     */
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

    /**
     * Calculates the total portfolio value including cash balance and positions.
     * 
     * @param currentPrices map of symbol to current price
     * @return total portfolio value (positions + cash)
     */
    public BigDecimal getTotalValue(Map<String, BigDecimal> currentPrices) {
        BigDecimal positionsValue = getPositionsValue(currentPrices);
        return positionsValue.add(balance);
    }

    /**
     * Calculates the unrealized gain/loss on all positions.
     * When there are no positions, compares current balance to initial balance.
     * 
     * @param currentPrices map of symbol to current price
     * @return total unrealized profit/loss
     */
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

    /**
     * Calculates the unrealized gain/loss percentage on all positions.
     * When there are no positions, compares current balance to initial balance.
     * 
     * @param currentPrices map of symbol to current price
     * @return gain/loss as a percentage, or ZERO if no cost basis
     */
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
        return totalGainLoss.divide(costBasis, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

}
