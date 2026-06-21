package com.edu.basic.booking.dto.response;

import lombok.Data;

@Data
public class EventAvailabilityResponse {

    private Long eventId;
    private String eventName;
    private int maleSlotsBooked;
    private int femaleSlotsBooked;
    private int maleSlotsLeft;
    private int femaleSlotsLeft;
    private boolean maleFull;
    private boolean femaleFull;

}