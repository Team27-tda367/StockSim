package org.team27.stocksim.model.users;
import org.team27.stocksim.model.util.dto.UserDTO;
import org.team27.stocksim.model.users.bot.IBotStrategy;
import java.util.HashMap;

public interface ITraderRegistry {

    boolean createUser(String id, String name);

    boolean createBot(String id, String name);

    boolean createBot(String id, String name, IBotStrategy strategy);

    HashMap<String, Trader> getAllTraders();

    HashMap<String, Bot> getBots();

    HashMap<String, User> getUsers();

    Trader getTrader(String id);

    User getCurrentUser();

    void setCurrentUser(String userId);

    UserDTO getCurrentUserDto();
}
