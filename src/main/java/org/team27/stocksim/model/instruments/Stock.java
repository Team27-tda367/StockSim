package org.team27.stocksim.model.instruments;

import java.math.BigDecimal;

import org.team27.stocksim.model.portfolio.PriceHistory;

public class Stock extends Instrument {
    private BigDecimal price;
    private PriceHistory priceHistory;

    public Stock(String symbol, String name, BigDecimal tickSize, int lotSize) {
        super(symbol, name, tickSize, lotSize);
        this.price = new BigDecimal(100);
        this.priceHistory = new PriceHistory();
    }

    @Override
    public BigDecimal getCurrentPrice() {
        return price;
    }

    public void setCurrentPrice(BigDecimal price) {
        this.price = price;
        priceHistory.addPrice(price);
    }

    public PriceHistory getPriceHistory() {
        return priceHistory;
    }

}
