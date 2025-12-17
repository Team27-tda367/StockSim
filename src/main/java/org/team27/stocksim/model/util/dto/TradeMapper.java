package org.team27.stocksim.model.util.dto;

import org.team27.stocksim.model.market.Trade;

/**
 * Mapper to convert between Trade domain objects and TradeDTO.
 */
public class TradeMapper {

    /**
     * Convert a domain Trade to a TradeDTO.
     *
     * @param trade The domain trade object
     * @return TradeDTO for view layer consumption
     */
    public static TradeDTO toDto(Trade trade) {
        if (trade == null) {
            return null;
        }

        return new TradeDTO(
                trade.getBuyOrderId(),
                trade.getSellOrderId(),
                trade.getStockSymbol(),
                trade.getPrice(),
                trade.getQuantity(),
                trade.getTime());
    }
}
