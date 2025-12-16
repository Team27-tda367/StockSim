package org.team27.stocksim.model.instruments;

/**
 * Represents different time periods for filtering price history data.
 * This is a domain model concept separate from UI concerns.
 * Uses actual timestamps for true time-based filtering.
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
     * Get the time window in milliseconds for this period.
     * 
     * @return Time window in milliseconds
     */
    public long getTimeWindowMillis() {
        return days * 24L * 60L * 60L * 1000L; // days to milliseconds
    }

    /**
     * Calculate the maximum number of display points for optimal chart rendering.
     * Shorter periods show more detail, longer periods show less.
     * 
     * @return Maximum points to display on chart
     */
    public int getMaxDisplayPoints() {
        return switch (this) {
            case ONE_DAY -> 200; // Show more detail for short periods
            case ONE_WEEK -> 160;
            case ONE_MONTH -> 120;
            case ONE_YEAR -> 100; // Show fewer points for long periods
        };
    }
}
