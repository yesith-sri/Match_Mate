package com.edu.basic.event.enums;

public enum TicketStatus {
    ACTIVE("Active"),
    USED("Used"),
    CANCELLED("Cancelled"),
    EXPIRED("Expired");

    private final String displayName;

    TicketStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}