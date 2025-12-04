package org.team27.stocksim.model.market;

import java.math.BigDecimal;

public class PricePoint {
    private long timestamp;
    private BigDecimal price;

    public PricePoint(long timestamp, BigDecimal price) {
        this.timestamp = timestamp;
        this.price = price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
