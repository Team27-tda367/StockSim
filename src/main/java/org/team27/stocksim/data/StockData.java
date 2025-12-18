package org.team27.stocksim.data;

public class StockData {
    private String symbol;
    private String name;
    private String tickSize;
    private String lotSize;
    private String category;
    private String initialPrice;

    // Default constructor for JSON deserialization
    public StockData() {
    }

    public StockData(String symbol, String name, String tickSize, String lotSize, String category,
            String initialPrice) {
        this.symbol = symbol;
        this.name = name;
        this.tickSize = tickSize;
        this.lotSize = lotSize;
        this.category = category;
        this.initialPrice = initialPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTickSize() {
        return tickSize;
    }

    public void setTickSize(String tickSize) {
        this.tickSize = tickSize;
    }

    public String getLotSize() {
        return lotSize;
    }

    public void setLotSize(String lotSize) {
        this.lotSize = lotSize;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(String initialPrice) {
        this.initialPrice = initialPrice;
    }
}
