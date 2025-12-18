package org.team27.stocksim.model.util.dto;

import org.team27.stocksim.model.users.OrderHistory;

public class OrderHistoryMapper {

    public static OrderHistoryDTO toDto(OrderHistory orderHistory) {
        if (orderHistory == null) {
            return null;
        }
        OrderHistoryDTO dto = new OrderHistoryDTO();
        dto.setOrders(orderHistory.getAllOrders().stream()
                .map(OrderMapper::toDto)
                .toList());
        dto.setTrades(orderHistory.getAllTrades().stream()
                .map(TradeMapper::toDto)
                .toList());
        return dto;
    }

}
