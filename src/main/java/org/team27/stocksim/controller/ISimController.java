package org.team27.stocksim.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.users.OrderHistory;
import org.team27.stocksim.model.users.User;
import org.team27.stocksim.model.util.dto.InstrumentDTO;
import org.team27.stocksim.observer.IModelObserver;

public interface ISimController {
    void createStock(String symbol, String stockName, String tickSize, String lotSize, String category);

    void addObserver(IModelObserver obs);

    void removeObserver(IModelObserver obs);

    ArrayList<String> getAllCategories();

    HashMap<String, InstrumentDTO> getStocks(String category);

    User getUser();

    void buyStock(String stockSymbol, int quantity, BigDecimal price);

    void placeMarketBuyOrder(String stockSymbol, int quantity);

    void placeMarketSellOrder(String stockSymbol, int quantity);

    void sellStock(String stockSymbol, int quantity, BigDecimal price);

    OrderHistory getOrderHistory();

    void setSelectedStock(InstrumentDTO stock);

    InstrumentDTO getSelectedStock();
}
