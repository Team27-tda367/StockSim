package org.team27.stocksim.controller;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.market.Stock;
import org.team27.stocksim.ui.fx.IView;

public class SimController {

    private StockSim model;
    private IView view;

    public SimController(IView view, StockSim model) {
        this.model = model;
        this.view = view;

        model.addObserver(view);
    }

    public void start() {
        view.show();
        // show initial stage
        view.newStockCreated(model.getStocks());
    }

    public void createStock(String symbol, String stockName, String tickSize, String lotSize) {
        model.createStock(symbol, stockName, tickSize, lotSize);
    }

}