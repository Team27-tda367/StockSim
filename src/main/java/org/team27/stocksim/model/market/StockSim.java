package org.team27.stocksim.model.market;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;

public class StockSim {
    MarketState state;
    HashMap<String, Instrument> stocks;
    InstrumentFactory stockFactory;

    private String createdStockMsg = "Initial message";

    public String messageCreatedStock() {
        return createdStockMsg;
    }

    public StockSim() {
        // Stock related inits
        stocks = new HashMap<>();
        stockFactory = new StockFactory();

    }

    /* Listeners */
    private final List<StockSimListener> listeners = new ArrayList<>();

    public void addListener(StockSimListener l) {
        listeners.add(l);
    }

    public void removeListener(StockSimListener l) {
        listeners.remove(l);
    }

    private void notifyListeners(String newMessage) {
        for (StockSimListener l : List.copyOf(listeners)) {
            l.messageChanged(newMessage);
        }
    }

    public void createStock(String symbol, String stockName, String tickSize, String lotSize) {
        // checking if symbol already exists (if yes -> error)
        String highSymbol = symbol.toUpperCase();
        if (stocks.containsKey(highSymbol)) {
            createdStockMsg = "Symbol already exists!";
        } else {
            Instrument stock = stockFactory.createInstrument(highSymbol, stockName, Double.parseDouble(tickSize),
                    Integer.parseInt(lotSize));
            stocks.put(highSymbol, stock);
            String createdStock = highSymbol + " " + stockName + " " + tickSize + " " + lotSize;
            createdStockMsg = createdStock;
        }

        notifyListeners(createdStockMsg);
    }

}