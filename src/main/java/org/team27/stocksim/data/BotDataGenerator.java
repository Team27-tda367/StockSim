package org.team27.stocksim.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BotDataGenerator {

    private final int botCount;
    private final List<String> strategies;
    private final int quantityMin;
    private final int quantityMax;
    private final double costBasisMin;
    private final double costBasisMax;

    private final Random rnd = new Random();

    public BotDataGenerator(int botCount,
            List<String> strategies,
            int quantityMin,
            int quantityMax,
            double costBasisMin,
            double costBasisMax) {
        this.botCount = botCount;
        this.strategies = strategies;
        this.quantityMin = quantityMin;
        this.quantityMax = quantityMax;
        this.costBasisMin = costBasisMin;
        this.costBasisMax = costBasisMax;
    }

    public List<BotData> generateBots() {
        List<BotData> bots = new ArrayList<>(botCount);

        for (int i = 1; i <= botCount; i++) {
            String id = "bot" + i;
            String name = "Bot " + i;

            // round-robin (enkel + reproducerbar). Byt till random om du vill.
            String strategy = strategies.get((i - 1) % strategies.size());

            List<PositionData> positions = new ArrayList<>(3); // default 3 (kan göras config om du vill)
            for (int p = 0; p < 3; p++) {
                String symbol = pickSymbol(); // enkel stub (byt till lista i config om ni vill)
                int quantity = randInt(quantityMin, quantityMax);
                double costBasis = round2(randDouble(costBasisMin, costBasisMax));
                positions.add(new PositionData(symbol, quantity, costBasis));
            }

            BotData botData = new BotData(id, name, strategy, positions);
            bots.add(botData);
        }

        return bots;
    }

    // ---- Helpers ----

    private String pickSymbol() {
        // Minimal default. Vill du styra via config: skicka in symbol-lista i
        // konstruktorn.
        String[] symbols = { "AAPL", "MSFT", "TSLA", "NVDA", "AMD", "META", "CSCO", "BA", "KO", "NFLX", "INTC", "NKE",
                "PYPL", "V" };
        return symbols[rnd.nextInt(symbols.length)];
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
