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
 * Loads stock data from JSON resources.
 */
public class StockDataLoader {
    private static final String DEFAULT_STOCKS_FILE = "/data/default-stocks.json";
    private final Gson gson;

    public StockDataLoader() {
        this.gson = new Gson();
    }

    /**
     * Load default stocks from the bundled JSON resource.
     * 
     * @return List of stock data
     * @throws RuntimeException if the file cannot be loaded
     */
    public List<StockData> loadDefaultStocks() {
        return loadStocksFromResource(DEFAULT_STOCKS_FILE);
    }

    /**
     * Load stocks from a specific resource path.
     * 
     * @param resourcePath Path to JSON file in resources folder
     * @return List of stock data
     * @throws RuntimeException if the file cannot be loaded
     */
    public List<StockData> loadStocksFromResource(String resourcePath) {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find resource: " + resourcePath);
            }

            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Type listType = new TypeToken<ArrayList<StockData>>() {
            }.getType();
            List<StockData> stocks = gson.fromJson(reader, listType);

            if (stocks == null || stocks.isEmpty()) {
                throw new RuntimeException("No stocks loaded from: " + resourcePath);
            }

            return stocks;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load stocks from " + resourcePath, e);
        }
    }
}
