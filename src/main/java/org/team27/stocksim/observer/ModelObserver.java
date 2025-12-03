package org.team27.stocksim.observer;

import java.util.HashMap;
import org.team27.stocksim.model.market.Instrument;

public interface ModelObserver {
    void newStockCreated(HashMap<String, Instrument> stocks);
}
