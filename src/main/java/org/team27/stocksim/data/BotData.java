package org.team27.stocksim.data;

import java.util.List;

public class BotData {
    private String id;
    private String name;
    private String strategy;
    private int balance;

    private List<PositionData> initialPositions;

    public BotData() {
    }

    public BotData(String id, String name, String strategy, List<PositionData> initialPositions, int balance) {
        this.id = id;
        this.name = name;
        this.strategy = strategy;
        this.initialPositions = initialPositions;
        this.balance = balance;
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

    public List<PositionData> getPositions() {
        return initialPositions;
    }

    public void setPositions(List<PositionData> positions) {
        this.initialPositions = positions;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
