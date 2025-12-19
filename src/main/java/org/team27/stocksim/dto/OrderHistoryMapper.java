package org.team27.stocksim.dto;

import org.team27.stocksim.model.users.OrderHistory;

public class OrderHistoryMapper {

    public static OrderHistoryDTO toDto(OrderHistory orderHistory) {
        if (orderHistory == null) {
            return null;
        }
        return new OrderHistoryDTO(
                orderHistory.getAllOrders().stream()
                        .map(OrderMapper::toDto)
                        .toList(),
                orderHistory.getAllTrades().stream()
                        .map(TradeMapper::toDto)
                        .toList());
    }

}
