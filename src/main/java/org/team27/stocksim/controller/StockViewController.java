package org.team27.stocksim.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class StockViewController {

    @FXML
    public void onExample(ActionEvent event) {
        ViewSwitcher.switchTo(View.EXAMPLE);
    }

}
