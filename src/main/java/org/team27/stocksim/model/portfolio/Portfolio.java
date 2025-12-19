package org.team27.stocksim.model.portfolio;

import org.team27.stocksim.model.market.Trade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Portfolio {

    private final BigDecimal initialBalance;
    private BigDecimal balance;
    private Map<String, Position> positions; // symbol -> Position

    public Portfolio(BigDecimal traderBalance) {
        this.balance = traderBalance;
        this.initialBalance = traderBalance;
        this.positions = new HashMap<>();
    }

    public synchronized BigDecimal getBalance() {
        return balance;
    }

    public synchronized void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public synchronized boolean withdraw(BigDecimal amount) {
        BigDecimal newBalance = this.balance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
            balance = newBalance;
            return true; // succesfull withdrawal
        }
        return false; // unsuccessfull withdrawal (not enough balance)
    }

    public synchronized void addStock(String symbol, int quantity, BigDecimal price, Trade trade) {
        Position position = positions.computeIfAbsent(symbol, Position::new);
        position.addShares(quantity, price, trade);
    }

    public synchronized void addStock(String symbol, int quantity) {
        addStock(symbol, quantity, BigDecimal.ZERO, null);
    }

    public synchronized boolean removeStock(String symbol, int quantity, Trade trade) {
        Position position = positions.get(symbol);
        if (position == null) {
            return false;
        }

        boolean success = position.removeShares(quantity, trade);
        if (success && position.isEmpty()) {
            positions.remove(symbol);
        }
        return success;
    }

    public synchronized boolean removeStock(String symbol, int quantity) {
        return removeStock(symbol, quantity, null);
    }

    public synchronized int getStockQuantity(String symbol) {
        Position position = positions.get(symbol);
        return position != null ? position.getQuantity() : 0;
    }

    public Position getPosition(String symbol) {
        return positions.get(symbol);
    }

    public Map<String, Position> getPositions() {
        return new HashMap<>(positions);
    }

    public Map<String, Integer> getStockHoldings() {
        return positions.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getQuantity()));
    }

    public boolean isEmpty() {
        return positions.isEmpty();
    }

    public BigDecimal getTotalCost() {
        return positions.values().stream().map(Position::getTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPositionsValue(Map<String, BigDecimal> currentPrices) {
        BigDecimal totalValue = BigDecimal.ZERO;

        for (Map.Entry<String, Position> entry : positions.entrySet()) {
            String symbol = entry.getKey();
            Position position = entry.getValue();
            BigDecimal currentPrice = currentPrices.get(symbol);

            if (currentPrice != null) {
                BigDecimal positionValue = currentPrice.multiply(BigDecimal.valueOf(position.getQuantity()));
                totalValue = totalValue.add(positionValue);
            }
        }

        return totalValue;
    }

    public BigDecimal getTotalValue(Map<String, BigDecimal> currentPrices) {
        BigDecimal positionsValue = getPositionsValue(currentPrices);
        return positionsValue.add(balance);
    }

    public BigDecimal getTotalGainLoss(Map<String, BigDecimal> currentPrices) {
        if (positions.isEmpty()) {
            // No positions: compare current balance to initial balance
            return balance.subtract(initialBalance);
        }
        // With positions: compare current position value to cost basis
        BigDecimal positionsValue = getPositionsValue(currentPrices);
        BigDecimal totalCost = getTotalCost();
        return positionsValue.subtract(totalCost);
    }

    public BigDecimal getGainLossPercentage(Map<String, BigDecimal> currentPrices) {
        BigDecimal costBasis;
        if (positions.isEmpty()) {
            // No positions: use initial balance as cost basis
            costBasis = initialBalance;
        } else {
            // With positions: use total cost of positions
            costBasis = getTotalCost();
        }

        if (costBasis.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalGainLoss = getTotalGainLoss(currentPrices);
        return totalGainLoss.divide(costBasis, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    public synchronized boolean canBuy(String symbol, int quantity, BigDecimal price) {
        BigDecimal totalCost = price.multiply(BigDecimal.valueOf(quantity));
        return balance.compareTo(totalCost) >= 0;
    }

    public synchronized boolean canSell(String symbol, int quantity) {
        return getStockQuantity(symbol) >= quantity;
    }

}
