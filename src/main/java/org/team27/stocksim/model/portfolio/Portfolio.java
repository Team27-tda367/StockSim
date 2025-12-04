package org.team27.stocksim.model.portfolio;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.team27.stocksim.model.instruments.Instrument;

/**
 * Represents a trader's portfolio containing cash balance and stock holdings.
 * This is a pure domain object - StockSim handles notifications when portfolio
 * changes.
 */
public class Portfolio {

    List<Instrument> instruments;
    private BigDecimal balance;
    private Map<String, Integer> stockHoldings; // symbol -> quantity

    public Portfolio(BigDecimal traderBalance) {
        this.balance = traderBalance;
        this.stockHoldings = new HashMap<>();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public boolean withdraw(BigDecimal amount) {
        BigDecimal newBalance = this.balance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
            balance = newBalance;
            return true; // succesfull withdrawal
        }
        return false; // unsuccessfull withdrawal (not enough balance)
    }

    public void addStock(String symbol, int quantity) {
        stockHoldings.put(symbol, stockHoldings.getOrDefault(symbol, 0) + quantity);
    }

    public boolean removeStock(String symbol, int quantity) {
        int currentQuantity = stockHoldings.getOrDefault(symbol, 0);
        if (currentQuantity >= quantity) {
            int newQuantity = currentQuantity - quantity;
            if (newQuantity == 0) {
                stockHoldings.remove(symbol);
            } else {
                stockHoldings.put(symbol, newQuantity);
            }
            return true;
        }
        return false;
    }

    public int getStockQuantity(String symbol) {
        return stockHoldings.getOrDefault(symbol, 0);
    }

    public Map<String, Integer> getStockHoldings() {
        return new HashMap<>(stockHoldings);
    }

    public boolean isEmpty() {
        return stockHoldings.isEmpty();
    }

    public List<Instrument> getInstruments() {
        return instruments;
    }

}
