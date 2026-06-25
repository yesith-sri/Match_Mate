package com.edu.basic.event.mapeer;

import com.edu.basic.event.dtos.EventResponseDTO;
import com.edu.basic.event.entity.Event;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventMapper {

    public EventResponseDTO mapToResponse(Event event) {
        LocalDateTime now = LocalDateTime.now();
        int bookingPercentage = event.getTotalSeats() == 0 ? 0 :
                ((event.getTotalSeats() - event.getAvailableSeats()) * 100) / event.getTotalSeats();

        return EventResponseDTO.builder()
                .eventId(event.getEventId())
                .eventName(event.getEventName())
                .description(event.getDescription())
                .eventType(event.getEventType())
                .eventStatus(event.getEventStatus())
                .eventDate(event.getEventDate())
                .eventEndDate(event.getEventEndDate())
                .location(event.getLocation())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .ticketPrice(event.getTicketPrice())
                .totalSeats(event.getTotalSeats())
                .availableSeats(event.getAvailableSeats())
                .imageUrl(event.getImageUrl())
                .specialInstructions(event.getSpecialInstructions())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .createdByUsername(event.getCreatedBy().getEmail())
                .createdByUserId(event.getCreatedBy().getId())
                .bookingPercentage(bookingPercentage)
                .isEventStarted(now.isAfter(event.getEventDate()))
                .isEventEnded(now.isAfter(event.getEventEndDate()))
                .build();
    }
}