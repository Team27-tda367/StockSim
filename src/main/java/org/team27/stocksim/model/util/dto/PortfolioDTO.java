package org.team27.stocksim.model.util.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PortfolioDTO {

    private BigDecimal balance;
    private List<InstrumentDTO> instruments;
    private Map<String, PositionDTO> positions;

    public PortfolioDTO() {}

    public PortfolioDTO(BigDecimal balance, List<InstrumentDTO> instruments, Map<String, PositionDTO> positions) {
        this.balance = balance;
        this.instruments = instruments;
        this.positions = positions;
    }

    // Getters and Setters

    public BigDecimal getBalance() {
        return balance;
    }

    public List<InstrumentDTO> getInstruments() {
        return instruments;
    }

    public Map<String, PositionDTO> getPositions() {
        return positions;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setInstruments(List<InstrumentDTO> instruments) {
        this.instruments = instruments;
    }

    public void setPositions(Map<String, PositionDTO> positions) {
        this.positions = positions;
    }
}
