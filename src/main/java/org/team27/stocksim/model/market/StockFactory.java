package org.team27.stocksim.model.market;

public class StockFactory implements InstrumentFactory{
    @Override
    public Instrument createInstrument(String name, Double tickSize, Integer lotSize) {
        return new Stock("ABCD", name, 1, 1); // everything except for name are placeholder-values
    }
}
