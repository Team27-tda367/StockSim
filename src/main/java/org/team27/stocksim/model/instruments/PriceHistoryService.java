package org.team27.stocksim.model.instruments;

import org.team27.stocksim.model.clock.ClockProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for processing and filtering price history data.
 * This class contains business logic for data sampling, filtering, and
 * windowing
 * without any dependencies on UI frameworks.
 * 
 * Uses timestamp-based filtering for accurate time-period representation.
 * Follows Single Responsibility Principle - only handles data processing logic.
 */
public class PriceHistoryService {

    /**
     * Filters and samples price history data based on a time period.
     * Uses actual timestamps to filter trades within the specified time window.
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

        // Get current time and calculate cutoff time for this period
        long simTime = ClockProvider.currentTimeMillis();
        long timeWindowMillis = timePeriod.getTimeWindowMillis();
        long cutoffTime = Math.min(simTime - timeWindowMillis, simTime);

        // Filter points within the time window
        List<PricePoint> filteredPoints = new ArrayList<>();
        for (PricePoint point : allPoints) {
            if (point.getTimestamp() >= cutoffTime) {
                filteredPoints.add(point);
            }
        }

        // If we have too many points, sample down to max display points
        if (filteredPoints.size() > timePeriod.getMaxDisplayPoints()) {
            return filteredPoints.subList(filteredPoints.size() - timePeriod.getMaxDisplayPoints(),
                    filteredPoints.size());
            // return samplePointsUniform(filteredPoints, timePeriod.getMaxDisplayPoints());
        }

        return filteredPoints;
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

        // Get what the filtered data SHOULD look like
        List<PricePoint> correctFilteredData = filterPriceData(priceHistory, timePeriod);

        // Calculate what we would have if we just added new points incrementally
        long simTime = ClockProvider.currentTimeMillis();
        long timeWindowMillis = timePeriod.getTimeWindowMillis();
        long cutoffTime = Math.min(simTime - timeWindowMillis, simTime);

        // Count how many old points would still be in the time window
        int oldPointsStillValid = 0;
        for (int i = 0; i < lastKnownSize; i++) {
            if (allPoints.get(i).getTimestamp() >= cutoffTime) {
                oldPointsStillValid++;
            }
        }

        // Get only the new points that are within the time window
        List<PricePoint> newPoints = new ArrayList<>();
        for (int i = lastKnownSize; i < currentSize; i++) {
            PricePoint point = allPoints.get(i);
            if (point.getTimestamp() >= cutoffTime) {
                newPoints.add(point);
            }
        }

        // Check if we need a full redraw:
        // 1. If old points have aged out (oldPointsStillValid != number we had
        // displayed)
        // 2. If we would exceed max display points with incremental add
        int potentialTotalPoints = oldPointsStillValid + newPoints.size();
        boolean needsFullRedraw = correctFilteredData.size() != potentialTotalPoints ||
                potentialTotalPoints > timePeriod.getMaxDisplayPoints();

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
     * Sample points from a list to reduce to a target count.
     * Uses uniform sampling to maintain temporal distribution.
     * 
     * @param points      The list of all points
     * @param targetCount The target number of points to return
     * @return List of sampled points
     */
    private List<PricePoint> samplePointsUniform(List<PricePoint> points, int targetCount) {
        if (points.size() <= targetCount) {
            return new ArrayList<>(points);
        }

        List<PricePoint> sampledPoints = new ArrayList<>();
        double interval = (double) points.size() / targetCount;

        for (int i = 0; i < targetCount; i++) {
            int index = (int) (i * interval);
            sampledPoints.add(points.get(index));
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
