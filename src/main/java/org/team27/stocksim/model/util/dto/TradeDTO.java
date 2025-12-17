package org.team27.stocksim.model.util.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Data Transfer Object for Trade information.
 * Immutable representation for view layer and external communication.
 */
public class TradeDTO {
    private final int buyOrderId;
    private final int sellOrderId;
    private final String symbol;
    private final BigDecimal price;
    private final int quantity;
    private final Instant timestamp;

    public TradeDTO(int buyOrderId, int sellOrderId, String symbol,
                    BigDecimal price, int quantity, Instant timestamp) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public int getBuyOrderId() {
        return buyOrderId;
    }

    public int getSellOrderId() {
        return sellOrderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public BigDecimal getTotalValue() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
