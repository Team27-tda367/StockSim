package org.team27.stocksim.model.instruments;

import java.math.BigDecimal;

public abstract class Instrument {
    protected final String symbol;
    protected final String name;
    protected final BigDecimal tickSize;
    protected final int lotSize;
    protected final String category;

    public Instrument(String symbol, String name, BigDecimal tickSize, int lotSize, String category) {
        this.symbol = symbol;
        this.name = name;
        this.tickSize = tickSize;
        this.lotSize = lotSize;
        this.category = category;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getTickSize() {
        return tickSize;
    }

    public int getLotSize() {
        return lotSize;
    }

    public String getCategory() {
        return category;
    }

    public abstract void setCurrentPrice(BigDecimal price);

    public abstract void setCurrentPrice(BigDecimal price, long timestamp);

    public abstract BigDecimal getCurrentPrice();

    public abstract PriceHistory getPriceHistory();

}
