package org.team27.stocksim.model.instruments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class InstrumentRegistry {

    private final HashMap<String, Instrument> instruments;
    private final InstrumentFactory instrumentFactory;

    public InstrumentRegistry(InstrumentFactory instrumentFactory) {
        this.instruments = new HashMap<>();
        this.instrumentFactory = instrumentFactory;
    }


    public boolean createInstrument(String symbol, String stockName, String tickSize, String lotSize, String category) {
        String highSymbol = symbol.toUpperCase();

        if (instruments.containsKey(highSymbol)) {
            System.out.println("Stock symbol already exists: " + highSymbol);
            return false;
        }

        Instrument instrument = instrumentFactory.createInstrument(
            highSymbol,
            stockName,
            new BigDecimal(tickSize),
            Integer.parseInt(lotSize),
            category
        );

        instruments.put(highSymbol, instrument);
        return true;
    }

    public HashMap<String, Instrument> getAllInstruments() {
        return instruments;
    }


    public HashMap<String, Instrument> getInstrumentsByCategory(String category) {
        if (category.equals("All")) {
            return instruments;
        }

        HashMap<String, Instrument> filtered = new HashMap<>();
        for (Instrument instrument : instruments.values()) {
            if (instrument.getCategory().equals(category)) {
                filtered.put(instrument.getSymbol(), instrument);
            }
        }
        return filtered;
    }

    public ArrayList<String> getCategories() {
        ArrayList<String> categoryLabels = new ArrayList<>();
        for (ECategory category : ECategory.values()) {
            categoryLabels.add(category.getLabel());
        }
        return categoryLabels;
    }

    public Instrument getInstrument(String symbol) {
        return instruments.get(symbol.toUpperCase());
    }

    public boolean hasInstrument(String symbol) {
        return instruments.containsKey(symbol.toUpperCase());
    }
}

