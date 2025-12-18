package org.team27.stocksim.model.util.dto;

import java.math.BigDecimal;

public class PositionDTO {

    private String symbol;
    private int quantity;
    private BigDecimal averageCost;

    public PositionDTO() {}

    public PositionDTO(String symbol, int quantity, BigDecimal averageCost) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averageCost = averageCost;

    }

    // Getters and Setters

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(BigDecimal averageCost) {
        this.averageCost = averageCost;
    }

}
