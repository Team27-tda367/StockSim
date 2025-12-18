package org.team27.stocksim.model.util.dto;

import org.team27.stocksim.model.market.Order;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderHistoryDTO {

    private List<OrderDTO> orders;
    private List<TradeDTO> trades;

    public OrderHistoryDTO() {}

    public OrderHistoryDTO(OrderDTO order, TradeDTO trade) {
        this.orders = List.of(order);
        this.trades = List.of(trade);
    }

    // Getters and Setters

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
    }

    public List<TradeDTO> getTrades() {
        return trades;
    }

    public void setTrades(List<TradeDTO> trades) {
        this.trades = trades;
    }

    public List<OrderDTO> getActiveOrders() {
        return orders.stream()
                .filter(order -> !Objects.equals(order.getStatus(), "FILLED")
                        && !Objects.equals(order.getStatus(), "CANCELLED"))
                .collect(Collectors.toList());
    }
}
