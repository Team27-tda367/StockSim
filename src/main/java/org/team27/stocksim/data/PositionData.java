package org.team27.stocksim.data;

public class PositionData {
    private String symbol;
    private int quantity;
    private double costBasis;

    public PositionData() {
    }

    public PositionData(String symbol, int quantity, double costBasis) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.costBasis = costBasis;
    }

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

    public double getCostBasis() {
        return costBasis;
    }

    public void setCostBasis(double costBasis) {
        this.costBasis = costBasis;
    }
}
