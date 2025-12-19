package org.team27.stocksim.model.market;

import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.Trader;
import org.team27.stocksim.model.users.User;
import org.team27.stocksim.model.instruments.Instrument;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Engine responsible for settling matched trades by updating portfolios.
 *
 * <p>SettlementEngine executes the final step of trade processing: transferring
 * cash and securities between trading parties. It ensures atomic settlement,
 * validates buyer funds, updates portfolios, records trade history, and updates
 * stock prices to reflect the last traded price.</p>
 *
 * <p><strong>Design Patterns:</strong> Strategy + Command</p>
 * <ul>
 *   <li>Atomic trade settlement (funds + securities transfer)</li>
 *   <li>Validates buyer has sufficient funds before settlement</li>
 *   <li>Updates both buyer and seller portfolios</li>
 *   <li>Records trades in user order history</li>
 *   <li>Updates stock last traded price</li>
 *   <li>Fires callback on successful settlement</li>
 * </ul>
 *
 * <h2>Settlement Process:</h2>
 * <ol>
 *   <li>Lookup buyer and seller traders from order IDs</li>
 *   <li>Calculate total trade value (price × quantity)</li>
 *   <li>Validate buyer has sufficient cash</li>
 *   <li>Withdraw cash from buyer portfolio</li>
 *   <li>Deposit cash to seller portfolio</li>
 *   <li>Transfer securities (remove from seller, add to buyer)</li>
 *   <li>Record trade in user histories</li>
 *   <li>Update stock's current price</li>
 *   <li>Invoke settlement callback</li>
 * </ol>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * ConcurrentHashMap<Integer, String> orderTracking = new ConcurrentHashMap<>();
 * Consumer<Trade> callback = trade ->
 *     System.out.println("Trade settled: " + trade);
 *
 * SettlementEngine engine = new SettlementEngine(orderTracking, callback);
 *
 * // Track orders
 * engine.trackOrder(order.getOrderId(), trader.getId());
 *
 * // Settle trade
 * boolean success = engine.settleTrade(trade, tradersMap, instrumentsMap);
 * if (success) {
 *     System.out.println("Settlement complete");
 * }
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see ISettlementEngine
 * @see Trade
 * @see MatchingEngine
 * @see Portfolio
 */
public class SettlementEngine implements ISettlementEngine {

    /**
     * Maps order IDs to trader IDs for settlement lookup.
     */
    private final ConcurrentHashMap<Integer, String> orderIdToTraderId;

    /**
     * Callback invoked after successful trade settlement.
     */
    private final Consumer<Trade> onTradeSettled;

    /**
     * Constructs a SettlementEngine with order tracking and callback.
     *
     * @param orderIdToTraderId Map for tracking which trader placed which order
     * @param onTradeSettled Callback invoked after settlement
     */
    public SettlementEngine(ConcurrentHashMap<Integer, String> orderIdToTraderId, Consumer<Trade> onTradeSettled) {
        this.orderIdToTraderId = orderIdToTraderId;
        this.onTradeSettled = onTradeSettled;
    }

    /**
     * Settles a trade by transferring cash and securities.
     *
     * <p>This method performs the complete settlement process atomically.
     * If the buyer lacks sufficient funds, settlement fails and portfolios
     * remain unchanged.</p>
     *
     * @param trade The trade to settle
     * @param traders Map of all traders
     * @param stocks Map of all instruments
     * @return true if settlement succeeded, false if failed
     */
    @Override
    public boolean settleTrade(Trade trade, HashMap<String, Trader> traders, HashMap<String, Instrument> stocks) {
        String buyerTraderId = orderIdToTraderId.get(trade.getBuyOrderId());
        String sellerTraderId = orderIdToTraderId.get(trade.getSellOrderId());

        if (buyerTraderId == null || sellerTraderId == null) {
            // TODO
            return false;
        }

        Trader buyer = traders.get(buyerTraderId);
        Trader seller = traders.get(sellerTraderId);

        if (buyer == null || seller == null) {
            // TODO
            return false;
        }

        BigDecimal tradeValue = trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity()));

        Portfolio buyerPortfolio = buyer.getPortfolio();
        Portfolio sellerPortfolio = seller.getPortfolio();

        if (!buyerPortfolio.withdraw(tradeValue)) {
            // TODO
            return false;
        }

        sellerPortfolio.deposit(tradeValue);

        sellerPortfolio.removeStock(trade.getStockSymbol(), trade.getQuantity(), trade);
        buyerPortfolio.addStock(trade.getStockSymbol(), trade.getQuantity(), trade.getPrice(), trade);

        recordTradeInHistory(buyer, trade);
        recordTradeInHistory(seller, trade);

        updateStockPrice(stocks, trade);

        if (onTradeSettled != null) {
            onTradeSettled.accept(trade);
        }

        return true;
    }

    /**
     * Records trade in trader's history if trader is a User.
     *
     * @param trader The trader to update
     * @param trade The trade to record
     */
    private void recordTradeInHistory(Trader trader, Trade trade) {
        if (trader instanceof User) {
            ((User) trader).getOrderHistory().addTrade(trade);
        }
    }

    /**
     * Updates the stock's current price to the trade price.
     *
     * @param stocks Map of instruments
     * @param trade The executed trade
     */
    private void updateStockPrice(HashMap<String, Instrument> stocks, Trade trade) {
        Instrument stock = stocks.get(trade.getStockSymbol());
        if (stock != null) {
            stock.setCurrentPrice(trade.getPrice());
        }
    }

    /**
     * Tracks which trader placed which order for settlement lookup.
     *
     * @param orderId The order ID
     * @param traderId The trader who placed the order
     */
    @Override
    public void trackOrder(int orderId, String traderId) {
        orderIdToTraderId.put(orderId, traderId);
    }
}
