package org.team27.stocksim.model.util.dto;

import org.team27.stocksim.model.market.Trade;

import java.math.BigDecimal;
import java.time.Instant;

public class TradeDTO {

    private String stockSymbol;
    private BigDecimal price;
    private int quantity;
    private Instant time;

    public TradeDTO() {};

    public TradeDTO(String stockSymbol, BigDecimal price, int quantity, Instant time) {
        this.stockSymbol = stockSymbol;
        this.price = price;
        this.quantity = quantity;
        this.time = time;
    }

    // Getters and Setters

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
