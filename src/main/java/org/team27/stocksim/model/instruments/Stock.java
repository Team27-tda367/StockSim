package org.team27.stocksim.model.instruments;

import java.math.BigDecimal;

public class Stock extends Instrument {
    private BigDecimal price;
    private PriceHistory priceHistory;

    public Stock(String symbol, String name, BigDecimal tickSize, int lotSize, String category) {
        super(symbol, name, tickSize, lotSize, category);
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
