package org.team27.stocksim.dto;

import org.team27.stocksim.model.instruments.PriceHistory;

import java.math.BigDecimal;

public class InstrumentDTO {

    private final String symbol;
    private final String name;
    private final String category;
    private final BigDecimal price;
    private final PriceHistory priceHistory;

    public InstrumentDTO(String symbol, String name, String category, BigDecimal price, PriceHistory priceHistory) {
        this.symbol = symbol;
        this.name = name;
        this.category = category;
        this.price = price;
        this.priceHistory = priceHistory;
    }

    // Getters only (immutable)
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
}
