package org.team27.stocksim.model.market;

import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.users.Trader;
import org.team27.stocksim.model.users.User;
import org.team27.stocksim.model.instruments.Instrument;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.function.Consumer;


public class SettlementEngine implements ISettlementEngine {

    private final HashMap<Integer, String> orderIdToTraderId;
    private final Consumer<Trade> onTradeSettled;

    public SettlementEngine(HashMap<Integer, String> orderIdToTraderId, Consumer<Trade> onTradeSettled) {
        this.orderIdToTraderId = orderIdToTraderId;
        this.onTradeSettled = onTradeSettled;
    }


    @Override
    public boolean settleTrade(Trade trade, HashMap<String, Trader> traders, HashMap<String, Instrument> stocks) {
        String buyerTraderId = orderIdToTraderId.get(trade.getBuyOrderId());
        String sellerTraderId = orderIdToTraderId.get(trade.getSellOrderId());

        if (buyerTraderId == null || sellerTraderId == null) {
            //TODO
            return false;
        }

        Trader buyer = traders.get(buyerTraderId);
        Trader seller = traders.get(sellerTraderId);

        if (buyer == null || seller == null) {
            //TODO
            return false;
        }

        BigDecimal tradeValue = trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity()));

        Portfolio buyerPortfolio = buyer.getPortfolio();
        Portfolio sellerPortfolio = seller.getPortfolio();


        if (!buyerPortfolio.withdraw(tradeValue)) {
            //TODO
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

    private void recordTradeInHistory(Trader trader, Trade trade) {
        if (trader instanceof User) {
            ((User) trader).getOrderHistory().addTrade(trade);
            System.out.println(((User) trader).getOrderHistory().getAllTrades().size() + " trades in history for " + trader.getId());
        }
    }

    private void updateStockPrice(HashMap<String, Instrument> stocks, Trade trade) {
        Instrument stock = stocks.get(trade.getStockSymbol());
        if (stock != null) {
            stock.setCurrentPrice(trade.getPrice());
        }
    }

    @Override
    public void trackOrder(int orderId, String traderId) {
        orderIdToTraderId.put(orderId, traderId);
    }
}
