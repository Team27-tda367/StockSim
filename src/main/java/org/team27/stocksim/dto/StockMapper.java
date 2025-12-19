package org.team27.stocksim.dto;

import org.team27.stocksim.model.instruments.Instrument;

public class StockMapper {



    public static InstrumentDTO toDto(Instrument instrument) {

        if (instrument == null) {
            return null;
        }
        return new InstrumentDTO(
                instrument.getSymbol(),
                instrument.getName(),
                instrument.getCategory(),
                instrument.getCurrentPrice(),
                instrument.getPriceHistory());
    }

}
