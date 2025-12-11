package org.team27.stocksim.view.fx.chart;

/**
 * Enum representing different time periods for chart display.
 * Each period defines how to filter and display price history data.
 */
public enum ChartTimePeriod {
    ONE_DAY("1D", 1),
    ONE_WEEK("1W", 7),
    ONE_MONTH("1M", 30),
    ONE_YEAR("1Y", 365);

    private final String label;
    private final int days;

    ChartTimePeriod(String label, int days) {
        this.label = label;
        this.days = days;
    }

    public String getLabel() {
        return label;
    }

    public int getDays() {
        return days;
    }

    /**
     * Get ChartTimePeriod from button text label.
     * 
     * @param label The button text (e.g., "1D", "1W")
     * @return The corresponding ChartTimePeriod, or ONE_DAY if not found
     */
    public static ChartTimePeriod fromLabel(String label) {
        for (ChartTimePeriod period : values()) {
            if (period.label.equals(label)) {
                return period;
            }
        }
        return ONE_DAY; // Default
    }

    /**
     * Calculate how many data points to display based on available history.
     * This method can be extended to implement more sophisticated filtering.
     * 
     * @param totalPoints Total number of price points available
     * @return Number of points to display for this time period
     */
    public int calculatePointsToDisplay(int totalPoints) {
        // For now, return all points up to the period's limit
        // This can be extended to sample or aggregate data differently
        return Math.min(totalPoints, days * 24); // Assuming hourly data points
    }
}
