package org.team27.stocksim.model.util.dto;

import org.team27.stocksim.model.market.Order;

public class OrderMapper {

    // Convert Trade to TradeDTO

    public static OrderDTO toDto(Order order) {

        if (order == null) {
            return null;
        }
        OrderDTO dto = new OrderDTO();
        dto.setSide(order.getSide().toString());
        dto.setInstrumentSymbol(order.getSymbol());
        dto.setPrice(order.getPrice());
        dto.setQuantity(order.getRemainingQuantity());
        dto.setStatus(order.getStatus().toString());

        return dto;
    }

}
