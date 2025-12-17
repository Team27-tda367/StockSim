package org.team27.stocksim.model.market;

import org.team27.stocksim.model.clock.ClockProvider;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MatchingEngine {

    public MatchingEngine() {

    }

    private static void executeTrade(Order incomingOrder, Order matchingOrder, OrderBook orderBook,
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
        
        // Market orders should not rest in the book - only add if limit order with remaining quantity
        if (incomingOrder.getRemainingQuantity() > 0 && !incomingOrder.isMarketOrder()) {
            orderBook.add(incomingOrder);
        }
        
        return trades;
    }

    /**
     * Determines if an incoming order can match with a resting order.
     * Market orders match at any price, limit orders only match when price is acceptable.
     */
    private boolean canMatch(Order incomingOrder, Order restingOrder) {
        // Market orders always match if there's liquidity
        if (incomingOrder.isMarketOrder()) {
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
