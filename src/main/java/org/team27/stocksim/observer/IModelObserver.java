package org.team27.stocksim.observer;

import org.team27.stocksim.model.instruments.Instrument;

import java.util.HashMap;

public interface IModelObserver {

    void onStocksChanged(Object payload);

    void onPriceUpdate(HashMap<String, ? extends Instrument> stocks);

    void onTradeSettled();

    void onPortfolioChanged();
}
