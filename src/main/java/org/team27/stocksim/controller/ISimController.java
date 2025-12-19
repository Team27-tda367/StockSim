package org.team27.stocksim.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.team27.stocksim.dto.InstrumentDTO;
import org.team27.stocksim.dto.OrderDTO;
import org.team27.stocksim.dto.TradeDTO;
import org.team27.stocksim.dto.UserDTO;
import org.team27.stocksim.observer.IModelObserver;

public interface ISimController {

    void addObserver(IModelObserver obs);

    void removeObserver(IModelObserver obs);

    ArrayList<String> getAllCategories();

    HashMap<String, InstrumentDTO> getStocks(String category);

    UserDTO getUser();

    void buyStock(String stockSymbol, int quantity, BigDecimal price);

    void placeMarketBuyOrder(String stockSymbol, int quantity);

    void placeMarketSellOrder(String stockSymbol, int quantity);

    void sellStock(String stockSymbol, int quantity, BigDecimal price);

    List<OrderDTO> getOrderHistory();

    List<TradeDTO> getTradeHistory();

    void setSelectedStock(InstrumentDTO stock);

    InstrumentDTO getSelectedStock();
}
