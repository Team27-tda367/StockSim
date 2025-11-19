package org.team27.stocksim.model.market;

import org.team27.stocksim.model.users.Trader;

import java.time.Instant;

public class Order {
    public enum Side { BUY, SELL }
    private final Side side;

    public enum Status { NEW, PARTIALLY_FILLED, FILLED, CANCELLED }
    private Status status = Status.NEW;



    private final int orderId;
    private final double price;
    private final int totalQuantity;
    private int remainingQuantity;
    private final Instant timeStamp;
    private final Instrument instrument;
    private final Trader owner;


    public Order(Side side, Instrument instrument, int orderId, double price, int quantity, Trader owner) {
        this.side = side;
        this.instrument = instrument;
        this.orderId = orderId;
        this.price = price;
        this.totalQuantity = quantity;
        this.owner = owner;

        this.timeStamp = Instant.now(); //Using java.time before deciding on our time implementation
    }
    public int getOrderId() {
        return orderId;
    }

    public double getPrice() {
        return price;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public void cancel() {
        status = Status.CANCELLED;
    }


}
