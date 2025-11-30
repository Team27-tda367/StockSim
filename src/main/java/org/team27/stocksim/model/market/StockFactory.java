package org.team27.stocksim.model.market;

import java.math.BigDecimal;

public class StockFactory implements InstrumentFactory{
    @Override
    public Instrument createInstrument(String symbol, String name, BigDecimal tickSize, int lotSize) {
        return new Stock(symbol, name, tickSize, lotSize);
    }
}
