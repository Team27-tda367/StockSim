package org.team27.stocksim.model.users.bot;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.users.Bot;

public interface BotStrategy {
    void decide(StockSim model, Bot bot);
}
