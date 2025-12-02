package org.team27.stocksim.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.team27.stocksim.model.users.Bot;
import org.team27.stocksim.model.users.BotFactory;
import org.team27.stocksim.model.users.Trader;
import org.team27.stocksim.model.users.bot.BotStrategy;
import org.team27.stocksim.model.users.bot.RandomStrategy;
import org.team27.stocksim.model.portfolio.Portfolio;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BotTest {

    private Bot bot;
    private BotFactory botFactory;

    @BeforeEach
    void setUp() {
        bot = new Bot("BOT1", "Bot 1", new Portfolio(new BigDecimal(10000)));
        botFactory = new BotFactory();
    }

    @Test
    void testBotCreation() {
        assertNotNull(bot, "Bot should be created successfully");
    }

    @Test
    void testBotIsTrader() {
        assertTrue(bot instanceof Trader, "Bot should be an instance of Trader");
    }

    @Test
    void testBotFactory() {
        Trader createdBot = botFactory.createTrader("BOT2", "Bot 2", new Portfolio(new BigDecimal(10000)));
        assertNotNull(createdBot, "BotFactory should create a bot");
        assertTrue(createdBot instanceof Bot, "BotFactory should create a Bot instance");
    }

    @Test
    void testMultipleBotCreation() {
        Bot bot1 = new Bot("BOT1", "Bot 1", new Portfolio(new BigDecimal(10000)));
        Bot bot2 = new Bot("BOT2", "Bot 2", new Portfolio(new BigDecimal(10000)));
        Bot bot3 = new Bot("BOT3", "Bot 3", new Portfolio(new BigDecimal(10000)));

        assertNotNull(bot1);
        assertNotNull(bot2);
        assertNotNull(bot3);
    }

    @Test
    void testBotWithDifferentIds() {
        Bot botA = new Bot("ALPHA", "Alpha Bot", new Portfolio(new BigDecimal(10000)));
        Bot botB = new Bot("BETA", "Beta Bot", new Portfolio(new BigDecimal(10000)));
        Bot botC = new Bot("GAMMA", "Gamma Bot", new Portfolio(new BigDecimal(10000)));

        assertNotNull(botA);
        assertNotNull(botB);
        assertNotNull(botC);
    }

    @Test
    void testBotWithDefaultStrategy() {
        Bot bot = new Bot("BOT_DEFAULT", "Default Bot", new Portfolio(new BigDecimal(10000)));
        assertEquals(RandomStrategy.class, bot.getStrategy().getClass(), "Bot should have a default strategy");
    }

}
