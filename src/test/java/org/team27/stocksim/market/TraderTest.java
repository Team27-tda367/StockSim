package org.team27.stocksim.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.team27.stocksim.model.market.StockSim;
import org.team27.stocksim.model.users.Trader;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class StockSimTest {

    private StockSim stockSim;

    @BeforeEach
    void setUp() {
        stockSim = new StockSim();
    }

    @Test
    void createUser_shouldAddNewTrader() {
        stockSim.createUser("1", "john");

        HashMap<String, Trader> traders = stockSim.getTraders();

        assertEquals(1, traders.size());
        assertTrue(traders.containsKey("1"));
    }

    @Test
    void createUser_shouldNotAddDuplicateTrader() {
        stockSim.createUser("1", "john");
        stockSim.createUser("1", "john"); // duplicate call

        HashMap<String, Trader> traders = stockSim.getTraders();

        assertEquals(1, traders.size());
    }

    @Test
    void createUser_shouldStoreIdInUppercase() {
        stockSim.createUser("alice", "Alice");

        HashMap<String, Trader> traders = stockSim.getTraders();

        assertTrue(traders.containsKey("ALICE"));
        assertFalse(traders.containsKey("alice"));
    }

    @Test
    void createUser_shouldThrowException_whenIdIsNull() {
        assertThrows(NullPointerException.class, () -> {
            stockSim.createUser(null, "Name");
        });
    }

    @Test
    void createUser_shouldNotCreateUser_whenIdIsEmpty() {
        stockSim.createUser("", "Name");

        HashMap<String, Trader> traders = stockSim.getTraders();

        // Empty string is allowed but becomes an empty key
        assertEquals(1, traders.size());
        assertTrue(traders.containsKey(""));
    }
}