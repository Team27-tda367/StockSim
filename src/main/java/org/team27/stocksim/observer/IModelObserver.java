package org.team27.stocksim.observer;

import org.team27.stocksim.model.util.dto.InstrumentDTO;

import java.util.HashMap;

public interface IModelObserver {

    void onStocksChanged(Object payload);

    void onPriceUpdate(HashMap<String, ? extends InstrumentDTO> stocks);

    void onTradeSettled();

    void onPortfolioChanged();
}
