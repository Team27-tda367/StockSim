package org.team27.stocksim.model.market;

import org.team27.stocksim.model.clock.ClockProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Engine responsible for matching buy and sell orders in the market.
 *
 * <p>The matching engine implements price-time priority matching logic, where
 * orders are matched based on the best available price, and orders at the same
 * price are matched based on arrival time. It handles both limit orders (with
 * specific prices) and market orders (executed at best available price with
 * deviation limits).</p>
 *
 * <p><strong>Design Patterns:</strong> Strategy + Chain of Responsibility</p>
 * <ul>
 *   <li>Price-time priority matching algorithm</li>
 *   <li>Market order protection via price deviation limits</li>
 *   <li>Self-trade prevention (same trader can't match own orders)</li>
 *   <li>Tracks last trade prices for market order validation</li>
 *   <li>Generates trades atomically with order book updates</li>
 * </ul>
 *
 * <h2>Matching Rules:</h2>
 * <ol>
 *   <li>Buy orders match with sell orders at same or better price</li>
 *   <li>Self-trades are prevented (same trader ID)</li>
 *   <li>Market orders match within configured price deviation limits</li>
 *   <li>Limit orders only rest in book if not market orders</li>
 *   <li>Filled orders are removed from the order book</li>
 * </ol>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * MatchingEngine engine = new MatchingEngine();
 * OrderBook orderBook = new OrderBook("AAPL");
 *
 * Order buyOrder = new Order(Order.Side.BUY, "AAPL", new BigDecimal("150.00"), 100, "trader1");
 * List<Trade> trades = engine.match(buyOrder, orderBook);
 *
 * for (Trade trade : trades) {
 *     System.out.println("Executed trade: " + trade.getQuantity() + " @ $" + trade.getPrice());
 * }
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see Order
 * @see OrderBook
 * @see Trade
 * @see MarketOrderConfig
 */
public class MatchingEngine {

    /**
     * Configuration for market order behavior (price deviation limits, etc.).
     */
    private final MarketOrderConfig config;

    /**
     * Cache of last trade prices by symbol for market order validation.
     */
    private final Map<String, BigDecimal> lastTradePrices;

    /**
     * Constructs a MatchingEngine with default configuration.
     */
    public MatchingEngine() {
        this(MarketOrderConfig.createDefault());
    }

    /**
     * Constructs a MatchingEngine with custom configuration.
     *
     * @param config Configuration controlling market order behavior
     */
    public MatchingEngine(MarketOrderConfig config) {
        this.config = config;
        this.lastTradePrices = new ConcurrentHashMap<>();
    }

    private void executeTrade(Order incomingOrder, Order matchingOrder, OrderBook orderBook,
            List<Trade> trades) {

        int tradeQuantity = Math.min(incomingOrder.getRemainingQuantity(), matchingOrder.getRemainingQuantity());

        incomingOrder.fill(tradeQuantity);
        matchingOrder.fill(tradeQuantity);

        if (matchingOrder.getRemainingQuantity() == 0) {
            orderBook.remove(matchingOrder);
        }
        Trade trade = new Trade(
                incomingOrder.isBuyOrder() ? incomingOrder.getOrderId() : matchingOrder.getOrderId(),
                !incomingOrder.isBuyOrder() ? incomingOrder.getOrderId() : matchingOrder.getOrderId(),
                incomingOrder.getSymbol(), matchingOrder.getPrice(), tradeQuantity, ClockProvider.getClock().instant());
        trades.add(trade);

        lastTradePrices.put(trade.getStockSymbol(), trade.getPrice());
    }

    public List<Trade> match(Order incomingOrder, OrderBook orderBook) {
        List<Trade> trades = new ArrayList<>();

        if (incomingOrder.isBuyOrder()) {
            while (!incomingOrder.isFilled()) {
                Order bestAsk = orderBook.getBestAsk();
                if (bestAsk != null && canMatch(incomingOrder, bestAsk)) {
                    executeTrade(incomingOrder, bestAsk, orderBook, trades);
                } else {
                    break;
                }
            }
        } else {
            while (!incomingOrder.isFilled()) {
                Order bestBid = orderBook.getBestBid();
                if (bestBid != null && canMatch(incomingOrder, bestBid)) {
                    executeTrade(incomingOrder, bestBid, orderBook, trades);
                } else {
                    break;
                }
            }
        }

        // Market orders should not rest in the book, only add if limit order with remaining quantity
        if (incomingOrder.getRemainingQuantity() > 0 && !incomingOrder.isMarketOrder()) {
            orderBook.add(incomingOrder);
        }

        return trades;
    }


    private boolean canMatch(Order incomingOrder, Order restingOrder) {

        if (incomingOrder.getTraderId().equals(restingOrder.getTraderId())) {
            return false;
        }


        if (incomingOrder.isMarketOrder()) {
            BigDecimal lastPrice = lastTradePrices.get(incomingOrder.getSymbol());
            if (lastPrice != null) {
                BigDecimal maxAllowedDeviation = lastPrice.multiply(config.getMaxPriceDeviation());
                BigDecimal maxPrice = lastPrice.add(maxAllowedDeviation);
                BigDecimal minPrice = lastPrice.subtract(maxAllowedDeviation);


                if (restingOrder.getPrice().compareTo(maxPrice) > 0 ||
                    restingOrder.getPrice().compareTo(minPrice) < 0) {
                    return false;
                }
            }
            return true;
        }
        
        // Limit orders match based on price
        if (incomingOrder.isBuyOrder()) {
            return incomingOrder.getPrice().compareTo(restingOrder.getPrice()) >= 0;
        } else {
            return incomingOrder.getPrice().compareTo(restingOrder.getPrice()) <= 0;
        }
    }
}
