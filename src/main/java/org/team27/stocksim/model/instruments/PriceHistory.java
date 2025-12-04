package org.team27.stocksim.model.instruments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PriceHistory {
    private List<PricePoint> points;

    public PriceHistory() {
        this.points = new ArrayList<>();
    }

    public void addPrice(BigDecimal price) {
        PricePoint point = new PricePoint(System.currentTimeMillis(), price);
        points.add(point);
    }

    public List<PricePoint> getPoints() {
        return new ArrayList<>(points); // Return a copy to maintain encapsulation
    }

}
