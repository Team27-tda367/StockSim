package org.team27.stocksim.model.instruments;

import java.math.BigDecimal;

public class StockFactory implements IInstrumentFactory {
    @Override
    public Instrument createInstrument(String symbol, String name, BigDecimal tickSize, int lotSize, String category) {
        return new Stock(symbol, name, tickSize, lotSize, category);
    }
}
