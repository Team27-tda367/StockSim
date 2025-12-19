package org.team27.stocksim.dto;

public class UserDTO {

    private final String id;
    private final String name;
    private final PortfolioDTO portfolio;
    private final OrderHistoryDTO orderHistory;

    public UserDTO(String id, String name, PortfolioDTO portfolio, OrderHistoryDTO orderHistory) {
        this.id = id;
        this.name = name;
        this.portfolio = portfolio;
        this.orderHistory = orderHistory;
    }

    // Getters only (immutable)
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
}
