package org.team27.stocksim.model.users.bot;

import java.util.List;

import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.market.StockSim;
import org.team27.stocksim.model.users.Bot;

public interface BotStrategy {
    List<Order> decide(StockSim model, Bot bot);
}
