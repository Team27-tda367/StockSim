package org.team27.stocksim.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.observer.ModelObserver;

/**
 * ViewAdapter acts as a hub between the model and view controllers.
 * It implements ModelObserver to receive events from the model,
 * and broadcasts them to registered view listeners.
 */
public class ViewAdapter implements ModelObserver {

    // Listener interfaces for view controllers
    public interface StocksChangedListener {
        void onStocksChanged(Object payload);
    }

    public interface PriceUpdateListener {
        void onPriceUpdate(HashMap<String, ? extends Instrument> stocks);
    }

    public interface TradeSettledListener {
        void onTradeSettled();
    }

    // Lists of registered listeners
    private final List<StocksChangedListener> stocksChangedListeners = new ArrayList<>();
    private final List<PriceUpdateListener> priceUpdateListeners = new ArrayList<>();
    private final List<TradeSettledListener> tradeSettledListeners = new ArrayList<>();

    // Registration methods
    public void addStocksChangedListener(StocksChangedListener listener) {
        stocksChangedListeners.add(listener);
    }

    public void addPriceUpdateListener(PriceUpdateListener listener) {
        priceUpdateListeners.add(listener);
    }

    public void addTradeSettledListener(TradeSettledListener listener) {
        tradeSettledListeners.add(listener);
    }

    public void removeStocksChangedListener(StocksChangedListener listener) {
        stocksChangedListeners.remove(listener);
    }

    public void removePriceUpdateListener(PriceUpdateListener listener) {
        priceUpdateListeners.remove(listener);
    }

    public void removeTradeSettledListener(TradeSettledListener listener) {
        tradeSettledListeners.remove(listener);
    }

    // ModelObserver implementation - broadcasts to listeners
    @Override
    public void onStocksChanged(Object payload) {
        for (StocksChangedListener listener : stocksChangedListeners) {
            listener.onStocksChanged(payload);
        }
    }

    @Override
    public void onPriceUpdate(HashMap<String, ? extends Instrument> stocks) {
        for (PriceUpdateListener listener : priceUpdateListeners) {
            listener.onPriceUpdate(stocks);
        }
    }

    @Override
    public void onTradeSettled() {
        for (TradeSettledListener listener : tradeSettledListeners) {
            listener.onTradeSettled();
        }
    }
}
