package org.team27.stocksim.model.instruments;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for processing and filtering price history data.
 * This class contains business logic for data sampling, filtering, and
 * windowing
 * without any dependencies on UI frameworks.
 * 
 */
public class PriceHistoryService {

    /**
     * Filters and samples price history data based on a time period.
     * Returns a subset of price points optimized for display.
     * 
     * @param priceHistory The complete price history
     * @param timePeriod   The time period to filter by
     * @return Filtered and sampled list of price points
     */
    public List<PricePoint> filterPriceData(PriceHistory priceHistory, TimePeriod timePeriod) {
        if (priceHistory == null) {
            return new ArrayList<>();
        }

        List<PricePoint> allPoints = priceHistory.getPoints();

        if (allPoints.isEmpty()) {
            return new ArrayList<>();
        }

        // Calculate how many points to display
        int pointsToDisplay = timePeriod.calculatePointsToDisplay(allPoints.size());

        // Get the most recent points
        int startIndex = Math.max(0, allPoints.size() - pointsToDisplay);

        // Calculate sampling interval to avoid overcrowding
        int samplingInterval = calculateSamplingInterval(pointsToDisplay, timePeriod);

        // Sample the data points
        return samplePoints(allPoints, startIndex, samplingInterval);
    }

    /**
     * Calculates incremental updates to price data.
     * Returns only new points that should be added to an existing filtered dataset.
     * 
     * @param priceHistory  The complete price history
     * @param timePeriod    The current time period filter
     * @param lastKnownSize The number of points previously processed
     * @return FilterResult containing new points and whether a full redraw is
     *         needed
     */
    public FilterResult getIncrementalUpdate(
            PriceHistory priceHistory,
            TimePeriod timePeriod,
            int lastKnownSize) {

        if (priceHistory == null) {
            return new FilterResult(new ArrayList<>(), lastKnownSize, false);
        }

        List<PricePoint> allPoints = priceHistory.getPoints();
        int currentSize = allPoints.size();

        // If history was reset or reduced, signal full redraw
        if (currentSize < lastKnownSize) {
            return new FilterResult(
                    filterPriceData(priceHistory, timePeriod),
                    currentSize,
                    true // needs full redraw
            );
        }

        // No new points
        if (currentSize <= lastKnownSize) {
            return new FilterResult(new ArrayList<>(), currentSize, false);
        }

        // Check if window has shifted
        int pointsToDisplay = timePeriod.calculatePointsToDisplay(currentSize);
        int startIndex = Math.max(0, currentSize - pointsToDisplay);

        if (startIndex > lastKnownSize) {
            // Window has moved, need full redraw
            return new FilterResult(
                    filterPriceData(priceHistory, timePeriod),
                    currentSize,
                    true);
        }

        // Just get new points within the window
        List<PricePoint> newPoints = new ArrayList<>();
        for (int i = lastKnownSize; i < currentSize; i++) {
            if (i >= startIndex) {
                newPoints.add(allPoints.get(i));
            }
        }

        return new FilterResult(newPoints, currentSize, false);
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
    private int calculateSamplingInterval(int totalPoints, TimePeriod timePeriod) {
        int maxDisplayPoints = timePeriod.getMaxDisplayPoints();

        // Calculate interval needed to reduce points to target
        int interval = Math.max(1, totalPoints / maxDisplayPoints);
        return interval;
    }

    /**
     * Sample points from a list using a specific interval.
     * 
     * @param points           The list of all points
     * @param startIndex       The index to start sampling from
     * @param samplingInterval The interval between sampled points
     * @return List of sampled points
     */
    private List<PricePoint> samplePoints(List<PricePoint> points, int startIndex, int samplingInterval) {
        List<PricePoint> sampledPoints = new ArrayList<>();

        for (int i = startIndex; i < points.size(); i += samplingInterval) {
            sampledPoints.add(points.get(i));
        }

        return sampledPoints;
    }

    /**
     * Result class containing filtered price points and metadata about the
     * filtering operation.
     */
    public static class FilterResult {
        private final List<PricePoint> points;
        private final int newSize;
        private final boolean needsFullRedraw;

        public FilterResult(List<PricePoint> points, int newSize, boolean needsFullRedraw) {
            this.points = points;
            this.newSize = newSize;
            this.needsFullRedraw = needsFullRedraw;
        }

        public List<PricePoint> getPoints() {
            return points;
        }

        public int getNewSize() {
            return newSize;
        }

        public boolean needsFullRedraw() {
            return needsFullRedraw;
        }
    }
}
