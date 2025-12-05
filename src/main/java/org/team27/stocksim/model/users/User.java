package org.team27.stocksim.model.users;

import org.team27.stocksim.model.portfolio.Portfolio;

public class User extends Trader {
    private OrderHistory orderHistory;

    public User(String id, String name, Portfolio portfolio) {
        super(id, name, portfolio);
        this.orderHistory = new OrderHistory();
    }

    public OrderHistory getOrderHistory() {
        return orderHistory;
    }
}
