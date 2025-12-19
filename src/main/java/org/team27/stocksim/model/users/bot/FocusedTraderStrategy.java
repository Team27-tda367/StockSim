package org.team27.stocksim.model.users.bot;

import org.team27.stocksim.dto.InstrumentDTO;
import org.team27.stocksim.model.StockSim;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.market.Order;

import java.math.BigDecimal;
import java.util.*;

/**
 * Focused Trader Strategy - Only trades specific favorite stocks
 * This bot specializes in a limited set of stocks:
 * - Only trades stocks from their watchlist
 * - Knows these stocks well
 * - More active with their favorites
 */
public class FocusedTraderStrategy extends AbstractBotStrategy {

    private final Set<String> watchlist;
    private final double tradeProbability;

    public FocusedTraderStrategy() {
        this(new Random(), generateRandomWatchlist(new Random()), 0.08, 1, 15);
    }

    public FocusedTraderStrategy(Random random, Set<String> watchlist, double tradeProbability,
            int minQuantity, int maxQuantity) {
        super(random, minQuantity, maxQuantity);
        this.watchlist = new HashSet<>(watchlist);
        this.tradeProbability = tradeProbability;
    }

    private static Set<String> generateRandomWatchlist(Random random) {
        // Common stock symbols - focused traders pick 3-6 favorites
        List<String> allStocks = Arrays.asList(
                "AAPL", "GOOGL", "MSFT", "TSLA", "AMZN", "META", "NVDA",
                "JPM", "V", "WMT", "DIS", "NFLX", "PYPL", "INTC", "AMD");

        Set<String> watchlist = new HashSet<>();
        int count = 3 + random.nextInt(4); // 3-6 stocks

        for (int i = 0; i < count && !allStocks.isEmpty(); i++) {
            String stock = allStocks.get(random.nextInt(allStocks.size()));
            watchlist.add(stock);
            allStocks = new ArrayList<>(allStocks);
            allStocks.remove(stock);
        }

        return watchlist;
    }

    @Override
    public List<Order> decide(StockSim model, Bot bot) {
        List<Order> orders = new ArrayList<>();
        if (random.nextDouble() > tradeProbability) {
            return orders;
        }

        // Get available stocks from watchlist
        List<InstrumentDTO> availableWatchlist = getAvailableWatchlistStocks(model);
        if (availableWatchlist.isEmpty()) {
            return orders; // No favorite stocks available
        }

        Order order = null;
        // Randomly buy or sell
        if (random.nextDouble() < 0.55) { // Slight preference to buy
            order = buy(model, bot, availableWatchlist);
        } else if (!bot.getPortfolio().isEmpty()) {
            order = sell(model, bot);
        }

        if (order != null) {
            orders.add(order);
        }

        return orders;
    }

    private List<InstrumentDTO> getAvailableWatchlistStocks(StockSim model) {
        List<InstrumentDTO> available = new ArrayList<>();
        HashMap<String, InstrumentDTO> allStocks = model.getStocks();

        if (allStocks == null) {
            return available;
        }

        for (String symbol : watchlist) {
            InstrumentDTO stock = allStocks.get(symbol);
            if (stock != null) {
                available.add(stock);
            }
        }

        return available;
    }

    private Order buy(StockSim model, Bot bot, List<InstrumentDTO> availableWatchlist) {
        if (availableWatchlist.isEmpty()) {
            return null;
        }

        InstrumentDTO stock = availableWatchlist.get(random.nextInt(availableWatchlist.size()));
        int quantity = randomQuantity();
        BigDecimal price = calculatePriceWithVariation(stock.getPrice(), 1.5);

        return createBuyOrder(model, bot, stock, quantity, price);
    }

    private Order sell(StockSim model, Bot bot) {
        HashMap<String, Integer> holdings = new HashMap<>(bot.getPortfolio().getStockHoldings());
        if (holdings.isEmpty()) {
            return null;
        }

        List<String> watchlistHoldings = new ArrayList<>();
        for (String symbol : holdings.keySet()) {
            if (watchlist.contains(symbol)) {
                watchlistHoldings.add(symbol);
            }
        }

        if (watchlistHoldings.isEmpty()) {
            watchlistHoldings.addAll(holdings.keySet());
        }

        String symbol = watchlistHoldings.get(random.nextInt(watchlistHoldings.size()));
        InstrumentDTO stock = model.getStocks().get(symbol);
        if (stock == null) {
            return null;
        }

        int maxAvailable = holdings.get(symbol);
        int quantity = Math.min(randomQuantity(), maxAvailable);
        BigDecimal price = calculatePriceWithVariation(stock.getPrice(), 1.0);

        return createSellOrder(model, bot, symbol, quantity, price);
    }

    public Set<String> getWatchlist() {
        return new HashSet<>(watchlist);
    }
}
