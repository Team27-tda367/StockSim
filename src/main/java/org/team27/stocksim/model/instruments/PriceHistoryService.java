package org.team27.stocksim.model.instruments;

import java.util.ArrayList;
import java.util.List;


public class PriceHistoryService {

    private static final int MAX_DISPLAY_POINTS = 200;


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
