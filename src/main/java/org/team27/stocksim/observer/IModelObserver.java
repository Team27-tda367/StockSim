package org.team27.stocksim.observer;

import java.util.HashMap;

import org.team27.stocksim.dto.InstrumentDTO;

public interface IModelObserver {

    void onStocksChanged(Object payload);

    void onPriceUpdate(HashMap<String, ? extends InstrumentDTO> stocks);

    void onTradeSettled();

    void onPortfolioChanged();
}
