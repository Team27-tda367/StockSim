package org.team27.stocksim.model.util.dto;

import org.team27.stocksim.model.market.Trade;

public class TradeMapper {

    // Convert Trade to TradeDTO

    public static TradeDTO toDto(Trade trade) {

        if (trade == null) {
            return null;
        }
        TradeDTO dto = new TradeDTO();
        dto.setStockSymbol(trade.getStockSymbol());
        dto.setPrice(trade.getPrice());
        dto.setQuantity(trade.getQuantity());
        dto.setTime(trade.getTime());

        return dto;
    }

}
