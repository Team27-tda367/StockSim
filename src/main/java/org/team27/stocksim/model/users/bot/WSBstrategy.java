package org.team27.stocksim.model.users.bot;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.users.Bot;

import java.util.Collections;
import java.util.List;

public class WSBstrategy implements BotStrategy {

    @Override
    public List<Order> decide(StockSim model, Bot bot) {
        // TODO: Implement WSB strategy logic
        return Collections.emptyList();
    }

}
