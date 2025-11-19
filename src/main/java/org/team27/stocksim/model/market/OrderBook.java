package org.team27.stocksim.model.market;

import java.util.Comparator;
import java.util.PriorityQueue;

public class OrderBook {
    private final PriorityQueue<Order> bids = new PriorityQueue<>(
            Comparator.comparingDouble(Order::getPrice).reversed().thenComparing(Order::getTimeStamp)
    );
    private final PriorityQueue<Order> asks = new PriorityQueue<>(
            Comparator.comparingDouble(Order::getPrice).thenComparing(Order::getTimeStamp)
    );
}
