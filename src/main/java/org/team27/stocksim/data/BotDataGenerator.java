package org.team27.stocksim.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BotDataGenerator {

    private final int botCount;
    private final List<String> strategies;
    private final int quantityMin;
    private final int quantityMax;
    private final int balanceMin;
    private final int balanceMax;
    private final double costBasisMin;
    private final double costBasisMax;
    private final List<String> symbols;

    private final Random rnd = new Random();

    public BotDataGenerator(int botCount,
            List<String> strategies,
            int quantityMin,
            int quantityMax,
            int balanceMin,
            int balanceMax,
            double costBasisMin,
            double costBasisMax,
            List<String> symbols) {
        this.botCount = botCount;
        this.strategies = strategies;
        this.quantityMin = quantityMin;
        this.quantityMax = quantityMax;
        this.balanceMin = balanceMin;
        this.balanceMax = balanceMax;
        this.costBasisMin = costBasisMin;
        this.costBasisMax = costBasisMax;
        this.symbols = symbols;
    }

    public List<BotData> generateBots() {
        List<BotData> bots = new ArrayList<>(botCount);

        for (int i = 1; i <= botCount; i++) {
            String id = "bot" + i;
            String name = "Bot " + i;

            // round-robin (enkel + reproducerbar). Byt till random om du vill.
            String strategy = strategies.get((i - 1) % strategies.size());

            int balance = randInt(balanceMin, balanceMax);

            List<PositionData> positions = new ArrayList<>(3); // default 3 (kan göras config om du vill)
            for (int p = 0; p < 3; p++) {
                String symbol = pickSymbol(); // enkel stub (byt till lista i config om ni vill)
                int quantity = randInt(quantityMin, quantityMax);
                double costBasis = round2(randDouble(costBasisMin, costBasisMax));
                positions.add(new PositionData(symbol, quantity, costBasis));
            }

            BotData botData = new BotData(id, name, strategy, positions, balance);
            bots.add(botData);
        }

        return bots;
    }

    // ---- Helpers ----

    private String pickSymbol() {

        return symbols.get(rnd.nextInt(symbols.size()));
    }

    private int randInt(int min, int max) {
        return min + rnd.nextInt(max - min + 1);
    }

    private double randDouble(double min, double max) {
        return min + rnd.nextDouble() * (max - min);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
