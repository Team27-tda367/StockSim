package org.team27.stocksim.controller;

import javafx.beans.property.StringProperty;

public interface SimController {

    // Define any methods that SimController should have
    void handleSampleAction();

    StringProperty messageProperty();

}
