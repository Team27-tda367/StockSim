package org.team27.stocksim.model.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.team27.stocksim.model.portfolio.Portfolio;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.team27.stocksim.model.util.MoneyUtils.money;

@DisplayName("User Tests")
class UserTest {

    private UserFactory userFactory;
    private Portfolio testPortfolio;
    private String testId;
    private String testName;

    @BeforeEach
    void setUp() {
        userFactory = new UserFactory();
        testPortfolio = new Portfolio(money("10000.00"));
        testId = "USER001";
        testName = "Test User";
    }

    @Test
    @DisplayName("Should create user with correct properties")
    void testCreateUser() {
        User user = (User) userFactory.createTrader(testId, testName, testPortfolio);

        assertNotNull(user);
        assertEquals(testId, user.getId());
        assertEquals(testName, user.getDisplayName());
        assertNotNull(user.getPortfolio());
        assertNotNull(user.getOrderHistory());
    }

    @Test
    @DisplayName("Should have empty order history initially")
    void testInitialOrderHistory() {
        User user = (User) userFactory.createTrader(testId, testName, testPortfolio);

        OrderHistory history = user.getOrderHistory();
        assertNotNull(history);
        assertTrue(history.getAllOrders().isEmpty());
        assertTrue(history.getAllTrades().isEmpty());
    }

    @Test
    @DisplayName("Should have portfolio with initial balance")
    void testInitialPortfolio() {
        User user = (User) userFactory.createTrader(testId, testName, testPortfolio);

        Portfolio portfolio = user.getPortfolio();
        assertNotNull(portfolio);
        assertEquals(money("10000.00"), portfolio.getBalance());
    }

    @Test
    @DisplayName("Should maintain user ID immutability")
    void testUserIdImmutability() {
        User user = (User) userFactory.createTrader(testId, testName, testPortfolio);

        String id1 = user.getId();
        String id2 = user.getId();

        assertEquals(id1, id2);
        assertSame(id1, id2);
    }

    @Test
    @DisplayName("Should maintain display name immutability")
    void testDisplayNameImmutability() {
        User user = (User) userFactory.createTrader(testId, testName, testPortfolio);

        String name1 = user.getDisplayName();
        String name2 = user.getDisplayName();

        assertEquals(name1, name2);
        assertSame(name1, name2);
    }

    @Test
    @DisplayName("Should maintain same portfolio reference")
    void testPortfolioReference() {
        User user = (User) userFactory.createTrader(testId, testName, testPortfolio);

        Portfolio portfolio1 = user.getPortfolio();
        Portfolio portfolio2 = user.getPortfolio();

        assertSame(portfolio1, portfolio2);
    }

    @Test
    @DisplayName("Should maintain same order history reference")
    void testOrderHistoryReference() {
        User user = (User) userFactory.createTrader(testId, testName, testPortfolio);

        OrderHistory history1 = user.getOrderHistory();
        OrderHistory history2 = user.getOrderHistory();

        assertSame(history1, history2);
    }

    @Test
    @DisplayName("Should create multiple distinct users")
    void testMultipleUsers() {
        User user1 = (User) userFactory.createTrader("USER001", "User One", testPortfolio);
        Portfolio portfolio2 = new Portfolio(money("20000.00"));
        User user2 = (User) userFactory.createTrader("USER002", "User Two", portfolio2);

        assertNotSame(user1, user2);
        assertNotEquals(user1.getId(), user2.getId());
        assertNotEquals(user1.getDisplayName(), user2.getDisplayName());
    }

    @Test
    @DisplayName("Should have independent order histories")
    void testIndependentOrderHistories() {
        User user1 = (User) userFactory.createTrader("USER001", "User One", testPortfolio);
        Portfolio portfolio2 = new Portfolio(money("20000.00"));
        User user2 = (User) userFactory.createTrader("USER002", "User Two", portfolio2);

        assertNotSame(user1.getOrderHistory(), user2.getOrderHistory());
    }

    @Test
    @DisplayName("Should have independent portfolios")
    void testIndependentPortfolios() {
        Portfolio portfolio1 = new Portfolio(money("10000.00"));
        Portfolio portfolio2 = new Portfolio(money("20000.00"));

        User user1 = (User) userFactory.createTrader("USER001", "User One", portfolio1);
        User user2 = (User) userFactory.createTrader("USER002", "User Two", portfolio2);

        assertNotSame(user1.getPortfolio(), user2.getPortfolio());
        assertNotEquals(user1.getPortfolio().getBalance(), user2.getPortfolio().getBalance());
    }

    @Test
    @DisplayName("Should handle different ID formats")
    void testDifferentIdFormats() {
        String[] ids = {"USER001", "user_123", "U-456", "12345"};

        for (String id : ids) {
            Portfolio portfolio = new Portfolio(money("10000.00"));
            User user = (User) userFactory.createTrader(id, "Test User", portfolio);
            assertEquals(id, user.getId());
        }
    }

    @Test
    @DisplayName("Should handle different name formats")
    void testDifferentNameFormats() {
        String[] names = {"John Doe", "Alice", "Bob Smith Jr.", "María García"};

        for (String name : names) {
            Portfolio portfolio = new Portfolio(money("10000.00"));
            User user = (User) userFactory.createTrader("USER" + name.hashCode(), name, portfolio);
            assertEquals(name, user.getDisplayName());
        }
    }

    @Test
    @DisplayName("Should handle zero balance portfolio")
    void testZeroBalancePortfolio() {
        Portfolio zeroPortfolio = new Portfolio(BigDecimal.ZERO);
        User user = (User) userFactory.createTrader(testId, testName, zeroPortfolio);

        assertEquals(BigDecimal.ZERO, user.getPortfolio().getBalance());
    }

    @Test
    @DisplayName("Should handle large balance portfolio")
    void testLargeBalancePortfolio() {
        Portfolio richPortfolio = new Portfolio(money("1000000000.00"));
        User user = (User) userFactory.createTrader(testId, testName, richPortfolio);

        assertEquals(money("1000000000.00"), user.getPortfolio().getBalance());
    }
}

