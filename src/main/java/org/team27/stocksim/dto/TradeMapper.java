package org.team27.stocksim.dto;

import org.team27.stocksim.model.market.Trade;

public class TradeMapper {

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
