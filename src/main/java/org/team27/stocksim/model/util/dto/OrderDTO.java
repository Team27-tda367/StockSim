package org.team27.stocksim.model.util.dto;

import java.math.BigDecimal;

public class OrderDTO {

    private String side;
    private String instrumentSymbol;
    private BigDecimal price;
    private int quantity;
    private String status;

    public OrderDTO() {}

    public OrderDTO(String side, String instrumentSymbol, BigDecimal price, int quantity, String status) {
        this.side = side;
        this.instrumentSymbol = instrumentSymbol;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
    }

    // Getters and Setters

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getInstrumentSymbol() {
        return instrumentSymbol;
    }

    public void setInstrumentSymbol(String instrumentSymbol) {
        this.instrumentSymbol = instrumentSymbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
