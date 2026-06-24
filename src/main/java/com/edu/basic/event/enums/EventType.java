package com.edu.basic.event.enums;

public enum EventType {
    SPEED_DATING("Speed Dating"),
    GROUP_DATING("Group Dating"),
    DINNER_NIGHT("Dinner Night"),
    ACTIVITY_DATE("Activity Date"),
    VIRTUAL_DATE("Virtual Date"),
    ADVENTURE_DATE("Adventure Date");

    private final String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}