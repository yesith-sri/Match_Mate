package com.edu.basic.event.dtos;

import com.edu.basic.event.enums.EventStatus;
import com.edu.basic.event.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponseDTO {

    private Long eventId;

    private String eventName;

    // FIXED: Changed "descripyes" back to "description"
    private String description;

    private EventType eventType;

    private EventStatus eventStatus;

    private LocalDateTime eventDate;

    private LocalDateTime eventEndDate;

    private String location;

    private Double latitude;

    private Double longitude;

    private BigDecimal ticketPrice;

    private Integer totalSeats;

    private Integer availableSeats;

    private String imageUrl;

    private String specialInstructions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdByUsername;

    private Long createdByUserId;

    // Calculated fields
    private Integer bookingPercentage;

    private Boolean isEventStarted;

    private Boolean isEventEnded;

    // --- ADDED: Merged availability fields ---
    private Integer confirmedMaleCount;

    private Integer confirmedFemaleCount;

    private Integer maleLimit;

    private Integer femaleLimit;
}