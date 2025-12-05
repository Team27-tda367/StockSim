package org.team27.stocksim.model.portfolio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.team27.stocksim.model.market.Trade;

/**
 * Represents a position in a specific stock, tracking quantity, cost basis, and
 * trade history.
 */
public class Position {
    private final String symbol;
    private int quantity;
    private BigDecimal totalCost; // Total cost of all shares (for average cost calculation)
    private final List<Trade> trades; // History of trades that built this position

    public Position(String symbol) {
        this.symbol = symbol;
        this.quantity = 0;
        this.totalCost = BigDecimal.ZERO;
        this.trades = new ArrayList<>();
    }

    /**
     * Adds shares to the position (buy operation).
     * 
     * @param quantity the number of shares to add
     * @param price    the price per share
     * @param trade    optional trade object to record in history
     */
    public void addShares(int quantity, BigDecimal price, Trade trade) {
        this.quantity += quantity;
        this.totalCost = this.totalCost.add(price.multiply(BigDecimal.valueOf(quantity)));
        if (trade != null) {
            this.trades.add(trade);
        }
    }

    /**
     * Removes shares from the position (sell operation).
     * 
     * @param quantity the number of shares to remove
     * @param trade    optional trade object to record in history
     * @return true if successful, false if insufficient quantity
     */
    public boolean removeShares(int quantity, Trade trade) {
        if (this.quantity < quantity) {
            return false;
        }

        // Calculate the cost basis of shares being sold (proportional)
        if (this.quantity > 0 && this.totalCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal costPerShare = this.totalCost.divide(BigDecimal.valueOf(this.quantity), 10,
                    RoundingMode.HALF_UP);
            BigDecimal costOfSoldShares = costPerShare.multiply(BigDecimal.valueOf(quantity));
            this.totalCost = this.totalCost.subtract(costOfSoldShares);
        }

        this.quantity -= quantity;

        if (trade != null) {
            this.trades.add(trade);
        }

        return true;
    }

    /**
     * Gets the average cost per share.
     * 
     * @return average cost per share, or ZERO if no shares
     */
    public BigDecimal getAverageCost() {
        if (quantity == 0) {
            return BigDecimal.ZERO;
        }
        return totalCost.divide(BigDecimal.valueOf(quantity), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates unrealized profit/loss based on current market price.
     * 
     * @param currentPrice the current market price per share
     * @return unrealized P&L
     */
    public BigDecimal getUnrealizedPnL(BigDecimal currentPrice) {
        BigDecimal currentValue = currentPrice.multiply(BigDecimal.valueOf(quantity));
        return currentValue.subtract(totalCost);
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public List<Trade> getTrades() {
        return new ArrayList<>(trades);
    }

    /**
     * Checks if the position is empty (no shares).
     * 
     * @return true if quantity is zero
     */
    public boolean isEmpty() {
        return quantity == 0;
    }
}
