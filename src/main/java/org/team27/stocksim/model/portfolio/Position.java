package org.team27.stocksim.model.portfolio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.team27.stocksim.model.market.Trade;

/**
 * Represents a trader's holdings of a specific stock.
 *
 * <p>A Position tracks the quantity of shares held, the total cost basis,
 * and the trade history that built the position. It calculates average cost
 * and unrealized profit/loss, providing essential portfolio analytics.</p>
 *
 * <p><strong>Design Patterns:</strong> Value Object + Aggregate</p>
 * <ul>
 *   <li>Tracks cost basis using weighted average method</li>
 *   <li>Maintains trade history for audit and tax purposes</li>
 *   <li>Calculates unrealized P&L against current market price</li>
 *   <li>Proportional cost reduction when selling shares</li>
 *   <li>Validates sell operations against current holdings</li>
 * </ul>
 *
 * <h2>Cost Basis Calculation:</h2>
 * <p>Uses weighted average cost method: when shares are added, their cost
 * is added to the total; when shares are sold, cost is reduced proportionally.</p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * Position position = new Position("AAPL");
 *
 * // Buy 100 shares at $150
 * position.addShares(100, new BigDecimal("150.00"), buyTrade);
 *
 * // Buy 50 more shares at $160
 * position.addShares(50, new BigDecimal("160.00"), buyTrade2);
 *
 * // Average cost: (100*150 + 50*160) / 150 = $153.33
 * BigDecimal avgCost = position.getAverageCost();
 *
 * // Unrealized P&L at current price $170
 * BigDecimal pnl = position.getUnrealizedPnL(new BigDecimal("170.00"));
 *
 * // Sell 50 shares
 * boolean success = position.removeShares(50, sellTrade);
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see Portfolio
 * @see Trade
 */
public class Position {
    /**
     * Stock symbol for this position.
     */
    private final String symbol;

    /**
     * Number of shares currently held.
     */
    private int quantity;

    /**
     * Total cost of all shares (for weighted average cost calculation).
     */
    private BigDecimal totalCost;

    /**
     * History of trades that built or reduced this position.
     */
    private final List<Trade> trades;

    /**
     * Constructs a new Position for the specified stock symbol.
     *
     * @param symbol Stock symbol (e.g., "AAPL", "GOOGL")
     */
    public Position(String symbol) {
        this.symbol = symbol;
        this.quantity = 0;
        this.totalCost = BigDecimal.ZERO;
        this.trades = new ArrayList<>();
    }


    public void addShares(int quantity, BigDecimal price, Trade trade) {
        this.quantity += quantity;
        this.totalCost = this.totalCost.add(price.multiply(BigDecimal.valueOf(quantity)));
        if (trade != null) {
            this.trades.add(trade);
        }
    }


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


    public BigDecimal getAverageCost() {
        if (quantity == 0) {
            return BigDecimal.ZERO;
        }
        return totalCost.divide(BigDecimal.valueOf(quantity), 2, RoundingMode.HALF_UP);
    }


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


    public boolean isEmpty() {
        return quantity == 0;
    }
}
