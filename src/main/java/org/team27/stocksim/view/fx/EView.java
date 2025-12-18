package org.team27.stocksim.view.fx;

public enum EView {
    MAINVIEW("/org/team27/stocksim/view/main_view.fxml"),
    STOCKVIEW("/org/team27/stocksim/view/stock_view.fxml"),
    PORTFOLIOVIEW("/org/team27/stocksim/view/portfolio_view.fxml");

    private String fileName;

    EView(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}