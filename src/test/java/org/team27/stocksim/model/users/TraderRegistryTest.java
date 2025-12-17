package org.team27.stocksim.model.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.team27.stocksim.model.portfolio.Portfolio;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TraderRegistry Tests")
class TraderRegistryTest {

    private TraderRegistry registry;
    private UserFactory userFactory;
    private BotFactory botFactory;

    @BeforeEach
    void setUp() {
        userFactory = new UserFactory();
        botFactory = new BotFactory();
        registry = new TraderRegistry(userFactory, botFactory);
    }

    @Test
    @DisplayName("Should create empty registry")
    void testCreateEmptyRegistry() {
        assertNotNull(registry);
        assertTrue(registry.getAllTraders().isEmpty());
        assertTrue(registry.getUsers().isEmpty());
        assertTrue(registry.getBots().isEmpty());
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser() {
        boolean result = registry.createUser("USER001", "Test User");

        assertTrue(result);
        assertNotNull(registry.getTrader("USER001"));
    }

    @Test
    @DisplayName("Should create bot successfully")
    void testCreateBot() {
        boolean result = registry.createBot("BOT001", "Test Bot");

        assertTrue(result);
        assertNotNull(registry.getTrader("BOT001"));
    }

    @Test
    @DisplayName("Should not create duplicate user")
    void testPreventDuplicateUser() {
        boolean first = registry.createUser("USER001", "Test User");
        boolean second = registry.createUser("USER001", "Another User");

        assertTrue(first);
        assertFalse(second);
    }

    @Test
    @DisplayName("Should not create duplicate bot")
    void testPreventDuplicateBot() {
        boolean first = registry.createBot("BOT001", "Test Bot");
        boolean second = registry.createBot("BOT001", "Another Bot");

        assertTrue(first);
        assertFalse(second);
    }

    @Test
    @DisplayName("Should handle case-insensitive IDs for users")
    void testCaseInsensitiveUserIds() {
        registry.createUser("user001", "Test User");

        assertNotNull(registry.getTrader("USER001"));
        assertNotNull(registry.getTrader("user001"));
    }

    @Test
    @DisplayName("Should handle case-insensitive IDs for bots")
    void testCaseInsensitiveBotIds() {
        registry.createBot("bot001", "Test Bot");

        assertNotNull(registry.getTrader("BOT001"));
        assertNotNull(registry.getTrader("bot001"));
    }

    @Test
    @DisplayName("Should prevent duplicate with different case")
    void testPreventDuplicateDifferentCase() {
        boolean first = registry.createUser("USER001", "Test User");
        boolean second = registry.createUser("user001", "Another User");

        assertTrue(first);
        assertFalse(second);
    }

    @Test
    @DisplayName("Should get all traders")
    void testGetAllTraders() {
        registry.createUser("USER001", "User One");
        registry.createUser("USER002", "User Two");
        registry.createBot("BOT001", "Bot One");
        registry.createBot("BOT002", "Bot Two");

        HashMap<String, Trader> allTraders = registry.getAllTraders();
        assertEquals(4, allTraders.size());
    }

    @Test
    @DisplayName("Should get only users")
    void testGetOnlyUsers() {
        registry.createUser("USER001", "User One");
        registry.createUser("USER002", "User Two");
        registry.createBot("BOT001", "Bot One");

        HashMap<String, User> users = registry.getUsers();
        assertEquals(2, users.size());
        assertTrue(users.containsKey("USER001"));
        assertTrue(users.containsKey("USER002"));
        assertFalse(users.containsKey("BOT001"));
    }

    @Test
    @DisplayName("Should get only bots")
    void testGetOnlyBots() {
        registry.createUser("USER001", "User One");
        registry.createBot("BOT001", "Bot One");
        registry.createBot("BOT002", "Bot Two");

        HashMap<String, Bot> bots = registry.getBots();
        assertEquals(2, bots.size());
        assertTrue(bots.containsKey("BOT001"));
        assertTrue(bots.containsKey("BOT002"));
        assertFalse(bots.containsKey("USER001"));
    }

    @Test
    @DisplayName("Should retrieve trader by ID")
    void testGetTraderById() {
        registry.createUser("USER001", "Test User");

        Trader trader = registry.getTrader("USER001");
        assertNotNull(trader);
        assertEquals("USER001", trader.getId());
        assertEquals("Test User", trader.getDisplayName());
    }

    @Test
    @DisplayName("Should return null for non-existent trader")
    void testGetNonExistentTrader() {
        Trader trader = registry.getTrader("NONEXISTENT");
        assertNull(trader);
    }

    @Test
    @DisplayName("Should distinguish between users and bots")
    void testDistinguishUsersAndBots() {
        registry.createUser("USER001", "Test User");
        registry.createBot("BOT001", "Test Bot");

        Trader user = registry.getTrader("USER001");
        Trader bot = registry.getTrader("BOT001");

        assertTrue(user instanceof User);
        assertFalse(user instanceof Bot);
        assertTrue(bot instanceof Bot);
        assertFalse(bot instanceof User);
    }

    @Test
    @DisplayName("Should create traders with portfolios")
    void testTradersHavePortfolios() {
        registry.createUser("USER001", "Test User");
        registry.createBot("BOT001", "Test Bot");

        Trader user = registry.getTrader("USER001");
        Trader bot = registry.getTrader("BOT001");

        assertNotNull(user.getPortfolio());
        assertNotNull(bot.getPortfolio());
    }

    @Test
    @DisplayName("Should create users with order history")
    void testUsersHaveOrderHistory() {
        registry.createUser("USER001", "Test User");

        User user = (User) registry.getTrader("USER001");
        assertNotNull(user.getOrderHistory());
    }

    @Test
    @DisplayName("Should create multiple users")
    void testCreateMultipleUsers() {
        for (int i = 0; i < 10; i++) {
            boolean result = registry.createUser("USER" + i, "User " + i);
            assertTrue(result);
        }

        assertEquals(10, registry.getUsers().size());
    }

    @Test
    @DisplayName("Should create multiple bots")
    void testCreateMultipleBots() {
        for (int i = 0; i < 10; i++) {
            boolean result = registry.createBot("BOT" + i, "Bot " + i);
            assertTrue(result);
        }

        assertEquals(10, registry.getBots().size());
    }

    @Test
    @DisplayName("Should prevent user and bot ID collision")
    void testPreventUserBotCollision() {
        boolean user = registry.createUser("TRADER001", "Test User");
        boolean bot = registry.createBot("TRADER001", "Test Bot");

        assertTrue(user);
        assertFalse(bot);
    }

    @Test
    @DisplayName("Should set and get current user")
    void testCurrentUser() {
        registry.createUser("USER001", "Test User");
        registry.setCurrentUser("USER001");

        User currentUser = registry.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals("USER001", currentUser.getId());
    }

    @Test
    @DisplayName("Should handle null current user initially")
    void testInitialCurrentUser() {
        assertNull(registry.getCurrentUser());
    }

    @Test
    @DisplayName("Should update current user")
    void testUpdateCurrentUser() {
        registry.createUser("USER001", "User One");
        registry.createUser("USER002", "User Two");

        registry.setCurrentUser("USER001");
        assertEquals("USER001", registry.getCurrentUser().getId());

        registry.setCurrentUser("USER002");
        assertEquals("USER002", registry.getCurrentUser().getId());
    }

    @Test
    @DisplayName("Should maintain independent trader portfolios")
    void testIndependentPortfolios() {
        registry.createUser("USER001", "User One");
        registry.createUser("USER002", "User Two");

        Trader user1 = registry.getTrader("USER001");
        Trader user2 = registry.getTrader("USER002");

        assertNotSame(user1.getPortfolio(), user2.getPortfolio());
    }

    @Test
    @DisplayName("Should handle many traders")
    void testManyTraders() {
        for (int i = 0; i < 100; i++) {
            registry.createUser("USER" + i, "User " + i);
            registry.createBot("BOT" + i, "Bot " + i);
        }

        assertEquals(200, registry.getAllTraders().size());
        assertEquals(100, registry.getUsers().size());
        assertEquals(100, registry.getBots().size());
    }

    @Test
    @DisplayName("Should retrieve traders by exact ID match")
    void testExactIdMatch() {
        registry.createUser("USER001", "User One");
        registry.createUser("USER0011", "User Eleven");

        Trader trader1 = registry.getTrader("USER001");
        Trader trader2 = registry.getTrader("USER0011");

        assertNotEquals(trader1, trader2);
        assertEquals("USER001", trader1.getId());
        assertEquals("USER0011", trader2.getId());
    }

    @Test
    @DisplayName("Should handle different name formats")
    void testDifferentNameFormats() {
        String[] names = {"John Doe", "Alice", "Bob-Smith", "María García", "李明"};

        for (int i = 0; i < names.length; i++) {
            boolean result = registry.createUser("USER" + i, names[i]);
            assertTrue(result);
            assertEquals(names[i], registry.getTrader("USER" + i).getDisplayName());
        }
    }
}

