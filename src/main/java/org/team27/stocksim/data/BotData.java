package org.team27.stocksim.data;

import java.util.List;

public class BotData {
    private String id;
    private String name;
    private String strategy;
    private List<PositionData> initialPositions;

    // Default constructor for JSON deserialization
    public BotData() {
    }

    public BotData(String id, String name, String strategy, List<PositionData> initialPositions) {
        this.id = id;
        this.name = name;
        this.strategy = strategy;
        this.initialPositions = initialPositions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public List<PositionData> getInitialPositions() {
        return initialPositions;
    }

    public void setInitialPositions(List<PositionData> initialPositions) {
        this.initialPositions = initialPositions;
    }

    public static class PositionData {
        private String symbol;
        private int quantity;
        private String costBasis;

        // Default constructor for JSON deserialization
        public PositionData() {
        }

        public PositionData(String symbol, int quantity, String costBasis) {
            this.symbol = symbol;
            this.quantity = quantity;
            this.costBasis = costBasis;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getCostBasis() {
            return costBasis;
        }

        public void setCostBasis(String costBasis) {
            this.costBasis = costBasis;
        }
    }
}
