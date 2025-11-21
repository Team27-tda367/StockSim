package org.team27.stocksim.model.market;

public interface InstrumentFactory {
    Instrument createInstrument(String symbol, String name, Double tickSize, int lotSize);
}
