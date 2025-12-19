package org.team27.stocksim.model.instruments;

import org.team27.stocksim.model.clock.ClockProvider;

import java.math.BigDecimal;

public class Stock extends Instrument {
    private BigDecimal price;
    private PriceHistory priceHistory;

    Stock(String symbol, String name, BigDecimal tickSize, int lotSize, String category, BigDecimal initialPrice) {
        super(symbol, name, tickSize, lotSize, category);
        this.price = initialPrice;
        this.priceHistory = new PriceHistory();
    }

    @Override
    public BigDecimal getCurrentPrice() {
        return price;
    }

    public void setCurrentPrice(BigDecimal price) {
        setCurrentPrice(price, ClockProvider.currentTimeMillis());
    }

    public void setCurrentPrice(BigDecimal price, long timestamp) {
        this.price = price;
        priceHistory.addPrice(price, timestamp);
    }

    public PriceHistory getPriceHistory() {
        return priceHistory;
    }

}
