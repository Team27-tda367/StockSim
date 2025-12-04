package org.team27.stocksim.model.market;

import java.math.BigDecimal;

public class Stock extends Instrument {
    private BigDecimal price;

    public Stock(String symbol, String name, BigDecimal tickSize, int lotSize) {
        super(symbol, name, tickSize, lotSize);
        this.price = new BigDecimal(100);
    }

    @Override
    public BigDecimal getCurrentPrice() {
        return price;
    }

    public void setCurrentPrice(BigDecimal price) {
        this.price = price;
    }

}
