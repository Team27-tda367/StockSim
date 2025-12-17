package org.team27.stocksim.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.team27.stocksim.data.BotData;
import org.team27.stocksim.model.portfolio.Portfolio;
import org.team27.stocksim.model.portfolio.Position;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.users.Trader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Repository for persisting bot positions to JSON.
 * Handles reading and writing bot position data to bot-positions.json.
 */
public class BotPositionRepository {

    private static final String RESOURCE_PATH = "/db/bot-positions.json";
    private static final String FILE_PATH = "src/main/resources/db/bot-positions.json";
    private final Gson gson;

    public BotPositionRepository() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Save all bot positions to JSON file.
     * 
     * @param traders Map of trader ID to trader object
     */
    public void saveBotPositions(Map<String, Trader> traders) {
        try {
            List<BotData> botDataList = new ArrayList<>();

            for (Map.Entry<String, Trader> entry : traders.entrySet()) {
                Trader trader = entry.getValue();

                // Only save Bot positions, not human users
                if (trader instanceof Bot) {
                    Bot bot = (Bot) trader;

                    BotData botData = new BotData();
                    botData.setId(bot.getId());
                    botData.setName(bot.getDisplayName());
                    botData.setStrategy(bot.getStrategy().getClass().getSimpleName());

                    // Convert portfolio positions to PositionData
                    List<BotData.PositionData> positions = new ArrayList<>();
                    Portfolio portfolio = bot.getPortfolio();

                    for (Map.Entry<String, Integer> holding : portfolio.getStockHoldings().entrySet()) {
                        String symbol = holding.getKey();
                        int quantity = holding.getValue();
                        Position position = portfolio.getPosition(symbol);

                        if (position != null && quantity > 0) {
                            BotData.PositionData posData = new BotData.PositionData();
                            posData.setSymbol(symbol);
                            posData.setQuantity(quantity);
                            posData.setCostBasis(position.getAverageCost().doubleValue());
                            positions.add(posData);
                        }
                    }

                    botData.setInitialPositions(positions);
                    botDataList.add(botData);
                }
            }

            // Write to file
            try (FileWriter writer = new FileWriter(FILE_PATH)) {
                gson.toJson(botDataList, writer);
            }

        } catch (IOException e) {
            System.err.println("Error saving bot positions: " + e.getMessage());
        }
    }

    /**
     * Load bot positions from JSON file.
     * 
     * @return List of bot data, or null if file doesn't exist or is empty
     */
    public List<BotData> loadBotPositions() {
        try {
            // Try loading from classpath first (for JAR execution)
            InputStreamReader reader = null;
            try {
                var stream = getClass().getResourceAsStream(RESOURCE_PATH);
                if (stream != null) {
                    reader = new InputStreamReader(stream);
                }
            } catch (Exception e) {
                // Fall back to file system
            }

            // Fall back to file system (for development)
            if (reader == null) {
                if (!Files.exists(Paths.get(FILE_PATH))) {
                    return null;
                }
                reader = new InputStreamReader(Files.newInputStream(Paths.get(FILE_PATH)));
            }

            try (InputStreamReader r = reader) {
                Type type = new TypeToken<List<BotData>>() {
                }.getType();
                List<BotData> data = gson.fromJson(r, type);
                return (data == null || data.isEmpty()) ? null : data;
            }

        } catch (IOException e) {
            System.err.println("Error loading bot positions: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if saved bot positions exist.
     * 
     * @return true if bot-positions.json exists and is not empty
     */
    public boolean hasSavedPositions() {
        try {
            if (!Files.exists(Paths.get(FILE_PATH))) {
                return false;
            }

            List<BotData> data = loadBotPositions();
            return data != null && !data.isEmpty();

        } catch (Exception e) {
            return false;
        }
    }
}
