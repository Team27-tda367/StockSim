package org.team27.stocksim.model.instruments;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for optimizing price history data for chart rendering.
 *
 * <p>PriceHistoryService manages the efficient transfer of price data to chart
 * components by filtering and providing incremental updates. It prevents performance
 * issues by limiting displayed points and detecting when full redraws versus
 * incremental updates are needed.</p>
 *
 * <p><strong>Design Pattern:</strong> Service + Optimization Strategy</p>
 * <ul>
 *   <li>Limits displayed points to prevent chart performance degradation</li>
 *   <li>Provides incremental updates when possible (add only new points)</li>
 *   <li>Detects when full redraw is necessary</li>
 *   <li>Handles price history resets gracefully</li>
 *   <li>Optimizes memory and rendering performance</li>
 * </ul>
 *
 * <h2>Optimization Strategy:</h2>
 * <ul>
 *   <li><strong>Point Limiting:</strong> Max 200 points displayed (most recent)</li>
 *   <li><strong>Incremental Updates:</strong> Add only new points when under limit</li>
 *   <li><strong>Full Redraw:</strong> When exceeding limit or history reset detected</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * PriceHistoryService service = new PriceHistoryService();
 * Stock stock = instrumentRegistry.get("AAPL");
 * int lastKnownSize = 0;
 *
 * // Initial load
 * List<PricePoint> initialData = service.filterPriceData(stock.getPriceHistory());
 * chart.setData(initialData);
 * lastKnownSize = initialData.size();
 *
 * // Periodic updates
 * FilterResult result = service.getIncrementalUpdate(
 *     stock.getPriceHistory(),
 *     lastKnownSize
 * );
 *
 * if (result.needsFullRedraw()) {
 *     chart.setData(result.getPoints()); // Full redraw
 * } else if (!result.getPoints().isEmpty()) {
 *     chart.addPoints(result.getPoints()); // Incremental add
 * }
 * lastKnownSize = result.getNewSize();
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see PriceHistory
 * @see PricePoint
 */
public class PriceHistoryService {

    /**
     * Maximum number of price points to display in charts.
     * Limits memory usage and improves rendering performance.
     */
    private static final int MAX_DISPLAY_POINTS = 200;

    /**
     * Filters price history to most recent points within display limit.
     *
     * <p>If history has more than MAX_DISPLAY_POINTS, returns only the most
     * recent points. This prevents performance issues with large histories.</p>
     *
     * @param priceHistory The price history to filter
     * @return List of price points limited to MAX_DISPLAY_POINTS
     */
    public List<PricePoint> filterPriceData(PriceHistory priceHistory) {
        if (priceHistory == null) {
            return new ArrayList<>();
        }

        List<PricePoint> allPoints = priceHistory.getPoints();

        if (allPoints.isEmpty()) {
            return new ArrayList<>();
        }

        // Take only the most recent points if we have too many
        if (allPoints.size() > MAX_DISPLAY_POINTS) {
            return allPoints.subList(allPoints.size() - MAX_DISPLAY_POINTS, allPoints.size());
        }

        return new ArrayList<>(allPoints);
    }

    /**
     * Gets incremental price history updates optimized for chart rendering.
     *
     * <p>This method intelligently determines whether to provide incremental
     * updates (new points only) or signal a full redraw. It handles three cases:</p>
     * <ol>
     *   <li><strong>History reset/reduced:</strong> Signals full redraw</li>
     *   <li><strong>Exceeding limit:</strong> Returns filtered data, signals full redraw</li>
     *   <li><strong>Normal growth:</strong> Returns only new points, no redraw</li>
     * </ol>
     *
     * @param priceHistory The current price history
     * @param lastKnownSize Size of history from last update
     * @return FilterResult containing points and update strategy
     */
    public FilterResult getIncrementalUpdate(
            PriceHistory priceHistory,
            int lastKnownSize) {

        if (priceHistory == null) {
            return new FilterResult(new ArrayList<>(), lastKnownSize, false);
        }

        List<PricePoint> allPoints = priceHistory.getPoints();
        int currentSize = allPoints.size();

        // If history was reset or reduced, signal full redraw
        if (currentSize < lastKnownSize) {
            return new FilterResult(
                    filterPriceData(priceHistory),
                    currentSize,
                    true // needs full redraw
            );
        }

        // No new points
        if (currentSize <= lastKnownSize) {
            return new FilterResult(new ArrayList<>(), currentSize, false);
        }

        // Get what the filtered data SHOULD look like
        List<PricePoint> correctFilteredData = filterPriceData(priceHistory);

        // Get only the new points
        List<PricePoint> newPoints = new ArrayList<>();
        for (int i = lastKnownSize; i < currentSize; i++) {
            newPoints.add(allPoints.get(i));
        }

        // Check if we need a full redraw (if we would exceed max display points)
        boolean needsFullRedraw = currentSize > MAX_DISPLAY_POINTS;

        if (needsFullRedraw) {
            return new FilterResult(
                    correctFilteredData,
                    currentSize,
                    true // needs full redraw
            );
        }

        // Return incremental update without full redraw
        return new FilterResult(
                newPoints,
                currentSize,
                false // no full redraw needed for incremental updates
        );
    }

    /**
     * Result of incremental update calculation.
     *
     * <p>Contains the price points to add/display, the new history size for
     * tracking, and whether a full chart redraw is necessary.</p>
     *
     * @author Team 27
     * @version 1.0
     */
    public static class FilterResult {
        /**
         * Price points to add or display.
         */
        private final List<PricePoint> points;

        /**
         * Current size of price history for future comparisons.
         */
        private final int newSize;

        /**
         * Whether chart needs full redraw (vs incremental update).
         */
        private final boolean needsFullRedraw;

        /**
         * Constructs a FilterResult.
         *
         * @param points Price points to add/display
         * @param newSize Current history size
         * @param needsFullRedraw Whether full redraw is needed
         */
        public FilterResult(List<PricePoint> points, int newSize, boolean needsFullRedraw) {
            this.points = points;
            this.newSize = newSize;
            this.needsFullRedraw = needsFullRedraw;
        }

        /**
         * Gets the price points for this update.
         *
         * @return List of price points
         */
        public List<PricePoint> getPoints() {
            return points;
        }

        /**
         * Gets the new history size for tracking.
         *
         * @return Current size of price history
         */
        public int getNewSize() {
            return newSize;
        }

        /**
         * Checks if full chart redraw is needed.
         *
         * @return true if full redraw needed, false for incremental update
         */
        public boolean needsFullRedraw() {
            return needsFullRedraw;
        }
    }
}
