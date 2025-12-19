package org.team27.stocksim.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.team27.stocksim.dto.InstrumentDTO;
import org.team27.stocksim.dto.OrderDTO;
import org.team27.stocksim.dto.TradeDTO;
import org.team27.stocksim.dto.UserDTO;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.users.User;
import org.team27.stocksim.observer.IModelObserver;

public class SimController implements ISimController {

    private StockSim model;

    public SimController(StockSim model) {
        this.model = model;
    }

    public void addObserver(IModelObserver obs) {
        model.addObserver(obs);
    }

    public void removeObserver(IModelObserver obs) {
        model.removeObserver(obs);
    }

    @Override
    public ArrayList<String> getAllCategories() {
        return model.getCategories();
    }

    @Override
    public HashMap<String, InstrumentDTO> getStocks(String category) {
        return model.getStocks(category);
    }

    @Override
    public UserDTO getUser() {
        return model.getCurrentUserDto();
    }

    @Override
    public void buyStock(String stockSymbol, int quantity, BigDecimal price) {

        Order buyOrder = new Order(Order.Side.BUY, stockSymbol, price, quantity,
                model.getCurrentUser().getId());
        model.placeOrder(buyOrder);
    }

    public void placeMarketBuyOrder(String stockSymbol, int quantity) {
        Order order = new Order(Order.Side.BUY, Order.OrderType.MARKET,
                stockSymbol, BigDecimal.ZERO, quantity, model.getCurrentUser().getId());
        model.placeOrder(order);
    }

    public void placeMarketSellOrder(String stockSymbol, int quantity) {
        User user = model.getCurrentUser();
        quantity = getQuantity(user, quantity, stockSymbol);
        if (quantity <= 0) {
            return;
        }

        Order order = new Order(Order.Side.SELL, Order.OrderType.MARKET, stockSymbol, BigDecimal.ZERO, quantity,
                model.getCurrentUser().getId());

        model.placeOrder(order);
    }

    private int getQuantity(User user, int quantity, String stockSymbol) {
        int availableQuantity = user.getPortfolio().getStockQuantity(stockSymbol);

        if (availableQuantity < quantity) {
            quantity = availableQuantity;
        }
        return quantity;

    }

    @Override
    public void sellStock(String stockSymbol, int quantity, BigDecimal price) {
        User user = model.getCurrentUser();

        quantity = getQuantity(user, quantity, stockSymbol);
        if (quantity <= 0) {
            return;
        }
        Order sellOrder = new Order(Order.Side.SELL, stockSymbol, price, quantity, user.getId());
        model.placeOrder(sellOrder);
    }

    @Override
    public List<OrderDTO> getOrderHistory() {
        User user = model.getCurrentUser();
        return user != null ? user.getOrderHistory().getAllOrdersDTO() : new ArrayList<>();
    }

    @Override
    public List<TradeDTO> getTradeHistory() {
        User user = model.getCurrentUser();
        return user != null ? user.getOrderHistory().getAllTradesDTO() : new ArrayList<>();
    }

    @Override
    public void setSelectedStock(InstrumentDTO stock) {
        model.getSelectionManager().setSelectedStock(stock);
    }

    @Override
    public InstrumentDTO getSelectedStock() {
        return model.getSelectionManager().getSelectedStock();
    }
}