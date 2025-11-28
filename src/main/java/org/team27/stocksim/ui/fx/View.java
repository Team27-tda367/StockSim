package org.team27.stocksim.ui.fx;

public enum View {
    MAINVIEW("/org/team27/stocksim/view/main_view.fxml"), // Add full path
    CREATESTOCK("/org/team27/stocksim/view/create_stock_page.fxml"); // Add full path

    private String fileName;

    View(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}