package com.edu.basic.event.dtos;

import com.edu.basic.event.enums.EventType;
import jakarta.validation.constraints.*;
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
public class EventRequestDTO {

    @NotBlank(message = "Event name is required")
    @Size(min = 3, max = 100, message = "Event name must be between 3 and 100 characters")
    private String eventName;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @NotNull(message = "Event type is required")
    private EventType eventType;

    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;

    @NotNull(message = "Event end date is required")
    @Future(message = "Event end date must be in the future")
    private LocalDateTime eventEndDate;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    @NotNull(message = "Ticket price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Ticket price must be greater than 0")
    private BigDecimal ticketPrice;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be at least 1")
    @Max(value = 10000, message = "Total seats cannot exceed 10000")
    private Integer totalSeats;

    private String imageUrl;

    @Size(max = 1000, message = "Special instructions cannot exceed 1000 characters")
    private String specialInstructions;
}

