package org.team27.stocksim.model.market;

import java.math.BigDecimal;
import java.time.Instant;


public class Trade {
    private final String stockSymbol;
    private final BigDecimal price;
    private final int quantity;
    private final int buyOrderId;
    private final int sellOrderId;
    private final Instant time;

    public Trade(int buyOrderId, int sellOrderId, String stockSymbol, BigDecimal price, int quantity, Instant time) {

        this.stockSymbol = stockSymbol;
        this.price = price;
        this.quantity = quantity;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.time = time;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getBuyOrderId() {
        return buyOrderId;
    }

    public int getSellOrderId() {
        return sellOrderId;
    }

    public Instant getTime() {
        return time;
    }
}
