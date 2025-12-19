package org.team27.stocksim.data;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class BotDataLoader {
    private static final String CONFIG_BOT_FILE = "/config/bot-config.json";
    private static final String CONFIG_STOCK_FILE = "/config/stocks-config.json";
    private final Gson gson;

    public BotDataLoader() {
        this.gson = new Gson();
    }


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
            StockDataLoader stockLoader = new StockDataLoader();
            List<StockData> stocks = stockLoader.loadStocksFromResource(CONFIG_STOCK_FILE);

            BotDataGenerator generator = new BotDataGenerator(
                    config.getBotCount(),
                    config.getStrategies(),
                    config.getQuantityMin(),
                    config.getQuantityMax(),
                    config.getBalanceMin(),
                    config.getBalanceMax(),
                    config.getCostBasisMin(),
                    config.getCostBasisMax(),
                    stocks.stream().map(StockData::getSymbol).toList());

            return generator.generateBots();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bots from config " + resourcePath, e);
        }
    }

}
