package com.edu.basic.booking.dto.response;

public class EventAvailabilityResponse {

    private Long eventId;
    private String eventName;
    private int maleSlotsBooked;
    private int femaleSlotsBooked;
    private int maleSlotsLeft;
    private int femaleSlotsLeft;
    private boolean maleFull;
    private boolean femaleFull;

    public EventAvailabilityResponse() {
    }

    public EventAvailabilityResponse(Long eventId, String eventName, int maleSlotsBooked,
                                     int femaleSlotsBooked, int maleLimit, int femaleLimit) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.maleSlotsBooked = maleSlotsBooked;
        this.femaleSlotsBooked = femaleSlotsBooked;
        this.maleSlotsLeft = Math.max(0, maleLimit - maleSlotsBooked);
        this.femaleSlotsLeft = Math.max(0, femaleLimit - femaleSlotsBooked);
        this.maleFull = maleSlotsBooked >= maleLimit;
        this.femaleFull = femaleSlotsBooked >= femaleLimit;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getMaleSlotsBooked() {
        return maleSlotsBooked;
    }

    public void setMaleSlotsBooked(int maleSlotsBooked) {
        this.maleSlotsBooked = maleSlotsBooked;
    }

    public int getFemaleSlotsBooked() {
        return femaleSlotsBooked;
    }

    public void setFemaleSlotsBooked(int femaleSlotsBooked) {
        this.femaleSlotsBooked = femaleSlotsBooked;
    }

    public int getMaleSlotsLeft() {
        return maleSlotsLeft;
    }

    public void setMaleSlotsLeft(int maleSlotsLeft) {
        this.maleSlotsLeft = maleSlotsLeft;
    }

    public int getFemaleSlotsLeft() {
        return femaleSlotsLeft;
    }

    public void setFemaleSlotsLeft(int femaleSlotsLeft) {
        this.femaleSlotsLeft = femaleSlotsLeft;
    }

    public boolean isMaleFull() {
        return maleFull;
    }

    public void setMaleFull(boolean maleFull) {
        this.maleFull = maleFull;
    }

    public boolean isFemaleFull() {
        return femaleFull;
    }

    public void setFemaleFull(boolean femaleFull) {
        this.femaleFull = femaleFull;
    }
}