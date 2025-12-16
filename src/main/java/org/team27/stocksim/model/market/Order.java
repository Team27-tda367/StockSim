package org.team27.stocksim.model.market;

import org.team27.stocksim.model.clock.ClockProvider;

import java.math.BigDecimal;
import java.time.Instant;

public class Order {
    private static int nextOrderId = 1;

    private final Side side;
    private final int orderId;
    private final BigDecimal price;
    private final int totalQuantity;
    private final Instant timeStamp;
    private final String instrumentSymbol;
    private final String traderId;
    private Status status = Status.NEW;
    private int remainingQuantity;

    public Order(Side side, String instrumentSymbol, BigDecimal price, int quantity, String traderId) {
        this.side = side;
        this.instrumentSymbol = instrumentSymbol;
        this.orderId = generateOrderId();
        this.price = price;
        this.totalQuantity = quantity;
        this.remainingQuantity = quantity;
        this.traderId = traderId;

        this.timeStamp = ClockProvider.getClock().instant();
    }

    private static synchronized int generateOrderId() {
        return nextOrderId++;
    }

    public int getOrderId() {
        return orderId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public String getSymbol() {
        return instrumentSymbol;
    }

    public Side getSide() {
        return side;
    }

    public Status getStatus() {
        updateStatus();
        return status;
    }

    public String getTraderId() {
        return traderId;
    }

    public void cancel() {// TODO
        status = Status.CANCELLED;
    }

    public void fill(int quantity) {
        remainingQuantity = remainingQuantity - quantity;
        updateStatus();
    }

    public boolean isBuyOrder() {
        return side == Side.BUY;
    }

    public boolean isFilled() {
        updateStatus();
        return status == Status.FILLED;
    }

    private void updateStatus() {
        if (remainingQuantity == 0) {
            status = Status.FILLED;
        } else if (remainingQuantity < totalQuantity) {
            status = Status.PARTIALLY_FILLED;
        }
    }

    public enum Side {
        BUY, SELL
    }

    public enum Status {
        NEW, PARTIALLY_FILLED, FILLED, CANCELLED
    }

}
