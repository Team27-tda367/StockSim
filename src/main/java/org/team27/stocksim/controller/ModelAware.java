package org.team27.stocksim.controller;

import org.team27.stocksim.model.market.StockSim;

/**
 * Implementera detta interface i controllers som behöver få modellen injicerad
 */
public interface ModelAware {
    void setModel(StockSim model);
}
