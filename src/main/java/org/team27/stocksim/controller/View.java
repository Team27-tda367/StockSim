package org.team27.stocksim.controller;

public enum View {
    MAINVIEW("/org/team27/stocksim/view/main_view.fxml"),  // Add full path
    EXAMPLE("/org/team27/stocksim/view/exampel.fxml");     // Add full path

    private String fileName;

    View(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileName() {
        return fileName;
    }
}