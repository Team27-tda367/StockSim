package org.team27.stocksim.model.market;

import java.util.ArrayList;
import java.util.List;

public class StockSim {

    /* Listeners */
    private final List<StockSimListener> listeners = new ArrayList<>();

    public void addListener(StockSimListener l) {
        listeners.add(l);
    }

    public void removeListener(StockSimListener l) {
        listeners.remove(l);
    }

    private void notifyMessageChanged(String newMessage) {
        for (StockSimListener l : List.copyOf(listeners)) {
            l.messageChanged(newMessage);
        }
    }

    /* Test string and methods */
    private String message = "Initial message";

    public void testFetch() {
        String msg = "Test string from model";
        this.message = msg;
        notifyMessageChanged(msg);
    }

    public String getMessage() {
        return message;
    }
}
