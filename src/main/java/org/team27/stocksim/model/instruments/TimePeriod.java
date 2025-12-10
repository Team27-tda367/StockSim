package org.team27.stocksim.model.instruments;

/**
 * Represents different time periods for filtering price history data.
 * This is a domain model concept separate from UI concerns.
 */
public enum TimePeriod {
    ONE_DAY(1),
    ONE_WEEK(7),
    ONE_MONTH(30),
    ONE_YEAR(365);

    private final int days;

    TimePeriod(int days) {
        this.days = days;
    }

    /**
     * Get the number of days this time period represents.
     * 
     * @return Number of days
     */
    public int getDays() {
        return days;
    }

    /**
     * Calculate how many data points to display based on available history.
     * Assumes roughly hourly data points.
     * 
     * @param totalPoints Total number of price points available
     * @return Number of points to display for this time period
     */
    public int calculatePointsToDisplay(int totalPoints) {
        // Limit to hourly data points for the time period
        return Math.min(totalPoints, days * 24);
    }

    /**
     * Calculate the maximum number of display points for optimal chart rendering.
     * Shorter periods show more detail, longer periods show less.
     * 
     * @return Maximum points to display on chart
     */
    public int getMaxDisplayPoints() {
        return switch (this) {
            case ONE_DAY -> 100; // Show more detail for short periods
            case ONE_WEEK -> 80;
            case ONE_MONTH -> 60;
            case ONE_YEAR -> 50; // Show fewer points for long periods
        };
    }
}
