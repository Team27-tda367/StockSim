package org.team27.stocksim.model.util.dto;

import org.team27.stocksim.model.instruments.PriceHistory;

import java.math.BigDecimal;

public class InstrumentDTO {

    private String symbol;
    private String name;
    private String category;
    private BigDecimal price;
    private PriceHistory priceHistory;

    // Empty constructor for serialization/deserialization
    public InstrumentDTO() {
    }

    public InstrumentDTO(String symbol, String name, String category, BigDecimal price, PriceHistory priceHistory) {
        this.symbol = symbol;
        this.name = name;
        this.category = category;
        this.price = price;
        this.priceHistory = priceHistory;
    }

    // Getters and Setters
    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public PriceHistory getPriceHistory() {
        return priceHistory;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setPriceHistory(PriceHistory priceHistory) {
        this.priceHistory = priceHistory;
    }
}
