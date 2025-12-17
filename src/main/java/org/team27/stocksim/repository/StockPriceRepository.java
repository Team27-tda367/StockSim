package org.team27.stocksim.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.team27.stocksim.model.instruments.Instrument;
import org.team27.stocksim.model.instruments.PricePoint;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository for persisting stock price history to JSON.
 * Handles reading and writing price data to stock_prices.json.
 */
public class StockPriceRepository {

    private static final String RESOURCE_PATH = "/db/stock_prices.json";
    private static final String FILE_PATH = "src/main/resources/db/stock_prices.json";
    private final Gson gson;

    public StockPriceRepository() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Save all stock price histories to JSON file.
     * 
     * @param instruments Map of stock symbol to instrument
     */
    public void saveStockPrices(Map<String, Instrument> instruments) {
        try {
            Map<String, StockPriceData> priceData = new HashMap<>();

            for (Map.Entry<String, Instrument> entry : instruments.entrySet()) {
                String symbol = entry.getKey();
                Instrument instrument = entry.getValue();

                StockPriceData data = new StockPriceData();
                data.symbol = symbol;
                data.name = instrument.getName();
                data.currentPrice = instrument.getCurrentPrice();
                data.priceHistory = instrument.getPriceHistory().getPoints();

                priceData.put(symbol, data);
            }

            // Write to file
            try (FileWriter writer = new FileWriter(FILE_PATH)) {
                gson.toJson(priceData, writer);
            }

        } catch (IOException e) {
            System.err.println("Error saving stock prices: " + e.getMessage());
        }
    }

    /**
     * Load stock price histories from JSON file.
     * 
     * @return Map of stock symbol to price data
     */
    public Map<String, StockPriceData> loadStockPrices() {
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
                    return new HashMap<>();
                }
                reader = new InputStreamReader(Files.newInputStream(Paths.get(FILE_PATH)));
            }

            try (InputStreamReader r = reader) {
                Type type = new TypeToken<Map<String, StockPriceData>>() {
                }.getType();
                Map<String, StockPriceData> data = gson.fromJson(r, type);
                return data != null ? data : new HashMap<>();
            }

        } catch (IOException e) {
            System.err.println("Error loading stock prices: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Data class for JSON serialization of stock price information.
     */
    public static class StockPriceData {
        public String symbol;
        public String name;
        public BigDecimal currentPrice;
        public List<PricePoint> priceHistory;
    }
}
