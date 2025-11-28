package org.team27.stocksim.ui.fx;

import org.team27.stocksim.model.market.StockSim;
import org.team27.stocksim.model.market.StockSimListener;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MainViewAdapter implements StockSimListener {

    private final StockSim model;
    private final StringProperty message = new SimpleStringProperty("");

    public MainViewAdapter(StockSim model) {
        this.model = model;

        // Initialize properties with model data
        this.message.set(model.messageCreatedStock());

        // Add self as listener to model
        model.addListener(this);
    }

    public void dispose() {
        model.removeListener(this);
    }

    // Exponera property till GUI
    public StringProperty messageProperty() {
        return message;
    }

    // Connect model updates to adapter properties
    @Override
    public void messageChanged(String newMessage) {
        // uppdatera JavaFX-property på FX-tråden
        Platform.runLater(() -> message.set(newMessage));
    }
}
