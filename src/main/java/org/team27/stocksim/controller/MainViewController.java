package org.team27.stocksim.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;



public class MainViewController{


    @FXML
    public void onExample(ActionEvent event){
        
        ViewSwitcher.switchTo(View.EXAMPLE);
    }

    
     //Funktion för att koppla sorteringen i stockTags
    @FXML
    private void sortByTag(ActionEvent event){
        
    }
}