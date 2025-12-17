package org.team27.stocksim.view.fx.chart;

import org.team27.stocksim.model.instruments.PriceHistory;
import org.team27.stocksim.model.instruments.PriceHistoryService;
import org.team27.stocksim.model.instruments.PricePoint;
import javafx.scene.chart.XYChart;

import java.util.List;

/**
 * View-layer service responsible for converting domain model price data
 * into JavaFX chart series. This class handles only UI-specific concerns.
 * Business logic for data filtering and sampling is delegated to
 * PriceHistoryService.
 * 
 * Follows Single Responsibility Principle - only handles JavaFX conversion.
 */
public class ChartDataService {

    private final PriceHistoryService priceHistoryService;

    public ChartDataService() {
        this.priceHistoryService = new PriceHistoryService();
    }

    /**
     * Prepare chart data series.
     * Delegates data processing to model layer, then converts to JavaFX format.
     * 
     * @param priceHistory The complete price history of the stock
     * @return XYChart.Series with filtered data points
     */
    public XYChart.Series<Number, Number> prepareChartData(PriceHistory priceHistory) {

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        if (priceHistory == null) {
            return series;
        }

        // Delegate filtering to model service
        List<PricePoint> filteredPoints = priceHistoryService.filterPriceData(priceHistory);

        // Convert to JavaFX chart format
        convertToChartSeries(filteredPoints, series);

        return series;
    }

    /**
     * Update existing chart series with new data points.
     * This method efficiently adds only new points without recreating the entire
     * series.
     * 
     * @param series        The existing chart series to update
     * @param priceHistory  The complete price history
     * @param lastKnownSize The number of points that were previously added
     * @return The new size of the price history (for tracking)
     */
    public int updateChartData(
            XYChart.Series<Number, Number> series,
            PriceHistory priceHistory,
            int lastKnownSize) {

        if (series == null || priceHistory == null) {
            return lastKnownSize;
        }

        // Delegate update logic to model service
        PriceHistoryService.FilterResult result = priceHistoryService.getIncrementalUpdate(
                priceHistory,
                lastKnownSize);

        if (result.needsFullRedraw()) {
            // Clear and redraw entire chart
            series.getData().clear();
            convertToChartSeries(result.getPoints(), series);
        } else if (!result.getPoints().isEmpty()) {
            // Just add new points
            int seriesOffset = series.getData().size();
            for (PricePoint point : result.getPoints()) {
                series.getData().add(
                        new XYChart.Data<>(seriesOffset++, point.getPrice().doubleValue()));
            }
        }

        return result.getNewSize();
    }

    /**
     * Convert a list of PricePoints to JavaFX chart data format.
     * This is pure view-layer conversion logic.
     * 
     * @param points The price points to convert
     * @param series The series to populate
     */
    private void convertToChartSeries(List<PricePoint> points, XYChart.Series<Number, Number> series) {
        int xIndex = 0;
        for (PricePoint point : points) {
            series.getData().add(
                    new XYChart.Data<>(xIndex++, point.getPrice().doubleValue()));
        }
    }
}
