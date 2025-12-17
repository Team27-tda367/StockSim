package org.team27.stocksim.model.util.dto;

import org.team27.stocksim.model.portfolio.Portfolio;

import java.util.Collections;
import java.util.stream.Collectors;

public class PortfolioMapper {

    // Convert Portfolio to PortfolioDTO

    public static PortfolioDTO toDto(Portfolio portfolio) {

        if (portfolio == null) {
            return null;
        }
        PortfolioDTO dto = new PortfolioDTO();
        dto.setBalance(portfolio.getBalance());
        // Null-safe instruments
        dto.setInstruments(
                portfolio.getInstruments() == null
                        ? Collections.emptyList()
                        : portfolio.getInstruments().stream()
                        .map(StockMapper::toDto)
                        .collect(Collectors.toList())
        );

        // Null-safe positions
        dto.setPositions(
                portfolio.getPositions() == null
                        ? Collections.emptyMap()
                        : portfolio.getPositions().entrySet().stream()
                        .collect(Collectors.toMap(
                                java.util.Map.Entry::getKey,
                                entry -> PositionMapper.toDto(entry.getValue())
                        ))
        );

        return dto;
    }

}
