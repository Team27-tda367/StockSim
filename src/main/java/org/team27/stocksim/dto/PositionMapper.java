package org.team27.stocksim.dto;

import org.team27.stocksim.model.portfolio.Position;

public class PositionMapper {

    public static PositionDTO toDto(Position position) {
        if (position == null) {
            return null;
        }
        return new PositionDTO(
                position.getSymbol(),
                position.getQuantity(),
                position.getAverageCost());
    }

}
