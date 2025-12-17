package org.team27.stocksim.model.users;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.market.Order;

import java.util.List;

public class BotAction {
    private final Bot bot;
    private final List<Order> orders;
    private final StockSim model;

    public BotAction(Bot bot, List<Order> orders, StockSim model) {
        this.bot = bot;
        this.orders = orders;
        this.model = model;
    }

    public void execute() {
        try {
            for (Order order : orders) {
                model.placeOrder(order);
            }
        } catch (Exception e) {
            System.err.println("Error executing bot action for " + bot.getId() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            bot.returnToIdle();
        }
    }
}

