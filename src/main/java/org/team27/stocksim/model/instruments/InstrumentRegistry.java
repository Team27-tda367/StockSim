package org.team27.stocksim.model.instruments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class InstrumentRegistry implements IInstrumentRegistry {

    private final HashMap<String, Instrument> instruments;
    private final IInstrumentFactory instrumentFactory;

    public InstrumentRegistry(IInstrumentFactory instrumentFactory) {
        this.instruments = new HashMap<>();
        this.instrumentFactory = instrumentFactory;
    }


    @Override
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


    @Override
    public HashMap<String, Instrument> getAllInstruments() {
        return instruments;
    }


    @Override
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


    @Override
    public ArrayList<String> getCategories() {
        ArrayList<String> categoryLabels = new ArrayList<>();
        for (ECategory category : ECategory.values()) {
            categoryLabels.add(category.getLabel());
        }
        return categoryLabels;
    }

    @Override
    public Instrument getInstrument(String symbol) {
        return instruments.get(symbol.toUpperCase());
    }


    @Override
    public boolean hasInstrument(String symbol) {
        return instruments.containsKey(symbol.toUpperCase());
    }
}
