package org.team27.stocksim.model.util.dto;

import org.team27.stocksim.model.users.OrderHistory;

public class UserDTO {

    private String id;
    private String name;
    private PortfolioDTO portfolio;
    private OrderHistoryDTO orderHistory;

    // Empty constructor for serialization/deserialization
    public UserDTO() {}

    public UserDTO(String id, String name, PortfolioDTO portfolio, OrderHistoryDTO orderHistory) {
        this.id = id;
        this.name = name;
        this.portfolio = portfolio;
        this.orderHistory = orderHistory;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PortfolioDTO getPortfolio() {
        return portfolio;
    }

    public OrderHistoryDTO getOrderHistory() {
        return orderHistory;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPortfolio(PortfolioDTO portfolio) {
        this.portfolio = portfolio;
    }

    public void setOrderHistory(OrderHistoryDTO orderHistory) {
        this.orderHistory = orderHistory;
    }
}
