package org.team27.stocksim.model.market;

import java.util.ArrayList;
import java.util.PriorityQueue;

import static java.util.Comparator.comparing;

public class OrderBook {
    private final PriorityQueue<Order> bids = new PriorityQueue<>(comparing(Order::getPrice).reversed().thenComparing(Order::getTimeStamp));
    private final PriorityQueue<Order> asks = new PriorityQueue<>(comparing(Order::getPrice).thenComparing(Order::getTimeStamp));
    private final String symbol;

    public OrderBook(String symbol) {
        this.symbol = symbol;
    }

    public synchronized void add(Order order) {
        if (order.isBuyOrder()) {
            bids.add(order);
        } else {
            asks.add(order);
        }
    }

    public synchronized void remove(Order order) {
        if (order.isBuyOrder()) {
            bids.remove(order);
        } else {
            asks.remove(order);
        }
    }

    public synchronized Order getBestBid() {
        return bids.peek();
    }

    public synchronized Order getBestAsk() {
        return asks.peek();
    }

    public synchronized ArrayList<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.addAll(bids);
        orders.addAll(asks);
        return orders;
    }

    public synchronized void fillOrder(Order order, int quantity) {
        order.fill(quantity);
    }
}
