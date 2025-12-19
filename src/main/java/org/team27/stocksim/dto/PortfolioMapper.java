package org.team27.stocksim.dto;

import org.team27.stocksim.model.portfolio.Portfolio;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class PortfolioMapper {

    // Convert Portfolio to PortfolioDTO

    public static PortfolioDTO toDto(Portfolio portfolio) {

        if (portfolio == null) {
            return null;
        }

        // Null-safe positions
        Map<String, PositionDTO> positions = portfolio.getPositions() == null
                ? Collections.emptyMap()
                : portfolio.getPositions().entrySet().stream()
                        .collect(Collectors.toMap(
                                java.util.Map.Entry::getKey,
                                entry -> PositionMapper.toDto(entry.getValue())));

        return new PortfolioDTO(
                portfolio.getBalance(),
                positions);
    }

}
