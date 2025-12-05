package org.team27.stocksim.view.fx.viewControllers;

import org.team27.stocksim.model.instruments.PriceHistory;
import org.team27.stocksim.model.instruments.PricePoint;
import javafx.scene.chart.XYChart;

import java.util.List;

/**
 * Service class responsible for filtering and preparing chart data
 * based on different time periods. This class encapsulates the logic
 * for data aggregation, sampling, and filtering.
 */
public class ChartDataService {

    /**
     * Prepare chart data series for a specific time period.
     * 
     * @param priceHistory The complete price history of the stock
     * @param timePeriod   The time period to display
     * @return XYChart.Series with filtered data points
     */
    public XYChart.Series<Number, Number> prepareChartData(
            PriceHistory priceHistory,
            ChartTimePeriod timePeriod) {

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        if (priceHistory == null) {
            return series;
        }

        List<PricePoint> allPoints = priceHistory.getPoints();

        if (allPoints.isEmpty()) {
            return series;
        }

        // Calculate how many points to display
        int pointsToDisplay = timePeriod.calculatePointsToDisplay(allPoints.size());

        // Get the most recent points
        int startIndex = Math.max(0, allPoints.size() - pointsToDisplay);

        // Calculate sampling interval to avoid overcrowding
        int samplingInterval = calculateSamplingInterval(pointsToDisplay, timePeriod);

        // Add sampled data points to the series
        int xIndex = 0;
        for (int i = startIndex; i < allPoints.size(); i += samplingInterval) {
            PricePoint point = allPoints.get(i);
            series.getData().add(
                    new XYChart.Data<>(xIndex++, point.getPrice().doubleValue()));
        }

        return series;
    }

    /**
     * Calculate sampling interval based on time period to prevent overcrowding.
     * Larger time periods will have fewer points displayed.
     * 
     * @param totalPoints Total number of points in the range
     * @param timePeriod  The time period being displayed
     * @return The interval at which to sample points (1 = every point, 2 = every
     *         other point, etc.)
     */
    private int calculateSamplingInterval(int totalPoints, ChartTimePeriod timePeriod) {
        // Target maximum points to display on chart for good visibility
        int maxDisplayPoints = switch (timePeriod) {
            case ONE_DAY -> 100; // Show more detail for short periods
            case ONE_WEEK -> 80;
            case ONE_MONTH -> 60;
            case ONE_YEAR -> 50; // Show fewer points for long periods
        };

        // Calculate interval needed to reduce points to target
        int interval = Math.max(1, totalPoints / maxDisplayPoints);
        return interval;
    }

    /**
     * Update existing chart series with new data points.
     * This method efficiently adds only new points without recreating the entire
     * series.
     * 
     * @param series        The existing chart series to update
     * @param priceHistory  The complete price history
     * @param timePeriod    The current time period filter
     * @param lastKnownSize The number of points that were previously added
     * @return The new size of the price history (for tracking)
     */
    public int updateChartData(
            XYChart.Series<Number, Number> series,
            PriceHistory priceHistory,
            ChartTimePeriod timePeriod,
            int lastKnownSize) {

        if (series == null || priceHistory == null) {
            return lastKnownSize;
        }

        List<PricePoint> allPoints = priceHistory.getPoints();
        int currentSize = allPoints.size();

        // If history was reset or reduced, redraw everything
        if (currentSize < lastKnownSize) {
            series.getData().clear();
            XYChart.Series<Number, Number> newSeries = prepareChartData(priceHistory, timePeriod);
            series.getData().addAll(newSeries.getData());
            return currentSize;
        }

        // Add only new points
        if (currentSize > lastKnownSize) {
            int pointsToDisplay = timePeriod.calculatePointsToDisplay(currentSize);
            int startIndex = Math.max(0, currentSize - pointsToDisplay);

            // Check if we need to redraw (if period changes or we're showing a window)
            if (startIndex > lastKnownSize) {
                // We've moved past our window, redraw
                series.getData().clear();
                XYChart.Series<Number, Number> newSeries = prepareChartData(priceHistory, timePeriod);
                series.getData().addAll(newSeries.getData());
            } else {
                // Just add new points
                int seriesOffset = series.getData().size();
                for (int i = lastKnownSize; i < currentSize; i++) {
                    if (i >= startIndex) {
                        PricePoint point = allPoints.get(i);
                        series.getData().add(
                                new XYChart.Data<>(seriesOffset++, point.getPrice().doubleValue()));
                    }
                }
            }
        }

        return currentSize;
    }

    /**
     * Calculate appropriate time axis labels based on the time period.
     * This can be extended to provide meaningful time labels.
     * 
     * @param timePeriod The time period being displayed
     * @return A label for the time axis
     */
    public String getTimeAxisLabel(ChartTimePeriod timePeriod) {
        return switch (timePeriod) {
            case ONE_DAY -> "Hours";
            case ONE_WEEK -> "Days";
            case ONE_MONTH -> "Days";
            case ONE_YEAR -> "Months";
        };
    }
}
