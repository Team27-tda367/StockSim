package org.team27.stocksim.model.users;

import java.util.HashMap;
import java.util.Map;

public class TraderFactoryRegistry {
    private final Map<TraderType, TraderFactory> factories = new HashMap<>();

    public void register(TraderType type, TraderFactory factory) {
        factories.put(type, factory);
    }

    public Trader createTrader(TraderType type, String id, String name) {
        TraderFactory factory = factories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("No factory registered for type: " + type);
        }
        return factory.createTrader(id, name);
    }
}
