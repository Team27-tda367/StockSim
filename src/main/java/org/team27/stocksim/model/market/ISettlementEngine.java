package org.team27.stocksim.model.market;
import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.users.Trader;
import java.util.HashMap;

public interface ISettlementEngine {

    boolean settleTrade(Trade trade, HashMap<String, Trader> traders, HashMap<String, Instrument> stocks);

    void trackOrder(int orderId, String traderId);
}
