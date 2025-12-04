package org.team27.stocksim.model.market;

import java.math.BigDecimal;

public abstract class Instrument {
    protected final String symbol;
    protected final String name;
    protected final BigDecimal tickSize;
    protected final int lotSize;

    public Instrument(String symbol, String name, BigDecimal tickSize, int lotSize) {
        this.symbol = symbol;
        this.name = name;
        this.tickSize = tickSize;
        this.lotSize = lotSize;
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

    public abstract void setCurrentPrice(BigDecimal price);

    public abstract BigDecimal getCurrentPrice();

}
