package org.team27.stocksim.model.users;

public class BotFactory implements TraderFactory{
    @Override
    public Trader createTrader(String id) {
        return new Bot(id);
    }
}
