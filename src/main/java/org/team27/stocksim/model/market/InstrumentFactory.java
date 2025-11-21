package org.team27.stocksim.model.market;

public interface InstrumentFactory {
    Instrument createInstrument(String name, Double tickSize, Integer lotSize);
}
