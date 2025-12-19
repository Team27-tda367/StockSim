package org.team27.stocksim.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderDTO {
    private final int orderId;
    private final String side; // "BUY" or "SELL"
    private final String orderType; // "LIMIT" or "MARKET"
    private final String symbol;
    private final BigDecimal price;
    private final int totalQuantity;
    private final int remainingQuantity;
    private final String status; // "NEW", "PARTIALLY_FILLED", "FILLED", "CANCELLED"
    private final Instant timestamp;
    private final String traderId;

    public OrderDTO(int orderId, String side, String orderType, String symbol,
            BigDecimal price, int totalQuantity, int remainingQuantity,
            String status, Instant timestamp, String traderId) {
        this.orderId = orderId;
        this.side = side;
        this.orderType = orderType;
        this.symbol = symbol;
        this.price = price;
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = remainingQuantity;
        this.status = status;
        this.timestamp = timestamp;
        this.traderId = traderId;
    }

    // Getters only (immutable)
    public int getOrderId() {
        return orderId;
    }

    public String getSide() {
        return side;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public String getStatus() {
        return status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getTraderId() {
        return traderId;
    }

    // Computed properties for display
    public int getFilledQuantity() {
        return totalQuantity - remainingQuantity;
    }

    public double getFillPercentage() {
        return totalQuantity > 0 ? (double) (totalQuantity - remainingQuantity) / totalQuantity * 100 : 0;
    }

    public boolean isBuyOrder() {
        return "BUY".equals(side);
    }

    public boolean isSellOrder() {
        return "SELL".equals(side);
    }
}
