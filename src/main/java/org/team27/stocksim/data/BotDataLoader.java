package org.team27.stocksim.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads bot data from JSON resources.
 */
public class BotDataLoader {
    private static final String DEFAULT_BOTS_FILE = "/data/default-bots.json";
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
        return loadBotsFromResource(DEFAULT_BOTS_FILE);
    }

    /**
     * Load bots from a specific resource path.
     * 
     * @param resourcePath Path to JSON file in resources folder
     * @return List of bot data
     * @throws RuntimeException if the file cannot be loaded
     */
    public List<BotData> loadBotsFromResource(String resourcePath) {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find resource: " + resourcePath);
            }

            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Type listType = new TypeToken<ArrayList<BotData>>() {
            }.getType();
            List<BotData> bots = gson.fromJson(reader, listType);

            if (bots == null || bots.isEmpty()) {
                throw new RuntimeException("No bots loaded from: " + resourcePath);
            }

            return bots;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load bots from " + resourcePath, e);
        }
    }
}
