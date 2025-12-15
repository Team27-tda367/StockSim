package org.team27.stocksim.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.team27.stocksim.SimSetup;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.users.OrderHistory;
import org.team27.stocksim.model.users.User;
import org.team27.stocksim.model.util.dto.InstrumentDTO;
import org.team27.stocksim.observer.IModelObserver;

public class SimController implements ISimController {

    private StockSim model;

    public SimController(StockSim model) {
        this.model = model;
    }

    public void setUpSimulation() {
        SimSetup setup = new SimSetup(model);
        setup.start();
    }

    public void createStock(String symbol, String stockName, String tickSize, String lotSize, String category) {
        model.createStock(symbol, stockName, tickSize, lotSize, category);
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
    public User getUser() {
        return model.getCurrentUser();
    }

    @Override
    public void buyStock(String stockSymbol, int quantity, BigDecimal price) {

        Order buyOrder = new Order(Order.Side.BUY, stockSymbol, price, quantity,
                model.getCurrentUser().getId());
        model.placeOrder(buyOrder);
    }

    @Override
    public void sellStock(String stockSymbol, int quantity, BigDecimal price) {
        User user = model.getCurrentUser();
        int availableQuantity = user.getPortfolio().getStockQuantity(stockSymbol);

        if (availableQuantity < quantity) {
            System.out.println("Insufficient stock quantity to sell.");
            quantity = availableQuantity;
        }

        Order sellOrder = new Order(Order.Side.SELL, stockSymbol, price, quantity, user.getId());
        model.placeOrder(sellOrder);
    }

    @Override
    public OrderHistory getOrderHistory() {
        User user = model.getCurrentUser();
        return user != null ? user.getOrderHistory() : new OrderHistory();
    }

}