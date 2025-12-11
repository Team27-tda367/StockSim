package org.team27.stocksim.model.instruments;

public enum ECategory {
    TECHNOLOGY("Technology"),
    FINANCE("Finance"),
    CONSUMER("Consumer"),
    ENTERTAINMENT("Entertainment"),
    AVIATION("Aviation"),
    SEMICONDUCTORS("Semiconductors");

    private final String label;

    ECategory(String label) { this.label = label; }

    public String getLabel() { return label; }
}
