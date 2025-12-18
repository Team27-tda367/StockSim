package org.team27.stocksim.model.instruments;

import java.util.ArrayList;
import java.util.HashMap;

public interface IInstrumentRegistry {

    boolean createInstrument(String symbol, String stockName, String tickSize, String lotSize, String category);

    HashMap<String, Instrument> getAllInstruments();

    HashMap<String, Instrument> getInstrumentsByCategory(String category);

    ArrayList<String> getCategories();

    Instrument getInstrument(String symbol);

    boolean hasInstrument(String symbol);
}
