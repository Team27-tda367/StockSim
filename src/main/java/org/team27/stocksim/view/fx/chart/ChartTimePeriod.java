package org.team27.stocksim.view.fx.chart;

import org.team27.stocksim.model.instruments.TimePeriod;

/**
 * View-layer enum that maps UI labels to domain model TimePeriod.
 * Responsible only for UI presentation concerns (button labels).
 * Business logic has been moved to the model's TimePeriod enum.
 */
public enum ChartTimePeriod {
    ONE_DAY("1D", TimePeriod.ONE_DAY),
    ONE_WEEK("1W", TimePeriod.ONE_WEEK),
    ONE_MONTH("1M", TimePeriod.ONE_MONTH),
    ONE_YEAR("1Y", TimePeriod.ONE_YEAR);

    private final String label;
    private final TimePeriod modelTimePeriod;

    ChartTimePeriod(String label, TimePeriod modelTimePeriod) {
        this.label = label;
        this.modelTimePeriod = modelTimePeriod;
    }

    /**
     * Get the UI label for this time period.
     * 
     * @return Button label (e.g., "1D", "1W")
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the corresponding domain model TimePeriod.
     * 
     * @return Domain model representation
     */
    public TimePeriod toModelTimePeriod() {
        return modelTimePeriod;
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
     * Get appropriate time axis label for UI display.
     * 
     * @return Label for the chart's time axis
     */
    public String getTimeAxisLabel() {
        return switch (this) {
            case ONE_DAY -> "Hours";
            case ONE_WEEK -> "Days";
            case ONE_MONTH -> "Days";
            case ONE_YEAR -> "Months";
        };
    }
}
