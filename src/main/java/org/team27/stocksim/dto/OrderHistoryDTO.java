package org.team27.stocksim.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderHistoryDTO {

    private final List<OrderDTO> orders;
    private final List<TradeDTO> trades;

    public OrderHistoryDTO(List<OrderDTO> orders, List<TradeDTO> trades) {
        this.orders = Collections.unmodifiableList(orders);
        this.trades = Collections.unmodifiableList(trades);
    }

    // Getters only (immutable)
    public List<OrderDTO> getOrders() {
        return orders;
    }

    public List<TradeDTO> getTrades() {
        return trades;
    }

    public List<OrderDTO> getActiveOrders() {
        return orders.stream()
                .filter(order -> !Objects.equals(order.getStatus(), "FILLED")
                        && !Objects.equals(order.getStatus(), "CANCELLED"))
                .collect(Collectors.toList());
    }


    public List<OrderDTO> getActiveOrdersDTO() {
        return getActiveOrders();
    }
}
