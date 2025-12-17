package org.team27.stocksim.model.users;

import org.team27.stocksim.model.users.bot.IBotStrategy;
import java.util.HashMap;

public interface ITraderRegistry {

    boolean createUser(String id, String name);

    boolean createBot(String id, String name);

    boolean createBot(String id, String name, IBotStrategy strategy);

    HashMap<String, Trader> getAllTraders();

    HashMap<String, Trader> getBots();

    HashMap<String, User> getUsers();

    Trader getTrader(String id);

    User getCurrentUser();

    void setCurrentUser(String userId);
}
