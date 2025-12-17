package org.team27.stocksim.model.util.dto;

import org.team27.stocksim.model.market.Order;

/**
 * Mapper to convert between Order domain objects and OrderDTO.
 */
public class OrderMapper {

    /**
     * Convert a domain Order to an OrderDTO.
     *
     * @param order The domain order object
     * @return OrderDTO for view layer consumption
     */
    public static OrderDTO toDto(Order order) {
        if (order == null) {
            return null;
        }

        return new OrderDTO(
                order.getOrderId(),
                order.getSide().name(),
                order.getOrderType().name(),
                order.getSymbol(),
                order.getPrice(),
                order.getTotalQuantity(),
                order.getRemainingQuantity(),
                order.getStatus().name(),
                order.getTimeStamp(),
                order.getTraderId()
        );
    }
}
