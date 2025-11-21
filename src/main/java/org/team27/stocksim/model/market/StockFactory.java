package org.team27.stocksim.model.market;

public class StockFactory implements InstrumentFactory{
    @Override
    public Instrument createInstrument(String symbol, String name, Double tickSize, int lotSize) {
        return new Stock(symbol, name, tickSize, lotSize);
    }
}
