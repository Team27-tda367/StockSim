package org.team27.stocksim.model.users.bot;

import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.util.dto.InstrumentDTO;
import org.team27.stocksim.model.market.Order;
import org.team27.stocksim.model.portfolio.Position;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Abstract base class for bot trading strategies
 * Provides common functionality to reduce code duplication:
 * - Random number generation
 * - Stock selection helpers
 * - Order placement utilities
 * - Portfolio analysis methods
 * - Price calculation utilities
 */
public abstract class AbstractBotStrategy implements IBotStrategy {

    protected final Random random;
    protected final int minQuantity;
    protected final int maxQuantity;

    protected AbstractBotStrategy(Random random, int minQuantity, int maxQuantity) {
        this.random = random;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Generate a random quantity within the configured range
     */
    protected int randomQuantity() {
        if (maxQuantity <= minQuantity) {
            return minQuantity;
        }
        return minQuantity + random.nextInt(maxQuantity - minQuantity + 1);
    }

    /**
     * Calculate a price with random variation around the base price
     * 
     * @param basePrice        The starting price
     * @param variationPercent Max variation as decimal (e.g., 0.01 = 1%)
     * @return Price with random variation
     */
    protected BigDecimal calculatePriceWithVariation(BigDecimal basePrice, double variationPercent) {
        double variation = (random.nextDouble() - 0.5) * 2 * variationPercent;
        return basePrice.multiply(BigDecimal.valueOf(1.0 + variation))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Pick a random stock from the market
     */
    protected InstrumentDTO pickRandomStock(StockSim model) {
        HashMap<String, InstrumentDTO> stocks = model.getStocks();
        if (stocks == null || stocks.isEmpty()) {
            return null;
        }
        List<InstrumentDTO> stockList = new ArrayList<>(stocks.values());
        return stockList.get(random.nextInt(stockList.size()));
    }

    /**
     * Pick a random stock symbol from bot's holdings
     */
    protected String pickRandomHolding(Bot bot) {
        HashMap<String, Integer> holdings = new HashMap<>(bot.getPortfolio().getStockHoldings());
        if (holdings.isEmpty()) {
            return null;
        }
        List<String> symbols = new ArrayList<>(holdings.keySet());
        return symbols.get(random.nextInt(symbols.size()));
    }

    // ==================== ORDER CREATION ====================

    /**
     * Create a buy order for a stock
     * 
     * @return Order if it can be placed, null otherwise
     */
    protected Order createBuyOrder(StockSim model, Bot bot, InstrumentDTO stock, int quantity, BigDecimal price) {
        BigDecimal cost = price.multiply(BigDecimal.valueOf(quantity));
        if (bot.getPortfolio().getBalance().compareTo(cost) < 0) {
            return null; // Insufficient funds
        }

        return new Order(Order.Side.BUY, stock.getSymbol(), price, quantity, bot.getId());
    }

    /**
     * Create a sell order for a stock
     * 
     * @return Order if it can be placed, null otherwise
     */
    protected Order createSellOrder(StockSim model, Bot bot, String symbol, int quantity, BigDecimal price) {
        int available = bot.getPortfolio().getStockQuantity(symbol);
        if (available < quantity) {
            return null; // Insufficient shares
        }

        InstrumentDTO stock = model.getStocks().get(symbol);
        if (stock == null) {
            return null;
        }

        return new Order(Order.Side.SELL, symbol, price, quantity, bot.getId());
    }

    // ==================== PORTFOLIO ANALYSIS ====================

    /**
     * Calculate profit/loss percentage for a position
     */
    protected BigDecimal calculateProfitPercent(Bot bot, String symbol, BigDecimal currentPrice) {
        Position position = bot.getPortfolio().getPosition(symbol);
        if (position == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal avgCost = position.getAverageCost();
        if (avgCost.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return currentPrice.subtract(avgCost)
                .divide(avgCost, 4, RoundingMode.HALF_UP);
    }

    /**
     * Find all holdings that meet a profit threshold
     * 
     * @param profitThreshold Minimum profit percentage (e.g., 0.10 for 10%)
     * @return List of symbols meeting the threshold
     */
    protected List<String> findProfitableHoldings(StockSim model, Bot bot, double profitThreshold) {
        List<String> profitable = new ArrayList<>();
        HashMap<String, Integer> holdings = new HashMap<>(bot.getPortfolio().getStockHoldings());

        for (String symbol : holdings.keySet()) {
            InstrumentDTO stock = model.getStocks().get(symbol);
            if (stock == null)
                continue;

            BigDecimal profitPercent = calculateProfitPercent(bot, symbol, stock.getPrice());
            if (profitPercent.compareTo(BigDecimal.valueOf(profitThreshold)) > 0) {
                profitable.add(symbol);
            }
        }

        return profitable;
    }

    /**
     * Find all holdings with losses exceeding a threshold
     */
    protected List<String> findLosingHoldings(StockSim model, Bot bot, double lossThreshold) {
        List<String> losing = new ArrayList<>();
        HashMap<String, Integer> holdings = new HashMap<>(bot.getPortfolio().getStockHoldings());

        for (String symbol : holdings.keySet()) {
            InstrumentDTO stock = model.getStocks().get(symbol);
            if (stock == null)
                continue;

            BigDecimal profitPercent = calculateProfitPercent(bot, symbol, stock.getPrice());
            if (profitPercent.compareTo(BigDecimal.valueOf(-lossThreshold)) < 0) {
                losing.add(symbol);
            }
        }

        return losing;
    }

    // ==================== PRICE HISTORY ANALYSIS ====================

    /**
     * Check if stock price has dropped recently
     */
    protected boolean hasPriceDropped(InstrumentDTO stock, int lookbackPeriod, double dropThreshold) {
        List<BigDecimal> recentPrices = getRecentPrices(stock, lookbackPeriod);
        if (recentPrices.size() < 2) {
            return false;
        }

        BigDecimal oldPrice = recentPrices.get(0);
        BigDecimal currentPrice = stock.getPrice();

        if (oldPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        BigDecimal dropPercent = oldPrice.subtract(currentPrice)
                .divide(oldPrice, 4, RoundingMode.HALF_UP);

        return dropPercent.compareTo(BigDecimal.valueOf(dropThreshold)) > 0;
    }

    /**
     * Check if stock price has risen recently
     */
    protected boolean hasPriceRisen(InstrumentDTO stock, int lookbackPeriod, double riseThreshold) {
        List<BigDecimal> recentPrices = getRecentPrices(stock, lookbackPeriod);
        if (recentPrices.size() < 2) {
            return false;
        }

        BigDecimal oldPrice = recentPrices.get(0);
        BigDecimal currentPrice = stock.getPrice();

        if (oldPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        BigDecimal risePercent = currentPrice.subtract(oldPrice)
                .divide(oldPrice, 4, RoundingMode.HALF_UP);

        return risePercent.compareTo(BigDecimal.valueOf(riseThreshold)) > 0;
    }

    /**
     * Get recent price history
     */
    protected List<BigDecimal> getRecentPrices(InstrumentDTO stock, int lookbackPeriod) {
        var history = stock.getPriceHistory().getPoints();
        if (history.isEmpty()) {
            return new ArrayList<>();
        }

        int startIdx = Math.max(0, history.size() - lookbackPeriod);
        return history.subList(startIdx, history.size()).stream()
                .map(pp -> pp.getPrice())
                .toList();
    }

    /**
     * Find the highest price in recent history
     */
    protected BigDecimal findRecentHigh(InstrumentDTO stock, int lookbackPeriod) {
        List<BigDecimal> prices = getRecentPrices(stock, lookbackPeriod);
        return prices.stream()
                .max(BigDecimal::compareTo)
                .orElse(stock.getPrice());
    }
}
