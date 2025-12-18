package org.team27.stocksim.model.util.dto;

import java.math.BigDecimal;

public class PositionDTO {

    private final String symbol;
    private final int quantity;
    private final BigDecimal averageCost;

    public PositionDTO(String symbol, int quantity, BigDecimal averageCost) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averageCost = averageCost;
    }

    // Getters only (immutable)
    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }

}
