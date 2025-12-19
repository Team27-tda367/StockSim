package org.team27.stocksim.model.market;
import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.users.Trader;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface IMarket {

    void placeOrder(Order order, HashMap<String, Trader> traders, HashMap<String, Instrument> stocks);

    void cancelOrder(int orderId, HashMap<String, Trader> traders);

    void addOrderBook(String symbol, OrderBook orderBook);

    void removeOrderBook(String symbol);

    OrderBook getOrderBook(String symbol);

    List<Trade> getCompletedTrades();

    void setOnPriceUpdate(Consumer<Set<String>> callback);

    void setOnTradeSettled(Consumer<Trade> callback);
}
