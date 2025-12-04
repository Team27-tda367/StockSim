package org.team27.stocksim.model.market;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
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
                incomingOrder.getSymbol(), matchingOrder.getPrice(), tradeQuantity, Instant.now());
        trades.add(trade);
    }

    public List<Trade> match(Order incomingOrder, OrderBook orderBook) {
        List<Trade> trades = new ArrayList<>();
        if (incomingOrder.isBuyOrder()) { // TODO Maybe refactor to use enum method. isBuy() and separate to matchBuy
                                          // and matchSell
            while (!incomingOrder.isFilled()) {
                Order bestAsk = orderBook.getBestAsk();
                if (bestAsk != null && incomingOrder.getPrice().compareTo(bestAsk.getPrice()) >= 0) {
                    executeTrade(incomingOrder, bestAsk, orderBook, trades);
                } else {
                    break;
                }
            }
        } else {
            while (!incomingOrder.isFilled()) {
                Order bestBid = orderBook.getBestBid();
                if (bestBid != null && incomingOrder.getPrice().compareTo(bestBid.getPrice()) <= 0) {
                    executeTrade(incomingOrder, bestBid, orderBook, trades);
                } else {
                    break;
                }
            }
        }
        if (incomingOrder.getRemainingQuantity() > 0) {
            if (incomingOrder.isBuyOrder()) {
                orderBook.add(incomingOrder); // TODO refactor addBid and addAsk to addOrder based on side
            } else {
                orderBook.add(incomingOrder);
            }
        }
        return trades;
    }
}
