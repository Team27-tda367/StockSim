package org.team27.stocksim.observer;

import java.util.HashMap;

public interface ModelObserver {
    void newStockCreated(HashMap stocks);
}
