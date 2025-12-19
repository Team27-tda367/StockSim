package org.team27.stocksim.dto;

import org.team27.stocksim.model.instruments.Instrument;

public class StockMapper {

    // Convert Instrument to InstrumentDTO

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

    // Can be implemented for creating stocks from DTOs in the future

    // Convert InstrumentDTO to Instrument
    /*
     * public static Instrument toEntity(InstrumentDTO dto) {
     *
     * if (dto == null) return null;
     * Instrument stock = new Stock();
     * stock.setSymbol(dto.getSymbol());
     * stock.setName(dto.getName());
     * stock.setCategory(dto.getCategory());
     *
     * return user;
     * }
     */

    /*
     * public static Instrument toEntity(InstrumentDTO dto) {
     * 
     * if (dto == null) return null;
     * Instrument stock = new Stock();
     * stock.setSymbol(dto.getSymbol());
     * stock.setName(dto.getName());
     * stock.setCategory(dto.getCategory());
     * 
     * return user;
     * }
     */

}
