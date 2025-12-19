package org.team27.stocksim.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PortfolioDTO {

    private final BigDecimal balance;
    private final Map<String, PositionDTO> positions;

    public PortfolioDTO(BigDecimal balance, Map<String, PositionDTO> positions) {
        this.balance = balance;
        this.positions = Collections.unmodifiableMap(positions);
    }

    // Getters only (immutable)
    public BigDecimal getBalance() {
        return balance;
    }

    public Map<String, PositionDTO> getPositions() {
        return positions;
    }

    /**
     * Gets a position for a specific stock.
     *
     * @param symbol the stock symbol
     * @return the Position DTO, or null if not found
     */
    public PositionDTO getPosition(String symbol) {
        return positions.get(symbol);
    }

    /**
     * Calculates the current market value of all positions based on current prices.
     *
     * @param currentPrices map of symbol to current price
     * @return total market value of all positions (excluding cash)
     */
    public BigDecimal getPositionsValue(Map<String, BigDecimal> currentPrices) {
        BigDecimal totalValue = BigDecimal.ZERO;

        for (Map.Entry<String, PositionDTO> entry : positions.entrySet()) {
            String symbol = entry.getKey();
            PositionDTO position = entry.getValue();
            BigDecimal currentPrice = currentPrices.get(symbol);

            if (currentPrice != null) {
                BigDecimal positionValue = currentPrice.multiply(BigDecimal.valueOf(position.getQuantity()));
                totalValue = totalValue.add(positionValue);
            }
        }

        return totalValue;
    }

    /**
     * Calculates the total portfolio value including cash balance and positions.
     *
     * @param currentPrices map of symbol to current price
     * @return total portfolio value (positions + cash)
     */
    public BigDecimal getTotalValue(Map<String, BigDecimal> currentPrices) {
        BigDecimal positionsValue = getPositionsValue(currentPrices);
        return positionsValue.add(balance);
    }

    /**
     * Calculates the total cost basis of all positions in the portfolio.
     *
     * @return total cost of all positions
     */
    public BigDecimal getTotalCost() {
        return positions.values().stream()
                .map(position -> position.getAverageCost().multiply(BigDecimal.valueOf(position.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the unrealized gain/loss on all positions.
     *
     * @param currentPrices map of symbol to current price
     * @return total unrealized profit/loss
     */
    public BigDecimal getTotalGainLoss(Map<String, BigDecimal> currentPrices) {
        if (positions.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal positionsValue = getPositionsValue(currentPrices);
        BigDecimal totalCost = getTotalCost();
        return positionsValue.subtract(totalCost);
    }

    /**
     * Calculates the unrealized gain/loss percentage on all positions.
     *
     * @param currentPrices map of symbol to current price
     * @return gain/loss as a percentage, or ZERO if no cost basis
     */
    public BigDecimal getGainLossPercentage(Map<String, BigDecimal> currentPrices) {
        BigDecimal costBasis = getTotalCost();

        if (costBasis.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalGainLoss = getTotalGainLoss(currentPrices);
        return totalGainLoss.divide(costBasis, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }
}
