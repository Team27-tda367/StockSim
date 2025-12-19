package org.team27.stocksim.model.portfolio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.team27.stocksim.model.market.Trade;


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
