package org.team27.stocksim.model.instruments;

import java.math.BigDecimal;

public interface IInstrumentFactory {
    Instrument createInstrument(String symbol, String name, BigDecimal tickSize, int lotSize, String category,
            BigDecimal initialPrice);
}
