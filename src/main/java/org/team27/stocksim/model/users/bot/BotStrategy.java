package org.team27.stocksim.model.users.bot;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.users.Bot;

import java.util.List;

public interface BotStrategy {
    List<Order> decide(StockSim model, Bot bot);
}
