package org.team27.stocksim.model.users.bot;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.util.dto.InstrumentDTO;
import org.team27.stocksim.model.market.Order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Panic Seller Strategy - Sells quickly when prices drop
 * This bot gets nervous and sells at the first sign of trouble:
 * - Monitors for any price drops
 * - Sells immediately when losing money
 * - Rarely buys, mostly holds cash
 */
public class PanicSellerStrategy extends AbstractBotStrategy {

    private final double checkProbability;
    private final double panicThreshold;
    private final int lookbackPeriod;
    private final double buyProbability;

    public PanicSellerStrategy() {
        this(new Random(), 0.15, 0.02, 5, 0.005, 1, 3);
    }

    public PanicSellerStrategy(Random random, double checkProbability, double panicThreshold,
            int lookbackPeriod, double buyProbability, int minQuantity, int maxQuantity) {
        super(random, minQuantity, maxQuantity);
        this.checkProbability = checkProbability;
        this.panicThreshold = panicThreshold;
        this.lookbackPeriod = lookbackPeriod;
        this.buyProbability = buyProbability;
    }

    @Override
    public List<Order> decide(StockSim model, Bot bot) {
        List<Order> orders = new ArrayList<>();
        if (random.nextDouble() > checkProbability) {
            return orders;
        }

        // Check for positions that are losing money or dropping
        Order order = panicSell(model, bot);
        if (order != null) {
            orders.add(order);
            return orders;
        }

        // Very rarely buy (panic sellers are cautious)
        if (random.nextDouble() < buyProbability) {
            order = cautiousBuy(model, bot);
            if (order != null) {
                orders.add(order);
            }
        }

        return orders;
    }

    private Order panicSell(StockSim model, Bot bot) {
        HashMap<String, Integer> holdings = new HashMap<>(bot.getPortfolio().getStockHoldings());
        if (holdings.isEmpty()) {
            return null;
        }

        List<String> panicSymbols = new ArrayList<>();
        for (String symbol : holdings.keySet()) {
            InstrumentDTO stock = model.getStocks().get(symbol);
            if (stock == null)
                continue;

            if (hasPriceDropped(stock, lookbackPeriod, panicThreshold) || isLosingMoney(bot, symbol)) {
                panicSymbols.add(symbol);
            }
        }

        if (panicSymbols.isEmpty()) {
            return null;
        }

        String symbol = panicSymbols.get(random.nextInt(panicSymbols.size()));
        InstrumentDTO stock = model.getStocks().get(symbol);
        if (stock == null)
            return null;

        int quantity = holdings.get(symbol); // Sell ALL shares
        BigDecimal price = stock.getPrice()
                .multiply(BigDecimal.valueOf(0.96 + random.nextDouble() * 0.02)) // 2-4% discount
                .setScale(2, java.math.RoundingMode.HALF_UP);

        return createSellOrder(model, bot, symbol, quantity, price);
    }

    private boolean isLosingMoney(Bot bot, String symbol) {
        // This is a simplified check - we could enhance with current price from model
        // For now, we'll rely on hasPriceDropped in the calling code
        return false;
    }

    private Order cautiousBuy(StockSim model, Bot bot) {
        HashMap<String, InstrumentDTO> stocks = model.getStocks();
        if (stocks == null || stocks.isEmpty()) {
            return null;
        }

        List<InstrumentDTO> safeStocks = new ArrayList<>();
        for (InstrumentDTO stock : stocks.values()) {
            if (!hasPriceDropped(stock, lookbackPeriod, panicThreshold)) {
                safeStocks.add(stock);
            }
        }

        if (safeStocks.isEmpty()) {
            return null;
        }

        InstrumentDTO stock = safeStocks.get(random.nextInt(safeStocks.size()));
        int quantity = randomQuantity();
        BigDecimal price = calculatePriceWithVariation(stock.getPrice(), 0.005);

        return createBuyOrder(model, bot, stock, quantity, price);
    }
}
