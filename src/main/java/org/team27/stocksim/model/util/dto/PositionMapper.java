package org.team27.stocksim.model.util.dto;

import org.team27.stocksim.model.portfolio.Position;

public class PositionMapper {

    public static PositionDTO toDto(Position position) {
        if (position == null) {
            return null;
        }
        PositionDTO dto = new PositionDTO();
        dto.setSymbol(position.getSymbol());
        dto.setQuantity(position.getQuantity());
        dto.setAverageCost(position.getAverageCost());
        return dto;
    }

}
