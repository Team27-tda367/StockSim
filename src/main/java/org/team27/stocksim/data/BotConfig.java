package org.team27.stocksim.data;

import java.util.List;

public class BotConfig {

    private int botCount;
    private List<String> strategies;
    private int positionsPerBot;
    private int quantityMin;
    private int quantityMax;
    private int balanceMin;
    private int balanceMax;
    private double costBasisMin;
    private double costBasisMax;

    // ---- getters & setters ----

    public int getBotCount() {
        return botCount;
    }

    public void setBotCount(int botCount) {
        this.botCount = botCount;
    }

    public List<String> getStrategies() {
        return strategies;
    }

    public void setStrategies(List<String> strategies) {
        this.strategies = strategies;
    }

    public int getPositionsPerBot() {
        return positionsPerBot;
    }

    public void setPositionsPerBot(int positionsPerBot) {
        this.positionsPerBot = positionsPerBot;
    }

    public int getQuantityMin() {
        return quantityMin;
    }

    public void setQuantityMin(int quantityMin) {
        this.quantityMin = quantityMin;
    }

    public int getQuantityMax() {
        return quantityMax;
    }

    public void setQuantityMax(int quantityMax) {
        this.quantityMax = quantityMax;
    }

    public double getCostBasisMin() {
        return costBasisMin;
    }

    public void setCostBasisMin(double costBasisMin) {
        this.costBasisMin = costBasisMin;
    }

    public double getCostBasisMax() {
        return costBasisMax;
    }

    public void setCostBasisMax(double costBasisMax) {
        this.costBasisMax = costBasisMax;
    }

    public int getBalanceMin() {
        return balanceMin;
    }

    public void setBalanceMin(int balanceMin) {
        this.balanceMin = balanceMin;
    }

    public int getBalanceMax() {
        return balanceMax;
    }

    public void setBalanceMax(int balanceMax) {
        this.balanceMax = balanceMax;
    }
}
