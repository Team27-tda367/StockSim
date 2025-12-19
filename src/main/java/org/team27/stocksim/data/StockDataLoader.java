package org.team27.stocksim.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class StockDataLoader {
    private static final String DEFAULT_STOCKS_FILE = "/config/stocks-config.json";
    private final Gson gson;

    public StockDataLoader() {
        this.gson = new Gson();
    }


    public List<StockData> loadDefaultStocks() {
        return loadStocksFromResource(DEFAULT_STOCKS_FILE);
    }

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
