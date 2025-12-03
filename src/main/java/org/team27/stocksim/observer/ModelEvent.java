package org.team27.stocksim.observer;

public class ModelEvent {
    public enum Type {
        STOCKS_CHANGED,
        // lägg till nya typer vid behov
    }

    private final Type type;
    private final Object payload; // eller mer specifika fält

    public ModelEvent(Type type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public Type getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
