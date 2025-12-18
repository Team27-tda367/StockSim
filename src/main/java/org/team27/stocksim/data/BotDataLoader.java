package org.team27.stocksim.data;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Loads bot data from JSON resources.
 */
public class BotDataLoader {
    private static final String CONFIG_BOT_FILE = "/config/bot-config.json";
    private final Gson gson;

    public BotDataLoader() {
        this.gson = new Gson();
    }

    /**
     * Load default bots from the bundled JSON resource.
     * 
     * @return List of bot data
     * @throws RuntimeException if the file cannot be loaded
     */
    public List<BotData> loadDefaultBots() {
        // Call script and load from config file, then return loaded bots

        return createBotsFromConfigFile(CONFIG_BOT_FILE);
    }

    private List<BotData> createBotsFromConfigFile(String resourcePath) {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find resource: " + resourcePath);
            }

            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BotConfig config = gson.fromJson(reader, BotConfig.class);

            BotDataGenerator generator = new BotDataGenerator(
                    config.getBotCount(),
                    config.getStrategies(),
                    config.getQuantityMin(),
                    config.getQuantityMax(),
                    config.getCostBasisMin(),
                    config.getCostBasisMax());

            return generator.generateBots();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bots from config " + resourcePath, e);
        }
    }

}
